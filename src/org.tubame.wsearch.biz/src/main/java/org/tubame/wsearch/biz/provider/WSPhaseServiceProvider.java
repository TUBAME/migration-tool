/*
 * WSPhaseServiceProvider.java
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
package org.tubame.wsearch.biz.provider;

import org.tubame.wsearch.biz.WSAnalyzerAndCompareService;
import org.tubame.wsearch.biz.WSDestLibMetaDataGeneratorService;
import org.tubame.wsearch.biz.WSPhaseService;
import org.tubame.wsearch.biz.WSPhaseService.TYPE;

/**
 * It is the provider class that supplies services to be used for each
 * processing phase a general-purpose search.<br/>
 */
public class WSPhaseServiceProvider implements WSServiceProvider {

    /**
     * {@inheritDoc}
     */
    public WSPhaseService factory(TYPE type) {
        if (type == TYPE.DESTLIB_METADATA_GENERATE) {
            return new WSDestLibMetaDataGeneratorService();
        } else if (type == TYPE.SRC_PARSER_AND_COMPARE) {
            return new WSAnalyzerAndCompareService();
        }
        return null;
    }
}
