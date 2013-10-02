/*
 * CmnResourceUtil.java
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
 * Resource common classes.<br/>
 * It can be used to perform various common process associated with the
 * resource.<br/>
 */
public class CmnResourceUtil {

    /** Resource bundle */
    private static ResourceBundle resourceBundle = null;
    static {
        resourceBundle = ResourceBundle.getBundle("properties.common.resource");
    }

    /** Separator of additional information and answer */
    public static final String ANSWER_ADDITION_SEPARATOR = "answerAddtionSeparator";

    /** String */
    public static final String DATAKIND_STRING_VIEW = "dataKindStringView";

    /** Date */
    public static final String DATAKIND_DATE_VIEW = "dataKindDateView";

    /** Numeric value */
    public static final String DATAKIND_NUMBER_VIEW = "dataKindNumberView";

    /** Chapter without */
    public static final String NO_GUIDE_LINE_CHAPTER = "noGuideLineChapter";

    /** ( */
    public static final String START_BRACKET = "startBracket";

    /** ) */
    public static final String END_BRACKET = "endBracket";

    /** - */
    public static final String MIDDLE_DOTS = "middleDots";

    /** Log message: Failed to convert the DocBook object. */
    public static final String LOG_ERR_DOCBOOK_CONVERT = "logErrorDocBookConvert";

    /** Log message: The destination directory does not exist. */
    public static final String LOG_ERR_OUTPUT_DIR_NOT_FOUND = "logErrorOutputDirNotFound";

    /** Log message: Style sheet is invalid. */
    public static final String LOG_ERR_STYLESHEET_INACCURATE = "logErrorStylesheetInaccurate";

    /** Log message: XSL file does not exist. */
    public static final String LOG_ERR_XSL_FILE_NOT_FOUND = "logErrorXslFileNotFound";

    /**
     * Constructor.<br/>
     * Prohibits the instantiation from other classes.<br/>
     * 
     */
    private CmnResourceUtil() {
        // no operation
    }

    /**
     * Set the resource property resource bundle.<br/>
     * When using a common module jar, call this method by utilizing the plug of
     * Activator#start.<br/>
     * 
     * @param in
     *            Input stream of common module resource properties file
     * @throws IOException
     */
    public static void setBundle(InputStream in) throws IOException {
        resourceBundle = new PropertyResourceBundle(in);
    }

    /**
     * Process after getting the resource value.<br/>
     * Return the resource value for the specified key.<br/>
     * 
     * @param key
     *            Key value of the resource
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
