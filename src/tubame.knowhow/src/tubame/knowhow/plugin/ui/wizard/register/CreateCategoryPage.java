/*
 * CreateCategoryPage.java
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
package tubame.knowhow.plugin.ui.wizard.register;

import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;

import tubame.knowhow.util.ControlUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Concrete class of category Registration Wizard page.<br/>
 */
public class CreateCategoryPage extends AbstractCreateKnowhowPage {

    /** Number of lines recorded */
    private Button appropriateLine;

    /**
     * Constructor.<br/>
     * 
     * @param pageName
     *            Page information
     * @param wizardName
     *            Wizard information
     */
    protected CreateCategoryPage(String pageName, String wizardName) {
        super(pageName, wizardName);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void createControl(Composite parent) {

        Composite composite = new Composite(parent, SWT.NONE);
        composite.setLayout(new GridLayout());
        super.createControl(composite);
        super.setLabelText(ResourceUtil.categoryNameLabel);
        setAppropriateLineArea(composite);
        init();

        super.setControl(composite);
    }

    /**
     * The set the initial processing.<br/>
     * 
     */
    private void init() {
        appropriateLine.setSelection(true);
    }

    /**
     * Create a line number calculation area.<br/>
     * 
     * @param composite
     *            Composite
     */
    private void setAppropriateLineArea(Composite composite) {

        Group appropriateLineAreaGroup = super.getParentGroup();

        Label appropriateLineLabel = new Label(appropriateLineAreaGroup,
                SWT.NULL);
        appropriateLineLabel.setText(ResourceUtil.appropriateLineLabel);
        appropriateLineLabel.setLayoutData(ControlUtil.getTopGridData());
        appropriateLine = new Button(appropriateLineAreaGroup, SWT.CHECK);
        appropriateLine.setLayoutData(ControlUtil.getTopGridData());
    }

    /**
     * Get the line number calculation.<br/>
     * 
     * @return Line number calculation check box
     */
    public boolean isAppropriateLine() {
        return appropriateLine.getSelection();
    }

}
