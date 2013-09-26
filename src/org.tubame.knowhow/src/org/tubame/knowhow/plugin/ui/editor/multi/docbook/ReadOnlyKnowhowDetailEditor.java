/*
 * ReadOnlyKnowhowDetailEditor.java
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
package org.tubame.knowhow.plugin.ui.editor.multi.docbook;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;

/**
 * Class that controls the know-how advanced editor non-editable.<br/>
 */
public class ReadOnlyKnowhowDetailEditor extends KnowhowDetailEditor {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ReadOnlyKnowhowDetailEditor.class);

    /**
     * Constructor.<br/>
     * 
     * @param maintenanceKnowhowMultiPageEditor
     *            Know-how editor
     */
    public ReadOnlyKnowhowDetailEditor(
            MaintenanceKnowhowMultiPageEditor maintenanceKnowhowMultiPageEditor) {
        super(maintenanceKnowhowMultiPageEditor);
        ReadOnlyKnowhowDetailEditor.LOGGER
                .info(MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.LOG_CREATE_DISABLE_EDIT_KNOWHOWPAGE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditorInputModifiable() {
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isEditorInputReadOnly() {
        return true;
    }
}
