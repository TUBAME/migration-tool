/*
 * RemoveRelationKnowhow.java
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

import tubame.common.util.CmnStringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.logic.KnowhowManagement;
import tubame.knowhow.plugin.model.view.CategoryViewType;
import tubame.knowhow.plugin.model.view.KnowhowDetailType;
import tubame.knowhow.plugin.model.view.KnowhowViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.plugin.ui.editor.multi.docbook.KnowhowDetailEditor;
import tubame.knowhow.util.PluginUtil;

/**
 * Make a related item deletion process know-how information.<br/>
 * Delete stick know-how related to the item to be deleted,<br/>
 * the item that you want to match the key of its own from the reference list of
 * key know-how detailed information,<br/>
 * the parent category.<br/>
 */
public class RemoveRelationKnowhow implements RemoveRelationItemStrategy {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(RemoveRelationKnowhow.class);
    /** Know-how entry view item */
    private KnowhowViewType knowhowViewType;
    /** Deleted items */
    private PortabilityKnowhowListViewOperation portabilityKnowhowListViewOperation;

    /**
     * Constructor.<br/>
     * 
     * @param portabilityKnowhowListViewOperation
     *            Deleted items
     */
    public RemoveRelationKnowhow(
            PortabilityKnowhowListViewOperation portabilityKnowhowListViewOperation) {
        this.portabilityKnowhowListViewOperation = portabilityKnowhowListViewOperation;
        this.knowhowViewType = (KnowhowViewType) portabilityKnowhowListViewOperation
                .getKnowhowViewType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRelationItem() {
        RemoveRelationKnowhow.LOGGER.debug(CmnStringUtil.EMPTY);
        removeKnowhowDetail();
        removeEntryViewItem();

    }

    /**
     * Delete key reference to itself from the parent category that is
     * registered in the entry view.<br/>
     * 
     */
    private void removeEntryViewItem() {
        CategoryViewType categoryViewType = (CategoryViewType) portabilityKnowhowListViewOperation
                .getParent().getKnowhowViewType();
        String removeTargetKey = null;
        for (String knowhowRefKey : categoryViewType.getKnowhowRefKeies()) {
            if (knowhowViewType.getRegisterKey().equals(knowhowRefKey)) {
                removeTargetKey = knowhowRefKey;
            }
        }
        if (removeTargetKey != null) {
            categoryViewType.getKnowhowRefKeies().remove(removeTargetKey);
        }
    }

    /**
     * Delete the data that matches the key from its own know-how detail data
     * list.<br/>
     * Remove know-how detail data that matches the reference key know-how from
     * its own know-how detail data list.<br/>
     * 
     */
    private void removeKnowhowDetail() {
        KnowhowDetailType removeTargetItem = null;
        for (KnowhowDetailType knowhowDetailType : KnowhowManagement
                .getKnowhowDetailTypes()) {
            if (knowhowDetailType.getKnowhowDetailId().equals(
                    knowhowViewType.getKnowhowDetailRefKey())) {
                removeTargetItem = knowhowDetailType;
            }
        }
        if (removeTargetItem != null) {
            KnowhowManagement.getKnowhowDetailTypes().remove(removeTargetItem);
            clearKnowhoweDetaileditor(removeTargetItem);
        }
    }

    /**
     * Initialization of know-how detail page editor.<br/>
     * 
     * @param removeTargetItem
     *            Deleted items
     */
    private void clearKnowhoweDetaileditor(KnowhowDetailType removeTargetItem) {
        MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor = PluginUtil
                .getKnowhowEditor();
        KnowhowDetailEditor detailEditor = knowhowMultiPageEditor
                .getKnowhowDetailEditor();
        if (detailEditor.getKnowhowDetailType() != null) {
            if (removeTargetItem.getKnowhowDetailId().equals(
                    detailEditor.getKnowhowDetailType().getKnowhowDetailId())) {
                knowhowMultiPageEditor.clearKnowhowDetail();
            }
        }
    }
}
