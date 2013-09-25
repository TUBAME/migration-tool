/*
 * EditSearchPackageDialog.java
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

import java.util.regex.Pattern;
import java.util.regex.PatternSyntaxException;


import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.swt.widgets.Text;
import org.tubame.wsearch.Activator;
import org.tubame.wsearch.biz.model.WSPackage;

/**
 * It is excluded editing dialog class.<br/>
 */
public class EditSearchPackageDialog extends Dialog {

    /**
     * Exclusions
     */
    private WSPackage wsPackage;

    /**
     * Title
     */
    private String title;

    /**
     * Constructor.<br/>
     * 
     * @param parentShell
     *            Parent shell
     * @param wsPackage
     *            Exclusions
     * @param type
     *            Type
     */
    public EditSearchPackageDialog(Shell parentShell, WSPackage wsPackage,
            String type) {
        super(parentShell);
        setShellStyle(SWT.CLOSE | SWT.MAX | SWT.TITLE | SWT.BORDER | SWT.RESIZE
                | getDefaultOrientation());
        this.wsPackage = wsPackage;
        if (this.wsPackage == null) {
            this.wsPackage = new WSPackage(type, "", false);
            this.title = Activator
                    .getResourceString(EditSearchPackageDialog.class.getName()
                            + ".title.new");
        } else {
            this.title = Activator
                    .getResourceString(EditSearchPackageDialog.class.getName()
                            + ".title.edit");
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
        contents.setLayoutData(gd);
        contents.setLayout(new GridLayout(3, false));
        applyDialogFont(contents);
        initializeDialogUnits(area);
        Label nameLabel = new Label(contents, SWT.NONE);
        nameLabel.setText(Activator
                .getResourceString(EditSearchPackageDialog.class.getName()
                        + ".label.package"));
        final Text nameText = new Text(contents, SWT.BORDER);
        gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        gd.horizontalSpan = 2;
        nameText.setLayoutData(gd);
        nameText.setText(wsPackage.getName());
        nameText.addModifyListener(new ModifyListener() {

            @Override
            public void modifyText(ModifyEvent e) {
                wsPackage.setName(nameText.getText());
            }
        });
        return area;
    }

    /**
     * Get excluded.<br/>
     * 
     * @return Exclusions
     */
    public WSPackage getWsPackage() {
        return wsPackage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void okPressed() {
        try {
            Pattern.compile(wsPackage.getName());
        } catch (PatternSyntaxException e) {
            ErrorDialog.openErrorDialog(
                    getShell(),
                    null,
                    Activator.getResourceString(EditSearchPackageDialog.class
                            .getName() + ".err.msg.PatternCompileErr"));
            return;
        }
        super.okPressed();
    }
}