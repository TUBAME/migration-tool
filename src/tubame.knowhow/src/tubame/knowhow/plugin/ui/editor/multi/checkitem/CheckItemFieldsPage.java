/*
 * CheckItemFieldsPage.java
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
package tubame.knowhow.plugin.ui.editor.multi.checkitem;

import tubame.common.util.CmnStringUtil;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.editor.multi.EditorDirty;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Check Item Details tab configuration class know-how of the editor.<br/>
 * Configuration list page from ({@link CheckItemFieldsBlock}).<br/>
 */
public class CheckItemFieldsPage extends FormPage {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CheckItemFieldsPage.class);
    /** Check items section */
    private CheckItemFieldsBlock checkItemFieldsBlock;
    /** Search information sect */
    private SearchInfoFieldsBlock saerchInfoFieldsBlock;
    /** PortabilityKnowhowListViewOperation. */
    private PortabilityKnowhowListViewOperation tempKnowhowListViewData;
    /** Presence or absence of storage */
    private boolean dirty;

    /**
     * Constructor.<br/>
     * 
     * @param editor
     *            Know-how editor
     * @param id
     *            ID
     * @param title
     *            Title
     */
    public CheckItemFieldsPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
        checkItemFieldsBlock = new CheckItemFieldsBlock(this);
        saerchInfoFieldsBlock = new SearchInfoFieldsBlock(this);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createFormContent(IManagedForm managedForm) {
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();

        form.setText(ResourceUtil.checkItem);
        form.getBody().setLayout(new GridLayout(2, false));

        Section checkItemSection = toolkit.createSection(form.getBody(),
                Section.TITLE_BAR | Section.DESCRIPTION);
        checkItemSection.setText(ResourceUtil.checkItemInfo);
        checkItemSection.setDescription(ResourceUtil.checkItemInfoDescription);
        checkItemSection.setLayout(new GridLayout(2, false));

        checkItemFieldsBlock.createSection(toolkit, checkItemSection,
                managedForm);

        Section searchInfoSection = toolkit.createSection(form.getBody(),
                Section.TITLE_BAR | Section.DESCRIPTION);
        searchInfoSection.setText(ResourceUtil.searchInfo);
        searchInfoSection.setDescription(ResourceUtil.searchInfoDescription);
        searchInfoSection.setLayout(new GridLayout(2, false));

        saerchInfoFieldsBlock.createSection(toolkit, searchInfoSection,
                managedForm);
        form.reflow(true);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirty() {
        CheckItemFieldsPage.LOGGER.debug(CmnStringUtil.EMPTY);
        return dirty;
    }

    /**
     * Set dirty.<br/>
     * 
     * @param dirty
     *            Change information
     */
    public void setDirty(boolean dirty) {
        CheckItemFieldsPage.LOGGER.debug("[dirty]" + dirty);
        this.dirty = dirty;
        // Send change notification
        super.setActive(dirty);
    }

    /**
     * Get Editor.<br/>
     * 
     * @return Editor
     */
    public EditorDirty getEditorEitry() {
        CheckItemFieldsPage.LOGGER.debug(CmnStringUtil.EMPTY);
        return (EditorDirty) super.getEditor();
    }

    /**
     * Set to check items block the data registered by the know-how entry view.<br/>
     * 
     * @param knowhowListViewData
     *            PortabilityKnowhowListViewData
     */
    public void updateCheckItemBrock(
            PortabilityKnowhowListViewOperation knowhowListViewData) {
        CheckItemFieldsPage.LOGGER.debug("[knowhowListViewData]"
                + knowhowListViewData);

        checkItemFieldsBlock.setRegisterItemData(knowhowListViewData
                .getKnowhowViewType());
        checkItemFieldsBlock.setEnableGroup(true);
        // Set the data in the child hierarchy
        updateSearchInfoBrock(knowhowListViewData);
        this.tempKnowhowListViewData = knowhowListViewData;
    }

    /**
     * Set to search information block search information data of the check
     * items under.<br/>
     * 
     * @param knowhowListViewData
     *            PortabilityKnowhowListViewData
     */
    private void updateSearchInfoBrock(
            PortabilityKnowhowListViewOperation knowhowListViewData) {
        if (!knowhowListViewData.getChildList().isEmpty()) {
            saerchInfoFieldsBlock.setEnableGroup(true);
            for (PortabilityKnowhowListViewOperation childData : knowhowListViewData
                    .getChildList()) {
                saerchInfoFieldsBlock.setRegisterItemData(childData
                        .getKnowhowViewType());
            }
        } else {
            saerchInfoFieldsBlock.setEnableGroup(false);
        }
    }

    /**
     * Clear data of the check item information tab.<br/>
     * 
     */
    public void clear() {
        CheckItemFieldsPage.LOGGER.debug(CmnStringUtil.EMPTY);
        if (checkItemFieldsBlock.isClear() || saerchInfoFieldsBlock.isClear()) {
            checkItemFieldsBlock.clear();
            checkItemFieldsBlock.setEnableGroup(false);
            saerchInfoFieldsBlock.clear();
            saerchInfoFieldsBlock.setEnableGroup(false);
        }
    }

    /**
     * Get checkItemFieldsBlock.<br/>
     * 
     * @return checkItemFieldsBlock
     */
    public CheckItemFieldsBlock getCheckItemFieldsBlock() {
        CheckItemFieldsPage.LOGGER.debug(CmnStringUtil.EMPTY);
        return checkItemFieldsBlock;
    }

    /**
     * Get saerchInfoFieldsBlock.<br/>
     * 
     * @return saerchInfoFieldsBlock
     */
    public SearchInfoFieldsBlock getSaerchInfoFieldsBlock() {
        CheckItemFieldsPage.LOGGER.debug(CmnStringUtil.EMPTY);
        return saerchInfoFieldsBlock;
    }

    /**
     * Make the process of rewriting the data that has changed.<br/>
     * 
     */
    public void temporaryStorage() {
        CheckItemFieldsPage.LOGGER.debug(CmnStringUtil.EMPTY);
        // Data update process
        if (this.tempKnowhowListViewData != null) {
            checkItemFieldsBlock.updateData(tempKnowhowListViewData);
            if (!tempKnowhowListViewData.getChildList().isEmpty()) {
                saerchInfoFieldsBlock.updateData(tempKnowhowListViewData
                        .getChildList().get(0));
            }
        }
    }
}
