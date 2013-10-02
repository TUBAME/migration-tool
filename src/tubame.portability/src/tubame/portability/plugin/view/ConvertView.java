/*
 * ConvertView.java
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
package tubame.portability.plugin.view;

import java.util.List;

import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.preference.IPreferenceStore;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.TreeViewerColumn;
import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.Activator;
import tubame.portability.exception.JbmException;
import tubame.portability.logic.ConvertAccessFactory;
import tubame.portability.model.generated.model.JbossMigrationConvertTool;
import tubame.portability.model.generated.model.MigrationItem;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.MessageUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Transformation view class.<br/>
 * It displays the following items.<br/>
 * 
 * Conversion item Degree of importance Conversion result
 */
public class ConvertView extends ViewPart {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConvertView.class);

    /**
     * Display last path
     */
    private static final String FILE_PATH = "CONVERT_OPEN_STATUSFILE_PATH";
    /**
     * Column size conversion result
     */
    private static final int RESULT_COLUMN_SIZE = 75;
    /**
     * Item column size before conversion
     */
    private static final int CHANGE_ITEM_BEFORE_COLUMN_SIZE = 350;
    /**
     * Converted item column size
     */
    private static final int CHANGE_ITEM_AFTER_COLUMN_SIZE = 350;
    /**
     * No column size
     */
    private static final int NO_COLUMN_SIZE = 50;
    /**
     * Screen display TreeViewer
     */
    private TreeViewer treeViewer;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(Composite parent) {
        LOGGER.debug("createPartControl");
        treeViewer = new MousePointGetTreeViewer(parent, SWT.FULL_SELECTION);

        treeViewer.setContentProvider(new ConvertContentProvider());
        ConvertToolTipSupport.getInstance(treeViewer);

        treeViewer.setLabelProvider(new ConvertLabelProvider());

        treeViewer.addDoubleClickListener(new ConvertDoubleClickListener(
                (MousePointGetTreeViewer) treeViewer));
        // Column setting
        makeColumn();

        getSite().setSelectionProvider(treeViewer);

        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        try {
            String filePath = store.getString(ConvertView.FILE_PATH);
            refreshView(filePath);
        } catch (JbmException e) {
            LOGGER.error(MessageUtil.ERR_CONVERT_READ, e);
            PluginUtil.viewErrorDialog(ResourceUtil.CONVERT_VIEW_RESULT,
                    MessageUtil.ERR_CONVERT_READ, e);
        }
    }

    /**
     * Refresh the view than the file path.<br/>
     * Get the data in the file path, and update the view screen.<br/>
     * 
     * @param filePath
     *            File Path
     * @throws JbmException
     *             JBM exception
     */
    private void refreshView(String filePath) throws JbmException {
        if (!StringUtil.isEmpty(filePath)) {
            JbossMigrationConvertTool top = ConvertAccessFactory
                    .getConvertReadFacade().read(filePath);
            if (top != null) {
                treeViewer.setInput(top.getMigrationItems().getMigrationItem());
                treeViewer.refresh();
            }
        }
    }

    /**
     * And setting the right-click menu control, setting the column.<br/>
     * 
     */
    private void makeColumn() {
        // Set the menu to open the right-click
        // contextMenu(treeViewer);
        // Display settings such as header
        Tree tree = treeViewer.getTree();
        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);
        tree.setSortDirection(SWT.DOWN);
        // Registration of the column
        addColumn(tree);
    }

    /**
     * Register the mouse context menu.<br/>
     * 
     * @param treeViewer
     *            Tree data on the screen
     * 
     */
    public void contextMenu(TreeViewer treeViewer) {
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new ConvertContextMenuListener(this));
        Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);
    }

    /**
     * Add a column to be used in the screen.<br/>
     * 
     * @param tree
     *            Add Tree
     */
    private void addColumn(Tree tree) {
        ConvertLabelProvider labelProvider = new ConvertLabelProvider();

        tree.setHeaderVisible(true);
        tree.setLinesVisible(true);

        TreeColumn columnNo = new TreeColumn(tree, SWT.NULL);
        columnNo.setText(ResourceUtil.CONVERT_VIEW_NO);
        columnNo.setWidth(ConvertView.NO_COLUMN_SIZE);

        TreeColumn columnPrioriy = new TreeColumn(tree, SWT.NULL);
        // columnPrioriy.setText(ResourcesUtil.CONVERT_VIEW_CHANGE_ITEM);
        columnPrioriy.setText(ResourceUtil.BEFORE_CONVERTING_FILE);
        columnPrioriy.setWidth(ConvertView.CHANGE_ITEM_BEFORE_COLUMN_SIZE);
        new TreeViewerColumn(treeViewer, columnPrioriy)
                .setLabelProvider(labelProvider);

        TreeColumn columnAfterPrioriy = new TreeColumn(tree, SWT.NULL);
        columnAfterPrioriy.setText(ResourceUtil.AFTER_CONVERTING_FILE);
        columnAfterPrioriy.setWidth(ConvertView.CHANGE_ITEM_AFTER_COLUMN_SIZE);
        new TreeViewerColumn(treeViewer, columnAfterPrioriy)
                .setLabelProvider(labelProvider);

        TreeColumn columnConverted = new TreeColumn(tree, SWT.NULL);
        columnConverted.setText(ResourceUtil.CONVERT_VIEW_RESULT);
        columnConverted.setWidth(ConvertView.RESULT_COLUMN_SIZE);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        treeViewer.getControl().setFocus();
    }

    /**
     * The change or refresh the display with the data specified.<br/>
     * 
     * @param migrationItemList
     *            Screen display data
     * @param filePath
     *            File Path
     */
    public void update(List<MigrationItem> migrationItemList, String filePath) {
        IPreferenceStore store = Activator.getDefault().getPreferenceStore();
        store.setValue(ConvertView.FILE_PATH, filePath);

        treeViewer.setInput(migrationItemList);
        treeViewer.refresh();
        PluginUtil.showConvertView();
    }

    /**
     * Get the selected object.<br/>
     * 
     * @return Selected object
     */
    public Object getSelectionObject() {
        return treeViewer.getSelection();
    }
}
