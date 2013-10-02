/*
 * AbstractDocumentationEditorDrop.java
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
package tubame.knowhow.plugin.ui.editor.multi.documentation.drop;

import tubame.knowhow.plugin.model.editor.EntryOperator;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.editor.multi.documentation.EditorTreeViewerOperator;

/**
 * Base class of mouse drop processing.<br/>
 */
public abstract class AbstractDocumentationEditorDrop implements EditorDrop {
    /** Tree view */
    private EditorTreeViewerOperator treeViewer;
    /** Entry drop target (Entry to become the parent of additional items) */
    private EntryOperator dropTargetEntry;

    /**
     * Constructor.<br/>
     * 
     * @param treeViewer
     *            Tree view
     */
    public AbstractDocumentationEditorDrop(EditorTreeViewerOperator treeViewer) {
        this.treeViewer = treeViewer;
    }

    /**
     * Generate each Entry from the drag source data.<br/>
     * 
     * @param dragViewdata
     *            Target PortabilityKnowhowListViewOperation
     * @return Various Entry to add
     */
    protected abstract EntryOperator getEntry(
            PortabilityKnowhowListViewOperation dragViewdata);

    /**
     * {@inheritDoc}
     */
    @Override
    public void add(EntryOperator addParentEntry,
            PortabilityKnowhowListViewOperation dragViewdata) {

        dropTargetEntry = addParentEntry;
        treeViewer.addEntryItem(addParentEntry, getEntry(dragViewdata));
    }

    /**
     * Get (Entry to be the parent of the additional items) Entry of the drop
     * target.<br/>
     * 
     * @return Entry drop target (Entry to become the parent of additional
     *         items)
     */
    public EntryOperator getDropTargetEntry() {
        return dropTargetEntry;
    }

    /**
     * Get treeViewer.<br/>
     * 
     * @return treeViewer
     */
    public EditorTreeViewerOperator getTreeViewer() {
        return treeViewer;
    }

    /**
     * Determines whether the drag-and-drop possible.<br/>
     * Drop target is return true if category or heading.<br/>
     * 
     * @param dropTargetEntryDrop
     *            Shipping
     * @return true:In the case of heading ro category false:Other
     */
    public boolean isDraggable(EntryOperator dropTargetEntry) {
        if (dropTargetEntry.isChapter() || dropTargetEntry.isCategory()) {
            return true;
        }
        return false;
    }

}
