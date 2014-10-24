/*
 * DeleteEntryItemAction.java
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
package tubame.knowhow.plugin.ui.action;


import org.eclipse.jface.action.IAction;
import org.eclipse.jface.viewers.ISelection;
import org.eclipse.ui.IViewActionDelegate;
import org.eclipse.ui.IViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.ui.view.KnowhowEntryCheckItemView;
import tubame.knowhow.plugin.ui.view.KnowhowEntryTreeViewer;
import tubame.knowhow.util.PluginUtil;

/**
 * Class action to remove the selected item from the know-how entry view.<br/>
 */
public class DeleteEntryItemAction implements IViewActionDelegate {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(DeleteEntryItemAction.class);
    /** Access to KnowhowEntryTreeViewer */
    private KnowhowEntryTreeViewer knowhowEntryTreeViewer;

    /**
     * {@inheritDoc}
     */
    @Override
    public void run(IAction action) {
        DeleteEntryItemAction.LOGGER.debug("[action]" + action);

        knowhowEntryTreeViewer = PluginUtil.getKnowhowEntryViewTreeViewer();
        knowhowEntryTreeViewer.removeEntryItem(knowhowEntryTreeViewer
                .getOneSelection());
        
        DeleteEntryItemAction.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_ENTRY_REMOVE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void selectionChanged(IAction action, ISelection selection) {
        // no operation
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void init(IViewPart view) {
        // no operation
    }

}
