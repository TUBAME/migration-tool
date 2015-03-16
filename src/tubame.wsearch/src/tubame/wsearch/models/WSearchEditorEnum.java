/*
 * WSearchEditorEnum.java
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

import tubame.wsearch.Activator;

/**
 * An enumeration that defines the column items are displayed in the search
 * results view.<br/>
 */
public enum WSearchEditorEnum {
    /**
     * Result
     */
    INDEX_STATUS(0, Activator.getResourceString(WSearchEditorEnum.class
            .getName() + ".label.result"), 100, 0),
    /**
     * Category
     */
    INDEX_CATEGORY(1, Activator.getResourceString(WSearchEditorEnum.class
            .getName() + ".label.category"), 100, 1),
    /**
     * Package
     */
    INDEX_PACKAGE(2, Activator.getResourceString(WSearchEditorEnum.class
            .getName() + ".label.package"), 400, 2),
    /**
     * Porting the original library
     */
    INDEX_SRC_LIBRARY(3, Activator.getResourceString(WSearchEditorEnum.class
            .getName() + ".label.source"), 0, 3),
    /**
     * Porting library
     */
    INDEX_DEST_LIBRARY(4, Activator.getResourceString(WSearchEditorEnum.class
            .getName() + ".label.destination"), 0, 3),
    /**
     * Class
     */
    INDEX_CLAZZ(5, Activator.getResourceString(WSearchEditorEnum.class
            .getName() + ".label.class"), 150, 3),
    /**
     * Hits
     */
    INDEX_HIT_NUM(6, Activator.getResourceString(WSearchEditorEnum.class
            .getName() + ".label.hit"), 60, 3),
    /**
     * Migrated files
     */
    INDEX_FILES(7, Activator.getResourceString(WSearchEditorEnum.class
            .getName() + ".label.file"), 200, 4),
    /**
     * Line
     */
    INDEX_LINE(8, Activator.getResourceString(WSearchEditorEnum.class.getName()
            + ".label.line"), 60, 4),
    /**
     * Result detail
     */
    INDEX_DETAIL(9, Activator.getResourceString(WSearchEditorEnum.class
            .getName() + ".label.detail"), 200, 4),
    /**
     * Remarks
     */
    INDEX_NOTE(10, Activator.getResourceString(WSearchEditorEnum.class
            .getName() + ".label.note"), 200, 4);

    /**
     * Index column value
     */
    private int index;

    /**
     * Column title
     */
    private String title;

    /**
     * Column width
     */
    private int width;

    /**
     * Display level
     */
    private int level;

    /**
     * Constructor.<br/>
     * Do not do anything.<br/>
     * 
     * @param index
     *            CSV column Index value
     */
    private WSearchEditorEnum(int index, String title, int width, int level) {
        this.index = index;
        this.title = title;
        this.width = width;
        this.level = level;
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
     * Get the column title.<br/>
     * 
     * @return Column title
     */
    public String getTitle() {
        return title;
    }

    /**
     * Get the column width.<br/>
     * 
     * @return Column width
     */
    public int getWidth() {
        return width;
    }

    /**
     * Get the display level.<br/>
     * 
     * @return Display level
     */
    public int getLevel() {
        return level;
    }

    /**
     * Get WSearchEditorEnum the specified instance.<br/>
     * If the code does not exist, return NULL.<br/>
     * 
     * @param code
     *            Code
     * @return WSearchEditorEnum
     */
    public static WSearchEditorEnum get(int code) {
        for (WSearchEditorEnum temp : WSearchEditorEnum.values()) {
            if (temp.getIndex() == code) {
                return temp;
            }
        }
        return null;
    }
}