/*
 * StringUtil.java
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
package org.tubame.portability.util;

import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;

/**
 * Utility classes related to the string.<br/>
 * Empty blank judgment and judgment and related trim.<br/>
 */
public class StringUtil {
    /**
     * Null character
     */
    public static final String EMPTY = "";
    /**
     * Blank character
     */
    public static final String BLANK = " ";
    /**
     * Slash
     */
    public static final String SLASH = "/";
    /**
     * CSV delimiter (,)
     */
    public static final String CSV_DELIMITER = ",";
    /**
     * Period (.)
     */
    public static final String PERIOD = ".";
    /**
     * Hyphen (-)
     */
    public static final String HYPHEN = "-";
    /**
     * Line feed code
     */
    public static final String LINE_SEPARATOR = System
            .getProperty("line.separator");

    /**
     * Default constructor.<br/>
     * Disable the instance generation from other class.<br/>
     * 
     */
    private StringUtil() {
        // no operation
    }

    /**
     * The Returns true if the string is an empty string or null.<br/>
     * 
     * @param target
     *            Investigation target string
     * @return true:Null character false:Non-null
     */
    public static boolean isEmpty(String target) {
        if (target == null) {
            return true;
        }
        if (target.length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * Return whether blank.<br/>
     * 
     * @param target
     *            Investigation target string
     * @return true:Blank false:Non-blank
     */
    public static boolean isBlank(String target) {
        if (StringUtil.BLANK.equals(target)) {
            return true;
        }
        return false;
    }

    /**
     * The return of the true extension of the target is the same as if the
     * extension.<br/>
     * 
     * @param target
     *            Investigation target string
     * @param extension
     *            Extension
     * @return true:Extension jbm false:Extension other than jbm
     */
    public static boolean isFileExtension(String target, String extension) {
        if (target.endsWith(PERIOD + extension)) {
            return true;
        }
        return false;
    }

    /**
     * Get a string from the Text.<br/>
     * 
     * @param text
     *            Text
     * @return Text string
     */
    public static String getText(Object text) {
        if (text == null) {
            return StringUtil.EMPTY;
        }
        if (text instanceof Text) {
            return ((Text) text).getText();
        }
        return StringUtil.EMPTY;
    }

    /**
     * Get a string from the Combo.<br/>
     * 
     * @param combo
     *            Text
     * @return Text string
     */
    public static String getComboText(Object combo) {
        if (combo == null) {
            return StringUtil.EMPTY;
        }
        if (combo instanceof Combo) {
            return ((Combo) combo).getText();
        }
        return StringUtil.EMPTY;
    }
}
