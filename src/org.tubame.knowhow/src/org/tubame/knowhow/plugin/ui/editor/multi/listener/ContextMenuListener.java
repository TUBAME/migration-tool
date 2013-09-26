/*
 * ContextMenuListener.java
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
package org.tubame.knowhow.plugin.ui.editor.multi.listener;

import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.plugin.ui.action.CreateChapterPopupMenuAction;
import org.tubame.knowhow.plugin.ui.action.DeletePopupMenuAction;
import org.tubame.knowhow.plugin.ui.editor.multi.documentation.EditorTreeViewerOperator;

/**
 * Context menu Listener class know-how of the editor (the documentation tab).<br/>
 * Class that controls the menu to be displayed when right-clicking in the
 * editor.<br/>
 */
public class ContextMenuListener implements IMenuListener {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ContextMenuListener.class);
    /** Access to treeViewer. */
    private EditorTreeViewerOperator treeViewer;

    /**
     * Constructor.<br/>
     * 
     * @param treeViewer
     *            Tree view
     * 
     */
    public ContextMenuListener(EditorTreeViewerOperator treeViewer) {
        LOGGER.debug("[treeViewer]" + treeViewer);
        this.treeViewer = treeViewer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuAboutToShow(IMenuManager manager) {
        LOGGER.debug("[manager]" + manager);
        manager.removeAll();
        manager.add(new CreateChapterPopupMenuAction(treeViewer));
        if (treeViewer.getOneSelection() != null) {
            manager.add(new DeletePopupMenuAction(treeViewer));
        }
    }

}
