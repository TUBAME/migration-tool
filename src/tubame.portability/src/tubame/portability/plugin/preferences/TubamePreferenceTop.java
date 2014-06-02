/*
 * WSearchPreferenceTop.java
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
package tubame.portability.plugin.preferences;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.Activator;

/**
 * This is the class of the setting screen top of general-purpose search.<br/>
 */
public class TubamePreferenceTop extends PreferencePage implements
        IWorkbenchPreferencePage {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(TubamePreferenceTop.class);

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     */
    public TubamePreferenceTop() {
        noDefaultAndApplyButton();
    }

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param title
     *            Title
     */
    public TubamePreferenceTop(String title) {
        super(title);
    }

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param title
     *            Title
     * @param image
     *            Image
     */
    public TubamePreferenceTop(String title, ImageDescriptor image) {
        super(title, image);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IWorkbench workbench) {
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createContents(Composite parent) {
        initializeDialogUnits(parent);
        Composite composite = new Composite(parent, SWT.NONE);
//        GridLayout layout = new GridLayout(1, false);
//        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//        composite.setLayout(layout);
//        Group pathsGroup = createGroup(composite, 1);
//        pathsGroup.setText(Activator
//                .getResourceString(TubamePreferenceTop.class.getName()
//                        + ".label.description"));
//        Label pathsDescription = new Label(pathsGroup, SWT.NONE);
//        pathsDescription.setText(Activator
//                .getResourceString(TubamePreferenceTop.class.getName()
//                        + ".label.descriptionText"));
        return composite;
    }

    /**
     * Create a group.<br/>
     * 
     * @param composite
     *            Composite
     * @param column
     *            Number of columns
     * @return Group
     */
    private Group createGroup(Composite composite, int column) {
        GridLayout layout;
        Group group = new Group(composite, SWT.NONE);
        GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
        group.setLayoutData(gd);
        layout = new GridLayout(column, false);
        group.setLayout(layout);
        return group;
    }
}