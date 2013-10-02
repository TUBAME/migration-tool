/*
 * PortabilityKnowhowFacade.java
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
package tubame.knowhow.biz.logic;

import java.io.IOException;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.logic.converter.PortabilityKnowhowConverter;
import tubame.knowhow.biz.logic.io.ConvertKnowhowFilePath;
import tubame.knowhow.biz.logic.io.PortabilityKnowhowRead;
import tubame.knowhow.biz.logic.io.PortabilityKnowhowWrite;
import tubame.knowhow.biz.logic.io.PortabilityKnowhowXMLReader;
import tubame.knowhow.biz.logic.io.PortabilityKnowhowXMLWriter;
import tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

/**
 * Facade to implement the read / write of transplant know-how.<br/>
 */
public final class PortabilityKnowhowFacade {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PortabilityKnowhowFacade.class);
    /** ProtabilityKnowhowWrite */
    private static PortabilityKnowhowWrite protabilityKnowhowWrite = new PortabilityKnowhowXMLWriter();

    /**
     * Constructor.<br/>
     */
    private PortabilityKnowhowFacade() {
        // no operation
    }

    /**
     * Call the {@link PortabilityKnowhowXMLReader}, to implement the reading
     * process.<br/>
     * 
     * @param filePathPath
     *            of the XML file to be read subject
     * @return Transplant class know-how
     * @throws JbmException
     *             JBM exception
     */
    public static PortabilityKnowhow read(String filePath) throws JbmException {
        LOGGER.debug("[ProtabilityKnowhow]=" + filePath);
        return PortabilityKnowhowFacade.getPortabilityKnowhowRead().read(
                filePath);
    }

    /**
     * Call the {@link PortabilityKnowhowXMLReader}, to implement the reading
     * process.<br/>
     * 
     * @param filePath
     *            Path of the XML file to be read subject
     * @return Transplant class know-how
     * @throws JbmException
     *             JBM exception
     */
    public static PortabilityKnowhow readFullPath(String filePath)
            throws JbmException {
        LOGGER.debug("[ProtabilityKnowhow]=" + filePath);
        return PortabilityKnowhowFacade.getPortabilityKnowhowReadFullPath()
                .read(filePath);
    }

    /**
     * Get the know-how XML file read interface.<br/>
     * 
     * @return PortabilityKnowhowRead
     */
    protected static PortabilityKnowhowRead getPortabilityKnowhowRead() {
        return new PortabilityKnowhowXMLReader();
    }

    /**
     * Get the know-how XML file read interface.<br/>
     * 
     * @return PortabilityKnowhowRead
     */
    protected static PortabilityKnowhowRead getPortabilityKnowhowReadFullPath() {
        return new ConvertKnowhowFilePath();
    }

    /**
     * Make call {@link PortabilityKnowhowXMLWriter}, the writing process.<br/>
     * 
     * @param filepath
     *            Path of the XML file to be written target
     * @param portabilityKnowhow
     *            Transplant know-how
     * @throws JbmException
     *             JBM exception
     */
    public static void write(String filepath,
            PortabilityKnowhow portabilityKnowhow) throws JbmException {
        try {
            PortabilityKnowhowFacade.getPortabikityKnowhowWrite().write(
                    filepath, portabilityKnowhow);
        } catch (IOException e) {
            throw new JbmException(
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.ERROR_FILE_SAVE_FAILURE),
                    e);
        }
    }

    /**
     * Get the know-how XML file writing logic.<br/>
     * 
     * @return PortabilityKnowhowWrite
     */
    protected static PortabilityKnowhowWrite getPortabikityKnowhowWrite() {
        return PortabilityKnowhowFacade.protabilityKnowhowWrite;
    }

    /**
     * Get the know-how transplant convert data.<br/>
     * 
     * @param portabilityKnowhow
     *            PortabilityKnowhow
     * @return PortabilityKnowhowConverter
     */
    public static PortabilityKnowhowConverter getPortabilityKnowhowConverter(
            PortabilityKnowhow portabilityKnowhow) {
        return new PortabilityKnowhowConverter(portabilityKnowhow);
    }
}
