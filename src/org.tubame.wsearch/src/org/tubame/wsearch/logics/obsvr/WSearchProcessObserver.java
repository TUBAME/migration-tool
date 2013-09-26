/*
 * WSearchProcessObserver.java
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

import java.util.List;
import java.util.concurrent.CountDownLatch;


import org.eclipse.core.runtime.IProgressMonitor;
import org.tubame.wsearch.Activator;
import org.tubame.wsearch.biz.model.PhaseCountDownLatch;
import org.tubame.wsearch.biz.process.AbstractProcessor;
import org.tubame.wsearch.biz.process.WSearchAnalyzeAndCompareProcessor;
import org.tubame.wsearch.biz.process.WSearchLibraryMetaInfoGenerateProcessor;
import org.tubame.wsearch.biz.process.obsvr.AbstractProcessObserver;

/**
 * Watchdog observer abstract class of each phase of the process.<br/>
 */
public abstract class WSearchProcessObserver extends AbstractProcessObserver {

    /**
     * Monitor progress
     */
    private IProgressMonitor monitor;

    /**
     * Exception list
     */
    protected List<Throwable> exceptionList;

    /**
     * Constructor.<br/>
     */
    public WSearchProcessObserver() {
        super();
    }

    /**
     * Get the exception list.<br/>
     * 
     * @return Exception list
     */
    public List<Throwable> getExceptionList() {
        return this.exceptionList;
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
    public WSearchProcessObserver(CountDownLatch starter,
            PhaseCountDownLatch phaseCountDownLatch,
            AbstractProcessor processor, IProgressMonitor monitor) {
        super(starter, phaseCountDownLatch, processor);
        this.monitor = monitor;
        this.monitor.beginTask(
                Activator.getResourceString(WSearchProcessObserver.class
                        .getName() + ".label.preparation"),
                IProgressMonitor.UNKNOWN);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public boolean isCanceled() {
        return monitor.isCanceled();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void printSubTask(String target) {
        monitor.subTask("targetfile:" + target);

    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void monitorWorked(Integer workCnt) {
        monitor.worked(workCnt);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void beginTask(Integer workMax) {
        if (this.processor instanceof WSearchAnalyzeAndCompareProcessor) {
            WSearchAnalyzeAndCompareProcessor compareProcessor = (WSearchAnalyzeAndCompareProcessor) this.processor;
            compareProcessor.getParserType();
            this.monitor
                    .beginTask(
                            compareProcessor.getParserType().toString()
                                    + Activator.getResourceString(WSearchProcessObserver.class
                                            .getName()
                                            + ".start.task.analyze.label"),
                            workMax);
        } else if (this.processor instanceof WSearchLibraryMetaInfoGenerateProcessor) {
            this.monitor.beginTask(
                    Activator.getResourceString(WSearchProcessObserver.class
                            .getName() + ".start.task.libgen.label"), workMax);
        }
    }

    /**
     * Determine whether the exception occurred.<br/>
     * 
     * @return Determine if an exception occurs
     */
    public abstract boolean exceptionOccurred();
}
