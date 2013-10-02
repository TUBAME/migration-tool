/*
 * EditorTreeViewerOperator.java
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
package tubame.knowhow.plugin.ui.editor.multi.documentation;

import java.util.List;

import org.eclipse.jface.viewers.ISelectionChangedListener;

import tubame.knowhow.plugin.model.editor.EntryOperator;

/**
 * Interface of know-how heading for the GUI tree viewer.<br/>
 */
public interface EditorTreeViewerOperator {
    /**
     * After deleting items, I will do the refresh.<br/>
     * 
     * @param entry
     *            Deleted items
     */
    public void removeEntryItem(EntryOperator entry);

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
     * Make the activation / deactivation of setting up and down buttons.<br/>
     * 
     */
    public void setEnableButton();

    /**
     * After adding items, I will do the refresh.<br/>
     * 
     * @param addParentEntry
     *            Parent of the item you want to add
     * @param addEntry
     *            Add a Item
     */
    public void addEntryItem(EntryOperator addParentEntry,
            EntryOperator addEntry);

    /**
     * Set the data that was added to the TreeViere.<br/>
     * 
     * @param entryList
     *            List of Entry
     */
    public void setEntryListData(List<EntryOperator> entryList);

    /**
     * To register the selection change listener.<br/>
     * 
     * @param iSelectionChangedListener
     *            Selection listener
     */
    public void addSelectionChangedListener(
            ISelectionChangedListener iSelectionChangedListener);

    /**
     * Updated the tree viewer.<br/>
     * 
     * @param type
     *            true:Data state change false:No data state change
     */
    public void refreshTreeViewer(boolean type);

    /**
     * Get the selected row in a TreeViewer.<br/>
     * 
     * @return Entry
     */
    public EntryOperator getOneSelection();

    /**
     * Acquisition of the display data (other than category).<br/>
     * 
     * @return List of Entry
     */
    public List<EntryOperator> getInputEntry();
}
