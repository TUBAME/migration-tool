/*
 * SelectEntryViewItemListenter.java
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

import java.util.List;


import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.plugin.model.editor.CategoryEntry;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import org.tubame.knowhow.util.PluginUtil;

/**
 * Classes for controlling the operation when the header list information is
 * double-clicked.<br/>
 */
public class SelectEntryViewItemListenter implements IDoubleClickListener {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SelectEntryViewItemListenter.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void doubleClick(DoubleClickEvent event) {
        SelectEntryViewItemListenter.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_DOUBLE_CLICK_ITEM));
        IStructuredSelection structuredSelection = (IStructuredSelection) event
                .getSelection();

        if (structuredSelection.getFirstElement() instanceof CategoryEntry) {
            CategoryEntry categoryEntry = (CategoryEntry) structuredSelection
                    .getFirstElement();
            String categoryId = categoryEntry.getKey();
            if (!PluginUtil.isTreeViewerDisposed()) {
                selectEntryViewItem(categoryId, PluginUtil
                        .getKnowhowEntryViewTreeViewer().getInputEntry());
            }
        }
    }

    /**
     * And focuses on those categories that have the ID matched by the know-how
     * entry view.<br/>
     * 
     * @param categoryId
     *            Category ID
     * @param inputEntry
     *            Know-how entry list
     */
    private void selectEntryViewItem(String categoryId,
            List<PortabilityKnowhowListViewOperation> inputEntry) {
        for (PortabilityKnowhowListViewOperation knowhowListViewOperation : inputEntry) {
            if (categoryId.equals(knowhowListViewOperation.getKnowhowViewType()
                    .getRegisterKey())) {
                PluginUtil.getKnowhowEntryViewTreeViewer().setSelectionItem(
                        knowhowListViewOperation);
                break;
            } else {
                if (!knowhowListViewOperation.getChildList().isEmpty()) {
                    selectEntryViewItem(categoryId,
                            knowhowListViewOperation.getChildList());
                }
            }
        }
    }
}
