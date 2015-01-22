/*
 * ConfirmItemChangeActionFactory.java
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
package tubame.portability.plugin.action;

import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.TreeItem;

import tubame.portability.model.ConfirmItemEnum;
import tubame.portability.model.JbmEditorEnum;
import tubame.portability.model.JbmEditorMigrationRow;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Generate a {@link ConfirmItemChangeAction} Action of visual confirmation
 * hearing item. <br/>
 * In addition, menu to be displayed in the cell when right-clicking also
 * generated.<br/>
 */
public class ConfirmItemChangeActionFactory {

    /**
     * Visual confirmation OK action
     */
    private static final Action eyeOKColorChange = new ConfirmItemChangeAction(
            JbmEditorEnum.VISUAL_CONFIRM_STATSU_ITEM.getCode(),
            ConfirmItemEnum.STATUS_OK,
            ResourceUtil.CONFIRM_ITEM_MENU_STRING_EYE_OK);
    /**
     * Visual confirmation NG action
     */
    private static final Action eyeNGColorChange = new ConfirmItemChangeAction(
            JbmEditorEnum.VISUAL_CONFIRM_STATSU_ITEM.getCode(),
            ConfirmItemEnum.STATUS_NG,
            ResourceUtil.CONFIRM_ITEM_MENU_STRING_EYE_NG);
    /**
     * Visual confirmation unconfirmed action
     */
    private static final Action eyeNonColorChange = new ConfirmItemChangeAction(
            JbmEditorEnum.VISUAL_CONFIRM_STATSU_ITEM.getCode(),
            ConfirmItemEnum.STATUS_NON,
            ResourceUtil.CONFIRM_ITEM_MENU_STRING_EYE_NON);
    /**
     * Confirmation hearing OK action
     */
    private static final Action hearingOKColorChange = new ConfirmItemChangeAction(
            JbmEditorEnum.HIARING_STATUS.getCode(), ConfirmItemEnum.STATUS_OK,
            ResourceUtil.CONFIRM_ITEM_MENU_STRING_HIARING_OK);
    /**
     * Confirmation hearing NG action
     */
    private static final Action hearingNGColorChange = new ConfirmItemChangeAction(
            JbmEditorEnum.HIARING_STATUS.getCode(), ConfirmItemEnum.STATUS_NG,
            ResourceUtil.CONFIRM_ITEM_MENU_STRING_HIARING_NG);
    /**
     * Confirmation hearing unconfirmed action
     */
    private static final Action hearingNonColorChange = new ConfirmItemChangeAction(
            JbmEditorEnum.HIARING_STATUS.getCode(), ConfirmItemEnum.STATUS_NON,
            ResourceUtil.CONFIRM_ITEM_MENU_STRING_HIARING_NON);

    /**
     * Constructor.<br/>
     * Copy the static area own instance.<br/>
     * 
     */
    private ConfirmItemChangeActionFactory() {
        // no operation
    }

    /**
     * Add Action to MenuManager.<br/>
     * Add a menu to determine the selected row and column from the coordinates
     * of the mouse click to display.<br/>
     * 
     * @param manager
     *            Menu Manager
     * @param viewer
     *            View
     * @param mousePoint
     *            Mouse click coordinate
     */
    public static void setAction(IMenuManager manager, TreeViewer viewer,
            Point mousePoint) {
        // Get the Cell object from the mouse click position of the current
        ViewerCell cell = viewer.getCell(mousePoint);
        if (cell == null) {
            return;
        }
        // Get the selected row
        TreeItem[] selectedItems = viewer.getTree().getSelection();

        // However does not show the same menu item check does not exist in all
        // the selection line
        Boolean isMenuView = null;
        for (TreeItem selectedItem : selectedItems) {
            if (isMenuView != null && !isMenuView.booleanValue()) {
                break;
            }
            // JbmEditorMigrationRow treeNode =
            // (JbmEditorMigrationRow)selectedItems[count].getData();
            JbmEditorMigrationRow row = (JbmEditorMigrationRow) selectedItem
                    .getData();
            if (row.getLevel() == JbmEditorMigrationRow.LEVEL_THIRD) {
                // Bottom
                isMenuView = ConfirmItemChangeActionFactory
                        .isConfirm(row, cell);
            } else if (row.getLevel() == JbmEditorMigrationRow.LEVEL_SECOND) {
                // Middle layer
                isMenuView = ConfirmItemChangeActionFactory
                        .setSecoundTreeViewConfirm(cell, row.getChildList());
            } else if (row.getLevel() == JbmEditorMigrationRow.LEVEL_FIRST) {
                // Top layer
                // Child can take
                for (JbmEditorMigrationRow childRow : row.getChildList()) {
                    isMenuView = ConfirmItemChangeActionFactory
                            .setSecoundTreeViewConfirm(cell,
                                    childRow.getChildList());
                }
            }
        }
        ConfirmItemChangeActionFactory.addAction(isMenuView, manager, cell);
        
    }

    /**
     * Inspecting the menu display the presence or absence of the child
     * hierarchy.<br/>
     * Get the grandchild hierarchy from the Child hierarchy, <br/>
     * get the menu display in the presence or absence of a grandchild
     * hierarchy.<br/>
     * 
     * @param cell
     *            Target cell
     * @param childList
     *            Data
     * @return true:Visual / hearing confirmation item false:Not a visual /
     *         hearing item
     */
    private static Boolean setSecoundTreeViewConfirm(ViewerCell cell,
            List<JbmEditorMigrationRow> childList) {
        Boolean isMenuView = null;
        for (JbmEditorMigrationRow row : childList) {
            isMenuView = ConfirmItemChangeActionFactory.isConfirm(row, cell);
        }
        return isMenuView;
    }

    /**
     * Add Action.<br/>
     * For visual confirmation / hearing items, target column is added Action.<br/>
     * 
     * @param isMenuView
     *            Add existence flag
     * @param manager
     *            Menu Manager
     * @param cell
     *            Click the target cell
     */
    private static void addAction(Boolean isMenuView, IMenuManager manager,
            ViewerCell cell) {
        if (isMenuView != null && isMenuView.booleanValue()) {
            if (JbmEditorEnum.VISUAL_CONFIRM_STATSU_ITEM.getCode() == cell
                    .getColumnIndex()) {
                manager.add(ConfirmItemChangeActionFactory.eyeNGColorChange);
                manager.add(ConfirmItemChangeActionFactory.eyeOKColorChange);
                manager.add(ConfirmItemChangeActionFactory.eyeNonColorChange);
            }
            if (JbmEditorEnum.HIARING_STATUS.getCode() == cell.getColumnIndex()) {
                manager.add(ConfirmItemChangeActionFactory.hearingNGColorChange);
                manager.add(ConfirmItemChangeActionFactory.hearingOKColorChange);
                manager.add(ConfirmItemChangeActionFactory.hearingNonColorChange);
            }
        }
    }

    /**
     * The return TRUE if the visual / hearing item, return FALSE if visual /
     * hearing fee.<br/>
     * 
     * @param item
     *            Line data
     * @param cell
     *            Target cell data
     * @return true:Confirmation item false:Unacknowledged item
     */
    private static boolean isConfirm(JbmEditorMigrationRow item, ViewerCell cell) {
        // Visual confirmation
        if (JbmEditorEnum.VISUAL_CONFIRM_STATSU_ITEM.getCode() == cell
                .getColumnIndex()) {
            if (item.isCheckEye()) {
                return true;
            }
        }
        // Confirmation hearing
        if (JbmEditorEnum.HIARING_STATUS.getCode() == cell.getColumnIndex()) {
            if (item.isHearing()) {
                return true;
            }
        }
        return false;
    }
}
