/*
 * AbstractProcessor.java
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
package org.tubame.wsearch.biz.process;

import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import org.tubame.wsearch.biz.WSPhaseService;
import org.tubame.wsearch.biz.model.PhaseInbound;
import org.tubame.wsearch.biz.process.obsvr.AbstractProcessObserver;

/**
 * It is the base class of general-purpose search process.<br/>
 */
public abstract class AbstractProcessor<IN, OUT> {

    /**
     * Object to run the Runnable task
     */
    protected ExecutorService executor;

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param threadCnt
     *            Number of threads
     */
    public AbstractProcessor(int threadCnt) {
        super();
        this.executor = Executors.newFixedThreadPool(threadCnt);
    }

    /**
     * Run for the processing of a general-purpose search based on the input
     * value.<br/>
     * 
     * @param inbound
     *            Input values for processing
     * @return Processing result
     */
    public abstract OUT process(PhaseInbound<IN> inbound);

    /**
     * Cancel the operation running.<br/>
     */
    public void cancel() {
        executor.shutdownNow();
    }

    /**
     * Get the type of treatment.<br/>
     * 
     * @return Kind of the process
     */
    public abstract WSPhaseService.TYPE getType();

    /**
     * Get the object that is monitoring this process.<br/>
     * 
     * @param observer
     *            Object that is monitoring this process
     */
    public abstract void setObserver(AbstractProcessObserver observer);
}
