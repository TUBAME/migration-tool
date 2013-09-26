/*
 * ReplacementTopLevelItem.java
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
package org.tubame.knowhow.plugin.ui.view.replace;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.exception.JbmException;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import org.tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import org.tubame.knowhow.util.PluginUtil;

/**
 * Class for controlling the movement of (category) top-level of know-how entry
 * view.<br/>
 */
public class ReplacementTopLevelItem implements ReplacementItemStrategy {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ReplacementTopLevelItem.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public void replacementSelectItem(
            PortabilityKnowhowListViewOperation selectedEntry, int rowIndex) {
        ReplacementTopLevelItem.LOGGER.debug("[selectedEntry]" + selectedEntry);

        List<PortabilityKnowhowListViewOperation> childList = PluginUtil
                .getKnowhowEntryViewTreeViewer().getInputEntry();
        int index = childList.indexOf(selectedEntry);
        if (rowIndex + index < childList.size() && 0 <= rowIndex + index) {
            PortabilityKnowhowListViewOperation afterEntry = childList.set(
                    rowIndex + index, selectedEntry);
            childList.set(index, afterEntry);
        } else {
            JbmException.outputExceptionLog(LOGGER, null, MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.ERROR_MOVE_ELEMENT));
            ErrorDialog
                    .openErrorDialog(
                            PluginUtil.getActiveWorkbenchShell(),
                            null,
                            MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.ERROR_MOVE_ELEMENT));
        }
    }
}
