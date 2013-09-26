/*
 * CmnJbmToolsLoggingUtil.java
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
package org.tubame.common.logging;

import java.io.File;
import java.io.FileOutputStream;
import java.io.InputStream;
import java.net.URL;

import org.slf4j.LoggerFactory;

import ch.qos.logback.classic.LoggerContext;
import ch.qos.logback.classic.joran.JoranConfigurator;
import ch.qos.logback.core.util.StatusPrinter;

/**
 * Log to .metadata/.plugins/plugin folder name.<br/>
 */
public class CmnJbmToolsLoggingUtil {

    /**
     * Initializing process.<br/>
     * Do the initialization of the logging component.<br/>
     * 
     * @param filename
     *            Log file name
     * @param stateDirPath
     *            State location of Eclipse directory that contains the folder
     *            name of the plugin
     * @throws Exception
     */
    public static void configureLoggerForLocal(String filename,
            File stateDirPath) throws Exception {

        File configFile = new File(stateDirPath, "logback.xml");
        if (!configFile.isFile()) {

            InputStream is = null;
            try {
                is = CmnJbmToolsLoggingUtil.class.getClass()
                        .getResourceAsStream("/defaultLogback/logback.xml");
                if (is != null) {
                    configFile.getParentFile().mkdirs();
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(configFile);
                        for (byte[] buffer = new byte[1024 * 4];;) {
                            int n = is.read(buffer);
                            if (n < 0) {
                                break;
                            }
                            fos.write(buffer, 0, n);
                        }
                    } finally {
                        if (fos != null) {
                            fos.close();
                        }
                    }
                } else {
                    throw new IllegalArgumentException(
                            "logback path is invalid path:");
                }
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        loadConfiguration(filename, stateDirPath, configFile);
    }

    /**
     * Perform the initialization process.<br/>
     * Do the initialization of the logging component.<br/>
     * 
     * @param filename
     *            Log file name
     * @param stateDirPath
     *            State location of Eclipse directory that contains the folder
     *            name of the plugin
     * @param pluginHoldlogbackResourceUrl
     *            Logback resource URL
     * @throws Exception
     */
    public static void configureLoggerForPlugin(String filename,
            File stateDirPath, URL pluginHoldlogbackResourceUrl)
            throws Exception {

        File configFile = new File(stateDirPath, "logback.xml");
        if (!configFile.isFile()) {
            InputStream is = null;
            try {
                is = pluginHoldlogbackResourceUrl.openStream();
                if (is != null) {
                    configFile.getParentFile().mkdirs();
                    FileOutputStream fos = null;
                    try {
                        fos = new FileOutputStream(configFile);
                        for (byte[] buffer = new byte[1024 * 4];;) {
                            int n = is.read(buffer);
                            if (n < 0) {
                                break;
                            }
                            fos.write(buffer, 0, n);
                        }
                    } finally {
                        if (fos != null) {
                            fos.close();
                        }
                    }
                } else {
                    throw new IllegalArgumentException(
                            "logback path is invalid path:");
                }
            } finally {
                if (is != null) {
                    is.close();
                }
            }
        }
        loadConfiguration(filename, stateDirPath, configFile);
    }

    /**
     * Make the configuration file load.<br/>
     * Perform configuration file reading.<br/>
     * 
     * @param filename
     *            Log file name
     * @param loggingDir
     *            State location of Eclipse directory that contains the folder
     *            name of the plugin
     * @param configFile
     *            Configuration file
     * @throws Exception
     */
    private static void loadConfiguration(String filename, File loggingDir,
            File configFile) throws Exception {
        LoggerContext lc = (LoggerContext) LoggerFactory.getILoggerFactory();
        lc.reset();

        lc.putProperty(CmnJbmToolsLoggingUtil.class.getPackage().getName()
                + ".dir", loggingDir.toString());
        lc.putProperty("filename", filename);

        JoranConfigurator configurator = new JoranConfigurator();
        configurator.setContext(lc);
        configurator.doConfigure(configFile);

        StatusPrinter.printInCaseOfErrorsOrWarnings(lc);
    }
}
