/*
 * ZipUtil.java
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
package org.tubame.wsearch.biz.util;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.nio.channels.ClosedByInterruptException;
import java.util.Enumeration;
import java.util.jar.JarOutputStream;
import java.util.jar.Pack200;
import java.util.jar.Pack200.Unpacker;

import org.apache.tools.zip.ZipEntry;
import org.apache.tools.zip.ZipFile;
import org.apache.tools.zip.ZipOutputStream;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * It is a utility class that will handle the ZIP file.<br/>
 */
public class ZipUtil {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory.getLogger(ZipUtil.class);

    /**
     * Performs the compression of the ZIP file.<br/>
     * 
     * @param targetBase
     *            Directory to a compressed
     * @param destination
     *            The destination directory
     * @throws IOException
     *             If it fails to file input and output
     */
    public static void zip(File targetBase, File destination)
            throws IOException {
        String zipFileName = targetBase.getName() + ".zip";
        String baseDirPath = targetBase.getParentFile().getAbsolutePath()
                + File.separator;

        // Destination file
        File zipFile = new File(destination, zipFileName);
        LOGGER.debug(MessageUtil.getResourceString("debug.msg.write.file")
                + zipFile);
        ZipOutputStream zos = null;
        try {
            zos = new ZipOutputStream(new BufferedOutputStream(
                    new FileOutputStream(zipFile)));
            archive(zos, targetBase, baseDirPath);
        } finally {
            if (zos != null) {
                zos.close();
            }
        }
    }

    /**
     * Add the compressed data in a ZIP file.<br/>
     * 
     * @param zos
     *            Stream to the output destination
     * @param file
     *            Files to be added
     * @param baseDirPath
     *            Directory path that is a base of the compression process
     * @throws IOException
     *             If an error occurs when the file input and output
     */
    private static void archive(ZipOutputStream zos, File file,
            String baseDirPath) throws IOException {
        if (file.isDirectory()) {
            // Directory to restart call the files contained.
            File[] files = file.listFiles();
            for (File f : files) {
                archive(zos, f, baseDirPath);
            }
        } else {
            // Generate the input stream.
            LOGGER.trace(MessageUtil.getResourceString("debug.msg.read.file")
                    + file);
            BufferedInputStream fis = null;
            try {
                fis = new BufferedInputStream(new FileInputStream(file));
                // Get the Entry name.
                String entryName = file.getAbsolutePath().replace(baseDirPath,
                        "");
                // Set the output destination Entry.
                zos.putNextEntry(new ZipEntry(entryName));
                // Write to the output stream read the input file.
                int ava = 0;
                while ((ava = fis.available()) > 0) {
                    byte[] bs = new byte[ava];
                    int byteCount = fis.read(bs);
                    zos.write(bs, 0, byteCount);
                }
                // Close the Entry After writing.
                zos.closeEntry();
            } finally {
                if (fis != null) {
                    fis.close();
                }
            }
        }
    }

    /**
     * Do the decompression of ZIP files.<br/>
     * 
     * @param targetFileName
     *            File name of the ZIP file and unzip target
     * @param destination
     *            The destination directory
     * @throws IOException
     *             If the file I / O error occurs
     */
    public static void unzip(String targetFileName, File destination)
            throws IOException {
        unzip(new File(targetFileName), destination);
    }

    /**
     * Do the decompression of ZIP files.<br/>
     * 
     * @param targetFile
     *            Path of the ZIP file and unzip target
     * @param destination
     *            The destination directory
     * @throws IOException
     *             If the file I / O error occurs
     */
    public static void unzip(File targetFile, File destination)
            throws IOException {
        if (!destination.exists()) {
            destination.mkdirs();
        }
        LOGGER.debug(MessageUtil.getResourceString("debug.msg.read.file")
                + targetFile.toString());
        ZipFile zip = null;
        try {
            zip = new ZipFile(targetFile);
            Enumeration<? extends ZipEntry> entries = zip.getEntries();
            while (entries.hasMoreElements()) {
                ZipEntry entry = entries.nextElement();
                if (entry.isDirectory()) {
                    continue;
                } else {
                    File out = new File(destination, entry.getName());
                    BufferedInputStream iStream = null;
                    try {
                        if (!out.getParentFile().exists()) {
                            out.getParentFile().mkdirs();
                        }
                        iStream = new BufferedInputStream(
                                zip.getInputStream(entry));
                        LOGGER.trace(MessageUtil
                                .getResourceString("debug.msg.write.file")
                                + out.toString());
                        BufferedOutputStream bo = new BufferedOutputStream(
                                new FileOutputStream(out.getAbsolutePath()));
                        byte[] buf = new byte[1024];
                        int size = 0;
                        while ((size = iStream.read(buf)) != -1) {
                            bo.write(buf, 0, size);
                        }
                        bo.flush();
                        bo.close();
                    } finally {
                        if (iStream != null) {
                            iStream.close();
                        }
                    }
                }
            }
        } catch (ClosedByInterruptException e) {
            // Ignore to go only log output because it occurs just when pressed
            // the cancel button.
            LOGGER.debug(
                    MessageUtil.getResourceString(ZipUtil.class.getName()
                            + ".err.interrupted"), e);
        } finally {
            if (zip != null) {
                zip.close();
            }
        }
    }

    /**
     * Check entry with the specified name whether included in the ZIP file.<br/>
     * 
     * @param filePath
     *            Path of the ZIP file
     * @param entryName
     *            Entry name
     * @return Entry to see if it includes a
     * @throws IOException
     *             If the file I / O error occurs
     */
    public static boolean existsEntry(String filePath, String entryName)
            throws IOException {
        ZipFile file = null;
        try {
            file = new ZipFile(filePath);
            ZipEntry entry = file.getEntry(entryName);
            if (entry == null) {
                if (entryName.contains("\\")) {
                    entryName = entryName.replaceAll("\\\\", "/");
                } else if (entryName.contains("/")) {
                    entryName = entryName.replaceAll("\\/", "\\\\");
                }
            } else {
                return true;
            }
            entry = file.getEntry(entryName);
            return (entry != null);
        } finally {
            if (file != null) {
                file.close();
            }
        }
    }

    /**
     * Extract the files packed in Pack200 format.<br/>
     * 
     * @param targetFile
     *            Path of the file that has been packed
     * @param destination
     *            Destination of the extracted files
     * @return The path of the file that was deployed
     * @throws IOException
     *             If the file I / O error occurs
     */
    public static File unpack(File targetFile, File destination)
            throws IOException {
        if (!destination.exists()) {
            destination.mkdirs();
        }

        String fileName = targetFile.getName();
        int dotIndex = fileName.lastIndexOf(".");
        if (dotIndex > 0) {
            fileName = fileName.substring(0, dotIndex);
        }
        File outFile = new File(destination, fileName);

        Unpacker unpacker = Pack200.newUnpacker();

        LOGGER.debug(MessageUtil.getResourceString("debug.msg.read.file")
                + targetFile.toString());
        LOGGER.debug(MessageUtil.getResourceString("debug.msg.write.file")
                + outFile.toString());
        JarOutputStream out = null;
        try {
            out = new JarOutputStream(new BufferedOutputStream(
                    new FileOutputStream(outFile)));
            unpacker.unpack(targetFile, out);
        } finally {
            if (out != null) {
                out.close();
            }
        }
        return outFile;
    }
}
