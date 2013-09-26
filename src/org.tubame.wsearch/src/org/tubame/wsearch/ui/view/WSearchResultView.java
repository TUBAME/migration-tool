/*
 * WSearchResultView.java
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
package org.tubame.wsearch.ui.view;

import java.io.BufferedOutputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.Set;

import org.tubame.common.util.CmnStringUtil;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IMarker;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.CoreException;
import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.eclipse.jface.action.Action;
import org.eclipse.jface.action.IMenuManager;
import org.eclipse.jface.action.IToolBarManager;
import org.eclipse.jface.action.Separator;
import org.eclipse.jface.dialogs.IInputValidator;
import org.eclipse.jface.dialogs.InputDialog;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.TreeColumn;
import org.eclipse.swt.widgets.TreeItem;
import org.eclipse.ui.IActionBars;
import org.eclipse.ui.ide.IDE;
import org.eclipse.ui.part.ViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.wsearch.Activator;
import org.tubame.wsearch.biz.comparer.AbstractComparer;
import org.tubame.wsearch.logics.WSearchReadFacade;
import org.tubame.wsearch.models.MigrationEditorRow;
import org.tubame.wsearch.models.WSearchEditorEnum;
import org.tubame.wsearch.models.WSearchEditorMigrationRow;
import org.tubame.wsearch.ui.dialog.ErrorDialog;
import org.tubame.wsearch.ui.dialog.ResultViewFilterDialog;
import org.tubame.wsearch.ui.ex.WSearchPluginException;
import org.tubame.wsearch.util.PluginUtil;
import org.tubame.wsearch.util.resource.ResourceUtil;

/**
 * It is the view class general-purpose search results.<br/>
 */
public class WSearchResultView extends ViewPart {

    /**
     * View ID
     */
    public static final String ID = "org.tubame.wsearch.ui.view.GenericSearchResultView";

    /**
     * Parent element
     */
    private Composite parent;
    /**
     * Viewer
     */
    private TreeViewer viewer;
    /**
     * Expand all action
     */
    private Action expandAllAction;
    /**
     * Reduced display all action
     */
    private Action collapseAllAction;
    /**
     * Filter action result
     */
    private Action statusFilterAction;
    /**
     * Category filter action
     */
    private Action categoryFilterAction;
    /**
     * Package filter action
     */
    private Action packageFilterAction;
    /**
     * Porting the original library filter action
     */
    private Action srcLibraryFilterAction;
    /**
     * Porting library filter action
     */
    private Action destLibraryFilterAction;
    /**
     * Display number configuration actions
     */
    private Action viewLimitAction;
    /**
     * CSV output action
     */
    private Action outputCsvAction;
    /**
     * The path of the file results
     */
    private String resultFilePath;

    /**
     * Display data to be set to view
     */
    private List<MigrationEditorRow> jbmEditorData = new ArrayList<MigrationEditorRow>();

    /**
     * Filter map
     */
    private Map<WSearchEditorEnum, String[]> filterMap;
    /**
     * Displayed
     */
    private int limit = 100;

    /**
     * Error count
     */
    private int errorCount = 0;
    /**
     * Warning number
     */
    private int warnCount = 0;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(WSearchResultView.class);

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     */
    public WSearchResultView() {
    }

    /**
     * It is the process of reading the results.<br/>
     * 
     * @param fileName
     *            Result file
     * @return List of line data results
     * @throws WSearchPluginException
     *             If it fails to load, it is Throw
     */
    public List<MigrationEditorRow> load(String fileName)
            throws WSearchPluginException {
        List<WSearchEditorMigrationRow> list = new WSearchReadFacade()
                .readNewJbm(fileName);
        List<MigrationEditorRow> resultList = new ArrayList<MigrationEditorRow>(
                list.size());
        resultList.addAll(list);
        return resultList;
    }

    /**
     * {@inheritDoc}
     */
    public void createPartControl(Composite parent) {
        LOGGER.info(Activator.getResourceString("debug.msg.start")
                + "WSearchResultView#createPartControl");
        this.parent = parent;
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        this.resultFilePath = ws.getRoot().getLocation().addTrailingSeparator()
                .toString()
                + this.getViewSite().getSecondaryId();

        // Toolbar & Pulldown generation
        makeActions();
        contributeToActionBars();
        loadResultFile();
        LOGGER.info(Activator.getResourceString("debug.msg.end")
                + "WSearchResultView#createPartControl");
    }

    /**
     * Get the parent element.<br/>
     * 
     * @return Parent element
     */
    public Composite getParent() {
        return this.parent;
    }

    /**
     * Read the file results.<br/>
     */
    private void loadResultFile() {
        if (this.resultFilePath == null) {
            return;
        }
        try {
            if (jbmEditorData.size() > 0) {
                jbmEditorData.clear();
            }
            jbmEditorData.addAll(load(this.resultFilePath));
        } catch (WSearchPluginException e) {
            ErrorDialog.openErrorDialog(
                    parent.getShell(),
                    e,
                    Activator.getResourceString(WSearchResultView.class
                            .getName() + ".err.msg.ReadResultErr"));
            return;
        }
        for (MigrationEditorRow row : jbmEditorData) {
            String status = row.getColumnText(WSearchEditorEnum.INDEX_STATUS
                    .getIndex());
            if (status.equals(AbstractComparer.STATUS.ERROR.getValue())) {
                errorCount = Integer.parseInt(row
                        .getColumnText(WSearchEditorEnum.INDEX_HIT_NUM
                                .getIndex()));
            } else if (status.equals(AbstractComparer.STATUS.WARN.getValue())) {
                warnCount = Integer.parseInt(row
                        .getColumnText(WSearchEditorEnum.INDEX_HIT_NUM
                                .getIndex()));
            }
        }

        // Generation of the TreeView
        viewer = new TreeViewer(parent, SWT.BORDER | SWT.H_SCROLL
                | SWT.V_SCROLL | SWT.FULL_SELECTION);
        viewer.getTree().setHeaderVisible(true);
        viewer.getTree().setLinesVisible(true);

        // Generation of tree column
        Map<String, Integer> columnMap = new LinkedHashMap<String, Integer>();
        for (WSearchEditorEnum column : WSearchEditorEnum.values()) {
            columnMap.put(column.getTitle(), column.getWidth());
        }

        // Sort listener
        SelectionListener selectionListener = new WSearchSortListener(viewer);

        // Set of tree column name
        for (Map.Entry<String, Integer> entry : columnMap.entrySet()) {

            // Tree column sort function implementation
            TreeColumn column = new TreeColumn(viewer.getTree(), SWT.LEFT);
            column.setText(entry.getKey());
            column.setWidth(entry.getValue());
            if (isSetListener(entry.getKey())) {
                column.addSelectionListener(selectionListener);
            }
        }

        // Registration of content provider
        viewer.setContentProvider(new GjbmSearchResultContentProvider());
        viewer.setLabelProvider(new GjbmSearchResultLabelProvider());
        viewer.setInput(jbmEditorData);

        // Set the filter as a state without filter
        filterMap = new HashMap<WSearchEditorEnum, String[]>();
        addFilter(new ResultViewFilter(filterMap));

        // Set action when double-clicked
        viewer.addDoubleClickListener(new IDoubleClickListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void doubleClick(DoubleClickEvent event) {
                ISelection selection = event.getSelection();
                if (selection instanceof IStructuredSelection) {
                    Object element = ((IStructuredSelection) selection)
                            .getFirstElement();
                    if (element instanceof MigrationEditorRow) {
                        MigrationEditorRow row = (MigrationEditorRow) element;
                        String fileName = row
                                .getColumnText(WSearchEditorEnum.INDEX_FILES
                                        .getIndex());
                        if (!CmnStringUtil.isEmpty(fileName)) {
                            String resultPath = getViewSite().getSecondaryId();
                            String projectName = resultPath.substring(0,
                                    resultPath.indexOf(CmnStringUtil.SLASH));
                            try {
                                IFile file = PluginUtil.getProject(projectName)
                                        .getFile(fileName);
                                IMarker marker = file
                                        .createMarker(IMarker.TEXT);
                                int lineNumber = 0;
                                try {
                                    lineNumber = Integer.parseInt(row
                                            .getColumnText(WSearchEditorEnum.INDEX_LINE
                                                    .getIndex()));
                                } catch (NumberFormatException e) {
                                    // no operation
                                }
                                marker.setAttribute(IMarker.LINE_NUMBER,
                                        lineNumber);
                                IDE.openEditor(PluginUtil
                                        .getActiveWorkbenchWindow()
                                        .getActivePage(), marker);
                                file.deleteMarkers(IMarker.TEXT, false,
                                        IResource.DEPTH_ZERO);

                            } catch (CoreException e) {
                                ErrorDialog.openErrorDialog(parent.getShell(),
                                        e, WSearchResultView.class.getName()
                                                + ".err.msg.EditorOpenErr");
                            }
                        }

                    }
                }
            }
        });

        // Add a description.
        String description = AbstractComparer.STATUS.ERROR.getValue()
                + ":"
                + errorCount
                + " "
                + AbstractComparer.STATUS.WARN.getValue()
                + ":"
                + warnCount
                + " ("
                + limit
                + Activator.getResourceString(WSearchResultView.class.getName()
                        + ".label.description") + ") [" + this.resultFilePath
                + "] ";
        setContentDescription(description);

        // View open, all the tree.
        viewer.expandAll();
        parent.layout();
    }

    /**
     * Determine whether or not the listener is set.<br/>
     * 
     * @param key
     *            Key of the listener
     * @return Determine if is set
     */
    private boolean isSetListener(String key) {
        if (key.equals(Activator.getResourceString(WSearchResultView.class
                .getName() + ".label.result"))) {
            return true;
        }
        if (key.equals(Activator.getResourceString(WSearchResultView.class
                .getName() + ".label.category"))) {
            return true;
        }
        if (key.equals(Activator.getResourceString(WSearchResultView.class
                .getName() + ".label.package"))) {
            return true;
        }
        return false;
    }

    /**
     * Make the registration menu in the action bar.<br/>
     */
    private void contributeToActionBars() {
        IActionBars bars = getViewSite().getActionBars();
        fillLocalPullDown(bars.getMenuManager());
        fillLocalToolBar(bars.getToolBarManager());
    }

    /**
     * Do the registration of the local pull-down menu.<br/>
     * 
     * @param manager
     *            Menu manager
     */
    private void fillLocalPullDown(IMenuManager manager) {
        manager.add(statusFilterAction);
        manager.add(categoryFilterAction);
        manager.add(packageFilterAction);
        manager.add(srcLibraryFilterAction);
        manager.add(destLibraryFilterAction);
        manager.add(new Separator());
        manager.add(outputCsvAction);
        manager.add(new Separator());
        manager.add(viewLimitAction);
    }

    /**
     * Do the registration of the local toolbar.<br/>
     * 
     * @param manager
     *            Toolbar manager
     */
    private void fillLocalToolBar(IToolBarManager manager) {
        manager.add(expandAllAction);
        manager.add(collapseAllAction);
    }

    /**
     * Create a variety of action.<br/>
     */
    private void makeActions() {
        ImageDescriptor expandIcon = null;
        ImageDescriptor collapseIcon = null;
        ImageDescriptor filterIcon = null;
        ImageDescriptor hammerIcon = null;
        ImageDescriptor csvIcon = null;

        try {
            URL pluginURL = Activator.getDefault().getBundle()
                    .getEntry(CmnStringUtil.SLASH);
            expandIcon = ImageDescriptor.createFromURL(new URL(pluginURL,
                    ResourceUtil.EXPAND_ALL_ICON));
            collapseIcon = ImageDescriptor.createFromURL(new URL(pluginURL,
                    ResourceUtil.COLLAPSE_ALL_ICON));
            filterIcon = ImageDescriptor.createFromURL(new URL(pluginURL,
                    ResourceUtil.FILTER_ICON));
            hammerIcon = ImageDescriptor.createFromURL(new URL(pluginURL,
                    ResourceUtil.SETTING_ICON));
            csvIcon = ImageDescriptor.createFromURL(new URL(pluginURL,
                    ResourceUtil.CSV_ICON));
        } catch (IOException e) {
            ErrorDialog.openErrorDialog(
                    viewer.getControl().getShell(),
                    e,
                    Activator.getResourceString(WSearchResultView.class
                            .getName() + ".err.msg.IconLoadErr"));
        }

        // Expand all tree action
        expandAllAction = new Action() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                viewer.expandAll();
            }
        };
        expandAllAction.setToolTipText(Activator
                .getResourceString(WSearchResultView.class.getName()
                        + ".label.ExpandAll"));
        expandAllAction.setImageDescriptor(expandIcon);

        // Tree all reduced display
        collapseAllAction = new Action() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                viewer.collapseAll();
            }
        };
        collapseAllAction.setToolTipText(Activator
                .getResourceString(WSearchResultView.class.getName()
                        + ".label.CollapseAll"));
        collapseAllAction.setImageDescriptor(collapseIcon);

        // Display screen filter results: pull-down menu
        statusFilterAction = new Action() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                @SuppressWarnings("unchecked")
                List<WSearchEditorMigrationRow> rowList = (List<WSearchEditorMigrationRow>) viewer
                        .getTree().getData();

                Set<String> displayFilterSet = new HashSet<String>(
                        rowList.size());
                Set<String> hideFilterSet = new HashSet<String>(rowList.size());
                setFilterItems(rowList, WSearchEditorEnum.INDEX_STATUS,
                        displayFilterSet, hideFilterSet);
                ResultViewFilterDialog dialog = new ResultViewFilterDialog(
                        viewer.getControl().getShell(),
                        displayFilterSet.toArray(), hideFilterSet.toArray(),
                        Activator.getResourceString(WSearchResultView.class
                                .getName() + ".label.ResultFilter"));
                int performButton = dialog.open();
                if (performButton == 0) {

                    // OK button
                    filterMap.put(WSearchEditorEnum.INDEX_STATUS,
                            dialog.getDefultList());
                    addFilter(new ResultViewFilter(filterMap));
                }
            }
        };
        statusFilterAction.setText(Activator
                .getResourceString(WSearchResultView.class.getName()
                        + ".label.ResultFilter"));
        statusFilterAction.setImageDescriptor(filterIcon);

        // Pull-down menu: category filter screen Display
        categoryFilterAction = new Action() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                @SuppressWarnings("unchecked")
                List<WSearchEditorMigrationRow> rowList = (List<WSearchEditorMigrationRow>) viewer
                        .getTree().getData();
                Set<String> displayFilterSet = new HashSet<String>(
                        rowList.size());
                Set<String> hideFilterSet = new HashSet<String>(rowList.size());
                setFilterItems(rowList, WSearchEditorEnum.INDEX_CATEGORY,
                        displayFilterSet, hideFilterSet);
                ResultViewFilterDialog dialog = new ResultViewFilterDialog(
                        viewer.getControl().getShell(),
                        displayFilterSet.toArray(), hideFilterSet.toArray(),
                        Activator.getResourceString(WSearchResultView.class
                                .getName() + ".label.CategoryFilter"));
                int performButton = dialog.open();

                if (performButton == 0) {

                    // OK button
                    filterMap.put(WSearchEditorEnum.INDEX_CATEGORY,
                            dialog.getDefultList());
                    addFilter(new ResultViewFilter(filterMap));
                }
            }
        };
        categoryFilterAction.setText(Activator
                .getResourceString(WSearchResultView.class.getName()
                        + ".label.CategoryFilter"));
        categoryFilterAction.setImageDescriptor(filterIcon);

        // Pull-down menu: package filter screen display
        packageFilterAction = new Action() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                @SuppressWarnings("unchecked")
                List<WSearchEditorMigrationRow> rowList = (List<WSearchEditorMigrationRow>) viewer
                        .getTree().getData();

                Set<String> displayFilterSet = new HashSet<String>(
                        rowList.size());
                Set<String> hideFilterSet = new HashSet<String>(rowList.size());
                setFilterItems(rowList, WSearchEditorEnum.INDEX_PACKAGE,
                        displayFilterSet, hideFilterSet);

                ResultViewFilterDialog dialog = new ResultViewFilterDialog(
                        viewer.getControl().getShell(),
                        displayFilterSet.toArray(), hideFilterSet.toArray(),
                        Activator.getResourceString(WSearchResultView.class
                                .getName() + ".label.PackageFilter"));
                int performButton = dialog.open();

                if (performButton == 0) {

                    // OK button
                    filterMap.put(WSearchEditorEnum.INDEX_PACKAGE,
                            dialog.getDefultList());
                    addFilter(new ResultViewFilter(filterMap));
                }
            }
        };
        packageFilterAction.setText(Activator
                .getResourceString(WSearchResultView.class.getName()
                        + ".label.PackageFilter"));
        packageFilterAction.setImageDescriptor(filterIcon);

        // Pull-down menu: porting the original library filter
        srcLibraryFilterAction = new Action() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                @SuppressWarnings("unchecked")
                List<WSearchEditorMigrationRow> rowList = (List<WSearchEditorMigrationRow>) viewer
                        .getTree().getData();

                Set<String> displayFilterSet = new HashSet<String>(
                        rowList.size());
                Set<String> hideFilterSet = new HashSet<String>(rowList.size());
                setFilterItems(rowList, WSearchEditorEnum.INDEX_SRC_LIBRARY,
                        displayFilterSet, hideFilterSet);

                ResultViewFilterDialog dialog = new ResultViewFilterDialog(
                        viewer.getControl().getShell(),
                        displayFilterSet.toArray(), hideFilterSet.toArray(),
                        Activator.getResourceString(WSearchResultView.class
                                .getName() + ".label.SrcLibraryFilter"));
                int performButton = dialog.open();
                if (performButton == 0) {

                    // OK button
                    filterMap.put(WSearchEditorEnum.INDEX_SRC_LIBRARY,
                            dialog.getDefultList());
                    addFilter(new ResultViewFilter(filterMap));
                }
            }
        };
        srcLibraryFilterAction.setText(Activator
                .getResourceString(WSearchResultView.class.getName()
                        + ".label.SrcLibraryFilter"));
        srcLibraryFilterAction.setImageDescriptor(filterIcon);

        // Pull-down menu: porting library filter
        destLibraryFilterAction = new Action() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                @SuppressWarnings("unchecked")
                List<WSearchEditorMigrationRow> rowList = (List<WSearchEditorMigrationRow>) viewer
                        .getTree().getData();

                Set<String> displayFilterSet = new HashSet<String>(
                        rowList.size());
                Set<String> hideFilterSet = new HashSet<String>(rowList.size());
                setFilterItems(rowList, WSearchEditorEnum.INDEX_DEST_LIBRARY,
                        displayFilterSet, hideFilterSet);

                ResultViewFilterDialog dialog = new ResultViewFilterDialog(
                        viewer.getControl().getShell(),
                        displayFilterSet.toArray(), hideFilterSet.toArray(),
                        Activator.getResourceString(WSearchResultView.class
                                .getName() + ".label.DestLibraryFilter"));
                int performButton = dialog.open();
                if (performButton == 0) {

                    // OK button
                    filterMap.put(WSearchEditorEnum.INDEX_DEST_LIBRARY,
                            dialog.getDefultList());
                    addFilter(new ResultViewFilter(filterMap));
                }
            }
        };
        destLibraryFilterAction.setText(Activator
                .getResourceString(WSearchResultView.class.getName()
                        + ".label.DestLibraryFilter"));
        destLibraryFilterAction.setImageDescriptor(filterIcon);

        // Pull-down menu: Display number setting screen display
        viewLimitAction = new Action() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                InputDialog dialog = new InputDialog(viewer.getControl()
                        .getShell(),
                        Activator.getResourceString(WSearchResultView.class
                                .getName() + ".title.limit"),
                        Activator.getResourceString(WSearchResultView.class
                                .getName() + ".label.LimitSettingText"),
                        Integer.toString(limit), new IInputValidator() {
                            @Override
                            public String isValid(String input) {
                                try {
                                    int value = Integer.parseInt(input);
                                    if ((value < 0) || (value > 100000000)) {
                                        return Activator.getResourceString(WSearchResultView.class
                                                .getName()
                                                + ".err.msg.InvalidLimit");
                                    }
                                } catch (NumberFormatException e) {
                                    return Activator
                                            .getResourceString(WSearchResultView.class
                                                    .getName()
                                                    + ".err.msg.InvalidLimit");
                                }
                                return null;
                            }
                        });
                int performButton = dialog.open();
                if (performButton == 0) {
                    limit = Integer.parseInt(dialog.getValue());
                    if (limit == 0) {

                        // Add a description
                        String description = AbstractComparer.STATUS.ERROR
                                .getValue()
                                + ":"
                                + errorCount
                                + " "
                                + AbstractComparer.STATUS.WARN.getValue()
                                + ":"
                                + warnCount + " [" + resultFilePath + "]";
                        setContentDescription(description);
                    } else {

                        // Add a description
                        String description = AbstractComparer.STATUS.ERROR
                                .getValue()
                                + ":"
                                + errorCount
                                + " "
                                + AbstractComparer.STATUS.WARN.getValue()
                                + ":"
                                + warnCount
                                + " ("
                                + limit
                                + Activator
                                        .getResourceString(WSearchResultView.class
                                                .getName()
                                                + ".label.description")
                                + ") ["
                                + resultFilePath + "]";
                        setContentDescription(description);
                    }
                    viewer.getTree().setRedraw(false);
                    viewer.refresh();
                    viewer.expandAll();
                    viewer.getTree().setRedraw(true);
                }
            }
        };
        viewLimitAction.setText(Activator
                .getResourceString(WSearchResultView.class.getName()
                        + ".label.LimitSetting"));
        viewLimitAction.setImageDescriptor(hammerIcon);

        // CSV output
        outputCsvAction = new Action() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                FileDialog dialog = new FileDialog(viewer.getControl()
                        .getShell(), SWT.SAVE);
                String[] extensions = { ResourceUtil.EXTENSION_CSV };
                dialog.setFilterExtensions(extensions);
                dialog.setFileName(ResourceUtil.RESULT_FILE_NAME.substring(0,
                        ResourceUtil.RESULT_FILE_NAME.lastIndexOf(".")));

                final String path = dialog.open();
                if (path != null) {
                    List<WSearchEditorMigrationRow> data = new ArrayList<WSearchEditorMigrationRow>(
                            viewer.getTree().getItems().length);
                    for (TreeItem item : viewer.getTree().getItems()) {
                        Object obj = item.getData();
                        if (obj instanceof MigrationEditorRow) {
                            data.add((WSearchEditorMigrationRow) obj);
                        }
                    }
                    final List<WSearchEditorMigrationRow> list = getFilteredDataForCSV(data);
                    Job job = new Job(
                            Activator.getResourceString(WSearchResultView.class
                                    .getName() + ".label.OutputtingCsv")) {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        protected IStatus run(IProgressMonitor monitor) {

                            monitor.beginTask(
                                    Activator
                                            .getResourceString(WSearchResultView.class
                                                    .getName()
                                                    + ".label.OutputtingCsv"),
                                    IProgressMonitor.UNKNOWN);

                            BufferedOutputStream out = null;
                            try {
                                out = new BufferedOutputStream(
                                        new FileOutputStream(path));
                                for (MigrationEditorRow row : list) {
                                    out.write((row.toString() + "\n")
                                            .getBytes());

                                    // Cancellation process
                                    if (monitor.isCanceled()) {
                                        return Status.CANCEL_STATUS;
                                    }
                                }
                            } catch (Exception e) {
                                String message = Activator
                                        .getResourceString(WSearchResultView.class
                                                .getName()
                                                + ".err.msg.OutputCsvErr");
                                Activator.log(e, message);
                                return new Status(IStatus.ERROR,
                                        Activator.PLUGIN_ID, message);
                            } finally {
                                if (out != null) {
                                    try {
                                        out.flush();
                                        out.close();
                                    } catch (IOException e) {
                                        e.printStackTrace();
                                    }
                                }
                            }
                            monitor.done();
                            return Status.OK_STATUS;
                        }
                    };
                    job.setUser(true);
                    job.schedule();
                }
            }
        };
        outputCsvAction.setText(Activator
                .getResourceString(WSearchResultView.class.getName()
                        + ".label.OutputCsv"));
        outputCsvAction.setImageDescriptor(csvIcon);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        if (viewer != null) {
            viewer.getControl().setFocus();
        }
    }

    /**
     * Add a filter to view.<br/>
     * 
     * @param filter
     *            Filter
     */
    private void addFilter(ViewerFilter filter) {
        viewer.getTree().setRedraw(false);
        for (ViewerFilter current : viewer.getFilters()) {
            viewer.removeFilter(current);
        }
        viewer.addFilter(filter);
        viewer.expandAll();
        viewer.getTree().setRedraw(true);
    }

    /**
     * It is a general-purpose filter class.<br/>
     * Filter for the item at the specified index in the constructor.<br/>
     */
    private class ResultViewFilter extends ViewerFilter {

        /** Filter map */
        private Map<WSearchEditorEnum, String[]> filterMap;

        /**
         * Constructor.<br/>
         * Nothing in particular.<br/>
         * 
         * @param indexes
         *            Index
         */
        private ResultViewFilter(Map<WSearchEditorEnum, String[]> indexes) {
            this.filterMap = indexes;
        }

        /** Line count */
        private int rowCount;
        /** Show list */
        private ArrayList<Object> showList;

        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] filter(Viewer viewer, Object parent, Object[] elements) {
            int size = elements.length;
            if (size > 0) {
                if (((WSearchEditorMigrationRow) elements[0]).getLevel() == WSearchEditorMigrationRow.LEVEL.FIRST
                        .getLevel()) {
                    showList = new ArrayList<Object>(size);
                }
                for (Object element : elements) {
                    WSearchEditorMigrationRow row = (WSearchEditorMigrationRow) element;
                    if (row.getLevel() == WSearchEditorMigrationRow.LEVEL.FIRST
                            .getLevel()) {
                        rowCount = 1;
                        deepSelect(row);
                    }
                }
            }
            ArrayList<Object> out = new ArrayList<Object>(size);
            for (Object element : elements) {
                if (showList.contains(element)) {
                    out.add(element);
                }
            }
            return out.toArray();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public boolean select(Viewer viewer, Object parent, Object e) {
            // no operation
            return false;
        }

        /**
         * Choose recursively.<br/>
         * 
         * @param row
         *            Line
         * @return true, false
         */
        private boolean deepSelect(WSearchEditorMigrationRow row) {
            if ((limit != 0) && (rowCount > limit)) {
                return false;
            }
            if (row.getLevel() == WSearchEditorMigrationRow.LOWEST_LEVEL
                    .getLevel()) {
                if (filterMap.size() > 0) {
                    for (Map.Entry<WSearchEditorEnum, String[]> filter : filterMap
                            .entrySet()) {
                        boolean result = false;
                        String value = row.getColumnText(filter.getKey()
                                .getIndex(), false);
                        for (String v : filter.getValue()) {
                            if (value.equals(v)) {
                                result = true;
                                break;
                            }
                        }
                        if (!result) {
                            return false;
                        }
                    }
                    showList.add(row);
                    rowCount++;
                    return true;
                } else {
                    showList.add(row);
                    rowCount++;
                    return true;
                }
            } else {
                boolean result = false;
                for (WSearchEditorMigrationRow child : row.getChildren()) {
                    if (deepSelect(child)) {
                        showList.add(row);
                        result = true;
                    }
                }
                return result;
            }
        }
    }

    /**
     * Get all of the items that were captured by general-purpose search
     * results.<br/>
     * Get all the data of the specified column from the items that were
     * acquired in the general-purpose search results.<br/>
     * 
     * @param rows
     *            List of search result data
     * @param column
     *            Column to be filtered
     * @param list
     *            Set that contains the item list
     */
    private void getAllItems(List<WSearchEditorMigrationRow> rows,
            WSearchEditorEnum column, Set<String> list) {
        for (WSearchEditorMigrationRow row : rows) {
            if (row.getLevel() == column.getLevel()) {
                list.add(row.getColumnText(column.getIndex()));
            }
            List<WSearchEditorMigrationRow> children = row.getChildList();
            if (children.size() > 0) {
                getAllItems(children, column, list);
            }
        }
    }

    /**
     * Set the non-display filter list and display filter list.<br/>
     * 
     * @param rows
     *            List of search result data
     * @param column
     *            Column to be filtered
     * @param displayList
     *            Display filter list
     * @param hideList
     *            Hide filter list
     */
    private void setFilterItems(List<WSearchEditorMigrationRow> rows,
            WSearchEditorEnum column, Set<String> displayList,
            Set<String> hideList) {

        // Acquisition of the entire list
        Set<String> allSet = new HashSet<String>();
        getAllItems(rows, column, allSet);

        // Display list acquisition
        Set<String> displaySet = new HashSet<String>();
        for (Entry<WSearchEditorEnum, String[]> entry : filterMap.entrySet()) {
            if (entry.getKey().equals(column)) {
                for (String entryValue : (String[]) entry.getValue()) {
                    displaySet.add(entryValue);
                }
            }
        }
        IdentificationFilterList(displayList, hideList, allSet, displaySet);
    }

    /**
     * Set to each list by identifying whether a non-display filter list or a
     * display filter list.<br/>
     * 
     * @param displayList
     *            Display filter list
     * @param hideList
     *            Hide filter list
     * @param allSet
     *            All columns data
     * @param displaySet
     *            Column data that is displayed
     */
    private void IdentificationFilterList(Set<String> displayList,
            Set<String> hideList, Set<String> allSet, Set<String> displaySet) {
        for (String allItemName : allSet) {
            boolean displayFilter = false;
            if (displaySet.size() > 0) {
                for (String displayItemName : displaySet) {
                    if (allItemName.equals(displayItemName)) {
                        displayList.add(allItemName);
                        displayFilter = true;
                    }
                }
            } else {
                displayList.add(allItemName);
                displayFilter = true;
            }
            if (!displayFilter) {
                hideList.add(allItemName);
            }
        }
    }

    /**
     * Update the model.<br/>
     * 
     * @param wsearchResultFilePath
     *            The path of the file results
     */
    public void modelUpdate(String wsearchResultFilePath) {
        this.resultFilePath = wsearchResultFilePath;
        loadResultFile();
    }

    /**
     * Get the data in the CSV output that reflects the filter.<br/>
     * 
     * @param list
     *            Data to be output source
     * @return Data that reflects the filter
     */
    private List<WSearchEditorMigrationRow> getFilteredDataForCSV(
            List<WSearchEditorMigrationRow> list) {
        List<WSearchEditorMigrationRow> filtered = new ArrayList<WSearchEditorMigrationRow>();
        for (WSearchEditorMigrationRow row : list) {
            if (row.getLevel() == WSearchEditorMigrationRow.LOWEST_LEVEL
                    .getLevel()) {
                if (filterMap.size() > 0) {
                    boolean add = true;
                    for (Map.Entry<WSearchEditorEnum, String[]> filter : filterMap
                            .entrySet()) {
                        boolean result = false;
                        String value = row.getColumnText(filter.getKey()
                                .getIndex(), false);
                        for (String v : filter.getValue()) {
                            if (value.equals(v)) {
                                result = true;
                                break;
                            }
                        }
                        if (!result) {
                            add = false;
                            break;
                        }
                    }
                    if (add) {
                        filtered.add(row);
                    }
                } else {
                    filtered.add(row);
                }
            } else {
                filtered.addAll(getFilteredDataForCSV(row.getChildren()));
            }
        }
        return filtered;
    }
}