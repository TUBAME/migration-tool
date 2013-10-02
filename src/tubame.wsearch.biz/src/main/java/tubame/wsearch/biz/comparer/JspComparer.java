/*
 * JspComparer.java
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

import tubame.wsearch.biz.analyzer.Analyzer;
import tubame.wsearch.biz.analyzer.ParserResult;
import tubame.wsearch.biz.cache.WSearchLibraryCache.LibraryMetaData;
import tubame.wsearch.biz.model.LibraryModel;
import tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * It is a class that provides a comparison process jsp file.<br/>
 * Examine class and taglib that are used in the transplant subject, the
 * existence of the porting library.<br/>
 * Also, to examine whether it is deprecated when present as a class, and
 * outputs.<br/>
 */
public class JspComparer extends JavaComparer {

    /**
     * Map the TLD of each library
     */
    private Map<String, Set<String>> tldListMap;

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @throws IOException
     *             If it fails to generate the comparer
     */
    public JspComparer() throws IOException {
        super();
        this.type = "Jsp";
        this.tldListMap = new HashMap<String, Set<String>>();
        if (this.libraryMap != null) {
            for (String libraryName : this.libraryMap.keySet()) {
                this.tldListMap.put(libraryName,
                        this.libraryMap.get(libraryName).getTldList());
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

        if ("tld".equals(extension)) {
            String fileName = result.getValue();
            if (fileName.contains("/")) {
                fileName = fileName.substring(fileName.lastIndexOf("/") + 1);
            }

            // Search at destination library
            for (String libraryName : this.libraryMap.keySet()) {
                if (!tldListMap.containsKey(libraryName)) {
                    tldListMap.put(libraryName, new HashSet<String>());
                }
                LibraryMetaData library = this.libraryMap.get(libraryName);
                // Skip source library
                if (library.getModel().getTargetType() == LibraryModel.TARGET_TYPE.SRC) {
                    continue;
                }
                if (this.tldListMap.get(libraryName).contains(fileName)) {
                    // No problem
                    return;
                }
            }

            // Search at source library
            Set<String> sourceList = new HashSet<String>();
            for (String libraryName : this.libraryMap.keySet()) {
                LibraryMetaData library = this.libraryMap.get(libraryName);
                // Skip destination library
                if (library.getModel().getTargetType() == LibraryModel.TARGET_TYPE.DEST) {
                    continue;
                }
                if (this.tldListMap.get(libraryName).contains(fileName)) {
                    sourceList.add(libraryName);
                }
            }
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
            return;
        } else if (resultType == Analyzer.TYPE_JSP_TAGLIB) {
            // Warning output taglib analysis result is not the tld file.
            writeResult(
                    null,
                    result.getValue(),
                    new HashSet<String>(),
                    null,
                    result.getFilePath(),
                    result.getLineNumber(),
                    STATUS.WARN,
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".message.tagliburi"));
            return;

        }
        super.compare(result, resultType);
    }
}
