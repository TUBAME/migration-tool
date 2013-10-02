/*
 * InitializePotability.java
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
package tubame.portability.logic;

import java.util.concurrent.CountDownLatch;

import tubame.knowhow.biz.util.JaxbUtil;

/**
 * Class that controls the initial processing of know-how XML reading.<br/>
 */
public class InitializePotability {

    /** Initialization processing threads */
    private static volatile CountDownLatch latch;

    /**
     * Constructor.<br/>
     * 
     */
    private InitializePotability() {
        // no operation
    }

    /**
     * Do the initial processing of JAXB.<br/>
     * 
     */
    private static void init() {
        JaxbUtil.init();
    }

    /**
     * Get CountDownLatch.<br/>
     * 
     * @return CountDownLatch
     */
    private static CountDownLatch getLatch() {
        return InitializePotability.latch;
    }

    /**
     * To do the initial processing.<br/>
     * Processing is performed to generate each marshaler.<br/>
     * 
     */
    public static void initializeMarshaller() {
        // Only for the initialization process, the argument set to 1 processing
        latch = new CountDownLatch(1);
        // To develop and execute a thread
        new Thread(new Runnable() {
            /**
             * {@inheritDoc}
             */
            @Override
            public void run() {
                // Background processing
                InitializePotability.init();
                // Countdown initialization process is complete
                InitializePotability.getLatch().countDown();
            }
        }).start();
    }
}
