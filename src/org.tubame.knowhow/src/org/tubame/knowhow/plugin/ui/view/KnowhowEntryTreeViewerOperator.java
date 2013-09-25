/*
 * KnowhowEntryTreeViewerOperator.java
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
package org.tubame.knowhow.plugin.ui.view;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;

/**
 * Interface of know-how entry tree viewer.<br/>
 */
public interface KnowhowEntryTreeViewerOperator {
    /**
     * After deleting the item and conducted a refresh.<br/>
     * 
     * @param knowhowListViewData
     *            Deleted items
     */
    public void removeEntryItem(
            PortabilityKnowhowListViewOperation knowhowListViewData);

    /**
     * Move up one item.<br/>
     * 
     */
    public void upItem();

    /**
     * Move down one item.<br/>
     * 
     */
    public void downItem();

    /**
     * After adding items, do the refresh.<br/>
     * 
     * @param addParentEntry
     *            Parent of the item to be added
     * @param addEntry
     *            Add a Item
     */
    public void addRegisterItem(
            PortabilityKnowhowListViewOperation addParentEntry,
            PortabilityKnowhowListViewOperation addEntry);

    /**
     * Data is set to TreeViewer.<br/>
     * 
     * @param knowhowListViewOperations
     *            List<PortabilityKnowhowListViewOperation>
     */
    public void setEntryListData(
            List<PortabilityKnowhowListViewOperation> knowhowListViewOperations);

    /**
     * Register the selection change listener.<br/>
     * 
     * @param iSelectionChangedListener
     *            Selection listener
     */
    public void addSelectionChangedListener(
            ISelectionChangedListener iSelectionChangedListener);

    /**
     * Updated the tree viewer.<br/>
     * 
     * @param dirty
     *            Change notification
     */
    public void refreshTreeViewer(boolean dirty);

    /**
     * Get the selected row in a TreeViewer.<br/>
     * 
     * @return PortabilityKnowhowListViewOperation
     */
    public PortabilityKnowhowListViewOperation getOneSelection();

    /**
     * Acquisition of the display data(Excluding Department).<br/>
     * 
     * @return List<PortabilityKnowhowListViewOperation>
     */
    public List<PortabilityKnowhowListViewOperation> getInputEntry();

    /**
     * Open the tree hierarchy recursively the entry specified.<br/>
     * 
     * @param knowhowListViewOperation
     *            PortabilityKnowhowListViewOperation
     */
    public void expandToLevel(
            PortabilityKnowhowListViewOperation knowhowListViewOperation);

    /**
     * Set to select an item state.<br/>
     * 
     * @param entry
     *            PortabilityKnowhowListViewOperation
     */
    public void setSelectionItem(PortabilityKnowhowListViewOperation entry);
}
