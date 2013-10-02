/*
 * LibraryRepositoryPreferencePage.java
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

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.Map;

import tubame.common.util.CmnStringUtil;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.IDialogConstants;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.jface.viewers.CheckStateChangedEvent;
import org.eclipse.jface.viewers.CheckboxTableViewer;
import org.eclipse.jface.viewers.ICheckStateListener;
import org.eclipse.jface.viewers.IColorProvider;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.IStructuredContentProvider;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.ITableLabelProvider;
import org.eclipse.jface.viewers.LabelProvider;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.SWT;
import org.eclipse.swt.custom.StyleRange;
import org.eclipse.swt.custom.StyledText;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.graphics.Image;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Table;
import org.eclipse.swt.widgets.TableColumn;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.eclipse.ui.WorkbenchException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.wsearch.Activator;
import tubame.wsearch.biz.cache.CacheBase;
import tubame.wsearch.biz.cache.WSearchLibraryCacheArgument;
import tubame.wsearch.biz.model.LibraryModel;
import tubame.wsearch.biz.util.FileVisitor;
import tubame.wsearch.biz.util.PomReader;
import tubame.wsearch.biz.util.ZipUtil;
import tubame.wsearch.models.LibraryRepository;
import tubame.wsearch.ui.dialog.EditLibraryRepositoryDialog;
import tubame.wsearch.ui.dialog.EditLibraryRepositoryImportExportDialog;
import tubame.wsearch.ui.dialog.ErrorDialog;

/**
 * It is a library repository configuration page class.<br/>
 */
public class LibraryRepositoryPreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {

    /**
     * Tree Viewer
     */
    private CheckboxTableViewer tableViewer;

    /**
     * List of repository
     */
    private List<LibraryRepository> libraryRepositories;

    /**
     * More text
     */
    private StyledText detailsText;

    /**
     * The selected library
     */
    private LibraryRepository selectionLibrary;

    /**
     * Button of whether to use a cache
     */
    private Button[] cacheUseCheckButton;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(LibraryRepositoryPreferencePage.class);

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     */
    public LibraryRepositoryPreferencePage() {
    }

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param title
     *            Title
     */
    public LibraryRepositoryPreferencePage(String title) {
        super(title);
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
    public LibraryRepositoryPreferencePage(String title, ImageDescriptor image) {
        super(title, image);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performApply() {
        LOGGER.debug(Activator.getResourceString("debug.msg.start")
                + "LibraryRepositoryPreferencePage#performApply");
        Map<String, LibraryRepository> repositoryMap = new HashMap<String, LibraryRepository>();
        for (LibraryRepository rep : libraryRepositories) {
            repositoryMap.put(rep.getName(), rep);
        }
        Activator.setLibraryRepositories(repositoryMap);
        Activator.saveLibraryRepositories();
        Activator.getDefault().savePreferences();
        tableViewer.setInput(libraryRepositories);
        for (LibraryRepository libRepository : libraryRepositories) {
            tableViewer.setChecked(libRepository, libRepository.isEnabled());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IWorkbench workbench) {
        LOGGER.info(Activator.getResourceString("debug.msg.start")
                + "LibraryRepositoryPreferencePage#init");
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performOk() {
        LOGGER.info(Activator.getResourceString("debug.msg.start")
                + "LibraryRepositoryPreferencePage#performOk");
        performApply();
        return super.performOk();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean performCancel() {
        LOGGER.info(Activator.getResourceString("debug.msg.start")
                + "LibraryRepositoryPreferencePage#performCancel");
        return super.performCancel();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void performDefaults() {
        LOGGER.info(Activator.getResourceString("debug.msg.start")
                + "LibraryRepositoryPreferencePage#performDefault");
        boolean openQuestion = false;
        List<LibraryRepository> repos = null;
        try {
            repos = existUserGenerationCache();
        } catch (Exception e1) {
            Activator.logWithDialog(e1, Activator
                    .getResourceString(LibraryRepositoryPreferencePage.class
                            .getName()
                            + ".err.msg.UserGenerationCacheCheckFail"));
            return;
        }
        if (repos != null && !repos.isEmpty()) {

            // If the cache of built-in Plugin other than the present, the
            // restoration can be cached load.
            openQuestion = MessageDialog.openQuestion(getShell(), Activator
                    .getResourceString(LibraryRepositoryPreferencePage.class
                            .getName() + ".title.confirm"), Activator
                    .getResourceString(LibraryRepositoryPreferencePage.class
                            .getName() + ".label.LoadCache"));

            for (LibraryRepository libraryRepository : repos) {
                if (!openQuestion && !libraryRepository.isPluginEmbedded()) {
                    continue;
                }
                boolean exists = false;
                for (LibraryRepository rep : libraryRepositories) {
                    if (rep.getName().equals(libraryRepository.getName())) {
                        exists = true;
                    }
                }
                if (!exists) {
                    libraryRepository.setOrder(libraryRepositories.size() + 1);
                    libraryRepositories.add(libraryRepository);
                }
            }
        }
        Collections.sort(libraryRepositories,
                new Comparator<LibraryRepository>() {
                    @Override
                    public int compare(LibraryRepository arg0,
                            LibraryRepository arg1) {
                        return arg0.getOrder() - arg1.getOrder();
                    }
                });
        tableViewer.setInput(libraryRepositories);
        for (LibraryRepository repository : libraryRepositories) {
            tableViewer.setChecked(repository, repository.isEnabled());
        }
        tableViewer.refresh();
        cacheUseCheckButton[0].setSelection(true);
        cacheUseCheckButton[1].setSelection(false);
        super.performDefaults();
    }

    /**
     * Determine if there is a cache that is created by the user.<br/>
     * 
     * @return Determine if there is a cache that is created by the user
     * @throws Exception
     *             Exception
     */
    private static List<LibraryRepository> existUserGenerationCache()
            throws Exception {

        // If there is any thing other than the built-in plugin information from
        // the cache meta file in the library cache directory, load it.
        return Activator.checkExistUserCacheFromCacheFolder();

    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createContents(Composite parent) {
        initializeDialogUnits(parent);
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
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.LibrarySetting"));
        tableViewer = CheckboxTableViewer.newCheckList(group, SWT.BORDER
                | SWT.V_SCROLL | SWT.SINGLE);
        Table table = tableViewer.getTable();
        gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = 400;
        gd.heightHint = 200;

        table.setLayoutData(gd);
        table.setHeaderVisible(true);
        table.setLinesVisible(true);

        String[] columnNames = new String[] {
                Activator
                        .getResourceString(LibraryRepositoryPreferencePage.class
                                .getName() + ".label.name.simple"),
                Activator
                        .getResourceString(LibraryRepositoryPreferencePage.class
                                .getName() + ".label.url.simple") };
        int[] columnWidths = new int[] { 150, 300 };

        for (int i = 0; i < columnNames.length; i++) {
            TableColumn tc = new TableColumn(table, SWT.LEFT);
            tc.setText(columnNames[i]);
            tc.setWidth(columnWidths[i]);

        }
        try {
            Map<String, LibraryRepository> originals = Activator
                    .getLibraryRepositories();
            libraryRepositories = new ArrayList<LibraryRepository>(
                    originals.size());
            for (LibraryRepository original : originals.values()) {
                LibraryRepository repository = new LibraryRepository(
                        original.getName(), original.getUrl(),
                        original.getDescription(), original.isEnabled(),
                        original.isMavenType(), original.isPluginEmbedded());
                repository.setZipCache(original.isZipCache());
                repository.setCache(original.hasCache());
                repository.setOrder(original.getOrder());
                libraryRepositories.add(repository);
            }
            Collections.sort(libraryRepositories,
                    new Comparator<LibraryRepository>() {
                        @Override
                        public int compare(LibraryRepository o1,
                                LibraryRepository o2) {
                            return o1.getOrder() - o2.getOrder();
                        }
                    });
        } catch (WorkbenchException e) {
            String message = Activator
                    .getResourceString(LibraryRepositoryPreferencePage.class
                            .getName() + ".err.msg.GetDefaultRepoErr");
            Activator.log(e, message);
            ErrorDialog.openErrorDialog(getShell(), e, message);
            return null;
        }

        tableViewer.setLabelProvider(new LibraryRepositoryLabelProvider());
        tableViewer.setContentProvider(new LibraryRepositoryContentProvider(
                libraryRepositories));
        tableViewer.setInput(libraryRepositories);
        for (LibraryRepository elem : libraryRepositories) {
            tableViewer.setChecked(elem, elem.isEnabled());
        }
        tableViewer.addCheckStateListener(new ICheckStateListener() {
            public void checkStateChanged(CheckStateChangedEvent event) {
                LibraryRepository repository = (LibraryRepository) event
                        .getElement();
                repository.setEnabled(!repository.isEnabled());
            }
        });

        // Nexus Repositories
        Group detailsGroup = new Group(composite, SWT.NONE);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        detailsGroup.setLayoutData(gd);
        layout = new GridLayout(2, false);
        detailsGroup.setLayout(layout);
        detailsGroup.setText(Activator
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.detail"));

        createDetailsText(detailsGroup);
        createButtons(group, tableViewer);

        // Cache Check Area
        Group cacheCheck = new Group(composite, SWT.NONE);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        cacheCheck.setLayoutData(gd);
        layout = new GridLayout(2, false);
        cacheCheck.setLayout(layout);
        cacheCheck.setText(Activator
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.cache.check"));
        createCacheCheckArea(cacheCheck);
        tableViewer.refresh();
        return composite;
    }

    /**
     * Create a cache check area.<br/>
     * 
     * @param group
     *            Group
     */
    private void createCacheCheckArea(Group group) {

        cacheUseCheckButton = new Button[2];
        cacheUseCheckButton[0] = new Button(group, SWT.RADIO);
        cacheUseCheckButton[0].setSelection(Activator.useLibraryCache());
        cacheUseCheckButton[0].setText(Activator
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.cache.use"));
        cacheUseCheckButton[0].setLocation(50, 250);
        cacheUseCheckButton[0].addSelectionListener(new SelectionListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                Activator.setUseLibraryCache(((Button) e.widget).getSelection());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        cacheUseCheckButton[0].pack();

        cacheUseCheckButton[1] = new Button(group, SWT.RADIO);
        cacheUseCheckButton[1].setSelection(!Activator.useLibraryCache());
        cacheUseCheckButton[1].setText(Activator
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.cache.notuse"));
        cacheUseCheckButton[1].setLocation(120, 250);
        cacheUseCheckButton[1].pack();

    }

    /**
     * Create an advanced text.<br/>
     * 
     * @param composite
     *            Composite
     */
    private void createDetailsText(Composite composite) {
        this.detailsText = new StyledText(composite, SWT.BORDER | SWT.V_SCROLL);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = 300;
        gd.heightHint = 150;
        this.detailsText.setEditable(false);
        this.detailsText.setLayoutData(gd);
    }

    /**
     * Update the description text.<br/>
     * 
     * @param libraryRepository
     *            Library repository
     */
    private void updateDetailsText(LibraryRepository libraryRepository) {
        List<StyleRange> styles = new ArrayList<StyleRange>();
        StringBuilder builder = new StringBuilder();
        libraryRepository.getName();
        String mavenType = Activator
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.MavenType.NotMaven");

        if (libraryRepository.isMavenType()) {
            mavenType = Activator
                    .getResourceString(LibraryRepositoryPreferencePage.class
                            .getName() + ".label.MavenType.Maven");
        }
        String hasCache = Activator
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.no");
        if (libraryRepository.hasCache()) {
            hasCache = Activator
                    .getResourceString(LibraryRepositoryPreferencePage.class
                            .getName() + ".label.yes");
        }
        appendLabelAndValue(
                Activator.getResourceString(this.getClass().getName()
                        + ".label.name"), libraryRepository.getName(), builder,
                styles);
        appendLabelAndValue(
                Activator.getResourceString(this.getClass().getName()
                        + ".label.url"), libraryRepository.getUrl(), builder,
                styles);
        appendLabelAndValue(
                Activator.getResourceString(this.getClass().getName()
                        + ".label.cache"), hasCache, builder, styles);
        appendLabelAndValue(
                Activator.getResourceString(this.getClass().getName()
                        + ".label.type"), mavenType, builder, styles);
        appendLabelAndValue(
                Activator.getResourceString(this.getClass().getName()
                        + ".label.description"),
                libraryRepository.getDescription(), builder, styles);
        this.detailsText.setText(builder.toString());

        for (StyleRange style : styles) {
            this.detailsText.setStyleRange(style);
        }
    }

    /**
     * Add value and label.<br/>
     * 
     * @param label
     *            Label
     * @param value
     *            Value
     * @param builder
     *            StringBuilder
     * @param styles
     *            Style
     */
    private void appendLabelAndValue(String label, String value,
            StringBuilder builder, List<StyleRange> styles) {
        StyleRange styleRange = startLabelStyleRange(builder);
        builder.append(label);
        finishLabelStyleRange(builder, styleRange);
        builder.append(value).append("\n");
        styles.add(styleRange);
    }

    /**
     * begin to configure the style.<br/>
     * 
     * @param builder
     *            StringBuilder
     * @return Styling
     */
    private StyleRange startLabelStyleRange(StringBuilder builder) {
        StyleRange styleRange = new StyleRange();
        styleRange.start = builder.length();
        styleRange.fontStyle = SWT.BOLD;
        return styleRange;
    }

    /**
     * Finish setting up the style.<br/>
     * 
     * @param builder
     *            StringBuilder
     * @param styleRange
     *            Styling
     * @return Styling
     */
    private StyleRange finishLabelStyleRange(StringBuilder builder,
            StyleRange styleRange) {
        styleRange.length = builder.length() - styleRange.start;
        return styleRange;
    }

    /**
     * Create a button.<br/>
     * 
     * @param parent
     *            Parent group
     * @param viewer
     *            Checkbox table viewer
     */
    private void createButtons(Group parent, final CheckboxTableViewer viewer) {
        Composite buttonComposite = new Composite(parent, SWT.NONE);
        buttonComposite.setLayout(new GridLayout(1, false));
        buttonComposite.setLayoutData(new GridData(SWT.FILL, SWT.TOP, false,
                false));
        Button addButton = new Button(buttonComposite, SWT.PUSH);
        addButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        addButton.setText(Activator
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.add"));
        addButton.addSelectionListener(new SelectionListener() {

            /**
             * {@inheritDoc}
             */
            public void widgetDefaultSelected(SelectionEvent e) {

            }

            /**
             * {@inheritDoc}
             */
            public void widgetSelected(SelectionEvent e) {
                EditLibraryRepositoryDialog dialog = new EditLibraryRepositoryDialog(
                        getShell(), null, false);
                int ok = dialog.open();
                if (ok == Window.OK) {
                    LibraryRepository repository = dialog.getRepository();
                    String libraryName = repository.getName();
                    repository.setOrder(libraryRepositories.size() + 1);
                    int index = -1;
                    for (LibraryRepository rep : libraryRepositories) {
                        if (rep.getName().equals(libraryName)) {
                            int ret = new Dialog(getShell()) {
                                protected Control createDialogArea(
                                        Composite parent) {
                                    Composite composite = (Composite) super
                                            .createDialogArea(parent);
                                    Label label = new Label(composite,
                                            SWT.SHADOW_NONE);
                                    label.setText(Activator
                                            .getResourceString(LibraryRepositoryPreferencePage.class
                                                    .getName()
                                                    + ".label.AddOverwrite"));
                                    return composite;
                                }
                            }.open();
                            if (ret == IDialogConstants.CANCEL_ID) {
                                return;
                            }
                            repository.setOrder(rep.getOrder());
                            index = libraryRepositories.indexOf(rep);
                            break;
                        }
                    }
                    File cacheDir = new File(
                            Activator.getLibraryCacheHomeDir(), libraryName);
                    if (cacheDir.exists()) {
                        try {
                            FileVisitor.walkFileTree(cacheDir,
                                    new FileVisitor() {
                                        @Override
                                        public FileVisitResult visitFile(
                                                File file) throws IOException {
                                            file.delete();
                                            return FileVisitResult.CONTINUE;
                                        }

                                        @Override
                                        public FileVisitResult postVisitDirectory(
                                                File dir) throws IOException {
                                            dir.delete();
                                            return FileVisitResult.CONTINUE;
                                        }
                                    });
                        } catch (IOException e1) {
                            Activator.log(e1);
                            ErrorDialog
                                    .openErrorDialog(
                                            getShell(),
                                            null,
                                            Activator.getResourceString(LibraryRepositoryPreferencePage.class
                                                    .getName()
                                                    + ".err.msg.DeleteCacheErr"));
                            return;
                        }
                    }
                    if (index >= 0) {
                        libraryRepositories.set(index, repository);
                    } else {
                        libraryRepositories.add(repository);
                    }
                    viewer.refresh();
                    tableViewer.setChecked(repository, repository.isEnabled());
                }
                viewer.refresh();
            }
        });

        final Button editButton = new Button(buttonComposite, SWT.PUSH);
        editButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        editButton.setText(Activator
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.edit"));
        editButton.setEnabled(false);

        editButton.addSelectionListener(new SelectionListener() {

            /**
             * {@inheritDoc}
             */
            public void widgetSelected(SelectionEvent e) {
                ISelection sel = viewer.getSelection();
                if (sel instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection) sel;
                    Object object = selection.getFirstElement();
                    if (object instanceof LibraryRepository) {
                        LibraryRepository repository = (LibraryRepository) object;
                        LibraryRepository edit = new LibraryRepository(
                                repository.getName(), repository.getUrl(),
                                repository.getDescription(), repository
                                        .isEnabled(), repository.isMavenType(),
                                repository.isPluginEmbedded());

                        boolean zip = true;
                        if (new File(repository.getUrl()).isDirectory()) {
                            zip = false;
                        }
                        EditLibraryRepositoryDialog dialog = new EditLibraryRepositoryDialog(
                                getShell(), edit, zip);
                        int ok = dialog.open();
                        if (ok == Window.OK) {
                            repository.setName(edit.getName());
                            repository.setUrl(edit.getUrl());
                            repository.setEnabled(edit.isEnabled());
                            repository.setDescription(edit.getDescription());
                            viewer.refresh();
                            tableViewer.setChecked(repository,
                                    repository.isEnabled());
                            updateDetailsText(repository);
                        }
                    }
                }
            }

            /**
             * {@inheritDoc}
             */
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });

        final Button removeButton = new Button(buttonComposite, SWT.PUSH);
        removeButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        removeButton.setText(Activator
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.remove"));
        removeButton.setEnabled(false);

        final Button upButton = new Button(buttonComposite, SWT.PUSH);
        upButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        upButton.setText(Activator
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.up"));
        upButton.setEnabled(false);

        final Button downButton = new Button(buttonComposite, SWT.PUSH);
        downButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        downButton.setText(Activator
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.down"));
        downButton.setEnabled(false);

        final Button exportButton = new Button(buttonComposite, SWT.PUSH);
        exportButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        exportButton.setText(Activator
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.export"));
        exportButton.setEnabled(false);

        removeButton.addSelectionListener(new SelectionListener() {

            /**
             * {@inheritDoc}
             */
            public void widgetSelected(SelectionEvent e) {
                ISelection sel = viewer.getSelection();
                if (sel instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection) sel;
                    Object object = selection.getFirstElement();
                    if (object instanceof LibraryRepository) {
                        int order = ((LibraryRepository) object).getOrder();
                        libraryRepositories.remove(object);
                        editButton.setEnabled(false);
                        removeButton.setEnabled(false);
                        upButton.setEnabled(false);
                        downButton.setEnabled(false);
                        exportButton.setEnabled(false);
                        for (LibraryRepository rep : libraryRepositories) {
                            if (rep.getOrder() > order) {
                                rep.setOrder(rep.getOrder() - 1);
                            }
                        }
                        detailsText.setText(CmnStringUtil.EMPTY);
                        viewer.refresh();
                    }
                }
            }

            /**
             * {@inheritDoc}
             */
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });
        upButton.addSelectionListener(new SelectionListener() {

            /**
             * {@inheritDoc}
             */
            public void widgetSelected(SelectionEvent e) {
                ISelection sel = viewer.getSelection();
                if (sel instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection) sel;
                    Object object = selection.getFirstElement();
                    if (object instanceof LibraryRepository) {
                        LibraryRepository selected = (LibraryRepository) object;
                        int selectedIndex = libraryRepositories
                                .indexOf(selected);
                        if (selectedIndex > 0) {
                            LibraryRepository temp = libraryRepositories
                                    .get(selectedIndex - 1);
                            libraryRepositories.set((selectedIndex - 1),
                                    selected);
                            libraryRepositories.set(selectedIndex, temp);
                            selected.setOrder(selectedIndex - 1);
                            temp.setOrder(selectedIndex);
                            viewer.refresh();
                            int newIndex = selectedIndex - 1;
                            downButton
                                    .setEnabled(newIndex < (libraryRepositories
                                            .size() - 1));
                            upButton.setEnabled(newIndex > 0);
                        }
                    }
                }
            }

            /**
             * {@inheritDoc}
             */
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        downButton.addSelectionListener(new SelectionListener() {

            /**
             * {@inheritDoc}
             */
            public void widgetSelected(SelectionEvent e) {
                ISelection sel = viewer.getSelection();
                if (sel instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection) sel;
                    Object object = selection.getFirstElement();
                    if (object instanceof LibraryRepository) {
                        LibraryRepository selected = (LibraryRepository) object;
                        int selectedIndex = libraryRepositories
                                .indexOf(selected);
                        if ((selectedIndex >= 0)
                                && (selectedIndex < libraryRepositories.size())) {
                            LibraryRepository temp = libraryRepositories
                                    .get(selectedIndex + 1);
                            libraryRepositories.set(selectedIndex, temp);
                            libraryRepositories.set((selectedIndex + 1),
                                    selected);
                            selected.setOrder(selectedIndex + 1);
                            temp.setOrder(selectedIndex);
                            viewer.refresh();
                            int newIndex = selectedIndex + 1;
                            downButton
                                    .setEnabled(newIndex < (libraryRepositories
                                            .size() - 1));
                            upButton.setEnabled(newIndex > 0);
                        }
                    }
                }
            }

            /**
             * {@inheritDoc}
             */
            public void widgetDefaultSelected(SelectionEvent e) {
            }
        });
        final Button importButton = new Button(buttonComposite, SWT.PUSH);
        importButton.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        importButton.setText(Activator
                .getResourceString(LibraryRepositoryPreferencePage.class
                        .getName() + ".label.import"));
        importButton.setEnabled(true);
        importButton.addSelectionListener(new SelectionListener() {

            /**
             * {@inheritDoc}
             */
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            /**
             * {@inheritDoc}
             */
            public void widgetSelected(SelectionEvent e) {
                EditLibraryRepositoryImportExportDialog dialog = new EditLibraryRepositoryImportExportDialog(
                        getShell(), null, true);
                int ok = dialog.open();
                if (ok == Window.OK) {
                    LibraryRepository repository = dialog.getRepository();

                    String targetZip = repository.getUrl();
                    String libraryCacheHomeDir = Activator
                            .getLibraryCacheHomeDir();

                    LibraryModel libraryModel = new LibraryModel();
                    libraryModel.setPath(targetZip);
                    WSearchLibraryCacheArgument argument = new WSearchLibraryCacheArgument(
                            CacheBase.TYPE.LIBRARY, libraryCacheHomeDir,
                            libraryModel);
                    try {
                        String libraryName = targetZip.substring(
                                (targetZip.lastIndexOf(File.separator) + 1),
                                targetZip.lastIndexOf("."));
                        String pomEntry = libraryName + File.separator
                                + "pom.xml";
                        if (!ZipUtil.existsEntry(targetZip, pomEntry)) {
                            ErrorDialog
                                    .openErrorDialog(
                                            getShell(),
                                            null,
                                            Activator.getResourceString(LibraryRepositoryPreferencePage.class
                                                    .getName()
                                                    + ".err.msg.FormatErr"));
                            return;
                        }
                        int index = -1;
                        for (LibraryRepository rep : libraryRepositories) {
                            if (rep.getName().equals(libraryName)) {
                                int ret = new Dialog(getShell()) {
                                    protected Control createDialogArea(
                                            Composite parent) {
                                        Composite composite = (Composite) super
                                                .createDialogArea(parent);
                                        Label label = new Label(composite,
                                                SWT.SHADOW_NONE);
                                        label.setText(Activator.getResourceString(LibraryRepositoryPreferencePage.class
                                                .getName()
                                                + ".label.ImportOverwrite"));
                                        return composite;
                                    }
                                }.open();
                                if (ret == IDialogConstants.CANCEL_ID) {
                                    return;
                                }
                                rep.setUrl(repository.getUrl());
                                repository = rep;
                                index = libraryRepositories.indexOf(rep);
                                break;
                            }
                        }
                        File cacheDir = new File(Activator
                                .getLibraryCacheHomeDir(), libraryName);
                        if (cacheDir.exists()) {
                            try {
                                FileVisitor.walkFileTree(cacheDir,
                                        new FileVisitor() {
                                            @Override
                                            public FileVisitResult visitFile(
                                                    File file)
                                                    throws IOException {
                                                file.delete();
                                                return FileVisitResult.CONTINUE;
                                            }

                                            @Override
                                            public FileVisitResult postVisitDirectory(
                                                    File dir)
                                                    throws IOException {
                                                dir.delete();
                                                return FileVisitResult.CONTINUE;
                                            }
                                        });
                            } catch (IOException e1) {
                                Activator.log(e1);
                                ErrorDialog
                                        .openErrorDialog(
                                                getShell(),
                                                null,
                                                Activator.getResourceString(LibraryRepositoryPreferencePage.class
                                                        .getName()
                                                        + ".err.msg.DeleteCacheErr"));
                                return;
                            }
                        }
                        Activator.extractCacheDir(new File(targetZip),
                                argument, false);
                        String pomPath = libraryCacheHomeDir + File.separator
                                + libraryName + File.separator + "pom.xml";
                        PomReader reader = new PomReader();
                        reader.loadPomFile(pomPath);
                        repository.setName(libraryName);
                        repository.setCache(true);
                        repository.setEnabled(true);
                        repository.setMavenType(reader.isMavenType());
                        repository.setPluginEmbedded(reader.isEmbeddedCache());
                        repository.setZipCache(true);
                        repository.setOrder(libraryRepositories.size() + 1);
                        if (index >= 0) {
                            libraryRepositories.set(index, repository);
                        } else {
                            libraryRepositories.add(repository);
                        }
                    } catch (IOException ex) {
                        String message = Activator
                                .getResourceString(LibraryRepositoryPreferencePage.class
                                        .getName() + ".err.msg.UnzipErr");
                        Activator.log(ex, message);
                        ErrorDialog.openErrorDialog(getShell(), ex, message);
                        return;
                    } catch (XmlPullParserException ex) {
                        String message = Activator
                                .getResourceString(LibraryRepositoryPreferencePage.class
                                        .getName() + ".err.msg.PomReadErr");
                        Activator.log(ex, message);
                        ErrorDialog.openErrorDialog(getShell(), ex, message);
                        return;
                    }
                    viewer.refresh();
                    tableViewer.setChecked(repository, repository.isEnabled());
                }
                viewer.refresh();
            }

        });

        exportButton.addSelectionListener(new SelectionListener() {

            /**
             * {@inheritDoc}
             */
            public void widgetDefaultSelected(SelectionEvent e) {
            }

            /**
             * {@inheritDoc}
             */
            public void widgetSelected(SelectionEvent e) {
                EditLibraryRepositoryImportExportDialog dialog = new EditLibraryRepositoryImportExportDialog(
                        getShell(), selectionLibrary, false);
                int ok = dialog.open();
                if (ok == Window.OK) {
                    LibraryRepository repository = dialog.getRepository();

                    String targetZip = repository.getUrl();

                    String base = Activator.getLibraryCacheHomeDir()
                            + File.separator + repository.getName();
                    File baseFile = new File(base);
                    try {
                        ZipUtil.zip(baseFile, new File(targetZip));
                    } catch (IOException ex) {
                        String message = Activator
                                .getResourceString(LibraryRepositoryPreferencePage.class
                                        .getName() + ".err.msg.ZipErr");
                        Activator.log(ex, message);
                        ErrorDialog.openErrorDialog(getShell(), ex, message);
                        return;
                    }
                }
            }
        });

        viewer.addSelectionChangedListener(new ISelectionChangedListener() {

            /**
             * {@inheritDoc}
             */
            public void selectionChanged(SelectionChangedEvent event) {
                ISelection sel = viewer.getSelection();
                if (sel instanceof IStructuredSelection) {
                    IStructuredSelection selection = (IStructuredSelection) sel;
                    Object object = selection.getFirstElement();
                    if (object instanceof LibraryRepository) {
                        selectionLibrary = (LibraryRepository) object;
                        if (selectionLibrary.isPluginEmbedded()
                                || selectionLibrary.hasCache()) {
                            editButton.setEnabled(false);
                            exportButton.setEnabled(true);
                        } else {
                            editButton.setEnabled(true);
                            exportButton.setEnabled(false);
                        }
                        removeButton.setEnabled(true);
                        upButton.setEnabled(false);
                        downButton.setEnabled(false);
                        if (libraryRepositories.size() > 1) {
                            Iterator<LibraryRepository> iterator = libraryRepositories
                                    .iterator();
                            LibraryRepository first = null;
                            if (iterator.hasNext()) {
                                first = iterator.next();
                            }
                            LibraryRepository last = null;
                            while (iterator.hasNext()) {
                                last = iterator.next();
                            }
                            if (selectionLibrary.equals(last)) {
                                upButton.setEnabled(true);
                            } else if (selectionLibrary.equals(first)) {
                                downButton.setEnabled(true);
                            } else {
                                upButton.setEnabled(true);
                                downButton.setEnabled(true);
                            }
                        }
                        updateDetailsText(selectionLibrary);
                    }
                } else {
                    editButton.setEnabled(false);
                    removeButton.setEnabled(false);
                    upButton.setEnabled(false);
                    downButton.setEnabled(false);
                }
            }
        });
    }

    /**
     * It is a content provider class of library repository.<br/>
     */
    private static class LibraryRepositoryContentProvider implements
            IStructuredContentProvider {

        /**
         * List of repository
         */
        private List<LibraryRepository> repositories;

        /**
         * Constructor.<br/>
         * Nothing in particular.<br/>
         * 
         * @param repositories
         *            List of repository
         */
        public LibraryRepositoryContentProvider(
                List<LibraryRepository> repositories) {
            this.repositories = repositories;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object[] getElements(Object inputElement) {
            return repositories.toArray();
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
            repositories = (List<LibraryRepository>) newInput;
        }
    }

    /**
     * The label provider classes in the library repository.<br/>
     */
    static class LibraryRepositoryLabelProvider extends LabelProvider implements
            ITableLabelProvider, IColorProvider {

        /**
         * Constructor.<br/>
         * Nothing in particular.<br/>
         */
        public LibraryRepositoryLabelProvider() {
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
            if (element instanceof LibraryRepository) {
                LibraryRepository nr = (LibraryRepository) element;
                if (columnIndex == 0) {
                    return nr.getName();
                }
                if (columnIndex == 1) {
                    if (nr.isPluginEmbedded()) {
                        return Activator
                                .getResourceString(LibraryRepositoryPreferencePage.class
                                        .getName() + ".label.EmbeddedCache");
                    } else if (nr.isZipCache()) {
                        return Activator
                                .getResourceString(LibraryRepositoryPreferencePage.class
                                        .getName() + ".label.ZipCache");
                    }
                    return nr.getUrl();
                }
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Color getForeground(Object element) {
            LibraryRepository libraryRepository = (LibraryRepository) element;
            if (libraryRepository.hasCache()) {
                return Display.getCurrent().getSystemColor(SWT.COLOR_DARK_GRAY);
            }
            return null;
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Color getBackground(Object element) {
            return null;
        }
    }
}