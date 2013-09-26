/*
 * TableTagDialog.java
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
package org.tubame.knowhow.plugin.ui.dialog;

import org.tubame.common.util.CmnStringUtil;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Spinner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.util.ControlUtil;
import org.tubame.knowhow.util.resource.ResourceUtil;

/**
 * Class that specifies the row / column of the table tag when inserting Know
 * how tag.<br/>
 */
public class TableTagDialog extends Dialog {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TableTagDialog.class);
    /** Number of lines */
    private int rowIndex;
    /** Number of Columns */
    private int entryIndex;
    /** Line */
    private Spinner row;
    /** Column */
    private Spinner entry;

    /**
     * Constructor.<br/>
     * 
     * @param parent
     *            Shell
     */
    public TableTagDialog(Shell parent) {
        super(parent);
        TableTagDialog.LOGGER.debug("[parent]" + parent);
    }

    /**
     * {@inheritDoc}
     */
    protected Point getInitialSize() {
        // CheckStyle magic number
        return new Point(400, 200);
    }

    /**
     * {@inheritDoc}
     */
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(ResourceUtil.insertTableTag);
    }

    /**
     * {@inheritDoc}
     */
    protected Control createDialogArea(Composite parent) {

        Composite composite = (Composite) super.createDialogArea(parent);
        Group rowAndEntryGroup = new Group(composite, SWT.SHADOW_NONE);
        rowAndEntryGroup.setText(ResourceUtil.rowAndEntry);
        rowAndEntryGroup
                .setLayoutData(ControlUtil.getFillGridData(true, false));
        // CheckStyle magic number
        rowAndEntryGroup.setLayout(new GridLayout(4, false));
        createRowAndEntryArea(rowAndEntryGroup);

        return composite;
    }

    /**
     * Create the input area of the rows and columns.<br/>
     * 
     * @param rowAndEntryGroup
     *            Group
     */
    private void createRowAndEntryArea(Group rowAndEntryGroup) {
        Label rowLabel = new Label(rowAndEntryGroup, SWT.NULL);
        rowLabel.setText(ResourceUtil.row);

        row = new Spinner(rowAndEntryGroup, SWT.BORDER);
        row.setMinimum(1);
        row.setSelection(1);
        row.setLayoutData(ControlUtil.getTopGridData());

        Label entryLabel = new Label(rowAndEntryGroup, SWT.NULL);
        entryLabel.setText(ResourceUtil.entry);

        entry = new Spinner(rowAndEntryGroup, SWT.BORDER);
        entry.setMinimum(1);
        entry.setSelection(1);
        entry.setLayoutData(ControlUtil.getTopGridData());
    }

    /**
     * {@inheritDoc}
     */
    public void okPressed() {
        TableTagDialog.LOGGER.debug(CmnStringUtil.EMPTY);
        rowIndex = row.getSelection();
        entryIndex = entry.getSelection();
        super.okPressed();
    }

    /**
     * Get the number of rows.<br/>
     * 
     * @return Number of lines
     */
    public int getRowIndex() {
        return rowIndex;
    }

    /**
     * The retrieve the column number.<br/>
     * 
     * @return Number of Columns
     */
    public int getEntryIndex() {
        return entryIndex;
    }
}
