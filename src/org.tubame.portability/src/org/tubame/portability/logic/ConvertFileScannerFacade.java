/*
 * ConvertFileScannerFacade.java
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
package org.tubame.portability.logic;

import java.util.List;

/**
 * Facade to scan the result of the conversion.<br/>
 */
public class ConvertFileScannerFacade {
    /**
     * Get a file scanner class conversion result.<br/>
     * 
     * @param filePathLsit
     *            File path list
     * @return File Scanner class conversion result
     */
    public ConvertFileScanner getScanner(List<String> filePathLsit) {
        return new ConvertFileScanner(filePathLsit);
    }
}
