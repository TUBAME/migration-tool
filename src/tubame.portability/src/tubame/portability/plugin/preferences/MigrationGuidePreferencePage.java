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


import java.io.IOException;

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
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.IWorkbench;
import org.eclipse.ui.IWorkbenchPreferencePage;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.Activator;
import tubame.portability.logic.GuideViewFacade;
import tubame.portability.plugin.wizard.BrowseDirectoryButtonSelectionListener;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.PythonUtil;
import tubame.portability.util.resource.ApplicationPropertyUtil;

/**
 * This is the class of the setting screen top of general-purpose search.<br/>
 */
public class MigrationGuidePreferencePage extends PreferencePage implements
        IWorkbenchPreferencePage {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(MigrationGuidePreferencePage.class);
    
	private Text guideIndexPath;

	public static final String PREF_KEY_GUIDE_INDEX_PATH ="PREF_KEY_GUIDE_INDEX_PATH";

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     */
    public MigrationGuidePreferencePage() {
        noDefaultAndApplyButton();
    }

    /**
     * Constructor.<br/>
     * Nothing in particular.<br/>
     * 
     * @param title
     *            Title
     */
    public MigrationGuidePreferencePage(String title) {
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
    public MigrationGuidePreferencePage(String title, ImageDescriptor image) {
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
//        Composite composite = new Composite(parent, SWT.NONE);
//        GridLayout layout = new GridLayout(1, false);
//        composite.setLayoutData(new GridData(SWT.FILL, SWT.FILL, true, true));
//        composite.setLayout(layout);
//        Group pathsGroup = createGroup(composite, 1);
//        pathsGroup.setText(Activator
//                .getResourceString(PythonPreferencePage.class.getName()
//                        + ".label.description"));
//        Label pathsDescription = new Label(pathsGroup, SWT.NONE);
//        pathsDescription.setText(Activator
//                .getResourceString(PythonPreferencePage.class.getName()
//                        + ".label.descriptionText"));
        
        
        Group group = new Group(composite, SWT.SHADOW_NONE);
        group.setText(Activator .getResourceString(MigrationGuidePreferencePage.class.getName()
              + ".label.description"));
        group.setLayoutData(new GridData(GridData.FILL_HORIZONTAL));
        group.setLayout(new GridLayout(2, false));


        // Converted source file output destination selection text
        guideIndexPath = new Text(group, SWT.BORDER);
        String preferences = Activator.getPreferences(PREF_KEY_GUIDE_INDEX_PATH);
        if(preferences == null){
        	try {
        		guideIndexPath.setText(PluginUtil.getResolvedPluginDir() 
				        + ApplicationPropertyUtil.GUIDE_FILE_PATH);
			} catch (IOException e) {
				new IllegalStateException(e.getMessage());
			}
        }else{
        	guideIndexPath.setText(preferences);
        }
        
        guideIndexPath.setLayoutData(new GridData(
                GridData.FILL_HORIZONTAL));
       
        guideIndexPath.addModifyListener(new ModifyListener() {
			
			@Override
			public void modifyText(ModifyEvent arg0) {
				
				
			}
		});

        // Converted source file output destination selection button
        Button button = new Button(group, SWT.NULL);
        button.setText(Activator .getResourceString(MigrationGuidePreferencePage.class.getName()
                + ".button.label"));
        button.addSelectionListener(new SelectionListener() {
			
			@Override
			public void widgetSelected(SelectionEvent arg0) {
				FileDialog dialog = new FileDialog(getShell(), SWT.SINGLE);
	            String[] extensions = { "*.html" };
	            dialog.setFilterExtensions(extensions);
				String result = dialog.open();
                if (result == null || result.trim().length() == 0) {
                    return;
                }
                
                guideIndexPath.setText(result);
			}
			
			@Override
			public void widgetDefaultSelected(SelectionEvent arg0) {
				
			}
		});
        
        return composite;
    }

    @Override
	public boolean performOk() {
    	LOGGER.debug("INDEX_GUIDE_FILE_PATH :" + guideIndexPath.getText());
    	Activator.savePreferences(PREF_KEY_GUIDE_INDEX_PATH, guideIndexPath.getText());
    	GuideViewFacade.INDEX_GUIDE_FILE_PATH = guideIndexPath.getText();
		// TODO Auto-generated method stub
		return super.performOk();
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