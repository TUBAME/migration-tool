/*
 * ConfirmItemDeleteAction.java
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

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.viewers.TreeViewer;
import org.tubame.portability.model.ConfirmItemEnum;
import org.tubame.portability.model.JbmEditorMigrationRow;
import org.tubame.portability.plugin.editor.MigrationEditorOperation;
import org.tubame.portability.plugin.view.WorkStatusView;
import org.tubame.portability.util.PluginUtil;
import org.tubame.portability.util.resource.MessageUtil;
import org.tubame.portability.util.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Delete items at once the status of visual confirmation hearing item is in
 * {@link ConfirmItemEnum} Delete.<br/>
 * If the confirmation of the child element is finished all after the deletion, <br/>
 * the status of the parent element also change.<br/>
 * In addition, the notification on the screen that the file has changed.<br/>
 */
public class ConfirmItemDeleteAction extends AbstractJbmEditorCommandButton {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConfirmItemDeleteAction.class);

    /**
     * {@inheritDoc}
     */
    @Override
    void commandButtonExecute(MigrationEditorOperation editor) {
        LOGGER.info(MessageUtil.INF_CHECK_DELETE_ROW);
        @SuppressWarnings("unchecked")
        List<JbmEditorMigrationRow> rowList = (List<JbmEditorMigrationRow>) editor
                .getTreeViewer().getTree().getData();

        boolean isChange = false;
        TreeViewer treeViewer = editor.getTreeViewer();

        List<JbmEditorMigrationRow> deleteList = new ArrayList<JbmEditorMigrationRow>();

        for (JbmEditorMigrationRow first : rowList) {
            for (JbmEditorMigrationRow second : first.getChildList()) {
                for (JbmEditorMigrationRow third : second.getChildList()) {
                    if (isDelete(third)) {
                        deleteList.add(third);
                        isChange = true;
                    }
                }
            }
        }
        if (isChange) {
            JbmRowDelete deleter = new JbmRowDelete();
            for (JbmEditorMigrationRow target : deleteList) {
                deleter.removeRowItem(editor, target);
            }
            WorkStatusView.out(false, rowList);
            treeViewer.refresh();
            // File change settings
            editor.setDirty(true);
        } else {
            PluginUtil.viewInfoDialog(ResourceUtil.LINE_DELETE_TITLE,
                    MessageUtil.INF_NO_DELETE_ROW);
        }
    }

    /**
     * The judgment deleted line is present.<br/>
     * The judgment is NG item or not visually / hearing confirmation item.<br/>
     * 
     * @param row
     *            Target row data
     * @return true:Delete target row false:Delete excluded
     */
    private boolean isDelete(JbmEditorMigrationRow row) {
        if (isNG(row.getCheckEyeStatus())) {
            return true;
        }
        return isNG(row.getHearingStatus());
    }

    /**
     * To determine whether NG item from a string.<br/>
     * 
     * @param confirmStatus
     *            Status
     * @return true:Delete target row false:Delete excluded
     */
    private boolean isNG(String confirmStatus) {
        ConfirmItemEnum check = ConfirmItemEnum.getForString(confirmStatus);
        if (check != null) {
            if (ConfirmItemEnum.STATUS_NG.equals(check)) {
                return true;
            }
        }
        return false;
    }
}
