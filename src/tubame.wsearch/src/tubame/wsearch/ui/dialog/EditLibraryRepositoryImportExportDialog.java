/*
 * EditLibraryRepositoryImportExportDialog.java
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
package tubame.wsearch.ui.dialog;

import java.io.File;

import org.eclipse.core.runtime.Platform;
import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
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
import tubame.wsearch.Activator;
import tubame.wsearch.models.LibraryRepository;
import tubame.wsearch.util.resource.ResourceUtil;

/**
 * It is import export dialog class library repository.<br/>
 */
public class EditLibraryRepositoryImportExportDialog extends Dialog {

    /**
     * Library repository
     */
    private LibraryRepository libraryRepository;
    /**
     * Title
     */
    private String title;
    /**
     * Import type
     */
    private boolean importType;

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param parentShell
     *            Parent shell
     * @param repository
     *            Library repository
     * @param importType
     *            Import type
     */
    public EditLibraryRepositoryImportExportDialog(Shell parentShell,
            LibraryRepository repository, boolean importType) {
        super(parentShell);

        setShellStyle(SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.RESIZE
                | getDefaultOrientation());
        this.libraryRepository = repository;
        this.importType = importType;
        if (this.importType) {
            this.title = Activator
                    .getResourceString(EditLibraryRepositoryImportExportDialog.class
                            .getName() + ".title.import");
        } else {
            this.title = Activator
                    .getResourceString(EditLibraryRepositoryImportExportDialog.class
                            .getName() + ".title.export");
        }
        if (this.libraryRepository == null) {
            this.libraryRepository = new LibraryRepository("", "", "", false,
                    false, false);
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

        Label urlLabel = new Label(contents, SWT.NONE);
        urlLabel.setText(Activator
                .getResourceString(EditLibraryRepositoryImportExportDialog.class
                        .getName() + ".label.url"));
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

        if (importType) {
            final Button outputDirectoryBrowse = new Button(contents, SWT.PUSH);
            outputDirectoryBrowse.setText(Activator
                    .getResourceString("browse.button.label"));
            outputDirectoryBrowse.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
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
                }
            });
        } else {
            final Button outputDirectoryBrowse = new Button(contents, SWT.PUSH);
            outputDirectoryBrowse.setText(Activator
                    .getResourceString("browse.button.label"));
            outputDirectoryBrowse.addSelectionListener(new SelectionAdapter() {

                /**
                 * {@inheritDoc}
                 */
                @Override
                public void widgetSelected(SelectionEvent e) {
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
            });
        }
        return area;
    }

    @Override
    protected void okPressed() {

        if (libraryRepository.getUrl().equals("")) {
            MessageDialog
                    .openError(
                            getShell(),
                            Activator
                                    .getResourceString(EditLibraryRepositoryImportExportDialog.class
                                            .getName() + ".title.error"),
                            Activator.getResourceString(this.getClass()
                                    .getName() + ".err.msg.NoPath"));
            return;
        }

        // Validation path is not a fraud
        if (libraryRepository.getUrl() == null) {
            MessageDialog
                    .openError(
                            getShell(),
                            Activator
                                    .getResourceString(EditLibraryRepositoryImportExportDialog.class
                                            .getName() + ".title.error"),
                            Activator.getResourceString(this.getClass()
                                    .getName() + ".err.msg.PathValidationErr"));
            return;
        }
        if (!new File(libraryRepository.getUrl()).exists()) {
            MessageDialog
                    .openError(
                            getShell(),
                            Activator
                                    .getResourceString(EditLibraryRepositoryImportExportDialog.class
                                            .getName() + ".title.error"),
                            Activator.getResourceString(this.getClass()
                                    .getName() + ".err.msg.UrlValidationErr"));
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