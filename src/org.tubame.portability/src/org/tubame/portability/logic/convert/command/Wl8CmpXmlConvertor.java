/*
 * Wl8CmpXmlConvertor.java
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
package org.tubame.portability.logic.convert.command;

import java.util.List;

import javax.xml.bind.JAXBException;

import org.tubame.portability.exception.JbmException;
import org.tubame.portability.model.generated.jboss.jbosscmp.CmpField;
import org.tubame.portability.model.generated.jboss.jbosscmp.ColumnName;
import org.tubame.portability.model.generated.jboss.jbosscmp.Defaults;
import org.tubame.portability.model.generated.jboss.jbosscmp.EnterpriseBeans;
import org.tubame.portability.model.generated.jboss.jbosscmp.Entity;
import org.tubame.portability.model.generated.jboss.jbosscmp.FieldName;
import org.tubame.portability.model.generated.jboss.jbosscmp.JbosscmpJdbc;
import org.tubame.portability.model.generated.jboss.jbosscmp.ObjectFactory;
import org.tubame.portability.model.generated.model.ConvertType;
import org.tubame.portability.model.generated.model.WebLogic;
import org.tubame.portability.model.generated.weblogic.wlcmp.wlv8.FieldMap;
import org.tubame.portability.model.generated.weblogic.wlcmp.wlv8.TableMap;
import org.tubame.portability.model.generated.weblogic.wlcmp.wlv8.WeblogicRdbmsBean;
import org.tubame.portability.model.generated.weblogic.wlcmp.wlv8.WeblogicRdbmsJar;
import org.tubame.portability.util.StringUtil;
import org.xml.sax.SAXException;

/**
 * Conversion to jboss-cmp.xml from weblogic-cmp-rdbms.xml of WebLogic Version
 * 8.<br/>
 */
public class Wl8CmpXmlConvertor extends AbstractXmlConvert {
    /**
     * Constructor.<br/>
     * Call the constructor of @ link AbstractXmlConvert} .<br/>
     * 
     * @throws JAXBException
     *             XML exception when
     * @throws SAXException
     *             Perth exceptional cases
     */
    public Wl8CmpXmlConvertor() throws JAXBException, SAXException {
        super(AbstractXmlConvert.Type.WEBLOGIC8_CMP_RDBMS_JAR_XML_CONVERSE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object analysis(Object tagetObj, String pluginDir)
            throws JbmException {

        ObjectFactory objectFactory = new ObjectFactory();
        // What makes the xml file

        JbosscmpJdbc jbosscmpJdbc = objectFactory.createJbosscmpJdbc();

        WeblogicRdbmsJar weblogicRdbmsJar = (WeblogicRdbmsJar) tagetObj;
        List<WeblogicRdbmsBean> weblogicRdbmsBeans = weblogicRdbmsJar
                .getWeblogicRdbmsBean();
        // V5.1 delete
        // List<WeblogicRdbmsRelation> weblogicRdbmsRelation = weblogicRdbmsJar
        // .getWeblogicRdbmsRelation();

        EnterpriseBeans createEnterpriseBeans = objectFactory
                .createEnterpriseBeans();
        // Make the tag enterprise-beans

        List<Entity> entityList = createEnterpriseBeans.getEntity();
        String webLogicCreateDefaultDbmsTables = weblogicRdbmsJar
                .getCreateDefaultDbmsTables();
        // Creating a tag WeblogicRdbmsBean part
        for (WeblogicRdbmsBean weblogicRdbmsBean : weblogicRdbmsBeans) {
            jbosscmpJdbc = createJbossCmpJdbc(weblogicRdbmsBean, jbosscmpJdbc,
                    objectFactory, createEnterpriseBeans, entityList,
                    webLogicCreateDefaultDbmsTables);
        }

        return jbosscmpJdbc;
    }

    /**
     * Generate JbosscmpJdbc element.<br/>
     * 
     * @param weblogicRdbmsBean
     *            XML element:WeblogicRdbmsBean
     * @param jbosscmpJdbc
     *            XML element:JbosscmpJdbc
     * @param objectFactory
     *            ObjectFactory
     * @param createEnterpriseBeans
     *            XML element:
     * @param entityList
     *            XML element:List of Entity
     * @param webLogicCreateDefaultDbmsTables
     *            webLogicCreateDefaultDbmsTables
     * @return XML element:JbosscmpJdbc
     */
    JbosscmpJdbc createJbossCmpJdbc(WeblogicRdbmsBean weblogicRdbmsBean,
            JbosscmpJdbc jbosscmpJdbc, ObjectFactory objectFactory,
            EnterpriseBeans createEnterpriseBeans, List<Entity> entityList,
            String webLogicCreateDefaultDbmsTables) {

        String ejbName = weblogicRdbmsBean.getEjbName();
        String dsName = weblogicRdbmsBean.getDataSourceName();
        List<TableMap> tablemap = weblogicRdbmsBean.getTableMap();

        Defaults creatDefaults = objectFactory.createDefaults();
        // Make the tag deaults
        creatDefaults.setDatasource(dsName);
        creatDefaults.setDatasourceMapping(TODO);
        if (!StringUtil.isEmpty(webLogicCreateDefaultDbmsTables)) {
            creatDefaults.setCreateTable(TODO);
            creatDefaults.setRemoveTable(TODO);
        }
        jbosscmpJdbc.setDefaults(creatDefaults);

        // Written to file here

        Entity entity = new Entity();
        entity = getEntity(entity, ejbName, tablemap);
        entityList.add(entity);
        jbosscmpJdbc.setEnterpriseBeans(createEnterpriseBeans);

        return jbosscmpJdbc;
    }

    // Make the tag part entity
    /**
     * Generate an Entity element.<br/>
     * 
     * @param entity
     *            XML element:Entity
     * @param ejbName
     *            EJB name
     * @param tablemap
     *            XML element:List of TableMap
     * @return Entity element
     */
    public Entity getEntity(Entity entity, String ejbName,
            List<TableMap> tablemap) {

        entity.setEjbName(ejbName);
        // entity.setCreateTable("true");

        List<CmpField> cmpfield = entity.getCmpField();

        for (TableMap tableMap2 : tablemap) {
            String tablename = tableMap2.getTableName();
            List<FieldMap> fieldmap = tableMap2.getFieldMap();
            entity.setCreateTable("true");
            entity.setTableName(tablename);

            for (FieldMap fieldMap2 : fieldmap) {

                CmpField cmpfield2 = new CmpField();
                FieldName fieldName = new FieldName();
                ColumnName columnName = new ColumnName();
                org.tubame.portability.model.generated.weblogic.wlcmp.wlv8.CmpField cmpField3 = fieldMap2
                        .getCmpField();
                fieldName.setvalue(cmpField3.getvalue());
                columnName.setvalue(fieldMap2.getDbmsColumn());
                cmpfield2.setFieldName(fieldName);
                cmpfield2.setColumnName(columnName);
                cmpfield.add(cmpfield2);
            }
        }
        return entity;

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
        return WebLogic.V_8;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWriteFileName() {
        return AbstractXmlConvertBase.JBOSS_CMP_XML_FILENAME;
    }

}
