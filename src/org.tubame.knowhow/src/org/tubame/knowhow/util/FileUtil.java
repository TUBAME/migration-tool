/*
 * FileUtil.java
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
package org.tubame.knowhow.util;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.net.MalformedURLException;
import java.net.URL;

import org.tubame.common.util.CmnFileUtil;
import org.tubame.common.util.CmnStringUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.exception.JbmException;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

/**
 * Utility classes for the File (Directory) operation.<br/>
 * Conducted acquisition of file separator or File system-specific attributes.<br/>
 */
public final class FileUtil {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(FileUtil.class);

    /**
     * Default constructor.<br/>
     * Disable the instance generation from other class.<br/>
     * 
     */
    private FileUtil() {
        // no operation
    }

    /**
     * Create a Work directory of know-how detail editor for editing.<br/>
     * 
     * @param container
     *            Container
     * @param target
     *            File Path
     * @return true:Directory creation false:Not directory
     * @throws IOException
     *             IO exception
     */
    public static boolean createWorkDir(IContainer container, String target)
            throws IOException {
        if (!CmnStringUtil.isEmpty(target)) {
            Path path = new Path(target);
            File targetFile = new File(container.getLocationURI().getPath()
                    + CmnFileUtil.FILE_SEPARATOR + path.toOSString());
            if (targetFile.exists()) {
                return false;
            } else {
                return targetFile.mkdir();
            }
        }
        return false;
    }

    /**
     * Generate a template file information.<br/>
     * 
     * @param tmplateFilePath
     *            Template file path
     * @param indentationSpace
     *            Indent space
     * @return Template string
     * @throws JbmException
     *             Jbm exception
     */
    public static String createFileContentStr(String tmplateFilePath,
            String indentationSpace) throws JbmException {
        StringBuilder templateStr = new StringBuilder();
        File file = new File(tmplateFilePath);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String str = null;
            int insertIndentation = 0;
            while ((str = br.readLine()) != null) {
                if (insertIndentation != 0) {
                    templateStr.append(CmnStringUtil.newLine(indentationSpace));
                }
                templateStr.append(str);
                insertIndentation++;
            }
        } catch (FileNotFoundException e) {
            throw new JbmException(e.getMessage(), e);
        } catch (IOException e) {
            throw new JbmException(e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return templateStr.toString();
    }

    /**
     * Generate a template file information that was replaced by the string.<br/>
     * 
     * @param tmplateFilePath
     *            Template file path
     * @param indentationSpace
     *            Indent space
     * @param replacementStr
     *            Replacement string
     * @return Template string
     * @throws JbmException
     *             Jbm exception
     */
    public static String createFileContentReplacementStr(
            String tmplateFilePath, String indentationSpace,
            String replacementStr) throws JbmException {
        StringBuilder templateStr = new StringBuilder();
        File file = new File(tmplateFilePath);
        BufferedReader br = null;
        try {
            br = new BufferedReader(new FileReader(file));
            String str = null;
            int insertIndentation = 0;
            while ((str = br.readLine()) != null) {
                if (insertIndentation != 0) {
                    templateStr.append(CmnStringUtil.newLine(indentationSpace));
                }
                if (str.equals("[ReplacementStringElement]")) {
                    String reStr = str.replace(str, replacementStr);
                    templateStr.append(reStr);
                } else {
                    templateStr.append(str);
                }
                insertIndentation++;
            }
        } catch (FileNotFoundException e) {
            throw new JbmException(e.getMessage(), e);
        } catch (IOException e) {
            throw new JbmException(e.getMessage(), e);
        } finally {
            if (br != null) {
                try {
                    br.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return templateStr.toString();
    }

    /**
     * Converted into the URL format file path and returns a string.<br/>
     * 
     * @param filePath
     *            File Path
     * @return URL
     */
    public static URL getURL(String filePath) {
        File file = new File(filePath);
        try {
            return file.toURI().toURL();
        } catch (MalformedURLException e) {
            JbmException.outputExceptionLog(LOGGER, e, MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.FAIL_GET_URL));
        }
        return null;
    }
}
