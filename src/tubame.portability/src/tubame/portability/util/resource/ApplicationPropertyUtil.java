/*
 * ApplicationPropertyUtil.java
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
package tubame.portability.util.resource;

import org.eclipse.osgi.util.NLS;

/**
 * Property operation common classes.<br/>
 * Various common process that is associated with the property.<br/>
 */
public class ApplicationPropertyUtil extends NLS {
    /**
     * Properties file name
     */
    private static final String BUNDLE_NAME = "resources.properties.application"; //$NON-NLS-1$

    /**
     * Python executable path
     */
    public static String PYTHON_RUNTIME_PATH;

    /**
     * Path of transplant item search Python modules
     */
    public static String PYTHON_SEARCH_MODULE_PATH;

    /**
     * Check list information file path
     */
    public static String CHECK_LIST_INFORMATION_FILE_PATH;
    /**
     * HTM file path
     */
    public static String GUIDE_FILE_PATH;
    /**
     * Jbm default file name
     */
    public static String DEFAULT_JBM_NAME;

    /**
     * Search keyword file path
     */
    public static String SEARCH_KEYWORD_FILE;

    /**
     * Conversion place search keyword file path
     */
    public static String SEARCH_CONVERT_KEYWORD_FILE;

    /**
     * Conversion place search result output destination path
     */
    public static String CONVERT_JBM_OUT_FILE_PATH;

    /**
     * Default file name conversion status
     */
    public static String DEFAULT_CONVERT_STATUS_FILE_NAME;

    /**
     * Know-how default XML file name
     */
    public static String DEFAULT_KNOWHOW_FILE_NAME;

    /**
     * CSV extension
     */
    public static String EXTENSION_CSV;

    /**
     * Edited column (number of lines)
     */
    public static String EDIT_COLUMN_LINENUM;

    /**
     * Edited column (line number basis)
     */
    public static String EDIT_COLUMN_LINENUMCONTENTS;

    /**
     * XML file character set
     */
    public static String CHARSET_XML;

    /**
     * CSV file character set
     */
    public static String CHARSET_CSV;

    /**
     * Default CSV file name
     */
    public static String DEFAULT_CSV_FILENAME;

    /**
     * Message property path (know-how Biz)
     */
    public static String PROPERTIES_PATH_MSG_KNOWHOW;

    /**
     * Application property path (know-how Biz)
     */
    public static String PROPERTIES_PATH_APP_KNOWHOW;

    /**
     * Temporary file output CSV
     */
    public static String OUTPUT_TEMPORARY_CSV;
    
    
    public static String SEARCH_REPORTGEN_KEYWORD_FILE;

	public static String REPORTGEN_OUTPUT_DIR;
	
	public static String KNOWLEDGE_DIR;
	
	public static String REPORT_TEMPORARY_JSON;
	
	public static String WIN_PYTHON_SEARCH_MODULE;
	
	public static String MAC_PYTHON_SEARCH_MODULE;
	
	public static String WIN_PYTHON_SEARCH_MODULE_ZIP;
	
	public static String MAC_PYTHON_SEARCH_MODULE_ZIP;
	
	public static String LINUX_PYTHON_SEARCH_MODULE;
	
	public static String LINUX_PYTHON_SEARCH_MODULE_ZIP;
	
	
	

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ApplicationPropertyUtil.class);
    }

}
