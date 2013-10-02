/*
 * WSearchResultViewLauncher.java
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
package tubame.wsearch.ui.editor;

import org.eclipse.core.resources.IWorkspace;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.core.runtime.IPath;
import org.eclipse.ui.IEditorLauncher;
import org.eclipse.ui.IViewReference;
import org.eclipse.ui.IWorkbenchPage;
import org.eclipse.ui.PartInitException;
import tubame.wsearch.Activator;
import tubame.wsearch.ui.view.WSearchResultView;
import tubame.wsearch.util.PluginUtil;

/**
 * It is a starting class of general-purpose search results view.<br/>
 * General-purpose search result file is the class to start a generic search
 * results view when it is double-click in the Package Explorer, etc..<br/>
 */
public class WSearchResultViewLauncher implements IEditorLauncher {

    /**
     * {@inheritDoc}
     */
    @Override
    public void open(IPath file) {
        String filePath = file.toOSString();
        System.out.println(filePath);
        IWorkspace ws = ResourcesPlugin.getWorkspace();
        filePath = file.makeRelativeTo(ws.getRoot().getLocation()).toString();
        for (IViewReference ref : PluginUtil.getActivePage()
                .getViewReferences()) {
            if (WSearchResultView.ID.equals(ref.getId())
                    && filePath.equals(ref.getSecondaryId())) {
                PluginUtil.getActivePage().hideView(ref);
            }
        }
        try {
            PluginUtil.getActivePage().showView(WSearchResultView.ID, filePath,
                    IWorkbenchPage.VIEW_ACTIVATE);
        } catch (PartInitException e) {
            Activator.logWithDialog(e, e.getMessage());
        }
    }
}