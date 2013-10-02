/*
 * EntryElementPasteAction.java
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
package tubame.knowhow.plugin.ui.action.view;

import java.util.List;

import tubame.common.util.CmnStringUtil;

import org.eclipse.jface.action.Action;
import org.eclipse.jface.viewers.TreeViewer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.logic.ClipBoardEntryFacade;
import tubame.knowhow.plugin.logic.EntryItemManagement;
import tubame.knowhow.plugin.logic.KnowhowManagement;
import tubame.knowhow.plugin.model.editor.AbstractEntry;
import tubame.knowhow.plugin.model.view.AbstractViewType;
import tubame.knowhow.plugin.model.view.CategoryViewType;
import tubame.knowhow.plugin.model.view.CheckItemViewType;
import tubame.knowhow.plugin.model.view.KnowhowDetailType;
import tubame.knowhow.plugin.model.view.KnowhowViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.model.view.SearchInfoViewType;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.plugin.ui.view.KnowhowEntryTreeViewerOperator;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.ViewUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Make a copy of each Entry.<br/>
 * Copy to clipboard {@link AbstractEntry}.<br/>
 */
public class EntryElementPasteAction extends Action {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntryElementPasteAction.class);
    /** TreeViewer to be added */
    private KnowhowEntryTreeViewerOperator treeViewer;
    /** Entry objects contained in the select target */
    private PortabilityKnowhowListViewOperation selectedEntry;

    /**
     * Constructor.<br/>
     * 
     * @param treeViewer
     *            KnowhowEntryTreeViewerOperator
     * @param selectedEntry
     *            Entry you have selected
     * 
     */
    public EntryElementPasteAction(KnowhowEntryTreeViewerOperator treeViewer,
            PortabilityKnowhowListViewOperation selectedEntry) {
        this.treeViewer = treeViewer;
        this.selectedEntry = selectedEntry;
        EntryElementPasteAction.LOGGER.debug("[treeViewer]" + treeViewer
                + "[selectedEntry]" + selectedEntry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return ResourceUtil.contextPasteMenuName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        EntryElementPasteAction.LOGGER.debug(CmnStringUtil.EMPTY);
        PortabilityKnowhowListViewOperation clipBordEntry = ClipBoardEntryFacade
                .getEntry();

        if (selectedEntry != null) {
            treeViewer.addRegisterItem(selectedEntry,
                    rewritingViewOperation(clipBordEntry, selectedEntry));
            treeViewer.setSelectionItem(clipBordEntry);
        } else {
            // TreeViewer display processing
            TreeViewer entryViewer = (TreeViewer) treeViewer;
            entryViewer.getTree().setRedraw(false);
            Object[] expandedNodes = entryViewer.getExpandedElements();
            // Paste data processing
            List<PortabilityKnowhowListViewOperation> operationListDataList = ViewUtil
                    .getPortabilityKnowhowListViewOperationList();
            // Rewriting of information
            PortabilityKnowhowListViewOperation knowhowListViewOperation = rewritingViewOperation(
                    clipBordEntry, null);
            // Clear parent information explicitly
            knowhowListViewOperation.setParent(null);
            operationListDataList.add(knowhowListViewOperation);
            treeViewer.setEntryListData(operationListDataList);
            // TreeViewer display processing
            entryViewer.setExpandedElements(expandedNodes);
            entryViewer.getTree().setRedraw(true);
            treeViewer.setSelectionItem(knowhowListViewOperation);
        }
        notifyKnowhowEditorDirty(true);
        EntryElementPasteAction.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_ELEMENT_PASTE));
    }

    /**
     * Make the update notification to the editor know-how.<br/>
     * 
     * @param dirty
     *            Update notification
     */
    private void notifyKnowhowEditorDirty(boolean dirty) {
        MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor = PluginUtil
                .getKnowhowEditor();
        knowhowMultiPageEditor.setDirty(dirty);

    }

    /**
     * Rewrite the information know-how of entry.<br/>
     * 
     * @param entry
     *            Data entry
     * @param parentEntry
     *            Parent entry
     * @return Know-how entry information
     */
    private PortabilityKnowhowListViewOperation rewritingViewOperation(
            PortabilityKnowhowListViewOperation entry,
            PortabilityKnowhowListViewOperation parentEntry) {

        PortabilityKnowhowListViewOperation reEntry = rewritingItemKey(entry);
        rewitingClearKnowhowRefKey(reEntry);

        if (!reEntry.getChildList().isEmpty()) {
            for (PortabilityKnowhowListViewOperation knowhowListViewOperation : reEntry
                    .getChildList()) {
                rewritingViewOperation(knowhowListViewOperation, reEntry);
                rewritingItemRefKey(knowhowListViewOperation, reEntry);
            }
        }
        return reEntry;
    }

    /**
     * Clear key know-how reference list of categories.<br/>
     * 
     * @param reEntry
     *            PortabilityKnowhowListViewOperation
     */
    private void rewitingClearKnowhowRefKey(
            PortabilityKnowhowListViewOperation reEntry) {
        if (reEntry.isCategory()) {
            CategoryViewType categoryCheckItem = (CategoryViewType) reEntry
                    .getKnowhowViewType();
            categoryCheckItem.clearKnowhowRefKey();
        }

    }

    /**
     * Options were granted to any item of information the item reference key.<br/>
     * Options were granted to any item of information and know-how / search key
     * information.<br/>
     * 
     * @param entry
     *            Entry
     * @param parentEntry
     *            Parent entry
     */
    private void rewritingItemRefKey(PortabilityKnowhowListViewOperation entry,
            PortabilityKnowhowListViewOperation parentEntry) {
        if (entry.isKnowhow() && parentEntry != null) {
            KnowhowViewType knowhowViewType = (KnowhowViewType) entry
                    .getKnowhowViewType();
            CategoryViewType categoryCheckItem = (CategoryViewType) parentEntry
                    .getKnowhowViewType();
            categoryCheckItem
                    .addKnowhowRefKey(knowhowViewType.getRegisterKey());
            parentEntry.setKnowhowViewType(categoryCheckItem);
        } else if (entry.isSearchInfo() && parentEntry != null) {
            SearchInfoViewType searchInfoViewType = (SearchInfoViewType) entry
                    .getKnowhowViewType();
            CheckItemViewType parentCheckItem = (CheckItemViewType) parentEntry
                    .getKnowhowViewType();
            parentCheckItem
                    .setSearchRefKey(searchInfoViewType.getRegisterKey());
            parentEntry.setKnowhowViewType(parentCheckItem);
        }
    }

    /**
     * Rewrite the ID of each item.<br/>
     * 
     * @param entry
     *            PortabilityKnowhowListViewOperation
     * @return PortabilityKnowhowListViewOperation
     */
    private PortabilityKnowhowListViewOperation rewritingItemKey(
            PortabilityKnowhowListViewOperation entry) {
        String rewritingKey = null;
        if (entry.isCategory()) {
            rewritingKey = EntryItemManagement.categoryNumbering();
        } else if (entry.isKnowhow()) {
            rewritingKey = EntryItemManagement.knowhowNumbering();
            // Rewriting process of know-how detailed data key
            entry.setKnowhowViewType(rewritingKnowhowDetailKey(entry,
                    EntryItemManagement.knowhowDetailNumbering()));
        } else if (entry.isCheckItem()) {
            rewritingKey = EntryItemManagement.checkItemNumbering();
        } else if (entry.isSearchInfo()) {
            rewritingKey = EntryItemManagement.searchInfoNumbering();
            // Set the subject name the key only if the search
            entry.setSubjectName(rewritingKey);
        }
        entry.setKnowhowViewType(EntryItemManagement.replaceViewTypeKey(
                rewritingKey, entry));

        return entry;
    }

    /**
     * Updated the know-how key details.<br/>
     * 
     * @param entry
     *            Entry
     * @param knowhowDetailNumbering
     *            Know-how detail key (updated)
     * @return Data entry
     */
    private AbstractViewType rewritingKnowhowDetailKey(
            PortabilityKnowhowListViewOperation entry,
            String knowhowDetailNumbering) {

        KnowhowViewType viewType = (KnowhowViewType) entry.getKnowhowViewType();
        addNewKnowhowDetail(knowhowDetailNumbering, viewType);
        viewType.setKnowhowDetailRefKey(knowhowDetailNumbering);

        return viewType;

    }

    /**
     * Add the data that you copied to know-how detail list.<br/>
     * 
     * @param knowhowDetailNumbering
     *            Know-how detail key (updated)
     * @param viewType
     *            KnowhowViewType
     */
    private void addNewKnowhowDetail(String knowhowDetailNumbering,
            KnowhowViewType viewType) {
        String previousDetailKey = viewType.getKnowhowDetailRefKey();
        KnowhowDetailType newDetailType = new KnowhowDetailType();
        if (KnowhowManagement.getKnowhowDetailInfo(previousDetailKey) != null) {
            newDetailType.setDocbookdata(KnowhowManagement
                    .getKnowhowDetailInfo(previousDetailKey).getDocbookdata());
        } else {
            // Grant template information If know-how detail information is
            // empty
            newDetailType = KnowhowManagement.createTemplateKnowhowDetail(
                    knowhowDetailNumbering, viewType.getRegisterName());
        }
        newDetailType.setKnowhowDetailId(knowhowDetailNumbering);
        KnowhowManagement.addKnowhowDetail(newDetailType);
    }
}
