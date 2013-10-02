/*
 * WSearchResultSet.java
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
package tubame.wsearch.ui.dialog;

import org.eclipse.jface.dialogs.Dialog;
import org.eclipse.swt.SWT;
import org.eclipse.swt.events.SelectionAdapter;
import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.graphics.Point;
import org.eclipse.swt.layout.GridData;
import org.eclipse.swt.layout.GridLayout;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Composite;
import org.eclipse.swt.widgets.Control;
import org.eclipse.swt.widgets.Label;
import org.eclipse.swt.widgets.List;
import org.eclipse.swt.widgets.Shell;
import tubame.wsearch.Activator;

/**
 * display the detailed information of a general-purpose search results.<br/>
 */
public class WSearchResultSet extends Dialog {

    /**
     * The height of the screen
     */
    private final int assessmentResultDetailHeight = 320;

    /**
     * Width of the screen
     */
    private final int assessmentResultDetailWidth = 400;

    /**
     * Data shift
     */
    protected List leftList;
    /**
     * Data shift
     */
    protected List rightList;
    /**
     * Data shift
     */
    protected Button left2RightButton;
    /**
     * Data shift
     */
    protected Button left2RightAllButton;
    /**
     * Data shift
     */
    protected Button right2LeftButton;
    /**
     * Data shift
     */
    protected Button right2LeftAllButton;
    /**
     * Data shift
     */
    protected String leftListTitle;
    /**
     * Data shift
     */
    protected String rightListTitle;
    /**
     * Data shift
     */
    protected String left2Right;
    /**
     * Data shift
     */
    protected String left2RightAll;
    /**
     * Data shift
     */
    protected String right2Left;
    /**
     * Data shift
     */
    protected String right2LeftAll;

    /**
     * Title
     */
    private String title;

    /**
     * Column list result
     */
    private Object[] defaultList;

    /**
     * Get the result column list.<br/>
     * 
     * @return Column list result
     */
    public String[] getDefultList() {
        return (String[]) defaultList;
    }

    /**
     * Constructor.<br/>
     * Do not do anything.<br/>
     * 
     * @param parentShell
     *            Parent shell
     * @param filterList
     *            Column list result
     * @param title
     *            Title
     */
    public WSearchResultSet(Shell parentShell, Object[] filterList, String title) {
        super(parentShell);
        defaultList = filterList;
        this.title = title;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Point getInitialSize() {
        return new Point(assessmentResultDetailWidth,
                assessmentResultDetailHeight);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void configureShell(Shell newShell) {
        super.configureShell(newShell);
        newShell.setText(title);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected void okPressed() {
        defaultList = leftList.getItems();
        super.okPressed();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected Control createDialogArea(Composite parent) {
        Composite composite = (Composite) super.createDialogArea(parent);
        composite.setLayout(new GridLayout(3, false));
        Label leftLabel = new Label(composite, SWT.NONE);
        leftLabel.setText(Activator.getResourceString(WSearchResultSet.class
                .getName() + ".label.VisibleList"));
        new Label(composite, SWT.NONE);
        Label rightLabel = new Label(composite, SWT.NONE);
        rightLabel.setText(Activator.getResourceString(WSearchResultSet.class
                .getName() + ".label.InvisibleList"));
        leftList = new List(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        GridData gd_list = new GridData(SWT.CENTER, SWT.FILL, false, false, 1,
                5);
        gd_list.heightHint = 180;
        gd_list.widthHint = 100;
        leftList.setLayoutData(gd_list);
        leftList.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                rightList.deselectAll();
                setButtonStatus();
            }
        });

        // All removal
        left2RightAllButton = new Button(composite, SWT.NONE);
        left2RightAllButton.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                shiftAllToRightAction();
            }
        });
        left2RightAllButton.setText(Activator
                .getResourceString(WSearchResultSet.class.getName()
                        + ".label.RemoveAll"));
        rightList = new List(composite, SWT.MULTI | SWT.BORDER | SWT.V_SCROLL);
        GridData gd_list_1 = new GridData(SWT.CENTER, SWT.FILL, false, false,
                1, 5);
        gd_list_1.heightHint = 180;
        gd_list_1.widthHint = 100;
        rightList.setLayoutData(gd_list_1);
        rightList.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                leftList.deselectAll();
                setButtonStatus();
            }
        });

        // Initial data
        for (Object filter : defaultList) {
            leftList.add((String) filter);
        }

        // Remove
        left2RightButton = new Button(composite, SWT.NONE);
        left2RightButton.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                shiftToRightAction();
            }
        });
        left2RightButton.setText(Activator
                .getResourceString(WSearchResultSet.class.getName()
                        + ".label.Remove"));

        // Add
        right2LeftButton = new Button(composite, SWT.NONE);
        right2LeftButton.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                shiftToLeftAction();
            }
        });
        right2LeftButton.setText(Activator
                .getResourceString(WSearchResultSet.class.getName()
                        + ".label.Add"));

        // Add all
        right2LeftAllButton = new Button(composite, SWT.NONE);
        right2LeftAllButton.addSelectionListener(new SelectionAdapter() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void widgetSelected(SelectionEvent e) {
                shiftAllToLeftAction();
            }
        });
        right2LeftAllButton.setText(Activator
                .getResourceString(WSearchResultSet.class.getName()
                        + ".label.AddAll"));
        setButtonStatus();
        return composite;
    }

    /**
     * Control the button state.<br/>
     */
    private void setButtonStatus() {
        if (leftList.getSelectionCount() > 0) {
            left2RightButton.setEnabled(true);
        } else {
            left2RightButton.setEnabled(false);
        }
        if (leftList.getItemCount() > 0) {
            left2RightAllButton.setEnabled(true);
            Button okButton = super.getButton(OK);
            if (okButton != null) {
                okButton.setEnabled(true);
            }
        } else {
            left2RightAllButton.setEnabled(false);
            Button okButton = super.getButton(OK);
            if (okButton != null) {
                okButton.setEnabled(false);
            }
        }
        if (rightList.getSelectionCount() > 0) {
            right2LeftButton.setEnabled(true);
        } else {
            right2LeftButton.setEnabled(false);
        }
        if (rightList.getItemCount() > 0) {
            right2LeftAllButton.setEnabled(true);
        } else {
            right2LeftAllButton.setEnabled(false);
        }
    }

    /**
     * Control the left-hand side data.<br/>
     */
    private void computeLeftData() {
        String selectedItems[] = leftList.getItems();
        for (int i = 0; i < selectedItems.length; i++) {
            String name = selectedItems[i];
            leftList.setData(name, Integer.valueOf(i));
        }
    }

    /**
     * The action of "Add All >>" when.<br/>
     */
    protected void shiftAllToRightAction() {
        String selectedItems[] = leftList.getItems();
        for (int i = 0; i < selectedItems.length; i++) {
            String name = selectedItems[i];
            rightList.add(name);
            rightList.setData(name, Integer.valueOf(-1));
            leftList.remove(name);
        }
        setButtonStatus();
    }

    /**
     * The action of the "Add >>" when.<br/>
     */
    protected void shiftToRightAction() {
        String selectedItems[] = leftList.getSelection();
        int movedNum = 0;
        for (int i = 0; i < selectedItems.length; i++) {
            String name = selectedItems[i];
            rightList.add(name);
            rightList.setData(name, Integer.valueOf(-1));
            leftList.remove(name);
            movedNum++;
        }

        computeLeftData();
        leftList.deselectAll();
        int indecies[] = new int[movedNum];
        for (int i = 0; i < movedNum; i++)
            indecies[i] = rightList.getItemCount() - 1 - i;
        rightList.setSelection(indecies);
        setButtonStatus();
    }

    /**
     * The action of "<< Remove" when.<br/>
     */
    protected void shiftToLeftAction() {
        String selectedItems[] = rightList.getSelection();
        int movedNum = 0;
        for (int i = 0; i < selectedItems.length; i++) {
            String name = selectedItems[i];
            leftList.add(name);
            rightList.remove(name);
            movedNum++;
        }
        computeLeftData();
        int indecies[] = new int[movedNum];
        for (int i = 0; i < movedNum; i++)
            indecies[i] = leftList.getItemCount() - 1 - i;
        leftList.setSelection(indecies);
        setButtonStatus();
    }

    /**
     * The action of "<< Remove all" when.<br/>
     */
    protected void shiftAllToLeftAction() {
        String selectedItems[] = rightList.getItems();
        for (int i = 0; i < selectedItems.length; i++) {
            String name = selectedItems[i];
            leftList.add(name);
            rightList.remove(name);
        }
        computeLeftData();
        setButtonStatus();
    }
}