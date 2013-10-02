/*
 * WSearchCsvEnum.java
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
package tubame.wsearch.models;

/**
 * An enumeration that defines the item in the search results file.<br/>
 */
public enum WSearchCsvEnum {
    /**
     * Result
     */
    TOKEN_INDEX_STATUS(0),
    /**
     * Category
     */
    TOKEN_INDEX_CATEGORY(1),
    /**
     * Package
     */
    TOKEN_INDEX_PACKAGE(2),
    /**
     * Porting the original library
     */
    TOKEN_INDEX_SRC_LIBRARY(3),
    /**
     * Porting library
     */
    TOKEN_INDEX_DEST_LIBRARY(4),
    /**
     * Class
     */
    TOKEN_INDEX_CLAZZ(5),
    /**
     * Porting the original file
     */
    TOKEN_INDEX_FILES(6),
    /**
     * Search corresponding line
     */
    TOKEN_INDEX_HIT_LINE(7),
    /**
     * Result detail
     */
    TOKEN_INDEX_DETAIL(8),
    /**
     * Remarks
     */
    TOKEN_INDEX_NOTE(9);
    /**
     * CSV column Index value
     */
    private int index;

    /**
     * The maximum number of CSV delimiter
     */
    public static final int CSV_COLUMN_NUM = 8;

    /**
     * Constructor.<br/>
     * Do not do anything.<br/>
     * 
     * @param index
     *            CSV column Index value
     */
    private WSearchCsvEnum(int index) {
        this.index = index;
    }

    /**
     * Get the index.<br/>
     * 
     * @return index
     */
    public int getIndex() {
        return index;
    }

    /**
     * Set the index.<br/>
     * 
     * @param index
     *            index
     */
    public void setIndex(int index) {
        this.index = index;
    }
}