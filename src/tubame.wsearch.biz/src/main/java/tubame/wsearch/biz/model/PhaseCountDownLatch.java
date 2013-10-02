/*
 * PhaseCountDownLatch.java
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
package tubame.wsearch.biz.model;

import java.util.concurrent.CountDownLatch;

import tubame.wsearch.biz.analyzer.Analyzer;
import tubame.wsearch.biz.comparer.AbstractComparer;

/**
 * This is a class to help the synchronization process in the general-purpose
 * search process.<br/>
 */
public class PhaseCountDownLatch extends CountDownLatch {

    /**
     * Counters for counting the processed
     */
    private Integer counter;

    /**
     * Analyzer to be used for processing
     */
    private Analyzer analyzer;
    /**
     * Comparer that is used to process
     */
    private AbstractComparer comparer;

    /**
     * Set the analyzer.<br/>
     * 
     * @param analyzer
     *            Analyzer
     */
    public void setAnalyzer(Analyzer analyzer) {
        this.analyzer = analyzer;
    }

    /**
     * Get the analyzer.<br/>
     * 
     * @return Analyzer
     */
    public Analyzer getAnalyzer() {
        return analyzer;
    }

    /**
     * Set the comparer.<br/>
     * 
     * @param comparer
     *            Comparer
     */
    public void setComparer(AbstractComparer comparer) {
        this.comparer = comparer;
    }

    /**
     * Get the comparer.<br/>
     * 
     * @return Comparer
     */
    public AbstractComparer getComparer() {
        return this.comparer;
    }

    /**
     * Increment the processed counter.<br/>
     * 
     * @return Value of the counter after incrementing
     */
    synchronized public Integer increment() {
        this.counter = Integer.valueOf(counter + 1);
        return this.counter;

    }

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param count
     *            Number of processing
     */
    public PhaseCountDownLatch(int count) {
        super(count);
        counter = Integer.valueOf(0);
    }

    /**
     * Get the value of the current counter.<br/>
     * 
     * @return Current counter
     */
    public Integer getCounter() {
        return counter;
    }
}
