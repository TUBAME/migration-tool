/*
 * AppropriateLineListener.java
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


import org.eclipse.swt.events.SelectionEvent;
import org.eclipse.swt.events.SelectionListener;
import org.eclipse.swt.widgets.Button;
import org.eclipse.swt.widgets.Text;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

/**
 * Class that controls the activation / deactivation of the Description area of
 * the line number calculation.<br/>
 */
public class AppropriateLineListener implements SelectionListener {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AppropriateLineListener.class);
    /** Number of lines recorded */
    private Button appropriateLine;
    /** Number of lines non-calculated Description area */
    private Text appropriateContext;

    /**
     * Constructor.<br/>
     * 
     * @param appropriateLine
     *            Line number calculation
     * @param appropriateContext
     *            Number of lines non-calculated Description area
     */
    public AppropriateLineListener(Button appropriateLine,
            Text appropriateContext) {
        this.appropriateLine = appropriateLine;
        this.appropriateContext = appropriateContext;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetSelected(SelectionEvent e) {
        appropriateContext.setEnabled(true);
        if (appropriateLine.getSelection()) {
            appropriateContext.setEnabled(false);
        }
        AppropriateLineListener.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_CHANGE_APPROPRIATE_LINE));
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void widgetDefaultSelected(SelectionEvent e) {
        // no operation
    }
}
