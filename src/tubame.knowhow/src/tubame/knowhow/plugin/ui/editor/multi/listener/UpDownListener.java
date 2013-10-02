/*
 * UpDownListener.java
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
package tubame.knowhow.plugin.ui.editor.multi.listener;


import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.ui.editor.multi.documentation.EditorTreeViewerOperator;

/**
 * Classes for the Up / Down items TreeViewer.<br/>
 * Processing of Up / Down is delegated to TreeViewerOperator.<br/>
 */
public class UpDownListener implements SelectionListener {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(UpDownListener.class);
    /** true:UP false:down */
    private boolean upOrdown;
    /** TreeViewer */
    private EditorTreeViewerOperator treeViewer;

    /**
     * Constructor.<br/>
     * 
     * @param upOrdown
     *            true:UP false:down
     * @param treeViewer
     *            TreeViewer
     */
    public UpDownListener(boolean upOrdown, EditorTreeViewerOperator treeViewer) {
        this.treeViewer = treeViewer;
        this.upOrdown = upOrdown;
        UpDownListener.LOGGER.debug("[upOrdown]" + upOrdown + "[treeViewer]"
                + treeViewer);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        UpDownListener.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_MOVE_ITEM));
        if (this.upOrdown) {
            this.treeViewer.upItem();
        } else {
            this.treeViewer.downItem();
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
