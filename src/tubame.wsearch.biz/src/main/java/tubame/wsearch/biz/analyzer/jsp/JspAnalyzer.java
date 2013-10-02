/*
 * JspAnalyzer.java
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
/*
 * Licensed to the Apache Software Foundation (ASF) under one or more
 * contributor license agreements. See the NOTICE file distributed with
 * this work for additional information regarding copyright ownership.
 * The ASF licenses this file to You under the Apache License, Version 2.0
 * (the "License"); you may not use this file except in compliance with
 * the License. You may obtain a copy of the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS,
 * WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
 * See the License for the specific language governing permissions and
 * limitations under the License.
 */
package tubame.wsearch.biz.analyzer.jsp;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStreamReader;
import java.text.MessageFormat;
import java.text.ParseException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.Attributes;
import org.xml.sax.helpers.AttributesImpl;

import tubame.wsearch.biz.WSPhaseService;
import tubame.wsearch.biz.analyzer.Analyzer;
import tubame.wsearch.biz.analyzer.AttributeParser;
import tubame.wsearch.biz.analyzer.JspReader;
import tubame.wsearch.biz.analyzer.Mark;
import tubame.wsearch.biz.analyzer.ParserResult;
import tubame.wsearch.biz.analyzer.UniqueAttributesImpl;
import tubame.wsearch.biz.cache.WSearchCacheManager;
import tubame.wsearch.biz.ex.WSearchBizException;
import tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * This is a class to extract class information you are using in your code to
 * parse the source code Jsp, the tag library information.<br/>
 * Analysis on the assumption that the imported project to the eclipse.<br/>
 * Example of usage.<br/>
 * 
 * <pre>
 * ResourcesPlugin.getWorkspace().getRoot().getProject(&quot;test&quot;)
 *         .accept(new IResourceVisitor() {
 *             public boolean visit(IResource resource) throws CoreException {
 *                 List&lt;Pattern&gt; ignores = new ArrayList&lt;Pattern&gt;();
 *                 ignores.add(Pattern.compile(&quot;&circ;java.*&quot;));
 *                 ignores.add(Pattern.compile(&quot;&circ;jp.*&quot;));
 *                 switch (resource.getType()) {
 *                 case IResource.FILE:
 *                     Analyzer analyzer = null;
 *                     if (resource.getFileExtension() == null) {
 *                         break;
 *                     } else if (resource.getFileExtension().equals(&quot;jsp&quot;)) {
 *                         analyzer = new JspAnalyzer((IFile) resource, ignores);
 *                         analyzer.analyze();
 *                         Map&lt;String, List&lt;ParseResult&gt;&gt; map = analyzer
 *                                 .getResultMap();
 *                         // Can also be retrieved by specifying a Map as follows:
 *                         // .getResultMap(Analyzer.TYPE_JSP_IMPORT);
 *                         // .getResultMap(Analyzer.TYPE_JSP_USEBEAN);
 *                         // .getResultMap(Analyzer.TYPE_JSP_TAGLIB);
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
 * This implementation is not synchronized.<br/>
 * If you want to access multiple threads in parallel, those threads to change
 * the member variable, it must be synchronized externally.<br/>
 * 
 * This class implements a parser for a JSP page (non-xml view). JSP page
 * grammar is included here for reference. The token '#' that appears in the
 * production indicates the current input token location in the production.
 * 
 * #Modified By Nippon Telegraph and Telephone Corporation 2013/3/15
 * 
 * @author Kin-man Chung
 * @author Shawn Bayern
 * @author Mark Roth
 */
public class JspAnalyzer implements Analyzer {

    private static final Logger LOGGER = LoggerFactory
            .getLogger(JspAnalyzer.class);

    public static final String INCLUDE_ACTION = "include";
    public static final String FORWARD_ACTION = "forward";
    public static final String INVOKE_ACTION = "invoke";
    public static final String DOBODY_ACTION = "doBody";
    public static final String GET_PROPERTY_ACTION = "getProperty";
    public static final String SET_PROPERTY_ACTION = "setProperty";
    public static final String USE_BEAN_ACTION = "useBean";
    public static final String PLUGIN_ACTION = "plugin";
    public static final String ELEMENT_ACTION = "element";
    public static final String ATTRIBUTE_ACTION = "attribute";
    public static final String BODY_ACTION = "body";
    public static final String FALLBACK_ACTION = "fallback";
    public static final String PARAMS_ACTION = "params";
    public static final String PARAM_ACTION = "param";
    public static final String OUTPUT_ACTION = "output";

    /**
     * True if the analyze the executed
     */
    private boolean analyzed;

    /**
     * The path of the JSP file
     */
    private String filePath;

    /**
     * Map that is registered import class information groups are used in JSP
     * file
     */
    private Map<String, List<ParserResult>> importResultMap;

    /**
     * Map that is registered useBean class information groups are used in JSP
     * file
     */
    private Map<String, List<ParserResult>> useBeanResultMap;

    /**
     * Map that is registered tag library information groups are used in JSP
     * file
     */
    private Map<String, List<ParserResult>> taglibResultMap;

    /**
     * Object that manages the analysis point of JSP source code
     */
    private Mark mark;

    /**
     * Buffer object to be used to analyze the source code JSP
     */
    private JspReader reader;

    /**
     * Regular expression string list in which the extracted outside
     */
    private List<Pattern> ignoreList;

    /**
     * Constructor.<br/>
     * Initialize the JspAnalyzer.<br/>
     * 
     * @param filePath
     *            The path of the JSP file
     */
    public JspAnalyzer(final String filePath) {
        this(filePath, new ArrayList<Pattern>());
    }

    /**
     * Constructor.<br/>
     * Initialize the JspAnalyzer.<br/>
     * By specifying a list of regular expression string keywords that extracted
     * outside, you can refine the extraction target.<br/>
     * 
     * @param filePath
     *            The path of the JSP file
     * @param ignoreList
     *            Regular expression string list in which the extracted outside
     */
    public JspAnalyzer(final String filePath, final List<Pattern> ignoreList) {
        File file = new File(filePath);
        InputStreamReader isReader = null;
        JspReader reader = null;
        try {
            LOGGER.trace(MessageUtil.getResourceString("debug.msg.read.file")
                    + filePath);
            isReader = new InputStreamReader(new BufferedInputStream(
                    new FileInputStream(file)));
            reader = new JspReader(filePath, isReader);
        } catch (IOException e) {
            throw new IllegalArgumentException(e.getMessage(), e);
        } finally {
            if (isReader != null) {
                try {
                    isReader.close();
                } catch (Exception e) {
                    // Without the need for handling
                }
            }
        }
        this.filePath = filePath;
        this.reader = reader;
        this.mark = reader.mark();
        this.importResultMap = new HashMap<String, List<ParserResult>>();
        this.useBeanResultMap = new HashMap<String, List<ParserResult>>();
        this.taglibResultMap = new HashMap<String, List<ParserResult>>();
        this.analyzed = false;
        this.ignoreList = ignoreList;
    }

    /**
     * Register the Map corresponding to the mapType you specified the class
     * information that is extracted.<br/>
     * This method does not cause the user to call.<br/>
     * 
     * @param mapType
     *            Type of map you want to register
     * @param key
     *            Tag library information or class name
     * @param node
     *            Attributes object where that class is used
     */
    private void putResultMap(final int mapType, final String key,
            final Attributes node) {
        for (Pattern pattern : this.ignoreList) {
            final Matcher matcher = pattern.matcher(key);
            if (matcher.matches()) {
                return;
            }
        }
        if (key.endsWith(".*")) {
            String packageName = key.substring(0, (key.lastIndexOf(".") + 1));
            if (WSearchCacheManager.getInstance().getPackageNameMap()
                    .containsValue(packageName)) {
                return;
            }
        } else {
            if (WSearchCacheManager.getInstance().getClassNameList()
                    .contains(key)) {
                return;
            }
        }
        List<ParserResult> nodeList = null;
        switch (mapType) {
        case TYPE_JSP_IMPORT:
            nodeList = this.importResultMap.get(key);
            if (nodeList == null) {
                nodeList = new ArrayList<ParserResult>();
                this.importResultMap.put(key, nodeList);
            }
            break;
        case TYPE_JSP_USEBEAN:
            nodeList = this.useBeanResultMap.get(key);
            if (nodeList == null) {
                nodeList = new ArrayList<ParserResult>();
                this.useBeanResultMap.put(key, nodeList);
            }
            break;
        case TYPE_JSP_TAGLIB:
            nodeList = this.taglibResultMap.get(key);
            if (nodeList == null) {
                nodeList = new ArrayList<ParserResult>();
                this.taglibResultMap.put(key, nodeList);
            }
            break;
        default:
            throw new IllegalArgumentException(MessageFormat.format(
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".err.illegalarg"), mapType));
        }
        nodeList.add(new ParserResult(key, this.filePath, mark.getLineNumber(),
                mark.getColumnNumber(), node));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void analyze() throws WSearchBizException {
        if (!isAnalyzed()) {
            try {
                while (this.reader.hasMoreInput()) {
                    parseElements();
                }
            } catch (RuntimeException e) {
                throw new WSearchBizException(
                        WSPhaseService.TYPE.SRC_PARSER_AND_COMPARE, e);
            } catch (ParseException e) {
                throw new WSearchBizException(
                        WSPhaseService.TYPE.SRC_PARSER_AND_COMPARE, e);
            } catch (Exception e) {
                throw new WSearchBizException(
                        WSPhaseService.TYPE.SRC_PARSER_AND_COMPARE,
                        (this.mark.toString() + e.toString()));
            }
            this.analyzed = true;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isAnalyzed() {
        return this.analyzed;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<ParserResult>> getAnalyzedMap() {
        if (isAnalyzed()) {
            final Map<String, List<ParserResult>> map = new HashMap<String, List<ParserResult>>();
            map.putAll(this.importResultMap);
            map.putAll(this.useBeanResultMap);
            map.putAll(this.taglibResultMap);
            return map;
        }
        throw new IllegalStateException(MessageUtil.getResourceString(this
                .getClass().getName() + ".err.illegalstate"));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Map<String, List<ParserResult>> getAnalyzedMap(final int mapType) {
        if (isAnalyzed()) {
            switch (mapType) {
            case TYPE_JSP_IMPORT:
                return this.importResultMap;
            case TYPE_JSP_USEBEAN:
                return this.useBeanResultMap;
            case TYPE_JSP_TAGLIB:
                return this.taglibResultMap;
            default:
                throw new IllegalArgumentException(MessageFormat.format(
                        MessageUtil.getResourceString(this.getClass().getName()
                                + ".err.illegalarg"), mapType));
            }
        }
        throw new IllegalStateException(MessageUtil.getResourceString(this
                .getClass().getName() + ".err.illegalstate"));
    }

    /*
     * AllBody ::= ( '<%--' JSPCommentBody ) | ( '<%@' DirectiveBody ) | (
     * '<jsp:directive.' XMLDirectiveBody ) | ( '<%!' DeclarationBody ) | (
     * '<jsp:declaration' XMLDeclarationBody ) | ( '<%=' ExpressionBody ) | (
     * '<jsp:expression' XMLExpressionBody ) | ( '${' ELExpressionBody ) | (
     * '<%' ScriptletBody ) | ( '<jsp:scriptlet' XMLScriptletBody ) | (
     * '<jsp:text' XMLTemplateText ) | ( '<jsp:' StandardAction ) | ( '<'
     * CustomAction CustomActionBody ) | TemplateText
     */
    private void parseElements() throws ParseException {
        this.mark = this.reader.mark();
        if (this.reader.matches("<%--")) {
            parseComment();
        } else if (this.reader.matches("<%@")) {
            parseDirective();
        } else if (this.reader.matches("<jsp:directive.")) {
            parseXMLDirective();
        } else if (this.reader.matches("<%!")) {
            parseDeclaration();
        } else if (this.reader.matches("<jsp:declaration")) {
            parseXMLDeclaration();
        } else if (this.reader.matches("<%=")) {
            parseExpression();
        } else if (this.reader.matches("<jsp:expression")) {
            parseXMLExpression();
        } else if (this.reader.matches("<%")) {
            parseScriptlet();
        } else if (this.reader.matches("<jsp:scriptlet")) {
            parseXMLScriptlet();
        } else if (this.reader.matches("<jsp:text")) {
            parseXMLTemplateText();
        } else if (this.reader.matches("<jsp:")) {
            parseStandardAction();
        } else if (!parseCustomTag()) {
            parseTemplateText();
        }
    }

    /*
     * JSPCommentBody ::= (Char* - (Char* '--%>')) '--%>'
     */
    private void parseComment() throws ParseException {
        this.mark = this.reader.mark();
        final Mark stop = this.reader.skipUntil("--%>");
        if (stop == null) {
            throw new ParseException(MessageUtil.getJspErrorMessage(
                    this.mark.toString(), this.getClass().getName()
                            + ".err.unterminated", "<%--"), this.mark.cursor);
        }
    }

    /*
     * Parses a directive with the following syntax: Directive ::= S? ( 'page'
     * PageDirective | 'include' IncludeDirective | 'taglib' TagLibDirective) S?
     * '%>'
     * 
     * TagDirective ::= S? ('tag' PageDirective | 'include' IncludeDirective |
     * 'taglib' TagLibDirective) | 'attribute AttributeDirective | 'variable
     * VariableDirective S? '%>'
     */
    private void parseDirective() throws ParseException {
        this.reader.skipSpaces();
        String directive = null;
        if (this.reader.matches("page")) {
            directive = "<%@ page";
            parsePageDirective();
        } else if (this.reader.matches("include")) {
            directive = "<%@ include";
            parseIncludeDirective();
        } else if (this.reader.matches("taglib")) {
            directive = "<%@ taglib";
            parseTaglibDirective();
        } else if (this.reader.matches("tag")) {
            directive = "<%@ tag";
            parseTagDirective();
        } else if (this.reader.matches("attribute")) {
            directive = "<%@ attribute";
            parseAttributeDirective();
        } else if (this.reader.matches("variable")) {
            directive = "<%@ variable";
            parseVariableDirective();
        } else {
            throw new ParseException(MessageUtil.getJspErrorMessage(this.reader
                    .mark().toString(), this.getClass().getName()
                    + ".err.invaliddir"), this.reader.mark().cursor);
        }
        this.reader.skipSpaces();
        if (!this.reader.matches("%>")) {
            throw new ParseException(MessageUtil.getJspErrorMessage(
                    this.mark.toString(), this.getClass().getName()
                            + ".err.unterminated", directive), this.mark.cursor);
        }
    }

    /*
     * Parses a directive with the following syntax:
     * 
     * XMLJSPDirectiveBody ::= S? ( ( 'page' PageDirectiveAttrList S? ( '/>' | (
     * '>' S? ETag ) ) | ( 'include' IncludeDirectiveAttrList S? ( '/>' | ( '>'
     * S? ETag ) ) | <TRANSLATION_ERROR>
     * 
     * XMLTagDefDirectiveBody ::= ( ( 'tag' TagDirectiveAttrList S? ( '/>' | (
     * '>' S? ETag ) ) | ( 'include' IncludeDirectiveAttrList S? ( '/>' | ( '>'
     * S? ETag ) ) | ( 'attribute' AttributeDirectiveAttrList S? ( '/>' | ( '>'
     * S? ETag ) ) | ( 'variable' VariableDirectiveAttrList S? ( '/>' | ( '>' S?
     * ETag ) ) ) | <TRANSLATION_ERROR>
     */
    private void parseXMLDirective() throws ParseException {
        this.reader.skipSpaces();
        String eTag = null;
        if (this.reader.matches("page")) {
            eTag = "jsp:directive.page";
            parsePageDirective();
        } else if (this.reader.matches("include")) {
            eTag = "jsp:directive.include";
            parseIncludeDirective();
        } else if (this.reader.matches("tag")) {
            eTag = "jsp:directive.tag";
            parseTagDirective();
        } else if (this.reader.matches("attribute")) {
            eTag = "jsp:directive.attribute";
            parseAttributeDirective();
        } else if (this.reader.matches("variable")) {
            eTag = "jsp:directive.variable";
            parseVariableDirective();
        } else {
            throw new ParseException(MessageUtil.getJspErrorMessage(this.reader
                    .mark().toString(), this.getClass().getName()
                    + ".err.invaliddir"), this.reader.mark().cursor);
        }
        this.reader.skipSpaces();
        if (this.reader.matches(">")) {
            this.reader.skipSpaces();
            if (!this.reader.matchesETag(eTag)) {
                throw new ParseException(MessageUtil.getJspErrorMessage(
                        this.mark.toString(), this.getClass().getName()
                                + ".err.unterminated", "<" + eTag),
                        this.mark.cursor);
            }
        } else if (!this.reader.matches("/>")) {
            throw new ParseException(MessageUtil.getJspErrorMessage(
                    this.mark.toString(), this.getClass().getName()
                            + ".err.unterminated", "<" + eTag),
                    this.mark.cursor);
        }
    }

    /*
     * DeclarationBody ::= (Char* - (char* '%>')) '%>'
     */
    private void parseDeclaration() throws ParseException {
        this.mark = this.reader.mark();
        final Mark stop = this.reader.skipUntil("%>");
        if (stop == null) {
            throw new ParseException(MessageUtil.getJspErrorMessage(
                    this.mark.toString(), this.getClass().getName()
                            + ".err.unterminated", "<%!"), this.mark.cursor);
        }
    }

    /*
     * XMLDeclarationBody ::= ( S? '/>' ) | ( S? '>' (Char* - (char* '<'))
     * CDSect?)* ETag | <TRANSLATION_ERROR> CDSect ::= CDStart CData CDEnd
     * CDStart ::= '<![CDATA[' CData ::= (Char* - (Char* ']]>' Char*)) CDEnd ::=
     * ']]>'
     */
    private void parseXMLDeclaration() throws ParseException {
        this.reader.skipSpaces();
        if (!this.reader.matches("/>")) {
            if (!this.reader.matches(">")) {
                throw new ParseException(MessageUtil.getJspErrorMessage(
                        this.mark.toString(), this.getClass().getName()
                                + ".err.unterminated", "<jsp:declaration>"),
                        this.mark.cursor);
            }
            Mark stop;
            while (true) {
                this.mark = this.reader.mark();
                stop = this.reader.skipUntil("<");
                if (stop == null) {
                    throw new ParseException(
                            MessageUtil.getJspErrorMessage(
                                    this.mark.toString(), this.getClass()
                                            .getName() + ".err.unterminated",
                                    "<jsp:declaration>"), this.mark.cursor);
                }
                if (this.reader.matches("![CDATA[")) {
                    this.mark = this.reader.mark();
                    stop = this.reader.skipUntil("]]>");
                    if (stop == null) {
                        throw new ParseException(
                                MessageUtil.getJspErrorMessage(this.mark
                                        .toString(), this.getClass().getName()
                                        + ".err.unterminated", "CDATA"),
                                this.mark.cursor);
                    }
                } else {
                    break;
                }
            }
            if (!this.reader.matchesETagWithoutLessThan("jsp:declaration")) {
                throw new ParseException(MessageUtil.getJspErrorMessage(
                        this.mark.toString(), this.getClass().getName()
                                + ".err.unterminated", "<jsp:declaration>"),
                        this.mark.cursor);
            }
        }
    }

    /*
     * ExpressionBody ::= (Char* - (char* '%>')) '%>'
     */
    private void parseExpression() throws ParseException {
        this.mark = this.reader.mark();
        final Mark stop = this.reader.skipUntil("%>");
        if (stop == null) {
            throw new ParseException(MessageUtil.getJspErrorMessage(
                    this.mark.toString(), this.getClass().getName()
                            + ".err.unterminated", "<%="), this.mark.cursor);
        }
    }

    /*
     * XMLExpressionBody ::= ( S? '/>' ) | ( S? '>' (Char* - (char* '<'))
     * CDSect?)* ETag ) | <TRANSLATION_ERROR>
     */
    private void parseXMLExpression() throws ParseException {
        this.reader.skipSpaces();
        if (!this.reader.matches("/>")) {
            if (!this.reader.matches(">")) {
                throw new ParseException(MessageUtil.getJspErrorMessage(
                        this.mark.toString(), this.getClass().getName()
                                + ".err.unterminated", "<jsp:expression>"),
                        this.mark.cursor);
            }
            Mark stop;
            while (true) {
                this.mark = this.reader.mark();
                stop = this.reader.skipUntil("<");
                if (stop == null) {
                    throw new ParseException(MessageUtil.getJspErrorMessage(
                            this.mark.toString(), this.getClass().getName()
                                    + ".err.unterminated", "<jsp:expression>"),
                            this.mark.cursor);
                }
                if (this.reader.matches("![CDATA[")) {
                    this.mark = this.reader.mark();
                    stop = this.reader.skipUntil("]]>");
                    if (stop == null) {
                        throw new ParseException(
                                MessageUtil.getJspErrorMessage(this.mark
                                        .toString(), this.getClass().getName()
                                        + ".err.unterminated", "CDATA"),
                                this.mark.cursor);
                    }
                } else {
                    break;
                }
            }
            if (!this.reader.matchesETagWithoutLessThan("jsp:expression")) {
                throw new ParseException(MessageUtil.getJspErrorMessage(
                        this.mark.toString(), this.getClass().getName()
                                + ".err.unterminated", "<jsp:expression>"),
                        this.mark.cursor);
            }
        }
    }

    /*
     * ELExpressionBody (following "${" to first unquoted "}")
     */
    private void parseELExpression(final char type) throws ParseException {
        this.mark = this.reader.mark();
        boolean singleQuoted = false, doubleQuoted = false;
        int currentChar;
        do {
            currentChar = this.reader.nextChar();
            if (currentChar == '\\' && (singleQuoted || doubleQuoted)) {
                // skip character following '\' within quotes
                this.reader.nextChar();
                currentChar = this.reader.nextChar();
            }
            if (currentChar == -1) {
                throw new ParseException(MessageUtil.getJspErrorMessage(
                        this.mark.toString(), this.getClass().getName()
                                + ".err.unterminated", type + "{"),
                        this.mark.cursor);
            }
            if (currentChar == '"' && !singleQuoted) {
                doubleQuoted = !doubleQuoted;
            }
            if (currentChar == '\'' && !doubleQuoted) {
                singleQuoted = !singleQuoted;
            }
        } while (currentChar != '}' || (singleQuoted || doubleQuoted));
    }

    /*
     * ScriptletBody ::= (Char* - (char* '%>')) '%>'
     */
    private void parseScriptlet() throws ParseException {
        this.mark = this.reader.mark();
        final Mark stop = this.reader.skipUntil("%>");
        if (stop == null) {
            throw new ParseException(MessageUtil.getJspErrorMessage(
                    this.mark.toString(), this.getClass().getName()
                            + ".err.unterminated", "<%"), this.mark.cursor);
        }
    }

    /*
     * XMLScriptletBody ::= ( S? '/>' ) | ( S? '>' (Char* - (char* '<'))
     * CDSect?)* ETag ) | <TRANSLATION_ERROR>
     */
    private void parseXMLScriptlet() throws ParseException {
        this.reader.skipSpaces();
        if (!this.reader.matches("/>")) {
            if (!this.reader.matches(">")) {
                throw new ParseException(MessageUtil.getJspErrorMessage(
                        this.mark.toString(), this.getClass().getName()
                                + ".err.unterminated", "<jsp:scriptlet>"),
                        this.mark.cursor);
            }
            Mark stop;
            while (true) {
                this.mark = this.reader.mark();
                stop = this.reader.skipUntil("<");
                if (stop == null) {
                    throw new ParseException(MessageUtil.getJspErrorMessage(
                            this.mark.toString(), this.getClass().getName()
                                    + ".err.unterminated", "<jsp:scriptlet>"),
                            this.mark.cursor);
                }
                if (this.reader.matches("![CDATA[")) {
                    this.mark = this.reader.mark();
                    stop = this.reader.skipUntil("]]>");
                    if (stop == null) {
                        throw new ParseException(
                                MessageUtil.getJspErrorMessage(this.mark
                                        .toString(), this.getClass().getName()
                                        + ".err.unterminated", "CDATA"),
                                this.mark.cursor);
                    }
                } else {
                    break;
                }
            }
            if (!this.reader.matchesETagWithoutLessThan("jsp:scriptlet")) {
                throw new ParseException(MessageUtil.getJspErrorMessage(
                        this.mark.toString(), this.getClass().getName()
                                + ".err.unterminated", "<jsp:scriptlet>"),
                        this.mark.cursor);
            }
        }
    }

    /*
     * XMLTemplateText ::= ( S? '/>' ) | ( S? '>' ( ( Char* - ( Char* ( '<' |
     * '${' ) ) ) ( '${' ELExpressionBody )? CDSect? )* ETag ) |
     * <TRANSLATION_ERROR>
     */
    private void parseXMLTemplateText() throws ParseException {
        this.reader.skipSpaces();
        if (!this.reader.matches("/>")) {
            if (!this.reader.matches(">")) {
                throw new ParseException(MessageUtil.getJspErrorMessage(
                        this.mark.toString(), this.getClass().getName()
                                + ".err.unterminated", "<jsp:text>"),
                        this.mark.cursor);
            }
            while (this.reader.hasMoreInput()) {
                int chara = this.reader.nextChar();
                if (chara == '<') {
                    // Check for <![CDATA[
                    if (!this.reader.matches("![CDATA[")) {
                        break;
                    }
                    this.mark = this.reader.mark();
                    final Mark stop = this.reader.skipUntil("]]>");
                    if (stop == null) {
                        throw new ParseException(
                                MessageUtil.getJspErrorMessage(this.mark
                                        .toString(), this.getClass().getName()
                                        + ".err.unterminated", "CDATA"),
                                this.mark.cursor);
                    }
                } else if (chara == '\\') {
                    if (!this.reader.hasMoreInput()) {
                        break;
                    }
                    chara = this.reader.nextChar();
                    if (chara != '$' && chara != '#') {
                        continue;
                    }
                } else if (chara == '$' || chara == '#') {
                    if (!this.reader.hasMoreInput()) {
                        break;
                    }
                    if (this.reader.nextChar() != '{') {
                        this.reader.pushChar();
                        continue;
                    }

                    // Mark and parse the EL expression and create its node:
                    this.mark = this.reader.mark();
                    parseELExpression((char) chara);

                    this.mark = this.reader.mark();
                }
            }

            if (!this.reader.hasMoreInput()) {
                throw new ParseException(MessageUtil.getJspErrorMessage(
                        this.mark.toString(), this.getClass().getName()
                                + ".err.unterminated", "<jsp:text>"),
                        this.mark.cursor);
            } else if (!this.reader.matchesETagWithoutLessThan("jsp:text")) {
                throw new ParseException(MessageUtil.getJspErrorMessage(
                        this.mark.toString(), this.getClass().getName()
                                + ".err.unescaped"), this.mark.cursor);
            }
        }
    }

    /*
     * Parses a page directive with the following syntax: PageDirective ::= ( S
     * Attribute)*
     */
    private void parsePageDirective() throws ParseException {
        final Attributes attrs = parseAttributes(true);
        for (int i = 0; i < attrs.getLength(); i++) {
            if ("import".equals(attrs.getQName(i))) {
                // Split by comma
                for (String attr : attrs.getValue(i).split(",")) {
                    // Split by semi-colons
                    for (String token : ("import " + attr).split(";")) {
                        token = token.trim();
                        // If the import statement
                        if (token.startsWith("import")) {
                            token = token.substring("import".length()).trim();
                            // Remove class attribute part and static part in
                            // the case of import static
                            if (token.startsWith("static")) {
                                token = token.substring("static".length());
                                token = token.replaceAll("\\.[^\\.]+$", "");
                            }
                            token = token.replaceAll("\\s", "");
                            if (!token.isEmpty()) {
                                putResultMap(TYPE_JSP_IMPORT, token, attrs);
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * Parses an include directive with the following syntax: IncludeDirective
     * ::= ( S Attribute)*
     */
    private void parseIncludeDirective() throws ParseException {
        parseAttributes();
    }

    /*
     * Attributes ::= (S Attribute)* S?
     */
    private Attributes parseAttributes() throws ParseException {
        return parseAttributes(false);
    }

    private Attributes parseAttributes(final boolean pageDirective)
            throws ParseException {
        final UniqueAttributesImpl attrs = new UniqueAttributesImpl(
                pageDirective);
        this.reader.skipSpaces();
        try {
            while (parseAttribute(attrs)) {
                this.reader.skipSpaces();
            }
        } catch (IllegalArgumentException e) {
            // Duplicate attribute
            throw new ParseException(MessageUtil.getJspErrorMessage(this.reader
                    .mark().toString(), this.getClass().getName()
                    + ".err.notunique"), this.reader.mark().cursor);
        }
        return attrs;
    }

    /*
     * Attribute ::= Name S? Eq S? ( '"<%=' RTAttributeValueDouble | '"'
     * AttributeValueDouble | "'<%=" RTAttributeValueSingle | "'"
     * AttributeValueSingle } Note: JSP and XML spec does not allow while spaces
     * around Eq. It is added to be backward compatible with Tomcat, and with
     * other xml parsers.
     */
    private boolean parseAttribute(final AttributesImpl attrs)
            throws ParseException {
        // Get the qualified name
        final String qName = parseName();
        if (qName == null) {
            return false;
        }

        // Determine prefix and local name components
        String localName = qName;
        final int index = qName.indexOf(':');
        if (index != -1) {
            localName = qName.substring(index + 1);
        }

        this.reader.skipSpaces();
        if (!this.reader.matches("=")) {
            throw new ParseException(MessageUtil.getJspErrorMessage(this.reader
                    .mark().toString(), this.getClass().getName()
                    + ".err.symbolunmatch"), this.reader.mark().cursor);
        }

        this.reader.skipSpaces();
        final char quote = (char) this.reader.nextChar();
        if (quote != '\'' && quote != '"') {
            throw new ParseException(MessageUtil.getJspErrorMessage(this.reader
                    .mark().toString(), this.getClass().getName()
                    + ".err.symbolunmatch"), this.reader.mark().cursor);
        }

        String watchString = "";
        if (this.reader.matches("<%=")) {
            watchString = "%>";
        }
        watchString += quote;

        final String attrValue = parseAttributeValue(watchString);
        attrs.addAttribute("", localName, qName, "CDATA", attrValue);
        return true;
    }

    /*
     * Name ::= (Letter | '_' | ':') (Letter | Digit | '.' | '_' | '-' | ':')*
     */
    private String parseName() {
        char chara = (char) this.reader.peekChar();
        if (Character.isLetter(chara) || chara == '_' || chara == ':') {
            final StringBuilder buf = new StringBuilder();
            buf.append(chara);
            this.reader.nextChar();
            chara = (char) this.reader.peekChar();
            while (Character.isLetter(chara) || Character.isDigit(chara)
                    || chara == '.' || chara == '_' || chara == '-'
                    || chara == ':') {
                buf.append(chara);
                this.reader.nextChar();
                chara = (char) this.reader.peekChar();
            }
            return buf.toString();
        }
        return null;
    }

    /*
     * AttributeValueDouble ::= (QuotedChar - '"')* ('"' | <TRANSLATION_ERROR>)
     * RTAttributeValueDouble ::= ((QuotedChar - '"')* - ((QuotedChar-'"')'%>"')
     * ('%>"' | TRANSLATION_ERROR)
     */
    private String parseAttributeValue(final String watch)
            throws ParseException {
        final Mark start = this.reader.mark();
        final Mark stop = this.reader.skipUntilIgnoreEsc(watch);
        if (stop == null) {
            throw new ParseException(MessageUtil.getJspErrorMessage(
                    start.toString(), this.getClass().getName()
                            + ".err.notproperly", watch), start.cursor);
        }

        String ret = null;
        try {
            final char quote = watch.charAt(watch.length() - 1);

            // If watch is longer than 1 character this is a scripting
            // expression and EL is always ignored
            final boolean isElIgnored = watch.length() > 1;

            ret = AttributeParser.getUnquoted(this.reader.getText(start, stop),
                    quote, isElIgnored, false);
        } catch (IllegalArgumentException e) {
            throw new ParseException(start.toString() + ": " + e.getMessage(),
                    start.cursor);
        }
        if (watch.length() == 1) { // quote
            return ret;
        }

        // Put back delimiter '<%=' and '%>', since they are needed if the
        // attribute does not allow RTexpression.
        return "<%=" + ret + "%>";
    }

    /*
     * Parses a taglib directive with the following syntax: Directive ::= ( S
     * Attribute)*
     */
    private void parseTaglibDirective() throws ParseException {
        final Attributes attrs = parseAttributes();
        putResultMap(TYPE_JSP_TAGLIB, attrs.getValue("uri").trim(), attrs);
    }

    /*
     * Parse for a template text string until '<' or "${" or "#{" is
     * encountered, recognizing escape sequences "<\%", "\$", and "\#".
     */
    private void parseTemplateText() {
        if (!this.reader.hasMoreInput()) {
            return;
        }

        // Output the first character
        int chara = this.reader.nextChar();
        if (chara == '\\') {
            this.reader.pushChar();
        }

        while (this.reader.hasMoreInput()) {
            final int prev = chara;
            chara = this.reader.nextChar();
            if (chara == '<') {
                this.reader.pushChar();
                break;
            } else if ((chara == '$' || chara == '#')) {
                if (!this.reader.hasMoreInput()) {
                    break;
                }
                if (this.reader.nextChar() == '{') {
                    this.reader.pushChar();
                    this.reader.pushChar();
                    break;
                }
                this.reader.pushChar();
                continue;
            } else if (chara == '\\') {
                if (!this.reader.hasMoreInput()) {
                    break;
                }
                final char next = (char) this.reader.peekChar();
                // Looking for \% or \$ or \#
                if ((prev == '<' && next == '%')
                        || ((next == '$' || next == '#'))) {
                    chara = this.reader.nextChar();
                }
            }
        }
    }

    /*
     * StandardAction ::= 'include' StdActionContent | 'forward'
     * StdActionContent | 'invoke' StdActionContent | 'doBody' StdActionContent
     * | 'getProperty' StdActionContent | 'setProperty' StdActionContent |
     * 'useBean' StdActionContent | 'plugin' StdActionContent | 'element'
     * StdActionContent
     */
    private void parseStandardAction() throws ParseException {
        final Mark start = this.reader.mark();

        if (this.reader.matches(INCLUDE_ACTION)) {
            parseInclude();
        } else if (this.reader.matches(FORWARD_ACTION)) {
            parseForward();
        } else if (this.reader.matches(INVOKE_ACTION)) {
            parseInvoke();
        } else if (this.reader.matches(DOBODY_ACTION)) {
            parseDoBody();
        } else if (this.reader.matches(GET_PROPERTY_ACTION)) {
            parseGetProperty();
        } else if (this.reader.matches(SET_PROPERTY_ACTION)) {
            parseSetProperty();
        } else if (this.reader.matches(USE_BEAN_ACTION)) {
            parseUseBean();
        } else if (this.reader.matches(PLUGIN_ACTION)) {
            parsePlugin();
        } else if (this.reader.matches(ELEMENT_ACTION)) {
            parseElement();
        } else if (reader.matches(ATTRIBUTE_ACTION)) {
            throw new ParseException(MessageUtil.getJspErrorMessage(
                    start.toString(), this.getClass().getName()
                            + ".err.attraction"), start.cursor);
        } else if (reader.matches(BODY_ACTION)) {
            throw new ParseException(MessageUtil.getJspErrorMessage(
                    start.toString(), this.getClass().getName()
                            + ".err.bodyaction"), start.cursor);
        } else if (reader.matches(FALLBACK_ACTION)) {
            throw new ParseException(MessageUtil.getJspErrorMessage(
                    start.toString(), this.getClass().getName()
                            + ".err.fallback"), start.cursor);
        } else if (reader.matches(PARAMS_ACTION)) {
            throw new ParseException(MessageUtil.getJspErrorMessage(
                    start.toString(), this.getClass().getName()
                            + ".err.paramsaction"), start.cursor);
        } else if (reader.matches(PARAM_ACTION)) {
            throw new ParseException(MessageUtil.getJspErrorMessage(
                    start.toString(), this.getClass().getName()
                            + ".err.paramaction"), start.cursor);
        } else if (reader.matches(OUTPUT_ACTION)) {
            throw new ParseException(
                    MessageUtil.getJspErrorMessage(start.toString(), this
                            .getClass().getName() + ".err.output"),
                    start.cursor);
        } else {
            throw new ParseException(MessageUtil.getJspErrorMessage(
                    start.toString(), this.getClass().getName()
                            + ".err.invalidaction"), start.cursor);
        }
    }

    /*
     * For UseBean: StdActionContent ::= Attributes OptionalBody
     */
    private void parseUseBean() throws ParseException {
        final Attributes attrs = parseAttributes();
        this.reader.skipSpaces();
        String value = attrs.getValue("class");
        if (value != null && value.trim().length() > 0) {
            putResultMap(TYPE_JSP_USEBEAN, value.trim(), attrs);
        }
        value = attrs.getValue("type");
        if (value != null && value.trim().length() > 0) {
            putResultMap(TYPE_JSP_USEBEAN, value.trim(), attrs);
        }
        parseOptionalBody("jsp:useBean");
    }

    /*
     * # '<' CustomAction CustomActionBody CustomAction ::= TagPrefix ':'
     * CustomActionName TagPrefix ::= Name CustomActionName ::= Name
     * CustomActionBody ::= ( Attributes CustomActionEnd ) | <TRANSLATION_ERROR>
     * Attributes ::= ( S Attribute )* S? CustomActionEnd ::=
     * CustomActionTagDependent | CustomActionJSPContent |
     * CustomActionScriptlessContent CustomActionTagDependent ::=
     * TagDependentOptionalBody CustomActionJSPContent ::= OptionalBody
     * CustomActionScriptlessContent ::= ScriptlessOptionalBody
     */
    private boolean parseCustomTag() throws ParseException {
        if (this.reader.peekChar() != '<') {
            return false;
        }

        // Parse 'CustomAction' production (tag prefix and custom action name)
        this.reader.nextChar(); // skip '<'
        final String tagName = this.reader.parseToken(false);
        if (tagName.indexOf(':') == -1) {
            this.reader.reset(this.mark);
            return false;
        }

        // Parse 'Attributes' production:
        parseAttributes();
        this.reader.skipSpaces();

        // Parse 'CustomActionEnd' production:
        if (this.reader.matches("/>")) {
            return true;
        }
        parseOptionalBody(tagName);
        return true;
    }

    /*
     * Parses a tag directive with the following syntax: PageDirective ::= ( S
     * Attribute)*
     */
    private void parseTagDirective() throws ParseException {
        final Attributes attrs = parseAttributes(true);

        /*
         * A page directive may contain multiple 'import' attributes, each of
         * which consists of a comma-separated list of package names. Store each
         * list with the node, where it is parsed.
         */
        for (int i = 0; i < attrs.getLength(); i++) {
            if ("import".equals(attrs.getQName(i))) {
                // Split by comma
                for (String attr : attrs.getValue(i).split(",")) {
                    // Split by semi-colons
                    for (String token : ("import " + attr).split(";")) {
                        token = token.trim();
                        // If the import statement
                        if (token.startsWith("import")) {
                            token = token.substring("import".length()).trim();
                            // Remove class attribute part and static part in
                            // the case of import static
                            if (token.startsWith("static")) {
                                token = token.substring("static".length());
                                token = token.replaceAll("\\.[^\\.]+$", "");
                            }
                            token = token.replaceAll("\\s", "");
                            if (!token.isEmpty()) {
                                putResultMap(TYPE_JSP_IMPORT, token, attrs);
                            }
                        }
                    }
                }
            }
        }
    }

    /*
     * Parses a attribute directive with the following syntax:
     * AttributeDirective ::= ( S Attribute)*
     */
    private void parseAttributeDirective() throws ParseException {
        parseAttributes();
    }

    /*
     * Parses a variable directive with the following syntax: PageDirective ::=
     * ( S Attribute)*
     */
    private void parseVariableDirective() throws ParseException {
        parseAttributes();
    }

    /*
     * For Include: StdActionContent ::= Attributes ParamBody
     * 
     * ParamBody ::= EmptyBody | ( '>' S? ( '<jsp:attribute' NamedAttributes )?
     * '<jsp:body' (JspBodyParam | <TRANSLATION_ERROR> ) S? ETag ) | ( '>' S?
     * Param* ETag )
     * 
     * EmptyBody ::= '/>' | ( '>' ETag ) | ( '>' S? '<jsp:attribute'
     * NamedAttributes ETag )
     * 
     * JspBodyParam ::= S? '>' Param* '</jsp:body>'
     */
    private void parseInclude() throws ParseException {
        parseAttributes();
        this.reader.skipSpaces();
        parseOptionalBody("jsp:include");
    }

    /*
     * For Forward: StdActionContent ::= Attributes ParamBody
     */
    private void parseForward() throws ParseException {
        parseAttributes();
        this.reader.skipSpaces();
        parseOptionalBody("jsp:forward");
    }

    /**
     * Parse jsp:invoke.<br/>
     * 
     * @throws ParseException
     *             Parse Exception
     */
    private void parseInvoke() throws ParseException {
        parseAttributes();
        this.reader.skipSpaces();
        parseOptionalBody("jsp:invoke");
    }

    /**
     * Parse jsp:doBody.<br/>
     * 
     * @throws ParseException
     *             Parse Exception
     */
    private void parseDoBody() throws ParseException {
        parseAttributes();
        this.reader.skipSpaces();
        parseOptionalBody("jsp:doBody");
    }

    /*
     * For GetProperty: StdActionContent ::= Attributes EmptyBody
     */
    private void parseGetProperty() throws ParseException {
        parseAttributes();
        this.reader.skipSpaces();
        parseOptionalBody("jsp:getProperty");
    }

    /*
     * For SetProperty: StdActionContent ::= Attributes EmptyBody
     */
    private void parseSetProperty() throws ParseException {
        parseAttributes();
        this.reader.skipSpaces();
        parseOptionalBody("jsp:setProperty");
    }

    /*
     * For Plugin: StdActionContent ::= Attributes PluginBody
     * 
     * PluginBody ::= EmptyBody | ( '>' S? ( '<jsp:attribute' NamedAttributes )?
     * '<jsp:body' ( JspBodyPluginTags | <TRANSLATION_ERROR> ) S? ETag ) | ( '>'
     * S? PluginTags ETag )
     * 
     * EmptyBody ::= '/>' | ( '>' ETag ) | ( '>' S? '<jsp:attribute'
     * NamedAttributes ETag )
     */
    private void parsePlugin() throws ParseException {
        parseAttributes();
        this.reader.skipSpaces();
        parseOptionalBody("jsp:plugin");
    }

    /**
     * Parse jsp:element.<br/>
     * 
     * @throws ParseException
     *             Parse Exception
     */
    private void parseElement() throws ParseException {
        parseAttributes();
        this.reader.skipSpaces();
        parseOptionalBody("jsp:element");
    }

    /*
     * Parses OptionalBody, but also reused to parse bodies for plugin and param
     * since the syntax is identical (the only thing that differs substantially
     * is how to process the body, and thus we accept the body type as a
     * parameter). OptionalBody ::= EmptyBody | ActionBody
     * ScriptlessOptionalBody ::= EmptyBody | ScriptlessActionBody
     * TagDependentOptionalBody ::= EmptyBody | TagDependentActionBody EmptyBody
     * ::= '/>' | ( '>' ETag ) | ( '>' S? '<jsp:attribute' NamedAttributes ETag
     * ) ActionBody ::= JspAttributeAndBody | ( '>' Body ETag )
     * ScriptlessActionBody ::= JspAttributeAndBody | ( '>' ScriptlessBody ETag
     * ) TagDependentActionBody ::= JspAttributeAndBody | ( '>' TagDependentBody
     * ETag )
     */
    private void parseOptionalBody(final String tag) throws ParseException {
        if (this.reader.matches("/>")) {
            // EmptyBody
            return;
        }
        if (!reader.matches(">")) {
            throw new ParseException(MessageUtil.getJspErrorMessage(this.reader
                    .mark().toString(), this.getClass().getName()
                    + ".err.unterminated", "<" + tag),
                    this.reader.mark().cursor);
        }
        if (this.reader.matchesETag(tag)) {
            // EmptyBody
            return;
        }
        final Mark end = this.reader.skipUntilETag(tag);
        if (end == null) {
            throw new ParseException(MessageUtil.getJspErrorMessage(this.reader
                    .mark().toString(), this.getClass().getName()
                    + ".err.unterminated", "<" + tag),
                    this.reader.mark().cursor);
        }
    }
}
