/*
 * Wl5CmpXmlConvertor.java
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
package tubame.portability.logic.convert.command;

import java.util.List;

import javax.xml.bind.JAXBException;

import tubame.portability.exception.JbmException;
import tubame.portability.model.generated.jboss.jbosscmp.CmpField;
import tubame.portability.model.generated.jboss.jbosscmp.ColumnName;
import tubame.portability.model.generated.jboss.jbosscmp.Defaults;
import tubame.portability.model.generated.jboss.jbosscmp.EnterpriseBeans;
import tubame.portability.model.generated.jboss.jbosscmp.Entity;
import tubame.portability.model.generated.jboss.jbosscmp.FieldName;
import tubame.portability.model.generated.jboss.jbosscmp.JbosscmpJdbc;
import tubame.portability.model.generated.jboss.jbosscmp.ObjectFactory;
import tubame.portability.model.generated.model.ConvertType;
import tubame.portability.model.generated.model.WebLogic;
import tubame.portability.model.generated.weblogic.wlcmp.wlv5.AttributeMap;
import tubame.portability.model.generated.weblogic.wlcmp.wlv5.ObjectLink;
import tubame.portability.model.generated.weblogic.wlcmp.wlv5.WeblogicRdbmsBean;

/**
 * Conversion to jboss-cmp.xml from weblogic-cmp-rdbms.xml of WebLogic Version
 * 5.<br/>
 */
public class Wl5CmpXmlConvertor extends AbstractXmlConvert {
    /**
     * Constructor. Call the constructor of {@link AbstractXmlConvert}.<br/>
     * 
     * @throws JAXBException
     *             XML exception
     */
    public Wl5CmpXmlConvertor() throws JAXBException {
        super(AbstractXmlConvert.Type.WEBLOGIC5_CMP_RDBMS_JAR_XML_CONVERSE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object analysis(Object tagetObj, String pluginDir)
            throws JbmException {
        /*
         * TargetObj has a value of each such poolname the WeblogicRdbmsBean
         * WeblogicRdbmsBean
         */
        WeblogicRdbmsBean weblogicRdbmsBean0 = (WeblogicRdbmsBean) tagetObj;

        ObjectFactory objectFactory = new ObjectFactory();
        // What makes the xml file

        JbosscmpJdbc jbosscmpJdbc = objectFactory.createJbosscmpJdbc();
        // Create a tag jboddcmp-jdbc to objectFactory within.

        EnterpriseBeans createEnterpriseBeans = objectFactory
                .createEnterpriseBeans();
        // Make the tag enterprise-beans

        List<Entity> entityList = createEnterpriseBeans.getEntity();
        // Pass to entityList of storage entity?

        jbosscmpJdbc = createJbossCmpJdbc(weblogicRdbmsBean0, jbosscmpJdbc,
                objectFactory, createEnterpriseBeans, entityList);

        return jbosscmpJdbc;
    }

    /**
     * Generate JbosscmpJdbc.<br/>
     * 
     * @param weblogicRdbmsBean0
     *            XML element:WeblogicRdbmsBean
     * @param jbosscmpJdbc
     *            XML element:JbosscmpJdbc
     * @param objectFactory
     *            ObjectFactory
     * @param createEnterpriseBeans
     *            XML element: EnterpriseBeans
     * @param entityList
     *            XML element: List of Entity
     * @return JbosscmpJdbc schema of the parent root
     */
    public JbosscmpJdbc createJbossCmpJdbc(
            WeblogicRdbmsBean weblogicRdbmsBean0, JbosscmpJdbc jbosscmpJdbc,
            ObjectFactory objectFactory, EnterpriseBeans createEnterpriseBeans,
            List<Entity> entityList) {

        String tableName = weblogicRdbmsBean0.getTableName();
        AttributeMap attr = weblogicRdbmsBean0.getAttributeMap();

        List<ObjectLink> objectLink = attr.getObjectLink();

        Defaults creatDefaults = objectFactory.createDefaults();
        // Make the tag deaults

        creatDefaults.setDatasource(TODO);
        creatDefaults.setDatasourceMapping(TODO);
        creatDefaults.setCreateTable(TODO);
        creatDefaults.setRemoveTable(TODO);
        jbosscmpJdbc.setDefaults(creatDefaults);
        // Be written to file here

        Entity entity = new Entity();
        entity.setEjbName(TODO);
        entity.setCreateTable("true");
        entity.setTableName(tableName);

        List<CmpField> cmpfList = entity.getCmpField();

        // Create a portion of the cmp-field
        for (ObjectLink link : objectLink) {
            CmpField cmpfield01 = new CmpField();
            FieldName fieldName = new FieldName();
            ColumnName columnName = new ColumnName();
            fieldName.setvalue(link.getBeanField());
            cmpfield01.setFieldName(fieldName);
            columnName.setvalue(link.getDbmsColumn());
            cmpfield01.setColumnName(columnName);

            cmpfList.add(cmpfield01);
        }

        entityList.add(entity);

        jbosscmpJdbc.setEnterpriseBeans(createEnterpriseBeans);
        return jbosscmpJdbc;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConvertType getConverType() {
        return ConvertType.EJB_CMP_XML_CONVERT;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public WebLogic getWebLogicVersion() {
        return WebLogic.V_5;
    }

}
