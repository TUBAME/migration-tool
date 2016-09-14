/*
 * WSearchReadFacade.java
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
 * File read Facade class results.<br/>
 */
public class WSearchReadFacade {
    /**
     * Generate Row information to read the results file and displayed in the
     * view.<br/>
     * 
     * @param filePath
     *            Read the target file path
     * @param projectPath 
     * @return Row information to be displayed in the view
     * @throws WSearchPluginException
     *             File read failure
     */
    public List<WSearchEditorMigrationRow> readNewJbm(String filePath, String projectPath)
            throws WSearchPluginException {
        return this.createNewJbmReaderFactory().read(filePath,projectPath);
    }

    /**
     * The generate a file reading Reader Search result.<br/>
     * 
     * @return File read Reader Search Results
     */
    protected WSearchReader createNewJbmReaderFactory() {
        return new WSearchCsvReader();
    }
}
