/*
 * WSearchReader.java
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
package tubame.wsearch.logics;

import java.util.List;

import tubame.wsearch.models.WSearchEditorMigrationRow;
import tubame.wsearch.ui.ex.WSearchPluginException;

/**
 * If the operation to load the search results file is implemented, the
 * implementation of this interface.<br/>
 */
public interface WSearchReader {
    /**
     * Reads the results file and generate a List of
     * {@link WSearchEditorMigrationRow}.<br/>
     * 
     * @param filePath
     *            Target file path
     * @return Read data object
     * @throws WSearchPluginException
     *             Exception information
     */
    public List<WSearchEditorMigrationRow> read(String filePath)
            throws WSearchPluginException;
}
