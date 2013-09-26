/*
 * EditorDirty.java
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
package org.tubame.knowhow.plugin.ui.editor.multi;

/**
 * Save sensing interface of the editor.<br/>
 */
public interface EditorDirty {
    /**
     * Set the state of preservation.<br/>
     * 
     * @param dirty
     *            true:Save required false:No need Save
     */
    public void setDirty(boolean dirty);

    /**
     * Do the Validation check of current data of Editor.<br/>
     * 
     * @return Verification result true:No warning in the validate false:There
     *         warning
     */
    public boolean check();

    /**
     * Get a file name that is currently open.<br/>
     * Get the name of the file that is currently open in the Editor.<br/>
     * 
     * @return File name that is currently open in the Editor
     */
    public String getTargetFileName();

    /**
     * Get the state of preservation.<br/>
     * 
     * @return dirty
     */
    public boolean getDirty();
}
