/*
 * PythonUtil.java
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

import java.io.File;
import java.io.IOException;
import java.util.Map;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.Activator;
import tubame.portability.plugin.preferences.PythonPreferencePage;
import tubame.portability.util.resource.ApplicationPropertyUtil;

/**
 * Utility class that manages the operation of external execution module Python.<br/>
 * Utility to manage the path module name of the Python.<br/>
 */
public class PythonUtil {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PythonUtil.class);

    /**
     * Character code of Python modules
     */
    public static final String PYTHON_CHARACTOR_CODE = "Shift-Jis";

    
    public static String PY_RUNTIME_PATH = ApplicationPropertyUtil.PYTHON_RUNTIME_PATH;
    
    
    public static Map<String,Integer> PY_SEARCH_PROGRESS_STATUS_MAP = null;
    
    
    /**
     * Constructor.<br/>
     * It can not be instantiated from another class.<br/>
     * 
     */
    private PythonUtil() {
        super();
    }

    /**
     * Get the path of (exe) execution module of Python.<br/>
     * 
     * @return Path of Python executable EXE
     * @throws IOException
     *             plugin directory acquisition failure
     */
    public static String getPythonExePath() throws IOException {
    	String preferences = Activator.getPreferences(PythonPreferencePage.PREF_KEY_PY_RUNTIME_PATH);
    	if(preferences == null){
            String temp = ApplicationPropertyUtil.PYTHON_RUNTIME_PATH;
            if (!new File(temp).isAbsolute()) {
                temp = PluginUtil.getResolvedPluginDir() + temp;
            }
            return temp;
    	}else{
    		return preferences;
    	}

    }

    /**
     * Get the path of porting Python module search item.<br/>
     * 
     * @return Path of Python executable EXE
     * @throws IOException
     *             plugin directory acquisition failure
     */
    public static String getPythonSearchModulePath() throws IOException {
        String temp = PluginUtil.getResolvedPluginDir()
                + ApplicationPropertyUtil.PYTHON_SEARCH_MODULE_PATH;
        LOGGER.info(temp);
        return temp;
    }

    /**
     * Get the path of the search keyword file.<br/>
     * 
     * @param keywordFile
     *            Search Keyword files
     * @return Keyword file full path
     * @throws IOException
     *             plugin directory acquisition failure
     */
    public static String getSearchKeywordFilePath(String keywordFile)
            throws IOException {
        String filePath = PluginUtil.getResolvedPluginDir() + keywordFile;
        LOGGER.info(filePath);
        return filePath;
    }
    
    /**
     * Get the path of the search keyword file.<br/>
     * 
     * @param keywordFile
     *            Search Keyword files
     * @return Keyword file full path
     * @throws IOException
     *             plugin directory acquisition failure
     */
    public static String getReportGenerationPath()
            throws IOException {
        String filePath = PluginUtil.getResolvedPluginDir() + ApplicationPropertyUtil.REPORTGEN_OUTPUT_DIR;
        LOGGER.info(filePath);
        return filePath;
    }
    
    public static String getReportTplPath()
            throws IOException {
        String filePath = PluginUtil.getResolvedPluginDir() + ApplicationPropertyUtil.REPORT_TEMPORARY_JSON;
        LOGGER.info(filePath);
        return filePath;
    }
    
    
    public static String getPyPath()
            throws IOException {
        String filePath = PluginUtil.getResolvedPluginDir() + ApplicationPropertyUtil.REPORTGEN_OUTPUT_DIR;
        LOGGER.info(filePath);
        return filePath;
    }
    
    public static String getWinSearchModulePath() throws IOException {
        String temp = PluginUtil.getResolvedPluginDir()
                + ApplicationPropertyUtil.WIN_PYTHON_SEARCH_MODULE;
        LOGGER.info(temp);
        return temp;
    }
    
    public static String getWinSearchModuleZipPath() throws IOException {
        String temp = PluginUtil.getResolvedPluginDir()
                + ApplicationPropertyUtil.WIN_PYTHON_SEARCH_MODULE_ZIP;
        return temp;
    }
    
    public static String getMacSearchModulePath() throws IOException {
        String temp = PluginUtil.getResolvedPluginDir()
                + ApplicationPropertyUtil.MAC_PYTHON_SEARCH_MODULE;
        LOGGER.info(temp);
        return temp;
    }
    
    public static String getMacSearchModuleZipPath() throws IOException {
        String temp = PluginUtil.getResolvedPluginDir()
                + ApplicationPropertyUtil.MAC_PYTHON_SEARCH_MODULE_ZIP;
        LOGGER.info(temp);
        return temp;
    }
    
    public static String getLinuxSearchModulePath() throws IOException {
        String temp = PluginUtil.getResolvedPluginDir()
                + ApplicationPropertyUtil.LINUX_PYTHON_SEARCH_MODULE;
        LOGGER.info(temp);
        return temp;
    }
    
    public static String getLinuxSearchModuleZipPath() throws IOException {
        String temp = PluginUtil.getResolvedPluginDir()
                + ApplicationPropertyUtil.LINUX_PYTHON_SEARCH_MODULE_ZIP;
        LOGGER.info(temp);
        return temp;
    }
  
    
}
