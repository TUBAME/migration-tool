/*
 * DocumentationTagDropTargetListener.java
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


import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.TreeSelection;
import org.eclipse.swt.dnd.DND;
import org.eclipse.swt.dnd.DropTargetEvent;
import org.eclipse.swt.dnd.DropTargetListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.exception.JbmException;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.plugin.model.editor.AbstractEntry;
import org.tubame.knowhow.plugin.model.editor.EntryOperator;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewData;
import org.tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import org.tubame.knowhow.plugin.ui.editor.multi.documentation.EditorTreeViewerOperator;
import org.tubame.knowhow.plugin.ui.editor.multi.documentation.drop.DropCategory;
import org.tubame.knowhow.plugin.ui.editor.multi.documentation.drop.EditorDrop;
import org.tubame.knowhow.util.PluginUtil;

/**
 * To register an event of drop event occurs in the tree view.<br/>
 */
public class DocumentationTagDropTargetListener implements DropTargetListener {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocumentationTagDropTargetListener.class);
    /** Original data being dragged (know-how information objects). */
    private PortabilityKnowhowListViewData dragViewdata;
    /** Tree view */
    private EditorTreeViewerOperator treeViewer;
    /** Data format drop target receives */
    private LocalSelectionTransfer localSelectionTransfer;

    /**
     * Constructor.<br/>
     * Set tree view, the data format that you receive.<br/>
     * 
     * @param treeViewer
     *            Tree view of the drop target
     * @param localSelectionTransfer
     *            Data format drop target receives
     */
    public DocumentationTagDropTargetListener(
            EditorTreeViewerOperator treeViewer,
            LocalSelectionTransfer localSelectionTransfer) {
        this.treeViewer = treeViewer;
        this.localSelectionTransfer = localSelectionTransfer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragEnter(DropTargetEvent event) {
        // Event that occurs when you enter the drop area
        TreeSelection selection = (TreeSelection) localSelectionTransfer
                .getSelection();
        dragViewdata = (PortabilityKnowhowListViewData) selection
                .getFirstElement();

        if (event.detail == DND.DROP_DEFAULT) {
            if ((event.operations & DND.DROP_COPY) != 0) {
                event.detail = DND.DROP_COPY;
            } else {
                // It does not occur if there is no drag-and-drop operation
                event.detail = DND.DROP_NONE;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragLeave(DropTargetEvent event) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragOperationChanged(DropTargetEvent event) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragOver(DropTargetEvent event) {
        // Events continue to occur until the mouse up since the beginning of
        // the drop area

        event.feedback = DND.FEEDBACK_SELECT | DND.FEEDBACK_SCROLL
                | DND.FEEDBACK_EXPAND;

        if (event.item != null) {
            // Get the tree items that are currently on mouse expertise editor
            // (documentation tab)
            // If it is not a drop where possible, to drop disables the cursor
            // icon
            if (!isDropEnable((AbstractEntry) event.item.getData())) {
                event.detail = DND.DROP_NONE;
            } else {
                event.detail = DND.DROP_COPY;
            }
        } else {
            // If the tree item is not present in the mouse on the part of the
            // current (margin)
            event.detail = DND.DROP_NONE;
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void drop(DropTargetEvent event) {
        // Events that occur at the moment it is up mouse
        if (localSelectionTransfer.isSupportedType(event.currentDataType)) {
            // Data of the place that has been dropped in (the documentation
            // tab) editor know-how
            EntryOperator dropTargetEndty = null;
            if (event.item != null) {
                dropTargetEndty = (EntryOperator) event.item.getData();
            }
            // If possible drop, add the drag element in the tree
            addDragTarget(dropTargetEndty);

            DocumentationTagDropTargetListener.LOGGER
                    .info(MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.LOG_DROP_ITEM));
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dropAccept(DropTargetEvent event) {
        // no operation
    }

    /**
     * Examine the tree items in the drop and target elements currently being
     * dragged, Get whether drop possible.<br/>
     * 
     * @param dropTargetEntry
     *            Tree item drop target (Entry)
     * @return true:Droppable false:Drop ban
     */
    private boolean isDropEnable(EntryOperator dropTargetEntry) {
        EditorDrop drop = getDocumentEditorDrop(dragViewdata.getLevel());
        if (drop != null) {
            return drop.isDropEnable(dropTargetEntry, dragViewdata);
        }
        return false;
    }

    /**
     * Add the dragged element if it is determined whether the drop holds, it
     * holds.<br/>
     * 
     * @param dropTargetEntry
     *            Tree item drop target (Entry)
     */
    private void addDragTarget(EntryOperator dropTargetEntry) {
        // Only if the drop is established, added drag element
        EditorDrop drop = getDocumentEditorDrop(dragViewdata.getLevel());
        if (drop != null) {
            if (drop.isDropEnable(dropTargetEntry, dragViewdata)) {
                drop.add(dropTargetEntry, dragViewdata);
            } else {
                JbmException
                        .outputExceptionLog(
                                LOGGER,
                                null,
                                MessagePropertiesUtil
                                        .getMessage(MessagePropertiesUtil.ERROR_DROP_ELEMENT));
                ErrorDialog.openErrorDialog(PluginUtil
                        .getActiveWorkbenchShell(), null, MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.ERROR_DROP_ELEMENT));
            }
        }
    }

    /**
     * If the drag source is a category, and get the Drop operation class.<br/>
     * 
     * @param level
     *            Level
     * @return EditorDrop
     */
    private EditorDrop getDocumentEditorDrop(int level) {
        EditorDrop drop = null;

        if (dragViewdata.getLevel() == PortabilityKnowhowListViewData.LEVEL_FIRST) {
            drop = new DropCategory(treeViewer);
        }
        return drop;
    }

}
