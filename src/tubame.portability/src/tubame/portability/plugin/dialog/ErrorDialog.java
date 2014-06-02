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
package tubame.portability.plugin.dialog;

import java.util.List;

import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.swt.widgets.Shell;

import tubame.portability.Activator;

/**
 * It is an error screen to display if an error occurs.<br/>
 * Implement the error screen to be displayed in the system error occurs inherit
 * MessageDialog.<br/>
 * Make the opening and closing of the error log display unit by which the user
 * presses the "More Information" button.<br/>
 */
public final class ErrorDialog {

    /**
     * Constructor.<br/>
     * Do not do anything.<br/>
     */
    private ErrorDialog() {
        // no operation
    }

    /**
     * Display an error dialog.<br/>
     * Show the (only if Exception is passed) trace and error messages.<br/>
     * 
     * @param shell
     *            Shell
     * @param e
     *            Exception
     * @param messasge
     *            Error message
     */
    public static void openErrorDialog(Shell shell, Exception e, String messasge) {
        ErrorDialog.open(shell, e, messasge, IStatus.ERROR);
    }

    public static void openWarnDialog(Shell shell, Exception e, String messasge) {
        ErrorDialog.open(shell, e, messasge, IStatus.WARNING);
    }
    /**
     * Display an error dialog with the specified icon.<br/>
     * 
     * @param shell
     *            Shell
     * @param e
     *            Exception
     * @param message
     *            Error message
     * @param type
     *            Status
     */
    private static void open(Shell shell, Exception e, String message, int type) {
        if (e == null) {
            org.eclipse.jface.dialogs.ErrorDialog.openError(shell, null, null,
                    new Status(type, Activator.PLUGIN_ID, message));
        } else {
            MultiStatus status = new MultiStatus(Activator.PLUGIN_ID, type,
                    message, e);
            for (StackTraceElement element : e.getStackTrace()) {
                status.add(new Status(type, Activator.PLUGIN_ID, element
                        .toString()));
            }
            org.eclipse.jface.dialogs.ErrorDialog.openError(shell, null, null,
                    status);
        }
    }

    /**
     * Display an error dialog warning icon.<br/>
     * Show the (only if Exception is passed) trace and error messages.<br/>
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
        org.eclipse.jface.dialogs.ErrorDialog.openError(shell, title,
                description, ErrorDialog.createMultiStatus(IStatus.WARNING,
                        reason, errorList));
    }

    /**
     * Display an error dialog the error icon.<br/>
     * Show the (only if errorList is passed) trace and error messages.<br/>
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
        org.eclipse.jface.dialogs.ErrorDialog
                .openError(shell, title, description, ErrorDialog
                        .createMultiStatus(IStatus.ERROR, reason, errorList));
    }

    /**
     * Display a dialog with the status that you specify.<br/>
     * 
     * @param type
     *            Type
     * @param reason
     *            Error message
     * @param errorList
     *            List
     * @return MultiStatus
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