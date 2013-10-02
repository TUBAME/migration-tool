/*
 * WSearchSortListener.java
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

import java.util.Arrays;
import java.util.Collections;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import tubame.wsearch.models.MigrationEditorRow;
import tubame.wsearch.models.WSearchEditorMigrationRow;

/**
 * It is a general-purpose search result view for sort listener.<br/>
 */
public class WSearchSortListener implements SelectionListener {

    /**
     * Last column sort value
     */
    private String previousValue;

    /**
     * Tree view in the general-purpose search results view
     */
    private TreeViewer treeViewer;

    /**
     * Sort type
     */
    private int sortType = -1;

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param viewer
     *            Viewer
     */
    public WSearchSortListener(TreeViewer viewer) {
        this.treeViewer = viewer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        TreeColumn column = (TreeColumn) e.widget;
        Tree tree = treeViewer.getTree();
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
     * Sorting.<br/>
     * 
     * @param column
     *            Tree column
     * @param tree
     *            Tree
     * @param treeItems
     *            Tree items
     * @param numOfColumns
     *            Number of columns
     * @param columnIndex
     *            Column index
     */
    private void sort(TreeColumn column, Tree tree, TreeItem[] treeItems,
            int numOfColumns, int columnIndex) {
        this.treeViewer.getTree().setRedraw(false);
        this.treeViewer.collapseAll();

        boolean isAsc = false;
        if (sortType == SWT.UP) {
            isAsc = true;
        }
        tree.setSortDirection(sortType);

        if (columnIndex == 0) {
            Arrays.sort(treeItems,
                    new WSearchViewComparator(isAsc, columnIndex));
        } else {
            sortCildrenItem(treeItems, isAsc, columnIndex);
        }
        TreeViewer treeViewer = this.treeViewer;

        // Reflected in the tree treeItems that are sorted.
        for (int count = 1; count < treeItems.length; count++) {
            TreeItem item = new TreeItem(tree, SWT.NONE);
            item.setText(getColumnValues(treeItems[count], numOfColumns));
            item.setImage(getColumnImages(treeItems[count], numOfColumns));
            item.setFont(treeItems[count].getFont());
            swapTreeItemChild(treeItems, item, count);
            treeItems[count].dispose();
        }

        // Expand All
        treeViewer.expandAll();
        treeViewer.getTree().setRedraw(true);
    }

    /**
     * Do sort of child elements.<br/>
     * 
     * @param treeItems
     *            Tree items
     * @param isAsc
     *            Ascending or descending order
     * @param columnIndex
     *            Column index
     */
    private void sortCildrenItem(TreeItem[] treeItems, boolean isAsc,
            int columnIndex) {

        for (TreeItem item : treeItems) {
            MigrationEditorRow editorRow = (MigrationEditorRow) item.getData();
            List<WSearchEditorMigrationRow> migrationRows = editorRow
                    .getChildren();
            if (columnIndex == 1) {
                Collections.sort(migrationRows, new WSearchComparator(isAsc,
                        columnIndex));
                treeViewer.refresh();
            } else if (columnIndex == 2) {
                for (WSearchEditorMigrationRow editorMigrationRow : migrationRows) {
                    Collections.sort(editorMigrationRow.getChildList(),
                            new WSearchComparator(isAsc, columnIndex));
                    treeViewer.refresh();
                }
            }
        }
    }

    /**
     * Replaced the child elements.<br/>
     * 
     * @param treeItems
     *            Subelement
     * @param treeItemParent
     *            Parent element
     * @param count
     *            The index of the child element
     */
    private void swapTreeItemChild(TreeItem[] treeItems,
            TreeItem treeItemParent, int count) {
        treeItemParent.setData(treeItems[count].getData());
        new TreeItem(treeItemParent, SWT.NONE);
        return;
    }

    /**
     * Get the value of the column.<br/>
     * 
     * @param treeItem
     *            Tree items
     * @param numOfColumns
     *            Number of columns
     * @return Value of column
     */
    private String[] getColumnValues(TreeItem treeItem, int numOfColumns) {
        String[] values = new String[numOfColumns];
        for (int count = 0; count < numOfColumns; count++) {
            values[count] = treeItem.getText(count);
        }
        return values;
    }

    /**
     * Get an image of the column.<br/>
     * 
     * @param treeItem
     *            Tree items
     * @param numOfColumns
     *            Number of columns
     * @return Image of the column
     */
    private Image[] getColumnImages(TreeItem treeItem, int numOfColumns) {
        Image[] values = new Image[numOfColumns];
        for (int count = 0; count < numOfColumns; count++) {
            values[count] = treeItem.getImage(count);
        }
        return values;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // no operation
    }

    /**
     * Set the sort type.<br/>
     * Set the member variable (sortType / previousValue) the type of sort to do
     * next.<br/>
     * 
     * @param clickTargetName
     *            Click column name
     */
    private void setSortType(String clickTargetName) {
        if (clickTargetName.equals(previousValue)) {

            // If the same
            if (sortType == -1) {

                // Descending order If you click for the first time
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
     * Get the column Index number.<br/>
     * Get the column Index number from the name of the column column.<br/>
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