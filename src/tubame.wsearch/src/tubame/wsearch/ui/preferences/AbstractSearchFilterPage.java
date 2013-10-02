/*
 * AbstractSearchFilterPage.java
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
package tubame.wsearch.ui.preferences;

import java.util.HashMap;
import java.util.LinkedHashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Set;


import org.eclipse.jface.preference.PreferencePage;
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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import tubame.wsearch.Activator;
import tubame.wsearch.biz.model.Extension;
import tubame.wsearch.biz.model.SearchFilter;
import tubame.wsearch.biz.model.WSPackage;
import tubame.wsearch.ui.dialog.EditSearchExtAndPackageDialog;
import tubame.wsearch.ui.dialog.EditSearchPackageDialog;
import tubame.wsearch.ui.layout.AutoResizeTableLayout;

/**
 * It is the base class of filter configuration page.<br/>
 */
public abstract class AbstractSearchFilterPage extends PreferencePage implements
        IWorkbenchPreferencePage {

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param title
     *            Title
     */
    public AbstractSearchFilterPage(String title) {
        super(title);
    }

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     */
    public AbstractSearchFilterPage() {

    }

    /**
     * Table viewer
     */
    protected CheckboxTableViewer tableViewer;

    /**
     * Filter set
     */
    protected Set<SearchFilter> searchFilters;

    /**
     * Composite extension
     */
    protected ExpandableComposite advancedComposite;

    /**
     * Toolkit
     */
    protected FormToolkit toolkit;

    /**
     * Map of extension detail table
     */
    protected HashMap<String, TableViewer> searchExtentionDetailTableViewerMap;

    /**
     * Map of package detail table
     */
    protected HashMap<String, CheckboxTableViewer> searchPackageDetailTableViewerMap;

    /**
     * Button map
     */
    protected HashMap<String, List<Button>> buttonMap;

    /**
     * Selected object
     */
    protected Object selectionObject;

    /**
     * Make the details of the sub-filter.<br/>
     * 
     * @param group
     *            Group
     * @param fliters
     *            Filter set
     */
    protected void createSearchSubFilterDetails(Group group,
            Set<SearchFilter> fliters) {

        for (final SearchFilter sfilter : fliters) {
            final TableViewer extentionDetailTableViewer = new TableViewer(
                    group, SWT.BORDER);
            Table table = extentionDetailTableViewer.getTable();
            GridData gd = new GridData(GridData.FILL_BOTH);
            table.setLayoutData(gd);
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            String[] columnNames = new String[] { Activator
                    .getResourceString(AbstractSearchFilterPage.class.getName()
                            + ".label.extension") };
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
            gd = new GridData(GridData.FILL_BOTH);
            table.setLayoutData(gd);
            table.setHeaderVisible(true);
            table.setLinesVisible(true);
            columnNames = new String[] { Activator
                    .getResourceString(AbstractSearchFilterPage.class.getName()
                            + ".label.package") };
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
     * Create a button.<br/>
     * 
     * @param parent
     *            Parent group
     * @param viewer
     *            Viewer
     * @param filter
     *            Filter
     */
    protected void createButtons(Group parent,
            final CheckboxTableViewer viewer, final SearchFilter filter) {
        List<Button> buttons = new LinkedList<Button>();
        Composite buttonComposite = new Composite(parent, SWT.NONE);
        GridLayout layout = new GridLayout(1, false);
        buttonComposite.setLayout(layout);
        buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
                false));
        final Button addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        addButton.setText(Activator
                .getResourceString(AbstractSearchFilterPage.class.getName()
                        + ".label.add"));
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
                // sub-xml filter xml.
                if (filter.getName().contains("xml/xml")) {
                    EditSearchExtAndPackageDialog dialog = new EditSearchExtAndPackageDialog(
                            getShell(), null, filter.getName());
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
                            TableViewer checkboxPkgTableViewer = searchPackageDetailTableViewerMap
                                    .get(filter.getName());
                            checkboxPkgTableViewer.refresh();
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
                .getResourceString(AbstractSearchFilterPage.class.getName()
                        + ".label.edit"));
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
                .getResourceString(AbstractSearchFilterPage.class.getName()
                        + ".label.remove"));
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
                switchEditAndRemoveButton(filter.getName(), false);
            }

            /**
             * {@inheritDoc}
             */
            @Override
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
    protected HashMap<String, List<Button>> getButtonMap() {
        return buttonMap;
    }

    /**
     * Get a filter from the name.<br/>
     * 
     * @param name
     *            Name
     * @return Filter
     */
    protected SearchFilter getSeachFilterByName(String name) {
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
    protected void switchEditAndRemoveButton(String name, boolean toggle) {
        List<Button> list = buttonMap.get(name);
        Button editButton = list.get(1);
        Button removeButton = list.get(2);
        editButton.setEnabled(toggle);
        removeButton.setEnabled(toggle);
    }

    /**
     * Search filter for content provider class.<br/>
     */
    protected static class SearchFilterContentProvider implements
            IStructuredContentProvider {

        /**
         * Repository set
         */
        private Set<SearchFilter> repositories;

        /**
         * Constructor.<br/>
         * Nothing in particular.<br/>
         * 
         * @param repositories
         *            Repository set
         */
        public SearchFilterContentProvider(Set<SearchFilter> repositories) {
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
            repositories = (Set<SearchFilter>) newInput;
        }
    }

    /**
     * Search filter for label provider class.<br/>
     */
    protected static class SearchFilterLabelProvider extends LabelProvider
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
     * Extension advanced filter for content provider class.<br/>
     */
    protected static class SearchExtentionDetailFilterContentProvider implements
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
     * Extension advanced filter for label provider class.<br/>
     */
    protected static class SearchExtentionDetailFilterLabelProvider extends
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
     * It is a package for advanced filter content provider class.<br/>
     */
    protected static class SearchPackageDetailFilterContentProvider implements
            IStructuredContentProvider {

        /**
         * Package
         */
        private List<WSPackage> wsPackages;

        /**
         * Constructor.<br/>
         * Nothing in particular.<br/>
         * 
         * @param wsPackages
         *            Package
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
     * It is a package for advanced filter label provider class.<br/>
     */
    protected static class SearchPackageDetailFilterLabelProvider extends
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
}