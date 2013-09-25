/*
 * PortabilityKnowhowWrite.java
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
package org.tubame.knowhow.biz.logic.io;

import java.io.IOException;

import org.tubame.knowhow.biz.exception.JbmException;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow;

/**
 * Know-how XML file write interface.<br/>
 * Defines a method for writing a transplant know-how.<br/>
 */
public interface PortabilityKnowhowWrite {

    /**
     * Writing know-how XML.<br/>
     * Generates an XML from transplant class know-how, make the output to the
     * location specified.<br/>
     * 
     * @param filepath
     *            Output XML path
     * @param portabilityKnowhow
     *            Transplant class know-how
     * @throws JbmException
     *             JBM exception
     * @throws IOException
     *             IO exception
     */
    void write(String filepath, PortabilityKnowhow portabilityKnowhow)
            throws JbmException, IOException;
}
