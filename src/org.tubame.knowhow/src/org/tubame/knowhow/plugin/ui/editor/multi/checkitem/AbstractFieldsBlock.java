/*
 * AbstractFieldsBlock.java
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
package org.tubame.knowhow.plugin.ui.editor.multi.checkitem;

import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.Section;
import org.tubame.knowhow.plugin.model.view.AbstractViewType;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import org.tubame.knowhow.util.ControlUtil;

/**
 * Abstract class know-how XML editor (check items tab).<br/>
 */
public abstract class AbstractFieldsBlock {

    /** The width of the section. */
    protected static final int WIDTHHINT = 300;
    /** Check item information page */
    private CheckItemFieldsPage checkItemFieldsPage;

    /**
     * Constructor.<br/>
     * 
     * @param checkItemFieldsPage
     *            Check items page information
     */
    public AbstractFieldsBlock(CheckItemFieldsPage checkItemFieldsPage) {
        this.checkItemFieldsPage = checkItemFieldsPage;
    }

    /**
     * Set the layout for a section, Create a each form part.<br/>
     * 
     * @param toolkit
     *            FormToolkit
     * @param section
     *            Section
     * @param managedForm
     *            IManagedForm
     */
    public void createSection(FormToolkit toolkit, Section section,
            IManagedForm managedForm) {

        section.setLayoutData(ControlUtil.getAutoSizingHorizontalGridData());
        Composite composite = toolkit.createComposite(section);
        composite.setLayout(new GridLayout(2, false));

        makeFormPart(composite);
        setEnableGroup(false);
        section.setClient(composite);
    }

    /**
     * Create a form part.<br/>
     * 
     * @param composite
     *            Composite
     */
    protected abstract void makeFormPart(Composite composite);

    /**
     * Set the data registered in the knowledge entry view.<br/>
     * 
     * @param abstractViewType
     *            Data entry
     */
    public abstract void setRegisterItemData(AbstractViewType abstractViewType);

    /**
     * Set the activity and non-activity of each block.<br/>
     * 
     * @param enableFlag
     *            Active and non-active flag
     */
    public abstract void setEnableGroup(boolean enableFlag);

    /**
     * Clear each block information.<br/>
     * 
     */
    public abstract void clear();

    /**
     * Determines whether the state can be cleared each block information.<br/>
     * 
     * @return true:Clear target false:Clear excluded
     */
    public abstract boolean isClear();

    /**
     * Get the know-how editor.<br/>
     * 
     * @return Know-how editor
     */
    protected FormEditor getEditor() {
        return checkItemFieldsPage.getEditor();
    }

    /**
     * Updated the check item information detailed data.<br/>
     * 
     * @param tempKnowhowListViewData
     *            PortabilityKnowhowListViewOperation
     */
    public abstract void updateData(
            PortabilityKnowhowListViewOperation tempKnowhowListViewData);

}
