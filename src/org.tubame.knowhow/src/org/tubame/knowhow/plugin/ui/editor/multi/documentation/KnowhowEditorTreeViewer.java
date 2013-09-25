/*
 * KnowhowEditorTreeViewer.java
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
package org.tubame.knowhow.plugin.ui.editor.multi.documentation;

import java.util.List;

import org.tubame.common.util.CmnStringUtil;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTarget;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.plugin.model.editor.AbstractEntry;
import org.tubame.knowhow.plugin.model.editor.EntryOperator;
import org.tubame.knowhow.plugin.ui.editor.multi.EditorDirty;
import org.tubame.knowhow.plugin.ui.editor.multi.listener.ContextMenuListener;
import org.tubame.knowhow.plugin.ui.editor.multi.listener.DocumentationTagDropTargetListener;
import org.tubame.knowhow.plugin.ui.editor.multi.listener.SelectEntryViewItemListenter;

/**
 * Document heading tree viewer class.<br/>
 */
public class KnowhowEditorTreeViewer extends TreeViewer implements
        EditorTreeViewerOperator {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(KnowhowEditorTreeViewer.class);
    /** Menu identifier */
    private static final String POPUP_MUNE = "#PopupMenu";
    /** On migration index */
    private static final int UP_INDEX = -1;
    /** Under migration index */
    private static final int DOWN_INDEX = 1;
    /** Presence or absence of storage */
    private boolean dirty;
    /** Editor parent information */
    private EditorDirty editor;
    /** Page information */
    private DocumentationFormPage documentationFormPage;

    /**
     * Constructor.<br/>
     * 
     * @param documentationFormPage
     *            Page information
     * @param parent
     *            Composite
     * @param style
     *            style
     */
    public KnowhowEditorTreeViewer(DocumentationFormPage documentationFormPage,
            Composite parent, int style) {
        super(parent, style);
        LOGGER.debug("[editor]" + documentationFormPage + "[parent]" + parent
                + "[style]" + style);
        this.documentationFormPage = documentationFormPage;
        this.editor = documentationFormPage.getEditorEitry();
        // Provider's settings
        super.setContentProvider(new EntryContentProvider());
        super.setLabelProvider(new EntryLabelProvider());
        super.addSelectionChangedListener(new ISelectionChangedListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                setEnableButton();
            }
        });
        this.addDoubleClickListener(new SelectEntryViewItemListenter());
        // Setting the drop listener
        setDropListener(this);
        makeMenu(this);
    }

    /**
     * This is done and set in the right-click menu control, the setting of the
     * column.<br/>
     * 
     * @param treeViewer
     *            Tree view
     * 
     */
    private void makeMenu(TreeViewer treeViewer) {
        LOGGER.debug("[treeViewer]" + treeViewer);

        // Set the menu to open the right-click
        MenuManager menuMgr = new MenuManager(POPUP_MUNE);
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new ContextMenuListener(this));
        Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);

    }

    /**
     * Set the data format listeners drop in the tree view.<br/>
     * 
     * @param treeViewer
     *            Tree view
     * 
     */
    private void setDropListener(TreeViewer treeViewer) {
        LOGGER.debug("[treeViewer]" + treeViewer);

        // Set the drop target on top of the tree view
        DropTarget target = new DropTarget(treeViewer.getControl(),
                DND.DROP_MOVE | DND.DROP_COPY | DND.DROP_DEFAULT);

        // Set the format to receive data
        LocalSelectionTransfer localSelectionTransfer = LocalSelectionTransfer
                .getTransfer();
        target.setTransfer(new Transfer[] { localSelectionTransfer });

        // Set the drop listener
        target.addDropListener(new DocumentationTagDropTargetListener(
                (EditorTreeViewerOperator) treeViewer, localSelectionTransfer));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEntryItem(EntryOperator entry) {
        LOGGER.debug("[entry]" + entry);

        // Erase myself from parent
        if (entry.getParentEntry() != null) {
            List<EntryOperator> childList = entry.getParentEntry()
                    .getEntryChildren();
            childList.remove(entry);
            super.remove(entry);
        } else {
            List<EntryOperator> topLevelList = getInputEntry();
            topLevelList.remove(entry);
        }
        refreshTreeViewer(true);
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<EntryOperator> getInputEntry() {
        return (List<EntryOperator>) super.getInput();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEntryListData(List<EntryOperator> entryList) {
        super.setInput(entryList);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshTreeViewer(boolean type) {
        LOGGER.debug("[type]" + type);
        dirty = type;
        // Notify parent
        editor.setDirty(type);
        super.refresh();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addEntryItem(EntryOperator addParentEntry,
            EntryOperator addEntry) {
        LOGGER.debug("[addParentEntry]" + addParentEntry + "[addEntry]"
                + addEntry);
        EntryOperator addTargetEntry = addParentEntry
                .getFindEntry(getInputEntry());
        if (addTargetEntry != null) {
            addEntry.setParentEntry(addTargetEntry);
            addTargetEntry.addChildren(addEntry);
            refreshTreeViewer(true);
            // Expand the additional elements
            super.expandToLevel(addEntry, AbstractTreeViewer.ALL_LEVELS);
        } else {
            List<EntryOperator> entryList = getInputEntry();
            entryList.add(addEntry);
            this.setEntryListData(entryList);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public EntryOperator getOneSelection() {
        LOGGER.debug(CmnStringUtil.EMPTY);

        IStructuredSelection structuredSelection = (IStructuredSelection) this
                .getSelection();
        return (AbstractEntry) structuredSelection.getFirstElement();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upItem() {
        move(getOneSelection(), UP_INDEX);
        setEnableButton();
    }

    /**
     * Move the order of the Child elements of the Entry specified.<br/>
     * 
     * @param selectedEntry
     *            Element of the target
     * @param rowIndex
     *            Additional Indexes
     */
    private void move(EntryOperator selectedEntry, int rowIndex) {
        LOGGER.debug("[selectedEntry]" + selectedEntry + "[rowIndex]"
                + rowIndex);

        Object[] expandedNodes = this.getExpandedElements();
        if (isTargetReplacement(selectedEntry)) {
            if (selectedEntry.isChapter()) {
                // Change the order of the top hierarchy
                chapterReplacement(selectedEntry, rowIndex);
            } else if (selectedEntry.isCategory()) {
                // Reordering of Child hierarchy
                categoryReplacement(selectedEntry, rowIndex);
            }
        }
        this.setExpandedElements(expandedNodes);
    }

    /**
     * Determining whether the moving object.<br/>
     * 
     * @param selectedEntry
     *            EntryOperator
     * @return true:Moving target false:moved outside
     */
    private boolean isTargetReplacement(EntryOperator selectedEntry) {
        if (selectedEntry != null) {
            return true;
        }
        return false;
    }

    /**
     * Replace the heading order of top hierarchy.<br/>
     * 
     * @param selectedEntry
     *            EntryOperator
     * @param rowIndex
     *            Move number
     */
    private void chapterReplacement(EntryOperator selectedEntry, int rowIndex) {
        List<EntryOperator> childList = getInputEntry();
        int index = childList.indexOf(selectedEntry);
        if (isReplacementItem(index, rowIndex, childList.size())) {
            EntryOperator afterEntry = childList.set(rowIndex + index,
                    selectedEntry);
            childList.set(index, afterEntry);
            refreshTreeViewer(true);
            LOGGER.info(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_MOVE_ITEM));
        }
    }

    /**
     * Determine whether a movable item to be moved. <br/>
     * The return of true movable case.<br/>
     * 
     * @param index
     *            Migration index
     * @param rowIndex
     *            Line index
     * @param listSize
     *            List size
     * @return true:Movable false:Unmovable
     */
    private boolean isReplacementItem(int index, int rowIndex, int listSize) {
        if (rowIndex + index < listSize && 0 <= rowIndex + index) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    public void setEnableButton() {

        List<EntryOperator> childList = null;
        boolean upButtonEnabled = true;
        boolean downButtonEnabled = true;

        if (getOneSelection() != null
                && getOneSelection().getParentEntry() != null) {
            childList = getOneSelection().getParentEntry().getEntryChildren();
        } else {
            childList = getInputEntry();
        }
        if (!isReplacementItem(UP_INDEX, childList.indexOf(getOneSelection()),
                childList.size())) {
            upButtonEnabled = false;
        }
        if (!isReplacementItem(DOWN_INDEX,
                childList.indexOf(getOneSelection()), childList.size())) {
            downButtonEnabled = false;
        }

        documentationFormPage.updownEnabled(upButtonEnabled, downButtonEnabled);
    }

    /**
     * Replace the category order of the child hierarchy.<br/>
     * 
     * @param selectedEntry
     *            EntryOperator
     * @param rowIndex
     *            Move number
     */
    private void categoryReplacement(EntryOperator selectedEntry, int rowIndex) {

        List<EntryOperator> childList = selectedEntry.getParentEntry()
                .getEntryChildren();
        int index = childList.indexOf(selectedEntry);
        if (rowIndex + index < childList.size() && 0 <= rowIndex + index) {
            EntryOperator afterEntry = childList.set(rowIndex + index,
                    selectedEntry);
            childList.set(index, afterEntry);
            refreshTreeViewer(true);
            LOGGER.info(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_MOVE_ITEM));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void downItem() {
        move(getOneSelection(), DOWN_INDEX);
        setEnableButton();
    }

    /**
     * Get change notification.<br/>
     * 
     * @return dirty
     */
    public boolean isDirty() {
        return dirty;
    }
}
