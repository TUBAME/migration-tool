/*
 * ConvertLabelProvider.java
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
package org.tubame.portability.plugin.view;

import org.eclipse.jface.viewers.CellLabelProvider;
import org.eclipse.jface.viewers.ViewerCell;
import org.tubame.portability.model.generated.model.MigrationItem;
import org.tubame.portability.model.generated.model.MigrationTarget;
import org.tubame.portability.util.StringUtil;
import org.tubame.portability.util.resource.ResourceUtil;

/**
 * Label provider of conversion star status view screen.<br/>
 */
public class ConvertLabelProvider extends CellLabelProvider {
    /**
     * @see CellLabelProvider#getToolTipDisplayDelayTime(Object)
     */
    private static final int TOOL_TIP_DISPLAY_DELAY_TIME = 2000;
    /**
     * @see CellLabelProvider#getToolTipTimeDisplayed(Object)
     */
    private static final int TOO_TIP_TIME_DISPLAYED = 5000;

    /**
     * Screen display status (conversion OK)
     */
    private static final String CONVERT_OK = ResourceUtil.CONVERT_OK;

    /**
     * Screen display status (conversion NG)
     */
    private static final String CONVERT_NG = ResourceUtil.CONVERT_NG;

    /**
     * Index number column to display the tool tip
     */
    private int toolTipColumnIndex;

    /**
     * Displays information of MigrationItem.<br/>
     * 
     * @param migrationItem
     *            MigrationItem
     * @param columnIndex
     *            Index column number
     * @return String
     */
    private String parentGetColumnText(MigrationItem migrationItem,
            int columnIndex) {
        switch (columnIndex) {
        case 0:
            return migrationItem.getNumber();
        case 1:
            return migrationItem.getConvertType().toString();
        default:
            return StringUtil.EMPTY;
        }
    }

    /**
     * Displays information of MigrationTarget.<br/>
     * 
     * @param migrationTarget
     *            MigrationTarget
     * @param columnIndex
     *            Index column number
     * @return String
     */
    private String childGetColumnText(MigrationTarget migrationTarget,
            int columnIndex) {
        switch (columnIndex) {
        case 1:
            return migrationTarget.getTargetFile();
        case 2:
            return migrationTarget.getConvertedFile();
        case 3:
            if (migrationTarget.isConvert()) {
                return ConvertLabelProvider.CONVERT_OK;
            }
            return ConvertLabelProvider.CONVERT_NG;
        default:
            return StringUtil.EMPTY;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(ViewerCell cell) {
        // Set the string to be displayed in the cell
        String text = StringUtil.EMPTY;
        Object element = cell.getElement();
        if (element instanceof MigrationItem) {
            MigrationItem migrationItem = (MigrationItem) element;
            text = parentGetColumnText(migrationItem, cell.getColumnIndex());
        } else if (element instanceof MigrationTarget) {
            MigrationTarget migrationTarget = (MigrationTarget) element;
            text = childGetColumnText(migrationTarget, cell.getColumnIndex());
        }
        cell.setText(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getToolTipText(Object element) {
        // the tool tip of the conversion item is displayed, and display the
        // converted file
        String view = super.getToolTipText(element);

        if (element instanceof MigrationTarget) {
            if (toolTipColumnIndex == 1) {
                return ((MigrationTarget) element).getTargetFile();
            } else if (toolTipColumnIndex == 2) {
                return ((MigrationTarget) element).getConvertedFile();
            }
        }
        return view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getToolTipDisplayDelayTime(Object object) {
        return ConvertLabelProvider.TOOL_TIP_DISPLAY_DELAY_TIME;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public int getToolTipTimeDisplayed(Object object) {
        return ConvertLabelProvider.TOO_TIP_TIME_DISPLAYED;
    }

    /**
     * Set the column Index number to display the tool tip.<br/>
     * 
     * @param index
     *            Index column number
     */
    public void setToolTipIndex(int index) {
        toolTipColumnIndex = index;
    }
}
