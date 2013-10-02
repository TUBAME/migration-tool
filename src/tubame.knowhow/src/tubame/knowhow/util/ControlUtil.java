/*
 * ControlUtil.java
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
package tubame.knowhow.util;

import java.util.Map;

import tubame.common.util.CmnStringUtil;
import tubame.knowhow.biz.model.DefineEnumOperator;

import org.eclipse.jface.resource.JFaceResources;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IFormColors;
import org.eclipse.ui.forms.widgets.FormToolkit;

/**
 * Utility class of display control system.<br/>
 */
public final class ControlUtil {
    /**
     * Constructor.<br/>
     * 
     */
    private ControlUtil() {
        // no operation
    }

    /**
     * If a match with a value in the combo box within the specified value, <br/>
     * and returns the order of the items. It returns -1 if no match was found.<br/>
     * 
     * @param combo
     *            Combo box
     * @param setValue
     *            The value to check (string)
     * @return Item order in which they appear in the combo box (-1 If not
     *         stored)
     */
    public static int getComboNo(Combo combo, String setValue) {
        int comboNo = -1;
        for (int count = 0; count < combo.getItems().length; count++) {
            if (combo.getItem(count).equals(setValue)) {
                comboNo = count;
                break;
            }
        }
        return comboNo;
    }

    /**
     * Get GridData of width, length width full. <br/>
     * Specifies the presence or absence of space in the argument.<br/>
     * 
     * @param setHorizontalSpace
     *            Presence or absence of horizontal space
     * @param setVerticalSpace
     *            Presence or absence of vertical space
     * @return Grit data created
     */
    public static GridData getFillGridData(boolean setHorizontalSpace,
            boolean setVerticalSpace) {
        GridData gridData = new GridData();
        gridData.grabExcessHorizontalSpace = setHorizontalSpace;
        gridData.grabExcessVerticalSpace = setVerticalSpace;
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;

        return gridData;
    }

    /**
     * Get GridData in the upper left-justified.<br/>
     * 
     * @return Grit data created
     */
    public static GridData getLeftTopGridData() {
        GridData gridData = new GridData(SWT.LEFT, SWT.TOP, false, true);
        gridData.horizontalAlignment = GridData.FILL;

        return gridData;
    }

    /**
     * Get GridData on the packed.<br/>
     * 
     * @return Grit data created
     */
    public static GridData getTopGridData() {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.BEGINNING;
        gridData.verticalAlignment = SWT.TOP;
        return gridData;
    }

    /**
     * Get GridData of specified width and over stuffed.<br/>
     * 
     * @param width
     *            Width
     * @return Grit data created
     */
    public static GridData getSizingHorizontalTopGridData(int width) {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = SWT.BEGINNING;
        gridData.verticalAlignment = SWT.TOP;
        gridData.widthHint = width;
        return gridData;
    }

    /**
     * Get GridData three rows of text.<br/>
     * 
     * @param text
     *            Text area
     * @return Grit data created
     */
    public static GridData getThreeColumnGridData(Text text) {
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        gridData.horizontalAlignment = GridData.FILL;
        gridData.verticalAlignment = GridData.FILL;
        // CheckStyle Magic number
        gridData.heightHint = text.getLineHeight() * 3;
        return gridData;
    }

    /**
     * Get GridData of 3Grid combined width. <br/>
     * Width is set manually.<br/>
     * 
     * @param width
     *            The length of the width
     * @return Grit data created
     */
    public static GridData getThreeGridWidthData(int width) {
        GridData gridData = new GridData();
        // CheckStyle Magic number
        gridData.horizontalSpan = 3;
        gridData.horizontalAlignment = SWT.BEGINNING;
        gridData.widthHint = width;
        gridData.verticalAlignment = GridData.FILL;
        return gridData;
    }

    /**
     * Get GridData of 2Grid combined width. <br/>
     * Width is set manually.<br/>
     * 
     * @param width
     *            The length of the width
     * @return Grit data created
     */
    public static GridData getTwoGridWidthData(int width) {
        GridData gridData = new GridData();
        gridData.horizontalSpan = 2;
        gridData.horizontalAlignment = SWT.BEGINNING;
        gridData.widthHint = width;
        gridData.verticalAlignment = GridData.FILL;
        return gridData;
    }

    /**
     * Get GridData specified in pixels width, length width.<br/>
     * 
     * @param widthHint
     *            Width
     * @param heightHint
     *            heightHint
     * @return Grit data created
     */
    public static GridData getSizingGridData(int widthHint, int heightHint) {
        GridData gridData = new GridData();
        gridData.widthHint = widthHint;
        gridData.heightHint = heightHint;
        return gridData;
    }

    /**
     * Get GridData specified in pixels width.<br/>
     * 
     * @param widthHint
     *            Width
     * @return Grit data created
     */
    public static GridData getSizingHorizontalGridData(int widthHint) {
        GridData gridData = new GridData();
        gridData.widthHint = widthHint;
        return gridData;
    }

    /**
     * Specify in pixels width, get GridData that in Top the height of the
     * display position.<br/>
     * 
     * @param widthHint
     *            Width
     * @return Grit data created
     */
    public static GridData getSizingHorizontalGridToTopData(int widthHint) {
        GridData gridData = new GridData();
        gridData.verticalAlignment = SWT.BEGINNING;
        gridData.widthHint = widthHint;
        return gridData;
    }

    /**
     * Get GridData specified in pixels vertical width. <br/>
     * Extend the full width of the parent control width.<br/>
     * 
     * @param heightHint
     *            heightHint
     * @return Grit data created
     */
    public static GridData getSizingVerticalGridData(int heightHint) {
        GridData gridData = new GridData();
        gridData.horizontalAlignment = GridData.FILL;
        gridData.grabExcessHorizontalSpace = true;
        gridData.heightHint = heightHint;
        return gridData;
    }

    /**
     * Get GridData specified by the child hierarchy items more width.<br/>
     * 
     * @return Grit data created
     */
    public static GridData getAutoSizingHorizontalGridData() {
        GridData gridData = new GridData();
        gridData.verticalAlignment = SWT.BEGINNING;
        gridData.horizontalAlignment = GridData.FILL_HORIZONTAL;
        return gridData;
    }

    /**
     * Get the group that set the Text.<br/>
     * 
     * @param parent
     *            Parent composite
     * @param style
     *            Style of group
     * @param groupName
     *            String to setText
     * @return Group you created
     */
    public static Group createGroup(Composite parent, int style,
            String groupName) {
        Group group = new Group(parent, style);
        group.setText(groupName);
        return group;
    }

    /**
     * Add Item to Combo box.<br/>
     * 
     * @param enums
     *            List of Enum (array)
     * @param combo
     *            Combo box
     */
    public static void addComboBoxItem(DefineEnumOperator[] enums, Combo combo) {
        for (DefineEnumOperator enumItem : enums) {
            combo.add(enumItem.getName());
        }
    }

    /**
     * Create a label (blue, bold, character is large) in the same font as the
     * title of the form.<br/>
     * 
     * @param parent
     *            Parent composite
     * @param toolkit
     *            FormToolkit
     * @param title
     *            Title to be displayed on the label
     * @return Label that you created
     */
    public static Label createTitleLabel(Composite parent, FormToolkit toolkit,
            String title) {
        Label label = toolkit.createLabel(parent, title);
        label.setForeground(toolkit.getColors().getColor(IFormColors.TITLE));
        label.setFont(JFaceResources.getHeaderFont());
        return label;
    }

    /**
     * Add to combo box the search module information.<br/>
     * 
     * @param pythonModule
     *            Combo
     * @param searchModuleMap
     *            searchModuleMap
     */
    public static void addPythonModule(Combo pythonModule,
            Map<String, String> searchModuleMap) {
        pythonModule.add(CmnStringUtil.EMPTY);
        for (Map.Entry<String, String> entry : searchModuleMap.entrySet()) {
            pythonModule.add(entry.getKey());
        }
    }
}
