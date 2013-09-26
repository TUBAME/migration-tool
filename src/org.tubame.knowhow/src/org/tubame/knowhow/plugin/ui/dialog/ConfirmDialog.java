/*
 * ConfirmDialog.java
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
package org.tubame.knowhow.plugin.ui.dialog;

import org.eclipse.jface.dialogs.MessageDialog;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Confirmation screen to be displayed when the warning or confirmation is
 * present.<br/>
 * Inherits the MessageDialog class, and define the confirmation screen.<br/>
 */
public class ConfirmDialog extends MessageDialog {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConfirmDialog.class);
    /** Yes */
    public static final int DIALOG_YES = 0;
    /** No */
    public static final int DIALOG_NO = 1;
    /** Cancellation */
    public static final int DIALOG_CANCEL = 2;

    /**
     * Confirmation dialog for constructor.<br/>
     * Confirmation dialog with "Yes" "No" "Cancel" button.<br/>
     * 
     * @param parentShell
     *            Shell to be displayed screen
     * @param dialogTitle
     *            Title of the dialog
     * @param dialogMessage
     *            Messages that are displayed in the dialog
     * @param dialogType
     *            Type of dialog
     */
    public ConfirmDialog(Shell parentShell, String dialogTitle,
            String dialogMessage, int dialogType) {

        super(parentShell, dialogTitle, null, dialogMessage, dialogType, null,
                1);

        LOGGER.debug("shell:" + parentShell + "message" + dialogMessage);
        if (dialogType == MessageDialog.QUESTION_WITH_CANCEL) {
            super.setButtonLabels(new String[] { "Yes", "No", "Cancel" });
        } else if (dialogType == MessageDialog.INFORMATION) {
            super.setButtonLabels(new String[] { "Yes" });
        } else {
            super.setButtonLabels(new String[] { "Yes", "No" });
        }
    }
}
