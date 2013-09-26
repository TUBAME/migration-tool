/*
 * CmnStringUtil.java
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
package org.tubame.common.util;

/**
 * A utility class involved in the string.<br/>
 * Make an empty judgment and blank judgment and trim related.<br/>
 */
public class CmnStringUtil {

    /** Blank character */
    public static final String BLANK = " ";
    /** Empty string */
    public static final String EMPTY = "";
    /** Hyphen (-) */
    public static final String HYPHEN = "-";
    /** Period (.) */
    public static final String PERIOD = ".";
    /** Slash */
    public static final String SLASH = "/";
    /** CSV delimiter */
    public static final String CSV_DELIMITER = ",";
    /** Underscore (_) */
    public static final String UNDERLINE = "_";
    /** Period (\\.) */
    public static final String SPRITTING_PERIOD = "\\.";
    /** Initial number */
    public static final String INITIAL_NUMBER = "0";
    /** Line feed code */
    public static final String LINE_SEPARATOR = System
            .getProperty("line.separator");

    /**
     * Default constructor<br/>
     * Prohibits the instantiation from other classes.<br/>
     * 
     */
    private CmnStringUtil() {
        // no operation
    }

    /**
     * Return to extract the column string from a row string of CSV.<br/>
     * Is divided by a comma string and returns a string of the specified
     * column.<br/>
     * The first column begins with {@code 0}.<br/>
     * If the column does not exist is specified, null is returned.<br/>
     * 
     * @param line
     *            String that contains a comma in CSV
     * @param col
     *            Column number to retrieve
     * @return The return of the CSV data of the specified column
     */
    public static String getCsvData(String line, int col) {
        String[] csvDatas = line.split(CSV_DELIMITER);
        if (csvDatas.length > col) {
            return csvDatas[col];
        }
        return EMPTY;
    }

    /**
     * Return whether blank.<br/>
     * 
     * @param target
     *            Investigation target string
     * @return true:Blank false:Non-blank
     */
    public static boolean isBlank(String target) {
        if (BLANK.equals(target)) {
            return true;
        }
        return false;
    }

    /**
     * To return true if the string is an empty string or {@code null}.<br/>
     * 
     * @param target
     *            Investigation target string
     * @return true:Null character false:Non-null character
     */
    public static boolean isEmpty(String target) {
        if (target == null) {
            return true;
        }
        if (target.isEmpty()) {
            return true;
        }
        return false;
    }

    /**
     * return the true extension're like {@code extension}.<br/>
     * 
     * @param target
     *            Investigation target string
     * @param extension
     *            Extension(Not including the dot ("."))
     * @return true:Extension match false:Extension mismatch
     */
    public static boolean isFileExtension(String target, String extension) {
        if (target.endsWith(PERIOD + extension)) {
            return true;
        }
        return false;
    }

    /**
     * Make the verdict string specified is whether the numerical value.<br/>
     * 
     * @param value
     *            The target string
     * @return If the specified string is a number, and return the {@code true}
     */
    public static boolean isNumber(final String value) {
        try {
            Double.parseDouble(value);
            return true;
        } catch (NumberFormatException e) {
            return false;
        }
    }

    /**
     * Get the terminal number.<br/>
     * Get the end of the number string that is coupled with an underscore.<br/>
     * 
     * @param targetStr
     *            Target string
     * @return Terminal number
     */
    public static int getTerminalNumber(String targetStr) {
        String[] tempStr = targetStr.split(CmnStringUtil.UNDERLINE);
        return Integer.valueOf(tempStr[1]);
    }

    /**
     * Get the indentation string of default.<br/>
     * Get the default indentation (spaces) string.<br/>
     * 
     * @param indentatinIndex
     *            Indented by several
     * @return Indent string of default
     */
    private static String defaltIndentation(int indentatinIndex) {
        StringBuilder defaltIndentation = new StringBuilder();
        for (int i = 0; i < indentatinIndex * 4; i++) {
            defaltIndentation.append(CmnStringUtil.BLANK);
        }
        return defaltIndentation.toString();
    }

    /**
     * Create a new line.<br/>
     * Generate a new line(new line + space indentation).<br/>
     * 
     * @param indentationSpace
     *            Indent space
     * @return New line
     */
    public static String newLine(String indentationSpace) {
        return CmnStringUtil.LINE_SEPARATOR + indentationSpace;
    }

    /**
     * Get a string that granted indent (space) to the tag string to be
     * inserted.<br/>
     * 
     * @param insertTag
     *            Insert object tag string
     * @param indentatinIndex
     *            Indented by several
     * @return Tag insertion string
     */
    public static String getTagStr(String insertTag, int indentatinIndex) {
        return CmnStringUtil.defaltIndentation(indentatinIndex) + insertTag;
    }
}
