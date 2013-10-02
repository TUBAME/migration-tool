/*
 * ReplacementCheckItem.java
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
package tubame.knowhow.plugin.ui.view.replace;

import java.util.List;


import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.ui.dialog.ErrorDialog;
import tubame.knowhow.util.PluginUtil;

/**
 * Check items move class know-how of entry view.<br/>
 */
public class ReplacementCheckItem implements ReplacementItemStrategy {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ReplacementCheckItem.class);
    /** All Items list */
    private List<PortabilityKnowhowListViewOperation> originalItemlist;

    /**
     * Constructor.<br/>
     * 
     * @param originalItemlist
     *            All Items List
     */
    public ReplacementCheckItem(
            List<PortabilityKnowhowListViewOperation> originalItemlist) {
        this.originalItemlist = originalItemlist;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void replacementSelectItem(
            PortabilityKnowhowListViewOperation selectedEntry, int rowIndex) {
        ReplacementCheckItem.LOGGER.debug("[selectedEntry]" + selectedEntry);
        int index = originalItemlist.indexOf(selectedEntry);
        if (rowIndex + index < originalItemlist.size() && 0 <= rowIndex + index) {
            PortabilityKnowhowListViewOperation afterEntry = originalItemlist
                    .set(rowIndex + index, selectedEntry);
            originalItemlist.set(index, afterEntry);
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
