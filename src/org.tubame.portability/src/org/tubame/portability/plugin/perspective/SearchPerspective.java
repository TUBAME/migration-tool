/*
 * SearchPerspective.java
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
package org.tubame.portability.plugin.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;
import org.tubame.portability.util.PluginUtil;

/**
 * Search for management perspective.<br/>
 * Perspective after the scan to start in the jbm file display.<br/>
 * Set {@link org.tubame.portability.plugin.view.CheckListInformationView}
 * to EclipseEditor right place.<br/>
 * Set {@link org.tubame.portability.plugin.view.HtmlGuideView} to
 * EclipseView place.<br/>
 * Set {@link org.tubame.portability.plugin.view.WorkStatusView} to
 * EclipseView place.<br/>
 * 
 */
public class SearchPerspective implements IPerspectiveFactory {

    /**
     * Rate of the Project Explorer
     */
    private static final float PROJECT_EXPLORER_RATIO = 0.2f;

    /**
     * Rate of HTML guide view
     */
    private static final float HTML_VIEW_RATIO = 0.8f;

    /**
     * Rate on the right side layout
     */
    private static final float LAYOUT_RATIO = 0.75f;
    /**
     * Rate of work progress
     */
    private static final float WORK_VIEW_RATIO = 0.8f;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createInitialLayout(IPageLayout layout) {
        // Get the editor area
        String editorArea = layout.getEditorArea();

        // Place the Project Explorer on the left side of the editor area
        layout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT,
                SearchPerspective.PROJECT_EXPLORER_RATIO, editorArea);

        // Place the HTML guide view on the lower side of the editor area
        layout.addView(PluginUtil.getHtmlViewId(), IPageLayout.BOTTOM,
                SearchPerspective.HTML_VIEW_RATIO, editorArea);

        IFolderLayout right = layout.createFolder("right", IPageLayout.RIGHT,
                SearchPerspective.LAYOUT_RATIO, editorArea);
        // Place a check list view information on the right-hand side of the
        // editor area
        right.addView(PluginUtil.getCheckListViewId());
        // Place the work progress view to the right of the editor area
        layout.addView(PluginUtil.getWorkStatusViewId(), IPageLayout.BOTTOM,
                SearchPerspective.WORK_VIEW_RATIO, "right");
    }
}
