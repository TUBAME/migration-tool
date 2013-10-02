/*
 * EntryElementCopyAction.java
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
package tubame.knowhow.plugin.ui.action.view;

import tubame.common.util.CmnStringUtil;

import org.eclipse.jface.action.Action;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.logic.ClipBoardEntryFacade;
import tubame.knowhow.plugin.model.editor.AbstractEntry;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Make a copy of each Entry.<br/>
 * Copy to clipboard {@link AbstractEntry}.<br/>
 */
public class EntryElementCopyAction extends Action {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(EntryElementCopyAction.class);
    /** Selection Entry */
    private PortabilityKnowhowListViewOperation selectedEntry;

    /**
     * Constructor.<br/>
     * 
     * @param selectedEntry
     *            Selection Entry
     */
    public EntryElementCopyAction(
            PortabilityKnowhowListViewOperation selectedEntry) {
        this.selectedEntry = selectedEntry;
        EntryElementCopyAction.LOGGER.debug("[selectedEntry]" + selectedEntry);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getText() {
        return ResourceUtil.contextCopyMenuName;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void run() {
        EntryElementCopyAction.LOGGER.debug(CmnStringUtil.EMPTY);
        ClipBoardEntryFacade.setEntry(selectedEntry);
        EntryElementCopyAction.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_ELEMENT_COPY));
    }
}
