/*
 * SelectionChangedListener.java
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
package tubame.portability.plugin.editor;

import java.io.IOException;

import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Point;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.logic.CheckListInformationFacade;
import tubame.portability.logic.CheckListInformationFactory;
import tubame.portability.logic.GuideViewFacade;
import tubame.portability.model.CheckListInformation;
import tubame.portability.model.JbmEditorEnum;
import tubame.portability.model.JbmEditorMigrationRow;
import tubame.portability.plugin.view.CheckListInformationView;
import tubame.portability.util.resource.MessageUtil;

/**
 * Search results edit screen row selection listener.<br/>
 * After selecting the line, and display the information in the checklist
 * checklist information view.<br/>
 * Checklist operation {@link CheckListInformationFacade} reference.<br/>
 */
public class SelectionChangedListener implements ISelectionChangedListener {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SelectionChangedListener.class);

    /**
     * Object you currently have selected
     */
    private Object selectionObject;

    /**
     * Editor
     */
    private final MigrationEditorOperation editor;

    /**
     * Constructor.<br/>
     * 
     * @param menuOperation
     *            Editor
     */
    public SelectionChangedListener(MigrationEditorOperation menuOperation) {
        editor = menuOperation;

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(SelectionChangedEvent event) {
    	LOGGER.info("SelectionChangedEvent event");
        selectionObject = ((StructuredSelection) event.getSelection())
                .getFirstElement();

        // Get the Cell that was clicked from mouse click coordinate
        Point point = editor.getMouseClickPoint();
        ViewerCell cell = ((org.eclipse.jface.viewers.TreeViewer) event
                .getSource()).getCell(point);
        if (cell == null) {
            // That the cell can not I get a row that has been deleted
            return;
        }

        if (selectionObject instanceof JbmEditorMigrationRow) {
            // View checklist information view
            JbmEditorMigrationRow row = (JbmEditorMigrationRow) selectionObject;
            printInformationView(row.getNo());
            // If the guide chapter number is clicked, it displays a guide
            if (JbmEditorEnum.CHAPTER_NO.getCode() == cell.getColumnIndex()) {
                try {
                    GuideViewFacade.view(row.getChapterNo());
                } catch (IOException e) {
                    LOGGER.error(MessageUtil.LOG_ERR_GUIDE_HTML_DISPLAY_FAIL, e);
                    throw new RuntimeException(e);
                }
            }
        }
    }

    /**
     * Set the value to the checklist information view.<br/>
     * 
     * @param no
     *            No
     */
    private void printInformationView(String no) {
		CheckListInformationFactory.getCheckListInformationFacade()
				.initCheckListInformationReader();
        CheckListInformation message = new CheckListInformation();
        CheckListInformationFacade facade = CheckListInformationFactory
                .getCheckListInformationFacade();
        message.setBigText(facade.getBigDescription(no));
        message.setMiddleText(facade.getMiddleDescription(no));
        message.setCheckEyeText(facade.getCheckEyeDescription(no));
        message.setHearingText(facade.getHearingDescription(no));
        message.setSearchText(facade.getSearchDescription(no));
        message.setFactorText(facade.getFactorDescription(no));
        message.setDegreeDetailText(facade.getDegreeDescription(no));
        message.setAppropriateText(facade.getAppropriateDescription(no));
        message.setInvestText(facade.getInvestigationDescription(no));
        CheckListInformationView.out(message);
    }

    /**
     * Get selected in the (keyboard) mouse (# selectionChanged) object.<br/>
     * 
     * @return Selected object
     */
    public Object getSelectionItem() {
        return selectionObject;
    }
}
