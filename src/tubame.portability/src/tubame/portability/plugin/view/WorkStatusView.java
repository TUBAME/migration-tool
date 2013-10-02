/*
 * WorkStatusView.java
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
package tubame.portability.plugin.view;

import java.util.ArrayList;
import java.util.List;

import org.eclipse.swt.SWT;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IEditorPart;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import tubame.portability.exception.JbmException;
import tubame.portability.model.ConfirmItemEnum;
import tubame.portability.model.JbmEditorEnum;
import tubame.portability.model.JbmEditorMigrationRow;
import tubame.portability.plugin.action.ConfirmItemChangeAction;
import tubame.portability.plugin.editor.JbmEditorPart;
import tubame.portability.util.ColorUtil;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.MessageUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Work progress view class.<br/>
 * To see the contents of the following. <br/>
 * 
 * Work status Check status
 */
public class WorkStatusView extends ViewPart {

    /**
     * Scroll with Form
     */
    private ScrolledForm form;
    /**
     * Text area that displays the work situation
     */
    private Text workText;
    /**
     * Text area that displays the confirmation item
     */
    private Text statusText;
    /**
     * Text area that displays a visual confirmation of the number of lines
     */
    private Text lineText;

    /**
     * Get WorkStatusView instance.<br/>
     * 
     * @return Own instance
     */
    public static WorkStatusView getInstance() {
        try {
            return PluginUtil.getWordStatusView();
        } catch (JbmException e) {
            PluginUtil.viewErrorDialog(MessageUtil.ERR_VIEW_OUTPUT,
                    MessageUtil.ERR_WORK_STATUS_VIEW_OPEN, e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(Composite parent) {
        // form created
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createScrolledForm(parent);
        form.getBody().setLayout(new TableWrapLayout());

        // Work situation
        Section workSection = toolkit.createSection(form.getBody(),
                ExpandableComposite.TITLE_BAR);
        workSection.setText(ResourceUtil.WORKVIEW_NOW_WORK);
        workSection.setTitleBarBackground(ColorUtil.getYellowGreen());
        workSection.setTitleBarBorderColor(ColorUtil.getPopGreen());
        workSection.setTitleBarForeground(ColorUtil.getGreen());
        workSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        workText = new Text(form.getBody(), SWT.WRAP | SWT.READ_ONLY);
        workText.setBackground(ColorUtil.getWhite());

        setSeparator(toolkit);

        // Check status
        Section statusSection = toolkit.createSection(form.getBody(),
                ExpandableComposite.TITLE_BAR);
        statusSection.setText(ResourceUtil.WORKVIEW_NOW_CONFIRM);
        statusSection.setTitleBarBackground(ColorUtil.getYellowGreen());
        statusSection.setTitleBarBorderColor(ColorUtil.getPopGreen());
        statusSection.setTitleBarForeground(ColorUtil.getGreen());
        statusSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        statusText = new Text(form.getBody(), SWT.WRAP | SWT.READ_ONLY);
        statusText.setBackground(ColorUtil.getWhite());

        setSeparator(toolkit);

        // Unknown / ToDo: SE manual calculation item modification status
        Section lineSection = toolkit.createSection(form.getBody(),
                ExpandableComposite.TITLE_BAR);
        lineSection.setText(ResourceUtil.WORKVIEW_NOW_LINE_CONFIRM);
        lineSection.setTitleBarBackground(ColorUtil.getYellowGreen());
        lineSection.setTitleBarBorderColor(ColorUtil.getPopGreen());
        lineSection.setTitleBarForeground(ColorUtil.getGreen());
        lineSection.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
        lineText = new Text(form.getBody(), SWT.WRAP | SWT.READ_ONLY);
        lineText.setBackground(ColorUtil.getWhite());

    }

    /**
     * After setting the display contents of the working situation view,
     * display.<br/>
     * Own instance acquisition success, set the working situation. In addition, <br/>
     * depending on the status of confirmation, set the content.<br/>
     * 
     * @param isFileChange
     *            File change flag
     * @param list
     *            Data that is displayed
     * 
     */
    public static void out(boolean isFileChange,
            List<JbmEditorMigrationRow> list) {

        WorkStatusView instance = WorkStatusView.getInstance();
        if (instance != null) {
            if (list == null) {
                List<JbmEditorMigrationRow> rowList = WorkStatusView.getList();
                instance.workText.setText(instance.createStateString(
                        isFileChange, rowList));
                instance.statusText.setText(instance
                        .crerateConfirmString(rowList));
                instance.lineText.setText(instance
                        .crerateLineNumberString(rowList));
            } else {
                instance.workText.setText(instance.createStateString(
                        isFileChange, list));
                instance.statusText
                        .setText(instance.crerateConfirmString(list));
                instance.lineText.setText(instance
                        .crerateLineNumberString(list));
            }
            instance.statusText.setBackground(ColorUtil.getWhite());
            instance.lineText.setBackground(ColorUtil.getWhite());
            instance.form.reflow(true);
        }
    }

    /**
     * Get display data from the Editor Active.<br/>
     * 
     * @return Display data
     */
    private static List<JbmEditorMigrationRow> getList() {
        IEditorPart editorPart = PluginUtil.getActiveEditor();
        if (editorPart instanceof JbmEditorPart) {
            JbmEditorPart part = (JbmEditorPart) editorPart;
            @SuppressWarnings("unchecked")
            List<JbmEditorMigrationRow> rowList = (List<JbmEditorMigrationRow>) part
                    .getTreeViewer().getInput();
            return rowList;
        }
        return new ArrayList<JbmEditorMigrationRow>();
    }

    /**
     * Generate status string.<br/>
     * Express the status string in the following types.<br/>
     * <br/>
     * File when changing:Please copy and overwrite the save file.<br/>
     * Check items Existence at the time and without a file change:Check the
     * visual / hearing confirmation item.<br/>
     * Other than the above:Please import the "checklist. Xls".<br/>
     * 
     * @param isFileChange
     *            File change flag (true: no file change: change file false)
     * @param rowList
     *            Data that is displayed
     * @return Status string
     */
    private String createStateString(boolean isFileChange,
            List<JbmEditorMigrationRow> rowList) {
        if (isFileChange) {
            return ResourceUtil.WORKVIEW_MESSAGE_FILE_UPDATE;
        }
        if (isConfirmItem(rowList)) {
            return ResourceUtil.WORKVIEW_MESSAGE_CONFIRM;
        }
        if (isConfirmLineItem(rowList)) {
            return ResourceUtil.WORKVIEW_MESSAGE_TODO_SE_CONFIRM;
        }
        return ResourceUtil.WORKVIEW_MESSAGE_COMPLETE;
    }

    /**
     * The decision visual confirmation item is present.<br/>
     * 
     * @param rowList
     *            Data that is displayed
     * @return true:There is visual confirmation false:Without visual
     *         confirmation
     */
    private boolean isConfirmItem(List<JbmEditorMigrationRow> rowList) {
        for (JbmEditorMigrationRow first : rowList) {
            if (ConfirmItemChangeAction.NG.equals(first
                    .getColumnText(JbmEditorEnum.VISUAL_CONFIRM_STATSU_ITEM
                            .getCode()))) {
                return true;
            }
            if (ConfirmItemChangeAction.NG.equals(first
                    .getColumnText(JbmEditorEnum.HIARING_STATUS.getCode()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * The judgment unknown / SE manual calculation item is present.<br/>
     * 
     * @param rowList
     *            Data that is displayed
     * @return true:Unknown / SE manual calculation exists false:Unknown / SE
     *         manual calculation none
     */
    private boolean isConfirmLineItem(List<JbmEditorMigrationRow> rowList) {
        for (JbmEditorMigrationRow first : rowList) {
            if (ResourceUtil.WORKVIEW_MESSAGE_TODO_SE.equals(first
                    .getColumnText(JbmEditorEnum.LINE_NUM.getCode()))) {
                return true;
            }
            if (ResourceUtil.WORKVIEW_MESSAGE_UNKNOWN.equals(first
                    .getColumnText(JbmEditorEnum.LINE_NUM.getCode()))) {
                return true;
            }
        }
        return false;
    }

    /**
     * Create a display string of visual confirmation status.<br/>
     * 
     * @param rowList
     *            Data that is displayed
     * 
     * @return Visual confirmation string
     */
    private String crerateConfirmString(List<JbmEditorMigrationRow> rowList) {
        int checkEyeCount = 0;
        int allCheckEyeCount = 0;
        int hearingCount = 0;
        int allHearingCount = 0;
        String checkStatus = String.valueOf(ConfirmItemEnum.STATUS_NON
                .getType());
        String hearingStatus = String.valueOf(ConfirmItemEnum.STATUS_NON
                .getType());

        if (rowList != null && rowList.size() > 0) {
            for (JbmEditorMigrationRow first : rowList) {
                for (JbmEditorMigrationRow second : first.getChildList()) {
                    for (JbmEditorMigrationRow third : second.getChildList()) {
                        if (third.isCheckEye()) {
                            allCheckEyeCount++;
                            if (checkStatus.equals(third.getCheckEyeStatus())) {
                                checkEyeCount++;
                            }
                        }
                        if (third.isHearing()) {
                            allHearingCount++;
                            if (hearingStatus.equals(third.getHearingStatus())) {
                                hearingCount++;
                            }
                        }
                    }
                }
            }
        }
        return String.format(ResourceUtil.WORKVIEW_MESSAGE, checkEyeCount,
                allCheckEyeCount, hearingCount, allHearingCount);
    }

    /**
     * Create a display string of manual calculation item fix Status: Unknown /
     * ToDo.<br/>
     * 
     * @param rowList
     *            Data that is displayed
     * 
     * @return Line number correction confirmation string
     */
    private String crerateLineNumberString(List<JbmEditorMigrationRow> rowList) {
        int unknownCount = 0;
        int allUnknownCount = 0;
        int todoCount = 0;
        int allTodoCount = 0;

        if (rowList != null && rowList.size() > 0) {
            for (JbmEditorMigrationRow first : rowList) {
                if (first.isLineUnKnown()) {
                    allUnknownCount++;
                    if (first.getLineNumber().equals(
                            ResourceUtil.WORKVIEW_MESSAGE_UNKNOWN)) {
                        unknownCount++;
                    }
                }
                if (first.isLineToDoSe()) {
                    allTodoCount++;
                    if (first.getLineNumber().equals(
                            ResourceUtil.WORKVIEW_MESSAGE_TODO_SE)) {
                        todoCount++;
                    }
                }
            }
        }
        return String.format(ResourceUtil.WORKVIEW_MESSAGE_LINE, unknownCount,
                allUnknownCount, todoCount, allTodoCount);
    }

    /**
     * Create a dummy label.<br/>
     * 
     * @param toolkit
     *            FormToolkit
     */
    private void setSeparator(FormToolkit toolkit) {
        toolkit.createLabel(form.getBody(), StringUtil.EMPTY);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        // no operation.
    }
}
