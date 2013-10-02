/*
 * JbmEditorComparator.java
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
package tubame.portability.plugin.editor;

import tubame.portability.model.DifficultyEnum;
import tubame.portability.model.JbmEditorEnum;
import tubame.portability.plugin.action.ConfirmItemChangeAction;

/**
 * Sort implementation class of JBM editors.<br/>
 * Comparison of each column.<br/>
 */
public class JbmEditorComparator extends AbstractEditorComparator {

    /**
     * Serial version ID
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.<br/>
     * 
     * @param asc
     * @see {@link AbstractEditorComparator#AbstractEditorComparator(boolean, String, int)}
     * @param column
     * @see {@link AbstractEditorComparator#AbstractEditorComparator(boolean, String, int)}
     * @param columnIndex
     * @see {@link AbstractEditorComparator#AbstractEditorComparator(boolean, String, int)}
     */
    public JbmEditorComparator(boolean asc, String column, int columnIndex) {
        super(asc, column, columnIndex);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int jbmEditorCompare(int index, String value1, String value2) {
        int nRet = 0;
        // "No"
        if (index == JbmEditorEnum.INDEX_NO.getCode()) {
            return numComparator(value1, value2);
        }
        // "Count"
        if (index == JbmEditorEnum.HIT_NUM.getCode()) {
            return intComparator(Integer.valueOf(value1),
                    Integer.valueOf(value2));
        }
        // "Degree of difficulty"
        if (index == JbmEditorEnum.DIFFICULTY.getCode()) {
            return intComparator(DifficultyEnum.get(value1).getSortWeight(),
                    DifficultyEnum.get(value2).getSortWeight());
        }
        // Visual confirmation" or "confirmation hearing"
        if (index == JbmEditorEnum.VISUAL_CONFIRM_STATSU_ITEM.getCode()
                || index == JbmEditorEnum.HIARING_STATUS.getCode()) {
            int data1 = 2;
            int data2 = 2;
            if (ConfirmItemChangeAction.OK.equals(value1)) {
                data1 = 1;
            }
            if (ConfirmItemChangeAction.OK.equals(value2)) {
                data2 = 1;
            }
            if (ConfirmItemChangeAction.NG.equals(value1)) {
                data1 = 0;
            }
            if (ConfirmItemChangeAction.NG.equals(value2)) {
                data2 = 0;
            }
            return intComparator(data1, data2);
        }
        // Number of lines
        if (index == JbmEditorEnum.LINE_NUM.getCode()) {
            return lineNumComparator(value1, value2);
        }
        return nRet;
    }
}
