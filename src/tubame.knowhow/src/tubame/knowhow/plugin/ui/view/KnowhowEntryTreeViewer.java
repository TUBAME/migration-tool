/*
 * KnowhowEntryTreeViewer.java
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
package tubame.knowhow.plugin.ui.view;

import java.util.LinkedList;
import java.util.List;

import tubame.common.util.CmnStringUtil;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.AbstractTreeViewer;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DragSource;
import org.eclipse.swt.dnd.Transfer;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import tubame.knowhow.plugin.ui.view.add.AbstractAddItemStrategy;
import tubame.knowhow.plugin.ui.view.add.AddCategoryItem;
import tubame.knowhow.plugin.ui.view.add.AddChecItemInfoItem;
import tubame.knowhow.plugin.ui.view.add.AddKnowhowItem;
import tubame.knowhow.plugin.ui.view.remove.RemoveRelationCategory;
import tubame.knowhow.plugin.ui.view.remove.RemoveRelationCheckItemInfo;
import tubame.knowhow.plugin.ui.view.remove.RemoveRelationItemStrategy;
import tubame.knowhow.plugin.ui.view.remove.RemoveRelationKnowhow;
import tubame.knowhow.plugin.ui.view.replace.ReplacementCheckItem;
import tubame.knowhow.plugin.ui.view.replace.ReplacementItemStrategy;
import tubame.knowhow.plugin.ui.view.replace.ReplacementKnohowItem;
import tubame.knowhow.plugin.ui.view.replace.ReplacementTopLevelItem;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Tree Viewer class know-how of entry.<br/>
 */
public class KnowhowEntryTreeViewer extends TreeViewer implements
        KnowhowEntryTreeViewerOperator {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(KnowhowEntryTreeViewer.class);
    /** Menu identifier */
    private static final String POPUP_MUNE = "#PopupMenu";
    /** UP_ITEM_INDEX */
    private static final int UP_ITEM_INDEX = -1;
    /** DOWN_ITEM_INDEX */
    private static final int DOWN_ITEM_INDEX = +1;
    /** All Items List */
    private List<PortabilityKnowhowListViewOperation> originalItemlist;
    /** Category item list */
    private List<PortabilityKnowhowListViewOperation> categoryList;
    /** Know-how item list */
    private List<PortabilityKnowhowListViewOperation> knowhowList;

    /**
     * Constructor.<br/>
     * 
     * @param parent
     *            Composite
     * @param style
     *            style
     */
    public KnowhowEntryTreeViewer(Composite parent, int style) {
        super(parent, style);
        super.getTree().setHeaderVisible(false);
        // Provider's settings
        super.setContentProvider(new KnowhowEntryViewContentProvider());
        super.setLabelProvider(new KnowhowEntryViewLabelProvider());

        KnowhowEntryTreeViewer.LOGGER.debug("[parent]" + parent + "[style]"
                + style);

        // Cell editing function
        setCellEditModify();
        // Create a drag source
        DragSource source = new DragSource(super.getControl(), DND.DROP_MOVE
                | DND.DROP_COPY);
        // Specifies the data type to be transferred in DragSource
        // There must match the drop time
        Transfer[] types = new Transfer[] { LocalSelectionTransfer
                .getTransfer() };
        source.setTransfer(types);
        // Set the drag listener
        source.addDragListener(new KnowhowViewDragSourceListener(this));
        makeMenu(this);
    }

    /**
     * Set the cell editing feature of the subject name change when clicked.<br/>
     * 
     */
    private void setCellEditModify() {
        String[] subject = new String[] { ResourceUtil.subject };
        super.setColumnProperties(subject);
        CellEditor[] editors = new CellEditor[] { new TextCellEditor(
                super.getTree()), };
        super.setCellEditors(editors);
        super.setCellModifier(new KnowhowEntryCellModifier(this));
    }

    /**
     * Setting the right-click menu control, setting the column.<br/>
     * 
     * @param treeViewer
     *            Tree view
     * 
     */
    private void makeMenu(TreeViewer treeViewer) {
        KnowhowEntryTreeViewer.LOGGER.debug("[treeViewer]" + treeViewer);

        // Set the menu to open the right-click
        MenuManager menuMgr = new MenuManager(POPUP_MUNE);
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new ContextMenuListener(this));
        Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeEntryItem(
            PortabilityKnowhowListViewOperation knowhowListViewData) {
        KnowhowEntryTreeViewer.LOGGER.debug("[entry]" + knowhowListViewData);
        KnowhowEntryTreeViewer.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_ENTRY_REMOVE));
        // Erase myself from parent
        if (knowhowListViewData != null) {
            RemoveRelationItemStrategy relationItemStrategy = getRemoveStrategy(knowhowListViewData);
            if (knowhowListViewData.getParent() != null) {
                List<PortabilityKnowhowListViewOperation> childList = knowhowListViewData
                        .getParent().getChildList();
                childList.remove(knowhowListViewData);
                super.remove(knowhowListViewData);
            } else {
                List<PortabilityKnowhowListViewOperation> topLevelList = getInputEntry();
                if (topLevelList != null) {
                    topLevelList.remove(knowhowListViewData);
                }
            }
            relationItemStrategy.removeRelationItem();
            refreshTreeViewer(true);
        }
    }

    /**
     * Get the strategy class of items removed.<br/>
     * Get Delete pattern strategy class of any item.<br/>
     * 
     * @param knowhowListViewData
     *            Element of the target
     * @return Delete strategy class
     */
    private RemoveRelationItemStrategy getRemoveStrategy(
            PortabilityKnowhowListViewOperation knowhowListViewData) {
        if (knowhowListViewData.isCategory()) {
            return new RemoveRelationCategory(knowhowListViewData);
        } else if (knowhowListViewData.isKnowhow()) {
            return new RemoveRelationKnowhow(knowhowListViewData);
        } else {
            return new RemoveRelationCheckItemInfo(knowhowListViewData);
        }
    }

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public List<PortabilityKnowhowListViewOperation> getInputEntry() {
        KnowhowEntryTreeViewer.LOGGER.debug(CmnStringUtil.EMPTY);
        return (List<PortabilityKnowhowListViewOperation>) super.getInput();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setEntryListData(
            List<PortabilityKnowhowListViewOperation> knowhowListViewOperations) {
        Object[] expandedNodes = this.getExpandedElements();
        super.setInput(knowhowListViewOperations);
        // Registration information and not Null, the retention of the Tree
        // state if it is not the initial display when
        if (knowhowListViewOperations != null) {
            refreshTreeViewer(true);
            this.setExpandedElements(expandedNodes);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void refreshTreeViewer(boolean dirty) {
        KnowhowEntryTreeViewer.LOGGER.debug("[dirty]" + dirty);
        super.refresh();
        if (isRefreshTreeViewer() && dirty) {
            PluginUtil.getKnowhowEditor().setDirty(dirty);
        }
    }

    /**
     * Judgment entry know-how view is either refresh possible.<br/>
     * Return of true refreshable case.
     * 
     * @return true:Refreshable false:Not refresh
     */
    private boolean isRefreshTreeViewer() {
        if (PluginUtil.getActivePage() == null) {
            return false;
        }
        if (PluginUtil.getKnowhowEditor() == null) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addRegisterItem(
            PortabilityKnowhowListViewOperation addParentEntry,
            PortabilityKnowhowListViewOperation addEntry) {
        KnowhowEntryTreeViewer.LOGGER.debug("[addParentEntry]" + addParentEntry
                + "[addEntry]" + addEntry);

        // Add the parent element explicitly by the relationship of the copy and
        // paste function
        addEntry.setParent(addParentEntry);
        splitKnowhowEntryList(addParentEntry);
        AbstractAddItemStrategy addItemStrategy = null;

        if (addEntry.isKnowhow()) {
            addItemStrategy = new AddCategoryItem(originalItemlist,
                    categoryList, knowhowList);
        } else if (addEntry.isCategory()) {
            addItemStrategy = new AddKnowhowItem(originalItemlist,
                    categoryList, knowhowList);
        } else {
            addItemStrategy = new AddChecItemInfoItem(originalItemlist,
                    categoryList, knowhowList);
        }
        addItemStrategy.setReferenceKey(addParentEntry, addEntry);
        addItemStrategy.addItem(addEntry);
        setSelectionItem(addEntry);
        refreshTreeViewer(false);
    }

    /**
     * The know-how the entire list, and divided into segments category.<br/>
     * 
     * @param addParentEntry
     *            Parent hierarchy entry
     */
    private void splitKnowhowEntryList(
            PortabilityKnowhowListViewOperation addParentEntry) {

        originalItemlist = addParentEntry.getChildList();
        categoryList = getExtractedCategoryList(addParentEntry.getChildList(),
                PortabilityKnowhowListViewData.LEVEL_FIRST);
        knowhowList = getExtractedCategoryList(addParentEntry.getChildList(),
                PortabilityKnowhowListViewData.LEVEL_SECOND);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public PortabilityKnowhowListViewOperation getOneSelection() {
        KnowhowEntryTreeViewer.LOGGER.debug(CmnStringUtil.EMPTY);
        IStructuredSelection structuredSelection = (IStructuredSelection) this
                .getSelection();
        return (PortabilityKnowhowListViewOperation) structuredSelection
                .getFirstElement();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setSelectionItem(PortabilityKnowhowListViewOperation entry) {
        KnowhowEntryTreeViewer.LOGGER.debug("[entry]" + entry);
        // Process of focus to items
        this.setSelection(new StructuredSelection(entry), true);
        expandToLevel(entry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void upItem() {
        move(getOneSelection(), UP_ITEM_INDEX);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void downItem() {
        move(getOneSelection(), DOWN_ITEM_INDEX);
    }

    /**
     * Move the order of the child elements of know-how that has been specified.<br/>
     * 
     * @param selectedEntry
     *            Element of the target
     * @param rowIndex
     *            Additional Indexes
     */
    private void move(PortabilityKnowhowListViewOperation selectedEntry,
            int rowIndex) {
        KnowhowEntryTreeViewer.LOGGER.debug("[selectedEntry]" + selectedEntry
                + "[rowIndex]" + rowIndex);
        if (isTargetReplacement(selectedEntry)) {
            Object[] expandedNodes = this.getExpandedElements();
            ReplacementItemStrategy replacementItemStrategy = getReplacementStrategy(selectedEntry);
            if (replacementItemStrategy != null) {
                replacementItemStrategy.replacementSelectItem(selectedEntry,
                        rowIndex);
                KnowhowEntryTreeViewer.LOGGER.info(MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.LOG_MOVE_ITEM));
            }
            refreshTreeViewer(true);
            this.setExpandedElements(expandedNodes);

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
     * Get the movement pattern strategy class of any item.<br/>
     * 
     * @param selectedEntry
     *            Element of the target
     * @return Movement strategy class
     */
    private ReplacementItemStrategy getReplacementStrategy(
            PortabilityKnowhowListViewOperation selectedEntry) {
        ReplacementItemStrategy replacementItemStrategy = null;
        if (selectedEntry.getParent() == null) {
            // Movement of the top hierarchy
            replacementItemStrategy = new ReplacementTopLevelItem();
        } else {
            splitKnowhowEntryList(selectedEntry.getParent());
            if (selectedEntry.isCategory()) {
                // Reordering category
                replacementItemStrategy = new ReplacementKnohowItem(
                        originalItemlist, categoryList, knowhowList);
            } else if (selectedEntry.isKnowhow()) {
                // Reordering of know-how
                replacementItemStrategy = new ReplacementKnohowItem(
                        originalItemlist, knowhowList, categoryList);
            } else if (selectedEntry.isCheckItem()) {
                // Reordering of check items items
                replacementItemStrategy = new ReplacementCheckItem(
                        originalItemlist);
            }
        }
        return replacementItemStrategy;
    }

    /**
     * Get a list with hierarchy information that is specified in the level.<br/>
     * 
     * @param originalCategorylist
     *            Original category list
     * @param level
     *            Hierarchical level
     * @return Extraction results list
     */
    private List<PortabilityKnowhowListViewOperation> getExtractedCategoryList(
            List<PortabilityKnowhowListViewOperation> originalCategorylist,
            int level) {
        List<PortabilityKnowhowListViewOperation> categoryList = new LinkedList<PortabilityKnowhowListViewOperation>();

        for (PortabilityKnowhowListViewOperation knowhowListViewData : originalCategorylist) {
            if (level == knowhowListViewData.getLevel()) {
                categoryList.add(knowhowListViewData);
            }
        }
        return categoryList;
    }

    /**
     * Determination whether the moving object relative to the selected item.<br/>
     * 
     * @param selectedEntry
     *            Selected Item
     * @return true:Movable items false:Unmovable items
     */
    private boolean isTargetReplacement(
            PortabilityKnowhowListViewOperation selectedEntry) {
        if (selectedEntry != null) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void expandToLevel(
            PortabilityKnowhowListViewOperation knowhowListViewOperation) {
        KnowhowEntryTreeViewer.LOGGER.debug("[knowhowListViewOperation]"
                + knowhowListViewOperation);
        super.expandToLevel(knowhowListViewOperation,
                AbstractTreeViewer.ALL_LEVELS);
    }

}
