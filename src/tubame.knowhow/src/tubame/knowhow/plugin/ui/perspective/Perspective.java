/*
 * Perspective.java
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
package tubame.knowhow.plugin.ui.perspective;

import org.eclipse.ui.IFolderLayout;
import org.eclipse.ui.IPageLayout;
import org.eclipse.ui.IPerspectiveFactory;

import tubame.knowhow.util.PluginUtil;

/**
 * <Manage perspective.<br/>
 */
public class Perspective implements IPerspectiveFactory {

    /** Rate of the Project Explorer */
    private static final float PROJECT_EXPLORER_RATIO = (float) 0.2f;
    /** Rate on the right layout */
    private static final float LAYOUT_RATIO = (float) 0.75f;
    /** FOLSER_ID */
    private static final String FOLDER_ID = "right";

    /**
     * {@inheritDoc}
     */
    @Override
    public void createInitialLayout(IPageLayout layout) {
        // Get the editor area
        String editorArea = layout.getEditorArea();
        // Place the Project Explorer on the left side of the editor area
        layout.addView(IPageLayout.ID_PROJECT_EXPLORER, IPageLayout.LEFT,
                Perspective.PROJECT_EXPLORER_RATIO, editorArea);
        IFolderLayout right = layout.createFolder(FOLDER_ID, IPageLayout.RIGHT,
                Perspective.LAYOUT_RATIO, editorArea);
        // Place the know-how entry view on the right-hand side of the editor
        // area
        right.addView(PluginUtil.getKnowhowEntryViewId());

    }
}
