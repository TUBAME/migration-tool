/*
 * ShowSearchWizardAction.java
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
package org.tubame.portability.plugin.action;

import org.eclipse.core.resources.IResource;
import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.wizard.WizardDialog;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.IActionDelegate;
import org.eclipse.ui.IObjectActionDelegate;
import org.eclipse.ui.IWorkbenchPart;
import org.eclipse.ui.IWorkbenchWindow;
import org.tubame.portability.plugin.wizard.JbmSearchWizard;
import org.tubame.portability.util.PluginUtil;
import org.tubame.portability.util.resource.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Search transplant run action class.<br/>
 */
public class ShowSearchWizardAction implements IObjectActionDelegate {

    /**
     * Resources of the search executed
     */
    private IResource resource;

    /**
     * Shell
     */
    private Shell shell;

    /**
     * Workbench window
     */
    private IWorkbenchWindow window;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ShowSearchWizardAction.class);

    /**
     * Constructor.<br/>
     */
    public ShowSearchWizardAction() {
        super();
        this.window = PluginUtil.getActiveWorkbenchWindow();
    }

    /**
     * @see IObjectActionDelegate#setActivePart(IAction, IWorkbenchPart)<br/>
     */
    @Override
    public void setActivePart(IAction action, IWorkbenchPart targetPart) {
        shell = targetPart.getSite().getShell();
    }

    /**
     * @see IActionDelegate#run(IAction)<br/>
     */
    @Override
    public void run(IAction action) {
        LOGGER.info(MessageUtil.INF_SHOW_SEARCHWIZARD);

        WizardDialog wizardDialog = new WizardDialog(shell,
                new JbmSearchWizard(window, this.resource));
        wizardDialog.setHelpAvailable(false);
        wizardDialog.open();
    }

    /**
     * @see IActionDelegate#selectionChanged(IAction, ISelection)<br/>
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        this.resource = getSelectedResource(selection);
    }

    /**
     * Get the selected resource.<br/>
     * 
     * @param selection
     *            Selected object
     * @return Resource
     */
    private IResource getSelectedResource(ISelection selection) {
        Object select = ((IStructuredSelection) selection).getFirstElement();
        if (select instanceof IResource) {
            return ((IResource) select);
        }
        return null;
    }
}
