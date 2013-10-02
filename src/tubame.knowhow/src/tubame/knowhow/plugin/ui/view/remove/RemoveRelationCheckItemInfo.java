/*
 * RemoveRelationCheckItemInfo.java
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
package tubame.knowhow.plugin.ui.view.remove;

import tubame.common.util.CmnStringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.plugin.model.view.CheckItemViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.model.view.SearchInfoViewType;

/**
 * Make a related item deletion processing of check items information.<br/>
 * If the deleted item is a search information and remove the reference key of
 * confidence from the check items of the parent.<br/>
 */
public class RemoveRelationCheckItemInfo implements RemoveRelationItemStrategy {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(RemoveRelationCheckItemInfo.class);
    /** Deleted items */
    private PortabilityKnowhowListViewOperation portabilityKnowhowListViewOperation;

    /**
     * Constructor.<br/>
     * 
     * @param portabilityKnowhowListViewOperation
     *            Deleted items
     */
    public RemoveRelationCheckItemInfo(
            PortabilityKnowhowListViewOperation portabilityKnowhowListViewOperation) {
        this.portabilityKnowhowListViewOperation = portabilityKnowhowListViewOperation;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void removeRelationItem() {
        RemoveRelationCheckItemInfo.LOGGER.debug(CmnStringUtil.EMPTY);
        if (portabilityKnowhowListViewOperation.isSearchInfo()) {
            SearchInfoViewType searchInfoViewType = (SearchInfoViewType) portabilityKnowhowListViewOperation
                    .getKnowhowViewType();
            removeSearchRefKey(portabilityKnowhowListViewOperation.getParent(),
                    searchInfoViewType.getRegisterKey());

        }
    }

    /**
     * Remove reference search key from the check items of the parent hierarchy.<br/>
     * If the key to be deleted and search reference key for a check items are
     * the same,<br/>
     * delete the reference search key.<br/>
     * 
     * @param parent
     *            Parent hierarchy items
     * @param registerKey
     *            Search key information
     */
    private void removeSearchRefKey(PortabilityKnowhowListViewOperation parent,
            String registerKey) {
        CheckItemViewType checkItemViewType = (CheckItemViewType) parent
                .getKnowhowViewType();
        if (checkItemViewType.getSearchRefKey().equals(registerKey)) {
            checkItemViewType.setSearchRefKey(CmnStringUtil.EMPTY);
        }
        parent.setKnowhowViewType(checkItemViewType);
    }
}
