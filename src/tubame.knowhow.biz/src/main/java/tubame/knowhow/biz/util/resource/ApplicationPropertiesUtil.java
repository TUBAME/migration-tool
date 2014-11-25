/*
 * ApplicationPropertiesUtil.java
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
 * Implement various common process related to the property.<br/>
 */
public final class ApplicationPropertiesUtil {

    /** Category icon path */
    public static final String PATH_CATEGORY_ICON = "pathCategoryIcon";
    /** Category non-recognized icon path */
    public static final String PATH_CATEGORY_APPROPRIATE_ICON = "pathCategoryAppropriateIcon";
    /** Know-how */
    public static final String PATH_KNOWHOW_ICON = "pathKnowhowIcon";
    /** Search information */
    public static final String PATH_SEARCH_ICON = "pathSearchIcon";
    /** Search Non-recorded */
    public static final String PATH_SEARCH_APPROPRIATE_ICON = "pathSearchAppropriateIcon";
    /** Heading */
    public static final String PATH_CHAPTER_ICON = "pathChapterIcon";
    /** Check items */
    public static final String PATH_CHECKITEM_ICON = "pathCheckItemIcon";
    /** .xml */
    public static final String EXTENSION_PORTABILITYKNOWHOW = "extensionPortabilityKnowhow";
    /** searchModuleXmlPath */
    public static final String SEARCH_MODULE_XML_PATH = "searchModuleXmlPath";
    /** NewKnowhow.xml */
    public static final String NEW_FILENAME_KNOWHOW_XML = "newFileNameKnowhowXML";
    /** /DocBookTemporary.xml */
    public static final String DOCBOOK_TEMPORARY_PATH = "docBookTemporaryPath";
    /** utf-8 */
    public static final String DOCBOOK_ENCODE = "docbookEncode";
    /** utf-8 */
    public static final String KNOWHOW_ENCODE = "knowhowEncode";
    /** utf-8 */
    public static final String SEARCHMODULE_ENCODE = "searchModuleEncode";
    /** Path of know-how XML schema **/
    public static final String PORTABILITYKNOWHOWSCHEMA_PATH = "portabilityKnowhowSchemaPath";
    /** Path of the search module definition schema **/
    public static final String PYTHONMODULEDEFINITIONSCHEMA_PATH = "pythonModuleDefinitionSchemaPath";
    /** Path of DocBook schema **/
    public static final String DOCBOOKSCHEMA_PATH = "docBookSchemaPath";
    /** File DocBookHTML one o'clock **/
    public static final String TEMPORARY_HTML_KNOWHOWDETAIL = "temporaryHtmlKnowhowDetail";
    /** File DocumentationHTML one o'clock **/
    public static final String TEMPORARY_HTML_DOCUMENTATION = "temporaryHtmlDocumentaion";
    /** Working directory **/
    public static final String WORK_DIRECTORY = "workDirectory";
    /** *.html */
    public static final String HTML_EXTENSION = "htmlExtension";
    /** *.xml */
    public static final String XML_EXTENSION = "xmlExtension";
    /** html */
    public static final String HTML = "html";
    /** degreeDetailHigh */
    public static final String DEGREEDETAIL_HIGH = "degreeDetailHigh";
    /** degreeDetailMiddle */
    public static final String DEGREEDETAIL_MIDDLE = "degreeDetailMiddle";
    /** low1 */
    public static final String LOW1 = "low1";
    /** low2 */
    public static final String LOW2 = "low2";
    /** degreeDetailNotPortability */
    public static final String DEGREEDETAIL_NOT_PORTABILITY = "degreeDetailNotPortability";
    /** unknown1 */
    public static final String UNKNOWN1 = "unknown1";
    /** unknown2 */
    public static final String UNKNOWN2 = "unknown2";
    /** degreeDetailNotSearch */
    public static final String DEGREEDETAIL_NOT_SEARCH = "degreeDetailNotSearch";
    /** category */
    public static final String CATEGORY = "category";
    /** knowhow */
    public static final String KNOWHOW = "knowhow";
    /** knowhowDetail */
    public static final String KNOWHOWDETAIL = "knowhowDetail";
    /** checkItem */
    public static final String CHECKITEM = "checkItem";
    /** searchInfo */
    public static final String SEARCHINFO = "searchInfo";
    /** empty */
    public static final String EMPTY = "empty";
    /** toDoSE */
    public static final String TODOSE = "toDoSE";
    /** lineNumberUnknown */
    public static final String LINENUMBER_UNKNOWN = "lineNumberUnknown";
    /** high */
    public static final String HIGH = "high";
    /** middle */
    public static final String MIDDLE = "middle";
    /** low */
    public static final String LOW = "low";
    /** notPortability */
    public static final String NOT_PORTABILITY = "notPortability";
    /** unknown */
    public static final String UNKNOWN = "unknown";
    /** notSearch */
    public static final String NOT_SEARCH = "notSearch";
    /** weblogicSpecific */
    public static final String WEBLOGIC_SPECIFIC = "weblogicSpecific";
    /** apServerAPIChange */
    public static final String AP_SERVERAPICHANGE = "apServerAPIChange";
    /** apLibrary */
    public static final String AP_LIBRARY = "apLibrary";
    /** apServerSpecific */
    public static final String APSERVERSPECIFIC = "apServerSpecific";
    /** jspServlet */
    public static final String JSP_SERVLET = "jspServlet";
    /** javaVersionUp */
    public static final String JAVA_VERSIONUP = "javaVersionUp";
    /** dbmSChange */
    public static final String DBMS_CHANGE = "dbmSChange";
    
    public static final String FRAMEWORK = "framework";
    
    public static final String FRAMEWORK_NON_BACKWARD_COMPATI = "frameworkNonBackwardCompati";
    
    public static final String OPEN_CHECKITEM_ENTRY_VIEW = "labelOpenCheckEntryItemView";
    
    public static final String MVC_FRAMEWORK_M = "mvcFrameworkM";
    
    public static final String MVC_FRAMEWORK_V = "mvcFrameworkV";
    
    public static final String MVC_FRAMEWORK_C = "mvcFrameworkC";
    
    public static final String MVC_FRAMEWORK_S = "mvcFrameworkSpecific";

    /** Resource bundle */
    private static ResourceBundle resourceBundle = null;
    static {
        resourceBundle = ResourceBundle
                .getBundle("properties.knowhow.application");
    }

    /**
     * Constructor.<br/>
     * Sets the resource bundle.<br/>
     * 
     */
    private ApplicationPropertiesUtil() {
        // no operation
    }

    /**
     * Get the property value for the key.<br/>
     * 
     * @param key
     *            Key value of the property
     * @return Property the value associated to the Key
     */
    public static String getProperty(String key) {
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
     * Specify the input stream, set the bundle of resources.<br/>
     * 
     * @param in
     *            Input stream
     * @throws IOException
     *             Throw to read failure when the stream
     */
    public static void setBundle(InputStream in) throws IOException {
        resourceBundle = new PropertyResourceBundle(in);
    }
}
