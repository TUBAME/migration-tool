/*
 * WSearchComparator.java
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

import tubame.wsearch.models.MigrationEditorRow;
import tubame.wsearch.models.WSearchCsvEnum;
import tubame.wsearch.models.WSearchEditorMigrationRow;

/**
 * It is a comparison class of general-purpose search result data.<br/>
 */
public class WSearchComparator implements Comparator<MigrationEditorRow>,
        Serializable {

    private static final long serialVersionUID = 260801264826833820L;
    /**
     * Ascending / descending order
     */
    private boolean asc;
    /**
     * Column index
     */
    private int columnIndex;

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param isAsc
     *            Ascending or descending order
     * @param columnIndex
     *            Column number
     */
    public WSearchComparator(boolean isAsc, int columnIndex) {
        super();
        this.asc = isAsc;
        this.columnIndex = columnIndex;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int compare(MigrationEditorRow o1, MigrationEditorRow o2) {
        int nRet = 0;
        WSearchEditorMigrationRow editorMigrationRow = (WSearchEditorMigrationRow) o1;
        WSearchEditorMigrationRow editorMigrationRow2 = (WSearchEditorMigrationRow) o2;

        if (columnIndex == WSearchCsvEnum.TOKEN_INDEX_CATEGORY.getIndex()) {
            nRet = jbmEditorCompare(columnIndex,
                    editorMigrationRow.getCategory(),
                    editorMigrationRow2.getCategory());
        }

        // Package
        if (columnIndex == WSearchCsvEnum.TOKEN_INDEX_PACKAGE.getIndex()) {

            nRet = jbmEditorCompare(columnIndex,
                    editorMigrationRow.getPackage(),
                    editorMigrationRow2.getPackage());
        }
        if (!asc) {
            nRet = nRet * -1;
        }
        return nRet;
    }

    /**
     * It is the comparison process.<br/>
     * 
     * @param columnIndex2
     *            It is the comparison process.
     * @param categoryName1
     *            Category name
     * @param categoryName2
     *            Category name
     * @return The negative integer if category name of the first is less than
     *         the category name of the second, 0 if both are equal,The positive
     *         integer if category name of the first is greater than the
     *         category name of the second
     */
    private int jbmEditorCompare(int columnIndex2, String categoryName1,
            String categoryName2) {
        return String.valueOf(categoryName1).compareToIgnoreCase(
                String.valueOf(categoryName2));
    }
}