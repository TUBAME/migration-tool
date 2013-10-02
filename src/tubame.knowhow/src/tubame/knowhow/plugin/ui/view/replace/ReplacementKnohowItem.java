/*
 * ReplacementKnohowItem.java
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
package tubame.knowhow.plugin.ui.view.replace;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.model.view.CategoryViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import tubame.knowhow.util.PluginUtil;

/**
 * Category of know-how entry view, know-how transfer class.<br/>
 */
public class ReplacementKnohowItem implements ReplacementItemStrategy {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ReplacementKnohowItem.class);
    /** All Items list */
    private List<PortabilityKnowhowListViewOperation> originalItemlist;
    /** Target list */
    private List<PortabilityKnowhowListViewOperation> targetItemList;
    /** Pending list */
    private List<PortabilityKnowhowListViewOperation> holdItemList;

    /**
     * Constructor.<br/>
     * 
     * @param originalItemlist
     *            All Items List
     * @param targetItemList
     *            Target list
     * @param holdItemList
     *            Pending list
     */
    public ReplacementKnohowItem(
            List<PortabilityKnowhowListViewOperation> originalItemlist,
            List<PortabilityKnowhowListViewOperation> targetItemList,
            List<PortabilityKnowhowListViewOperation> holdItemList) {
        this.originalItemlist = originalItemlist;
        this.targetItemList = targetItemList;
        this.holdItemList = holdItemList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replacementSelectItem(
            PortabilityKnowhowListViewOperation selectedEntry, int rowIndex) {
        ReplacementKnohowItem.LOGGER.debug("[selectedEntry]" + selectedEntry);

        int index = targetItemList.indexOf(selectedEntry);
        if (rowIndex + index < targetItemList.size() && 0 <= rowIndex + index) {
            PortabilityKnowhowListViewOperation afterEntry = targetItemList
                    .set(rowIndex + index, selectedEntry);
            targetItemList.set(index, afterEntry);

            // Turn on the list to change the order after clearing once
            originalItemlist.clear();
            // Place in the order of know-how > Category.
            insertItemOrder(selectedEntry);
        } else {
            JbmException.outputExceptionLog(LOGGER, null, MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.ERROR_MOVE_ELEMENT));
            ErrorDialog
                    .openErrorDialog(
                            PluginUtil.getActiveWorkbenchShell(),
                            null,
                            MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.ERROR_MOVE_ELEMENT));
        }

    }

    /**
     * Place to display data in the order of know-how > Category.<br/>
     * 
     * @param selectedEntry
     *            Selection entry
     */
    private void insertItemOrder(
            PortabilityKnowhowListViewOperation selectedEntry) {
        if (!holdItemList.isEmpty()) {
            if (holdItemList.get(0).isKnowhow()) {
                originalItemlist.addAll(holdItemList);
                addReplacementLevelItem(targetItemList);

            } else if (holdItemList.get(0).isCategory()) {
                addReplacementLevelItem(targetItemList);
                addReplacementLevelItem(holdItemList);
                replaceCategoryOfKnowhowRef(selectedEntry, targetItemList);
            }
        } else {
            addReplacementLevelItem(targetItemList);
        }
    }

    /**
     * Change the order of the know-how of key reference category.<br/>
     * 
     * @param selectedEntry
     *            Selection entry
     * @param targetItemList
     *            Target item list
     */
    private void replaceCategoryOfKnowhowRef(
            PortabilityKnowhowListViewOperation selectedEntry,
            List<PortabilityKnowhowListViewOperation> targetItemList) {
        PortabilityKnowhowListViewOperation selectedParentEntry = selectedEntry
                .getParent();
        CategoryViewType categoryViewType = (CategoryViewType) selectedEntry
                .getParent().getKnowhowViewType();
        categoryViewType.clearKnowhowRefKey();
        for (PortabilityKnowhowListViewOperation knowhowListViewOperation : targetItemList) {
            categoryViewType.addKnowhowRefKey(knowhowListViewOperation
                    .getKnowhowViewType().getRegisterKey());
        }
        selectedParentEntry.setKnowhowViewType(categoryViewType);
    }

    /**
     * Add items to list all of the items list.<br/>
     * 
     * @param addItemList
     *            Add item list
     */
    private void addReplacementLevelItem(
            List<PortabilityKnowhowListViewOperation> addItemList) {
        for (PortabilityKnowhowListViewOperation entryOperator : addItemList) {
            originalItemlist.add(entryOperator);
        }
    }
}
