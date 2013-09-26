/*
 * JavaAnalyzer.java
 * Created on 2013/06/28
 *
 * Copyright (C) 2011-2013 Nippon Telegraph and Telephone Corporation
 *
 * Licensed under the Apache License, Version 2.0 (the "License");
 * you may not use this file except in compliance with the License.
 * You may obtain a copy of the License at
 *
 *     http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package org.tubame.wsearch.logics.analyzer;

import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Locale;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.NullProgressMonitor;
import org.eclipse.jdt.core.ICompilationUnit;
import org.eclipse.jdt.core.IField;
import org.eclipse.jdt.core.IImportDeclaration;
import org.eclipse.jdt.core.IJavaElement;
import org.eclipse.jdt.core.IMethod;
import org.eclipse.jdt.core.IPackageDeclaration;
import org.eclipse.jdt.core.IType;
import org.eclipse.jdt.core.JavaCore;
import org.eclipse.jdt.core.JavaModelException;
import org.eclipse.jdt.core.dom.AST;
import org.eclipse.jdt.core.dom.ASTNode;
import org.eclipse.jdt.core.dom.ASTParser;
import org.eclipse.jdt.core.dom.ASTVisitor;
import org.eclipse.jdt.core.dom.CompilationUnit;
import org.eclipse.jdt.core.dom.ImportDeclaration;
import org.eclipse.jdt.core.dom.MethodDeclaration;
import org.eclipse.jdt.core.dom.MethodInvocation;
import org.eclipse.jdt.core.dom.PackageDeclaration;
import org.eclipse.jdt.core.dom.QualifiedName;
import org.eclipse.jdt.core.dom.SimpleName;
import org.eclipse.jdt.core.dom.SuperMethodInvocation;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.wsearch.Activator;
import org.tubame.wsearch.biz.WSPhaseService;
import org.tubame.wsearch.biz.analyzer.Analyzer;
import org.tubame.wsearch.biz.analyzer.ParserResult;
import org.tubame.wsearch.biz.cache.WSearchCacheManager;
import org.tubame.wsearch.biz.ex.WSearchBizException;

/**
 * Classes for extracting class information that are used in code parses Java
 * source code.<br/>
 * The analysis assumes that you have imported to the eclipse project.<br/>
 * It does not extract the class information that are defined in eclipse
 * project.<br/>
 * Need a library of following the operation.<br/>
 * <ul>
 * <li>org.eclipse.core.resources</li>
 * <li>org.eclipse.jdt.core</li>
 * <li>org.eclipse.core.runtime</li>
 * </ul>
 * The description below is an example of usage.<br />
 * 
 * <pre>
 * JavaAnalyzer.clearCache();
 * ResourcesPlugin.getWorkspace().getRoot().getProject(&quot;test&quot;)
 *         .accept(new IResourceVisitor() {
 *             public boolean visit(IResource resource) throws CoreException {
 *                 List&lt;Pattern&gt; ignores = new ArrayList&lt;Pattern&gt;();
 *                 ignores.add(Pattern.compile(&quot;&circ;java.*&quot;));
 *                 switch (resource.getType()) {
 *                 case IResource.FILE:
 *                     Analyzer analyzer = null;
 *                     if (resource.getFileExtension() == null) {
 *                         break;
 *                     } else if (resource.getFileExtension().equals(&quot;java&quot;)) {
 *                         analyzer = new JavaAnalyzer((IFile) resource, ignores);
 *                         analyzer.analyze();
 *                         Map&lt;String, List&lt;ParseResult&gt;&gt; map = analyzer
 *                                 .getResultMap();
 *                         for (Map.Entry&lt;String, List&lt;ParseResult&gt;&gt; entry : map
 *                                 .entrySet()) {
 *                             for (ParseResult rs : entry.getValue()) {
 *                                 System.out.println(rs);
 *                             }
 *                         }
 *                     }
 *                     break;
 *                 }
 *                 return true;
 *             }
 *         });
 * </pre>
 * 
 * This implementation is not synchronized.<br />
 * If the access after thread of them change the member variable multiple
 * threads in parallel,<br/>
 * it must be synchronized externally.<br/>
 */
public class JavaAnalyzer extends ASTVisitor implements Analyzer {

    /**
     * Handle constant of AST API (can be set appropriately in accordance with
     * the eclipse version)<br />
     */
    public static final int JLSN = AST.JLS3;

    /**
     * The IFile object for the Java source code<br />
     */
    private IFile file;

    /**
     * Whether analyze executed<br />
     */
    private boolean analyzed;

    /**
     * CompilationUnit object to the Java source code (ASTNode object of the
     * root)<br />
     */
    private CompilationUnit rootNode;

    /**
     * ICompilationUnit object to the Java source code (Java object model
     * representation) <br />
     */
    private ICompilationUnit sourceCode;

    /**
     * Package name of the Java source code (Including the dot at the end) <br />
     */
    private String packageName;

    /**
     * Class name of the Java source code<br />
     */
    private String className;

    /**
     * Class list the Java source code is import(static)<br />
     */
    private List<String> staticImportList;

    /**
     * Map that is registered information of the classes that are used in Java
     * source code<br />
     * Format,Map<(Or class name) class name absolute,<Where that class is used>
     * List> <br />
     */
    private Map<String, List<ParserResult>> resultMap;

    /**
     * Regular expression string list in which the extracted outside<br />
     */
    private List<Pattern> ignoreList;

    /**
     * Logger<br />
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(JavaAnalyzer.class);

    /**
     * Initialize JavaAnalyzer.<br/>
     * 
     * @param file
     *            object that represents the Java files of eclipse project on
     */
    public JavaAnalyzer(final IFile file) {
        this(file, new ArrayList<Pattern>());
    }

    /**
     * Initialize JavaAnalyzer.<br/>
     * By specifying a list of regular expression string keywords that extracted
     * outside, <br/>
     * you can refine the extraction target.<br />
     * If you are a (described in the following example:) inappropriate,<br/>
     * the specified file throw an exception.<br/>
     * <ul>
     * <li>If the hyphen is in the path of the source directory later</li>
     * <li>If it is not a file in the source directory under</li>
     * </ul>
     * </p>
     * 
     * @param file
     *            Object that represents the Java files of eclipse project on
     * @param ignoreList
     *            Regular expression string list in which the extracted outside
     * @throws IllegalArgumentException
     *             Regular expression string list in which the extracted outside
     */
    public JavaAnalyzer(final IFile file, final List<Pattern> ignoreList) {
        super();
        this.file = file;
        this.analyzed = false;
        this.resultMap = new HashMap<String, List<ParserResult>>();
        LOGGER.trace(Activator.getResourceString("debug.msg.read.file")
                + file.getLocation().toOSString());
        this.sourceCode = JavaCore.createCompilationUnitFrom(file);
        this.ignoreList = ignoreList;
        final IType type = this.sourceCode.findPrimaryType();
        if (type == null) {
            throw new IllegalArgumentException(MessageFormat.format(
                    Activator.getResourceString(JavaAnalyzer.class.getName()
                            + ".err.msg.IncorrectFileErr"), file));
        }
        this.className = type.getElementName();
        this.staticImportList = new ArrayList<String>();
        // Initialize with the analyze method within the later
        this.rootNode = null;
        this.packageName = null;
    }

    /**
     * Registering the Map is a member variable class information that is
     * extracted.<br/>
     * This method does not cause the user to call.<br/>
     * 
     * @param key
     *            Class name (if the full class name is not available to the
     *            class name) absolute
     * @param node
     *            ASTNode object of where that class is used
     */
    private void putResultMap(final String key, final ASTNode node) {

        List<String> classNameList = WSearchCacheManager.getInstance()
                .getClassNameList();
        // Extraction and are not covered under a class that exists in the
        // project under
        if (classNameList.contains(key)) {
            return;
        }
        // For import static variable is extracted outside
        if (this.staticImportList.contains(key)) {
            return;
        }
        // SuppressWarnings is extracted outside
        if ("java.lang.SuppressWarnings".equals(key)) {
            return;
        }

        Map<String, String> packageNameMap = WSearchCacheManager.getInstance()
                .getPackageNameMap();
        // In the case of [*],
        // the class name to the extraction and are not covered under the same
        // package exists in the project under
        final int index = key.indexOf('*');
        if (index > 0
                && packageNameMap.values().contains(key.substring(0, index))) {
            return;
        }
        // If there is a matching extracted outside pattern, extracted outside
        for (Pattern pattern : this.ignoreList) {
            final Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                return;
            }
        }
        List<ParserResult> nodeList = this.resultMap.get(key);
        if (nodeList == null) {
            nodeList = new ArrayList<ParserResult>();
            this.resultMap.put(key, nodeList);
        }
        nodeList.add(new ParserResult(key,
                this.file.getLocation().toOSString(), this.rootNode
                        .getLineNumber(node.getStartPosition()), this.rootNode
                        .getColumnNumber(node.getStartPosition()), node));
    }

    /**
     * The return of the full class name of ASTNode that you specify.<br/>
     * If the package name is not resolved, return only the class name.<br/>
     * If ASTNode specified was not related to class, return null.<br/>
     * 
     * 
     * @param node
     *            ASTNode (class that represents the Java code structure name,
     *            type, such as declaration, etc.)
     * @return Full class name
     */
    private String getFullyQualifiedName(final ASTNode node) {
        // Cut out the top class name portion of the code
        final String topNodeName = getTopLevelClassName(node);
        // If this is not the class name
        if (topNodeName == null) {
            return null;
        }
        // If it is package directly specified in the code
        if (topNodeName.contains(".")) {
            return topNodeName;
        }
        String result = null;
        String fullname = null;
        String name = null;
        final IJavaElement[] parts = codeSelect(node);
        // If ASTParser has automatic interpretation from information on the
        // classpath
        if (parts.length > 0) {
            IType type = null;
            for (IJavaElement part : parts) {
                switch (part.getElementType()) {
                case IJavaElement.TYPE: // Clazz obj; -> a.b.c.Clazz
                    type = ((IType) part);
                    break;
                case IJavaElement.METHOD: // new Clazz(); -> a.b.c.Clazz
                    type = ((IMethod) part).getDeclaringType();
                    break;
                case IJavaElement.FIELD: // Clazz.TYPE_TEST; -> a.b.c.Clazz
                    type = ((IField) part).getDeclaringType();
                    break;
                default:
                    return null;
                }
                fullname = getFullyQualifiedName(type);
                // If the package name is not be obtained
                if (fullname == null || !fullname.contains(".")) {
                    continue;
                }
                name = type.getTypeQualifiedName();
                // If the top class name of the code of the report are not
                // included in the class name ASTParser is interpreted
                if (!name.equals(topNodeName)
                        && !name.startsWith(topNodeName + "$")
                        && !name.endsWith("$" + topNodeName)
                        && !name.contains("$" + topNodeName + "$")) {
                    continue;
                }
                // Delete (inner class measures) the class name of topNodeName
                // after the class name of the automatic interpretation
                final int index = fullname.indexOf(topNodeName + "$");
                if (index >= 0) {
                    fullname = fullname.substring(0,
                            index + topNodeName.length());
                }
                result = getFullyQualifiedName(fullname);
                // Search ends upon unresolved
                if (result != null) {
                    break;
                }
            }
        }

        List<String> classNameList = WSearchCacheManager.getInstance()
                .getClassNameList();
        // Interpreting class name only string codes on If not unresolved
        if (result == null) {
            result = getFullyQualifiedName(topNodeName);
        }
        // If that name is not resolved
        if (result == null) {
            // If it is interpreted as a class project under extraction excluded
            if (parts.length == 1 && classNameList.contains(fullname)) {
                return null;
            }
            result = topNodeName;
        }
        return result;
    }

    /**
     * The return of the full class name of IType that you specify.<br/>
     * If the acquisition of the full class name of IType specified fails, it
     * returns a null.<br/>
     * 
     * @param type
     *            IType (class that represents the type of Java)
     * @return Full class name
     */
    private String getFullyQualifiedName(final IType type) {

        Map<String, String> packageNameMap = WSearchCacheManager.getInstance()
                .getPackageNameMap();
        try {
            final IResource resource = type.getResource();
            // If it is not a class of its own project
            if (resource == null) {
                return type.getFullyQualifiedName();
            }
            // For a class of its own resources to the project
            else {
                final String key = resource.getLocation().toString();
                String packageName = packageNameMap.get(key);
                // If it is not in the cache
                if (packageName == null) {
                    final ICompilationUnit sourceCode = JavaCore
                            .createCompilationUnitFrom(ResourcesPlugin
                                    .getWorkspace().getRoot()
                                    .getFileForLocation(resource.getLocation()));
                    for (IPackageDeclaration packageDec : sourceCode
                            .getPackageDeclarations()) {
                        packageName = packageDec.getElementName();
                        // To adopt the name of the package that you obtained at
                        // the beginning
                        break;
                    }
                }
                if (packageName == null) {
                    return type.getTypeQualifiedName();
                } else {
                    return packageName + type.getTypeQualifiedName();
                }
            }
        } catch (IllegalStateException ignore) {
            // Ignore it because it is not a Java code
        } catch (Exception ignore) {
            // Ignore it because it is not a Java code
        }
        return null;
    }

    /**
     * The return of the full class name of ASTNode that you specify.<br/>
     * If the package name can not be resolved, return null.<br/>
     * 
     * @param name
     *            Full class name or class name
     * @return Full class name
     */
    private String getFullyQualifiedName(final String name) {
        String _name = name;
        // class in the java.lang package under extract only the class name
        final boolean isJavaLang = _name.startsWith("java.lang.");
        if (isJavaLang) {
            _name = _name.substring(_name.lastIndexOf('.') + 1);
        }
        // Search from import actually
        final String result = findFromImports(_name);
        // If it is not found
        if (result == null) {
            List<String> classNameList = WSearchCacheManager.getInstance()
                    .getClassNameList();
            // If in the java.lang package class
            if (isJavaLang) {
                return name; // return of the name rather than the _name
            }
            // If the package is being interpreted
            if (_name.contains(".")) {

                // If known class in the same package under
                if (_name.startsWith(this.packageName)
                        && classNameList.contains(_name)) {
                    return _name;
                }
                // If the inner class method local own
                if (_name.startsWith(this.packageName + this.className + "$")) {
                    return _name;
                }
            }
            // If only the class name
            else {
                // If those same package interpretation exists in the known
                // class list
                if (classNameList.contains(this.packageName + _name)) {
                    return this.packageName + _name;
                }
                // If those inner class interpretation within the same class
                // exists in the known class list
                if (classNameList.contains(this.packageName + this.className
                        + "$" + _name)) {
                    return this.packageName + _name;
                }
            }
        }
        // If found
        else {
            return result;
        }
        return null;
    }

    /**
     * From the list of import declarations code specified, <br/>
     * extract the full class name that matches the first full class name or
     * class name is specified.<br/>
     * The return null if not found.<br/>
     * 
     * @param name
     *            Full class name or class name
     * @return Full class name or package name that matches the first
     */
    private String findFromImports(final String name) {
        try {
            String _name = name;
            // the inner class is specified, substituted with. The $
            if (_name.contains("$")) {
                _name = _name.replace('$', '.');
            }
            final IImportDeclaration[] imports = this.sourceCode.getImports();
            for (IImportDeclaration imp : imports) {
                final String impName = imp.getElementName();
                // In the case of import, which according to [*], if the package
                // name is part of the same match
                if (impName.contains("*")) {
                    if (name.contains(".")) { // Comparison in the name instead
                                              // of _name
                        final String packagename = impName.substring(0,
                                impName.lastIndexOf('.'));
                        if (_name.startsWith(packagename)) {
                            return _name;
                        }
                    }
                } else {
                    // For classes that are specified package name
                    if (name.contains(".")) { // Comparison in the name instead
                                              // of _name
                        // The match If the first part and the import statement
                        // is a match<br/>
                        if (impName.equals(_name)) {
                            return _name;
                        }
                    }
                    // If only the class name
                    else {
                        // Match If the class name part of the import statement
                        // are the same (assuming there is a dot in the import)
                        if (impName.endsWith("." + _name)) {
                            return impName;
                        }
                    }
                }
            }
        } catch (JavaModelException e) {
            // Without the need for handling
        }
        return null;
    }

    /**
     * From the Java code for ASTNode specified, extract only the class name of
     * the top-level.<br/>
     * The return null if the class name of the top-level can not be found.<br/>
     * 
     * @param node
     *            Node to be analyzed
     * @return Class name of the top-level
     * @see #getTopLevelClassName(String)
     */
    private String getTopLevelClassName(final ASTNode node) {
        final ASTNode parent = node.getParent();
        final String nodeName = node.toString();
        String method = null;
        // Check whether the information of class-related
        switch (parent.getNodeType()) {
        case ASTNode.METHOD_DECLARATION:
            method = ((MethodDeclaration) parent).getName().getIdentifier();
            break;
        case ASTNode.SUPER_METHOD_INVOCATION:
            method = ((SuperMethodInvocation) parent).getName().getIdentifier();
            break;
        case ASTNode.METHOD_INVOCATION:
            method = ((MethodInvocation) parent).getName().getIdentifier();
            break;
        case ASTNode.SIMPLE_TYPE:
        case ASTNode.NORMAL_ANNOTATION:
        case ASTNode.MARKER_ANNOTATION:
            return getTopLevelClassName(nodeName);
        case ASTNode.SINGLE_MEMBER_ANNOTATION:
            break;
        default:
            // For QUALIFIED_NAME non-extracted outside
            if (node.getNodeType() != ASTNode.QUALIFIED_NAME) {
                return null;
            }
            break;
        }
        // If it matches the method name is extracted outside
        if (method != null && method.equals(nodeName)) {
            return null;
        }
        final String name = getTopLevelClassName(nodeName);
        // Can not be detected properly if the class name all uppercase
        if (name != null && !name.equals(name.toUpperCase(Locale.getDefault()))) {
            return name;
        }
        return null;
    }

    /**
     * From the class string of nested, extract only the class name of the
     * top-level.<br/>
     * The return null if the class name of the top-level can not be found.<br/>
     * Conversion example. <br/>
     * <br/>
     * 
     * <pre>
     * <br/>
     *  Case 1<br/>
     *  Specification string: sample.AClass.BClass.CClass.TEST<br/>
     *  Return string: sample.AClass<br/>
     * <br/>
     *  Case 2<br/>
     *  Specification string: sample.a.b.c.d<br/>
     *  Return string: null<br/>
     * <br/>
     *  Case 3<br/>
     *  Specification string: sample.a.b.c.d.TEST<br/>
     *  Return string: TEST<br/>
     * </pre>
     * 
     * </p>
     * 
     * @param className
     *            Class Specification string
     * @return Class name of the top-level
     * @see #isLocalClassName(String)
     */
    private String getTopLevelClassName(final String className) {
        String old = null;
        String current = className;
        if (className != null && !className.endsWith(".")) {
            for (int i = current.lastIndexOf('.'); i > 0; i = current
                    .lastIndexOf('.')) {
                old = current;
                current = current.substring(0, i);
                // Can not be detected properly in the case of non-package names
                // in all lowercase
                if (current.equals(current.toLowerCase(Locale.getDefault()))
                        && isLocalClassName(old.substring(i + 1, old.length()))) {
                    return old;
                }
            }
            if (isLocalClassName(current)) {
                return current;
            }
        }
        return null;
    }

    /**
     * Check the specified string to see if it matches the rules of the class
     * name.<br/>
     * Check on the assumption that does not include the package name.<br/>
     * 
     * @param className
     *            String to check
     * @return True if the specified string matches the class name
     */
    private boolean isLocalClassName(final String className) {
        return className.length() > 2 // for <O,E,T,V,MK,MV...>
                && className.matches("[A-Z][A-Z,a-z,0-9,_,$]*");
    }

    /**
     * Convert IJavaElement the ASTNode specified.<br/>
     * The return null if the conversion failed.<br/>
     * 
     * @param node
     *            ASTNode (class that represents the Java source code structure
     *            name, type, such as declaration, etc.)
     * @return IJavaElement instance of ASTNode specified
     */
    private IJavaElement[] codeSelect(final ASTNode node) {
        try {
            final int start = node.getStartPosition();
            final int length = node.getLength();
            return this.sourceCode.codeSelect(start, length);
        } catch (JavaModelException e) {
            // Without the need for handling
        }
        return new IJavaElement[0];
    }

    /**
     * It is called automatically when parsing the Java source code.<br/>
     * Because the package name part analysis to be skipped, this method will
     * return false, <br/>
     * without any processing, It is not something that the user will call.<br/>
     * 
     * @param node
     *            Node to be analyzed by visit
     * @return Whether to continue the code analysis continues
     */
    @Override
    public boolean visit(final PackageDeclaration node) {
        return false;
    }

    /**
     * It is called automatically when parsing the Java source code.<br/>
     * The overriding a method ASTVisitor to extract the import class.<br/>
     * This method does not cause the user to call.<br/>
     * 
     * @param node
     *            Node to be analyzed by visit
     * @return Whether to continue the code analysis continues
     */
    @Override
    public boolean visit(final ImportDeclaration node) {
        String fullname = node.getName().getFullyQualifiedName();
        if (node.toString().contains("*")) {
            fullname += ".*";
        }
        if (node.isStatic()) {
            this.staticImportList.add(fullname);
            fullname = fullname.substring(0, fullname.lastIndexOf('.'));
        }
        if (fullname != null) {
            putResultMap(fullname, node);
        }
        return false;
    }

    /**
     * It is called automatically when parsing the Java source code.<br/>
     * It overrides the method of ASTVisitor to extract the class information <br/>
     * that can not be extracted in other visit. <br/>
     * This method does not cause the user to call.<br/>
     * 
     * @param node
     *            Node to be analyzed by visit
     * @return Whether to continue the code analysis continues
     */
    @Override
    public boolean visit(final QualifiedName node) {
        final String fullname = getFullyQualifiedName(node);
        if (fullname != null) {
            putResultMap(fullname, node);
        }
        return false;
    }

    /**
     * It is called automatically when parsing the Java source code.<br/>
     * overrides the method of ASTVisitor to extract the class information <br/>
     * that can not be extracted in other visit. <br/>
     * This method does not cause the user to call.<br/>
     * 
     * @param node
     *            Node to be analyzed by visit
     * @return Whether to continue the code analysis continues
     */
    @Override
    public boolean visit(final SimpleName node) {
        final String fullname = getFullyQualifiedName(node);
        if (fullname != null) {
            putResultMap(fullname, node);
        }
        return false;
    }

    /**
     * Updated the map of member variables to analyze the code.<br/>
     * Nothing takes action if the parsed already.<br/>
     * 
     * @throws WSearchBizException
     *             If it fails to parse code
     */
    @Override
    public void analyze() throws WSearchBizException {
        if (!isAnalyzed()) {
            CompilationUnit unit = null;
            try {
                if (this.sourceCode.isStructureKnown()) {
                    final ASTParser parser = ASTParser.newParser(JLSN);
                    parser.setSource(this.sourceCode);
                    unit = (CompilationUnit) parser
                            .createAST(new NullProgressMonitor());
                    // Initialize member variables necessary information
                    this.rootNode = unit;
                    if (unit.getPackage() == null) {
                        this.packageName = "";
                    } else {
                        this.packageName = unit.getPackage().getName() + ".";
                    }
                    // The analysis start the visit by source
                    unit.accept(this);
                }
                // For a source that can not be parsed
                else {
                    throw new ParseException(MessageFormat.format(Activator
                            .getResourceString(JavaAnalyzer.class.getName()
                                    + ".err.msg.StructureUnknownErr"),
                            this.file), -1);
                }
            } catch (RuntimeException e) {
                throw e;
            } catch (Exception e) {
                throw new WSearchBizException(
                        WSPhaseService.TYPE.SRC_PARSER_AND_COMPARE, e);
            }
            this.analyzed = true;
        }
    }

    /**
     * The return whether performed the {@link #analyze()}.<br/>
     * 
     * @return If true {@link #analyze()} that has already been executed
     */
    @Override
    public boolean isAnalyzed() {
        return this.analyzed;
    }

    /**
     * The return in the Map the extracted information.<br/>
     * Map has become organized as follows.<br/>
     * key:Full class name (or class name)<br/>
     * value:List of Place that is used(ParseResult)<br/>
     * 
     * @return Map of extracted information
     * @throws IllegalStateException
     *             if {@link #analyze()} not already running
     */
    @Override
    public Map<String, List<ParserResult>> getAnalyzedMap() {
        if (isAnalyzed()) {
            return this.resultMap;
        }
        throw new IllegalStateException(
                Activator.getResourceString(JavaAnalyzer.class.getName()
                        + ".err.msg.NotYetAnalyzeErr"));
    }

    /**
     * The return in the Map extract information corresponding to mapType you
     * specify.<br/>
     * mapType is valid only Analyzer.TYPE_JAVA. <br/>
     * Map has become organized as follows.<br/>
     * key:Full class name (or class name)<br/>
     * value:List of Place that is used(ParseResult)<br/>
     * 
     * @return Map of extracting information corresponding to mapType you
     *         specify
     * @throws IllegalArgumentException
     *             If the specified maptype that do not support
     * @throws IllegalStateException
     *             if {@link #analyze()} not already running
     */
    @Override
    public Map<String, List<ParserResult>> getAnalyzedMap(final int mapType) {
        if (mapType == TYPE_JAVA) {
            return getAnalyzedMap();
        }
        throw new IllegalArgumentException(MessageFormat.format(
                Activator.getResourceString(JavaAnalyzer.class.getName()
                        + ".err.msg.MapTypeErr"), mapType));
    }
}
