/*
 * CmnMessagePropertiesUtil.java
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
package tubame.common.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * Common message class.<br/>
 * It can be used to perform various common process associated with the message.<br/>
 */
public class CmnMessagePropertiesUtil {

    /** Resource bundle */
    private static ResourceBundle resourceBundle = null;
    static {
        resourceBundle = ResourceBundle.getBundle("properties.common.message");
    }

    /** Message: Failed to save the file. */
    public static final String ERR_FILE_SAVE_FAILURE = "errFileSaveFailure";

    /** Message: An unexpected error has occurred. */
    public static final String ERR_UNEXPECTED = "errUnexpected";

    /** Message: I/O device error has occurred. */
    public static final String ERROR_JBMIO = "errorJbmIo";

    /** Message: Failed to Close processing of files. */
    public static final String ERROR_FILE_CLOSE = "errorFileClose";

    /** Message: Failed in the Open file handling. */
    public static final String ERROR_FILE_OPNE = "errorFileOpne";

    /** Message: Extension is not XML. */
    public static final String ERROR_NOTMATCH_EXTENSION = "errorNotMatchExtension";

    /** Message: Failed to save the file. */
    public static final String ERROR_SAVE_ASSESSMENTDEFINITION_FILE = "errorSaveAssessmentDefinitionFile";

    /** Message: Error message in the XML output. */
    public static final String ERROR_SAVE_INCORRECTDATA = "errorSaveInCorrectData";

    /** Message: (Check the {2} character of the line {1}.) */
    public static final String ERR_JAXB_SUB = "errJaxbSub";

    /** Message: Failed to read the XML. */
    public static final String ERR_XML_READ_FAIL = "errorXmlReadFail";

    /**
     * Constructor.<br/>
     * prohibits the instantiation from other classes.<br/>
     * 
     */
    private CmnMessagePropertiesUtil() {
        // no operation
    }

    /**
     * Set the message property resource bundle.<br/>
     * Set the resource bundle of message properties.<br/>
     * When using a common module jar, call this method by utilizing the plug of
     * Activator#start.<br/>
     * 
     * @param in
     *            Input stream of common module message properties file
     * @throws IOException
     */
    public static void setBundle(InputStream in) throws IOException {
        resourceBundle = new PropertyResourceBundle(in);
    }

    /**
     * The process to get the message value.<br/>
     * get the message value for the key.<br/>
     * 
     * @param key
     *            Key value of the message
     * @return Messages related to the Key (If it fails to get, return the key)
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
            // Objects found with the specified key is not a string
        }
        return value;
    }
}
