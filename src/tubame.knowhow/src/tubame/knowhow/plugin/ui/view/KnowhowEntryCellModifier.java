/*
 * KnowhowEntryCellModifier.java
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
package tubame.knowhow.plugin.ui.view;

import java.util.List;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TreeItem;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.model.editor.EntryOperator;
import tubame.knowhow.plugin.model.view.AbstractViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.util.PluginUtil;

/**
 * Subject name change class know-how of entry view.<br/>
 */
public class KnowhowEntryCellModifier implements ICellModifier {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(KnowhowEntryCellModifier.class);
    /** Edit determination */
    private static boolean isModify;
    /** Know-how entry view */
    private KnowhowEntryTreeViewer knowhowEntryTreeViewer;

    /**
     * Constructor.<br/>
     * 
     * @param knowhowEntryTreeViewer
     *            Know-how entry view
     */
    public KnowhowEntryCellModifier(
            KnowhowEntryTreeViewer knowhowEntryTreeViewer) {
        this.knowhowEntryTreeViewer = knowhowEntryTreeViewer;
        KnowhowEntryCellModifier.LOGGER.debug("[knowhowEntryTreeViewer]"
                + knowhowEntryTreeViewer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canModify(Object element, String property) {
        KnowhowEntryCellModifier.LOGGER.debug("[element]" + element
                + "[property]" + property);
        return isModify;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(Object element, String property) {
        KnowhowEntryCellModifier.LOGGER.debug("[element]" + element
                + "[property]" + property);
        // Start editing
        if (element instanceof PortabilityKnowhowListViewOperation) {
            PortabilityKnowhowListViewOperation knowhowListViewOperation = (PortabilityKnowhowListViewOperation) element;
            return knowhowListViewOperation.getName();
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modify(Object element, String property, Object value) {
        KnowhowEntryCellModifier.LOGGER.debug("[element]" + element
                + "[property]" + property + "[value]" + value);
        // Close editor
        if (element instanceof TreeItem) {
            TreeItem item = (TreeItem) element;

            PortabilityKnowhowListViewOperation knowhowListViewOperation = (PortabilityKnowhowListViewOperation) item
                    .getData();
            knowhowListViewOperation.setSubjectName(value.toString());
            AbstractViewType abstractViewType = knowhowListViewOperation
                    .getKnowhowViewType();
            abstractViewType.setRegisterName(value.toString());
            knowhowListViewOperation.setKnowhowViewType(abstractViewType);
            knowhowEntryTreeViewer.refreshTreeViewer(true);
            // Name change to the know-how Advanced Editor
            refrectionDocumentationEditor(knowhowListViewOperation);

            KnowhowEntryCellModifier.LOGGER.info(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_CHANGE_SUBJECT_NAME));
            
            try {
				KnowhowEntryCheckItemView checkItemEntryView = (KnowhowEntryCheckItemView) PluginUtil.getCheckItemEntryView();
				checkItemEntryView.refresh();
				
			} catch (JbmException e) {
				 JbmException.outputExceptionLog(LOGGER, e, MessagePropertiesUtil
		                    .getMessage(MessagePropertiesUtil.FAIL_REFRESH_CHECKITEM_VIEW));
			}
        }
        KnowhowEntryCellModifier.setModify(false);
    }

    /**
     * Reflected in the documentation tab the subject name that is registered in
     * the entry view.<br/>
     * 
     * @param selectedEntry
     *            PortabilityKnowhowListViewOperation
     */
    private void refrectionDocumentationEditor(
            PortabilityKnowhowListViewOperation selectedEntry) {

        MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor = PluginUtil
                .getKnowhowEditor();
        replaceChapterName(knowhowMultiPageEditor.getTreeViewerOperator()
                .getInputEntry(), selectedEntry);
        knowhowMultiPageEditor.getTreeViewerOperator().refreshTreeViewer(true);

    }

    /**
     * Replace the chapter name that appears in the documentation tab.<br/>
     * 
     * @param inputEntry
     *            List<EntryOperator>
     * @param selectedEntry
     *            PortabilityKnowhowListViewOperation
     */
    private void replaceChapterName(List<EntryOperator> inputEntry,
            PortabilityKnowhowListViewOperation selectedEntry) {

        for (EntryOperator entryOperator : inputEntry) {
            if (selectedEntry.getKnowhowViewType().getRegisterKey()
                    .equals(entryOperator.getKey())) {
                entryOperator.setName(selectedEntry.getName());
            } else if (!entryOperator.getEntryChildren().isEmpty()) {
                this.replaceChapterName(entryOperator.getEntryChildren(),
                        selectedEntry);
            }
        }

    }

    /**
     * Set the editing determination.<br/>
     * 
     * @param isModify
     *            Edit determination
     */
    public static void setModify(boolean isModify) {
        KnowhowEntryCellModifier.LOGGER.debug("[isModify]" + isModify);
        KnowhowEntryCellModifier.isModify = isModify;
    }
}
