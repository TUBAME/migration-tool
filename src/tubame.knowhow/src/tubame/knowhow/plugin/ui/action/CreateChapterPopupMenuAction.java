/*
 * CreateChapterPopupMenuAction.java
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

import java.util.List;

import tubame.common.util.CmnStringUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.viewers.TreeViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.model.editor.ChapterEntry;
import tubame.knowhow.plugin.model.editor.EntryOperator;
import tubame.knowhow.plugin.ui.editor.multi.documentation.EditorTreeViewerOperator;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Dialog class action heading created.<br/>
 */
public class CreateChapterPopupMenuAction extends Action {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CreateChapterPopupMenuAction.class);
    /** Access to treeViewer */
    private EditorTreeViewerOperator treeViewer;

    /**
     * Constructor.<br/>
     * 
     * @param treeViewer
     *            Access to treeViewer
     */
    public CreateChapterPopupMenuAction(EditorTreeViewerOperator treeViewer) {
        super();
        this.treeViewer = treeViewer;
        CreateChapterPopupMenuAction.LOGGER.debug("[treeViewer]" + treeViewer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return ResourceUtil.createChapter;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        CreateChapterPopupMenuAction.LOGGER.debug(CmnStringUtil.EMPTY);

        InputDialog inputDialog = new InputDialog(
                PluginUtil.getActiveWorkbenchShell(),
                ResourceUtil.inputChapterSubject,
                ResourceUtil.inputChapterName, null, null);
        if (inputDialog.open() == InputDialog.OK) {
            // Pretreatment
            TreeViewer entryViewer = (TreeViewer) treeViewer;
            Object[] expandedNodes = entryViewer.getExpandedElements();
            entryViewer.getTree().setRedraw(false);
            // Entry Processing
            List<EntryOperator> entryList = treeViewer.getInputEntry();
            ChapterEntry chapter = new ChapterEntry();
            chapter.setKey(ResourceUtil.dummyChapterID);
            chapter.setName(inputDialog.getValue());
            chapter.setLevel(0);
            entryList.add(chapter);
            treeViewer.setEntryListData(entryList);
            // Post-processing
            treeViewer.refreshTreeViewer(true);
            entryViewer.setExpandedElements(expandedNodes);
            entryViewer.getTree().setRedraw(true);
        }
        CreateChapterPopupMenuAction.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_CREATE_CONTEXTMENULIST));
    }
}
