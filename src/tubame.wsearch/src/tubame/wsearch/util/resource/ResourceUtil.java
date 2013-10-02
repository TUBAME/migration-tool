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
package tubame.wsearch.util.resource;

import org.eclipse.osgi.util.NLS;

/**
 * It Is a resource for obtaining common classes.<br/>
 * It performs various common process associated with the resource.<br/>
 */
public class ResourceUtil extends NLS {
    /**
     * Bundle name
     */
    private static final String BUNDLE_NAME = "resources.properties.resource"; //$NON-NLS-1$

    /**
     * Extension of the ZIP file
     */
    public static String EXTENSION_ZIP;
    /**
     * Extension of the CSV file
     */
    public static String EXTENSION_CSV;

    /**
     * Result file name
     */
    public static String RESULT_FILE_NAME;
    /**
     * Character code in the CSV file
     */
    public static String DEFAULT_CSV_CHARACTER_CODE;

    /**
     * Path of the message properties file
     */
    public static String MESSAGE_PROPERTIES_PATH;
    /**
     * Path of the message properties file
     */
    public static String RESOURCE_PROPERTIES_PATH;

    /**
     * Path of error icon
     */
    public static String ERROR_ICON;
    /**
     * Path of the warning icon
     */
    public static String WARN_ICON;
    /**
     * Path of the expand icon all
     */
    public static String EXPAND_ALL_ICON;
    /**
     * Path of thumbnail icon all
     */
    public static String COLLAPSE_ALL_ICON;
    /**
     * Path of the filter icon
     */
    public static String FILTER_ICON;
    /**
     * Path of the configuration icon
     */
    public static String SETTING_ICON;
    /**
     * The path of the save icon to CSV
     */
    public static String CSV_ICON;

    static {
        // initialize resource bundle
        NLS.initializeMessages(BUNDLE_NAME, ResourceUtil.class);
    }
}