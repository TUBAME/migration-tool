/*
 * ConvertDoubleClickListener.java
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

import org.eclipse.jface.viewers.DoubleClickEvent;
import org.eclipse.jface.viewers.IDoubleClickListener;
import org.eclipse.jface.viewers.StructuredSelection;
import org.eclipse.jface.viewers.ViewerCell;
import org.eclipse.swt.graphics.Point;

import tubame.portability.model.generated.model.MigrationTarget;
import tubame.portability.util.EditorUtil;

/**
 * Double-click event listener class of the conversion status display.<br/>
 * Double-click after the second layer, and display the file.<br/>
 */
public class ConvertDoubleClickListener implements IDoubleClickListener {
    /**
     * MousePointGetTreeViewer
     */
    private final MousePointGetTreeViewer treeViewer;

    /**
     * Constructor.<br/>
     * 
     * @param treeViewer
     *            MousePoint with TreeViewer
     */
    public ConvertDoubleClickListener(MousePointGetTreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doubleClick(DoubleClickEvent event) {
        Object element = ((StructuredSelection) event.getSelection())
                .getFirstElement();
        if (element instanceof MigrationTarget) {
            Point point = treeViewer.getMouseClickPoint();
            ViewerCell cell = ((org.eclipse.jface.viewers.TreeViewer) event
                    .getSource()).getCell(point);
            if (cell != null) {
                openFile(cell.getColumnIndex(), element);
            }

        }
    }

    /**
     * A place where it has been double-click is "1", open the file of WebLogic.<br/>
     * 
     * @param columnIndex
     *            Double-click point number
     * @param element
     *            Double-click place element
     */
    private void openFile(int columnIndex, Object element) {
        if (columnIndex == 1) {
            EditorUtil.openEditor(((MigrationTarget) element).getTargetFile());
        }
        if (columnIndex == 2) {
            EditorUtil.openEditor(((MigrationTarget) element)
                    .getConvertedFile());
        }
    }
}
