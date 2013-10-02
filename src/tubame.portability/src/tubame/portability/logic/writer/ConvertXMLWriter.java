/*
 * ConvertXMLWriter.java
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
package tubame.portability.logic.writer;

import java.io.File;
import java.util.List;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.exception.JbmException;
import tubame.portability.exception.JbmException.ERROR_LEVEL;
import tubame.portability.model.generated.model.JbossMigrationConvertTool;
import tubame.portability.model.generated.model.MigrationItem;
import tubame.portability.model.generated.model.ObjectFactory;
import tubame.portability.model.generated.model.PriorityType;
import tubame.portability.model.generated.model.JbossMigrationConvertTool.MigrationItems;
import tubame.portability.util.resource.MessageUtil;

/**
 * Write a status file conversion result.<br/>
 */
public class ConvertXMLWriter implements ConvertWriter {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(ConvertXMLWriter.class);
    /**
     * Context package
     */
    private static final String CONTEXT_PATH = "tubame.portability.model.generated.model";

    /**
     * {@inheritDoc}
     */
    @Override
    public void write(List<MigrationItem> migrationItemList, String filePath)
            throws JbmException {
        LOGGER.debug("[migrationItemList]" + migrationItemList + "[filePath]"
                + filePath);
        ObjectFactory factory = new ObjectFactory();
        JbossMigrationConvertTool top = factory
                .createJbossMigrationConvertTool();

        // Creating MigrationItems
        MigrationItems items = factory
                .createJbossMigrationConvertToolMigrationItems();
        List<MigrationItem> itemList = items.getMigrationItem();

        for (MigrationItem migrationItem : migrationItemList) {
            migrationItem.setPriorityType(PriorityType.UNKNOWN);
            itemList.add(migrationItem);
        }
        top.setMigrationItems(items);

        try {
            JAXBContext context = JAXBContext.newInstance(CONTEXT_PATH);
            Marshaller marshaller = context.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.marshal(top, new File(filePath));
        } catch (JAXBException e) {
            throw new JbmException(e, LOGGER, ERROR_LEVEL.ERROR,
                    new String[] { MessageUtil.ERR_CONVER_XML_WRITE });
        }
    }
}
