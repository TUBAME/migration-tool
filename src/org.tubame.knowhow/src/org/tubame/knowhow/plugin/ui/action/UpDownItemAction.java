/*
 * UpDownItemAction.java
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
package org.tubame.knowhow.plugin.ui.action;


import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.plugin.ui.view.KnowhowEntryTreeViewer;
import org.tubame.knowhow.plugin.ui.view.KnowhowEntryView;

/**
 * Control class to move up or down the selected item in the knowledge entry
 * view.<br/>
 */
public class UpDownItemAction extends Action implements IViewActionDelegate {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(UpDownItemAction.class);
    /** Know-how entry view */
    private KnowhowEntryView knowhowEntryView;
    /** Know-how items move on ID. */
    private static final String UP_KNOWHOW_ITEM_ID = "org.tubame.knowhow.maintenance.portability.ui.actions.UpItemAction";
    /** Know-how items move under ID. */
    private static final String DOWN_KNOWHOW_ITEM_ID = "org.tubame.knowhow.maintenance.portability.ui.actions.DownItemAction";

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(IAction action) {
        UpDownItemAction.LOGGER.debug("[action]" + action);
        KnowhowEntryTreeViewer treeViewer = knowhowEntryView.getTreeViewer();
        if (!treeViewer.getSelection().isEmpty()) {
            replacementKnowhowItem(action, treeViewer);
            UpDownItemAction.LOGGER.info(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_MOVE_ITEM));
        }
    }

    /**
     * Move the items to be moved.<br/>
     * 
     * @param action
     *            Action
     * @param treeViewer
     *            Tree view
     */
    private void replacementKnowhowItem(IAction action,
            KnowhowEntryTreeViewer treeViewer) {

        if (DOWN_KNOWHOW_ITEM_ID.equals(action.getId())) {
            treeViewer.downItem();
        } else if (UP_KNOWHOW_ITEM_ID.equals(action.getId())) {
            treeViewer.upItem();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IViewPart view) {
        knowhowEntryView = (KnowhowEntryView) view;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEnabled() {
        return false;
    }

}
