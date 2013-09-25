/*
 * ExistingKnowhowSelectionListener.java
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
package org.tubame.knowhow.plugin.ui.wizard.listener;

import org.tubame.common.util.CmnStringUtil;

import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.util.PluginUtil;

/**
 * Browse button listener.<br/>
 * Listener class that displays the know-how XML selection dialog.<br/>
 */
public class ExistingKnowhowSelectionListener implements SelectionListener {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ExistingKnowhowSelectionListener.class);
    /** Member variable of type Text */
    private Text targetText;
    /** Member variable of type WizardPage */
    private WizardPage wizardPage;

    /**
     * Constructor.<br/>
     * 
     * @param targetText
     *            Combo
     * @param wizardPage
     *            WizardPage
     */
    public ExistingKnowhowSelectionListener(Text targetText,
            WizardPage wizardPage) {
        this.targetText = targetText;
        this.wizardPage = wizardPage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        ExistingKnowhowSelectionListener.LOGGER
                .info(MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.LOG_OPEN_SELECT_FOLDER_DIALOG));

        FileDialog openDialog = new FileDialog(
                PluginUtil.getActiveWorkbenchShell(), SWT.OPEN);
        // Open a file selection dialog
        String openFile = openFileSelect(openDialog);
        // If the file has been selected
        if (!CmnStringUtil.isEmpty(openFile)) {
            targetText.setText(openFile);
            wizardPage.setErrorMessage(null);
        }
    }

    /**
     * Get a file path that is selected from the dialog.<br/>
     * 
     * @param openDialog
     *            FileDialog
     * @return File Path
     */
    private String openFileSelect(FileDialog openDialog) {
        openDialog.setFilterExtensions(new String[] { ApplicationPropertiesUtil
                .getProperty(ApplicationPropertiesUtil.XML_EXTENSION) });
        return openDialog.open();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // no oparate
    }
}
