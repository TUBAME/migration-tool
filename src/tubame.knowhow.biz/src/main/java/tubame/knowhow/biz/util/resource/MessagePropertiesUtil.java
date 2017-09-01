/*
 * MessagePropertiesUtil.java
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
package tubame.knowhow.biz.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Common message class.<br/>
 * Implement various common process associated with the message.<br/>
 */
public final class MessagePropertiesUtil {
    /** Section tag is nested know-how detail data within */
    public static final String CHECK_NEST_SECTION_TAG = "checkNestSectionTag";
    /** null */
    public static final String EMPTY_MESSAGE = "emptyMessage";
    /** Failed to save file */
    public static final String ERROR_FILE_SAVE_FAILURE = "errorFileSaveFailure";
    /** Failed to read the XML know-how */
    public static final String ERROR_XML_READ_FAIL_KNOWHOW = "errorXmlReadFailKnowhow";
    /** It can not be placed in this place this element */
    public static final String ERROR_DROP_ELEMENT = "errorDropElement";
    /** Failed to get the workspace */
    public static final String ERROR_GET_WORKSPACE = "errorGetWorkspace";
    /** Please enter the subject name */
    public static final String ERROR_INPUT_SUBJECT_NAME = "errorInputSubjectName";
    /** Not move this element */
    public static final String ERROR_MOVE_ELEMENT = "errorMoveElement";
    /** Failed to display the know-how Editor */
    public static final String ERROR_OPEN_KNOWHOWEDITOR = "errorOpenKnowhowEditor";
    /** Failed to register a category */
    public static final String FAIL_CREATE_CATEGORY = "failCreateCategory";
    /** Failed to create the file */
    public static final String FAIL_CREATE_FILE = "failCreateFile";
    /** Failed to generate know-how editor */
    public static final String FAIL_CREATE_KNOWHOW_EDITOR = "failCreateKnowhowEditor";
    /** Failed to read initial processing */
    public static final String FAIL_INITIAL_READING = "failInitialReading";
    /** Failed to reads of information search module */
    public static final String FAIL_READ_SEARCH_MODULE = "failReadSearchModule";
    /** Failed to update the know-how entry view */
    public static final String FAIL_UPDATE_ENTRY_VIEW = "failUpdateEntryView";
    
    public static final String FAIL_OPEN_VIEW = "failOpenView";
    
    public static final String FAIL_REFRESH_CHECKITEM_VIEW = "failRefreshOpenCheckItemView";
    
    /** Failed to read the XML */
    public static final String FAIL_XML_READER = "failXmlReader";
    /** An error occurred fatal to completion button is pressed */
    public static final String FATAL_PERFORM_FINISH = "fatalPerformFinish";
    /**
     * Please enter the information obtained from the URI or create a new
     * know-how
     */
    public static final String COMMENCEMENT_PAGE_DESCRIPTION = "commencementPageDescription";
    /** Please check the inappropriate. Insertion point is the insertion point */
    public static final String INAPPROPRIATE_INSERT_PLACE = "inappropriateInsertPlace";
    /** XML know-how is not open */
    public static final String NOT_OPEN_KNOWHOW_XML = "notOpenKnowhowXml";
    /** Please specify the output destination */
    public static final String SELECTED_OUTPUT_FOLDER = "selectedOutputFolder";
    /** Please set the information of the destination */
    public static final String CONFIRMATION_PAGE_DESCRIPTION = "confirmationPageDescription";
    /** Parameters: */
    public static final String LOG_PARAMA = "logParam";
    /** Please enter the category name */
    public static final String INPUT_CATEGORY_SUBJECT = "inputCategorySubject";
    /** Please enter the item name check */
    public static final String INPUT_CHECKITEM_SUBJECT = "inputCheckItemName";
    /** Please enter the name of know-how */
    public static final String INPUT_KNOWHOW_SUBJECT = "inputKnowhowName";
    /** Problem occurs in the HTML conversion process */
    public static final String ERROR_HTML_CONVERT = "errorHtmlConvert";
    /** Problem occurs in the HTML conversion process */
    public static final String ERROR_SAVE_DOCBOOK = "errorSaveDocBook";
    
    public static final String ERROR_SAVE_ASCIIDOC = "errorSaveAsciiDoc";
    /** attachment of know-how detail does not exist */
    public static final String NOT_EXIST_DOCBOOK_FILE = "notExistDocBookFile";
    /** Failed to get the know-how editor */
    public static final String FAIL_GET_KNOWHOW_EDIROR = "failGetKnowhowEditor";
    /** Know-how editor unsaved state. Please perform the save processing */
    public static final String SAVE_PROCESS_KNOWHOW_EDITOR = "saveProcessKnowhowEditor";
    /** Failed to get the URL */
    public static final String FAIL_GET_URL = "failGetURL";
    /** Running the initial process of porting tool maintenance know-how */
    public static final String EXECUTE_INITIALIZE_PORTABILITY_KNOWHOW = "executeInitializePortabilityKnowhow";
    /** Failed to generate the indentation space */
    public static final String FAIL_GET_INDENTATION_SPACE = "failGetIndentationSpace";
    /** Please select an image file selection to be inserted */
    public static final String SELECT_INSERT_IMAGE_FILE = "selectInsertImageFile";
    /** A fatal error occurred in know-how detail information update */
    public static final String FATAL_UPDATE_KNOWHOW_DETAIL = "fatalUpdateKnowhowDetail";
    /** Failed to write file */
    public static final String FAIL_WRITE_FILE = "failWriteFile";
    /** Please confirm the syntax */
    public static final String MAKE_CHECK_SYNTAX = "makeCheckSyntax";
    /** Failed to transplant initial processing know-how */
    public static final String FAIL_INITIALIZE_PORTABILITY_KNOWHOW = "failInitializePortabilityKnowhow";
    /**
     * Editor know-how can not open more than one. Please close the \n \r this
     * editor
     */
    public static final String ERROR_MULTI_OPEN_KNOWHOWEDITOR = "errorMultiOpenKnowhowEditor";
    /** Same file already exists */
    public static final String EXIST_SAME_FILE = "existSameFile";
    /** Extension is not XML */
    public static final String NOT_XML_EXTENSION = "notXmlExtension";
    /** Failed to start Web browser of Eclipse standard */
    public static final String FAIL_OPEN_WEB_BROWSER = "failOpenWebBrowser";
    /** Save HTML know-how has been completed */
    public static final String PERFORM_SAVE_KNOWHOW_HTML = "performSaveKnowhowHtml";
    /** Preservation of know-how XML is complete */
    public static final String PERFORM_SAVE_KNOWHOW_XML = "performSaveKnowhowXml";
    
    public static final String PERFORM_SAVE_ASCII_DOC = "performSaveAsciiDoc";
    public static final String LOG_START_SAVE_ASCIIDOC = "logStartSaveAsciiDoc";
    /** File does not exist */
    public static final String NOT_EXIST_FILE = "notExistFile";
    /** Convert to HTML */
    public static final String CONVERT_HTML = "convertHtml";
    /** Has been canceled button press */
    public static final String PRESS_CANCEL_BUTTON = "pressCancelButton";
    /** After you select a resource, please perform the know-how creating XML */
    public static final String NOT_START_PORTABILITY_KNOWHOW = "notStartPortabilityKnowhow";
    /** Failed reads the DocBook XML format file */
    public static final String FAIL_READ_DOCBOOK_XML = "failReadDocBookXml";
    /**
     * Input value is invalid. Please specify the project under the present
     * know-how XML file output destination
     */
    public static final String ERROR_PROJECT_NOT_VALUE = "errorProjectNotValue";
    /** XML expertise of the selected empty. Please enter */
    public static final String ERROR_FILESIZE_IS_NULL = "errorFileSizeIsNull";
    /** XML know-how that has been selected can not be found */
    public static final String ERROR_NOT_EXIT_IMPORTFILE = "errorNotExitImportFile";
    /** Start the plugin. (Log) */
    public static final String LOG_PLUGIN_START = "logPluginStart";
    /** Exit the plugin. (Log) */
    public static final String LOG_PLUGIN_STOP = "logPluginStop";
    /** Paste the contents of the clipboard. (Log) */
    public static final String LOG_ELEMENT_PASTE = "logElementPaste";
    /** Opened the know-how XML editor. (Log) */
    public static final String LOG_OPEN_EDITOR = "logOpenEditor";
    /** Saved. (Log) */
    public static final String LOG_SAVE_EDITOR_START = "logSaveEditorStart";
    /** Drop. (Log) */
    public static final String LOG_DROP_ITEM = "logDropItem";
    /** Generate the know-how entry view. (Log) */
    public static final String LOG_CREATE_KNOWHOW_ENTRY_VIEW = "logCreateKnowhowEntryView";
    /** Start a drag. (Log) */
    public static final String LOG_DRAG_START = "logDragStart";
    /** Remove items from the tree. (Log) */
    public static final String LOG_ENTRY_REMOVE = "logEntryRemove";
    /** Create a new evaluation definition XML file creation Wizard. (Log) */
    public static final String LOG_CREATE_KNOWHOW_MAINTENANCE_WIZARD = "logCreateKnowhowMaintenanceWizard";
    /** Move the order of the elements. (Log) */
    public static final String LOG_MOVE_ITEM = "logMoveItem";
    /** Opened the know-how Registration Wizard screen. (Log) */
    public static final String LOG_OPEN_KNOWHOW_WIZARD = "logOpenKnowhowWizard";
    /** Information of the number of lines recorded has changed. (Log) */
    public static final String LOG_CHANGE_APPROPRIATE_LINE = "logChangeAppropriateLine";
    /** Open the folder selection screen. (Log) */
    public static final String LOG_OPEN_SELECT_FOLDER_DIALOG = "logOpenSelectFolderDialog";
    /** Open the file selection screen. (Log) */
    public static final String LOG_OPEN_SELECT_FILE_DOALOG = "logOpenSelectFileDialog";
    /** Generate a list to be displayed in the context menu. (Log) */
    public static final String LOG_CREATE_CONTEXTMENULIST = "logCreateContextMenuList";
    /** Launch a Web browser on a standard Eclipse. (Log) */
    public static final String LOG_OPEN_ECLIPSE_STANDARD_WEBBROWSER = "logOpenEclipseStandardWebBrowser";
    /** Get the category registration wizard. (Log) */
    public static final String LOG_OPEN_CATEGORY_WIZARD = "logOpenCategoryWizard";
    /** Get items check registration wizard. (Log) */
    public static final String LOG_OPEN_CHECKITEM_WIZARD = "logOpenCheckItemWizard";
    /** Set the number of lines recorded a selected item. (Log) */
    public static final String LOG_SETTING_APPROPRIATE_LINE = "logSettingAppropriateLine";
    /** Set the line number recorded a non-selected item. (Log) */
    public static final String LOG_SETTING_NONAPPROPRIATE_LINE = "logSettingNonAppropriateLine";
    /** Copy the selected item. (Log) */
    public static final String LOG_ELEMENT_COPY = "logElementCopy";
    /** Get the know-how information registration wizard. (Log) */
    public static final String LOG_OPEN_KNOWHOWINFOMATION_WIZARD = "logOpenKnowhowInfomationWizard";
    /** Get search information registration wizard. (Log) */
    public static final String LOG_OPEN_SEARCHINFOMATION_WIZARD = "logOpenSearchInfomationWizard";
    /** Change the subject name of the selected item. (Log) */
    public static final String LOG_CHANGE_SUBJECT_NAME = "logChangeSubjectName";
    /** Did the insertion of tags. (Log) */
    public static final String LOG_INSERT_TAG = "logInsertTag";
    /** Items that are registered have been double-click. (Log) */
    public static final String LOG_DOUBLE_CLICK_ITEM = "logDoubleClickItem";
    /** Save process is complete. (Log) */
    public static final String LOG_OUTPUT_KNOWHOW = "logOutputKnowhow";
    /** Save the know-how HTML. (Log) */
    public static final String LOG_SAVE_HTML = "logSaveHtml";
    /** Save the know-how XML. (Log) */
    public static final String LOG_SAVE_XML = "logSaveXml";
    /** Start the initial process of know-how. (Log) */
    public static final String LOG_INITIALIZE_MARSHAKKER_START = "logInitializeMarshallerStart";
    /** Initial processing of know-how end. (Log) */
    public static final String LOG_INITIALIZE_MARSHAKKER_STOP = "logInitializeMarshallerStop";
    /** Subject name has been changed. (Log) */
    public static final String LOG_CHANGE_PORTABILITYKNOWHOW_SUBJECTNAME = "logChangePortabilityKnowhowSubjectName";
    /** Please select an XML file */
    public static final String SELECT_XML_FILE = "selectXmlFile";
    /** Get the category Wizard. (Log) */
    public static final String LOG_CREATE_CATEGORY = "logCreateCategory";
    /** Get the know-how wizard. (Log) */
    public static final String LOG_CREATE_KNOWHOW = "logCreateKnowhow";
    /** Get a check item wizard. (Log) */
    public static final String LOG_CREATE_CHECKITEM = "logCreateCheckItem";
    /** Get information search wizard. (Log) */
    public static final String LOG_CREATE_SEARCHINFO = "logCreateSearchInfo";
    /** Generate editable know-how detail page. (Log) */
    public static final String LOG_CREATE_ENABLE_EDIT_KNOWHOWPAGE = "logCreateEnableEditKnowhowPage";
    /** Generates know-how detail pages editable. (Log) */
    public static final String LOG_CREATE_DISABLE_EDIT_KNOWHOWPAGE = "logCreateDisableEditKnowhowPage";
    /** Know-how entry view is not open */
    public static final String OPEN_KNOWHOW_ENTRY_VIEW = "openKnowhowEntryView";
    /** Failed to read the know-how XML. Know-how XML is empty */
    public static final String ERROR_READ_KNOWHWOW_XML_EMPTY = "errorReadKnowhowXmlEmpty";
    
    /**
     * Input value is invalid. Please specify the folder under the present
     * know-how XML file output destination
     */
    public static final String ERROR_FOLDER_NOT_VALUE = "errorFolderNotValue";
    /**
     * After you select the general project, please start the know-how
     * maintenance tool
     */
    public static final String NOT_SELECT_GENERAL_PROJECT = "notSelectGeneralProject";
    /** Close processing of the output file failed. (Log) */
    public static final String LOG_WARN_CLOSE_OUTPUT_FILE_FAILED = "logWarnCloseOutputFileFailed";

    /** Start an XML file read of know-how detail. (Log) */
    public static final String LOG_START_DOCBOOK_READER = "logStartDocBookReader";
    /** Complete the XML file read of know-how detail. (Log) */
    public static final String LOG_STOP_DOCBOOK_READER = "logStopDocBookReader";
    /** Start writing the XML file of know-how detail. (Log) */
    public static final String LOG_START_DOCBOOK_WRITER = "logStartDocBookWriter";
    /** The complete XML file writing know-how detail. (Log) */
    public static final String LOG_STOP_DOCBOOK_WRITER = "logStopDocBookWriter";
    /** Start the know-how XML file read. (Log) */
    public static final String LOG_START_PORTABILITY_KNOWHOW_READER = "logStartPortabilityKnowhowReader";
    /** The complete know-how to read XML file. (Log) */
    public static final String LOG_STOP_PORTABILITY_KNOWHOW_READER = "logStopPortabilityKnowhowReader";
    /** Start the know-how XML file writing. (Log) */
    public static final String LOG_START_PORTABILITY_KNOWHOW_WRITER = "logStartPortabilityKnowhowWriter";
    /** The complete know-how to write an XML file. (Log) */
    public static final String LOG_STOP_PORTABILITY_KNOWHOW_WRITER = "logStopPortabilityKnowhowWriter";
    /** Start search information XML file read. (Log) */
    public static final String LOG_START_SEARCH_MODULE_READER = "logStartSearchModuleReader";
    /** Complete the search information XML file read. (Log) */
    public static final String LOG_STOP_SEARCH_MODULE_READER = "logStopSearchModuleReader";
    /** Generate marshalers DocBook. (Log) */
    public static final String LOG_START_CREATE_DOCBOOK_MARSHALLER = "logStartCreateDocBookMarshaller";
    /** Generate marshalers DocBook. (Log)S */
    public static final String LOG_STOP_CREATE_DOCBOOK_MARSHALLER = "logStopCreateDocBookMarshaller";
    /** Generate Ann marshalers DocBook. (Log) */
    public static final String LOG_START_CREATE_DOCBOOK_UNMARSHALLER = "logStartCreateDocBookUnMarshaller";
    /** Generate Ann marshalers DocBook. (Log) */
    public static final String LOG_STOP_CREATE_DOCBOOK_UNMARSHALLER = "logStopCreateDocBookUnMarshaller";
    /** Generate marshaler of know-how. (Log) */
    public static final String LOG_START_CREATE_KNOWHOW_MARSHALLER = "logStartCreateKnowhowMarshaller";
    /** Generate marshaler of know-how. (Log) */
    public static final String LOG_STOP_CREATE_KNOWHOW_MARSHALLER = "logStopCreateKnowhowMarshaller";
    /** Generate Ann marshalers know-how. (Log) */
    public static final String LOG_START_CREATE_KNOWHOW_UNMARSHALLER = "logStartCreateKnowhowUnMarshaller";
    /** Generate Ann marshalers know-how. (Log) */
    public static final String LOG_STOP_CREATE_KNOWHOW_UNMARSHALLER = "logStopCreateKnowhowUnMarshaller";
    /** Save process is complete. (Log) */
    public static final String LOG_PERFORMFINISH_KNOWHOW_EDITOR_SAVE = "logPerformFinishKnowhowEditorSave";
    /** Generate XML file know-how. (Log) */
    public static final String LOG_START_CREATE_KNOWHOW_XML = "logStartCreateKnowhowXml";
    /** Generate XML file know-how. (Log) */
    public static final String LOG_STOP_CREATE_KNOWHOW_XML = "logStopCreateKnowhowXml";
    /** Start the HTML file storage process. (Log) */
    public static final String LOG_START_SAVE_HTML = "logStartSaveHtml";
    /** HTML file save process is complete. (Log) */
    public static final String LOG_STOP_SAVE_HTML = "logStopSaveHtml";
    /** Start an XML file save process. (Log) */
    public static final String LOG_START_SAVE_XML = "logStartSaveXml";
    /** XML file save process is complete. (Log) */
    public static final String LOG_STOP_SAVE_XML = "logStopSaveXml";
    
    public static final String LOG_STOP_SAVE_ASCIIDOC = "logStopSaveAsciiDoc";
    /** Start conversion process to HTML. (Log) */
    public static final String LOG_START_CONVERT_HTML = "logStartConvertHtml";
    /** Conversion to HTML is completed. (Log) */
    public static final String LOG_STOP_CONVERT_HTML = "logStopConvertHtml";
    
    public static final String JAXB_ERROR_EVENT_KNOWHOW_MARSHALL = "jaxbErrorEventKnowhowMarshall";
    
    public static final String ERR_OUTPUT_DIR_NOT_EXIST= "errorNotExistOutputDirectory";
    
    public static final String PERFORM_ADD_KNOWHOW_FROM_ASCIIDOC = "peformAddKnowhowFromAsciidoc";
    
    public static final String ERR_IMPORT_ASCIIDOC = "errImportAsciidoc";
    
    public static final String REQUIRED_ASCIIDOC_HEADER = "requireAsciidocHeader";
    
    public static final String EMPTY_ASCIIDOC = "emptyAsciidoc";
    
    /** Resource bundle */
    private static ResourceBundle resourceBundle = null;
    static {
        resourceBundle = ResourceBundle.getBundle("properties.knowhow.message");
    }

    /**
     * Constructor.<br/>
     * Sets the resource bundle.<br/>
     * 
     */
    private MessagePropertiesUtil() {
        // no operation
    }

    /**
     * Get the message value for the key.<br/>
     * 
     * @param key
     *            Key value of the message
     * @return Message string to brute Key
     */
    public static String getMessage(String key) {
        String value = key;
        try {
            value = resourceBundle.getString(key);
        } catch (NullPointerException e) {
            // Key is null
        } catch (MissingResourceException e) {
            // Object for the given key can not be found
        } catch (ClassCastException e) {
            // Object found for the given key is not a string
        }
        return value;
    }

    /**
     * Specify the input stream, to set the bundle of resources.<br/>
     * 
     * @param in
     *            Input stream
     * @throws IOException
     *             Throw in reading failure when the stream
     */
    public static void setBundle(InputStream in) throws IOException {
        resourceBundle = new PropertyResourceBundle(in);
    }
}
