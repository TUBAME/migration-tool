/*
 * AbstractComparer.java
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

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.wsearch.biz.analyzer.ParserResult;
import tubame.wsearch.biz.cache.CacheBase;
import tubame.wsearch.biz.cache.WSearchCacheManager;
import tubame.wsearch.biz.cache.WSearchLibraryCache;
import tubame.wsearch.biz.cache.WSearchLibraryCache.LibraryMetaData;
import tubame.wsearch.biz.util.resource.MessageUtil;
import tubame.wsearch.biz.util.resource.ResourceUtil;

/**
 * It is the base class for classes that perform the comparison process of
 * porting libraries transplant target.<br/>
 * You can use it depending on the file type of comparison, you can create an
 * implementation of this class.<br/>
 */
public abstract class AbstractComparer {

    /**
     * Result output stream
     */
    private static OutputStream out;

    /**
     * The type that defines the status result.<br/>
     */
    public enum STATUS {
        WARN("warn"), ERROR("error");

        /**
         * String representation of the result status
         */
        private String value;

        /**
         * Constructor.<br/>
         * No operation.<br/>
         * 
         * @param value
         *            String representation of the result status
         */
        private STATUS(String value) {
            this.value = value;
        }

        /**
         * Get a string representation of the result status.<br/>
         * 
         * @return String representation of the result status
         */
        public String getValue() {
            return this.value;
        }
    };

    /**
     * Type of comparer(Java or jsp or XML)
     */
    protected String type;

    /**
     * Map of the library
     */
    protected Map<String, LibraryMetaData> libraryMap;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractComparer.class);

    /**
     * Constructor.<br/>
     * No operation.<br/>
     */
    protected AbstractComparer() {
        WSearchLibraryCache cache = (WSearchLibraryCache) WSearchCacheManager
                .getInstance().getCache(CacheBase.TYPE.LIBRARY);
        if (cache == null) {
            this.libraryMap = null;
        } else {
            this.libraryMap = cache.getLibraryMap();
        }
    }

    /**
     * Open the result output stream.<br/>
     * 
     * @param fileName
     *            File name to the output target
     * @throws IOException
     *             If it fails to open the stream
     */
    public synchronized static void openOutputStream(String fileName)
            throws IOException {
        LOGGER.debug(MessageUtil.getResourceString("debug.msg.write.file")
                + fileName);
        out = new BufferedOutputStream(new FileOutputStream(fileName));
    }

    /**
     * Close the result output stream.<br/>
     * 
     * @throws IOException
     *             If it fails to open the stream
     */
    public synchronized static void closeOutputStream() throws IOException {
        if (out != null) {
            out.flush();
            out.close();
            out = null;
            LOGGER.debug(MessageUtil.getResourceString(AbstractComparer.class
                    .getName() + ".debug.close.stream"));
        }
    }

    /**
     * Compares the porting library information and analysis results of
     * transplantation target source.<br/>
     * By comparing the contents of the analysis results which are passed as an
     * argument, the porting library information that this class holds and
     * writes the results to the results output stream if the result is an error
     * or warning.<br/>
     * 
     * @param result
     *            Analysis results of transplantation target source
     * @param resultType
     *            Type of analysis results
     * @throws IOException
     *             If it fails to write to the output stream results
     * @see tubame.wsearch.biz.analyzer.Analyzer
     */
    public abstract void compare(ParserResult result, int resultType)
            throws IOException;

    /**
     * Write the error information to the result output stream.<br/>
     * 
     * @param filePath
     *            File path Migrated source that caused the error
     * @param detail
     *            Details of the error
     * @throws IOException
     *             If it fails to write to the output stream results
     */
    public void writeErrorResult(String filePath, String detail)
            throws IOException {
        if (out == null) {
            return;
        }

        synchronized (out) {
            StringBuilder str = new StringBuilder("\"").append(STATUS.WARN
                    .getValue());
            str.append("\",\"").append(this.type).append("\",\"\",\"(unknown)");
            str.append("\",-,\"\",\"").append(filePath).append("\",\"\",\"");
            str.append(detail).append("\",\"\"\n");
            out.write(str.toString().getBytes(
                    ResourceUtil
                            .getResourceString("default.csv.character.code")));
        }
    }

    /**
     * Write the comparison result to the result output stream.<br/>
     * 
     * @param packageName
     *            Package Name
     * @param className
     *            Class name
     * @param sourceList
     *            List of porting the original library name
     * @param destList
     *            List of porting library name
     * @param filePath
     *            File path of the analyzed source
     * @param line
     *            Line number of the corresponding code in the analyzed source
     * @param status
     *            Comparison result status
     * @param detail
     *            More info
     * @throws IOException
     *             If it fails to write to the output stream results
     */
    protected void writeResult(String packageName, String className,
            Set<String> sourceList, Set<String> destList, String filePath,
            int line, STATUS status, String detail) throws IOException {
        this.writeResult(packageName, className, sourceList, destList,
                filePath, line, status, detail, null);
    }

    /**
     * Write the comparison result to the result output stream.<br/>
     * 
     * @param packageName
     *            Package Name
     * @param className
     *            Class name
     * @param sourceList
     *            List of porting the original library name
     * @param destList
     *            List of porting library name
     * @param filePath
     *            File path of the analyzed source
     * @param line
     *            Line number of the corresponding code in the analyzed source
     * @param status
     *            Comparison result status
     * @param detail
     *            More info
     * @param note
     *            Note information
     * @throws IOException
     *             If it fails to write to the output stream results
     */
    protected void writeResult(String packageName, String className,
            Set<String> sourceSet, Set<String> destSet, String filePath,
            int line, STATUS status, String detail, Object note)
            throws IOException {

        if (out == null) {
            return;
        }

        synchronized (out) {
            StringBuilder str = new StringBuilder("\"").append(
                    status.getValue()).append("\",\"");
            str.append(this.type).append("\",\"");
            if (packageName != null) {
                str.append(packageName);
            }
            str.append("\",\"");
            // Porting the original library
            List<String> sourceList = new ArrayList<String>(sourceSet.size());
            sourceList.addAll(sourceSet);
            Collections.sort(sourceList);
            for (String source : sourceList) {
                str.append(source).append(",");
            }
            if (sourceList.size() > 0) {
                str.delete((str.length() - 1), str.length());
            } else {
                str.append("(unknown)");
            }
            str.append("\",\"");
            // Porting library
            if (destSet != null) {
                List<String> destList = new ArrayList<String>(destSet.size());
                destList.addAll(destSet);
                if (destList.size() != 0) {
                    Collections.sort(destList);
                    for (String destination : destList) {
                        str.append(destination).append(",");
                    }
                    if (destList.size() > 0) {
                        str.delete((str.length() - 1), str.length());
                    }
                } else {
                    str.append("-");
                }
            } else {
                str.append("-");
            }
            str.append("\",\"");
            if (className != null) {
                str.append(className);
            }
            str.append("\",\"");
            str.append(filePath).append("\",\"");
            str.append(line).append("\",\"");
            str.append(detail).append("\",\"");
            if (note instanceof Set) {
                for (Object value : (Set<?>) note) {
                    str.append(value).append(",");
                }
                if (((Set<?>) note).size() > 0) {
                    str.delete((str.length() - 1), str.length());
                }
            } else if (note instanceof String) {
                str.append(note);
            }
            str.append("\"\n");
            out.write(str.toString().getBytes(
                    ResourceUtil
                            .getResourceString("default.csv.character.code")));
        }
    }
}
