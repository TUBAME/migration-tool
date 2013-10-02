/*
 * RemoveRelationCategory.java
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
package tubame.knowhow.plugin.ui.view.remove;

import java.util.ArrayList;
import java.util.List;

import tubame.common.util.CmnStringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.model.editor.EntryOperator;
import tubame.knowhow.plugin.model.view.CategoryViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.plugin.ui.editor.multi.documentation.EditorTreeViewerOperator;
import tubame.knowhow.util.PluginUtil;

/**
 * Remove Know how the know-how of the category under the heading and list
 * information category item to be deleted is associated is referring to.<br/>
 */
public class RemoveRelationCategory implements RemoveRelationItemStrategy {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(RemoveRelationCategory.class);
    /** Deleted items */
    private PortabilityKnowhowListViewOperation portabilityKnowhowListViewOperation;
    /** Know-how editor entry deleted items */
    private List<EntryOperator> targetOperators = new ArrayList<EntryOperator>();

    /**
     * Constructor.<br/>
     * 
     * @param portabilityKnowhowListViewOperation
     *            Deleted items
     */
    public RemoveRelationCategory(
            PortabilityKnowhowListViewOperation portabilityKnowhowListViewOperation) {
        this.portabilityKnowhowListViewOperation = portabilityKnowhowListViewOperation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRelationItem() {
        RemoveRelationCategory.LOGGER.debug(CmnStringUtil.EMPTY);
        // Delete the know-how of child hierarchy
        removeKnowhowRetation();
        // Reflect the removal from the editor
        removeEditorEntry();

    }

    /**
     * Remove deleted items from heading the list that registered in the editor.<br/>
     * 
     */
    private void removeEditorEntry() {
        MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor = PluginUtil
                .getKnowhowEditor();
        EditorTreeViewerOperator editorTreeViewerOperator = knowhowMultiPageEditor
                .getTreeViewerOperator();
        List<EntryOperator> entryOperators = editorTreeViewerOperator
                .getInputEntry();
        extractionDeleteTarget(entryOperators,
                portabilityKnowhowListViewOperation);
        if (!targetOperators.isEmpty()) {
            for (EntryOperator targetOperator : targetOperators) {
                editorTreeViewerOperator.removeEntryItem(targetOperator);
            }
        }
    }

    /**
     * Extract the category for a list of headlines from the editor related to
     * the category to be deleted.<br/>
     * 
     * @param entryOperators
     *            Know-how editor entry
     * @param portabilityKnowhowListViewOperation
     *            Deleted entry
     */
    private void extractionDeleteTarget(
            List<EntryOperator> entryOperators,
            PortabilityKnowhowListViewOperation portabilityKnowhowListViewOperation) {
        if (portabilityKnowhowListViewOperation.isCategory()) {
            CategoryViewType categoryViewType = (CategoryViewType) portabilityKnowhowListViewOperation
                    .getKnowhowViewType();
            searchTargetEntryOperator(entryOperators,
                    categoryViewType.getRegisterKey());
            if (!portabilityKnowhowListViewOperation.getChildList().isEmpty()) {
                for (PortabilityKnowhowListViewOperation childViewOperation : portabilityKnowhowListViewOperation
                        .getChildList()) {
                    extractionDeleteTarget(entryOperators, childViewOperation);
                }
            }
        }
    }

    /**
     * Search for the category information from heading the list, and added to
     * the deletion list.<br/>
     * 
     * @param entryOperators
     *            Heading the list
     * @param categoryID
     *            Category ID
     */
    private void searchTargetEntryOperator(List<EntryOperator> entryOperators,
            String categoryID) {

        for (EntryOperator entryOperator : entryOperators) {
            if (entryOperator.getKey().equals(categoryID)) {
                targetOperators.add(entryOperator);
            } else if (!entryOperator.getEntryChildren().isEmpty()) {
                searchTargetEntryOperator(entryOperator.getEntryChildren(),
                        categoryID);
            }
        }
    }

    /**
     * Remove Know how information that it refers to know-how information.<br/>
     * 
     */
    private void removeKnowhowRetation() {
        if (!portabilityKnowhowListViewOperation.getChildList().isEmpty()) {
            removeKnowhowDetail(portabilityKnowhowListViewOperation
                    .getChildList());
        }
    }

    /**
     * Remove Know how information from the know-how of key detailed reference
     * information know-how.<br/>
     * 
     * @param childList
     *            Child hierarchy items
     */
    private void removeKnowhowDetail(
            List<PortabilityKnowhowListViewOperation> childList) {
        for (PortabilityKnowhowListViewOperation viewOperation : childList) {
            if (viewOperation.isKnowhow()) {
                RemoveRelationKnowhow relationKnowhow = new RemoveRelationKnowhow(
                        viewOperation);
                relationKnowhow.removeRelationItem();
            } else if (viewOperation.isCategory()) {
                if (!viewOperation.getChildList().isEmpty()) {
                    this.removeKnowhowDetail(viewOperation.getChildList());
                }
            }
        }
    }
}
