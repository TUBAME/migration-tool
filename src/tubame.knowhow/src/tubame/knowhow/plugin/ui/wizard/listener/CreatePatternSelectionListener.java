/*
 * CreatePatternSelectionListener.java
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
package tubame.knowhow.plugin.ui.wizard.listener;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.ui.wizard.commencement.CommencementPage;

/**
 * Listener class that Enable group settings is selected.<br/>
 */
public class CreatePatternSelectionListener implements SelectionListener {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CreatePatternSelectionListener.class);
    /** Selected target group */
    private Group selectedGroup;
    /** Non-selected target group */
    private Group anotherGroup;

    /**
     * Constructor.<br/>
     * 
     * @param selectedGroup
     *            Selection group
     * @param anotherGroup
     *            Non-selected group
     * @param firstMaintenanceKnowhowPage
     *            Initial page
     */
    public CreatePatternSelectionListener(Group selectedGroup,
            Group anotherGroup, CommencementPage firstMaintenanceKnowhowPage) {
        this.selectedGroup = selectedGroup;
        this.anotherGroup = anotherGroup;
        CreatePatternSelectionListener.LOGGER
                .debug("[selectedGroup]" + selectedGroup + "[anotherGroup]"
                        + anotherGroup + "[firstMaintenanceKnowhowPage]"
                        + firstMaintenanceKnowhowPage);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        this.selectedGroup.setEnabled(true);
        this.childrenControl(selectedGroup.getChildren(), true);
        this.anotherGroup.setEnabled(false);
        this.childrenControl(anotherGroup.getChildren(), false);

    }

    /**
     * Set the controls of the child element of the group.<br/>
     * Set the state of activation / deactivation of the child element of the
     * group.<br/>
     * 
     * @param children
     *            Control target element group
     * @param controlFlag
     *            Control flag
     */
    private void childrenControl(Control[] children, boolean controlFlag) {
        for (Control control : children) {
            control.setEnabled(controlFlag);
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // no operation

    }
}
