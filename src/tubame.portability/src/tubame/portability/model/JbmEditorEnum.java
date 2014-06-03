/*
 * JbmEditorEnum.java
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

import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * An enumeration to display in Jbm editor.<br/>
 * Order to be displayed in the search results editing screen to manage the
 * column column display name.<br/>
 */
public enum JbmEditorEnum {
    /**
     * NO
     */
    INDEX_NO(0),
    
    
    /**
     * Large category
     */
    BIG_ITEM(1),
    
    /**
     * Middle item
     */
    MIDDLE_ITEM(2),
    
    
    /**
     * Degree of difficulty
     */
    DIFFICULTY(3),
    
    /**
     * HIT number
     */
    HIT_NUM(4),

 
    
    
    /**
     * File name
     */
    TARGET_FILE_PATH(5),
    /**
     * Line number
     */
    ROW_NO(6),
    
    
    /**
     * Number of lines
     */
    LINE_NUM(7),
    /**
     * Line number basis
     */
    LINE_NUM_BASIS(8),
    /**
     * Total line
     */
    TOTAL_LINE(9),
    
    /**
     * Guide chapter number
     */
    CHAPTER_NO(10),
    
    
    


    

    /**
     * Visual confirmation status items
     */
    VISUAL_CONFIRM_STATSU_ITEM(11),
    /**
     * Hearing confirmation item
     */
    HIARING_STATUS(12),
    

    /**
     * Visual confirmation item
     */
    VISUAL_CONFIRM_ITEM(13),
    /**
     * Confirmation hearing content
     */
    HIARING_ITEM(14),


    /**
     * Porting factor
     */
    LINE_FACTOR(15),
    /**
     * Difficulty details
     */
    DEGREE_DETAIL(16);
    /**
     * Screen display column index
     */
    private int code;

    /**
     * Constructor.<br/>
     * Generate enumeration.<br/>
     * 
     * @param code
     *            Column index
     */
    private JbmEditorEnum(int code) {
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

    /**
     * Get JbmEditorEnum the specified instance.<br/>
     * If the code does not exist, it returns a NULL.<br/>
     * 
     * @param code
     *            Code
     * @return JbmEditorEnum
     */
    public static JbmEditorEnum get(int code) {
        for (JbmEditorEnum temp : JbmEditorEnum.values()) {
            if (temp.getCode() == code) {
                return temp;
            }
        }
        return null;
    }

    /**
     * Get the name.<br/>
     * 
     * @return name
     */
    public String getName() {
        switch (this) {
        case INDEX_NO:
            return ResourceUtil.EDITOR_VIEW_NO;
        case HIT_NUM:
            return ResourceUtil.EDITOR_VIEW_HIT_NUM;
        case BIG_ITEM:
            return ResourceUtil.EDITOR_VIEW_BIG_ITEM;
        case MIDDLE_ITEM:
            return ResourceUtil.EDITOR_VIEW_MIDDLE_ITEM;
        case TARGET_FILE_PATH:
            return ResourceUtil.EDITOR_VIEW_FILE;
        case ROW_NO:
            return ResourceUtil.EDITOR_VIEW_LINE;
        case DIFFICULTY:
            return ResourceUtil.EDITOR_VIEW_DIFFICULTY;
        case CHAPTER_NO:
            return ResourceUtil.EDITOR_VIEW_CHAPTOR_NO;
        case VISUAL_CONFIRM_ITEM:
            return ResourceUtil.EDITOR_VIEW_VISUAL_CONFIRM;
        case HIARING_ITEM:
            return ResourceUtil.EDITOR_VIEW_HIARING;
        case VISUAL_CONFIRM_STATSU_ITEM:
            return ResourceUtil.EDITOR_VIEW_VISUAL_CONFIRM_STATUS;
        case HIARING_STATUS:
            return ResourceUtil.EDITOR_VIEW_HIARING_STATUS;
        case LINE_NUM:
            return ResourceUtil.EDITOR_VIEW_LINE_NUM;
        case LINE_NUM_BASIS:
            return ResourceUtil.EDITOR_VIEW_LINE_NUM_BASIS;
        case TOTAL_LINE:
            return ResourceUtil.EDITOR_VIEW_TOTAL_LINE;
        }
        return StringUtil.EMPTY;
    }
}
