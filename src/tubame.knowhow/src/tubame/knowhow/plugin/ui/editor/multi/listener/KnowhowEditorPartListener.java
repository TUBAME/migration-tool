/*
 * KnowhowEditorPartListener.java
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
package tubame.knowhow.plugin.ui.editor.multi.listener;

import tubame.common.util.CmnFileUtil;
import tubame.common.util.CmnStringUtil;

import org.eclipse.ui.IPartListener2;
import org.eclipse.ui.IWorkbenchPartReference;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.logic.FileManagement;
import tubame.knowhow.plugin.logic.KnowhowManagement;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.ViewUtil;

/**
 * Control of each listener processing know-how of editor.<br/>
 */
public class KnowhowEditorPartListener implements IPartListener2 {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(KnowhowEditorPartListener.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void partActivated(IWorkbenchPartReference partRef) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partBroughtToTop(IWorkbenchPartReference partRef) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partClosed(IWorkbenchPartReference partRef) {
        KnowhowEditorPartListener.LOGGER.debug("[partRef]" + partRef);
        if (isMaintenaceKnowhowEditor(partRef)) {
            KnowhowManagement.setSubjectName(CmnStringUtil.EMPTY);
            deleteHtmlTemporaryFile();
            ViewUtil.initializationKnowhowData();
            ViewUtil.knowhowEntryViewRefresh();
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partDeactivated(IWorkbenchPartReference partRef) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partOpened(IWorkbenchPartReference partRef) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partHidden(IWorkbenchPartReference partRef) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partVisible(IWorkbenchPartReference partRef) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void partInputChanged(IWorkbenchPartReference partRef) {
        // no operation
    }

    /**
     * Determining whether the editor know-how.<br/>
     * 
     * @param partRef
     *            IWorkbenchPartReference
     * @return true:Know-how editor false:Other
     */
    private boolean isMaintenaceKnowhowEditor(IWorkbenchPartReference partRef) {
        if (partRef.getPart(false) instanceof MaintenanceKnowhowMultiPageEditor) {
            return true;
        }
        return false;
    }

    /**
     * Delete the temporary file documentation for the DocBook for HTML.<br/>
     * 
     */
    private void deleteHtmlTemporaryFile() {
        if (FileManagement.getDocBookHtmlTempFilePath() != null
                && CmnFileUtil.fileExists(FileManagement
                        .getDocBookHtmlTempFilePath())) {
            CmnFileUtil.delete(FileManagement.getDocBookHtmlTempFilePath());
            FileManagement.setDocBookHtmlTempFilePath(CmnStringUtil.EMPTY);
            PluginUtil.refreshWorkSpace();
        }
        if (FileManagement.getKnowhowHtmlTempFilePath() != null
                && CmnFileUtil.fileExists(FileManagement
                        .getKnowhowHtmlTempFilePath())) {
            CmnFileUtil.delete(FileManagement.getKnowhowHtmlTempFilePath());
            FileManagement.setKnowhowHtmlTempFilePath(CmnStringUtil.EMPTY);
            PluginUtil.refreshWorkSpace();
        }
    }
}
