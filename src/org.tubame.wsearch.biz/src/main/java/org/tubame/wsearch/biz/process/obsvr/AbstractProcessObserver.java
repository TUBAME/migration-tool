/*
 * AbstractProcessObserver.java
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
package org.tubame.wsearch.biz.process.obsvr;

import java.util.List;
import java.util.Observable;
import java.util.Observer;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;

import org.tubame.wsearch.biz.WSPhaseService;
import org.tubame.wsearch.biz.ex.WSearchBizException;
import org.tubame.wsearch.biz.model.AbstractWSPhaseTask;
import org.tubame.wsearch.biz.model.PhaseCountDownLatch;
import org.tubame.wsearch.biz.process.AbstractProcessor;

/**
 * It is a monitoring base class of general-purpose search process.<br/>
 */
public abstract class AbstractProcessObserver implements Observer {

    /**
     * Latch synchronization support
     */
    protected PhaseCountDownLatch phaseCountDownLatch;

    /**
     * Process
     */
    protected AbstractProcessor processor;

    /**
     * Regular expression string list in which the extracted outside
     */
    protected List<Pattern> ignoreList;

    /**
     * Constructor.<br/>
     * No operation.<br/>
     */
    public AbstractProcessObserver() {
    }

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param starterLatch
     *            Synchronization support for latch of the initial processing
     * @param phaseCountDownLatch
     *            Latch of synchronization support for
     * @param processor
     *            Process
     */
    public AbstractProcessObserver(CountDownLatch starterLatch,
            PhaseCountDownLatch phaseCountDownLatch, AbstractProcessor processor) {
        super();
        this.phaseCountDownLatch = phaseCountDownLatch;
        this.processor = processor;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void update(Observable o, Object arg) {
        if (arg instanceof AbstractWSPhaseTask.CurrentStatus) {
            AbstractWSPhaseTask.CurrentStatus status = (AbstractWSPhaseTask.CurrentStatus) arg;

            // If the initial event is called from core.
            if (status.getStatus().equals(AbstractWSPhaseTask.STATUS.INIT)) {
                beginTask(status.getWorkMax());
            }

            // If you are notified of the status of start 1 from core.
            else if (status.getStatus()
                    .equals(AbstractWSPhaseTask.STATUS.START)) {

                if (isCanceled()) {
                    this.processor.cancel();
                    return;
                }

                // Screen update of Eclipe.
                printSubTask(status.getTargetFilepath());

                try {
                    // Require processing to set the parameters required for
                    // each process.
                    // In the case of AnalyzeAndCompare, according (Java,
                    // JSP.xml) in processed of each Solo, you can create the
                    // appropriate analyze, is set to phaseCountDownLatch.
                    logicPrepare(this.processor.getType(),
                            status.getTargetFilepath());

                } catch (Exception e) {
                    // After this, update in STATUS.END so called, it is not
                    // necessary to increase the progress and counter increment.
                    execptionHandle(e);
                }

            } else if (status.getStatus().equals(
                    AbstractWSPhaseTask.STATUS.ERR_EXIT)) {
                execptionHandle(status.getException());
                this.processor.cancel();
            } else if (status.getStatus().equals(
                    AbstractWSPhaseTask.STATUS.ERR_CONTINUE)) {
                phaseCountDownLatch.increment();
                // Processing progress is OK with one fixed.
                monitorWorked(1);
                // If you are notified of the exit status of 1 from core.
            } else {
                // Counter increment.
                phaseCountDownLatch.increment();
                // Processing progress is OK with one fixed.
                monitorWorked(1);
            }
        }
    }

    /**
     * Set the regular expression string list in which the extracted outside.<br/>
     * 
     * @param ignoreList
     *            Regular expression string list in which the extracted outside
     */
    public void setIgnoreList(List<Pattern> ignoreList) {
        this.ignoreList = ignoreList;
    }

    /**
     * Determine whether the process has been canceled.
     * 
     * @return Process to see if it has been canceled
     */
    public abstract boolean isCanceled();

    /**
     * make the start process of task processing.
     * 
     * @param max
     *            Total processing progress
     */
    public abstract void beginTask(Integer max);

    /**
     * Inform the start of subtasks.
     * 
     * @param target
     *            Target
     */
    public abstract void printSubTask(String target);

    /**
     * Inform the progress of the operation.<br/>
     * 
     * @param workCnt
     *            Value of progress
     */
    public abstract void monitorWorked(Integer workCnt);

    /**
     * Make the handling of exception processing.<br/>
     * 
     * @param exception
     *            Exception
     */
    public abstract void execptionHandle(Exception exception);

    /**
     * Do the preparation of the processing of the service.<br/>
     * 
     * @param type
     *            Kind of the process
     * @param value
     *            Input value to be used for preparation
     * @throws WSearchBizException
     *             Business logic exception
     */
    public abstract void logicPrepare(WSPhaseService.TYPE type, Object value)
            throws WSearchBizException;

}
