/*
 * KnowhowXmlConvertFacade.java
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

import org.tubame.knowhow.biz.logic.PortabilityKnowhowFacade;
import org.tubame.knowhow.biz.logic.converter.PortabilityKnowhowConverter;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow;
import org.tubame.portability.exception.JbmException;
import org.tubame.portability.util.resource.MessageUtil;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * Facade class to convert the know-how XML.<br/>
 * Generate a checklist information XML file and search information from CSV
 * files know-how XML.<br/>
 */
public class KnowhowXmlConvertFacade {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(KnowhowXmlConvertFacade.class);

    /**
     * Constructor.<br/>
     * It can not be instantiated from another class.<br/>
     * 
     */
    protected KnowhowXmlConvertFacade() {
        super();
    }

    /**
     * Converted the know-how XML.<br/>
     * 
     * @param filePath
     *            Know-how XML file path
     * @throws JbmException
     *             Portable study tool exception
     */
    public void convertSearchFiles(String filePath) throws JbmException {
        // xml reading
        PortabilityKnowhow knowhow;
        try {
            // Know-how xml conversion
            LOGGER.info(MessageUtil.LOG_READ_KNOWHOW_XML);
            knowhow = PortabilityKnowhowFacade.readFullPath(filePath);
            PortabilityKnowhowConverter convert = PortabilityKnowhowFacade
                    .getPortabilityKnowhowConverter(knowhow);

            LOGGER.info(MessageUtil.LOG_CREATE_SEARCH_KEYWORD_CSV);
            // Search file generation
            CreateKeywordSearchFile.xmlToCsv(convert);

            // CheckListInfomation.xml file generation
            LOGGER.info(MessageUtil.LOG_CREATE_CHECKLISTINFO_XML);
            CreateCheckListInfomationFile.xmlToXml(convert);
        } catch (JbmException e) {
            throw e;
        } catch (org.tubame.knowhow.biz.exception.JbmException e) {
            throw new JbmException(e.getMessage(), e);
        }
    }
}
