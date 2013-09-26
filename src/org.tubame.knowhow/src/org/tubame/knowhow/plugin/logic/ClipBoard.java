/*
 * ClipBoard.java
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

import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;

/**
 * The class of clipboard (information retained).<br/>
 * This is the class of the clipboard application of (information retained).<br/>
 * JDK7<br/>
 * 
 */
public class ClipBoard {

    /** Data to be saved */
    private PortabilityKnowhowListViewOperation entry;

    /**
     * Get the entry.<br/>
     * 
     * @return entry
     */
    public PortabilityKnowhowListViewOperation getEntry() {
        return entry;
    }

    /**
     * Set the entry.<br/>
     * 
     * @param entry
     *            entry
     */
    public void setEntry(PortabilityKnowhowListViewOperation entry) {
        this.entry = entry;
    }

    /**
     * Clear.<br/>
     * 
     */
    public void clear() {
        this.entry = null;
    }

}
