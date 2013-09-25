/*
 * JbmEditorSortListener.java
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

import java.util.Arrays;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.tubame.portability.model.JbmEditorMigrationRow;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * The ascending or descending order for the column below.<br/>
 * <br/>
 * No<br/>
 * Hits<br/>
 * Degree of difficulty (Low > Middle > High > Unknown)<br/>
 * Visual confirmation status (unconfirmed> confirmed (on transplant)> Confirmed
 * (porting required))<br/>
 * Bearings situation (unconfirmed> confirmed (on transplant)> Confirmed
 * (porting required))<br/>
 */
public class JbmEditorSortListener implements SelectionListener {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(JbmEditorSortListener.class);

    /**
     * Sort type
     */
    private int sortType = -1;

    /**
     * Last column sort value
     */
    private String previousValue;

    /**
     * View
     */
    private final MigrationEditorOperation editorOperation;

    /**
     * Constructor.<br/>
     * 
     * @param editorOperation
     *            Editor screen
     */
    public JbmEditorSortListener(MigrationEditorOperation editorOperation) {
        super();
        this.editorOperation = editorOperation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        TreeColumn column = (TreeColumn) e.widget;
        LOGGER.debug("sort:" + column);
        Tree tree = column.getParent();
        TreeItem[] treeItems = tree.getItems();
        TreeColumn[] columns = tree.getColumns();
        tree.setSortColumn(column);
        int numOfColumns = columns.length;
        int columnIndex = findColumnIndex(columns, column, numOfColumns);
        // Determination of the sort type
        setSortType(column.getText());
        // Sort run
        sort(column, tree, treeItems, numOfColumns, columnIndex);
    }

    /**
     * Sorting of each column.<br/>
     * After sorting, reflected in screen.<br/>
     * 
     * @param column
     *            Target column
     * @param tree
     *            Tree
     * @param treeItems
     *            Tree items
     * @param numOfColumns
     *            Whole number of columns
     * @param columnIndex
     *            Target column Index
     */
    private void sort(TreeColumn column, Tree tree, TreeItem[] treeItems,
            int numOfColumns, int columnIndex) {
        boolean isAsc = false;
        if (sortType == SWT.UP) {
            isAsc = true;
        }
        tree.setSortDirection(sortType);
        Arrays.sort(treeItems, new JbmEditorComparator(isAsc, column.getText(),
                columnIndex));

        TreeViewer treeViewer = editorOperation.getTreeViewer();
        // Reflected in the tree treeItems that are sorted
        for (int count = 1; count < treeItems.length; count++) {
            TreeItem item = new TreeItem(tree, SWT.NONE);
            item.setText(getColumnValues(treeItems[count], numOfColumns));
            item.setImage(getColumnImages(treeItems[count], numOfColumns));
            item.setFont(treeItems[count].getFont());

            swapTreeItemChild(treeItems, item, count);
            treeItems[count].dispose();
        }
        // After being sorted, since the the first level only is displayed,
        // is performed the first level minute loop to re-display
        @SuppressWarnings("unchecked")
        List<JbmEditorMigrationRow> topList = (List<JbmEditorMigrationRow>) editorOperation
                .getTreeViewer().getInput();
        for (JbmEditorMigrationRow row : topList) {
            treeViewer.refresh(row);
        }
    }

    /**
     * Set child level when sorting.<br/>
     * 
     * @param treeItems
     *            Tree items
     * @param treeItemParent
     *            Parent tree
     * @param count
     *            Counter value
     */
    private void swapTreeItemChild(TreeItem[] treeItems,
            TreeItem treeItemParent, int count) {
        treeItemParent.setData(treeItems[count].getData());
        new TreeItem(treeItemParent, SWT.NONE);
        return;
    }

    /**
     * Get a column value display information in the tree.<br/>
     * 
     * @param treeItem
     *            Tree items
     * @param numOfColumns
     *            Column total number
     * @return One row of data
     */
    private String[] getColumnValues(TreeItem treeItem, int numOfColumns) {
        String[] values = new String[numOfColumns];
        for (int count = 0; count < numOfColumns; count++) {
            values[count] = treeItem.getText(count);
        }
        return values;
    }

    /**
     * Get the column display image information of the tree.<br/>
     * 
     * @param treeItem
     *            Tree items
     * @param numOfColumns
     *            Column total number
     * @return One row of data
     */
    private Image[] getColumnImages(TreeItem treeItem, int numOfColumns) {
        Image[] values = new Image[numOfColumns];
        for (int i = 0; i < numOfColumns; i++) {
            values[i] = treeItem.getImage(i);
        }

        return values;
    }

    /**
     * Set the sort type.<br/>
     * Set member variable (sortType / previousValue) the type of sort to do
     * next.<br/>
     * 
     * @param clickTargetName
     *            Click column name
     */
    private void setSortType(String clickTargetName) {
        if (clickTargetName.equals(previousValue)) {
            // If the same
            if (sortType == -1) {
                // Descending order if it is the first click
                sortType = SWT.UP;
            } else {
                if (sortType == SWT.DOWN) {
                    sortType = SWT.UP;
                } else {
                    sortType = SWT.DOWN;
                }
            }
        } else {
            // The descending order if not the same as the previous column
            sortType = SWT.UP;
        }
        previousValue = clickTargetName;
    }

    /**
     * Get the Index column number from the name of the column column.<br/>
     * 
     * @param columns
     *            All columns
     * @param column
     *            Target column
     * @param numOfColumns
     *            Whole number of columns
     * @return Index number
     */
    private int findColumnIndex(TreeColumn[] columns, TreeColumn column,
            int numOfColumns) {
        int index = 0;
        for (int count = 0; count < numOfColumns; count++) {
            if (column.equals(columns[count])) {
                index = count;
                break;
            }
        }
        return index;
    }
}
