/*
 * AbstractConvertDomain.java
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
package org.tubame.portability.logic.convert;

import java.util.ArrayList;
import java.util.List;

import javax.xml.bind.JAXBException;

import org.eclipse.core.runtime.IProgressMonitor;
import org.tubame.portability.exception.JbmException;
import org.tubame.portability.exception.JbmException.ERROR_LEVEL;
import org.tubame.portability.logic.convert.command.Convertor;
import org.tubame.portability.logic.convert.command.WL5EjbJarXmlConvertor;
import org.tubame.portability.logic.convert.command.WL6EjbJarXmlConvertor;
import org.tubame.portability.logic.convert.command.WL8EjbJarXmlConvertor;
import org.tubame.portability.logic.convert.command.Wl5CmpXmlConvertor;
import org.tubame.portability.logic.convert.command.Wl6CmpXmlConvertor;
import org.tubame.portability.logic.convert.command.Wl8CmpXmlConvertor;
import org.tubame.portability.model.generated.model.ConvertType;
import org.tubame.portability.model.generated.model.MigrationItem;
import org.tubame.portability.model.generated.model.WebLogic;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

/**
 * Abstract class that calls the conversion process.<br/>
 * Static nested class that creates a list of the converter.<br/>
 */
public abstract class AbstractConvertDomain {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractConvertDomain.class);

    /**
     * WebLogic Version
     */
    private final WebLogic webLogic;
    /**
     * Converter list
     */
    private final List<Convertor> convertorList;

    /**
     * Convert type
     */
    private final ConvertType convertType;

    /**
     * Converter factory (static nested class).<br/>
     * Generate all convert.<br/>
     */
    static class ConvertorsFactory {
        /**
         * Generate all convert.<br/>
         * 
         * @return Convert all type list
         * @throws JbmException
         *             Convert instantiation failure
         */
        public static List<Convertor> create() throws JbmException {
            List<Convertor> list = new ArrayList<Convertor>();
            try {
                list.add(new Wl5CmpXmlConvertor());
                list.add(new Wl6CmpXmlConvertor());
                list.add(new Wl8CmpXmlConvertor());
                list.add(new WL5EjbJarXmlConvertor());
                list.add(new WL6EjbJarXmlConvertor());
                list.add(new WL8EjbJarXmlConvertor());
                return list;
            } catch (JAXBException e) {
                throw new JbmException(e, LOGGER, ERROR_LEVEL.ERROR,
                        e.getMessage());
            } catch (SAXException e) {
                throw new JbmException(e, LOGGER, ERROR_LEVEL.ERROR,
                        e.getMessage());
            }
        }
    }

    /**
     * Get the converter list.<br/>
     * Get all converter the target list.<br/>
     * 
     * @return Converter list
     */
    public List<Convertor> getConvertorList() {
        return convertorList;
    }

    /**
     * It performs the conversion execution. <br/>
     * When implementing this class, override this method to perform the
     * conversion.<br/>
     * 
     * @param monitor
     *            Monitor
     * @param migrationItem
     *            XML object
     * @param projectHome
     *            Target project root path
     * @param outputFolder
     *            Output directory path
     * @param pluginDir
     *            Plug-in directory path
     * @throws InterruptedException
     *             Cancellation
     */
    public abstract void doExecute(IProgressMonitor monitor,
            MigrationItem migrationItem, String projectHome,
            String outputFolder, String pluginDir) throws InterruptedException;

    /**
     * Constructor.<br/>
     * 
     * @param webLogic
     *            WebLogic Version
     * @param convertType
     *            Conversion type
     * @throws JbmException
     *             Converter generation failure
     */
    public AbstractConvertDomain(WebLogic webLogic, ConvertType convertType)
            throws JbmException {
        this.webLogic = webLogic;
        this.convertType = convertType;
        convertorList = ConvertorsFactory.create();
    }

    /**
     * Get the WebLogic version.<br/>
     * 
     * @return WebLogic Version
     */
    public WebLogic getWebLogic() {
        return webLogic;
    }

    /**
     * Get the conversion type.<br/>
     * 
     * @return convertType version
     */
    public ConvertType getConvertType() {
        return convertType;
    }
}
