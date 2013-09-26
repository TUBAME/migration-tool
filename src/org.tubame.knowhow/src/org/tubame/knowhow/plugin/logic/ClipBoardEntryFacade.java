/*
 * ClipBoardEntryFacade.java
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
package org.tubame.knowhow.plugin.logic;

import org.tubame.common.util.CmnStringUtil;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import org.tubame.knowhow.util.PluginUtil;

/**
 * Facade class to access the clipboard of this application.<br/>
 */
public final class ClipBoardEntryFacade {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ClipBoardEntryFacade.class);
    /** Save target data */
    private static ClipBoard clipBoard = new ClipBoard();

    /**
     * Constructor.<br/>
     * 
     */
    private ClipBoardEntryFacade() {
        // no operation.
    }

    /**
     * Get the know-how entry information held.<br/>
     * 
     * @return Want to retain data
     */
    public static PortabilityKnowhowListViewOperation getEntry() {
        ClipBoardEntryFacade.LOGGER.debug(CmnStringUtil.EMPTY);
        PortabilityKnowhowListViewOperation entry = ClipBoardEntryFacade.clipBoard
                .getEntry();
        ClipBoardEntryFacade.clipBoard.clear();
        return entry;
    }

    /**
     * It holds the know-how entry information.<br/>
     * 
     * @param entry
     *            Want to retain data
     */
    public static void setEntry(PortabilityKnowhowListViewOperation entry) {
        ClipBoardEntryFacade.LOGGER.debug("[entry]" + entry);
        ClipBoardEntryFacade.clipBoard.setEntry(PluginUtil.deepCopy(entry));
    }

    /**
     * Check entry know-how information stored within the application that it is
     * still Existence.<br/>
     * 
     * @return true:Holds false:Does not hold
     */
    public static boolean isSaved() {
        PortabilityKnowhowListViewOperation temp = ClipBoardEntryFacade
                .getEntry();
        if (temp == null) {
            return false;
        }
        ClipBoardEntryFacade.setEntry(temp);
        return true;
    }
}
