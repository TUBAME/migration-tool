/*
 * KnowhowViewDragSourceListener.java
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
package tubame.knowhow.plugin.ui.view;


import org.eclipse.jface.util.LocalSelectionTransfer;
import org.eclipse.jface.viewers.IStructuredSelection;
import org.eclipse.jface.viewers.TreeViewer;
import org.eclipse.swt.dnd.DragSourceEvent;
import org.eclipse.swt.dnd.DragSourceListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

/**
 * Class that controls the item you want to drag source of know-how entry view.<br/>
 */
public class KnowhowViewDragSourceListener implements DragSourceListener {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(KnowhowViewDragSourceListener.class);
    /** Tree view */
    private TreeViewer treeViewer;

    /**
     * Constructor.<br/>
     * 
     * @param treeViewer
     *            Tree view
     */
    public KnowhowViewDragSourceListener(TreeViewer treeViewer) {
        this.treeViewer = treeViewer;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragStart(DragSourceEvent event) {
        KnowhowViewDragSourceListener.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_DRAG_START));
        // Set to Transfer the data of the drag source
        IStructuredSelection selection = (IStructuredSelection) treeViewer
                .getSelection();
        LocalSelectionTransfer.getTransfer().setSelection(selection);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragSetData(DragSourceEvent event) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void dragFinished(DragSourceEvent event) {
        // no operation
    }

}
