/*
 * AbstractEditorComparator.java
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
package org.tubame.portability.plugin.editor;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.swt.widgets.TreeItem;
import org.tubame.portability.util.StringUtil;

/**
 * Comparison of the column.<br/>
 */
public abstract class AbstractEditorComparator implements Comparator<TreeItem>,
        Serializable {
    /**
     * Serial version ID
     */
    private static final long serialVersionUID = 1L;
    /**
     * Ascending / descending order
     */
    private final boolean asc;
    /**
     * Column
     */
    private final String column;
    /**
     * Column Index
     */
    private final int columnIndex;

    /**
     * Constructor.
     * 
     * @param asc
     *            Ascending / descending order
     * @param column
     *            Sorted column
     * @param columnIndex
     *            Sorted column Index
     */
    public AbstractEditorComparator(boolean asc, String column, int columnIndex) {
        super();
        this.asc = asc;
        this.column = column;
        this.columnIndex = columnIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(TreeItem o1, TreeItem o2) {
        String value1 = o1.getText(columnIndex).trim();
        String value2 = o2.getText(columnIndex).trim();

        int nRet = jbmEditorCompare(columnIndex, value1, value2);
        if (!asc) {
            nRet = nRet * -1;
        }
        return nRet;
    }

    /**
     * Run comparator along the column.<br/>
     * 
     * @param index
     *            Column Index
     * @param value1
     *            Comparison value 1
     * @param value2
     *            Comparison value 2
     * @return Comparison result
     */
    public abstract int jbmEditorCompare(int index, String value1, String value2);

    /**
     * Comparison of type int.<br/>
     * 
     * @param value1
     *            Target 1
     * @param value2
     *            Target 2
     * @return Comparison result
     */
    protected int intComparator(int value1, int value2) {
        return Integer.valueOf(value1).compareTo(Integer.valueOf(value2));
    }

    /**
     * Comparison of No.<br/>
     * Because in the format of "-" -
     * "knowhow ID-Check Item No chapter numbers (. Separated)" No Item,<br/>
     * we compare the chapter number to divide by (hyphen), <br/>
     * if the chapter numbers match , comparison of the number of subordinate.<br/>
     * 
     * @param value1
     *            Target 1
     * @param value2
     *            Target 2
     * @return Comparison result
     */
    protected int numComparator(String value1, String value2) {
        // null, blank check
        if ((value1 == null || value1.isEmpty())
                && (value2 == null || value2.isEmpty())) {
            return 0;
        }
        if (value1 == null || value1.isEmpty()) {
            return -1;
        }
        if (value2 == null || value2.isEmpty()) {
            return 1;
        }

        // Separated by a hyphen-separated
        String[] temp1 = value1.split(StringUtil.HYPHEN);
        String[] temp2 = value2.split(StringUtil.HYPHEN);

        // Compare chapter number
        if (!temp1[0].equals(temp2[0])) {
            // Chapter number is a mismatch
            // Comparison by dividing a period separated the chapter number
            String[] chapNo1 = temp1[0].split("\\.");
            String[] chapNo2 = temp2[0].split("\\.");
            int len = chapNo1.length <= chapNo2.length ? chapNo1.length
                    : chapNo2.length;
            for (int i = 0; i < len; i++) {
                int ret = intComparator(Integer.valueOf(chapNo1[i]),
                        Integer.valueOf(chapNo2[i]));
                if (ret != 0) {
                    return ret;
                }
            }
            if (chapNo1.length < chapNo2.length) {
                // If the chapter number of Target 1 is short, Target 2 is large
                return -1;
            } else if (chapNo1.length > chapNo2.length) {
                // If the chapter number of Target 2 is shorter, Target 1 is
                // greater
                return 1;
            }
        } else {
            // Chapter numbers match
            // Compare lower No
            int len = temp1.length <= temp2.length ? temp1.length
                    : temp2.length;
            for (int i = 1; i < len; i++) {
                int ret = intComparator(Integer.valueOf(temp1[i]),
                        Integer.valueOf(temp2[i]));
                if (ret != 0) {
                    return ret;
                }
            }
            if (temp1.length < temp2.length) {
                // If Target 1 is short, Target 2 is large
                return -1;
            } else if (temp1.length > temp2.length) {
                // If Target 2 is a short, Target 1 is greater
                return 1;
            }
        }

        // Exact match
        return 0;
    }

    /**
     * Comparison of the number of lines.<br/>
     * 
     * @param value1
     *            Target 1
     * @param value2
     *            Target 2
     * @return Comparison result
     */
    protected int lineNumComparator(String value1, String value2) {
        int nRet = 0;
        String data1 = value1 != null && !value1.isEmpty() ? value1 : "-1";
        String data2 = value2 != null && !value2.isEmpty() ? value2 : "-1";
        boolean isNumber1 = true;
        boolean isNumber2 = true;
        int num1 = 0;
        int num2 = 0;
        try {
            num1 = Integer.valueOf(data1);
        } catch (NumberFormatException e) {
            isNumber1 = false;
        }
        try {
            num2 = Integer.valueOf(data2);
        } catch (NumberFormatException e) {
            isNumber2 = false;
        }
        // Both numbers
        if (isNumber1 && isNumber2) {
            nRet = intComparator(num1, num2);
        }
        // Both string
        else if (!isNumber1 && !isNumber2) {
            nRet = data1.compareTo(data2);
        }
        // Left-hand side is a string
        else if (!isNumber1) {
            nRet = 1;
        }
        // Right-hand side is a string
        else {
            nRet = -1;
        }
        return nRet;
    }
}
