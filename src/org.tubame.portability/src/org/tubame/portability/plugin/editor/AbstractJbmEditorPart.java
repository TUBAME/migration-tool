/*
 * AbstractJbmEditorPart.java
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
package org.tubame.portability.plugin.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.viewers.CellEditor;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TextCellEditor;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IEditorSite;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.part.EditorPart;
import org.tubame.portability.exception.JbmException;
import org.tubame.portability.model.MigrationEditorRow;
import org.tubame.portability.plugin.view.JbmEditorCellModifier;
import org.tubame.portability.plugin.view.MousePointGetTreeViewer;
import org.tubame.portability.plugin.view.WorkStatusView;
import org.tubame.portability.util.EditorUtil;
import org.tubame.portability.util.PluginUtil;
import org.tubame.portability.util.resource.ApplicationPropertyUtil;
import org.tubame.portability.util.resource.MessageUtil;

/**
 * Base class of search results editing Editor.<br/>
 * Common process of editing processing Editor.<br/>
 */
public abstract class AbstractJbmEditorPart extends EditorPart implements
        MigrationEditorOperation {

    /**
     * Selected row change listener
     */
    private SelectionChangedListener changedListener = null;

    /**
     * Double-click listener
     */
    private IDoubleClickListener doubleClickListener;

    /**
     * Check editing of whether it has been
     */
    private boolean dirty;
    /**
     * Display data to be set to view
     */
    private final List<MigrationEditorRow> jbmEditorData = new ArrayList<MigrationEditorRow>();

    /**
     * Save your edits.<br/>
     * If the data that is displayed on the screen is saved, override this
     * method.<br/>
     * 
     * @param fileName
     *            File name
     * @param treeViewer
     *            Tree data on the screen
     * @return true:Successful save false:Save failure
     */
    public abstract boolean save(String fileName, TreeViewer treeViewer);

    /**
     * The Masu read the data to be displayed in the edit screen.<br/>
     * For reading the data to be displayed, override this method.<br/>
     * 
     * @param fileName
     *            File name
     * @return Screen display list
     * @throws JbmException
     *             File read failure
     */
    public abstract List<MigrationEditorRow> load(String fileName)
            throws JbmException;

    /**
     * Register the mouse context menu.<br/>
     * 
     * @param treeViewer
     *            Tree data on the screen
     * 
     */
    public abstract void contextMenu(TreeViewer treeViewer);

    /**
     * Add a column to be used in the edit screen.<br/>
     * 
     * @param tree
     *            Add Tree
     */
    public abstract void addColumn(Tree tree);

    /**
     * Get a double-click listener for each editor.<br/>
     * 
     * @param editor
     *            Editor
     * 
     * @return Double-click listener
     */
    public abstract IDoubleClickListener getDoubleClickListener(
            MigrationEditorOperation editor);

    /**
     * Title acquisition of error dialog display.<br/>
     * 
     * @return Title
     */
    @Override
    public abstract String getTitle();

    /**
     * Mouse click detection viewer
     */
    private MousePointGetTreeViewer treeViewer = null;

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSave(IProgressMonitor monitor) {
        // Get the path to the file you want to save
        StringBuilder sb = new StringBuilder();
        sb.append(EditorUtil.getWorkSpace(this));
        sb.append(EditorUtil.getEditorOpenFileFullPath(this));
        if (save(sb.toString(), getTreeViewer())) {
            // The false flag editing
            setDirty(false);
            // Refresh
            PluginUtil.refreshFile(sb.toString(), monitor);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doSaveAs() {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IEditorSite site, IEditorInput input)
            throws PartInitException {

        preInit();
        super.setSite(site);
        super.setInput(input);

        // Get File information selected
        IFileEditorInput fileInput = (IFileEditorInput) super.getEditorInput();
        super.setPartName(fileInput.getFile().getName());

        // Get the absolute path of the file
        String fileLocation = fileInput.getFile().getLocation().toString();

        // Reads the CSV, set to model
        try {
            jbmEditorData.addAll(load(fileLocation));
            if (jbmEditorData.isEmpty()) {
                String message = String.format(MessageUtil.FILE_NOT_DATA,
                        fileInput.getFile().toString());

                PluginUtil.viewErrorDialog(getTitle(), message, null);
            }

        } catch (JbmException e) {
            throw new PartInitException(e.getViewMessage(), e);
        }
    }

    /**
     * The process before the init method.<br/>
     * 
     */
    public void preInit() {
        // no oparation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isSaveAsAllowed() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(Composite parent) {

        // Generate a tree view of the mouse position detection
        treeViewer = new MousePointGetTreeViewer(parent, SWT.VIRTUAL
                | SWT.FULL_SELECTION | SWT.MULTI);
        changedListener = new SelectionChangedListener(this);
        doubleClickListener = getDoubleClickListener(this);

        makeColumn();

        // Register a variety of listener
        addListner(treeViewer);
        // Register the content provider
        treeViewer.setContentProvider(new JbmSearchResultContentProvider());
        treeViewer.setLabelProvider(new JbmSearchResultLabelProvider());

        // Set to the tree model
        treeViewer.setInput(jbmEditorData);

        // Line number column is editable
        String[] properties = new String[] { "", "", "", "", "", "", "", "",
                "", "", "", "", ApplicationPropertyUtil.EDIT_COLUMN_LINENUM,
                ApplicationPropertyUtil.EDIT_COLUMN_LINENUMCONTENTS, "" };
        treeViewer.setColumnProperties(properties);
        CellEditor[] editors = new CellEditor[] { null, null, null, null, null,
                null, null, null, null, null, null, null,
                new TextCellEditor(treeViewer.getTree()),
                new TextCellEditor(treeViewer.getTree()), null };
        treeViewer.setCellEditors(editors);
        treeViewer.setCellModifier(new JbmEditorCellModifier(treeViewer));
        postCreatePartControl();
    }

    /**
     * Method called after reading createPartControl.<br/>
     * When processing is performed after initialization, override this method.<br/>
     * 
     */
    public void postCreatePartControl() {
        // no operation
    }

    /**
     * And setting the right-click menu control, setting the column.<br/>
     * 
     */
    private void makeColumn() {
        // Set the menu to open the right-click
        contextMenu(getTreeViewer());
        // Perform the display settings of the header, etc.
        Tree tree = treeViewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        tree.setSortDirection(SWT.DOWN);
        // Do the registration of the column
        addColumn(tree);
    }

    /**
     * Generate tree column sort of listener with.<br/>
     * 
     * @param tree
     *            Tree you want to add
     * @param title
     *            Column title
     * @param mode
     *            SWT mode
     * @param width
     *            Size
     * @param listener
     *            Listener to be added
     * @return Tree column
     */
    protected TreeColumn createTreeColumn(Tree tree, String title, int mode,
            int width, SelectionListener listener) {
        TreeColumn column = new TreeColumn(tree, mode);
        column.setText(title);
        column.setWidth(width);
        if (listener != null) {
            column.addSelectionListener(listener);
        }
        return column;
    }

    /**
     * Register a listener to various treeViewer.<br/>
     * Listener to be registered is the kind of following.<br/>
     * 
     * {@link SelectionChangedListener} {@link JbmEditorDoubleClickListener}
     * 
     * @param treeViewer
     *            Tree view to register a listener
     */
    private void addListner(TreeViewer treeViewer) {
        // Listener Registration of row selected
        treeViewer.addSelectionChangedListener(changedListener);
        // Listener registration when double-clicked
        treeViewer.addDoubleClickListener(doubleClickListener);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setDirty(boolean dirty) {
        if (this.dirty != dirty) {
            this.dirty = dirty;
            WorkStatusView.out(dirty, null);
            super.firePropertyChange(PROP_DIRTY);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Point getMouseClickPoint() {
        return treeViewer.getMouseClickPoint();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getSelectionObject() {
        return changedListener.getSelectionItem();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TreeViewer getTreeViewer() {
        return treeViewer;
    }
}
