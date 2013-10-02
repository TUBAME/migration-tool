/*
 * DeletePopupMenuAction.java
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
package tubame.knowhow.plugin.ui.action;

import tubame.common.util.CmnStringUtil;

import org.eclipse.jface.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.ui.editor.multi.documentation.EditorTreeViewerOperator;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Class action to remove the selected item from the list heading.<br/>
 */
public class DeletePopupMenuAction extends Action {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DeletePopupMenuAction.class);
    /** Sccess to treeViewer */
    private EditorTreeViewerOperator treeViewer;

    /**
     * Constructor.<br/>
     * 
     * @param treeViewer
     *            Access to treeViewer
     */
    public DeletePopupMenuAction(EditorTreeViewerOperator treeViewer) {
        super();
        this.treeViewer = treeViewer;
        DeletePopupMenuAction.LOGGER.debug("[treeViewer]" + treeViewer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return ResourceUtil.contextDelete;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        DeletePopupMenuAction.LOGGER.debug(CmnStringUtil.EMPTY);
        treeViewer.removeEntryItem(treeViewer.getOneSelection());
        treeViewer.refreshTreeViewer(true);
        DeletePopupMenuAction.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_ENTRY_REMOVE));
    }
}
