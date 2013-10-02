/*
 * Wl6CmpXmlConvertor.java
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

import org.xml.sax.SAXException;

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
import tubame.portability.model.generated.weblogic.wlcmp.wlv6.FieldMap;
import tubame.portability.model.generated.weblogic.wlcmp.wlv6.WeblogicRdbmsBean;
import tubame.portability.model.generated.weblogic.wlcmp.wlv6.WeblogicRdbmsJar;
import tubame.portability.util.StringUtil;

/**
 * Weblogic-cmp-rdbms conversion classes of WebLogic Version 6.<br/>
 * Conversion to jboss-cmp.xml from weblogic-cmp-rdbms.xml of WebLogic Version
 * 6.<br/>
 */
public class Wl6CmpXmlConvertor extends AbstractXmlConvert {
    /**
     * Constructor.<br/>
     * Call the constructor of {@link AbstractXmlConvert}.<br/>
     * 
     * @throws JAXBException
     *             XML exception when
     * @throws SAXException
     *             Perth exceptional cases
     */
    public Wl6CmpXmlConvertor() throws JAXBException, SAXException {
        super(AbstractXmlConvert.Type.WEBLOGIC6_CMP_RDBMS_JAR_XML_CONVERSE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object analysis(Object tagetObj, String pluginDir)
            throws JbmException {
        ObjectFactory objectFactory = new ObjectFactory();
        // what makes the xml file

        JbosscmpJdbc jbosscmpJdbc = objectFactory.createJbosscmpJdbc();
        WeblogicRdbmsJar weblogicRdbmsJar = (WeblogicRdbmsJar) tagetObj;
        List<WeblogicRdbmsBean> weblogicRdbmsBeans = weblogicRdbmsJar
                .getWeblogicRdbmsBean();
        // v5.1 Delete
        // List<WeblogicRdbmsRelation> weblogicRdbmsRelation = weblogicRdbmsJar
        // .getWeblogicRdbmsRelation();

        EnterpriseBeans createEnterpriseBeans = objectFactory
                .createEnterpriseBeans();

        // Make the tag enterprise-beans
        List<Entity> entityList = createEnterpriseBeans.getEntity();
        // Pass to entityList of storage entity?
        String webLogicCreateDefaultDbmsTables = weblogicRdbmsJar
                .getCreateDefaultDbmsTables();

        // Creating a tag WeblogicRdbmsBean part
        for (WeblogicRdbmsBean weblogicRdbmsBean : weblogicRdbmsBeans) {
            jbosscmpJdbc = createJbossCmpJdbc(weblogicRdbmsBean, jbosscmpJdbc,
                    objectFactory, entityList, webLogicCreateDefaultDbmsTables);
            jbosscmpJdbc.setEnterpriseBeans(createEnterpriseBeans);

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
     * @param entityList
     *            XML element:List of Entity
     * @param webLogicCreateDefaultDbmsTables
     *            webLogicCreateDefaultDbmsTables
     * @return XML element:JbosscmpJdbc
     */
    JbosscmpJdbc createJbossCmpJdbc(WeblogicRdbmsBean weblogicRdbmsBean,
            JbosscmpJdbc jbosscmpJdbc, ObjectFactory objectFactory,
            List<Entity> entityList, String webLogicCreateDefaultDbmsTables) {
        Entity entity = new Entity();
        List<CmpField> cmpfieldlist = entity.getCmpField();
        String ejbName = weblogicRdbmsBean.getEjbName();
        String dsName = weblogicRdbmsBean.getDataSourceName();
        String tableName = weblogicRdbmsBean.getTableName();
        List<FieldMap> fieldMap = weblogicRdbmsBean.getFieldMap();

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

        for (FieldMap fieldMap2 : fieldMap) {
            entity = getEntity(entity, ejbName, cmpfieldlist, tableName,
                    fieldMap2);
        }
        entityList.add(entity);
        // Add only times equal to the number of entity

        return jbosscmpJdbc;
    }

    /**
     * Generate an Entity element.<br/>
     * 
     * @param entity
     *            XML element:Entity
     * @param ejbName
     *            EJB name
     * @param cmpfield
     *            XML element:CmpField
     * @param tableName
     *            Table name
     * @param fieldMap2
     *            XML element:FieldMap
     * @return XML element:Entity
     */
    public Entity getEntity(Entity entity, String ejbName,
            List<CmpField> cmpfield, String tableName, FieldMap fieldMap2) {

        entity.setEjbName(ejbName);
        entity.setCreateTable("true");
        entity.setTableName(tableName);

        CmpField cmpfield2 = new CmpField();
        FieldName fieldName = new FieldName();
        ColumnName columnName = new ColumnName();

        tubame.portability.model.generated.weblogic.wlcmp.wlv6.CmpField cmpField3 = fieldMap2
                .getCmpField();

        fieldName.setvalue(cmpField3.getvalue());
        cmpfield2.setFieldName(fieldName);

        String dbmsColumn = fieldMap2.getDbmsColumn();
        columnName.setvalue(dbmsColumn);
        cmpfield2.setColumnName(columnName);
        cmpfield.add(cmpfield2);

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
        return WebLogic.V_6;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWriteFileName() {
        return AbstractXmlConvertBase.JBOSS_CMP_XML_FILENAME;
    }
}
