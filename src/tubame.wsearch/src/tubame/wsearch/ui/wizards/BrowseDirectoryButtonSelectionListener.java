/*
 * BrowseDirectoryButtonSelectionListener.java
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

import java.io.File;

import tubame.common.util.CmnStringUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.IFolder;
import org.eclipse.core.resources.IProject;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.Path;
import org.eclipse.jface.viewers.Viewer;
import org.eclipse.jface.viewers.ViewerFilter;
import org.eclipse.jface.window.Window;
import org.eclipse.jface.wizard.WizardPage;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.dialogs.ElementTreeSelectionDialog;
import org.eclipse.ui.model.WorkbenchContentProvider;
import org.eclipse.ui.model.WorkbenchLabelProvider;
import tubame.wsearch.Activator;
import tubame.wsearch.util.PluginUtil;

/**
 * It is the directory selection screen for listener class.<br/>
 */
public class BrowseDirectoryButtonSelectionListener implements
        SelectionListener {

    /**
     * Selection results reflect destination text
     */
    private Text targetText;

    /**
     * Page
     */
    private WizardPage wizardPage;

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param targetText
     *            Selection results reflect destination text
     * @param wizardPage
     *            Page
     */
    public BrowseDirectoryButtonSelectionListener(Text targetText,
            WizardPage wizardPage) {
        this.targetText = targetText;
        this.wizardPage = wizardPage;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        String filePath = targetText.getText();

        // Create dialog
        ElementTreeSelectionDialog dialog = new ElementTreeSelectionDialog(
                null, new WorkbenchLabelProvider(),
                new WorkbenchContentProvider());

        // Set the resources that are displayed in the dialog
        final IProject project = PluginUtil.getSelectedProject();
        dialog.setInput(ResourcesPlugin.getWorkspace());
        dialog.setMessage(project.getName());
        dialog.setTitle(Activator
                .getResourceString(BrowseDirectoryButtonSelectionListener.class
                        .getName() + ".title.target"));
        dialog.setHelpAvailable(false);

        // Acquisition check to see if the file has been entered in the text
        // box, and if it is filled.
        IFile file = null;
        if (!"".equals(filePath)) {
            IContainer container = ResourcesPlugin.getWorkspace().getRoot();
            Path path = new Path(filePath);
            if (container.exists(path)) {
                File targetFile = new File(container.getLocationURI().getPath()
                        + CmnStringUtil.SLASH + path.toOSString());
                if (targetFile.exists() && targetFile.isFile()) {
                    file = container.getFile(path);
                }
            }
            dialog.setInitialSelection(file);
        }

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
                } else if (element instanceof IProject) {
                    return (element == project);
                }
                return false;
            }
        });

        // Open the dialog.
        if (dialog.open() == Window.OK) {
            Object result = dialog.getFirstResult();
            if (result instanceof IFolder) {
                targetText.setText(project.getName() + CmnStringUtil.SLASH
                        + ((IFolder) result).getProjectRelativePath());
                wizardPage.setErrorMessage(null);
            } else if (result instanceof IProject) {
                targetText.setText(project.getName());
                wizardPage.setErrorMessage(null);
            }
            if (!isSetDirPath(targetText.getText())) {
                wizardPage
                        .setErrorMessage(Activator
                                .getResourceString(BrowseDirectoryButtonSelectionListener.class
                                        .getName() + ".err.msg.NotSelectedErr"));
            }
        }
    }

    /**
     * Determine directory path has been set.<br/>
     * 
     * @param targetTextData
     *            Target text data
     * @return Directory path is determined is being set
     */
    private boolean isSetDirPath(String targetTextData) {
        if (CmnStringUtil.isEmpty(targetTextData)) {
            return false;
        }
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // no oparate
    }
}