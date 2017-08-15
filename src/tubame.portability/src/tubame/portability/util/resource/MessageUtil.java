/*
 * MessageUtil.java
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
 * Message operation common classes.<br/>
 * Various common processes associated with the message.<br/>
 */
public class MessageUtil extends NLS {
    private static final String BUNDLE_NAME = "resources.properties.message"; //$NON-NLS-1$

    public static String ERR_JBM_IO;
    public static String ERR_ADD_MARKER;
    public static String INF_CONVERT_CANCEL;
    public static String INF_CONVERT_COMPLETE;
    public static String ERR_CONVERT_FAILED;
    public static String ERR_CONVERT_FILEPATH_INVALID;
    public static String ERR_CONVERT_DIRECTORY_NOT_ENTERED;
    public static String ERR_REPORT_DIRECTORY_NOT_ENTERED;
    public static String ERR_CONVERT_PROJECT_NOT_ENTERED;
    public static String ERR_CONVERT_PROJECT_NOT_VALUE;
    public static String ERR_REPORTGEN_PROJECT_NOT_VALUE;
    public static String ERR_REPORTGEN_PROJECT_NOT_OPEN;
    public static String ERR_CONVERT_PROJECT_NOT_OPEN;
    public static String ERR_CONVERT_DATA_NOT_FOUND;
    public static String QUE_CONVERT_DATA_CONFIRM;
    public static String INF_CONVERT_RUNNING;
    public static String ERR_CONVERT_READ;
    public static String ERR_DIRECTORY_NOT_ENTERED;
    public static String ERR_DIRECTORY_NOT_VALUE;
    public static String ERR_FILEPATH_INVALID;
    public static String ERR_GET_DIFFICULTY_ICON;
    public static String ERR_GUID_VIEW;
    public static String ERR_JBM_NOT_ENTERED;
    public static String ERR_JBM_NOT_EXTENSION;
    public static String ERR_KNOWHOW_NOT_EXTENSION;
    public static String ERR_JBM_FORMAT_CHECK_NO;
    public static String ERR_JBM_FORMAT_CHECK_ROW_NUMBER;
    public static String ERR_JBM_FORMAT_CHECK_SIZE;
    public static String ERR_JBM_NOT_FOUND;
    public static String ERR_PLUGINUTIL_PLUGIN_DIRECTORY_GET;
    public static String ERR_PROJECT_NOT_ENTERED;
    public static String ERR_PROJECT_NOT_ENTERED_SOURCEFOOLDER;
    public static String ERR_PROJECT_NOT_VALUE;
    public static String ERR_PROJECT_NOT_VALUE_SOURCEFOLDER;
    public static String ERR_PROJECT_NOT_VALUE_KNOWHOW_XML;
    public static String ERR_PROJECT_NOT_OPEN;
    public static String ERR_PROJECT_NOT_OPEN_SOURCEFOLDER;
    public static String ERR_PROJECT_NOT_OPEN_KNOWHOW_XML;
    public static String ERR_SAVE;
    public static String INF_SEARCH_CANCELED;
    public static String INF_SEARCH_COMPLETE;
    public static String ERR_SEARCH_FAILED;
    public static String INF_SEARCH_RUNNING;
    public static String QUE_SEARCH_UPDATE_CONFIRM;
    public static String INF_SHOW_SEARCHWIZARD;
    public static String INF_SHOW_REPORTGENWIZARD;
    public static String INF_SHOW_CONVERTWIZARD;
    public static String INF_CHECKLIST_INFORMATION_PATH;
    public static String ERR_CONVERT_NOT_FOUND;
    public static String ERR_CONVERT_FILE_CLOSE;
    public static String ERR_CONVERT_TYPE;
    public static String ERR_CONVER_XML_WRITE;
    public static String ERR_FILE_CLOSE;
    public static String CHECKLIST_INFORMATION_FILE_ERROR;
    public static String ERR_CONVERT_VIEW_OPEN;
    public static String ERR_WORK_STATUS_VIEW_OPEN;
    public static String ERR_SEARCH_PERSPECTIVE_OPEN;
    public static String INF_CANCEL;
    public static String ERR_CONVERT_MESSAGE;
    public static String INF_CONVERT_OUT_MESSAGE;
    public static String ERR_CONVERT_CONVERTER_GET_ERROR;
    public static String ERR_VIEW_OUTPUT;
    public static String ERR_OPEN_FILE;
    public static String INF_SEARCHONLY_COMPLETE;
    public static String ERR_CONVERT_FILE_READ;
    public static String INF_SAVE;
    public static String INF_SAVED;
    public static String FILE_NOT_DATA;
    public static String INF_START;
    public static String INF_END;
    public static String INF_SEARCH_NON;
    public static String ERR_NOT_SELECTED_PROJECT;
    public static String INF_CHECK_DELETE_ROW;
    public static String INF_DELETE_EDITOR_ROW;
    public static String INF_NO_DELETE_ROW;
    public static String CONVERTEDFILE_HAS_TODO;
    public static String INF_JBM_FILE_CHANGED;
    public static String INF_ACTION_SAVE_CSV;
    public static String INF_ACTION_JBM_FILTER;
    public static String INF_SAVE_CSV_START;
    public static String INF_SAVE_CSV_END;
    public static String ERR_PARSING_XML;
    public static String ERR_KNOWHOW_XML_NOT_ENTERED;
    public static String ERR_READ_KNOWHOW_XML;
    public static String ERR_CONVERT_KNOWHOW_XML;
    public static String LOG_READ_KNOWHOW_XML;
    public static String LOG_CREATE_SEARCH_KEYWORD_CSV;
    public static String LOG_CREATE_CHECKLISTINFO_XML;
    public static String ERR_CREATE_SEARCHINFO_CSV;
    public static String LOG_WARN_FILE_DELETE_FAIL;
    public static String LOG_ERR_GUIDE_HTML_DISPLAY_FAIL;
    public static String ERR_EDIT_CELL_VALUE;
    public static String ERR_OUTPUT_CSV;
    public static String INF_CHANGE_SEARCH_FILTER;
    public static String ERR_NULL_SEARCHKEYWORD_FILE;
    public static String LOG_INFO_PROC_START;
    public static String LOG_INFO_PROC_START_P;
    public static String LOG_INFO_PROC_END;
    public static String LOG_INFO_PROC_END_P;
    public static String LOG_INFO_PROC_CANCEL;
    public static String LOG_ERR_PROC_ABNORMAL_END;
    public static String LOG_ERR_PROC_ABNORMAL_END_P;
    public static String LOG_INFO_PROC_NAME_SEARCH;
    public static String LOG_INFO_PROC_NAME_CONVERT;
    public static String LOG_INFO_ACTION;
    public static String LOG_INFO_ACTION_P;
    public static String LOG_INFO_BTN_NAME_FINISH;
    public static String LOG_INFO_BTN_NAME_CANCEL;
    public static String LOG_INFO_BTN_NAME_SEARCH;
    public static String LOG_INFO_BTN_NAME_YES;
    public static String LOG_INFO_BTN_NAME_NO;
	public static String ERR_REPORTGEN_FAILED;
	public static String INF_REPORTGEN_COMPLETE;
	public static String INF_REPORTGEN_CANCEL;
	public static String LOG_INFO_PROC_NAME_REPORTGEN;
	public static String INF_SEARCH_REPORTGEN_NON;
	public static String ERR_REPORTGEN_CHKLISTXML_NON;
	
	public static String REPORT_TPL_APSERVER;
	
	public static String REPORT_TPL_MVCFRAMWORK;
	public static String REPORT_TPL_STRUTSFRAMWORK;
	
	public static String ERR_GET_REPORT_TPL;
	
	public static String INF_START_KNOWHOW_SEARCH;
	
	public static String UNZIP_INTERRUPT_ERR;
	

	public static String ITEM_EXPAND;
	public static String ITEM_COLLAPSE;
	public static String ALL_EXPAND;
	public static String ALL_COLLAPSE;
	public static String CONFIRM_RERPORT_SHOW_BROWSER;
	
	
	public static String ERR_OPEN_CODE_VIEWER;

	public static String MSG_OPEN_CODE_VIEWER;
	
	public static String WARN_KNOWLEDGE_IMPORT_ERR;
	
	public static String STEPCOUNT_WIZARDPAGE_TITLE;
	public static String STEPCOUNT_WIZARDPAGE_INFO;
	public static String STEPCOUNT_WIZARDPAGE_OUTPUT;
	public static String STEPCOUNT_WIZARDPAGE_BROWSE;
	public static String STEPCOUNT_WIZARD_SUCCESS_DIALOG_TITLE;
	public static String STEPCOUNT_WIZARD_ERR_DIALOG_TITLE;
	public static String STEPCOUNT_WIZARD_SUCCESS_MSG;
	public static String STEPCOUNT_WIZARD_ERR_MSG;
	public static String STEPCOUNT_WIZARD_SEARCH_START_MSG;
	
	public static String STEPCOUNT_WIZARD_CANCEL_MSG;
	
	
    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, MessageUtil.class);
    }

}
