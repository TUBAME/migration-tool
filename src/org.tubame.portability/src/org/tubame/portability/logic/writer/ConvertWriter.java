/*
 * ConvertWriter.java
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
package org.tubame.portability.logic.writer;

import java.util.List;

import org.tubame.portability.exception.JbmException;
import org.tubame.portability.model.generated.model.MigrationItem;

/**
 * Conversion status file write interface.<br/>
 * If the conversion status file write operation is implemented, implements this
 * interface.<br/>
 */
public interface ConvertWriter {

    /**
     * Create an XML file from MigrationItem.<br/>
     * 
     * @param migrationItemList
     *            MigrationItem list
     * @param filePath
     *            File path to create
     * @throws JbmException
     *             Plug-in exception
     */
    void write(List<MigrationItem> migrationItemList, String filePath)
            throws JbmException;
}
