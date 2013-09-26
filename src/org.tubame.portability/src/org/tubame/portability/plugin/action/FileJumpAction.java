/*
 * FileJumpAction.java
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

import org.eclipse.jface.action.Action;
import org.tubame.portability.util.EditorUtil;
import org.tubame.portability.util.resource.ResourceUtil;

/**
 * Gets the file name after conversion of the selected lines, <br/>
 * open the Eclipse editor file after conversion.<br/>
 * If the converted file does not exist, the message to that effect is displayed
 * in the Dialog screen.<br/>
 */
public class FileJumpAction extends Action {
    /**
     * File full path name to file open
     */
    private final String targetFilePath;

    /**
     * Menu name
     */
    private static final String MENU_NAME = ResourceUtil.CONVERT_MENU_OPEN_FILE;

    /**
     * Constructor. Have a file name argument to open a file.<br/>
     * 
     * @param openFileFullPath
     *            File name to open the file
     */
    public FileJumpAction(String openFileFullPath) {
        super();
        targetFilePath = openFileFullPath;
        super.setText(FileJumpAction.MENU_NAME);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        super.run();
        EditorUtil.openEditor(targetFilePath);
    }
}
