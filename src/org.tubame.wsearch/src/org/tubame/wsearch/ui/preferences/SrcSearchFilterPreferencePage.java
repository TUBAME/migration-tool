/*
 * SrcSearchFilterPreferencePage.java
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
package org.tubame.wsearch.ui.preferences;

import java.io.Serializable;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;


import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ColumnLayoutData;
import org.eclipse.jface.viewers.ColumnWeightData;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.TableLayout;
import org.eclipse.jface.viewers.TableViewer;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.ScrolledComposite;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.wsearch.Activator;
import org.tubame.wsearch.biz.model.Extension;
import org.tubame.wsearch.biz.model.SearchFilter;
import org.tubame.wsearch.biz.model.WSPackage;
import org.tubame.wsearch.ui.dialog.EditSearchExtAndPackageDialog;
import org.tubame.wsearch.ui.dialog.EditSearchPackageDialog;
import org.tubame.wsearch.ui.layout.AutoResizeTableLayout;

/**
 * It is portable target source for filter setting page class.<br/>
 */
public class SrcSearchFilterPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {

    /**
     * Table viewer
     */
    private CheckboxTableViewer tableViewer;

    /**
     * Filter set
     */
    private List<SearchFilter> searchFilters;

    /**
     * Composite extension
     */
    private ExpandableComposite advancedComposite;

    /**
     * Toolkit
     */
    private final FormToolkit toolkit;

    /**
     * Map of extension detail table
     */
    private HashMap<String, TableViewer> searchExtentionDetailTableViewerMap;

    /**
     * Map of exclusion detail table
     */
    private HashMap<String, CheckboxTableViewer> searchPackageDetailTableViewerMap;

    /**
     * Button map
     */
    private HashMap<String, List<Button>> buttonMap;

    /**
     * Selected object
     */
    private Object selectionObject;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SrcSearchFilterPreferencePage.class);

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     */
    public SrcSearchFilterPreferencePage() {
        toolkit = new FormToolkit(Display.getCurrent());
        searchExtentionDetailTableViewerMap = new HashMap<String, TableViewer>();
        searchPackageDetailTableViewerMap = new HashMap<String, CheckboxTableViewer>();
        buttonMap = new HashMap<String, List<Button>>();
    }

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param title
     *            Title
     */
    public SrcSearchFilterPreferencePage(String title) {

        super(title);
        toolkit = new FormToolkit(Display.getCurrent());
        searchExtentionDetailTableViewerMap = new HashMap<String, TableViewer>();
        searchPackageDetailTableViewerMap = new HashMap<String, CheckboxTableViewer>();
        buttonMap = new HashMap<String, List<Button>>();
    }

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param title
     *            Title
     * @param image
     *            Image
     */
    public SrcSearchFilterPreferencePage(String title, ImageDescriptor image) {
        super(title, image);
        toolkit = new FormToolkit(Display.getCurrent());
        searchExtentionDetailTableViewerMap = new HashMap<String, TableViewer>();
        searchPackageDetailTableViewerMap = new HashMap<String, CheckboxTableViewer>();
        buttonMap = new HashMap<String, List<Button>>();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IWorkbench workbench) {
        LOGGER.info(Activator.getResourceString("debug.msg.start")
                + "SrcSearchFilterPreferencePage#init");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createContents(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
        composite.setLayout(layout);

        // Nexus Repositories
        Group group = new Group(composite, SWT.NONE);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        group.setLayoutData(gd);
        layout = new GridLayout(2, false);
        group.setLayout(layout);
        group.setText(Activator
                .getResourceString(SrcSearchFilterPreferencePage.class
                        .getName() + ".label.target"));
        tableViewer = CheckboxTableViewer.newCheckList(group, SWT.BORDER
                | SWT.V_SCROLL | SWT.SINGLE);

        Table table = tableViewer.getTable();
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.widthHint = 600;
        table.setLayoutData(gd);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        String[] columnNames = new String[] { Activator
                .getResourceString(SrcSearchFilterPreferencePage.class
                        .getName() + ".label.name") };
        int[] columnWidths = new int[] { 200 };

        for (int i = 0; i < columnNames.length; i++) {
            TableColumn tc = new TableColumn(table, SWT.LEFT);
            tc.setText(columnNames[i]);
            tc.setWidth(columnWidths[i]);
        }
        ColumnLayoutData[] layouts = { new ColumnWeightData(200, 200),
                new ColumnWeightData(200, 200) };

        TableLayout tableLayout = new AutoResizeTableLayout(table);
        for (int i = 0; i < layouts.length; i++) {
            tableLayout.addColumnData(layouts[i]);
        }

        Set<SearchFilter> originals = Activator.getSrcSearchFilters();
        searchFilters = new ArrayList<SearchFilter>(originals.size());
        for (SearchFilter original : originals) {
            searchFilters.add(cloneSearchFilter(original));
        }
        tableViewer.setLabelProvider(new SearchFilterLabelProvider());
        tableViewer.setContentProvider(new SearchFilterContentProvider(
                searchFilters));

        Collections.sort(searchFilters, new SearchFilterComparator());
        tableViewer.setInput(searchFilters);
        for (SearchFilter elem : searchFilters) {
            tableViewer.setChecked(elem, elem.isEnabled());
        }
        tableViewer.addCheckStateListener(new ICheckStateListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void checkStateChanged(CheckStateChangedEvent event) {
                SearchFilter repository = (SearchFilter) event.getElement();
                repository.setEnabled(!repository.isEnabled());
            }
        });

        Composite advancedSection = createAdvancedSection(composite);
        Map<String, List<String>> subFilterMap = new HashMap<String, List<String>>();
        Set<String> linkedOrderdSet = new LinkedHashSet<String>();
        for (SearchFilter elem : searchFilters) {
            if (elem.isParent()) {
                linkedOrderdSet.add(elem.getName());
                List<Extension> targets = elem.getTargets();
                List<String> targetExt = new ArrayList<String>();
                for (Extension extension : targets) {
                    targetExt.add(extension.getChildRef());
                }
                subFilterMap.put(elem.getName(), targetExt);
            }
        }
        createSearchSubFilterDetails(advancedSection, linkedOrderdSet,
                subFilterMap);
        return composite;
    }

    /**
     * Make the details of the sub-filter.<br/>
     * 
     * @param parent
     *            Parent element
     * @param linkedOrderdSet
     *            Set of extension
     * @param subFilterMap
     *            Map of the sub-filter
     */
    private void createSearchSubFilterDetails(Composite parent,
            Set<String> linkedOrderdSet, Map<String, List<String>> subFilterMap) {
        for (String ext : linkedOrderdSet) {
            Group group = new Group(parent, SWT.NULL);
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
            gd.widthHint = 600;
            group.setLayoutData(gd);
            GridLayout layout = new GridLayout(3, false);
            group.setLayout(layout);
            group.setText(ext);
            List<String> keyNameList = subFilterMap.get(ext);
            Set<SearchFilter> srcSearchFilters = new HashSet<SearchFilter>();

            for (String key : keyNameList) {
                srcSearchFilters.add(getSeachFilterByName(key));
            }
            createSearchSubFilterDetails(group, srcSearchFilters);
        }
    }

    /**
     * Make the details of the sub-filter. <br/>
     * 
     * @param group
     *            Group
     * @param fliters
     *            Filter set
     */
    private void createSearchSubFilterDetails(Group group,
            Set<SearchFilter> fliters) {

        for (final SearchFilter sfilter : fliters) {
            final TableViewer extentionDetailTableViewer = new TableViewer(
                    group, SWT.BORDER);

            Table table = extentionDetailTableViewer.getTable();
            GridData gd = new GridData(SWT.FILL, SWT.FILL, true, true);
            table.setLayoutData(gd);
            table.setHeaderVisible(true);
            table.setLinesVisible(true);

            String[] columnNames = new String[] { Activator
                    .getResourceString(SrcSearchFilterPreferencePage.class
                            .getName() + ".label.extension") };
            int[] columnWidths = new int[] { 200 };

            for (int i = 0; i < columnNames.length; i++) {
                TableColumn tc = new TableColumn(table, SWT.LEFT);
                tc.setText(columnNames[i]);
                tc.setWidth(columnWidths[i]);
            }

            ColumnLayoutData[] layouts = { new ColumnWeightData(200, 200),
                    new ColumnWeightData(200, 200) };

            TableLayout tableLayout = new AutoResizeTableLayout(table);
            for (int i = 0; i < layouts.length; i++) {
                tableLayout.addColumnData(layouts[i]);
            }

            extentionDetailTableViewer
                    .setLabelProvider(new SearchExtentionDetailFilterLabelProvider());
            extentionDetailTableViewer
                    .setContentProvider(new SearchExtentionDetailFilterContentProvider(
                            sfilter));

            extentionDetailTableViewer.setInput(sfilter);

            extentionDetailTableViewer
                    .addSelectionChangedListener(new ISelectionChangedListener() {

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void selectionChanged(SelectionChangedEvent arg0) {
                            ISelection sel = extentionDetailTableViewer
                                    .getSelection();
                            if (sel instanceof IStructuredSelection) {
                                IStructuredSelection selection = (IStructuredSelection) sel;
                                selectionObject = selection.getFirstElement();
                                if (selectionObject instanceof Extension) {
                                    Extension extension = (Extension) selectionObject;
                                    if (sfilter.getName().equals("xml/xml")
                                            && extension.isUserDefined()) {
                                        switchEditAndRemoveButton(
                                                sfilter.getName(), true);
                                    } else {
                                        switchEditAndRemoveButton(
                                                sfilter.getName(), false);
                                    }

                                }
                            }

                        }
                    });

            final CheckboxTableViewer packageDetailTableViewer = CheckboxTableViewer
                    .newCheckList(group, SWT.BORDER | SWT.V_SCROLL | SWT.SINGLE);
            table = packageDetailTableViewer.getTable();
            gd = new GridData(SWT.FILL, SWT.FILL, true, true);

            table.setLayoutData(gd);
            table.setHeaderVisible(true);
            table.setLinesVisible(true);

            columnNames = new String[] { Activator
                    .getResourceString(SrcSearchFilterPreferencePage.class
                            .getName() + ".label.package") };
            columnWidths = new int[] { 200 };

            for (int i = 0; i < columnNames.length; i++) {
                TableColumn tc = new TableColumn(table, SWT.LEFT);
                tc.setText(columnNames[i]);
                tc.setWidth(columnWidths[i]);
            }
            ColumnLayoutData[] layouts2 = { new ColumnWeightData(200, 200),
                    new ColumnWeightData(200, 200) };

            TableLayout tableLayout2 = new AutoResizeTableLayout(table);
            for (int i = 0; i < layouts2.length; i++) {
                tableLayout2.addColumnData(layouts2[i]);
            }
            List<WSPackage> packages = sfilter.getPackages();
            packageDetailTableViewer
                    .setLabelProvider(new SearchPackageDetailFilterLabelProvider());
            packageDetailTableViewer
                    .setContentProvider(new SearchPackageDetailFilterContentProvider(
                            packages));
            packageDetailTableViewer.setInput(packages);
            for (WSPackage elem : packages) {
                packageDetailTableViewer.setChecked(elem, elem.isEnabled());
            }
            packageDetailTableViewer
                    .addCheckStateListener(new ICheckStateListener() {
                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void checkStateChanged(
                                CheckStateChangedEvent event) {
                            WSPackage wsPackage = (WSPackage) event
                                    .getElement();
                            wsPackage.setEnabled(!wsPackage.isEnabled());
                        }
                    });
            packageDetailTableViewer
                    .addSelectionChangedListener(new ISelectionChangedListener() {

                        /**
                         * {@inheritDoc}
                         */
                        @Override
                        public void selectionChanged(SelectionChangedEvent event) {
                            ISelection sel = packageDetailTableViewer
                                    .getSelection();
                            if (sel instanceof IStructuredSelection) {
                                IStructuredSelection selection = (IStructuredSelection) sel;
                                selectionObject = selection.getFirstElement();
                                if (selectionObject instanceof WSPackage) {
                                    WSPackage wsPackage = (WSPackage) selectionObject;

                                    if (wsPackage.isUserDefined()) {
                                        switchEditAndRemoveButton(
                                                wsPackage.getType(), true);
                                    } else {
                                        switchEditAndRemoveButton(
                                                wsPackage.getType(), false);
                                    }
                                }
                            }
                        }
                    });

            this.searchExtentionDetailTableViewerMap.put(sfilter.getName(),
                    extentionDetailTableViewer);
            this.searchPackageDetailTableViewerMap.put(sfilter.getName(),
                    packageDetailTableViewer);
            createButtons(group,
                    searchPackageDetailTableViewerMap.get(sfilter.getName()),
                    sfilter);

        }

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performApply() {
        LOGGER.info(Activator.getResourceString("debug.msg.start")
                + "SrcSearchFilterPreferencePage#performApply");
        Activator.setSrcSearchFilters(new HashSet<SearchFilter>(searchFilters));
        Activator.saveSrcSearchFilters();
        Activator.getDefault().savePreferences();
        List<SearchFilter> originals = searchFilters;
        searchFilters = new ArrayList<SearchFilter>(originals.size());
        for (SearchFilter original : originals) {
            searchFilters.add(cloneSearchFilter(original));
        }
        Collections.sort(searchFilters, new SearchFilterComparator());
        tableViewer.setInput(searchFilters);
        for (SearchFilter filter : searchFilters) {
            tableViewer.setChecked(filter, filter.isEnabled());
            if (searchPackageDetailTableViewerMap.containsKey(filter.getName())) {
                CheckboxTableViewer checkboxTableViewer = searchPackageDetailTableViewerMap
                        .get(filter.getName());
                checkboxTableViewer.setInput(filter.getPackages());
                for (WSPackage wsPackage : filter.getPackages()) {
                    checkboxTableViewer.setChecked(wsPackage,
                            wsPackage.isEnabled());
                }
            }
            if (searchExtentionDetailTableViewerMap.containsKey(filter
                    .getName())) {
                TableViewer viewer = searchExtentionDetailTableViewerMap
                        .get(filter.getName());
                viewer.setInput(filter);
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performOk() {
        LOGGER.info(Activator.getResourceString("debug.msg.start")
                + "SrcSearchFilterPreferencePage#performOk");
        performApply();
        return super.performOk();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performCancel() {
        LOGGER.info(Activator.getResourceString("debug.msg.start")
                + "SrcSearchFilterPreferencePage#performCancel");
        return super.performCancel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performDefaults() {
        LOGGER.info(Activator.getResourceString("debug.msg.start")
                + "SrcSearchFilterPreferencePage#performDefault");
        Set<SearchFilter> originals = Activator.getDefaultSrcSearchFilters();
        searchFilters = new ArrayList<SearchFilter>(originals.size());
        for (SearchFilter original : originals) {
            searchFilters.add(cloneSearchFilter(original));
        }
        Collections.sort(searchFilters, new SearchFilterComparator());
        tableViewer.setInput(searchFilters);
        for (SearchFilter filter : searchFilters) {
            tableViewer.setChecked(filter, filter.isEnabled());
            if (searchPackageDetailTableViewerMap.containsKey(filter.getName())) {
                CheckboxTableViewer checkboxTableViewer = searchPackageDetailTableViewerMap
                        .get(filter.getName());
                checkboxTableViewer.setInput(filter.getPackages());
                for (WSPackage wsPackage : filter.getPackages()) {
                    checkboxTableViewer.setChecked(wsPackage, true);
                }
            }
            if (searchExtentionDetailTableViewerMap.containsKey(filter
                    .getName())) {
                TableViewer viewer = searchExtentionDetailTableViewerMap
                        .get(filter.getName());
                viewer.setInput(filter);
            }
        }

        super.performDefaults();
    }

    /**
     * Create a button.<br/>
     * 
     * @param parent
     *            Parent group
     * @param viewer
     *            Viewer
     * @param filter
     *            Filter
     */
    private void createButtons(Group parent, final CheckboxTableViewer viewer,
            final SearchFilter filter) {
        List<Button> buttons = new LinkedList<Button>();
        Composite buttonComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);

        buttonComposite.setLayout(layout);
        buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
                false));

        final Button addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        addButton.setText(Activator
                .getResourceString(SrcSearchFilterPreferencePage.class
                        .getName() + ".label.add"));

        addButton.addSelectionListener(new SelectionListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                int ok = 0;

                // File extension to open a dialog that can be added only in the
                // sub-xml filter xml
                if (filter.getName().contains("xml/xml")) {
                    EditSearchExtAndPackageDialog dialog = new EditSearchExtAndPackageDialog(
                            getShell(), null, filter.getName());
                    ok = dialog.open();
                    if (ok == Window.OK && dialog.getTextValue() != null
                            && !dialog.getTextValue().equals("")) {
                        Object value = dialog.getValue();
                        SearchFilter searchFilter = getSeachFilterByName(filter
                                .getName());
                        if (value instanceof Extension) {
                            Extension exten = (Extension) value;

                            exten.setUserDefined(true);
                            searchFilter.getTargets().add(exten);
                            TableViewer checkboxExtTableViewer = searchExtentionDetailTableViewerMap
                                    .get(filter.getName());
                            checkboxExtTableViewer.refresh();

                        } else if (value instanceof WSPackage) {
                            WSPackage wsPackage = (WSPackage) value;
                            wsPackage.setUserDefined(true);
                            searchFilter.getPackages().add(wsPackage);
                            CheckboxTableViewer checkboxPkgTableViewer = searchPackageDetailTableViewerMap
                                    .get(filter.getName());
                            checkboxPkgTableViewer.refresh();
                            checkboxPkgTableViewer.setChecked(wsPackage,
                                    wsPackage.isEnabled());
                        }
                    }
                } else {
                    EditSearchPackageDialog dialog = new EditSearchPackageDialog(
                            getShell(), null, filter.getName());

                    ok = dialog.open();
                    if (ok == Window.OK && dialog.getWsPackage() != null
                            && !dialog.getWsPackage().getName().equals("")) {
                        WSPackage wsPackage = dialog.getWsPackage();
                        wsPackage.setUserDefined(true);
                        SearchFilter searchFilter = getSeachFilterByName(filter
                                .getName());
                        if (searchFilter != null) {
                            searchFilter.getPackages().add(wsPackage);
                            viewer.refresh();
                            viewer.setChecked(wsPackage, wsPackage.isEnabled());
                        }
                    }
                }
                viewer.refresh();
            }
        });

        final Button editButton = new Button(buttonComposite, SWT.PUSH);
        editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        editButton.setText(Activator
                .getResourceString(SrcSearchFilterPreferencePage.class
                        .getName() + ".label.edit"));
        editButton.setEnabled(false);

        editButton.addSelectionListener(new SelectionListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {

                if (selectionObject instanceof WSPackage) {
                    WSPackage wsPackage = (WSPackage) selectionObject;
                    WSPackage editWsPackage = new WSPackage(
                            wsPackage.getType(), wsPackage.getName(), wsPackage
                                    .isEnabled());
                    EditSearchExtAndPackageDialog dialog = new EditSearchExtAndPackageDialog(
                            getShell(), editWsPackage, filter.getName());
                    int ok = dialog.open();
                    if (ok == Window.OK && dialog.getTextValue() != null
                            && !dialog.getTextValue().equals("")) {
                        wsPackage.setName(dialog.getTextValue());
                        viewer.refresh();
                        viewer.setChecked(filter, filter.isEnabled());
                    }

                } else if (selectionObject instanceof Extension) {
                    Extension selectExtension = (Extension) selectionObject;
                    EditSearchExtAndPackageDialog dialog = new EditSearchExtAndPackageDialog(
                            getShell(), selectExtension, filter.getName());
                    int ok = dialog.open();
                    if (ok == Window.OK && dialog.getTextValue() != null
                            && !dialog.getTextValue().equals("")) {
                        selectExtension.setName(dialog.getTextValue());
                        TableViewer checkboxTableViewer = searchExtentionDetailTableViewerMap
                                .get(filter.getName());
                        checkboxTableViewer.refresh();
                    }
                }
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });

        final Button removeButton = new Button(buttonComposite, SWT.PUSH);
        removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        removeButton.setText(Activator
                .getResourceString(SrcSearchFilterPreferencePage.class
                        .getName() + ".label.remove"));
        removeButton.setEnabled(false);
        removeButton.addSelectionListener(new SelectionListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {

                if (selectionObject instanceof WSPackage) {
                    WSPackage wsPackage = (WSPackage) selectionObject;
                    SearchFilter seachFilterByName = getSeachFilterByName(filter
                            .getName());
                    seachFilterByName.getPackages().remove(wsPackage);
                    viewer.refresh();
                } else if (selectionObject instanceof Extension) {
                    Extension selectExtension = (Extension) selectionObject;
                    SearchFilter seachFilterByName = getSeachFilterByName(filter
                            .getName());
                    seachFilterByName.getTargets().remove(selectExtension);
                    TableViewer checkboxTableViewer = searchExtentionDetailTableViewerMap
                            .get(filter.getName());
                    checkboxTableViewer.refresh();
                }
            }

            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });

        buttons.add(addButton);
        buttons.add(editButton);
        buttons.add(removeButton);
        this.buttonMap.put(filter.getName(), buttons);
    }

    /**
     * Get the button map.<br/>
     * 
     * @return Button map
     */
    public HashMap<String, List<Button>> getButtonMap() {
        return buttonMap;
    }

    /**
     * Search filter for content provider class.<br/>
     */
    private static class SearchFilterContentProvider implements
            IStructuredContentProvider {

        /**
         * Repository set
         */
        private List<SearchFilter> repositories;

        /**
         * Constructor.<br/>
         * Nothing in particular.<br/>
         * 
         * @param repositories
         *            Repository set
         */
        public SearchFilterContentProvider(List<SearchFilter> repositories) {

            this.repositories = repositories;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] getElements(Object inputElement) {
            Set<Object> sets = new LinkedHashSet<Object>();
            for (SearchFilter searchFilter : repositories) {
                if (searchFilter.isParent()) {
                    sets.add(searchFilter);
                }
            }
            return sets.toArray();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {

        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            repositories = (List<SearchFilter>) newInput;
        }
    }

    /**
     * Search filter for label provider class.<br/>
     */
    private static class SearchFilterLabelProvider extends LabelProvider
            implements ITableLabelProvider {

        /**
         * Constructor.<br/>
         * Nothing in particular.<br/>
         */
        public SearchFilterLabelProvider() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof SearchFilter) {
                SearchFilter nr = (SearchFilter) element;
                if (columnIndex == 0) {
                    return nr.getName();
                }
            }
            return null;
        }
    }

    /**
     * It is the extension for more information content provider class.<br/>
     */
    private static class SearchExtentionDetailFilterContentProvider implements
            IStructuredContentProvider {

        /**
         * Filter
         */
        private SearchFilter filter;

        /**
         * Constructor.<br/>
         * Nothing in particular.<br/>
         * 
         * @param filter
         *            Filter
         */
        public SearchExtentionDetailFilterContentProvider(SearchFilter filter) {
            this.filter = filter;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] getElements(Object inputElement) {
            return filter.getTargets().toArray();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            filter = (SearchFilter) newInput;
        }
    }

    /**
     * It is the extension details for label provider class. <br/>
     */
    private static class SearchExtentionDetailFilterLabelProvider extends
            LabelProvider implements ITableLabelProvider, IColorProvider {

        /**
         * {@inheritDoc}
         */
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof Extension) {
                Extension extension = (Extension) element;
                if (columnIndex == 0) {
                    return extension.getName();
                }
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Color getBackground(Object arg0) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Color getForeground(Object element) {
            if (element instanceof Extension) {
                Extension extension = (Extension) element;
                if (!extension.isUserDefined()) {
                    return Display.getCurrent().getSystemColor(
                            SWT.COLOR_DARK_GRAY);
                }
            }
            return null;
        }
    }

    /**
     * It is excluded for more information content provider class.<br/>
     */
    private static class SearchPackageDetailFilterContentProvider implements
            IStructuredContentProvider {

        /**
         * Exclusions
         */
        private List<WSPackage> wsPackages;

        /**
         * Constructor.<br/>
         * Nothing in particular.<br/>
         * 
         * @param wsPackages
         *            Exclusions
         */
        public SearchPackageDetailFilterContentProvider(
                List<WSPackage> wsPackages) {
            this.wsPackages = wsPackages;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] getElements(Object inputElement) {
            return wsPackages.toArray();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public void dispose() {
        }

        /**
         * {@inheritDoc}
         */
        @Override
        @SuppressWarnings("unchecked")
        public void inputChanged(Viewer viewer, Object oldInput, Object newInput) {
            wsPackages = (List<WSPackage>) newInput;
        }
    }

    /**
     * It is excluded information for label provider class.<br/>
     */
    private static class SearchPackageDetailFilterLabelProvider extends
            LabelProvider implements ITableLabelProvider, IColorProvider {

        /**
         * Constructor.<br/>
         * Nothing in particular.<br/>
         */
        public SearchPackageDetailFilterLabelProvider() {
            super();
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Image getColumnImage(Object element, int columnIndex) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public String getColumnText(Object element, int columnIndex) {
            if (element instanceof WSPackage) {
                WSPackage wsPackage = (WSPackage) element;
                if (columnIndex == 0) {
                    return wsPackage.getName();
                }
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Color getBackground(Object arg0) {
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Color getForeground(Object element) {
            if (element instanceof WSPackage) {
                WSPackage wsPackage = (WSPackage) element;
                if (!wsPackage.isUserDefined()) {
                    return Display.getCurrent().getSystemColor(
                            SWT.COLOR_DARK_GRAY);
                }
            }
            return null;
        }
    }

    /**
     * Create a detail section.<br/>
     * 
     * @param container
     *            Container
     * @return Details section
     */
    private Composite createAdvancedSection(Composite container) {
        advancedComposite = toolkit.createExpandableComposite(container,
                ExpandableComposite.COMPACT | ExpandableComposite.TWISTIE
                        | ExpandableComposite.TITLE_BAR);
        advancedComposite.setFont(container.getFont());
        advancedComposite.setBackground(container.getBackground());
        advancedComposite.setText(Activator
                .getResourceString(SrcSearchFilterPreferencePage.class
                        .getName() + ".label.advanced"));
        advancedComposite.setExpanded(true);
        advancedComposite.setLayout(new GridLayout(3, true));
        advancedComposite.addExpansionListener(new ExpansionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                Object obj = e.getSource();
                handleExpand(getScrollingParent(obj));
            }
        });

        Composite advanced = new Composite(advancedComposite, SWT.None);
        advanced.setLayout(new GridLayout(1, false));
        advancedComposite.setClient(advanced);
        return advanced;
    }

    /**
     * Get the parent element that scroll.<br/>
     * 
     * @param obj
     *            Element
     * @return Parent element
     */
    private ScrolledComposite getScrollingParent(Object obj) {
        if (obj instanceof ExpandableComposite) {
            ExpandableComposite ecomp = (ExpandableComposite) obj;
            Composite parent = ecomp.getParent();
            while (parent != null && !(parent instanceof ScrolledComposite)) {
                parent = parent.getParent();
            }
            if (parent != null) {
                return (ScrolledComposite) parent;
            }
        }
        if (obj instanceof ScrolledComposite) {
            return (ScrolledComposite) obj;
        }
        return null;
    }

    /**
     * control the expansion of the element.<br/>
     * 
     * @param composite
     *            Element
     */
    private void handleExpand(ScrolledComposite composite) {
        if (composite == null) {
            return;
        }
        try {
            composite.setRedraw(false);
            Composite c = (Composite) composite.getContent();
            if (c == null) {
                return;
            }
            Point newSize = c.computeSize(SWT.DEFAULT, SWT.DEFAULT);
            composite.setMinSize(newSize);
            c.layout(true);
        } finally {
            composite.setRedraw(true);
        }
    }

    /**
     * Get a filter from the name.<br/>
     * 
     * @param name
     *            Name
     * @return Filter
     */
    private SearchFilter getSeachFilterByName(String name) {
        for (SearchFilter searchFilter : searchFilters) {
            if (searchFilter.getName().equals(name)) {
                return searchFilter;
            }
        }
        return null;
    }

    /**
     * Switch enable delete button and edit button, the invalid.<br/>
     * 
     * @param name
     *            Button name
     * @param toggle
     *            The determination whether to enabled or disabled
     */
    private void switchEditAndRemoveButton(String name, boolean toggle) {
        List<Button> list = buttonMap.get(name);
        Button editButton = list.get(1);
        Button removeButton = list.get(2);
        editButton.setEnabled(toggle);
        removeButton.setEnabled(toggle);
    }

    /**
     * Search filter comparison class.<br/>
     */
    private static class SearchFilterComparator implements
            Comparator<SearchFilter>, Serializable {
        /**
         * Serialization key
         */
        private static final long serialVersionUID = 5200728610212996761L;

        /**
         * {@inheritDoc}
         */
        @Override
        public int compare(SearchFilter arg0, SearchFilter arg1) {
            return arg0.getName().compareTo(arg1.getName());
        }
    }

    /**
     * Make a copy of SearchFilter.<br/>
     * 
     * @param original
     *            SearchFilter the source
     * @return SearchFilter created
     */
    private SearchFilter cloneSearchFilter(SearchFilter original) {
        List<Extension> targets = new ArrayList<Extension>(original
                .getTargets().size());
        for (Extension orgExt : original.getTargets()) {
            Extension ext = new Extension(orgExt.getName(), orgExt.isEnabled(),
                    orgExt.isUserDefined());
            ext.setChildRef(orgExt.getChildRef());
            targets.add(ext);
        }
        List<WSPackage> packages = new ArrayList<WSPackage>(original
                .getPackages().size());
        for (WSPackage orgPackage : original.getPackages()) {
            WSPackage wsPackage = new WSPackage(orgPackage.getType(),
                    orgPackage.getName(), orgPackage.isEnabled());
            wsPackage.setUserDefined(orgPackage.isUserDefined());
            packages.add(wsPackage);
        }
        SearchFilter filter = new SearchFilter(original.getName(), targets,
                packages, original.isEnabled());
        filter.setParent(original.isParent());
        return filter;
    }
}
