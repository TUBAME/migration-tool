/*
 * CmnFileUtil.java
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
package tubame.common.util;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.nio.channels.FileChannel;

/**
 * This is a utility class that performs the File (Directory) operation.<br/>
 * Make the acquisition of file separator, File system-specific attributes.<br/>
 */
public class CmnFileUtil {

    /**
     * File separator
     */
    public static final String FILE_SEPARATOR = File.separator;

    /**
     * Default constructor.<br/>
     * Prohibits the instantiation from other classes.<br/>
     * 
     */
    private CmnFileUtil() {
        // no operation
    }

    /**
     * Make sure the target folder are present.<br/>
     * 
     * @param folderPath
     *            Absolute path of the target folder
     * @return boolean true:Present false:Not exist
     */
    public static boolean folderExists(String folderPath) {
        if (!CmnFileUtil.getFile(folderPath).isDirectory()
                || !CmnFileUtil.getFile(folderPath).exists()) {
            return false;
        }
        return true;
    }

    /**
     * Make sure the target file exists.<br/>
     * 
     * @param filePath
     *            Absolute path of the target file
     * @return boolean true:Not exist false:Present
     */
    public static boolean fileExists(String filePath) {
        return CmnFileUtil.getFile(filePath).exists();
    }

    /**
     * generate a File.<br/>
     * 
     * @param filePath
     *            Absolute path of the target file (directory)
     * @return File
     */
    public static File getFile(String filePath) {
        return new File(filePath);
    }

    /**
     * Delete the file or directory.<br/>
     * 
     * @param filePath
     *            Absolute path of the target file (directory)
     * @return boolean true:Delete success false:Other
     */
    public static boolean delete(String filePath) {
        return CmnFileUtil.getFile(filePath).delete();
    }

    /**
     * Convert to a relative path.<br/>
     * 
     * @param top
     *            Directory
     * @param filePath
     *            File Path
     * @return String Relative path
     */
    public static String getRelativePath(String top, String filePath) {
        return filePath.replace(top, CmnStringUtil.EMPTY);
    }

    /**
     * Get to see if it is separated by a "\" or "/" string.<br/>
     * 
     * @param target
     *            String
     * @return true:sSeparated false:Not separated
     */
    public static boolean isSeparate(String target) {
        if (target.contains(CmnStringUtil.SLASH)) {
            return true;
        }
        if (target.contains(CmnFileUtil.FILE_SEPARATOR)) {
            return true;
        }
        return false;
    }

    /**
     * Determine whether the file is created possible.<br/>
     * 
     * @param filePath
     *            Absolute path of the target file
     * @return false:Because the wrong characters in the file or directory, NG
     *         true:The file can be created if
     */
    public static boolean isInputFileNormal(String filePath) {
        File file = CmnFileUtil.getFile(filePath);
        String fileName = CmnFileUtil.getFileName(filePath);
        String directoryPath = filePath.replace(fileName, CmnStringUtil.EMPTY);
        // The decision can be created directory does not exist
        if (!isInputDirectoryNormal(directoryPath)) {
            return false;
        }
        // File name is determined whether one or more characters with the
        // exception of the delimiter and extension
        if (fileName.substring(0, fileName.lastIndexOf(CmnStringUtil.PERIOD))
                .length() <= 1) {
            return false;
        }
        // The file name is correct judgment or name
        try {
            if (file.createNewFile()) {
                // Discard
                return file.delete();
            }
            return true;
        } catch (IOException e) {
            return false;
        }
    }

    /**
     * Get the file name.<br/>
     * Get the file name from the absolute path.<br/>
     * 
     * @param path
     *            Absolute path of the target file
     * @return File name
     */
    public static String getFileName(String path) {
        // The return string / since appeared in last
        if (CmnFileUtil.isEndSeparatedBySlash(path)) {
            return path.substring(path.lastIndexOf(CmnStringUtil.SLASH) + 1,
                    path.length());
        }
        // The return of the string \ or after the last occurrence
        return path.substring(path.lastIndexOf(CmnFileUtil.FILE_SEPARATOR) + 1,
                path.length());
    }

    /**
     * Get delimiter at the end of the string whether it is "/".<br/>
     * Get delimiter at the end of the target string is whether it is "/".<br/>
     * 
     * @param str
     *            Target string
     * @return false:The last one is "\" true:The last one is "/"
     */
    private static boolean isEndSeparatedBySlash(String str) {
        int lastSlash = 0;
        int lastBackSlash = 0;
        if (!(-1 == str.lastIndexOf(CmnStringUtil.SLASH))) {
            lastSlash = str.lastIndexOf(CmnStringUtil.SLASH);
        }
        if (!(-1 == str.lastIndexOf(CmnFileUtil.FILE_SEPARATOR))) {
            lastBackSlash = str.lastIndexOf(CmnFileUtil.FILE_SEPARATOR);
        }
        if (lastSlash > lastBackSlash) {
            return true;
        }
        return false;
    }

    /**
     * Determine if any directories can be created.<br/>
     * 
     * @param directoryPath
     *            Absolute path of the directory
     * @return false:Since the illegal character is in the directory, NG true:
     *         files can be created, or if the file exists
     */
    public static boolean isInputDirectoryNormal(String directoryPath) {
        File directory = CmnFileUtil.getFile(directoryPath);
        // The decision can be created directory does not exist
        if (!directory.exists() && !directory.mkdirs()) {
            return false;
        }
        return true;
    }

    /**
     * Copy the file.<br/>
     * 
     * @param fromPath
     *            Source file path
     * @param toPath
     *            Destination file path
     * @return true: Copy success false: Copy failure
     */
    public static boolean copyFile(String fromPath, String toPath) {
        FileChannel fromChannel = null;
        FileChannel toChannel = null;
        try {
            fromChannel = new FileInputStream(fromPath).getChannel();
            toChannel = new FileOutputStream(toPath).getChannel();
            fromChannel.transferTo(0, fromChannel.size(), toChannel);
        } catch (IOException e) {
            return false;
        } finally {
            if (fromChannel != null) {
                try {
                    fromChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
            if (toChannel != null) {
                try {
                    toChannel.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }

    /**
     * Get an InputStream object.<br/>
     * Get an InputStream object from a specified file path.<br/>
     * 
     * @param filePath
     *            File Path
     * @return InputStream InputStream object
     */
    public static InputStream getInputStream(String filePath) {
        if (CmnFileUtil.fileExists(filePath)) {
            try {
                File file = new File(filePath);
                InputStream inputStream = new FileInputStream(file);
                return inputStream;
            } catch (FileNotFoundException e) {
                // Continue the process
            }
        }
        return null;
    }

    /**
     * Check for file size.<br/>
     * Return false if there is no file size.<br/>
     * 
     * @param filePath
     *            File Path
     * @return true:File size exists false:No file size
     */
    public static boolean isFileSize(String filePath) {
        File file = new File(filePath);
        if (file.length() > 0) {
            return true;
        }
        return false;
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
        int point = fileName.lastIndexOf(CmnStringUtil.PERIOD);
        if (point != -1) {
            return fileName.substring(point + 1);
        }
        return fileName;
    }

    /**
     * Make the decision of the extension of the file.<br/>
     * Return true if the file extension is normal.<br/>
     * 
     * @param fileName
     *            File name
     * @param extension
     *            Extension
     * @return true:Normal false:Abnormality
     */
    public static boolean isExtension(String fileName, String extension) {
        String targetExtension = CmnFileUtil.getSuffix(fileName);
        if (targetExtension.equals(extension.toLowerCase())) {
            return true;
        } else if (targetExtension.equals(extension.toUpperCase())) {
            return true;
        }
        return false;
    }

    /**
     * Change the extension of the file name to a specified extension.<br/>
     * 
     * @param fileName
     *            File name
     * @param extension
     *            Extension
     * @return After changing the file name
     */
    public static String extensionChange(String fileName, String extension) {
        String beforeExtension = CmnFileUtil.getSuffix(fileName);
        return fileName.replaceFirst(beforeExtension, extension);
    }

    /**
     * Get the path to the file name.<br/>
     * 
     * @param filePath
     *            File Path
     * @param fileName
     *            File name
     * @return Path name
     */
    public static String getPathName(String filePath, String fileName) {
        return filePath.replace(fileName, CmnStringUtil.EMPTY);
    }

    /**
     * Clear the contents of the file.<br/>
     * 
     * @param filePath
     *            File Path
     * @return true: Clear success false: Clear failure
     */
    public static boolean clearFileContents(String filePath) {
        FileOutputStream erasor = null;
        try {
            erasor = new FileOutputStream(filePath);
            erasor.write(CmnStringUtil.EMPTY.getBytes());
        } catch (FileNotFoundException e) {
            return false;
        } catch (IOException e) {
            return false;
        } finally {
            if (erasor != null) {
                try {
                    erasor.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }
        return true;
    }
}
