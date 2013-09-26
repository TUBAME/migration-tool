/*
 * EditLibraryRepositoryDialog.java
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
package org.tubame.wsearch.ui.dialog;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.DirectoryDialog;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.tubame.wsearch.Activator;
import org.tubame.wsearch.models.LibraryRepository;
import org.tubame.wsearch.util.resource.ResourceUtil;

/**
 * It is a library repository dialog class.<br/>
 */
public class EditLibraryRepositoryDialog extends Dialog {

    /**
     * Library repository
     */
    private LibraryRepository libraryRepository;
    /**
     * Title
     */
    private String title;
    /**
     * Whether ZIP file
     */
    private boolean zipType;

    /**
     * Button to specify that it is a ZIP file
     */
    private Button buttonZip;
    /**
     * Button to specify that it is a directory.
     */
    private Button buttonDirectory;

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param parentShell
     *            Parent shell
     * @param repository
     *            Library repository
     * @param zipType
     *            Whether ZIP file
     */
    public EditLibraryRepositoryDialog(Shell parentShell,
            LibraryRepository repository, boolean zipType) {
        super(parentShell);

        setShellStyle(SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.RESIZE
                | getDefaultOrientation());
        this.libraryRepository = repository;
        this.zipType = zipType;
        if (this.libraryRepository == null) {
            this.libraryRepository = new LibraryRepository("", "", "", true,
                    false, false);
            this.title = Activator
                    .getResourceString(EditLibraryRepositoryDialog.class
                            .getName() + ".title.new");
        } else {
            this.title = Activator
                    .getResourceString(EditLibraryRepositoryDialog.class
                            .getName() + ".title.edit");
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        getShell().setText(title);
        Composite area = (Composite) super.createDialogArea(parent);
        Composite contents = new Composite(area, SWT.NONE);
        GridData gd = new GridData(GridData.FILL_BOTH);
        gd.widthHint = 400;
        contents.setLayoutData(gd);
        contents.setLayout(new GridLayout(3, false));
        applyDialogFont(contents);
        initializeDialogUnits(area);
        Label nameLabel = new Label(contents, SWT.NONE);
        nameLabel.setText(Activator
                .getResourceString(EditLibraryRepositoryDialog.class.getName()
                        + ".label.name"));
        final Text nameText = new Text(contents, SWT.BORDER);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        nameText.setLayoutData(gd);
        nameText.setText(libraryRepository.getName());
        nameText.addModifyListener(new ModifyListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void modifyText(ModifyEvent e) {
                libraryRepository.setName(nameText.getText());
            }
        });

        Label zipLable = new Label(contents, SWT.NONE);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        zipLable.setText(Activator
                .getResourceString(EditLibraryRepositoryDialog.class.getName()
                        + ".label.zip"));
        Composite zipCheck = new Composite(contents, SWT.NONE);
        gd = new GridData(GridData.FILL_BOTH);
        gd.horizontalSpan = 2;
        zipCheck.setLayoutData(gd);
        zipCheck.setLayout(new GridLayout(2, false));

        buttonZip = new Button(zipCheck, SWT.RADIO);
        buttonZip.setSelection(zipType);
        buttonZip.setText(Activator
                .getResourceString(EditLibraryRepositoryDialog.class.getName()
                        + ".label.ziptype"));
        buttonZip.setLocation(0, 0);
        buttonZip.pack();
        buttonDirectory = new Button(zipCheck, SWT.RADIO);
        buttonDirectory.setSelection(!zipType);
        buttonDirectory.setText(Activator
                .getResourceString(EditLibraryRepositoryDialog.class.getName()
                        + ".label.directorytype"));
        buttonDirectory.setLocation(300, 0);
        buttonDirectory.pack();

        Label urlLabel = new Label(contents, SWT.NONE);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        urlLabel.setText(Activator
                .getResourceString(EditLibraryRepositoryDialog.class.getName()
                        + ".label.url"));
        final Text urlText = new Text(contents, SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        urlText.setLayoutData(gd);
        urlText.setText(libraryRepository.getUrl());

        urlText.addModifyListener(new ModifyListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void modifyText(ModifyEvent e) {
                libraryRepository.setUrl(urlText.getText());
            }
        });

        final Button outputDirectoryBrowse = new Button(contents, SWT.PUSH);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        outputDirectoryBrowse.setText(Activator
                .getResourceString("browse.button.label"));
        outputDirectoryBrowse.addSelectionListener(new SelectionAdapter() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                zipType = buttonZip.getSelection();
                if (zipType) {
                    FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
                    String value = urlText.getText();
                    if (value.trim().length() == 0) {
                        value = Platform.getLocation().toOSString();
                    }
                    dialog.setFilterExtensions(new String[] { ResourceUtil.EXTENSION_ZIP });
                    dialog.setFilterPath(value);
                    String result = dialog.open();
                    if (result == null || result.trim().length() == 0) {
                        return;
                    }
                    urlText.setText(result);
                } else {
                    DirectoryDialog dialog = new DirectoryDialog(getShell(),
                            SWT.SINGLE);
                    String value = urlText.getText();
                    if (value.trim().length() == 0) {
                        value = Platform.getLocation().toOSString();
                    }
                    dialog.setFilterPath(value);

                    String result = dialog.open();
                    if (result == null || result.trim().length() == 0) {
                        return;
                    }
                    urlText.setText(result);
                }
            }
        });

        Label typeLabel = new Label(contents, SWT.NONE);
        typeLabel.setText(Activator
                .getResourceString(EditLibraryRepositoryDialog.class.getName()
                        + ".label.type"));

        final Button enabledButton = new Button(contents, SWT.CHECK);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;

        enabledButton.setLayoutData(gd);
        enabledButton.setText(Activator
                .getResourceString(EditLibraryRepositoryDialog.class.getName()
                        + ".label.maven"));
        enabledButton.setSelection(libraryRepository.isMavenType());
        enabledButton.addSelectionListener(new SelectionListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                libraryRepository.setMavenType(enabledButton.getSelection());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetDefaultSelected(SelectionEvent e) {

            }
        });

        Label descriptionLabel = new Label(contents, SWT.NONE);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 3;
        descriptionLabel.setLayoutData(gd);
        descriptionLabel.setText(Activator
                .getResourceString(EditLibraryRepositoryDialog.class.getName()
                        + ".label.description"));
        final Text descriptionText = new Text(contents, SWT.BORDER);
        gd = new GridData(GridData.FILL_HORIZONTAL);
        gd.horizontalSpan = 3;
        descriptionText.setLayoutData(gd);
        if (libraryRepository.getDescription() != null) {
            descriptionText.setText(libraryRepository.getDescription());
        }
        descriptionText.addModifyListener(new ModifyListener() {

            /**
             * {@inheritDoc}
             */
            @Override
            public void modifyText(ModifyEvent e) {
                libraryRepository.setDescription(descriptionText.getText());
            }
        });
        return area;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void okPressed() {
        if (libraryRepository.getName() == null
                || libraryRepository.getName().equals("")) {
            MessageDialog.openError(getShell(), Activator
                    .getResourceString(EditLibraryRepositoryDialog.class
                            .getName() + ".title.error"), Activator
                    .getResourceString(EditLibraryRepositoryDialog.class
                            .getName() + ".err.msg.NoName"));
            return;
        }

        if (libraryRepository.getUrl() == null
                || libraryRepository.getUrl().equals("")) {
            MessageDialog.openError(getShell(), Activator
                    .getResourceString(EditLibraryRepositoryDialog.class
                            .getName() + ".title.error"), Activator
                    .getResourceString(EditLibraryRepositoryDialog.class
                            .getName() + ".err.msg.NoPath"));
            return;
        }
        if (!new File(libraryRepository.getUrl()).exists()) {
            MessageDialog.openError(getShell(), Activator
                    .getResourceString(EditLibraryRepositoryDialog.class
                            .getName() + ".title.PathError"), Activator
                    .getResourceString(EditLibraryRepositoryDialog.class
                            .getName() + ".err.msg.IgnorePath"));
            return;
        }
        super.okPressed();
    }

    /**
     * Get the library repository.<br/>
     * 
     * @return Library repository
     */
    public LibraryRepository getRepository() {
        return libraryRepository;
    }
}