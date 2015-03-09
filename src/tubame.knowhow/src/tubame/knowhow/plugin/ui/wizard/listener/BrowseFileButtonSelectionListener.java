/*
 * BrowseFileButtonSelectionListener.java
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
package tubame.knowhow.plugin.ui.wizard.listener;

import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.IResource;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.common.util.CmnFileUtil;
import tubame.common.util.CmnStringUtil;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.ui.wizard.commencement.ConfirmationPage;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Reference File button listener.<br/>
 * Listener class that displays a file selection dialog.<br/>
 */
public class BrowseFileButtonSelectionListener implements SelectionListener {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(BrowseFileButtonSelectionListener.class);
    /** Know-how text output destination */
    private Text outputPlace;
    /** Page */
    private ConfirmationPage page;
    
    

    /**
     * Constructor.<br/>
     * 
     * @param page
     *            Page
     * @param outputPlace
     *            Text to be displayed in the file selection after
     * @param extension
     *            Extension
     */
    public BrowseFileButtonSelectionListener(ConfirmationPage page,
            Text outputPlace, String extension) {
        this.outputPlace = outputPlace;
        this.page = page;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // no operation

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        BrowseFileButtonSelectionListener.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_OPEN_SELECT_FILE_DOALOG));
        // Specify from the browse button output destination, (file name set to
        // the end) create a path
        IFolder targetFolder = folderBrowse();
        if (targetFolder != null) {
            StringBuilder sb = new StringBuilder();
            sb.append(targetFolder.getProject().getName());
            sb.append(CmnFileUtil.FILE_SEPARATOR);
            sb.append(targetFolder.getProjectRelativePath());
            sb.append(CmnFileUtil.FILE_SEPARATOR);
            sb.append(page.getKnowhowFileName());
            outputPlace.setText(sb.toString());
        }
    }

    /**
     * View the file selection dialog.<br/>
     * Called when the know-how of creating XML files "Browse" button is
     * pressed.<br/>
     * 
     * @return File
     */
    private IFolder folderBrowse() {
        String filePath = page.outputFilePath();
        String filePathExcludeProjectName = page.getOutputFilePathExcludeProjectName();
        if (CmnStringUtil.isEmpty(filePath)) {
            return null;
        }
        // Create dialog
        final ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
                PluginUtil.getActiveWorkbenchShell(), new WorkbenchLabelProvider(),
                new WorkbenchContentProvider());

        // Set the resources that are displayed in the dialog
//        IProject project = PluginUtil.getProject(filePath);
//        this.page.getSelectionProject();
        IProject selectionProject = this.page.getSelectionProject();
        dialog.setInput(selectionProject);
        dialog.setMessage(getDialogMessage( this.page.getSelectionProject()));
        dialog.setTitle(ResourceUtil.selectOutput);
        dialog.setHelpAvailable(false);
        // Add ViewerFileter
        dialog.addFilter(new ViewerFilter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public boolean select(Viewer viewer, Object parentElement,
                    Object element) {
                if (element instanceof IFolder) {
                    return true;
                }
                return false;
            }
        });
//        dialog.getFirstResult();
        // Open the dialog.
        if (dialog.open() == Window.OK) {
            Object result = dialog.getFirstResult();
            if (result instanceof IFolder) {
                return (IFolder) result;
            }
        }
       
        return null;
    }

    /**
     * Get the resources to be input in the dialog.<br/>
     * Under the project if the project is (or input) selection,<br/>
     * the return of the underlying workspace if it is not selected.<br/>
     * 
     * @param project
     *            Project that is (or input) selection
     * @return Resources to be input in the dialog
     */
    private IResource getDialogInput(IProject project) {
        if (project != null) {
            return (IResource) project;
        } else {
            return (IResource) ResourcesPlugin.getWorkspace();
        }
    }

    /**
     * Get the message to be displayed at the top of the dialog.<br/>
     * If the project is (or input) is selected, the display in addition to the
     * project name.<br/>
     * 
     * @param project
     *            Project that is (or input) selection
     * @return Message
     */
    private String getDialogMessage(IProject project) {
        String projectName = CmnStringUtil.EMPTY;
        if (project != null) {
            projectName = project.getName();
        }
        return String.format(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.SELECTED_OUTPUT_FOLDER),
                projectName);
    }
}
