/*
 * WSearchViewComparator.java
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
package tubame.wsearch.ui.view;

import java.io.Serializable;
import java.util.Comparator;

import org.eclipse.swt.widgets.TreeItem;
import tubame.wsearch.models.WSearchCsvEnum;

/**
 * It is a comparison class of general-purpose search results view.<br/>
 */
public class WSearchViewComparator implements Comparator<TreeItem>,
        Serializable {

    /**
     * Serialization key
     */
    private static final long serialVersionUID = -1588396424194043511L;
    /**
     * Ascending / descending order
     */
    private boolean asc;
    /**
     * Column Index
     */
    private int columnIndex;

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param isAsc
     *            Ascending or descending order
     * @param columnIndex
     *            Column index
     */
    public WSearchViewComparator(boolean isAsc, int columnIndex) {
        super();
        this.asc = isAsc;
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
     * It is the comparison process.<br/>
     * 
     * @param index
     *            Index
     * @param value1
     *            Compare
     * @param value2
     *            Compare
     * @return The negative integer if the first comparison is less than the
     *         comparison of the second, 0 if both are equal, the positive
     *         integer if the first comparison is greater than the comparison of
     *         the second
     */
    private int jbmEditorCompare(int index, String value1, String value2) {
        int nRet = 0;

        // Result
        if (index == WSearchCsvEnum.TOKEN_INDEX_STATUS.getIndex()) {
            return intComparetor(value1, value2);
        }
        return nRet;
    }

    /**
     * It is a comparison of type int.<br/>
     * 
     * @param value1
     *            Target 1
     * @param value2
     *            Target 2
     * @return Comparison result
     */
    protected int intComparetor(String value1, String value2) {
        return String.valueOf(value1).compareToIgnoreCase(
                String.valueOf(value2));
    }

    /**
     * It is divided into upper and lower values of No, and then return the
     * value obtained by multiplying each weight.<br/>
     * 
     * @param value
     *            Target string
     * @return Value converted to a string int value
     */
    protected int getIndexNo(String value) {
        String[] temp = value.split("-");
        return Integer.valueOf(temp[0]).intValue() * 1000
                + Integer.valueOf(temp[1]).intValue();
    }
}