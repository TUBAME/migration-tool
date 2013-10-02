/*
 * DocumentationFormPage.java
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
package tubame.knowhow.plugin.ui.editor.multi.documentation;

import java.util.ArrayList;
import java.util.List;


import org.eclipse.jface.viewers.ISelectionChangedListener;
import org.eclipse.jface.viewers.SelectionChangedEvent;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.KeyEvent;
import org.eclipse.swt.events.KeyListener;
import org.eclipse.swt.layout.FillLayout;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Group;
import org.eclipse.swt.widgets.Text;
import org.eclipse.ui.forms.IManagedForm;
import org.eclipse.ui.forms.SectionPart;
import org.eclipse.ui.forms.editor.FormEditor;
import org.eclipse.ui.forms.editor.FormPage;
import org.eclipse.ui.forms.widgets.FormToolkit;
import org.eclipse.ui.forms.widgets.ScrolledForm;
import org.eclipse.ui.forms.widgets.Section;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.logic.KnowhowManagement;
import tubame.knowhow.plugin.model.editor.EntryOperator;
import tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import tubame.knowhow.plugin.ui.editor.multi.EditorDirty;
import tubame.knowhow.plugin.ui.editor.multi.listener.DirtyListener;
import tubame.knowhow.plugin.ui.editor.multi.listener.UpDownListener;
import tubame.knowhow.util.ControlUtil;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Configuration Editor class of know-how document heading creating editor.<br/>
 */
public class DocumentationFormPage extends FormPage {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DocumentationFormPage.class);
    /** Tree view */
    private EditorTreeViewerOperator treeViewer;
    /** Presence or absence of storage */
    private boolean dirty;
    /** "Up" button */
    private Button upButton;
    /** "Down" button */
    private Button downButton;
    /** Transplant title know-how */
    private Text subjectNameText;

    /**
     * Constructor.<br/>
     */
    public DocumentationFormPage(FormEditor editor, String id, String title) {
        super(editor, id, title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void createFormContent(IManagedForm managedForm) {
        ScrolledForm form = managedForm.getForm();
        FormToolkit toolkit = managedForm.getToolkit();

        form.setText(ResourceUtil.documentation);
        form.getBody().setLayout(new GridLayout());

        Section section = toolkit.createSection(form.getBody(),
                Section.TITLE_BAR | Section.DESCRIPTION);
        section.setText(ResourceUtil.documentationInfo);
        section.setDescription(ResourceUtil.documentationDescription);
        section.setLayout(new FillLayout());

        setSectionArea(toolkit, section, managedForm);

    }

    /**
     * Setting of section area.<br/>
     * 
     * @param toolkit
     *            FormToolkit
     * @param section
     *            Section
     * @param managedForm
     *            IManagedForm
     */
    private void setSectionArea(FormToolkit toolkit, Section section,
            IManagedForm managedForm) {

        section.setLayoutData(ControlUtil.getFillGridData(true, true));

        Composite composite = toolkit.createComposite(section);
        SectionPart part = new SectionPart(section);
        composite.setLayout(new GridLayout(1, false));

        Group subjectGroup = ControlUtil.createGroup(composite, SWT.NONE,
                ResourceUtil.subjectName);
        subjectGroup.setLayout(new FillLayout());
        subjectGroup.setLayoutData(ControlUtil.getFillGridData(true, false));
        subjectNameText = new Text(subjectGroup, SWT.BORDER);
        subjectNameText.addKeyListener(new KeyListener() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void keyReleased(KeyEvent e) {
                DocumentationFormPage.LOGGER.info(MessagePropertiesUtil
                        .getMessage(MessagePropertiesUtil.LOG_CHANGE_PORTABILITYKNOWHOW_SUBJECTNAME));
                KnowhowManagement.setSubjectName(subjectNameText.getText());
            }

            /**
             * {@inheritDoc}
             */
            @Override
            public void keyPressed(KeyEvent e) {
                // no operation
            }
        });
        subjectNameText.addKeyListener(new DirtyListener(super.getEditor()));

        Composite lineOneComposite = toolkit.createComposite(composite);
        lineOneComposite.setLayout(new GridLayout(2, false));
        lineOneComposite.setLayoutData(ControlUtil.getFillGridData(true, true));

        // Composite tree
        Composite treeComposite = toolkit.createComposite(lineOneComposite);
        treeComposite.setLayout(new FillLayout());
        treeComposite.setLayoutData(ControlUtil.getFillGridData(true, true));
        // Tree view creation
        try {
            makeTree(treeComposite, managedForm, part);
            addButton(toolkit, lineOneComposite);
            section.setClient(composite);
        } catch (JbmException e) {
            JbmException.outputExceptionLog(LOGGER, e, e.getMessage());
            ErrorDialog.openErrorDialog(PluginUtil.getActiveWorkbenchShell(),
                    e, e.getMessage());
        }

    }

    /**
     * Set the subject name.<br/>
     * 
     */
    public void setKnowhowSubjectName() {
        subjectNameText.setText(KnowhowManagement.getSubjectName());
    }

    /**
     * Add button.<br/>
     * 
     * @param toolkit
     *            FormToolkit
     * @param lineTwoComposite
     *            Composite
     */
    private void addButton(FormToolkit toolkit, Composite lineTwoComposite) {

        // Button composite (right on the first line)
        Composite buttonComposite = toolkit.createComposite(lineTwoComposite);
        buttonComposite.setLayout(new GridLayout(1, false));
        // Button
        buttonComposite.setLayoutData(ControlUtil.getLeftTopGridData());
        upButton = toolkit.createButton(buttonComposite, ResourceUtil.up,
                SWT.PUSH);
        upButton.addSelectionListener(new UpDownListener(true, treeViewer));
        downButton = toolkit.createButton(buttonComposite, ResourceUtil.down,
                SWT.PUSH);
        downButton.addSelectionListener(new UpDownListener(false, treeViewer));
        updownEnabled(false, false);

    }

    /**
     * Make the activation / deactivation process of the up and down buttons.<br/>
     * 
     * @param upEnabled
     *            Up button information
     * @param downEnabled
     *            Down button information
     */
    protected void updownEnabled(boolean upEnabled, boolean downEnabled) {
        upButton.setEnabled(upEnabled);
        downButton.setEnabled(downEnabled);
    }

    /**
     * Create a tree view of heading the list.<br/>
     * 
     * @param treeComposite
     *            Parent composite
     * @param managedForm
     *            Managed form
     * @param part
     *            Section part
     * @throws JbmException
     *             Read failure
     */
    private void makeTree(Composite treeComposite,
            final IManagedForm managedForm, final SectionPart part)
            throws JbmException {

        // Tree view
        treeViewer = new KnowhowEditorTreeViewer(this, treeComposite,
                SWT.H_SCROLL | SWT.V_SCROLL | SWT.FULL_SELECTION | SWT.BORDER);
        // Setting the selection listener
        treeViewer.addSelectionChangedListener(new ISelectionChangedListener() {
            @Override
            public void selectionChanged(SelectionChangedEvent event) {
                managedForm.fireSelectionChanged(part, event.getSelection());
            }
        });
        treeViewer.setEntryListData(createEntryOperation());
    }

    /**
     * Get treeViewer.<br/>
     * 
     * @return treeViewer
     */
    public KnowhowEditorTreeViewer getTreeViewer() {
        return (KnowhowEditorTreeViewer) treeViewer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isDirty() {
        return dirty;
    }

    /**
     * Set the change notification.
     * 
     * @param dirty
     *            dirty
     */
    public void setDirty(boolean dirty) {
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
        return (EditorDirty) super.getEditor();
    }

    /**
     * Generate data to be displayed in the list heading.<br/>
     * If heading data list is empty, it returns an empty list.<br/>
     * 
     * @return Entry List display
     * @throws JbmException
     *             Read failure
     */
    public List<EntryOperator> createEntryOperation() throws JbmException {

        if (!KnowhowManagement.getEntryOperatorConvetData().isEmpty()) {
            return KnowhowManagement.getEntryOperatorConvetData();
        }

        return new ArrayList<EntryOperator>();

    }
}
