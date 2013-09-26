/*
 * EditorUtil.java
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
package org.tubame.portability.util;

import org.eclipse.core.resources.IFile;
import org.eclipse.core.resources.ResourcesPlugin;
import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Font;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.IFileEditorInput;
import org.eclipse.ui.PartInitException;
import org.eclipse.ui.ide.IDE;
import org.tubame.portability.exception.JbmException;
import org.tubame.portability.exception.JbmException.ERROR_LEVEL;
import org.tubame.portability.util.resource.MessageUtil;
import org.tubame.portability.util.resource.ResourceUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Utility class that specializes in EclipseEditor.<br/>
 * Set or get operation EclipseEditor properties etc.<br/>
 */
public class EditorUtil {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EditorUtil.class);

    /**
     * Default constructor.<br/>
     * Disable the instance generation from other class.<br/>
     * 
     */
    private EditorUtil() {
        // no operation
    }

    /**
     * Font of the search results screen editing
     */
    private static final Font FONT_FIRST_LEVEL = new Font(null, "Tahoma", 9,
            SWT.BOLD);

    /**
     * Get the current workspace.<br/>
     * Get the workspace name of Editor that you specify.<br/>
     * 
     * @param editor
     *            Editor
     * @return Workspace name
     */
    public static String getWorkSpace(IEditorPart editor) {
        return ((IFileEditorInput) editor.getEditorInput()).getFile()
                .getWorkspace().getRoot().getRawLocation().toString();
    }

    /**
     * Get the full path currently being edited.<br/>
     * Get the edited file full path currently Editor of the specified.<br/>
     * 
     * @param editor
     *            Editor
     * @return File full path
     */
    public static String getEditorOpenFileFullPath(IEditorPart editor) {
        return ((IFileEditorInput) editor.getEditorInput()).getFile()
                .getFullPath().toString();
    }

    /**
     * Get the first layer of the search results Font editing screen.<br/>
     * 
     * @return Font in the first layer
     */
    public static Font getFirstLevelFont() {
        return EditorUtil.FONT_FIRST_LEVEL;
    }

    /**
     * Open the editor.<br/>
     * 
     * @param filePath
     *            File Path
     */
    public static void openEditor(String filePath) {

        IFile file = PluginUtil.createIFile(ResourcesPlugin.getWorkspace()
                .getRoot(), filePath);
        if (file == null) {
            String message = String.format(MessageUtil.ERR_OPEN_FILE, filePath);
            JbmException.outputExceptionLog(null, LOGGER, ERROR_LEVEL.ERROR,
                    new String[] { message });
            PluginUtil.viewErrorDialog(ResourceUtil.FILE_OPEN_ERROR_TITLE,
                    message, null);
        } else {
            try {
                IDE.openEditor(PluginUtil.getActiveWorkbenchWindow()
                        .getActivePage(), file);
            } catch (PartInitException e) {
                JbmException.outputExceptionLog(e, LOGGER, ERROR_LEVEL.ERROR,
                        new String[] { filePath });
            }
        }
    }
}
