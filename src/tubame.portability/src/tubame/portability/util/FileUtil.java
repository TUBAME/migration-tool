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
package tubame.portability.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.util.resource.ApplicationPropertyUtil;
import tubame.portability.util.resource.MessageUtil;

/**
 * Utility classes for the File (Directory) operation.<br/>
 * Acquisition of file separator or File system-specific attributes.<br/>
 */
public class FileUtil {
    /**
     * Logger
     */
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
     * File separator
     */
    public static final String FILE_SEPARATOR = File.separator;

    /**
     * Check target folder are present.<br/>
     * 
     * @param folderPath
     *            Folder path from the workspace root
     * @return true:Exist false:Not exist
     */
    public static boolean folderExists(String folderPath) {
        if (!FileUtil.getFile(folderPath).isDirectory()
                || !FileUtil.getFile(folderPath).exists()) {
            return false;
        }
        return true;
    }

    /**
     * Being checked if the file exists.<br/>
     * 
     * @param filePath
     *            File path from the workspace root
     * @return boolean True if if present,it returns false if not exist
     */
    public static boolean fileExists(String filePath) {
        return FileUtil.getFile(filePath).exists();
    }

    /**
     * Generate File.<br/>
     * 
     * @param filePath
     *            Files from the workspace root path (directory)
     * @return File
     */
    public static File getFile(String filePath) {
        return new File(PluginUtil.getFileFullPath(filePath));
    }

    /**
     * Convert to relative path.<br/>
     * 
     * @param top
     *            Directory
     * @param filePath
     *            File Path
     * @return Relative path
     */
    public static String getRelativePath(String top, String filePath) {
        return filePath.replace(top, StringUtil.EMPTY);
    }

    /**
     * Get to see if you are separated by a "\" or "/" string.<br/>
     * 
     * @param target
     *            String
     * @return true:separated false:Not separated
     */
    public static boolean isSeparate(String target) {
        if (target.contains(StringUtil.SLASH)) {
            return true;
        }
        if (target.contains(FileUtil.FILE_SEPARATOR)) {
            return true;
        }
        return false;
    }

    /**
     * The decision whether the file is created possible.<br/>
     * 
     * @param filePath
     *            Relative path of the file
     * @return false:Because the wrong characters in the file or directory, NG
     *         true:The file can be created if
     */
    public static boolean isInputFileNormal(String filePath) {
        File file = FileUtil.getFile(filePath);
        String fileName = FileUtil.getFileName(filePath);
        String directoryPath = filePath.replace(fileName, StringUtil.EMPTY);
        // The decision can be created directory does not exist
        if (!isInputDirectoryNormal(directoryPath)) {
            return false;
        }
        // File name If the decision 1 or or more characters with the exception
        // of the delimiter and extension
        if (fileName.substring(0, fileName.lastIndexOf(StringUtil.PERIOD))
                .length() <= 1) {
            return false;
        }
        // The file name is correct judgment or name
        try {
            if (file.createNewFile()) {
                // Discard
                if (!file.delete()) {
                    LOGGER.warn(String.format(
                            MessageUtil.LOG_WARN_FILE_DELETE_FAIL, filePath));
                }
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Get the "+ file name (\ or /) delimiter" from the relative path.<br/>
     * 
     * @param path
     *            Relative path of the file
     * @return File name
     */
    public static String getFileName(String path) {
        // The return string / since appeared in last
        if (FileUtil.isEndSeparatedBySlash(path)) {
            return path.substring(path.lastIndexOf(StringUtil.SLASH),
                    path.length());
        }
        // The return of the string \ or after the last occurrence
        return path.substring(path.lastIndexOf(FileUtil.FILE_SEPARATOR),
                path.length());
    }

    /**
     * Acquisition delimiter at the end of the target string is whether it is /.<br/>
     * 
     * @param str
     *            Target string
     * @return false:The last one is \ true:The last one is /
     */
    private static boolean isEndSeparatedBySlash(String str) {
        int lastSlash = 0;
        int lastBackSlash = 0;
        if (!(-1 == str.lastIndexOf(StringUtil.SLASH))) {
            lastSlash = str.lastIndexOf(StringUtil.SLASH);
        }
        if (!(-1 == str.lastIndexOf(FileUtil.FILE_SEPARATOR))) {
            lastBackSlash = str.lastIndexOf(FileUtil.FILE_SEPARATOR);
        }
        if (lastSlash > lastBackSlash) {
            return true;
        }
        return false;
    }

    /**
     * The decision to determine whether or not the directory can be created.<br/>
     * 
     * @param directoryPath
     *            Absolute path of the directory
     * @return false:Since the illegal character is in the directory, NG
     *         true:Files can be created, or if the file exists
     */
    public static boolean isInputDirectoryNormal(String directoryPath) {
        File directory = FileUtil.getFile(directoryPath);
        // The decision can be created directory does not exist
        if (!directory.exists() && !directory.mkdirs()) {
            return false;
        }
        return true;

    }

    /**
     * Get the extension.<br/>
     * 
     * @param fileName
     *            File name
     * @return Extension
     */
    private static String getSuffix(String fileName) {
        if (fileName == null) {
            return null;
        }
        int point = fileName.lastIndexOf(StringUtil.PERIOD);
        if (point != -1) {
            return fileName.substring(point + 1);
        }
        return fileName;
    }

    /**
     * Get the extension of JBM.<br/>
     * 
     * @return Extension of JBM (jbm)
     */
    public static String getJbmSuffix() {
        return FileUtil.getSuffix(ApplicationPropertyUtil.DEFAULT_JBM_NAME);
    }

    /**
     * Get the extension of XML.<br/>
     * 
     * @return Extension of XML (xml)
     */
    public static String getXmlSuffix() {
        return FileUtil
                .getSuffix(ApplicationPropertyUtil.DEFAULT_KNOWHOW_FILE_NAME);
    }
    public static void copyFile(File in, File out) throws IOException {
        FileInputStream fis  = new FileInputStream(in);
        FileOutputStream fos = new FileOutputStream(out);
        try {
            byte[] buf = new byte[1024];
            int i = 0;
            while ((i = fis.read(buf)) != -1) {
                fos.write(buf, 0, i);
            }
        } 
        catch (IOException e) {
            throw e;
        }
        finally {
            if (fis != null) fis.close();
            if (fos != null) fos.close();
        }
      }
    
	public static  boolean isKnowHowXml(File file) {
		byte[] buff = new byte[256];
		BufferedInputStream bufferedInputStream = null;
		try {
			bufferedInputStream = new BufferedInputStream(new FileInputStream(file));
			if (bufferedInputStream != null) {
				bufferedInputStream.read(buff);
				if (new String(buff)
						.contains("<PortabilityKnowhow xmlns=\"http://generated.model.biz.knowhow.tubame/knowhow\" xmlns:ns2=\"http://docbook.org/ns/docbook\">")) {
					return true;
				}
			}
		} catch (Exception e) {
			;
		} finally {
			if (bufferedInputStream != null) {
				try {
					bufferedInputStream.close();
				} catch (IOException e) {
					;
				}
			}
		}
		return false;
	}
}
