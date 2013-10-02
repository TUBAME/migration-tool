/*
 * RowDeleteAction.java
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

import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.model.MigrationEditorRow;
import tubame.portability.plugin.editor.MigrationEditorOperation;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.resource.MessageUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * It is the Action to remove the selected row. <br/>
 * I get from the Editor Delete execution class.<br/>
 * Remove at once (several available) the selected line.<br/>
 * After deleting, the notification on the screen that the file has changed.<br/>
 */
public class RowDeleteAction extends AbstractJbmEditorCommandButton {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(RowDeleteAction.class);

    /**
     * {@inheritDoc}
     */
    @Override
    void commandButtonExecute(MigrationEditorOperation editor) {
        LOGGER.info(ResourceUtil.JBM_EDITOR + MessageUtil.INF_DELETE_EDITOR_ROW);

        MigrationRowDelete delete = editor.getRowDeleteExecutor();
        TreeViewer treeViewer = editor.getTreeViewer();
        TreeItem[] selectedItems = editor.getTreeViewer().getTree()
                .getSelection();
        boolean isDeleted = false;
        for (TreeItem deleteItem : selectedItems) {
            // Deleted row data (JbmEditorMigrationRow)
            MigrationEditorRow targetRow = (MigrationEditorRow) deleteItem
                    .getData();

            MigrationEditorRow deleteTop = targetRow.getMoreParent();

            // Determine whether removable items
            if (delete.isDeleteExecutor(targetRow.getLevel())) {
                // Remove the items that were selected from the data line
                delete.removeRowItem(editor, targetRow);
                deleteTop.updateWriteData();
                isDeleted = true;
            }
        }
        if (isDeleted) {
            treeViewer.refresh();
            // File change settings
            editor.setDirty(true);
        } else {
            PluginUtil.viewInfoDialog(ResourceUtil.LINE_DELETE_TITLE,
                    MessageUtil.INF_NO_DELETE_ROW);
        }
    }
}
