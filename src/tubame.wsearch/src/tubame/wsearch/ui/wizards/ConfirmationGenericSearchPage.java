/*
 * ConfirmationGenericSearchPage.java
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
package tubame.wsearch.ui.wizards;

import java.util.ArrayList;
import java.util.Collections;
import java.util.Comparator;
import java.util.List;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.WorkbenchException;
import org.eclipse.ui.forms.events.ExpansionAdapter;
import org.eclipse.ui.forms.events.ExpansionEvent;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import tubame.wsearch.Activator;
import tubame.wsearch.models.LibraryRepository;
import tubame.wsearch.ui.dialog.ErrorDialog;
import tubame.wsearch.util.PluginUtil;

/**
 * It is a general-purpose search confirmation page class.<br/>
 */
public class ConfirmationGenericSearchPage extends WizardPage {
    /**
     * Toolkit
     */
    private final FormToolkit toolkit;
    /**
     * Composite extension
     */
    private ExpandableComposite advancedComposite;
    /**
     * Output location
     */
    private Text outputPlace;

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param title
     *            Title
     */
    public ConfirmationGenericSearchPage(String title) {
        super(title, title, null);
        toolkit = new FormToolkit(Display.getCurrent());
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(Composite parent) {
        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        setOutputGenericResultArea(composite);
        Composite section = createAdvancedSection(composite);

        try {
            setPortabilityInfoArea(section);
        } catch (WorkbenchException e) {
            String message = Activator
                    .getResourceString(ConfirmationGenericSearchPage.class
                            .getName() + ".err.msg.OpenErr");
            Activator.log(e, message);
            ErrorDialog.openErrorDialog(getShell(), e, message);
            return;
        }
        super.setControl(composite);
    }

    /**
     * Set the output area results.<br/>
     * 
     * @param composite
     *            Composite
     */
    private void setOutputGenericResultArea(Composite composite) {
        Group group = new Group(composite, SWT.SHADOW_NONE);
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setLayout(new GridLayout(3, false));
        group.setText(Activator
                .getResourceString(ConfirmationGenericSearchPage.class
                        .getName() + ".label.TargetText"));
        Label label = new Label(group, NONE);
        label.setText(Activator
                .getResourceString(ConfirmationGenericSearchPage.class
                        .getName() + ".label.target"));
        GridData gridData = new GridData(GridData.FILL_HORIZONTAL);
        this.outputPlace = new Text(group, SWT.BORDER);
        this.outputPlace.setLayoutData(gridData);
        this.outputPlace.setText(PluginUtil.getSelectedProject().getName());

        Button outputFolder = new Button(group, SWT.PUSH);
        outputFolder
                .setText(Activator.getResourceString("browse.button.label"));
        outputFolder
                .addSelectionListener(new BrowseDirectoryButtonSelectionListener(
                        outputPlace, this));
    }

    /**
     * Get the output result.<br/>
     * 
     * @return Result output destination
     */
    public Text getOutputPlace() {
        return outputPlace;
    }

    /**
     * Set the portability information area.<br/>
     * 
     * @param composite
     *            Composite
     * @throws WorkbenchException
     *             if the configuration fails, It is thrown
     */
    private void setPortabilityInfoArea(Composite composite)
            throws WorkbenchException {
        Group portabilityInfoGroup = new Group(composite, SWT.SHADOW_NONE);
        portabilityInfoGroup.setLayoutData(new GridData(
                GridData.FILL_HORIZONTAL));
        portabilityInfoGroup.setLayout(new GridLayout(4, false));
        portabilityInfoGroup.setText(Activator
                .getResourceString(ConfirmationGenericSearchPage.class
                        .getName() + ".label.advanced"));
        StringBuffer destLibraryiesName = new StringBuffer();

        List<LibraryRepository> libraryRepositories = new ArrayList<LibraryRepository>(
                Activator.getLibraryRepositories().size());
        libraryRepositories.addAll(Activator.getLibraryRepositories().values());
        Collections.sort(libraryRepositories,
                new Comparator<LibraryRepository>() {
                    @Override
                    public int compare(LibraryRepository o1,
                            LibraryRepository o2) {
                        return o1.getOrder() - o2.getOrder();
                    }
                });
        for (LibraryRepository libraryRepository : libraryRepositories) {
            if (libraryRepository.isEnabled()) {
                destLibraryiesName.append(libraryRepository.getName() + "\n");
            } 
        }

        Label label2 = new Label(portabilityInfoGroup, NONE);
        label2.setText(Activator
                .getResourceString(ConfirmationGenericSearchPage.class
                        .getName() + ".label.DestLibrary"));

        Text destinationPortability = new Text(portabilityInfoGroup, SWT.MULTI
                | SWT.BORDER | SWT.V_SCROLL | SWT.WRAP | SWT.READ_ONLY);

        GridData gridData2 = new GridData(GridData.FILL_HORIZONTAL);
        gridData2.horizontalAlignment = GridData.FILL;
        gridData2.verticalAlignment = GridData.CENTER;
        gridData2.heightHint = destinationPortability.getLineHeight() * 3;

        destinationPortability.setLayoutData(gridData2);
        destinationPortability.setText(destLibraryiesName.toString());
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
                ExpandableComposite.COMPACT | ExpandableComposite.TITLE_BAR);
        advancedComposite.setFont(container.getFont());
        advancedComposite.setBackground(container.getBackground());
        advancedComposite.setText(Activator
                .getResourceString(ConfirmationGenericSearchPage.class
                        .getName() + ".label.LibrarySetting"));
        advancedComposite.setLayout(new GridLayout(3, true));
        advancedComposite.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        advancedComposite.setExpanded(true);
        advancedComposite.addExpansionListener(new ExpansionAdapter() {
            @Override
            public void expansionStateChanged(ExpansionEvent e) {
                getControl().getShell().pack(true);
            }
        });

        Composite advanced = new Composite(advancedComposite, SWT.NONE);
        advanced.setLayout(new GridLayout(1, false));
        advancedComposite.setClient(advanced);
        return advanced;
    }
}