/*
 * AbstractWSPhaseTask.java
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
package org.tubame.wsearch.biz.model;

import java.util.concurrent.Callable;
import java.util.concurrent.CountDownLatch;

import org.tubame.wsearch.biz.WSPhaseService;
import org.tubame.wsearch.biz.WSPhaseService.TYPE;
import org.tubame.wsearch.biz.process.obsvr.AbstractProcessObserver;
import org.tubame.wsearch.biz.provider.WSPhaseServiceProvider;
import org.tubame.wsearch.biz.provider.WSServiceProvider;

/**
 * This is the default class of phase process.<br/>
 */
public abstract class AbstractWSPhaseTask implements Callable<Object> {

    /**
     * Provider of the service class
     */
    WSServiceProvider wsServiceProvider = new WSPhaseServiceProvider();

    /**
     * Services that implement specific processing
     */
    protected WSPhaseService coreService;

    /**
     * Listener to monitor the progress of treatment
     */
    protected AbstractProcessObserver listener;

    /**
     * Latch to support synchronization
     */
    private PhaseCountDownLatch countDownLatch;

    /**
     * Latch to support the synchronization of the initial processing
     */
    protected CountDownLatch starter;

    /**
     * Kind of the process
     */
    private TYPE type;

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param type
     *            Kind of the process
     * @param starter
     *            Latch to support the synchronization of the initial processing
     * @param countDownLatch
     *            Latch to support synchronization
     */
    public AbstractWSPhaseTask(WSPhaseService.TYPE type,
            CountDownLatch starter, PhaseCountDownLatch countDownLatch) {
        super();
        this.type = type;
        this.starter = starter;
        this.coreService = wsServiceProvider.factory(type);
        this.countDownLatch = countDownLatch;
    }

    /**
     * Get the type of treatment.<br/>
     * 
     * @return Kind of the process
     */
    public TYPE getType() {
        return type;
    }

    /**
     * Set a listener to monitor the progress of treatment.
     * 
     * @param listener
     *            Listener to monitor the progress of treatment
     */
    public void setListener(AbstractProcessObserver listener) {
        this.listener = listener;
    }

    /**
     * Get the latch to support synchronization.<br/>
     * 
     * @return Latch to support synchronization
     */
    protected PhaseCountDownLatch getCountDownLatch() {
        return countDownLatch;
    }

    /**
     * The type that defines the status of the operation.<br/>
     */
    public enum STATUS {
        INIT, START, END, ERR_EXIT, ERR_CONTINUE
    }

    /**
     * This is a class to manage the state of processing.<br/>
     */
    public static class CurrentStatus {

        /**
         * Status of the operation
         */
        AbstractWSPhaseTask.STATUS status;

        /**
         * Files to be processed
         */
        String targetFilepath;

        /**
         * Maximum value of progress
         */
        Integer workMax;

        /**
         * Exception that occurred in the middle of the process
         */
        private Exception exception;

        /**
         * Get an exception that occurred in the middle of the process.<br/>
         * 
         * @return Exception that occurred in the middle of the process
         */
        public Exception getException() {
            return exception;
        }

        /**
         * Set the exception that occurred in the middle of the process.<br/>
         * 
         * @param exception
         *            Exception that occurred in the middle of the process
         */
        public void setException(Exception exception) {
            this.exception = exception;
        }

        /**
         * Get the file to be processed.<br/>
         * 
         * @return Files to be processed
         */
        public String getTargetFilepath() {
            return targetFilepath;
        }

        /**
         * Constructor.<br/>
         * No operation.<br/>
         * 
         * @param status
         *            Status of the operation
         * @param targetFilepath
         *            Files to be processed
         * @param workMax
         *            Maximum value of progress
         */
        public CurrentStatus(STATUS status, String targetFilepath,
                Integer workMax) {
            super();
            this.status = status;
            this.targetFilepath = targetFilepath;
            this.workMax = workMax;
        }

        /**
         * Constructor.<br/>
         * No operation.<br/>
         * 
         * @param status
         *            Status of the operation
         * @param workMax
         *            Maximum value of progress
         */
        public CurrentStatus(STATUS status, Integer workMax) {
            super();
            this.status = status;
            this.workMax = workMax;
        }

        /**
         * Get the status of the operation.<br/>
         * 
         * @return Status of the operation
         */
        public AbstractWSPhaseTask.STATUS getStatus() {
            return status;
        }

        /**
         * Get the maximum value of progress.<br/>
         * 
         * @return Maximum value of progress
         */
        public Integer getWorkMax() {
            return workMax;
        }

        /**
         * Set the maximum value of progress.<br/>
         * 
         * @param workMax
         *            Maximum value of progress
         */
        public void setWorkMax(Integer workMax) {
            this.workMax = workMax;
        }
    }
}
