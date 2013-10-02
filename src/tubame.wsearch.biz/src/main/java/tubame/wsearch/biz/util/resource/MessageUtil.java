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
package tubame.wsearch.biz.util.resource;

import java.io.IOException;
import java.io.InputStream;
import java.text.MessageFormat;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

/**
 * It is a class that manages the message information.<br/>
 */
public class MessageUtil {

    /**
     * Resource bundle
     */
    private static ResourceBundle resource = null;

    static {
        try {
            resource = PropertyResourceBundle
                    .getBundle("properties.wsearch.message");
        } catch (MissingResourceException e) {
            resource = null;
        }
    }

    /**
     * Specify the input stream, set the bundle of resources.<br/>
     * 
     * @param in
     *            Input stream
     * @throws IOException
     *             If it fails to load the stream
     */
    public static void setBundle(InputStream in) throws IOException {
        resource = new PropertyResourceBundle(in);
    }

    /**
     * Get the message that corresponds to the key from the properties file.<br/>
     * 
     * @param key
     *            Key message
     * @return Message
     */
    public static String getResourceString(String key) {
        if (resource == null) {
            return key;
        }
        try {
            return resource.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }

    /**
     * Get the error message JSP.<br/>
     * 
     * @param where
     *            Whether error where it occurs
     * @param key
     *            Key message
     * @param args
     *            Variable to replace the part embedded in the message
     * @return JSP error message
     */
    public static String getJspErrorMessage(final String where,
            final String key, final Object... args) {
        final StringBuilder buf = new StringBuilder();
        if (where != null) {
            buf.append(where + ": ");
        }
        buf.append(MessageFormat.format(resource.getString(key), args));
        return buf.toString();
    }
}
