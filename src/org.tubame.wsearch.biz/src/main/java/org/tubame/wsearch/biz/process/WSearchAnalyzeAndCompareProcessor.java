/*
 * WSearchAnalyzeAndCompareProcessor.java
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

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.concurrent.CountDownLatch;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.wsearch.biz.WSPhaseService;
import org.tubame.wsearch.biz.WSPhaseService.TYPE;
import org.tubame.wsearch.biz.analyzer.Analyzer;
import org.tubame.wsearch.biz.analyzer.ParserResult;
import org.tubame.wsearch.biz.ex.WSearchBizException;
import org.tubame.wsearch.biz.model.AbstractWSPhaseTask;
import org.tubame.wsearch.biz.model.AnalyzeAndCompareInput;
import org.tubame.wsearch.biz.model.PhaseCountDownLatch;
import org.tubame.wsearch.biz.model.PhaseInbound;
import org.tubame.wsearch.biz.process.obsvr.AbstractProcessObserver;
import org.tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * It is a process class that the analysis of transplant target source, the
 * process of comparison with porting.<br/>
 */
public class WSearchAnalyzeAndCompareProcessor extends
        AbstractProcessor<AnalyzeAndCompareInput, String> {

    /**
     * The type that defines the type of the parser to parse the process.<br/>
     */
    public enum PARSER {
        Java, JSP, XML
    }

    /**
     * Task to be processed
     */
    private AbstractWSPhaseTask analyzeAndCompareTask;

    /**
     * Input value
     */
    private PhaseInbound<AnalyzeAndCompareInput> inbound;

    /**
     * Type of parser
     */
    private PARSER parserType;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(WSearchAnalyzeAndCompareProcessor.class);

    /**
     * Get the type of the parser.
     * 
     * @return Type of parser
     */
    public PARSER getParserType() {
        return parserType;
    }

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param threadCnt
     *            Number of processed
     * @param starter
     *            Latch to support the synchronization of the initial processing
     * @param countDownLatch
     *            Latch to support synchronization
     * @param parser
     *            Type of parser
     */
    public WSearchAnalyzeAndCompareProcessor(int threadCnt,
            CountDownLatch starter, PhaseCountDownLatch countDownLatch,
            PARSER parser) {
        super(threadCnt);
        analyzeAndCompareTask = new AnalyzeAndCompareTask(starter,
                countDownLatch);
        this.parserType = parser;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String process(PhaseInbound<AnalyzeAndCompareInput> phaseInbound) {
        this.inbound = phaseInbound;
        executor.submit(analyzeAndCompareTask);
        return null;
    }

    /**
     * And analysis of transplant target source, and is a task of processing
     * class comparison with the porting.<br/>
     */
    class AnalyzeAndCompareTask extends AbstractWSPhaseTask {

        /**
         * Constructor.<br/>
         * No operation.<br/>
         * 
         * @param starter
         *            Latch to support the synchronization of the initial
         *            processing
         * @param countDownLatch
         *            Latch to support synchronization
         */
        public AnalyzeAndCompareTask(CountDownLatch starter,
                PhaseCountDownLatch countDownLatch) {
            super(WSPhaseService.TYPE.SRC_PARSER_AND_COMPARE, starter,
                    countDownLatch);
        }

        /**
         * {@inheritDoc}
         */
        @Override
        public Object call() {
            LOGGER.info(MessageUtil
                    .getResourceString("debug.msg.start.process")
                    + this.getClass().getName() + "#call()");
            try {
                Map<String, List<String>> targetsMap = inbound.getValue()
                        .getTargetsMap();
                if (this.starter != null) {
                    // Wait a background thread until the INIT notification.
                    int targetCount = 0;
                    for (List<String> targets : targetsMap.values()) {
                        targetCount += targets.size();
                    }
                    this.listener.update(null,
                            new AbstractWSPhaseTask.CurrentStatus(
                                    AbstractWSPhaseTask.STATUS.INIT,
                                    targetCount));
                    this.starter.countDown();
                }

                while (this.getCountDownLatch().getCount() > 0) {
                    if (this.listener.isCanceled()) {
                        cancel();
                        while (this.getCountDownLatch().getCount() > 0) {
                            this.getCountDownLatch().countDown();
                        }
                        return null;
                    }

                    // Get the search file list from counter.
                    String target = null;
                    String extension = null;
                    int counter = this.getCountDownLatch().getCounter();
                    for (String ext : inbound.getValue().getExtensions()) {
                        List<String> targets = targetsMap.get(ext);
                        if (targets != null) {
                            if (targets.size() > counter) {
                                target = targets.get(counter);
                                extension = ext;
                                break;
                            } else {
                                counter -= targets.size();
                            }
                        }
                    }

                    // It can not be
                    if ((target == null) || (extension == null)) {
                        AbstractWSPhaseTask.CurrentStatus status = new AbstractWSPhaseTask.CurrentStatus(
                                AbstractWSPhaseTask.STATUS.ERR_EXIT, null, null);
                        status.setException(new WSearchBizException(
                                getType(),
                                MessageUtil
                                        .getResourceString(WSearchAnalyzeAndCompareProcessor.class
                                                .getName() + ".err.TargetNull")));
                        this.listener.update(null, status);
                        cancel();
                        while (this.getCountDownLatch().getCount() > 0) {
                            this.getCountDownLatch().countDown();
                        }
                        return null;
                    }

                    // Set the filter to be used in the Analyzer that is created
                    // when updating.
                    this.listener.setIgnoreList(inbound.getValue()
                            .getFiltersMap().get(extension));
                    this.listener.update(null,
                            new AbstractWSPhaseTask.CurrentStatus(
                                    AbstractWSPhaseTask.STATUS.START, target,
                                    null));

                    // To notify the status to the listener starts from UI, to
                    // produce a Parser object side UI.
                    // Run Analyze who have these by setting the UI.
                    Analyzer analyzer = this.getCountDownLatch().getAnalyzer();

                    if (analyzer != null) {
                        try {
                            LOGGER.trace(MessageUtil
                                    .getResourceString(WSearchAnalyzeAndCompareProcessor.class
                                            .getName() + ".debug.start.analyze")
                                    + target);
                            analyzer.analyze();
                            LOGGER.trace(MessageUtil
                                    .getResourceString(WSearchAnalyzeAndCompareProcessor.class
                                            .getName() + ".debug.end.analyze")
                                    + target);
                        } catch (WSearchBizException e) {
                            this.getCountDownLatch()
                                    .getComparer()
                                    .writeErrorResult(
                                            target,
                                            MessageUtil.getResourceString(WSearchAnalyzeAndCompareProcessor.class
                                                    .getName()
                                                    + ".message.parseerror"));
                            this.getCountDownLatch().countDown();
                            this.listener.update(null,
                                    new AbstractWSPhaseTask.CurrentStatus(
                                            AbstractWSPhaseTask.STATUS.END,
                                            target, null));

                            LOGGER.debug(e.getMessage(), e);
                            continue;
                        }
                        for (Integer type : getResultTypes()) {
                            if (this.listener.isCanceled()) {
                                cancel();
                                while (this.getCountDownLatch().getCount() > 0) {
                                    this.getCountDownLatch().countDown();
                                }
                                return null;
                            }

                            Map<String, List<ParserResult>> analyzedMap = analyzer
                                    .getAnalyzedMap(type);

                            for (Map.Entry<String, List<ParserResult>> entry : analyzedMap
                                    .entrySet()) {
                                for (ParserResult parserResult : entry
                                        .getValue()) {
                                    if (this.listener.isCanceled()) {
                                        cancel();
                                        while (this.getCountDownLatch()
                                                .getCount() > 0) {
                                            this.getCountDownLatch()
                                                    .countDown();
                                        }
                                        return null;
                                    }

                                    LOGGER.trace(MessageUtil
                                            .getResourceString(WSearchAnalyzeAndCompareProcessor.class
                                                    .getName()
                                                    + ".debug.start.compare")
                                            + parserResult.toString());
                                    this.getCountDownLatch().getComparer()
                                            .compare(parserResult, type);
                                    LOGGER.trace(MessageUtil
                                            .getResourceString(WSearchAnalyzeAndCompareProcessor.class
                                                    .getName()
                                                    + ".debug.end.compare")
                                            + parserResult.toString());
                                }
                            }
                        }
                    }

                    this.getCountDownLatch().countDown();
                    this.listener.update(null,
                            new AbstractWSPhaseTask.CurrentStatus(
                                    AbstractWSPhaseTask.STATUS.END, target,
                                    null));
                }

                LOGGER.info(MessageUtil
                        .getResourceString("debug.msg.end.process")
                        + this.getClass().getName() + "#call()");
                return null;
            } catch (Exception e) {
                // catch here all if an exception such as RuntimeExceptio
                // occurs.
                LOGGER.error(
                        MessageUtil.getResourceString(this.getClass().getName()
                                + ".exception.load"), e);
                this.listener.execptionHandle(e);
                cancel();
                while (this.getCountDownLatch().getCount() > 0) {
                    this.getCountDownLatch().countDown();
                }
                return null;
            }
        }
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public TYPE getType() {
        return this.analyzeAndCompareTask.getType();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void setObserver(AbstractProcessObserver observer) {
        this.analyzeAndCompareTask.setListener(observer);
    }

    /**
     * Get a list of the result type.
     * 
     * @return Type List
     */
    private List<Integer> getResultTypes() {
        List<Integer> types = new ArrayList<Integer>();
        if (this.parserType == PARSER.Java) {
            types.add(Analyzer.TYPE_JAVA);
        } else if (this.parserType == PARSER.JSP) {
            types.add(Analyzer.TYPE_JSP_IMPORT);
            types.add(Analyzer.TYPE_JSP_TAGLIB);
            types.add(Analyzer.TYPE_JSP_USEBEAN);
        } else if (this.parserType == PARSER.XML) {
            types.add(Analyzer.TYPE_XML_DTD);
            types.add(Analyzer.TYPE_XML_XMLNS);
            types.add(Analyzer.TYPE_XML_XSD);
        }
        return types;
    }
}
