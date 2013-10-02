/*
 * JbmEditorCellModifier.java
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
package tubame.portability.plugin.view;

import tubame.knowhow.biz.model.LineNumberEnum;

import org.eclipse.jface.viewers.ICellModifier;
import org.eclipse.swt.widgets.TreeItem;

import tubame.portability.model.JbmEditorEnum;
import tubame.portability.model.JbmEditorMigrationRow;
import tubame.portability.model.MigrationEditorRow;
import tubame.portability.plugin.editor.MigrationEditorOperation;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.resource.ApplicationPropertyUtil;
import tubame.portability.util.resource.MessageUtil;
import tubame.portability.util.resource.ResourceUtil;

/**
 * Line number basis editing class and line number of search results editor of
 * portability study tool.<br/>
 */
public class JbmEditorCellModifier implements ICellModifier {

    /**
     * Know-how entry view
     */
    private final MousePointGetTreeViewer jbmEditorTreeViewer;

    /**
     * Constructor.<br/>
     * 
     * @param jbmEditorTreeViewer
     *            Know-how entry view
     */
    public JbmEditorCellModifier(MousePointGetTreeViewer jbmEditorTreeViewer) {
        this.jbmEditorTreeViewer = jbmEditorTreeViewer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean canModify(Object element, String property) {
        if (property.equals("")) {
            return false;
        }
        // return isModify;
        return true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object getValue(Object element, String property) {
        // Start editing
        if (property.equals(ApplicationPropertyUtil.EDIT_COLUMN_LINENUM)
                || property
                        .equals(ApplicationPropertyUtil.EDIT_COLUMN_LINENUMCONTENTS)) {
            MigrationEditorRow jbmEditorData = (MigrationEditorRow) element;
            if (jbmEditorData.getLevel() == 0) {
                String value = null;
                if (property
                        .equals(ApplicationPropertyUtil.EDIT_COLUMN_LINENUM)) {
                    value = jbmEditorData.getColumnText(JbmEditorEnum.LINE_NUM
                            .getCode());
                } else {
                    value = jbmEditorData
                            .getColumnText(JbmEditorEnum.LINE_NUM_BASIS
                                    .getCode());
                }
                return value;
            }
        }
        return null;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void modify(Object element, String property, Object value) {
        // Close editor
        if (element instanceof TreeItem) {
            TreeItem item = (TreeItem) element;
            if (property.equals(ApplicationPropertyUtil.EDIT_COLUMN_LINENUM)
                    || property
                            .equals(ApplicationPropertyUtil.EDIT_COLUMN_LINENUMCONTENTS)) {
                // Get a current Active Editor
                MigrationEditorOperation editor = (MigrationEditorOperation) PluginUtil
                        .getActiveEditor();
                JbmEditorMigrationRow row = (JbmEditorMigrationRow) item
                        .getData();
                // Input value setting
                if (property
                        .equals(ApplicationPropertyUtil.EDIT_COLUMN_LINENUM)) {
                    modifyLineNumber(value, row);
                } else {
                    modifyLineNumberContents(value, row);
                }
                jbmEditorTreeViewer.refresh(row);
                // Change notification
                row.updateWriteData();
                editor.setDirty(true);
            }
        }
    }

    /**
     * Set the cell number of lines after editing.<br/>
     * Set the data in the cell if it can be set, <br/>
     * in the case of can not be set, display an error dialog.<br/>
     * 
     * @param value
     *            Object
     * @param row
     *            JbmEditorMigrationRow
     */
    private boolean modifyLineNumber(Object value, JbmEditorMigrationRow row) {
        String editedValue = (String) value;
        if (!LineNumberEnum.ToDoSE.getName().equals(value)
                && !LineNumberEnum.Unknown.getName().equals(value)
                && !isInteger(editedValue)) {
            PluginUtil.viewErrorDialog(ResourceUtil.EDIT_CELL,
                    MessageUtil.ERR_EDIT_CELL_VALUE, null);
            return false;
        }
        row.setLineNumber(getLineNumberValue(editedValue));
        return true;
    }

    /**
     * Set the cell number of lines of evidence after editing.<br/>
     * Set the data in the cell if it can be set, in the case of can not be set, <br/>
     * display an error dialog.
     * 
     * @param value
     *            Object
     * @param row
     *            JbmEditorMigrationRow
     */
    private boolean modifyLineNumberContents(Object value,
            JbmEditorMigrationRow row) {
        String editedValue = (String) value;
        row.setLineNumberContents(editedValue);
        return true;

    }

    /**
     * Get the string to be displayed in the editor.<br/>
     * 
     * @param editedValue
     *            Edit string
     * @return Editor display string
     */
    private String getLineNumberValue(String editedValue) {
        if (isInteger(editedValue)) {
            return String.valueOf(Integer.parseInt(editedValue));
        }
        return editedValue;
    }

    /**
     * Judgment string after editing is configured with a range of 0 to
     * 2147483647.<br/>
     * 
     * @param editedValue
     *            After editing string
     * @return true:From 0 to 2147483647 false:Other
     */
    private boolean isInteger(String editedValue) {
        try {
            int targetInt = Integer.parseInt(editedValue);
            if (targetInt >= 0 && targetInt < Integer.MAX_VALUE) {
                return true;
            }
            return false;
        } catch (NumberFormatException e) {
            return false;
        }
    }
}
