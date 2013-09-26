/*
 * SelectPythonModuleItemListener.java
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
package org.tubame.knowhow.plugin.ui.wizard.listener;

import java.util.Map;

import org.tubame.common.util.CmnStringUtil;

import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Combo;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.plugin.logic.FileManagement;

/**
 * Class that controls the search module description area of the search module
 * is selected.<br/>
 */
public class SelectPythonModuleItemListener implements SelectionListener {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(SelectPythonModuleItemListener.class);
    /** Search module selection button */
    private Combo pythonModule;
    /** Search module Description area */
    private Text pythonModuleContext;
    /** Search information module Map */
    private Map<String, String> searchModuleMap = FileManagement
            .getSearchModuleMap();

    /**
     * Constructor.<br/>
     * 
     * @param pythonModule
     *            Search module selection button
     * @param pythonModuleContext
     *            Search module Description area
     */
    public SelectPythonModuleItemListener(Combo pythonModule,
            Text pythonModuleContext) {
        this.pythonModule = pythonModule;
        this.pythonModuleContext = pythonModuleContext;
        SelectPythonModuleItemListener.LOGGER.debug("[pythonModule]"
                + pythonModule + "[pythonModuleContext]" + pythonModuleContext);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        String selectedModule = pythonModule.getText();
        pythonModuleContext.setText(CmnStringUtil.EMPTY);
        for (Map.Entry<String, String> entry : searchModuleMap.entrySet()) {
            if (entry.getKey().equals(selectedModule)) {
                pythonModuleContext.setText(entry.getValue());
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // no operation
    }

}
