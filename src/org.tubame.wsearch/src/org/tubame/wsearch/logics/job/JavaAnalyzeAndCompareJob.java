/*
 * JavaAnalyzeAndCompareJob.java
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
package org.tubame.wsearch.logics.job;

import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;
import java.util.regex.Pattern;


import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.core.runtime.IStatus;
import org.eclipse.core.runtime.MultiStatus;
import org.eclipse.core.runtime.Status;
import org.eclipse.core.runtime.jobs.Job;
import org.tubame.wsearch.Activator;
import org.tubame.wsearch.biz.WSPhaseService.TYPE;
import org.tubame.wsearch.biz.ex.WSearchBizException;
import org.tubame.wsearch.biz.model.AnalyzeAndCompareInput;
import org.tubame.wsearch.biz.model.PhaseCountDownLatch;
import org.tubame.wsearch.biz.model.PhaseInbound;
import org.tubame.wsearch.biz.process.WSearchAnalyzeAndCompareProcessor;
import org.tubame.wsearch.biz.process.WSearchAnalyzeAndCompareProcessor.PARSER;
import org.tubame.wsearch.logics.obsvr.WSearchJavaAnalyzeProcessObserver;
import org.tubame.wsearch.logics.obsvr.WSearchProcessObserver;

/**
 * Jobs EclipsePlatform to compare processing Java and analyze.<br/>
 */
public class JavaAnalyzeAndCompareJob extends Job {

    /**
     * List of extensions analyze target
     */
    private List<String> extensions;

    /**
     * Map of analyze target
     */
    private Map<String, List<String>> targetsMap;

    /**
     * Map of the filter
     */
    private Map<String, List<Pattern>> filtersMap;

    /**
     * Output target directory
     */
    private String outputDir;

    /**
     * Constructor.<br/>
     * 
     * @param name
     *            Job name
     * @param extensions
     *            List of extensions
     * @param targetsMap
     *            Map to be processed
     * @param filtersMap
     *            Map of the filter
     * @param outputDir
     *            Destination directory
     */
    public JavaAnalyzeAndCompareJob(String name, List<String> extensions,
            Map<String, List<String>> targetsMap,
            Map<String, List<Pattern>> filtersMap, String outputDir) {
        super(name);
        this.extensions = extensions;
        this.targetsMap = targetsMap;
        this.filtersMap = filtersMap;
        this.outputDir = outputDir;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    protected IStatus run(IProgressMonitor monitor) {

        int targetCount = 0;
        for (String extension : this.extensions) {
            targetCount += this.targetsMap.get(extension).size();
        }

        PhaseCountDownLatch phaseCountDownLatch = new PhaseCountDownLatch(
                targetCount);
        CountDownLatch starter = new CountDownLatch(1);
        WSearchAnalyzeAndCompareProcessor analyzeAndCompareProcessor = new WSearchAnalyzeAndCompareProcessor(
                1, starter, phaseCountDownLatch, PARSER.Java);

        WSearchProcessObserver observer = new WSearchJavaAnalyzeProcessObserver(
                starter, phaseCountDownLatch, analyzeAndCompareProcessor,
                monitor);
        analyzeAndCompareProcessor.setObserver(observer);

        AnalyzeAndCompareInput analyzeAndCompareInput = new AnalyzeAndCompareInput(
                this.outputDir);
        analyzeAndCompareInput.setExtensions(extensions);
        analyzeAndCompareInput.setTargetsMap(targetsMap);
        analyzeAndCompareInput.setFiltersMap(filtersMap);
        PhaseInbound<AnalyzeAndCompareInput> phaseInbound = new PhaseInbound<AnalyzeAndCompareInput>(
                analyzeAndCompareInput);
        analyzeAndCompareProcessor.process(phaseInbound);

        // Screen update processing is performed in the Observer receives the
        // event notification from the core
        try {
            // Wait until the core side to notify the INIT.
            // In response in the observer, INIT to perform beginTask
            starter.await();
        } catch (InterruptedException e) {
            String message = Activator
                    .getResourceString(JavaAnalyzeAndCompareJob.class.getName()
                            + ".err.msg.StarterErr");
            Activator.log(e, message);
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
        }

        try {
            phaseCountDownLatch.await();
        } catch (InterruptedException e) {
            String message = Activator
                    .getResourceString(JavaAnalyzeAndCompareJob.class.getName()
                            + ".err.msg.InterruptedErr");
            Activator.log(e, message);
            return new Status(IStatus.ERROR, Activator.PLUGIN_ID, message);
        }

        monitor.done();
        if (observer.exceptionOccurred()) {
            String message = Activator
                    .getResourceString(JavaAnalyzeAndCompareJob.class.getName()
                            + ".err.msg.ExceptionOccurred");
            MultiStatus multiStatus = new MultiStatus(Activator.PLUGIN_ID,
                    IStatus.ERROR, null, new WSearchBizException(
                            TYPE.SRC_PARSER_AND_COMPARE, message));
            if (observer.getExceptionList() != null) {
                for (Throwable e : observer.getExceptionList()) {
                    MultiStatus status = new MultiStatus(Activator.PLUGIN_ID,
                            IStatus.ERROR, null, e);
                    if (e != null && e.getStackTrace() != null) {
                        for (StackTraceElement element : e.getStackTrace()) {
                            status.add(new Status(IStatus.ERROR,
                                    Activator.PLUGIN_ID, element.toString()));
                        }
                    }
                    multiStatus.add(status);
                }
            }

            return multiStatus;
        }
        if (monitor.isCanceled()) {
            return Status.CANCEL_STATUS;
        }
        return Status.OK_STATUS;
    }
}
