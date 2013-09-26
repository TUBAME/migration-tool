/*
 * EntryElementChangeAppropriate.java
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

import java.util.List;

import org.tubame.common.util.CmnStringUtil;

import org.eclipse.jface.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.plugin.model.editor.CategoryEntry;
import org.tubame.knowhow.plugin.model.editor.EntryOperator;
import org.tubame.knowhow.plugin.model.view.CategoryViewType;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import org.tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import org.tubame.knowhow.plugin.ui.view.KnowhowEntryTreeViewerOperator;
import org.tubame.knowhow.util.PluginUtil;
import org.tubame.knowhow.util.resource.ResourceUtil;

/**
 * Class to reset the line number calculation of category.<br/>
 */
public class EntryElementChangeAppropriate extends Action {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntryElementChangeAppropriate.class);
    /** TreeViewer to be added */
    private KnowhowEntryTreeViewerOperator treeViewer;
    /** Entry objects contained in the select target */
    private PortabilityKnowhowListViewOperation selectedEntry;
    /** Line number calculation */
    private static final boolean APPROPRIATE = true;

    /**
     * Constructor.<br/>
     * 
     * @param selectedEntry
     *            Entry that is stored in the select target
     * @param treeViewer
     *            KnowhowEntryTreeViewerOperator
     */
    public EntryElementChangeAppropriate(
            PortabilityKnowhowListViewOperation selectedEntry,
            KnowhowEntryTreeViewerOperator treeViewer) {
        this.treeViewer = treeViewer;
        this.selectedEntry = selectedEntry;
        EntryElementChangeAppropriate.LOGGER.debug("[selectedEntry]"
                + selectedEntry + "[treeViewer]" + treeViewer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        EntryElementChangeAppropriate.LOGGER.debug(CmnStringUtil.EMPTY);

        CategoryViewType categoryType = (CategoryViewType) selectedEntry
                .getKnowhowViewType();
        categoryType.setAppropriateFlag(APPROPRIATE);
        treeViewer.refreshTreeViewer(true);
        MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor = PluginUtil
                .getKnowhowEditor();
        refrectionAppropriateFlag(categoryType, knowhowMultiPageEditor
                .getTreeViewerOperator().getInputEntry());
        knowhowMultiPageEditor.getTreeViewerOperator().refreshTreeViewer(true);
        EntryElementChangeAppropriate.LOGGER
                .info(MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.LOG_SETTING_APPROPRIATE_LINE));
    }

    /**
     * When the line number calculation information is converted by the entry
     * view,<br/>
     * Change the line number calculation information of entry equivalent
     * present in the heading the list.<br/>
     * 
     * @param categoryType
     *            Entry view category information
     * @param EntryOperators
     *            Heading list information
     */
    private void refrectionAppropriateFlag(CategoryViewType categoryType,
            List<EntryOperator> EntryOperators) {
        for (EntryOperator entryOperator : EntryOperators) {
            if (categoryType.getRegisterKey().equals(entryOperator.getKey())) {
                if (entryOperator.isCategory()) {
                    CategoryEntry categoryEntry = (CategoryEntry) entryOperator;
                    categoryEntry.setEntryViewData(categoryType);
                }
            } else if (!entryOperator.getEntryChildren().isEmpty()) {
                refrectionAppropriateFlag(categoryType,
                        entryOperator.getEntryChildren());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return ResourceUtil.changeAppropriate;
    }

}
