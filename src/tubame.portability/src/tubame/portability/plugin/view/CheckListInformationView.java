/*
 * CheckListInformationView.java
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

import org.eclipse.swt.SWT;
import org.eclipse.swt.graphics.Color;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.widgets.ExpandableComposite;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.eclipse.ui.forms.widgets.TableWrapData;
import org.eclipse.ui.forms.widgets.TableWrapLayout;
import org.eclipse.ui.part.ViewPart;

import tubame.portability.exception.JbmException;
import tubame.portability.model.CheckListInformation;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.MessageUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Check List view information display class.<br/>
 * It displays the following items.<br/>
 * 
 * Large item middle item Visual confirmation Item Description Hearing items for
 * inside
 */
public class CheckListInformationView extends ViewPart {

    /**
     * Screen information (Form)
     */
    private ScrolledForm form;
    /**
     * Large item display area
     */
    private Text bigText;
    /**
     * Middle item display area
     */
    private Text middleText;
    /**
     * Visual confirmation item display area
     */
    private Text checkEyeText;
    /**
     * Hearing item display area
     */
    private Text hearingText;
    /**
     * Search procedure display area
     */
    private Text searchText;
    /**
     * Porting factor display area
     */
    private Text factorText;
    /**
     * Difficulty Detailed Display Area
     */
    private Text degreeDetailText;
    /**
     * Recorded required item display area
     */
    private Text appropriateText;
    /**
     * Line census method display area
     */
    private Text investText;

    /**
     * {@inheritDoc}
     */
    @Override
    public void createPartControl(Composite parent) {
        Color white = PluginUtil.getStandardDisplay().getSystemColor(
                SWT.COLOR_WHITE);

        // Form created
        FormToolkit toolkit = new FormToolkit(parent.getDisplay());
        form = toolkit.createScrolledForm(parent);
        form.setText(ResourceUtil.CHECKLISTVIEW_TILE);
        form.getBody().setLayout(new TableWrapLayout());

        // Create a large item section
        bigText = this.createSection(toolkit, form, white,
                ResourceUtil.CHECKLISTVIEW_BIG);

        // Create middle item section
        middleText = this.createSection(toolkit, form, white,
                ResourceUtil.CHECKLISTVIEW_MIDDLE);

        // Visual confirmation creation and configuration
        checkEyeText = this.createSection(toolkit, form, white,
                ResourceUtil.CHECKLISTVIEW_EYE);

        // Hearing section creation and configuration
        hearingText = this.createSection(toolkit, form, white,
                ResourceUtil.CHECKLISTVIEW_HEARING);

        // Create a search procedure section
        searchText = this.createSection(toolkit, form, white,
                ResourceUtil.CHECKLISTVIEW_SEARCH);
        // Create a transplant factors section
        factorText = this.createSection(toolkit, form, white,
                ResourceUtil.CHECKLISTVIEW_FACTOR);
        // Create difficulty detail section
        degreeDetailText = this.createSection(toolkit, form, white,
                ResourceUtil.CHECKLISTVIEW_DEGREE);
        // Create a recorded mandatory section
        appropriateText = this.createSection(toolkit, form, white,
                ResourceUtil.CHECKLISTVIEW_APP);
        // Create a survey methodology section
        investText = this.createSection(toolkit, form, white,
                ResourceUtil.CHECKLISTVIEW_INV);

        // Show an empty message
        setEmptyMessage();
    }

    /**
     * Generate section.<br/>
     * Generate a section with a title.<br/>
     * 
     * @param toolkit
     *            Widget
     * @param scrolledForm
     *            Form
     * @param title
     *            Title
     */
    private void createSection(FormToolkit toolkit, ScrolledForm scrolledForm,
            String title) {
        Section section = toolkit.createSection(scrolledForm.getBody(),
                ExpandableComposite.TITLE_BAR);
        section.setText(title);
        section.setLayoutData(new TableWrapData(TableWrapData.FILL_GRAB));
    }

    /**
     * Generate text of color.<br/>
     * 
     * @param scrolledForm
     *            Form
     * @param backColor
     *            White
     * @return Text
     */
    private Text createText(ScrolledForm scrolledForm, Color backColor) {
        Text text = new Text(scrolledForm.getBody(), SWT.WRAP | SWT.READ_ONLY);
        text.setBackground(backColor);
        return text;
    }

    /**
     * Create and display section.<br/>
     * 
     * @param toolkit
     *            Widget
     * @param scrolledForm
     *            Form
     * @param whiteColor
     *            Fair complexion
     * @param title
     *            Title
     * @return Text
     */
    private Text createSection(FormToolkit toolkit, ScrolledForm scrolledForm,
            Color whiteColor, String title) {
        // Create a section
        this.createSection(toolkit, scrolledForm, title);
        // Create a text
        Text text = createText(scrolledForm, whiteColor);
        // Add separator
        setSeparator(toolkit);
        return text;
    }

    /**
     * Show an empty message.<br/>
     * View checklist information there is no information.<br/>
     * 
     */
    private void setEmptyMessage() {
        CheckListInformation message = new CheckListInformation();
        message.setBigText(StringUtil.EMPTY);
        message.setMiddleText(StringUtil.EMPTY);
        message.setCheckEyeText(StringUtil.EMPTY);
        message.setHearingText(StringUtil.EMPTY);
        message.setSearchText(StringUtil.EMPTY);
        message.setFactorText(StringUtil.EMPTY);
        message.setDegreeDetailText(StringUtil.EMPTY);
        message.setAppropriateText(StringUtil.EMPTY);
        message.setInvestText(StringUtil.EMPTY);
    }

    /**
     * Set a separator Form.<br/>
     * 
     * @param toolkit
     *            Form
     */
    private void setSeparator(FormToolkit toolkit) {
        toolkit.createLabel(form.getBody(), StringUtil.EMPTY);
    }

    /**
     * The update from outside the checklist information.<br/>
     * 
     * @param message
     *            Checklist information object
     */
    public static void out(CheckListInformation message) {

        CheckListInformationView instance = CheckListInformationView
                .getInstance();
        if (instance != null) {
            Color white = PluginUtil.getStandardDisplay().getSystemColor(
                    SWT.COLOR_WHITE);
            instance.bigText.setBackground(white);
            instance.bigText.setText(message.getBigText());
            instance.middleText.setBackground(white);
            instance.middleText.setText(message.getMiddleText());
            instance.checkEyeText.setBackground(white);
            instance.checkEyeText.setText(message.getCheckEyeText());
            instance.hearingText.setBackground(white);
            instance.hearingText.setText(message.getHearingText());
            instance.searchText.setBackground(white);
            instance.searchText.setText(message.getSearchText());
            instance.factorText.setBackground(white);
            instance.factorText.setText(message.getFactorText());
            instance.degreeDetailText.setBackground(white);
            instance.degreeDetailText.setText(message.getDegreeDetailText());
            instance.appropriateText.setBackground(white);
            instance.appropriateText.setText(message.getAppropriateText());
            instance.investText.setBackground(white);
            instance.investText.setText(message.getInvestText());
            instance.form.reflow(true);
        }
    }

    /**
     * Get an instance of the checklist information View.<br/>
     * 
     * @return Checklist information View instance
     */
    public static CheckListInformationView getInstance() {
        try {
            return PluginUtil.getCheckListInformationView();
        } catch (JbmException e) {
            PluginUtil.viewErrorDialog(ResourceUtil.VIEW_CHECKLISTINFORMATION,
                    MessageUtil.ERR_VIEW_OUTPUT, e);
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setFocus() {
        // no operation.

    }
}
