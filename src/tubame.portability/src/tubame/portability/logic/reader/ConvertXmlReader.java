/*
 * ConvertXmlReader.java
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
package tubame.portability.logic.reader;

import java.io.File;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.exception.JbmException;
import tubame.portability.exception.JbmException.ERROR_LEVEL;
import tubame.portability.model.generated.model.JbossMigrationConvertTool;
import tubame.portability.util.resource.MessageUtil;

/**
 * Provides read function the result of the conversion status.<br/>
 * Read The JAXB format conversion status file.<br/>
 */
public class ConvertXmlReader implements ConvertReader {
    /**
     * Package path to JbossMigrationConvertTool
     */
    private static final String MODEL_PATH = "tubame.portability.model.generated.model"; //$NON-NLS-1$
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConvertXmlReader.class);

    /**
     * {@inheritDoc}
     */
    @Override
    public JbossMigrationConvertTool read(String filePath) throws JbmException {
        LOGGER.debug("[filePath]" + filePath);
        JAXBContext context;
        try {
            context = JAXBContext.newInstance(ConvertXmlReader.MODEL_PATH);
            Unmarshaller unmarshaller = context.createUnmarshaller();
            Object unmarshal = unmarshaller.unmarshal(new File(filePath));
            if (unmarshal instanceof JbossMigrationConvertTool) {
                return (JbossMigrationConvertTool) unmarshal;
            }
        } catch (JAXBException e) {
            throw new JbmException(e, LOGGER, ERROR_LEVEL.ERROR, new String[] {
                    MessageUtil.ERR_CONVERT_READ, filePath });
        }
        return null;
    }
}
