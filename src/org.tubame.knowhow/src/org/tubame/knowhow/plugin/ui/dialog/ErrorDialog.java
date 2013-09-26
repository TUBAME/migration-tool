/*
 * ErrorDialog.java
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

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.Activator;

/**
 * Error screen to display if an error occurs.<br/>
 * Implement an error screen to be displayed in the system error occurs inherit
 * MessageDialog.<br/>
 * Make the opening and closing of the error log display unit by which the user
 * presses the "More Information" button.<br/>
 */
public final class ErrorDialog {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ErrorDialog.class);

    /**
     * Constructor.<br/>
     * 
     */
    private ErrorDialog() {
        // no operation
    }

    /**
     * It displays an error dialog.<br/>
     * View (only if Exception is passed) and trace error message.<br/>
     * 
     * @param shell
     *            Shell
     * @param e
     *            Exception
     * @param messasge
     *            Error message
     */
    public static void openErrorDialog(Shell shell, Exception e, String messasge) {

        LOGGER.debug("shell:" + shell + "exception" + e + "message" + messasge);
        ErrorDialog.open(shell, e, messasge, IStatus.ERROR);
    }

    /**
     * It displays an error dialog with the specified icon.<br/>
     * 
     * @param shell
     *            Shell
     * @param e
     *            Exception
     * @param messasge
     *            Error message
     * @param type
     *            Status
     */
    private static void open(Shell shell, Exception e, String messasge, int type) {
        IStatus status = null;

        if (e == null) {
            status = new Status(type, Activator.PLUGIN_ID, messasge);
        } else {
            status = new Status(type, Activator.PLUGIN_ID, type, messasge,
                    e.getCause());
        }

        org.eclipse.jface.dialogs.ErrorDialog.openError(shell, null, null,
                status);
    }

    /**
     * It displays an error dialog warning icon.<br/>
     * View (only if Exception is passed) and trace error message.<br/>
     * 
     * @param shell
     *            Shell
     * @param title
     *            Title
     * @param description
     *            The top message
     * @param reason
     *            Error message
     * @param errorList
     *            List
     */
    public static void openCautionDialog(Shell shell, String title,
            String description, String reason, List<String> errorList) {

        LOGGER.debug("shell:" + shell + "messasge:" + reason + "errorList:"
                + errorList);
        org.eclipse.jface.dialogs.ErrorDialog.openError(shell, title,
                description, ErrorDialog.createMultiStatus(IStatus.WARNING,
                        reason, errorList));
    }

    /**
     * It displays an error dialog the error icon.<br/>
     * View (only if Exception is passed) and trace error message.<br/>
     * 
     * @param shell
     *            Shell
     * @param title
     *            Title
     * @param description
     *            The top message
     * @param reason
     *            Error message
     * @param errorList
     *            List
     */
    public static void openErrorDialog(Shell shell, String title,
            String description, String reason, List<String> errorList) {
        LOGGER.debug("shell:" + shell + "messasge:" + reason + "errorList:"
                + errorList);
        org.eclipse.jface.dialogs.ErrorDialog
                .openError(shell, title, description, ErrorDialog
                        .createMultiStatus(IStatus.ERROR, reason, errorList));

    }

    /**
     * Show the dialog with specified status.<br/>
     * 
     * @param type
     *            Type
     * @param reason
     *            Error message
     * @param errorList
     *            List
     * @return MultiStatus
     * 
     */
    private static MultiStatus createMultiStatus(int type, String reason,
            List<String> errorList) {
        MultiStatus mStatus = new MultiStatus(Activator.PLUGIN_ID, IStatus.OK,
                reason, null);
        for (String temp : errorList) {
            IStatus status = new Status(type, Activator.PLUGIN_ID, IStatus.OK,
                    temp, null);
            mStatus.add(status);
        }
        return mStatus;
    }
}
