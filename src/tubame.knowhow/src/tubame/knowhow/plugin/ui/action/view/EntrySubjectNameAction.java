/*
 * EntrySubjectNameAction.java
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
package tubame.knowhow.plugin.ui.action.view;

import tubame.common.util.CmnStringUtil;

import org.eclipse.jface.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.view.KnowhowEntryCellModifier;
import tubame.knowhow.plugin.ui.view.KnowhowEntryTreeViewer;
import tubame.knowhow.plugin.ui.view.KnowhowEntryTreeViewerOperator;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Subject Name Change Action class know-how entry view.<br/>
 */
public class EntrySubjectNameAction extends Action {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntrySubjectNameAction.class);
    /** Edited column index */
    private static final int COLUMN_INDEX = 0;

    /** TreeViewer to be added */
    private KnowhowEntryTreeViewerOperator treeViewer;
    /** Entry objects contained in the select target */
    private PortabilityKnowhowListViewOperation selectedEntry;

    /**
     * Constructor.<br/>
     * 
     * @param treeViewer
     *            KnowhowEntryTreeViewerOperator
     * @param selectedEntry
     *            Entry you have selected
     * 
     */
    public EntrySubjectNameAction(KnowhowEntryTreeViewerOperator treeViewer,
            PortabilityKnowhowListViewOperation selectedEntry) {
        this.treeViewer = treeViewer;
        this.selectedEntry = selectedEntry;
        EntrySubjectNameAction.LOGGER.debug("[treeViewer]" + treeViewer
                + "[selectedEntry]" + selectedEntry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return ResourceUtil.changeName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        EntrySubjectNameAction.LOGGER.debug(CmnStringUtil.EMPTY);
        KnowhowEntryTreeViewer entryTreeViewer = (KnowhowEntryTreeViewer) treeViewer;
        KnowhowEntryCellModifier.setModify(true);
        entryTreeViewer.editElement(selectedEntry, COLUMN_INDEX);
        EntrySubjectNameAction.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_CHANGE_SUBJECT_NAME));
    }
}
