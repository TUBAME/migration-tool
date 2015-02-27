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

import java.io.File;

import org.eclipse.jface.preference.PreferencePage;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.ModifyEvent;
import org.eclipse.swt.events.ModifyListener;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.FileDialog;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.Activator;
import tubame.portability.util.PythonUtil;

/**
 * This is the class of the setting screen top of general-purpose search.<br/>
 */
public class PythonPreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(PythonPreferencePage.class);

	private Text pyPathText;

	private Button[] pyUseButton;

	private Button browsebutton;

	public static final String PREF_KEY_PY_USE = "PREF_KEY_PY_USE";

	public static final String PREF_KEY_PY_RUNTIME_PATH = "PREF_KEY_PY_RUNTIME_PATH";
	
	private static final Boolean DEFAULT_PY_USE = Boolean.FALSE;

	/**
	 * Constructor.<br/>
	 * Nothing in particular.<br/>
	 */
	public PythonPreferencePage() {
		noDefaultAndApplyButton();
	}

	/**
	 * Constructor.<br/>
	 * Nothing in particular.<br/>
	 * 
	 * @param title
	 *            Title
	 */
	public PythonPreferencePage(String title) {
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
	public PythonPreferencePage(String title, ImageDescriptor image) {
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
		GridLayout layout = new GridLayout(1, false);
		composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
		composite.setLayout(layout);
		// Composite composite = new Composite(parent, SWT.NONE);
		// GridLayout layout = new GridLayout(1, false);
		// composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true,
		// true));
		// composite.setLayout(layout);
		// Group pathsGroup = createGroup(composite, 1);
		// pathsGroup.setText(Activator
		// .getResourceString(PythonPreferencePage.class.getName()
		// + ".label.description"));
		// Label pathsDescription = new Label(pathsGroup, SWT.NONE);
		// pathsDescription.setText(Activator
		// .getResourceString(PythonPreferencePage.class.getName()
		// + ".label.descriptionText"));

		// Cache Check Area
		Group pyUsecheckGroup = new Group(composite, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		pyUsecheckGroup.setLayoutData(gd);
		layout = new GridLayout(4, false);
		pyUsecheckGroup.setLayout(layout);
		pyUsecheckGroup.setText("use local python");

		createPyUseCheckArea(pyUsecheckGroup);

		// Group group = new Group(composite, SWT.SHADOW_NONE);
		// group.setText(Activator
		// .getResourceString(PythonPreferencePage.class.getName()
		// + ".label.description"));
		// group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		// group.setLayout(new GridLayout(2, false));

		// Converted source file output destination selection text
		pyPathText = new Text(pyUsecheckGroup, SWT.BORDER);
		pyPathText.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
		
		String preferences = Activator.getPreferences(PREF_KEY_PY_RUNTIME_PATH);
		if (preferences != null) {
			pyPathText.setText(preferences);
		} else {
			pyPathText.setText(PythonUtil.PY_RUNTIME_PATH);
		}

		pyPathText.addModifyListener(new ModifyListener() {

			@Override
			public void modifyText(ModifyEvent arg0) {
				// TODO Auto-generated method stub

			}
		});
		

		// Converted source file output destination selection button
		browsebutton = new Button(pyUsecheckGroup, SWT.NULL);
		browsebutton.setText(Activator.getResourceString(PythonPreferencePage.class.getName() + ".button.label"));
		browsebutton.addSelectionListener(new SelectionListener() {

			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
				String result = dialog.open();
				if (result == null || result.trim().length() == 0) {
					return;
				}
				pyPathText.setText(result);
			}

			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {

			}
		});

		if(isPyUse()){
			pyPathText.setVisible(true);
			browsebutton.setVisible(true);
		}else{
			pyPathText.setVisible(false);
			browsebutton.setVisible(false);
		}
		return composite;
	}

	private void createPyUseCheckArea(Group pyUsecheckGroup) {
		boolean pyUse = isPyUse();
		if (pyUse) {
			createPyUseCheckButton(true,false,pyUsecheckGroup);
		}else{
			createPyUseCheckButton(false,true,pyUsecheckGroup);
		}

	}

	private void createPyUseCheckButton(boolean trueButton, boolean falseButton,Group pyUsecheckGroup) {
		 pyUseButton = new Button[2];
    	 pyUseButton[0] = new Button(pyUsecheckGroup, SWT.RADIO);
    	 pyUseButton[0].setSelection(trueButton);
    	 pyUseButton[0].setText("true");
    	 pyUseButton[0].setLocation(50, 250);
    	 pyUseButton[0].addSelectionListener(new SelectionListener() {
             /**
              * {@inheritDoc}
              */
             @Override
             public void widgetSelected(SelectionEvent e) {
            	 pyUseButton[0].setSelection(true);
            	 pyUseButton[1].setSelection(false);
            	 pyPathText.setVisible(true);
            	 browsebutton.setVisible(true);
             }

             /**
              * {@inheritDoc}
              */
             @Override
             public void widgetDefaultSelected(SelectionEvent e) {
             }
         });
    	 

    	 pyUseButton[1] = new Button(pyUsecheckGroup, SWT.RADIO);
    	 pyUseButton[1].setSelection(falseButton);
    	 pyUseButton[1].setText("false");
    	 pyUseButton[1].setLocation(120, 250);
    	 pyUseButton[1].pack();
    	 pyUseButton[1].addSelectionListener(new SelectionListener() {
             /**
              * {@inheritDoc}
              */
             @Override
             public void widgetSelected(SelectionEvent e) {
            	 pyUseButton[0].setSelection(false);
            	 pyUseButton[1].setSelection(true);
            	 pyPathText.setVisible(false);
            	 browsebutton.setVisible(false);
             }

             /**
              * {@inheritDoc}
              */
             @Override
             public void widgetDefaultSelected(SelectionEvent e) {
             }
         });
    	 
	}

	@Override
	public boolean performOk() {
		if (pyUseButton[0].getSelection() == true) {
			File file = new File(pyPathText.getText());
			if (!file.exists()) {
				this.setErrorMessage("invalid python path");
				return false;
			}
			Activator.savePreferences(PREF_KEY_PY_RUNTIME_PATH, pyPathText.getText());
			Activator.savePreferences(PREF_KEY_PY_USE, Boolean.TRUE.toString());
		} else {
			Activator.savePreferences(PREF_KEY_PY_USE, Boolean.FALSE.toString());
		}
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		Activator.savePreferences(PREF_KEY_PY_RUNTIME_PATH, PythonUtil.PY_RUNTIME_PATH);
		Activator.savePreferences(PREF_KEY_PY_USE, Boolean.FALSE.toString());
	}

	@Override
	public boolean performCancel() {
		return super.performCancel();
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
	
	public static boolean isPyUse(){
		String pyUse = Activator.getPreferences(PREF_KEY_PY_USE);
		if (pyUse == null) {
			return DEFAULT_PY_USE;
		}
		return new Boolean(pyUse);
	}
}