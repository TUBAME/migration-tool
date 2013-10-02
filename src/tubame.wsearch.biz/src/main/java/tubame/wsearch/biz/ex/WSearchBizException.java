/*
 * WSearchBizException.java
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
package tubame.wsearch.biz.ex;

import tubame.wsearch.biz.WSPhaseService;

/**
 * It is a class that will handle exceptions that occur in the logic of the
 * general-purpose search.<br/>
 */
public class WSearchBizException extends Exception {

    /**
     * Kind of the process where the exception occurred
     */
    WSPhaseService.TYPE type;

    /**
     * Unique ID to be used when serializing
     */
    private static final long serialVersionUID = 1L;

    /**
     * Constructor.<br/>
     * No operation.<br/>
     */
    public WSearchBizException() {
    }

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param type
     *            Kind of the process
     * @param message
     *            Message
     */
    public WSearchBizException(WSPhaseService.TYPE type, String message) {
        super(message);
        this.type = type;
    }

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param type
     *            Kind of the process
     * @param cause
     *            Exception to the underlying
     */
    public WSearchBizException(WSPhaseService.TYPE type, Throwable cause) {
        super(cause);
        this.type = type;
    }

    /**
     * Constructor.<br/>
     * No operation.<br/>
     * 
     * @param type
     *            Kind of the process
     * @param message
     *            Message
     * @param cause
     *            Exception to the underlying
     */
    public WSearchBizException(WSPhaseService.TYPE type, String message,
            Throwable cause) {
        super(message, cause);
        this.type = type;
    }

    /**
     * Get the type of treatment.<br/>
     * 
     * @return Kind of the process
     */
    public WSPhaseService.TYPE getType() {
        return type;
    }
}
