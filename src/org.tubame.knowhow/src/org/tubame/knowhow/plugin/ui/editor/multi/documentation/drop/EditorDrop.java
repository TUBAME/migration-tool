/*
 * EditorDrop.java
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
package org.tubame.knowhow.plugin.ui.editor.multi.documentation.drop;

import org.tubame.knowhow.plugin.model.editor.EntryOperator;
import org.tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;

/**
 * Interface that represents a drop of the editor.<br/>
 */
public interface EditorDrop {
    /**
     * To determine whether the draggable. The return true if drag is possible.<br/>
     * 
     * @param dropTargetEntry
     *            Drag target Entry
     * @param dragViewdata
     *            Target PortabilityKnowhowListViewOperation
     * @return true:OK false:NG
     */
    public abstract boolean isDropEnable(EntryOperator dropTargetEntry,
            PortabilityKnowhowListViewOperation dragViewdata);

    /**
     * Add Entry.<br/>
     * 
     * @param addParentEntry
     *            Parent Entry to be added
     * @param dragViewdata
     *            Target PortabilityKnowhowListViewOperation
     */
    public void add(EntryOperator addParentEntry,
            PortabilityKnowhowListViewOperation dragViewdata);

}
