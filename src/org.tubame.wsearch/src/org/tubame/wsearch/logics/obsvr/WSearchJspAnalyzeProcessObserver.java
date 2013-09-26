/*
 * WSearchJspAnalyzeProcessObserver.java
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
package org.tubame.wsearch.logics.obsvr;

import java.util.ArrayList;
import java.util.concurrent.CountDownLatch;


import org.eclipse.core.runtime.IProgressMonitor;
import org.tubame.wsearch.Activator;
import org.tubame.wsearch.biz.WSPhaseService.TYPE;
import org.tubame.wsearch.biz.analyzer.jsp.JspAnalyzer;
import org.tubame.wsearch.biz.comparer.ComparerManager;
import org.tubame.wsearch.biz.ex.WSearchBizException;
import org.tubame.wsearch.biz.model.PhaseCountDownLatch;
import org.tubame.wsearch.biz.process.AbstractProcessor;

/**
 * Monitoring role of observer class comparison process and analyze Jsp.<br/>
 */
public class WSearchJspAnalyzeProcessObserver extends WSearchProcessObserver {

    /**
     * Exception occurrence or non-occurrence
     */
    private boolean exceptionOccurred = false;

    /**
     * Constructor.<br/>
     * 
     */
    public WSearchJspAnalyzeProcessObserver() {
        super();
    }

    /**
     * Constructor.<br/>
     * 
     * @param starter
     *            Latch countdown for the start
     * @param phaseCountDownLatch
     *            Countdown Latch of each phase processing
     * @param processor
     *            Processor
     * @param monitor
     *            Monitor progress
     */
    public WSearchJspAnalyzeProcessObserver(CountDownLatch starter,
            PhaseCountDownLatch phaseCountDownLatch,
            AbstractProcessor processor, IProgressMonitor monitor) {
        super(starter, phaseCountDownLatch, processor, monitor);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void logicPrepare(TYPE type, Object value)
            throws WSearchBizException {

        try {
            if (this.ignoreList == null) {
                this.phaseCountDownLatch.setAnalyzer(new JspAnalyzer(
                        (String) value));
            } else {
                this.phaseCountDownLatch.setAnalyzer(new JspAnalyzer(
                        (String) value, this.ignoreList));
            }

            // Changed to set the Konpeara, please delete if it is not good.
            this.phaseCountDownLatch.setComparer(ComparerManager.getInstance()
                    .getComparer(ComparerManager.COMPARER_TYPE.JSP));

        } catch (Exception e) {
            // Loggigng possible
            throw new WSearchBizException(type, e.getMessage(), e.getCause());
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void execptionHandle(Exception e) {
        if (this.exceptionList == null) {
            this.exceptionList = new ArrayList<Throwable>();
        }
        this.exceptionList.add(e);
        Activator.log(e, Activator
                .getResourceString(WSearchJspAnalyzeProcessObserver.class
                        .getName() + ".err.msg.ExceptionOccurred"));
        exceptionOccurred = true;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean exceptionOccurred() {
        return this.exceptionOccurred;
    }
}
