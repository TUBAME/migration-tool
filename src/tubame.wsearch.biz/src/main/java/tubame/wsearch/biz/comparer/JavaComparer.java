/*
 * JavaComparer.java
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
package tubame.wsearch.biz.comparer;

import java.io.IOException;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Locale;
import java.util.Map;
import java.util.Set;

import tubame.wsearch.biz.analyzer.ParserResult;
import tubame.wsearch.biz.cache.WSearchLibraryCache.LibraryMetaData;
import tubame.wsearch.biz.model.LibraryModel;
import tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * It is a class that provides a comparison process of java files.<br/>
 * Examine class used in the transplant subject, whether present in the porting
 * library.<br />
 * Also, to examine whether it is deprecated, if present, is output.<br />
 */
public class JavaComparer extends AbstractComparer {

    /**
     * Map test the existence of packages of each library
     */
    protected Map<String, Set<String>> existPackageMap;
    /**
     * Map of absence confirmed package of each library
     */
    protected Map<String, Set<String>> noExistPackageMap;

    /**
     * Map of the package of each library
     */
    protected Map<String, Set<String>> packageMap;

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @throws IOException
     *             If it fails to generate the comparer
     */
    public JavaComparer() throws IOException {
        super();
        this.type = "Java";
        this.existPackageMap = new HashMap<String, Set<String>>();
        this.noExistPackageMap = new HashMap<String, Set<String>>();
        this.packageMap = new HashMap<String, Set<String>>();
        if (this.libraryMap != null) {
            for (String libraryName : this.libraryMap.keySet()) {
                this.existPackageMap.put(libraryName, new HashSet<String>());
                this.noExistPackageMap.put(libraryName, new HashSet<String>());
                this.packageMap.put(libraryName,
                        this.libraryMap.get(libraryName).getPackages());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compare(ParserResult result, int resultType) throws IOException {
        String packageName = getPackageName(result.getValue());
        String className;
        if (packageName == null) {
            if (result.getValue().endsWith("*")) {
                packageName = result.getValue().substring(0,
                        (result.getValue().length() - 2));
                className = "*";
            } else {
                className = result.getValue();
            }
        } else {
            className = result.getValue().substring(packageName.length() + 1);
        }

        // If the package name can not be acquired
        if (packageName == null) {
            writeResult(
                    null,
                    className,
                    new HashSet<String>(),
                    new HashSet<String>(),
                    result.getFilePath(),
                    result.getLineNumber(),
                    STATUS.WARN,
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".message.nopackage"));
            return;
        }

        if ((className != null) && className.endsWith(".*")) {
            className = className.substring(0, (className.length() - 2));
        }

        Set<String> containsList = new HashSet<String>();
        Set<String> deprecatedList = new HashSet<String>();

        // Search at destination library.
        for (String libraryName : this.libraryMap.keySet()) {
            if (!noExistPackageMap.containsKey(libraryName)) {
                noExistPackageMap.put(libraryName, new HashSet<String>());
            }
            if (!existPackageMap.containsKey(libraryName)) {
                existPackageMap.put(libraryName, new HashSet<String>());
            }
            if (!packageMap.containsKey(libraryName)) {
                packageMap.put(libraryName, new HashSet<String>());
            }
            LibraryMetaData library = this.libraryMap.get(libraryName);

            // Skip source library
            if (library.getModel().getTargetType() == LibraryModel.TARGET_TYPE.SRC) {
                continue;
            }

            if (noExistPackageMap.get(libraryName).contains(packageName)) {
                continue;
            }

            if (existPackageMap.get(libraryName).contains(packageName)) {
                if (className == null) {
                    containsList.add(libraryName);
                } else if (library.getClasses(packageName).contains(className)) {
                    containsList.add(libraryName);
                    if (library.getDeprecated(packageName).contains(className)) {
                        deprecatedList.add(libraryName);
                    }
                }
            } else {
                if (packageMap.get(libraryName).contains(packageName)) {
                    if (className == null) {
                        containsList.add(libraryName);
                    } else if (library.getClasses(packageName).contains(
                            className)) {
                        containsList.add(libraryName);
                        if (library.getDeprecated(packageName).contains(
                                className)) {
                            deprecatedList.add(libraryName);
                        }
                    }
                    existPackageMap.get(libraryName).add(packageName);
                } else {
                    noExistPackageMap.get(libraryName).add(packageName);
                }
            }
        }

        if ((containsList.size() == 0) || (deprecatedList.size() > 0)) {
            // Search at source library
            Set<String> sourceList = new HashSet<String>();
            for (String libraryName : this.libraryMap.keySet()) {
                LibraryMetaData library = this.libraryMap.get(libraryName);

                // Skip destination library
                if (library.getModel().getTargetType() == LibraryModel.TARGET_TYPE.DEST) {
                    continue;
                }

                if (noExistPackageMap.get(libraryName).contains(packageName)) {
                    continue;
                }

                if (existPackageMap.get(libraryName).contains(packageName)) {
                    if ((className == null)
                            || library.getClasses(packageName).contains(
                                    className)) {
                        sourceList.add(libraryName);
                    }
                } else {
                    if (packageMap.get(libraryName).contains(packageName)) {
                        if ((className == null)
                                || library.getClasses(packageName).contains(
                                        className)) {
                            sourceList.add(libraryName);
                        }
                        existPackageMap.get(libraryName).add(packageName);
                    } else {
                        noExistPackageMap.get(libraryName).add(packageName);
                    }
                }
            }

            if (containsList.size() == 0) {
                writeResult(
                        packageName,
                        className,
                        sourceList,
                        containsList,
                        result.getFilePath(),
                        result.getLineNumber(),
                        STATUS.ERROR,
                        MessageUtil.getResourceString(this.getClass().getName()
                                + ".message.notexists"));
            } else {
                writeResult(
                        packageName,
                        className,
                        sourceList,
                        containsList,
                        result.getFilePath(),
                        result.getLineNumber(),
                        STATUS.WARN,
                        MessageUtil.getResourceString(this.getClass().getName()
                                + ".message.deprecated"), deprecatedList);
            }
        }
    }

    /**
     * From the class string of nested, and extract the package name.<br/>
     * Return null if the package name is not found.<br/>
     * For example<br />
     * 
     * <pre>
     * Case 1
     * input: sample.AClass.BClass.CClass.TEST
     * return: sample
     * 
     * Case 2
     * input: sample.a.b.c.d
     * return: null
     * 
     * Case 3
     * input: sample.a.b.c.d.TEST
     * return: sample.a.b.c.d
     * </pre>
     * 
     * @param className
     *            Class string
     * @return Package Name
     * @see #isLocalClassName(String)
     */
    private String getPackageName(final String className) {
        String old = null;
        String current = className;
        if (className != null && !className.endsWith(".")) {
            for (int i = current.lastIndexOf('.'); i > 0; i = current
                    .lastIndexOf('.')) {
                old = current;
                current = current.substring(0, i);
                // Can not be detected properly in the case of non-package names
                // in all lowercase.
                if (current.equals(current.toLowerCase(Locale.getDefault()))
                        && isLocalClassName(old.substring(i + 1, old.length()))) {
                    return current;
                }
            }
        }
        return null;
    }

    /**
     * Check the specified string to see if it matches the rules of the class
     * name.<br/>
     * check on the assumption that does not include the package name.<br/>
     * 
     * @param className
     *            Class string
     * @return True if the specified string matches the class name
     */
    private boolean isLocalClassName(final String className) {
        return className.length() > 2
                && className.matches("[A-Z][A-Z,a-z,0-9,_,$]*");
    }
}
