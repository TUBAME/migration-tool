/*
 * ResourceUtil.java
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
 * Resource operations common classes.<br/>
 * Various common process related to the resource.<br/>
 */
public class ResourceUtil extends NLS {

    /**
     * Store the text
     */
    private static final String BUNDLE_NAME = "resources.properties.resource"; //$NON-NLS-1$

    public static String CONFIRM_STATUS_NG;

    public static String CONFIRM_STATUS_NON;

    public static String CONFIRM_STATUS_OK;

    public static String DIALOG_CONVERT;

    public static String DIALOG_INFO_CONVERT;

    public static String DIALOG_INFO_SEARCH;

    public static String DIALOG_SEARCH;

    public static String DIR_SELECT_MESSAGE;

    public static String DIR_SELECT_TITLE;

    public static String EDITOR_VIEW_BIG_ITEM;

    public static String EDITOR_VIEW_CHAPTOR_NO;

    public static String EDITOR_VIEW_DIFFICULTY;

    public static String EDITOR_VIEW_FILE;

    public static String EDITOR_VIEW_HIARING;

    public static String EDITOR_VIEW_HIARING_STATUS;

    public static String EDITOR_VIEW_HIT_NUM;

    public static String EDITOR_VIEW_LINE;

    public static String EDITOR_VIEW_MIDDLE_ITEM;

    public static String EDITOR_VIEW_NO;

    public static String EDITOR_VIEW_VISUAL_CONFIRM;

    public static String EDITOR_VIEW_VISUAL_CONFIRM_STATUS;

    public static String EDITOR_VIEW_LINE_NUM;

    public static String EDITOR_VIEW_LINE_NUM_BASIS;

    public static String EDITOR_VIEW_TOTAL_LINE;

    public static String FILE_SELECT_MESSAGE;

    public static String FILE_SELECT_TITLE;

    public static String REFERENCE;

    public static String SELECT_FILE_SEARCH_RESULT;

    public static String SELECT_KNOWHOW_XML_FILE;

    public static String SELECT_WEBLOGICVERSION;

    public static String SELECT_FILE_CONVERTED;
    public static String JBM_VIEW_STATUS_OK;
    public static String JBM_VIEW_STATUS_NG;
    public static String JBM_EDITOR;

    public static String CONVERT_VIEW_NO;
    public static String CONVERT_VIEW_CHANGE_ITEM;
    public static String CONVERT_VIEW_RESULT;
    public static String VIEW_WORK;
    public static String VIEW_CHECKLISTINFORMATION;
    public static String FILE_OPEN_ERROR_TITLE;

    public static String CONFIRM_ITEM_MENU_STRING_EYE_OK;
    public static String CONFIRM_ITEM_MENU_STRING_EYE_NG;
    public static String CONFIRM_ITEM_MENU_STRING_EYE_NON;

    public static String CONFIRM_ITEM_MENU_STRING_HIARING_OK;
    public static String CONFIRM_ITEM_MENU_STRING_HIARING_NG;
    public static String CONFIRM_ITEM_MENU_STRING_HIARING_NON;

    public static String CONVERT_MENU_OPEN_FILE;

    public static String CHECKLISTVIEW_TILE;
    public static String CHECKLISTVIEW_BIG;
    public static String CHECKLISTVIEW_MIDDLE;
    public static String CHECKLISTVIEW_EYE;
    public static String CHECKLISTVIEW_HEARING;
    public static String CHECKLISTVIEW_SEARCH;
    public static String CHECKLISTVIEW_FACTOR;
    public static String CHECKLISTVIEW_DEGREE;
    public static String CHECKLISTVIEW_APP;
    public static String CHECKLISTVIEW_INV;

    public static String CONVERT_OK;
    public static String CONVERT_NG;

    public static String WORKVIEW_NOW_WORK;
    public static String WORKVIEW_NOW_CONFIRM;
    public static String WORKVIEW_NOW_LINE_CONFIRM;
    public static String WORKVIEW_MESSAGE_FILE_UPDATE;
    public static String WORKVIEW_MESSAGE_CONFIRM;
    public static String WORKVIEW_MESSAGE;
    public static String WORKVIEW_MESSAGE_LINE;
    public static String WORKVIEW_MESSAGE_UNKNOWN;
    public static String WORKVIEW_MESSAGE_TODO_SE;
    public static String WORKVIEW_MESSAGE_TODO_SE_CONFIRM;
    public static String WORKVIEW_MESSAGE_COMPLETE;
    public static String SEARCH_ERROR_FILE_STRING;
    public static String SEARCH_ERROR_STRING;
    public static String LINE_DELETE_TITLE;

    public static String BEFORE_CONVERTING_FILE;
    public static String AFTER_CONVERTING_FILE;
    public static String TODO;

    public static String WIZ_TITLE_SEARCH;
    public static String WIZ_TITLE_CONVERT;
    public static String WIZ_TITLE_REPORTGEN;
    public static String SELECT_REPORT_OUTPUT_DIR;
    public static String SEARCH_PROGRESS;

    public static String EDIT_CELL;
    public static String OUTPUT_CSV;
    
    public static String DIALOG_REPORTGEN;

	public static String REPORTGEN_PROGRESS;
	
	
	
    
    

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ResourceUtil.class);
    }

    /**
     * Constructor.<br/>
     * 
     */
    private ResourceUtil() {
    }
}
