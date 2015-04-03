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
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Label;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.Activator;
import tubame.portability.util.resource.ResourceUtil;

/**
 * This is the class of the setting screen top of general-purpose search.<br/>
 */
public class KnowledgePreferencePage extends PreferencePage implements IWorkbenchPreferencePage {

	/**
	 * Logger
	 */
	private static final Logger LOGGER = LoggerFactory.getLogger(KnowledgePreferencePage.class);

	private Button[] autoUseButton;

	private Button[] autoImportedCacheUseButton;

	public static final String PREF_KEY_AUTO_IMPORT_USE = "PREF_KEY_AUTO_IMPORT_USE";

	public static final String PREF_KEY_AUTO_IMPORTED_CACHE_USE = "PREF_KEY_AUTO_IMPORTED_CACHE_USE";

	private static final Boolean DEFAULT_AUTO_IMPORT_USE = Boolean.TRUE;

	private static final Boolean DEFAULT_AUTO_IMPORTED_CACHE_USE = Boolean.TRUE;

	/**
	 * Constructor.<br/>
	 * Nothing in particular.<br/>
	 */
	public KnowledgePreferencePage() {
		noDefaultAndApplyButton();
	}

	/**
	 * Constructor.<br/>
	 * Nothing in particular.<br/>
	 * 
	 * @param title
	 *            Title
	 */
	public KnowledgePreferencePage(String title) {
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
	public KnowledgePreferencePage(String title, ImageDescriptor image) {
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

		// AutoImport Check Area
		Group autoImportcheckGroup = new Group(composite, SWT.NONE);
		GridData gd = new GridData(SWT.FILL, SWT.FILL, true, false);
		autoImportcheckGroup.setLayoutData(gd);
		layout = new GridLayout(3, false);
		autoImportcheckGroup.setLayout(layout);
		// TODO form properties
		autoImportcheckGroup.setText(ResourceUtil.KNOWLEDGE_AUTO_IMPORT_LABEL_GROUP);

		createAutoImportCheckArea(autoImportcheckGroup);
		
		// AutoImportedCache Check Area
		Group autoImportedCachecheckGroup = new Group(composite, SWT.NONE);
		GridData gd2 = new GridData(SWT.FILL, SWT.FILL, true, false);
		autoImportedCachecheckGroup.setLayoutData(gd2);
		layout = new GridLayout(3, false);
		autoImportedCachecheckGroup.setLayout(layout);
		autoImportedCachecheckGroup.setText(ResourceUtil.KNOWLEDGE_AUTO_IMPORTED_CACHE_LABEL_GROUP);

		createAutoImportedCacheCheckArea(autoImportedCachecheckGroup);
		
		return composite;
	}

	private void createAutoImportCheckArea(Group checkGroup) {
		Label label = new Label(checkGroup, SWT.NONE);
		label.setText(ResourceUtil.KNOWLEDGE_AUTO_IMPORT_LABEL);
		boolean autoUse = isAutoImportUse();
		if (autoUse) {
			createPyUseCheckButton(true, false, checkGroup);
		} else {
			createPyUseCheckButton(false, true, checkGroup);
		}
	}

	private void createAutoImportedCacheCheckArea(Group checkGroup) {
		Label label = new Label(checkGroup, SWT.NONE);
		label.setText(ResourceUtil.KNOWLEDGE_AUTO_IMPORTED_CACHE_LABEL);
		boolean autoUse = isAutoImportedCacheUse();
		if (autoUse) {
			createAutoImportedCacheCheckButton(true, false, checkGroup);
		} else {
			createAutoImportedCacheCheckButton(false, true, checkGroup);
		}
	}

	private void createPyUseCheckButton(boolean trueButton, boolean falseButton, Group heckGroup) {
		autoUseButton = new Button[2];
		autoUseButton[0] = new Button(heckGroup, SWT.RADIO);
		autoUseButton[0].setSelection(trueButton);
		autoUseButton[0].setText("true");
		autoUseButton[0].setLocation(50, 250);
		autoUseButton[0].addSelectionListener(new SelectionListener() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				autoUseButton[0].setSelection(true);
				autoUseButton[1].setSelection(false);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		autoUseButton[1] = new Button(heckGroup, SWT.RADIO);
		autoUseButton[1].setSelection(falseButton);
		autoUseButton[1].setText("false");
		autoUseButton[1].setLocation(120, 250);
		autoUseButton[1].pack();
		autoUseButton[1].addSelectionListener(new SelectionListener() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				autoUseButton[0].setSelection(false);
				autoUseButton[1].setSelection(true);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

	}

	private void createAutoImportedCacheCheckButton(boolean trueButton, boolean falseButton, Group heckGroup) {
		autoImportedCacheUseButton = new Button[2];
		autoImportedCacheUseButton[0] = new Button(heckGroup, SWT.RADIO);
		autoImportedCacheUseButton[0].setSelection(trueButton);
		autoImportedCacheUseButton[0].setText("true");
		autoImportedCacheUseButton[0].setLocation(50, 250);
		autoImportedCacheUseButton[0].addSelectionListener(new SelectionListener() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				autoImportedCacheUseButton[0].setSelection(true);
				autoImportedCacheUseButton[1].setSelection(false);
			}

			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetDefaultSelected(SelectionEvent e) {
			}
		});

		autoImportedCacheUseButton[1] = new Button(heckGroup, SWT.RADIO);
		autoImportedCacheUseButton[1].setSelection(falseButton);
		autoImportedCacheUseButton[1].setText("false");
		autoImportedCacheUseButton[1].setLocation(120, 250);
		autoImportedCacheUseButton[1].pack();
		autoImportedCacheUseButton[1].addSelectionListener(new SelectionListener() {
			/**
			 * {@inheritDoc}
			 */
			@Override
			public void widgetSelected(SelectionEvent e) {
				autoImportedCacheUseButton[0].setSelection(false);
				autoImportedCacheUseButton[1].setSelection(true);
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
		if (autoUseButton[0].getSelection() == true) {
			Activator.savePreferences(PREF_KEY_AUTO_IMPORT_USE, Boolean.TRUE.toString());
		} else {
			Activator.savePreferences(PREF_KEY_AUTO_IMPORT_USE, Boolean.FALSE.toString());
		}
		if (autoImportedCacheUseButton[0].getSelection() == true) {
			Activator.savePreferences(PREF_KEY_AUTO_IMPORTED_CACHE_USE, Boolean.TRUE.toString());
		} else {
			Activator.savePreferences(PREF_KEY_AUTO_IMPORTED_CACHE_USE, Boolean.FALSE.toString());
		}
		return super.performOk();
	}

	@Override
	protected void performDefaults() {
		super.performDefaults();
		Activator.savePreferences(PREF_KEY_AUTO_IMPORT_USE, Boolean.TRUE.toString());
		Activator.savePreferences(PREF_KEY_AUTO_IMPORTED_CACHE_USE, Boolean.TRUE.toString());
	}

	@Override
	public boolean performCancel() {
		return super.performCancel();
	}


	public static boolean isAutoImportUse() {
		String autoUse = Activator.getPreferences(PREF_KEY_AUTO_IMPORT_USE);
		if (autoUse == null) {
			return DEFAULT_AUTO_IMPORT_USE;
		}
		return new Boolean(autoUse);
	}

	public static boolean isAutoImportedCacheUse() {
		String autoUse = Activator.getPreferences(PREF_KEY_AUTO_IMPORTED_CACHE_USE);
		if (autoUse == null) {
			return DEFAULT_AUTO_IMPORTED_CACHE_USE;
		}
		return new Boolean(autoUse);
	}

}