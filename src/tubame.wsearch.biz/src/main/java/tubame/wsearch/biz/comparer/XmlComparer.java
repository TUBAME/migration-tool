/*
 * XmlComparer.java
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
import java.util.Map;
import java.util.Set;

import tubame.wsearch.biz.analyzer.ParserResult;
import tubame.wsearch.biz.cache.WSearchLibraryCache;
import tubame.wsearch.biz.cache.WSearchLibraryCache.LibraryMetaData;
import tubame.wsearch.biz.cache.WSearchLibraryCache.XSD_PARTS;
import tubame.wsearch.biz.model.LibraryModel;
import tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * It is a class that provides a comparison of processing xml file.<br/>
 * <p>
 * Examine schema definition and document type definitions used in
 * transplantation target, whether present in the porting library.<br />
 * Also, check the version of the definition file whether the latest, if
 * present, will be output.<br/>
 */
public class XmlComparer extends AbstractComparer {

    /**
     * Map and their versions of each library XSD
     */
    private Map<String, Map<String, Set<String>>> xsdListMap;
    /**
     * Map and their versions XML namespace for each library
     */
    private Map<String, Map<String, Set<String>>> namespaceListMap;
    /**
     * Map of DTD for each library
     */
    private Map<String, Set<String>> dtdListMap;

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @throws IOException
     *             If it fails to generate the comparer
     */
    public XmlComparer() throws IOException {
        this.type = "Xml";
        this.xsdListMap = new HashMap<String, Map<String, Set<String>>>();
        this.namespaceListMap = new HashMap<String, Map<String, Set<String>>>();
        this.dtdListMap = new HashMap<String, Set<String>>();
        if (this.libraryMap != null) {
            for (String libraryName : this.libraryMap.keySet()) {
                this.xsdListMap.put(libraryName,
                        this.libraryMap.get(libraryName).getXsdList());
                this.namespaceListMap.put(libraryName,
                        this.libraryMap.get(libraryName).getNamespaceList());
                this.dtdListMap.put(libraryName,
                        this.libraryMap.get(libraryName).getDtdList());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void compare(ParserResult result, int resultType) throws IOException {
        int dotIndex = result.getValue().lastIndexOf(".");
        String extension = "";
        if (dotIndex >= 0) {
            extension = result.getValue().substring(dotIndex + 1);
        }

        Set<String> unknownList = new HashSet<String>();
        Set<String> laterVersionList = new HashSet<String>();
        Set<String> dtdExistList = new HashSet<String>();

        // Search at destination library
        for (String libraryName : this.libraryMap.keySet()) {
            if (!dtdListMap.containsKey(libraryName)) {
                dtdListMap.put(libraryName, new HashSet<String>());
            }
            if (!xsdListMap.containsKey(libraryName)) {
                xsdListMap.put(libraryName, new HashMap<String, Set<String>>());
            }
            if (!namespaceListMap.containsKey(libraryName)) {
                namespaceListMap.put(libraryName,
                        new HashMap<String, Set<String>>());
            }
            LibraryMetaData library = this.libraryMap.get(libraryName);
            if (library.getModel().getTargetType() == LibraryModel.TARGET_TYPE.SRC) {
                continue;
            }

            if ("dtd".equals(extension)) {
                String simpleName = result.getValue().substring(
                        result.getValue().lastIndexOf("/") + 1);
                if (this.dtdListMap.get(libraryName).contains(simpleName)) {
                    dtdExistList.add(libraryName);
                }
            } else if ("xsd".equals(extension)) {
                String simpleName = result.getValue().substring(
                        result.getValue().lastIndexOf("/") + 1);
                Map<XSD_PARTS, String> versionMap = WSearchLibraryCache
                        .getVersion(simpleName);
                if (versionMap.get(XSD_PARTS.VERSION) == null) {
                    if (this.xsdListMap.get(libraryName).containsKey(
                            versionMap.get(XSD_PARTS.NAME))) {
                        unknownList.add("dummyName");
                    }
                } else {
                    if (this.xsdListMap.get(libraryName).containsKey(
                            versionMap.get(XSD_PARTS.NAME))) {
                        String version = versionMap.get(XSD_PARTS.VERSION);
                        boolean existsSameVersion = false;
                        boolean existsLaterVersion = false;
                        for (String ver : this.xsdListMap.get(libraryName).get(
                                versionMap.get(XSD_PARTS.NAME))) {
                            int compareResult = version
                                    .compareToIgnoreCase(ver);
                            if (compareResult == 0) {
                                existsSameVersion = true;
                            } else if (compareResult < 0) {
                                existsLaterVersion = true;
                            }
                        }
                        if (existsSameVersion) {
                            if (existsLaterVersion) {
                                laterVersionList.add(libraryName);
                            } else {
                                // No problem
                                return;
                            }
                        }
                    }
                }
            } else {
                Map<XSD_PARTS, String> versionMap = WSearchLibraryCache
                        .getNamespaceVersion(result.getValue());
                if (versionMap.get(XSD_PARTS.VERSION) == null) {
                    if (this.namespaceListMap.get(libraryName).containsKey(
                            versionMap.get(XSD_PARTS.NAME))) {
                        unknownList.add("dummyName");
                    }
                } else {
                    if (this.namespaceListMap.get(libraryName).containsKey(
                            versionMap.get(XSD_PARTS.NAME))) {
                        String version = versionMap.get(XSD_PARTS.VERSION);
                        boolean existsSameVersion = false;
                        boolean existsLaterVersion = false;
                        for (String ver : this.namespaceListMap
                                .get(libraryName).get(
                                        versionMap.get(XSD_PARTS.NAME))) {
                            int compareResult = version
                                    .compareToIgnoreCase(ver);
                            if (compareResult == 0) {
                                existsSameVersion = true;
                            } else if (compareResult < 0) {
                                existsLaterVersion = true;
                            }
                        }
                        if (existsSameVersion) {
                            if (existsLaterVersion) {
                                laterVersionList.add(libraryName);
                            } else {
                                // No problem
                                return;
                            }
                        }
                    }
                }
            }
        }

        // Search at source library
        Set<String> sourceList = new HashSet<String>();
        for (String libraryName : this.libraryMap.keySet()) {
            LibraryMetaData library = this.libraryMap.get(libraryName);
            // Skip porting it will see the original transplant library
            if (library.getModel().getTargetType() == LibraryModel.TARGET_TYPE.DEST) {
                continue;
            }

            if ("dtd".equals(extension)) {
                String simpleName = result.getValue().substring(
                        result.getValue().lastIndexOf("/") + 1);
                if (this.dtdListMap.get(libraryName).contains(simpleName)) {
                    sourceList.add(libraryName);
                }
            } else if ("xsd".equals(extension)) {
                String simpleName = result.getValue().substring(
                        result.getValue().lastIndexOf("/") + 1);
                Map<XSD_PARTS, String> versionMap = WSearchLibraryCache
                        .getVersion(simpleName);
                if (versionMap.get(XSD_PARTS.VERSION) == null) {
                    if (this.xsdListMap.get(libraryName).containsKey(
                            versionMap.get(XSD_PARTS.NAME))) {
                        sourceList.add(libraryName);
                    }
                } else {
                    if (this.xsdListMap.get(libraryName).containsKey(
                            versionMap.get(XSD_PARTS.NAME))) {
                        String version = versionMap.get(XSD_PARTS.VERSION);
                        if (this.xsdListMap.get(libraryName)
                                .get(versionMap.get(XSD_PARTS.NAME))
                                .contains(version)) {
                            sourceList.add(libraryName);
                        }
                    }
                }
            } else {
                Map<XSD_PARTS, String> versionMap = WSearchLibraryCache
                        .getNamespaceVersion(result.getValue());
                if (versionMap.get(XSD_PARTS.VERSION) == null) {
                    if (this.namespaceListMap.get(libraryName).containsKey(
                            versionMap.get(XSD_PARTS.NAME))) {
                        sourceList.add(libraryName);
                    }
                } else {
                    if (this.namespaceListMap.get(libraryName).containsKey(
                            versionMap.get(XSD_PARTS.NAME))) {
                        String version = versionMap.get(XSD_PARTS.VERSION);
                        if (this.namespaceListMap.get(libraryName)
                                .get(versionMap.get(XSD_PARTS.NAME))
                                .contains(version)) {
                            sourceList.add(libraryName);
                        }
                    }
                }
            }
        }

        if (unknownList.size() > 0) {
            writeResult(
                    null,
                    result.getValue(),
                    sourceList,
                    null,
                    result.getFilePath(),
                    result.getLineNumber(),
                    STATUS.WARN,
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".message.unknownversion"));
        } else if (laterVersionList.size() > 0) {
            writeResult(
                    null,
                    result.getValue(),
                    sourceList,
                    laterVersionList,
                    result.getFilePath(),
                    result.getLineNumber(),
                    STATUS.WARN,
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".message.oldversion"));
        } else if (dtdExistList.size() > 0) {
            writeResult(
                    null,
                    result.getValue(),
                    sourceList,
                    dtdExistList,
                    result.getFilePath(),
                    result.getLineNumber(),
                    STATUS.WARN,
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".message.dtdexists"));
        } else {
            writeResult(
                    null,
                    result.getValue(),
                    sourceList,
                    null,
                    result.getFilePath(),
                    result.getLineNumber(),
                    STATUS.ERROR,
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".message.notexists"));
        }
    }
}
