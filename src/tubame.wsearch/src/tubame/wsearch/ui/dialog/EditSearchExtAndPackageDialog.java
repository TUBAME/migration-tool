/*
 * EditSearchExtAndPackageDialog.java
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

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import tubame.wsearch.Activator;
import tubame.wsearch.biz.model.Extension;
import tubame.wsearch.biz.model.WSPackage;

/**
 * Extension, is excluded editing dialog class.<br/>
 */
public class EditSearchExtAndPackageDialog extends Dialog {

    /**
     * Get the input text.<br/>
     * 
     * @return Input text
     */
    public String getTextValue() {
        return textValue;
    }

    /**
     * Input text
     */
    private String textValue;

    /**
     * Title
     */
    private String title;

    /**
     * Edited
     */
    private Object objectValue;

    /**
     * Radio button array
     */
    private Button[] radioButtons;

    /**
     * Name
     */
    private Text nameText;

    /**
     * Type
     */
    private String type;

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param parentShell
     *            Parent shell
     * @param objectValue
     *            Edited
     * @param type
     *            Type
     */
    public EditSearchExtAndPackageDialog(Shell parentShell, Object objectValue,
            String type) {
        super(parentShell);
        setShellStyle(SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.RESIZE
                | getDefaultOrientation());
        this.objectValue = objectValue;
        if (this.objectValue == null) {
            this.title = Activator
                    .getResourceString(EditSearchExtAndPackageDialog.class
                            .getName() + ".title.new");
        } else {
            this.title = Activator
                    .getResourceString(EditSearchExtAndPackageDialog.class
                            .getName() + ".title.edit");
        }
        this.type = type;
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
        contents.setLayoutData(gd);
        contents.setLayout(new GridLayout(2, false));
        applyDialogFont(contents);
        initializeDialogUnits(area);

        if (this.objectValue != null) {
            createEditArea(contents);
        } else {

            createAddArea(contents);
        }
        nameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                textValue = nameText.getText();
            }
        });
        return area;
    }

    /**
     * Make the edit area.<br/>
     * 
     * @param contents
     *            Content
     */
    private void createEditArea(Composite contents) {
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 1;
        Label nameLabel = new Label(contents, SWT.NONE);
        nameText = new Text(contents, SWT.BORDER);
        if (this.objectValue instanceof Extension) {
            Extension extension = (Extension) this.objectValue;
            nameLabel.setText(Activator
                    .getResourceString(EditSearchExtAndPackageDialog.class
                            .getName() + ".label.extension"));
            nameText.setText(extension.getName());
        } else {
            WSPackage wsPackage = (WSPackage) this.objectValue;
            nameLabel.setText(Activator
                    .getResourceString(EditSearchExtAndPackageDialog.class
                            .getName() + ".label.package"));
            nameText.setText(wsPackage.getName());
        }
        nameText.setLayoutData(gd);
    }

    /**
     * Create an additional area.<br/>
     * 
     * @param contents
     *            Content
     */
    private void createAddArea(Composite contents) {
        radioButtons = new Button[2];
        radioButtons[0] = new Button(contents, SWT.RADIO);
        radioButtons[0].setSelection(true);
        radioButtons[0].setText(Activator
                .getResourceString(EditSearchExtAndPackageDialog.class
                        .getName() + ".label.extension"));
        radioButtons[0].setLocation(50, 250);
        radioButtons[0].pack();

        radioButtons[1] = new Button(contents, SWT.RADIO);
        radioButtons[1].setText(Activator
                .getResourceString(EditSearchExtAndPackageDialog.class
                        .getName() + ".label.package"));
        radioButtons[1].setLocation(120, 250);
        radioButtons[1].pack();

        nameText = new Text(contents, SWT.BORDER);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        nameText.setLayoutData(gd);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void okPressed() {
        if (radioButtons != null) {
            if (radioButtons[0].getSelection()) {
                this.objectValue = new Extension(textValue, true, true);
            } else {
                this.objectValue = new WSPackage(type, textValue, true);
            }
        }
        if (this.objectValue instanceof WSPackage) {
            try {
                Pattern.compile(nameText.getText());
            } catch (PatternSyntaxException e) {
                ErrorDialog.openErrorDialog(getShell(), null, Activator
                        .getResourceString(EditSearchExtAndPackageDialog.class
                                .getName() + ".err.msg.PatternCompileErr"));
                return;
            }
        }
        super.okPressed();
    }

    /**
     * Get edited.<br/>
     * 
     * @return Edited
     */
    public Object getValue() {
        return this.objectValue;
    }
}