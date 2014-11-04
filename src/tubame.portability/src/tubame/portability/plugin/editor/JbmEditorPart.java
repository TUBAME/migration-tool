/*
 * JbmEditorPart.java
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
package tubame.portability.plugin.editor;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.core.resources.IProject;
import org.eclipse.jface.action.MenuManager;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Menu;
import org.eclipse.swt.widgets.Tree;
import org.eclipse.ui.IEditorInput;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.part.EditorPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.exception.JbmException;
import tubame.portability.logic.CheckListInformationFactory;
import tubame.portability.logic.JbmAccessFactory;
import tubame.portability.model.JbmEditorEnum;
import tubame.portability.model.JbmEditorMigrationRow;
import tubame.portability.model.MigrationEditorRow;
import tubame.portability.plugin.action.JbmRowDelete;
import tubame.portability.plugin.action.MigrationRowDelete;
import tubame.portability.plugin.view.WorkStatusView;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.MessageUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Eclipse editor that a hierarchical view of the results of the search
 * function.<br/>
 * Sort the search results record, link to the original source file
 * transplantation, <br/>
 * delete records, visual confirmation status change, a link to the portability
 * study guidelines,<br/>
 * save the file after editing.<br/>
 * The search results screen editing function display, and display using the
 * perspective of their own.<br/>
 */
public class JbmEditorPart extends AbstractJbmEditorPart {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(JbmEditorPart.class);
    /**
     * Hearing column size
     */
    private static final int HIARING_COLUMN_SIZE = 100;
    /**
     * Visual confirmation column size
     */
    private static final int VISUAL_COLUMN_SIZE = 100;
    /**
     * Guide chapter number column size
     */
    private static final int CHAPTER_NO_COLUMN_SIZE = 60;
    /**
     * Difficulty column size
     */
    private static final int DIFFICULTY_COLUMN_SIZE = 70;
    /**
     * Line number column size
     */
    private static final int ROW_COLUMN_SIZE = 30;
    /**
     * File Name column size
     */
    private static final int TARGET_FILE_PATH_COLUMN_SIZE = 100;
    /**
     * Major item column size
     */
    private static final int BIG_ITEM_COLUMN_SIZE = 120;
    
    
    /**
     * Major item column size
     */
    private static final int MIDDLE_ITEM_COLUMN_SIZE = 120;
    
    /**
     * Number of column size
     */
    private static final int HIT_COLUMN_SIZE = 60;
    /**
     * No column size
     */
    private static final int NO_COLUMN_SIZE = 40;
    /**
     * Line number size
     */
    private static final int LINE_COLUMN_SIZE = 80;
    /**
     * Line number basis size
     */
    private static final int LINE_BASIS_COLUMN_SIZE = 120;
    /**
     * Total line size
     */
    private static final int TOTAL_LINE_COLUMN_SIZE = 80;

    /**
     * {@inheritDoc}
     */
    @SuppressWarnings("unchecked")
    @Override
    public boolean save(String fileName, TreeViewer treeViewer) {
        LOGGER.info(MessageUtil.INF_SAVE);
        try {
            JbmAccessFactory.getJbmWriteFacade().update(fileName,
                    (List<JbmEditorMigrationRow>) treeViewer.getInput());
            WorkStatusView.out(false, null);
            LOGGER.debug(MessageUtil.INF_SAVED);
        } catch (JbmException e) {
            // Failed to save
            PluginUtil.viewErrorDialog(
                    ResourceUtil.JBM_EDITOR,
                    MessageUtil.ERR_SAVE + StringUtil.LINE_SEPARATOR
                            + e.getMessage(), e);
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public List<MigrationEditorRow> load(String fileName) throws JbmException {
        List<JbmEditorMigrationRow> list = JbmAccessFactory.getJbmReadFacade().read(fileName);
        List<MigrationEditorRow> resultList = new ArrayList<MigrationEditorRow>();
        resultList.addAll(list);
        return resultList;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void contextMenu(TreeViewer treeViewer) {
        MenuManager menuMgr = new MenuManager("#PopupMenu"); //$NON-NLS-1$
        menuMgr.setRemoveAllWhenShown(true);
        menuMgr.addMenuListener(new ContextMenuListener(this));
        Menu menu = menuMgr.createContextMenu(treeViewer.getControl());
        treeViewer.getControl().setMenu(menu);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void addColumn(Tree tree) {
        SelectionListener selectionListener = new JbmEditorSortListener(this);
        super.createTreeColumn(tree, JbmEditorEnum.INDEX_NO.getName(),
                SWT.NULL, JbmEditorPart.NO_COLUMN_SIZE, selectionListener);
        
        super.createTreeColumn(tree, JbmEditorEnum.BIG_ITEM.getName(),
                SWT.NULL, JbmEditorPart.BIG_ITEM_COLUMN_SIZE, null);
        
       super.createTreeColumn(tree, JbmEditorEnum.MIDDLE_ITEM.getName(),
       SWT.NULL, JbmEditorPart.MIDDLE_ITEM_COLUMN_SIZE, null);
        
       super.createTreeColumn(tree, JbmEditorEnum.DIFFICULTY.getName(),
               SWT.NULL, JbmEditorPart.DIFFICULTY_COLUMN_SIZE,
               selectionListener);
       
        super.createTreeColumn(tree, JbmEditorEnum.HIT_NUM.getName(),
                SWT.RIGHT, JbmEditorPart.HIT_COLUMN_SIZE, selectionListener);
        

        
        super.createTreeColumn(tree, JbmEditorEnum.TARGET_FILE_PATH.getName(),
                SWT.NULL, JbmEditorPart.TARGET_FILE_PATH_COLUMN_SIZE, null);
        
        super.createTreeColumn(tree, JbmEditorEnum.ROW_NO.getName(), SWT.RIGHT,
                JbmEditorPart.ROW_COLUMN_SIZE, null);
        

        
        // Number of line (with sort)
        super.createTreeColumn(tree, JbmEditorEnum.LINE_NUM.getName(),
                SWT.NULL, JbmEditorPart.LINE_COLUMN_SIZE, selectionListener);
        
        // Line number basis
        super.createTreeColumn(tree, JbmEditorEnum.LINE_NUM_BASIS.getName(),
                SWT.SELECTED, JbmEditorPart.LINE_BASIS_COLUMN_SIZE, null);
        
        // Total line
        super.createTreeColumn(tree, JbmEditorEnum.TOTAL_LINE.getName(),
                SWT.NULL, JbmEditorPart.TOTAL_LINE_COLUMN_SIZE, null);
        
        super.createTreeColumn(tree, JbmEditorEnum.CHAPTER_NO.getName(),
                SWT.NULL, JbmEditorPart.CHAPTER_NO_COLUMN_SIZE, null);
        
               

        


        
        
//        super.createTreeColumn(tree,
//                JbmEditorEnum.VISUAL_CONFIRM_ITEM.getName(), SWT.NULL, 0, null);
//        
//        
//        super.createTreeColumn(tree, JbmEditorEnum.HIARING_ITEM.getName(),
//                SWT.NULL, 0, null);
        super.createTreeColumn(tree,
                JbmEditorEnum.VISUAL_CONFIRM_STATSU_ITEM.getName(), SWT.NULL,
                JbmEditorPart.VISUAL_COLUMN_SIZE, selectionListener);
        super.createTreeColumn(tree, JbmEditorEnum.HIARING_STATUS.getName(),
                SWT.NULL, JbmEditorPart.HIARING_COLUMN_SIZE, selectionListener);


  
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public IDoubleClickListener getDoubleClickListener(
            MigrationEditorOperation editor) {
    	IProject project = null;
    	if (editor instanceof EditorPart) {
			EditorPart editorPart = (EditorPart) editor;
			project = ((IFileEditorInput) editorPart.getEditorInput()).getFile().getProject();
		}
    	
    	
        return new JbmEditorDoubleClickListener(editor,project);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public MigrationRowDelete getRowDeleteExecutor() {
        return new JbmRowDelete();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preInit() {
        try {
            // For check list view update information,
            // initialize the checklist file information acquisition class
            CheckListInformationFactory.getCheckListInformationFacade()
                    .initCheckListInformationReader();

            PluginUtil.openSeachPerspective();
            LOGGER.info(ResourceUtil.JBM_EDITOR + MessageUtil.INF_START);
        } catch (WorkbenchException e) {

            throw new RuntimeException(e);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void postCreatePartControl() {
        List<JbmEditorMigrationRow> topList = getInput();
        for (JbmEditorMigrationRow row : topList) {
            row.updateWriteData();
        }
        WorkStatusView.out(false, topList);
        // Expand all tree level
        super.getTreeViewer().expandAll();

    }

    /**
     * Get @ see JbmEditorMigrationRow list from the View data.<br/>
     * 
     * @return @see JbmEditorMigrationRwo list
     */
    @SuppressWarnings("unchecked")
    private List<JbmEditorMigrationRow> getInput() {
        TreeViewer treeViewer = getTreeViewer();
        return (List<JbmEditorMigrationRow>) treeViewer.getInput();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getTitle() {
        return ResourceUtil.JBM_EDITOR;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        WorkStatusView.out(false, getInput());
        super.setFocus();
    }
}
