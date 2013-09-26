/*
 * KnowhowEntryPopupActionFacade.java
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
package org.tubame.knowhow.plugin.ui.action.view;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.jface.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.plugin.logic.ClipBoardEntryFacade;
import org.tubame.knowhow.plugin.model.view.CategoryViewType;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import org.tubame.knowhow.plugin.ui.view.KnowhowEntryTreeViewerOperator;
import org.tubame.knowhow.util.PluginUtil;

/**
 * Facade to manage context menu of know-how entry view (various Action).<br/>
 * Added context menu {@link EntryElementCopyAction} In the case of copy ready.<br/>
 * Added context menu {@link EntryElementPasteAction} If the paste is ready.<br/>
 */
public final class KnowhowEntryPopupActionFacade {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(KnowhowEntryPopupActionFacade.class);

    /**
     * Constructor.<br/>
     * 
     */
    private KnowhowEntryPopupActionFacade() {
        // no operation
    }

    /**
     * Generate the Action list to be displayed in the context menu.<br/>
     * 
     * @param treeViewer
     *            Tree view of the target
     * @return Action List
     */
    public static List<Action> createActionList(
            KnowhowEntryTreeViewerOperator treeViewer) {
        KnowhowEntryPopupActionFacade.LOGGER.debug("[treeViewer]" + treeViewer);

        // Entry of selected
        PortabilityKnowhowListViewOperation selectedEntry = treeViewer
                .getOneSelection();
        // Entry of copying
        PortabilityKnowhowListViewOperation copyEntry = ClipBoardEntryFacade
                .getEntry();
        List<Action> actionList = new ArrayList<Action>();
        // The right-click menu displayed when there is no error and know-how
        // detail when saving
        if (isCreatePopupMenu()) {
            if (selectedEntry != null) {
                selectEntryMenuList(treeViewer, selectedEntry, copyEntry,
                        actionList);
            } else {
                noSelectionEntryMenuList(treeViewer, selectedEntry, copyEntry,
                        actionList);
            }
        }
        KnowhowEntryPopupActionFacade.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_CREATE_CONTEXTMENULIST));

        return actionList;
    }

    /**
     * Judgment or pop-up menu can be generated.<br/>
     * If the know-how editor is displayed,<br/>
     * Generating a pop-up menu if there are no problems with the know-how
     * detail editor.<br/>
     * 
     * @return true:Pop-up menu generation accepted false:Pop-up menu generation
     *         not
     */
    private static boolean isCreatePopupMenu() {
        if (PluginUtil.getOpenEditors().isEmpty()) {
            return false;
        }
        if (!PluginUtil.getKnowhowEditor().getKnowhowDetailEditor()
                .temporaryStorageKnowhowDetailOnMemory()) {
            return false;
        }
        return true;
    }

    /**
     * Generate a list of menus if the item is not selected in the knowledge
     * entry view.<br/>
     * If the category has been copied, the display on the menu.<br/>
     * 
     * @param treeViewer
     *            KnowhowEntryTreeViewerOperator
     * @param selectedEntry
     *            Selection entry
     * @param copyEntry
     *            Copy entry
     * @param actionList
     *            Action list to register the menu
     */
    private static void noSelectionEntryMenuList(
            KnowhowEntryTreeViewerOperator treeViewer,
            PortabilityKnowhowListViewOperation selectedEntry,
            PortabilityKnowhowListViewOperation copyEntry,
            List<Action> actionList) {

        actionList.add(new EntryCategoryAction(selectedEntry));
        ClipBoardEntryFacade.setEntry(copyEntry);
        if (ClipBoardEntryFacade.isSaved() && copyEntry.isCategory()) {
            actionList.add(new EntryElementPasteAction(treeViewer,
                    selectedEntry));
        }
    }

    /**
     * Generate a list of menus if you have selected an item in the know-how
     * entry view.<br/>
     * 
     * @param treeViewer
     *            KnowhowEntryTreeViewerOperator
     * @param selectedEntry
     *            Selection entry
     * @param copyEntry
     *            Copy entry
     * @param actionList
     *            Action list to register the menu
     */
    private static void selectEntryMenuList(
            KnowhowEntryTreeViewerOperator treeViewer,
            PortabilityKnowhowListViewOperation selectedEntry,
            PortabilityKnowhowListViewOperation copyEntry,
            List<Action> actionList) {
        // Flag whether hierarchy can be pasted
        boolean isPasteLevel = false;
        if (selectedEntry.isCategory()) {
            // Category
            isPasteLevel = KnowhowEntryPopupActionFacade
                    .isPasteAndSetActionListCategory(actionList, treeViewer,
                            selectedEntry, copyEntry);
        } else if (selectedEntry.isKnowhow()) {
            // Know-how
            isPasteLevel = KnowhowEntryPopupActionFacade
                    .isPasteAndSetActionListKnowhow(actionList, treeViewer,
                            selectedEntry, copyEntry);
        } else if (selectedEntry.isCheckItem()) {
            // Check items
            isPasteLevel = KnowhowEntryPopupActionFacade
                    .isPasteAndSetActionListCheckItem(actionList, treeViewer,
                            selectedEntry, copyEntry);
        } else if (selectedEntry.isSearchInfo()) {
            // Search information
            isPasteLevel = KnowhowEntryPopupActionFacade
                    .isPasteAndSetActionListSearchInfo(actionList, treeViewer,
                            selectedEntry, copyEntry);
        }
        // There is a copy of the Entry, only if it meets the conditions of
        // hierarchy or paste, paste effect
        ClipBoardEntryFacade.setEntry(copyEntry);
        if (ClipBoardEntryFacade.isSaved() && isPasteLevel) {
            actionList.add(new EntryElementPasteAction(treeViewer,
                    selectedEntry));
        }
        if (!selectedEntry.isSearchInfo()) {
            // Rename Action
            actionList
                    .add(new EntrySubjectNameAction(treeViewer, selectedEntry));
        }
    }

    /**
     * The registration of the menu when it is right-click the category
     * hierarchy. <br/>
     * No menu now.<br/>
     * 
     * @param actionList
     *            Action list to register the menu
     * @param treeViewer
     *            KnowhowEntryTreeViewerOperator
     * @param selectedEntry
     *            Selection entry
     * @param copyEntry
     *            Copy entry
     * @return Whether or not can be pasted (false always)
     */
    private static boolean isPasteAndSetActionListCategory(
            List<Action> actionList, KnowhowEntryTreeViewerOperator treeViewer,
            PortabilityKnowhowListViewOperation selectedEntry,
            PortabilityKnowhowListViewOperation copyEntry) {
        // Registration action
        actionList.add(new EntryCategoryAction(selectedEntry));
        actionList.add(new EntryKnowhowAction(selectedEntry));
        // Number of lines recorded action
        actionList.add(getAppropriateAction(selectedEntry, treeViewer));
        // Copy action
        actionList.add(new EntryElementCopyAction(selectedEntry));
        if (copyEntry != null
                && (copyEntry.isCategory() || copyEntry.isKnowhow())) {
            return true;
        }
        return false;
    }

    /**
     * Get Action reset the number of lines recorded.<br/>
     * 
     * @param selectedEntry
     *            PortabilityKnowhowListViewOperation
     * @param treeViewer
     *            KnowhowEntryTreeViewerOperator
     * @return Number of lines recorded / number of lines recorded non-Action
     */
    private static Action getAppropriateAction(
            PortabilityKnowhowListViewOperation selectedEntry,
            KnowhowEntryTreeViewerOperator treeViewer) {
        CategoryViewType categoryType = (CategoryViewType) selectedEntry
                .getKnowhowViewType();
        if (categoryType.isAppropriateFlag()) {
            return new EntryElementChangeNonAppropriate(selectedEntry,
                    treeViewer);
        }
        return new EntryElementChangeAppropriate(selectedEntry, treeViewer);
    }

    /**
     * Registering the Action menu to match the state of when it is right-click
     * on the know-how hierarchy.<br/>
     * 
     * @param actionList
     *            Action list to register the menu
     * @param treeViewer
     *            Tree view
     * @param selectedEntry
     *            Elements that are currently right-click
     * @param copyEntry
     *            Element of copying
     * @return Whether or not can be pasted true:Possible false:Impossible
     */
    private static boolean isPasteAndSetActionListKnowhow(
            List<Action> actionList, KnowhowEntryTreeViewerOperator treeViewer,
            PortabilityKnowhowListViewOperation selectedEntry,
            PortabilityKnowhowListViewOperation copyEntry) {
        // Check item registration action
        actionList.add(new EntryCheckItemAction(selectedEntry));
        // Copy action
        actionList.add(new EntryElementCopyAction(selectedEntry));
        if (copyEntry != null && copyEntry.isCheckItem()) {
            return true;
        }
        return false;
    }

    /**
     * Registering the Action menu to match the state of when it is right-click
     * on the check item hierarchy.<br/>
     * 
     * @param actionList
     *            Action list to register the menu
     * @param treeViewer
     *            Tree view
     * @param selectedEntry
     *            Elements that are currently right-click
     * @param copyEntry
     *            Element of copying
     * @return Whether or not can be pasted true:Possible false:Impossible
     */
    private static boolean isPasteAndSetActionListCheckItem(
            List<Action> actionList, KnowhowEntryTreeViewerOperator treeViewer,
            PortabilityKnowhowListViewOperation selectedEntry,
            PortabilityKnowhowListViewOperation copyEntry) {
        if (selectedEntry.getChildList().isEmpty()) {
            // Search information registration action
            actionList.add(new EntrySearchInfoAction(selectedEntry));
            if (copyEntry != null && copyEntry.isSearchInfo()) {
                return true;
            }
        }
        // Copy action
        actionList.add(new EntryElementCopyAction(selectedEntry));
        return false;
    }

    /**
     * Registering the Action menu to match the state of when it is right-click
     * on the search information hierarchy.<br/>
     * 
     * @param actionList
     *            Action list to register the menu
     * @param treeViewer
     *            Tree view
     * @param selectedEntry
     *            Elements that are currently right-click
     * @param copyEntry
     *            Element of copying
     * @return Whether or not can be pasted true:Possible false:Impossible
     */
    private static boolean isPasteAndSetActionListSearchInfo(
            List<Action> actionList, KnowhowEntryTreeViewerOperator treeViewer,
            PortabilityKnowhowListViewOperation selectedEntry,
            PortabilityKnowhowListViewOperation copyEntry) {
        actionList.add(new EntryElementCopyAction(selectedEntry));
        return false;
    }
}
