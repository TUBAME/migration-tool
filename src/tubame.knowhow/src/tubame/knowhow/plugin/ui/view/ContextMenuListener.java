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
package tubame.knowhow.plugin.ui.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuListener;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.ui.IWorkbenchActionConstants;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.ui.action.view.AbstractEntryItemAction;
import tubame.knowhow.plugin.ui.action.view.KnowhowEntryPopupActionFacade;

/**
 * Listener class of context menu of know-how entry view.<br/>
 * Class that controls the menu to be displayed when right-clicking on the
 * know-how entry view.<br/>
 */
public class ContextMenuListener implements IMenuListener {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ContextMenuListener.class);
    /** access to treeViewer */
    private KnowhowEntryTreeViewerOperator treeViewer;
    /** Registration action items list */
    private List<Action> entryItemActionList = new ArrayList<Action>();
    /** Edit action list */
    private List<Action> editActionList = new ArrayList<Action>();

    /**
     * Constructor.<br/>
     * 
     * @param treeViewer
     *            Tree view
     * 
     */
    public ContextMenuListener(KnowhowEntryTreeViewerOperator treeViewer) {
        LOGGER.debug("[treeViewer]" + treeViewer);
        this.treeViewer = treeViewer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void menuAboutToShow(IMenuManager manager) {
        LOGGER.debug("[manager]" + manager);

        initializeMenuItem(manager);
        for (Action action : KnowhowEntryPopupActionFacade
                .createActionList(treeViewer)) {
            if (action instanceof AbstractEntryItemAction) {
                entryItemActionList.add(action);
            } else {
                editActionList.add(action);
            }
        }
        // Show in order
        addActionItemMenu(manager, entryItemActionList);
        manager.add(new Separator(IWorkbenchActionConstants.MB_ADDITIONS));
        addActionItemMenu(manager, editActionList);
    }

    /**
     * Initialize menu information.<br/>
     * 
     * @param manager
     *            IMenuManager
     */
    private void initializeMenuItem(IMenuManager manager) {
        manager.removeAll();
        entryItemActionList.clear();
        editActionList.clear();
    }

    /**
     * Add an action to the menu list.<br/>
     * 
     * @param manager
     *            Menu Manager
     * @param actionList
     *            Action list
     */
    private void addActionItemMenu(IMenuManager manager, List<Action> actionList) {
        for (Action action : actionList) {
            manager.add(action);
        }
    }

}
