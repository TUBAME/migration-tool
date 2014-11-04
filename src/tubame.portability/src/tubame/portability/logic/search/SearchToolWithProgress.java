/*
 * SearchToolWithProgress.java
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
package tubame.portability.logic.search;

import java.io.File;
import java.io.IOException;

import org.eclipse.core.resources.IProject;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.Activator;
import tubame.portability.plugin.dialog.ErrorDialog;
import tubame.portability.plugin.preferences.PythonPreferencePage;
import tubame.portability.plugin.wizard.AbstractSearchToolWithProgress;
import tubame.portability.util.FileUtil;
import tubame.portability.util.PythonUtil;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.MessageUtil;

/**
 * Class to perform the search process.<br/>
 * Delegated to {@link AbstractSearchToolWithProgress} is the actual processing.<br/>
 * This class provides the acquisition of Python path, get the Python module,
 * progress title, <br/>
 * the search results file acquisition function.<br/>
 */
public class SearchToolWithProgress extends AbstractSearchToolWithProgress {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SearchToolWithProgress.class);
	private IProject selectedProject;

    /**
     * Constructor.<br/>
     * Constructor reference of {@link AbstractSearchToolWithProgress}<br/>
     * 
     * @param target
     *            Search directory
     * @param keywordFilePath
     *            Search files
     * @param outFilePath
     *            Search result output destination file path
     * @param iProject 
     */
    public SearchToolWithProgress(String target, String keywordFilePath,
            String outFilePath, IProject iProject) {
        super(target, keywordFilePath, outFilePath);
        this.selectedProject = iProject;
        LOGGER.debug("[target]" + target + "[keywordFilePath]"
                + keywordFilePath + "[outFilePath]" + outFilePath);
    }
    
    /**
     * Constructor.<br/>
     * Constructor reference of {@link AbstractSearchToolWithProgress}<br/>
     * 
     * @param target
     *            Search directory
     * @param keywordFilePath
     *            Search files
     * @param outFilePath
     *            Search result output destination file path
     */
    public SearchToolWithProgress(String target, String keywordFilePath,
            String outFilePath) {
        super(target, keywordFilePath, outFilePath);
        LOGGER.debug("[target]" + target + "[keywordFilePath]"
                + keywordFilePath + "[outFilePath]" + outFilePath);
    }
    

    /**
     * {@inheritDoc}
     * @throws IOException 
     */
    @Override
    public String getPythonExePath() throws IOException  {
    	String preferences = Activator.getPreferences(PythonPreferencePage.PREF_KEY_PY_RUNTIME_PATH);
    	if (preferences != null){
    		PythonUtil.PY_RUNTIME_PATH = preferences;
    	}
    	File py = new File(PythonUtil.PY_RUNTIME_PATH);
    	if(!py.exists()  || py.isDirectory()){
            String message = Activator.getResourceString(SearchToolWithProgress.class.getName()
                    + ".err.msg.invalidPythonPath");
            throw new IOException(message+" path="+PythonUtil.PY_RUNTIME_PATH);
    	}
        return PythonUtil.PY_RUNTIME_PATH;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getPythonModulePath() throws IOException {
        return PythonUtil.getPythonSearchModulePath();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getProgressTitle() {
        return MessageUtil.INF_SEARCH_RUNNING;
    }

	@Override
	protected String getWorkspacePath() {
		//WorkSpaceのパス配下に検索対象があるとは限らないので、iproject情報から検索情報対象のデータを取得する必要がある
		if(this.selectedProject!=null){
			 String root = this.selectedProject.getLocation().toFile().getParent();
			 return root.replace(StringUtil.SLASH, FileUtil.FILE_SEPARATOR);
		}
		return null;
	}
    
    

}
