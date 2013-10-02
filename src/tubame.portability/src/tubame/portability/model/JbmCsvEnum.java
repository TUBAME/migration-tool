/*
 * JbmCsvEnum.java
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
package tubame.portability.model;

/**
 * Enumeration of JbmCSV.<br/>
 * Manage the order of delimiter (delimiter).<br/>
 */
public enum JbmCsvEnum {
    /**
     * NO
     */
    TOKEN_INDEX_NO(0),
    /**
     * File name
     */
    TOKEN_INDEX_TARGET_FILE_PATH(1),
    /**
     * Count (Hit number)
     */
    TOKEN_INDEX_COUNT_NO(2),
    /**
     * Line number
     */
    TOKEN_INDEX_ROW_NO(3),
    /**
     * Degree of difficulty
     */
    TOKEN_INDEX_DIFFICULTY(4),
    /**
     * Conversion flag
     */
    TOKEN_INDEX_CHANGE_ITEM(5),
    /**
     * Guide chapter number
     */
    TOKEN_INDEX_CHAPTER_NO(6),
    /**
     * Check status
     */
    TOKEN_INDEX_CONFIRM_ITEM_KEY(7),
    /**
     * Porting factor
     */
    TOKEN_INDEX_FACTOR(8),
    /**
     * Difficulty details
     */
    TOKEN_INDEX_DEGREE_DETAIL(9),
    /**
     * Number of lines
     */
    TOKEN_INDEX_LINE_NUMBER(10),
    /**
     * Line number basis
     */
    TOKEN_INDEX_LINE_NUMBER_CONTENTS(11),
    /**
     * Total line
     */
    TOKEN_INDEX_TOTAL_LINENUM(12);

    /**
     * The maximum number of CSV delimiter
     */
    public static final int CSV_COLUMN_NUM = 13;
    /**
     * CSV column index
     */
    private int code;

    /**
     * Constructor.<br/>
     * Generate enumeration.<br/>
     * 
     * @param code
     *            Column index
     */
    private JbmCsvEnum(int code) {
        this.code = code;
    }

    /**
     * Get the code.<br/>
     * 
     * @return code
     */
    public int getCode() {
        return code;
    }

}
