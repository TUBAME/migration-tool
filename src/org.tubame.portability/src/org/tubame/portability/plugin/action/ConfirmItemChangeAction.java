/*
 * ConfirmItemChangeAction.java
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
package org.tubame.portability.plugin.action;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.tubame.portability.model.ConfirmItemEnum;
import org.tubame.portability.model.JbmEditorEnum;
import org.tubame.portability.model.JbmEditorMigrationRow;
import org.tubame.portability.plugin.editor.MigrationEditorOperation;
import org.tubame.portability.util.PluginUtil;
import org.tubame.portability.util.resource.ResourceUtil;

/**
 * Provide a status change of visual confirmation hearing item.<br/>
 * Change status visual confirmation of item Editor on (hearing items).<br/>
 * If all status of the child element was "confirmed", the status of the parent
 * element also change.<br/>
 * Contents of the status are as follows.<br/>
 * <br/>
 * blank: Item does not need to be visually / hearing confirmation<br/>
 * X (Bold): It means that the items to be displayed if there is at least one X
 * in the second hierarchy, <br/>
 * visually / hearing confirmation still exists.* only the first hierarchyV O
 * (Bold): It means that the items to be displayed when the second hierarchy was
 * all O,<br/>
 * visually / hearing check is completed. * only the first hierarchy<br/>
 * X: The displayed if the unconfirmed status is there is at least one in the
 * third tier, <br/>
 * * only the second hierarchy<br/>
 * O: The displayed if the unconfirmed status does not exist in the third layer. <br/>
 * * Only the second hierarchy<br/>
 * Confirmed (on transplantation): The items that should be visually / hearing
 * confirmed, <br/>
 * the display if you have (on transplantation) visually confirmed.<br/>
 * Confirmed (porting required): in the field that need to be visually / hearing
 * confirmed, <br/>
 * the display if you have to (porting required) visually confirmed.<br/>
 * The unconfirmed: in the field that need to be visually / hearing confirmed, <br/>
 * the display on the unconfirmed state.<br/>
 * 
 */
public class ConfirmItemChangeAction extends Action {
    /**
     * Display text for the confirmation item OK when
     */
    public static final String OK = ResourceUtil.JBM_VIEW_STATUS_OK;
    /**
     * Display text for the confirmation item when NG
     */
    public static final String NG = ResourceUtil.JBM_VIEW_STATUS_NG;
    /**
     * Index column number of the target
     */
    private final int columnIndex;
    /**
     * Column color information
     */
    private final ConfirmItemEnum confirmItemEnum;

    /**
     * Constructor.<br/>
     * Create Action of visual confirmation / hearing item.<br/>
     * 
     * @param index
     *            Index column number
     * @param confirmItemEnum
     *            Column color information
     * @param text
     *            Display text
     */
    public ConfirmItemChangeAction(int index, ConfirmItemEnum confirmItemEnum,
            String text) {
        super();
        columnIndex = index;
        this.confirmItemEnum = confirmItemEnum;
        super.setText(text);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        // Get a current Active Editor
        MigrationEditorOperation editor = (MigrationEditorOperation) PluginUtil
                .getActiveEditor();
        // Get the selected element
        TreeViewer treeViewer = editor.getTreeViewer();
        TreeItem[] selectedItems = treeViewer.getTree().getSelection();
        // Cancels the selected element
        editor.getTreeViewer().getTree().deselectAll();
        for (TreeItem item : selectedItems) {
            JbmEditorMigrationRow row = (JbmEditorMigrationRow) item.getData();
            if (row.getLevel() == JbmEditorMigrationRow.LEVEL_THIRD) {
                setItem(treeViewer, confirmItemEnum, row);

            } else if (row.getLevel() == JbmEditorMigrationRow.LEVEL_SECOND) {
                // Grandchild hierarchy also do the same if Child hierarchy's
                // selected
                setSecountTreeViewSetItem(treeViewer, confirmItemEnum, row);

            } else if (row.getLevel() == JbmEditorMigrationRow.LEVEL_FIRST) {
                // The processing is performed as well grandchild Child
                // hierarchy and hierarchy if parent hierarchy's selected
                for (JbmEditorMigrationRow childRow : row.getChildList()) {
                    setSecountTreeViewSetItem(treeViewer, confirmItemEnum,
                            childRow);
                }
                ConfirmItemEnum out;
                if (ConfirmItemEnum.STATUS_NON.equals(confirmItemEnum)) {
                    out = ConfirmItemEnum.STATUS_NG;
                } else {
                    out = ConfirmItemEnum.STATUS_OK;
                }
                setItem(treeViewer, out, row);
            }
        }
        editor.setDirty(true);
        // because it is carried out () that setDirty, unnecessary
        // WorkStatusView.out(false, null);
    }

    /**
     * Set the status of the second layer below.<br/>
     * 
     * @param treeViewer
     *            Tree view
     * @param confirmItemEnum
     *            Setting data
     * @param second
     *            Target row data
     */
    protected void setSecountTreeViewSetItem(TreeViewer treeViewer,
            ConfirmItemEnum confirmItemEnum, JbmEditorMigrationRow second) {
        for (JbmEditorMigrationRow childRow : second.getChildList()) {
            setItem(treeViewer, confirmItemEnum, childRow);
        }
        // Parent element
        ConfirmItemEnum out;
        if (ConfirmItemEnum.STATUS_NON.equals(confirmItemEnum)) {
            out = ConfirmItemEnum.STATUS_NG;
        } else {
            out = ConfirmItemEnum.STATUS_OK;
        }
        setItem(treeViewer, out, second);
    }

    /**
     * Set the status of the visual / hearing item of the third layer.<br/>
     * 
     * @param treeViewer
     *            Tree view
     * @param confirmItemEnum
     *            Setting data
     * @param child
     *            Target third layer line data
     */
    protected void setItem(TreeViewer treeViewer,
            ConfirmItemEnum confirmItemEnum, JbmEditorMigrationRow child) {
        if (columnIndex == JbmEditorEnum.VISUAL_CONFIRM_STATSU_ITEM.getCode()) {
            // Visual confirmation
            child.setCheckEyeStatus(String.valueOf(confirmItemEnum.getType()));
        } else if (columnIndex == JbmEditorEnum.HIARING_STATUS.getCode()) {
            // Hearing
            child.setHearingStatus(String.valueOf(confirmItemEnum.getType()));
        }
        treeViewer.refresh(child.getMoreParent());
        child.updateWriteData();
    }
}
