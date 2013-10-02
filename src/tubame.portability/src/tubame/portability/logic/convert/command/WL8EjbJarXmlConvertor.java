/*
 * WL8EjbJarXmlConvertor.java
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

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import tubame.portability.exception.JbmException;
import tubame.portability.exception.JbmException.ERROR_LEVEL;
import tubame.portability.model.generated.jboss.jboss.CachePolicyConf;
import tubame.portability.model.generated.jboss.jboss.ClusterConfig;
import tubame.portability.model.generated.jboss.jboss.ContainerCacheConf;
import tubame.portability.model.generated.jboss.jboss.ContainerConfiguration;
import tubame.portability.model.generated.jboss.jboss.ContainerConfigurations;
import tubame.portability.model.generated.jboss.jboss.ContainerPoolConf;
import tubame.portability.model.generated.jboss.jboss.EjbRef;
import tubame.portability.model.generated.jboss.jboss.EnterpriseBeans;
import tubame.portability.model.generated.jboss.jboss.Entity;
import tubame.portability.model.generated.jboss.jboss.InvokerProxyBinding;
import tubame.portability.model.generated.jboss.jboss.InvokerProxyBindingName;
import tubame.portability.model.generated.jboss.jboss.InvokerProxyBindings;
import tubame.portability.model.generated.jboss.jboss.Jboss;
import tubame.portability.model.generated.jboss.jboss.JndiName;
import tubame.portability.model.generated.jboss.jboss.MaximumSize;
import tubame.portability.model.generated.jboss.jboss.MessageDriven;
import tubame.portability.model.generated.jboss.jboss.MethodAttributes;
import tubame.portability.model.generated.jboss.jboss.MinimumSize;
import tubame.portability.model.generated.jboss.jboss.ObjectFactory;
import tubame.portability.model.generated.jboss.jboss.ResourceEnvRef;
import tubame.portability.model.generated.jboss.jboss.ResourceRef;
import tubame.portability.model.generated.jboss.jboss.Session;
import tubame.portability.model.generated.model.ConvertType;
import tubame.portability.model.generated.model.WebLogic;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.EjbReferenceDescription;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.EntityCache;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.EntityCacheRef;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.EntityClustering;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.EntityDescriptor;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.MessageDrivenDescriptor;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.Persistence;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.Pool;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.ReferenceDescriptor;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.ResourceDescription;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.ResourceEnvDescription;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.StatefulSessionCache;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.StatefulSessionClustering;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.StatefulSessionDescriptor;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.StatelessClustering;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.StatelessSessionDescriptor;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.TransactionDescriptor;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.WeblogicEjbJar;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv8.WeblogicEnterpriseBean;
import tubame.portability.util.StringUtil;

/**
 * Conversion to jboss.xml from weblogic-ejb-jar.xml for WebLogic Version 8.<br/>
 */
public class WL8EjbJarXmlConvertor extends AbstractXmlConvert {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(WL8EjbJarXmlConvertor.class);

    /**
     * Constructor.<br/>
     * Call the constructor of @ link AbstractXmlConvert} .<br/>
     * 
     * @throws JAXBException
     *             XML exception when
     * @throws SAXException
     *             Perth exceptional cases
     */
    public WL8EjbJarXmlConvertor() throws JAXBException, SAXException {
        super(AbstractXmlConvert.Type.WEBLOGIC8_EJB_JAR_XML_CONVERSE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object analysis(Object tagetObj, String pluginDir)
            throws JbmException {
        WeblogicEjbJar weblogicEjbJar = (WeblogicEjbJar) tagetObj;
        List<WeblogicEnterpriseBean> weblogicEnterpriseBeanList = weblogicEjbJar
                .getWeblogicEnterpriseBean();

        ObjectFactory jbossElmentFactory = new ObjectFactory();

        Jboss jboss = jbossElmentFactory.createJboss();

        EnterpriseBeans enterpriseBeans = jbossElmentFactory
                .createEnterpriseBeans();

        List<Object> sessionOrEntityOrMessageDrivenList = enterpriseBeans
                .getSessionOrEntityOrMessageDriven();

        // entity or session tag setting
        Entity entity = null;
        Session session = null;
        MessageDriven messageDriven = null;

        try {
            for (WeblogicEnterpriseBean weblogicEnterpriseBean : weblogicEnterpriseBeanList) {
                List<Object> obList = weblogicEnterpriseBean
                        .getEntityDescriptorOrStatelessSessionDescriptorOrStatefulSessionDescriptorOrMessageDrivenDescriptor();
                // Operations Add omitted system
                if (obList.size() == 0) {
                    String ejbName = weblogicEnterpriseBean.getEjbName();
                    if (super.isEntity(ejbName)) {
                        entity = createEntity(weblogicEnterpriseBean);
                        // -- Transaction-related tags --
                        MethodAttributes methodAttributes = addTransaction(
                                jbossElmentFactory, weblogicEnterpriseBean);
                        if (methodAttributes != null) {
                            entity.setMethodAttributes(methodAttributes);
                        }
                        sessionOrEntityOrMessageDrivenList.add(entity);
                    } else if (super.isSession(ejbName)) {
                        // session
                        session = createSession(weblogicEnterpriseBean);
                        // -- Transaction-related tags --
                        MethodAttributes methodAttributes = addTransaction(
                                jbossElmentFactory, weblogicEnterpriseBean);
                        if (methodAttributes != null) {
                            session.setMethodAttributes(methodAttributes);
                        }
                        sessionOrEntityOrMessageDrivenList.add(session);
                    } else if (super.isMdb(ejbName)) {
                        // MDB
                        messageDriven = createMessageDriven(
                                weblogicEnterpriseBean, jboss, null, pluginDir);
                        sessionOrEntityOrMessageDrivenList.add(messageDriven);
                    }

                } else {
                    // object instance check
                    for (Object object : obList) {
                        if (object instanceof EntityDescriptor) {
                            // -- Bean Entity --
                            entity = createEntity(weblogicEnterpriseBean);
                            // Add to v5.1 for
                            addSessionBeanEntity(jboss, entity, object);
                            // -- Transaction-related tags --
                            MethodAttributes methodAttributes = addTransaction(
                                    jbossElmentFactory, weblogicEnterpriseBean);
                            if (methodAttributes != null) {
                                entity.setMethodAttributes(methodAttributes);
                            }
                            sessionOrEntityOrMessageDrivenList.add(entity);
                        } else if (object instanceof StatefulSessionDescriptor) {
                            // -- Bean StatefulSession --
                            session = createSession(weblogicEnterpriseBean);
                            // Add to v5.1 for
                            addSessionBeanStatefulSession(jboss, session,
                                    object);
                            // -- Transaction-related tags --
                            MethodAttributes methodAttributes = addTransaction(
                                    jbossElmentFactory, weblogicEnterpriseBean);
                            if (methodAttributes != null) {
                                session.setMethodAttributes(methodAttributes);
                            }
                            sessionOrEntityOrMessageDrivenList.add(session);
                        } else if (object instanceof StatelessSessionDescriptor) {
                            // -- Bean StatelessSession --
                            session = createSession(weblogicEnterpriseBean);
                            // Add to v5.1 for
                            addSessionBeanStatelessSession(jboss, session,
                                    object);
                            // -- Transaction-related tags --
                            MethodAttributes methodAttributes = addTransaction(
                                    jbossElmentFactory, weblogicEnterpriseBean);
                            if (methodAttributes != null) {
                                session.setMethodAttributes(methodAttributes);
                            }
                            sessionOrEntityOrMessageDrivenList.add(session);
                        } else if (object instanceof MessageDrivenDescriptor) {
                            // -- Bean Mdb --
                            messageDriven = createMessageDriven(
                                    weblogicEnterpriseBean, jboss,
                                    (MessageDrivenDescriptor) object, pluginDir);
                            sessionOrEntityOrMessageDrivenList
                                    .add(messageDriven);
                        }
                    }
                }
            }
        } catch (JAXBException e) {
            throw new JbmException(e, LOGGER, ERROR_LEVEL.ERROR, e.getMessage());
        }
        jboss.setEnterpriseBeans(enterpriseBeans);

        return jboss;
    }

    /**
     * Add other transaction-related tags.<br/>
     * 
     * @param jbossElmentFactory
     *            ObjectFactory
     * @param weblogicEnterpriseBean
     *            WeblogicEnterpriseBean
     * @return MethodAttributes
     */
    private MethodAttributes addTransaction(ObjectFactory jbossElmentFactory,
            WeblogicEnterpriseBean weblogicEnterpriseBean) {
        TransactionDescriptor transactionDescriptor = weblogicEnterpriseBean
                .getTransactionDescriptor();
        if (transactionDescriptor != null) {
            String temp = transactionDescriptor.getTransTimeoutSeconds();
            if (!StringUtil.isEmpty(temp)) {
                MethodAttributes methodAttributes = jbossElmentFactory
                        .createMethodAttributes();
                tubame.portability.model.generated.jboss.jboss.Method method = new tubame.portability.model.generated.jboss.jboss.Method();
                method.setMethodName(TODOVALUE);
                method.setTransactionTimeout(TODOVALUE);
                methodAttributes.getMethod().add(method);

                // entity.setMethodAttributes(methodAttributes);
                return methodAttributes;
            }
        }
        return null;
    }

    /**
     * Get the algorithm class name.<br/>
     * 
     * @param value
     *            Load-algorithm value
     * @return clazz
     */
    private String getHomeLoadAlgorithm(String value) {
        String clazz = TODOVALUE;
        String target = value.toLowerCase();
        if ("roundrobin".equals(target)) {
            clazz = "org.jboss.ha.framework.interfaces.RoundRobin";
        } else if ("random".equals(target)) {
            clazz = "org.jboss.ha.framework.interfaces.RandomRobin";
        } else if ("randomaffinity".equals(target)) {
            clazz = "org.jboss.ha.framework.interfaces.FirstAvailable";
        } else if ("weightbased".equals(target)) {
            clazz = TODOVALUE;
        } else if ("roundrobinaffinity".equals(target)) {
            clazz = TODOVALUE;
        } else if ("weightbasedaffinity".equals(target)) {
            clazz = TODOVALUE;
        }
        return clazz;
    }

    /**
     * Add to tag the Session Bean Tag StatelessSession tag.<br/>
     * 
     * @param jboss
     *            Jboss
     * @param session
     *            Add a tag Session
     * @param object
     *            StatefulSessionDescriptor
     */
    private void addSessionBeanStatelessSession(Jboss jboss, Session session,
            Object object) {

        String extendsName = "Standard Stateless SessionBean";

        StatelessSessionDescriptor webLogicStatelessSessionDescriptor = (StatelessSessionDescriptor) object;
        StatelessClustering webLogicStatelessClustering = webLogicStatelessSessionDescriptor
                .getStatelessClustering();
        if (webLogicStatelessClustering != null) {
            extendsName = "Clustered Stateless SessionBean";
            session.setClustered("True");
            ClusterConfig clusterConfig = new ClusterConfig();
            String clazz = getHomeLoadAlgorithm(webLogicStatelessClustering
                    .getStatelessBeanLoadAlgorithm());
            clusterConfig.setHomeLoadBalancePolicy(clazz);
            clusterConfig.setBeanLoadBalancePolicy(clazz);
            session.setClusterConfig(clusterConfig);
        }

        Pool webLogicPool = webLogicStatelessSessionDescriptor.getPool();
        if (webLogicPool != null) {
            ContainerConfigurations containerConfigurations = getContainerConfigurations(jboss);
            ContainerPoolConf containerPoolConf = new ContainerPoolConf();
            List<Object> list = containerPoolConf
                    .getMinimumSizeOrMaximumSizeOrStrictMaximumSizeOrStrictTimeoutOrSynchronized();
            String temp = webLogicPool.getInitialBeansInFreePool();
            if (!super.isTagNull(temp)) {
                MinimumSize minimumSize = new MinimumSize();
                if (!super.isTagEmpty(temp)) {
                    minimumSize.setvalue(temp);
                } else {
                    minimumSize.setvalue(TODO);
                }
                list.add(minimumSize);
            }

            temp = webLogicPool.getMaxBeansInFreePool();
            if (!super.isTagNull(temp)) {
                MaximumSize maximumSize = new MaximumSize();
                if (!super.isTagEmpty(temp)) {
                    maximumSize.setvalue(temp);
                } else {
                    maximumSize.setvalue(TODO);
                }
                list.add(maximumSize);
            }
            ContainerConfiguration containerConfiguration = new ContainerConfiguration();
            containerConfiguration.setContainerPoolConf(containerPoolConf);
            containerConfiguration.setContainerName(session.getEjbName()
                    + StringUtil.BLANK + extendsName);

            containerConfiguration.setExtends(extendsName);
            containerConfigurations.getContainerConfiguration().add(
                    containerConfiguration);

            session.setConfigurationName(session.getEjbName()
                    + StringUtil.BLANK + extendsName);

        }
    }

    /**
     * Add to tag the Session Bean Entity tag tag.<br/>
     * 
     * @param jboss
     *            Jboss
     * @param entity
     *            Add a tag Session
     * @param object
     *            StatefulSessionDescriptor
     */
    private void addSessionBeanEntity(Jboss jboss, Entity entity, Object object) {

        String extendsName = "Standard CMP 2.x EntityBean";

        EntityDescriptor webLogicEntityDescriptor = (EntityDescriptor) object;
        EntityClustering webLogicEntityClustering = webLogicEntityDescriptor
                .getEntityClustering();
        // entity-clustering
        if (webLogicEntityClustering != null) {
            extendsName = "Clustered CMP 2.x EntityBean";
            entity.setClustered("True");
            ClusterConfig clusterConfig = new ClusterConfig();
            String clazz = getHomeLoadAlgorithm(webLogicEntityClustering
                    .getHomeLoadAlgorithm());
            clusterConfig.setHomeLoadBalancePolicy(clazz);
            clusterConfig.setBeanLoadBalancePolicy(clazz);
            entity.setClusterConfig(clusterConfig);
        }
        // Pool
        ContainerConfigurations containerConfigurations = getContainerConfigurations(jboss);
        boolean isAdd = false;

        Pool webLogicPool = webLogicEntityDescriptor.getPool();
        if (webLogicPool != null) {
            ContainerConfiguration containerConfiguration = getContainerConfiguration(
                    containerConfigurations, entity.getEjbName()
                            + StringUtil.BLANK + extendsName, extendsName);
            ContainerPoolConf containerPoolConf = new ContainerPoolConf();

            List<Object> list = containerPoolConf
                    .getMinimumSizeOrMaximumSizeOrStrictMaximumSizeOrStrictTimeoutOrSynchronized();

            String temp = webLogicPool.getInitialBeansInFreePool();
            if (!super.isTagNull(temp)) {
                MinimumSize minimumSize = new MinimumSize();
                if (!super.isTagEmpty(temp)) {
                    minimumSize.setvalue(temp);
                } else {
                    minimumSize.setvalue(TODO);
                }
                list.add(minimumSize);
            }
            temp = webLogicPool.getMaxBeansInFreePool();
            if (!super.isTagNull(temp)) {
                MaximumSize maximumSize = new MaximumSize();
                if (!super.isTagEmpty(temp)) {
                    maximumSize.setvalue(temp);
                } else {
                    maximumSize.setvalue(TODO);
                }
                list.add(maximumSize);
            }
            containerConfiguration.setContainerPoolConf(containerPoolConf);
            isAdd = true;

        }
        // Entity-cache
        for (Object entityCache : webLogicEntityDescriptor
                .getEntityCacheOrEntityCacheRef()) {
            if (entityCache instanceof EntityCache) {
                EntityCache webLogicEntityCache = (EntityCache) entityCache;
                ContainerConfiguration containerConfiguration = getContainerConfiguration(
                        containerConfigurations, entity.getEjbName()
                                + StringUtil.BLANK + extendsName, extendsName);
                ContainerCacheConf containerCacheConf = new ContainerCacheConf();
                CachePolicyConf cachePolicyConf = new CachePolicyConf();
                if (!super.isTagNull(webLogicEntityCache.getMaxBeansInCache())) {
                    cachePolicyConf.setMaxCapacity(TODOVALUE);
                }
                if (!super.isTagNull(webLogicEntityCache
                        .getIdleTimeoutSeconds())) {
                    cachePolicyConf.setOveragerPeriod(TODOVALUE);
                    cachePolicyConf.setMaxBeanAge(TODOVALUE);
                }
                containerCacheConf.setCachePolicyConf(cachePolicyConf);
                containerConfiguration
                        .setContainerCacheConf(containerCacheConf);

                String temp = webLogicEntityCache.getConcurrencyStrategy();
                if ("Exclusive".equals(temp)) {
                    containerConfiguration
                            .setLockingPolicy("org.jboss.ejb.plugins.lock.QueuedPessimisticEJBLock");
                } else if ("ReadOnly".equals(temp)) {
                    entity.setReadOnly("True");
                }
                isAdd = true;
            } else if (entityCache instanceof EntityCacheRef) {
                EntityCacheRef webLogicEntityCacheRef = (EntityCacheRef) entityCache;
                ContainerConfiguration containerConfiguration = getContainerConfiguration(
                        containerConfigurations, entity.getEjbName()
                                + StringUtil.BLANK + extendsName, extendsName);
                ContainerCacheConf containerCacheConf = new ContainerCacheConf();
                CachePolicyConf cachePolicyConf = new CachePolicyConf();
                if (!super.isTagNull(webLogicEntityCacheRef
                        .getIdleTimeoutSeconds())) {
                    cachePolicyConf.setOveragerPeriod(TODOVALUE);
                    cachePolicyConf.setMaxBeanAge(TODOVALUE);
                }
                containerCacheConf.setCachePolicyConf(cachePolicyConf);
                containerConfiguration
                        .setContainerCacheConf(containerCacheConf);

                String temp = webLogicEntityCacheRef.getConcurrencyStrategy();
                if ("Exclusive".equals(temp)) {
                    containerConfiguration
                            .setLockingPolicy("org.jboss.ejb.plugins.lock.QueuedPessimisticEJBLock");
                } else if ("ReadOnly".equals(temp)) {
                    entity.setReadOnly("True");
                }
                isAdd = true;
            }
        }
        // Sync-on-commit-only
        Persistence webLogicPersistence = webLogicEntityDescriptor
                .getPersistence();
        if (webLogicPersistence != null) {
            String temp = webLogicPersistence.getDelayUpdatesUntilEndOfTx();
            if (!StringUtil.isEmpty(temp)) {
                ContainerConfiguration containerConfiguration = getContainerConfiguration(
                        containerConfigurations, entity.getEjbName()
                                + StringUtil.BLANK + extendsName, extendsName);

                containerConfiguration.setSyncOnCommitOnly(temp);
                isAdd = true;
            }
        }
        if (!isAdd) {
            ContainerConfigurations addContainerConfigurations = jboss
                    .getContainerConfigurations();
            if (addContainerConfigurations.getContainerConfiguration().size() == 0) {
                jboss.setContainerConfigurations(null);
            }
        } else {
            // Set container if there is
            entity.setConfigurationName(entity.getEjbName() + StringUtil.BLANK
                    + extendsName);
        }

    }

    /**
     * Add to tag the Session Bean Tag StatefulSession tag.<br/>
     * 
     * @param jboss
     *            Jboss
     * @param session
     *            Add a tag Session
     * @param object
     *            StatefulSessionDescriptor
     */
    private void addSessionBeanStatefulSession(Jboss jboss, Session session,
            Object object) {

        String extendsName = "Standard Stateful SessionBean";

        StatefulSessionDescriptor webLogicStatefulSessionDescriptor = (StatefulSessionDescriptor) object;
        StatefulSessionClustering webLogicStatefulSessionClustering = webLogicStatefulSessionDescriptor
                .getStatefulSessionClustering();
        if (webLogicStatefulSessionClustering != null) {
            extendsName = "Clustered Stateful SessionBean";
            session.setClustered("True");
            ClusterConfig clusterConfig = new ClusterConfig();
            String clazz = getHomeLoadAlgorithm(webLogicStatefulSessionClustering
                    .getHomeLoadAlgorithm());
            clusterConfig.setHomeLoadBalancePolicy(clazz);
            clusterConfig.setBeanLoadBalancePolicy(clazz);
            String replicationType = webLogicStatefulSessionClustering
                    .getReplicationType();
            if ("InMemory".equals(replicationType)) {
                clusterConfig.setSessionStateManagerJndiName(TODOVALUE);
            }
            session.setClusterConfig(clusterConfig);
        }
        StatefulSessionCache webLogicStatefulSessionCache = webLogicStatefulSessionDescriptor
                .getStatefulSessionCache();
        if (webLogicStatefulSessionCache != null) {
            ContainerConfiguration containerConfiguration = new ContainerConfiguration();
            ContainerCacheConf containerCacheConf = new ContainerCacheConf();
            CachePolicyConf cachePolicyConf = new CachePolicyConf();
            if (!super.isTagNull(webLogicStatefulSessionCache
                    .getMaxBeansInCache())) {
                cachePolicyConf.setMaxCapacity(TODOVALUE);
            }
            if (!super.isTagNull(webLogicStatefulSessionCache
                    .getIdleTimeoutSeconds())) {
                cachePolicyConf.setOveragerPeriod(TODOVALUE);
                cachePolicyConf.setMaxBeanAge(TODOVALUE);
            }
            if (!super.isTagNull(webLogicStatefulSessionCache
                    .getSessionTimeoutSeconds())) {
                cachePolicyConf.setRemoverPeriod(TODOVALUE);
                cachePolicyConf.setMaxBeanLife(TODOVALUE);
            }
            containerCacheConf.setCachePolicyConf(cachePolicyConf);
            String webLogicCacheType = webLogicStatefulSessionCache
                    .getCacheType();
            if ("LRU".equals(webLogicCacheType)) {
                containerCacheConf
                        .setCachePolicy("org.jboss.ejb.plugins.LRUStatefulContextCachePolicy");
            }
            containerConfiguration.setContainerCacheConf(containerCacheConf);
            ContainerConfigurations containerConfigurations = getContainerConfigurations(jboss);

            containerConfiguration.setExtends(extendsName);
            containerConfiguration.setContainerName(session.getEjbName()
                    + StringUtil.BLANK + extendsName);
            containerConfigurations.getContainerConfiguration().add(
                    containerConfiguration);
            session.setConfigurationName(session.getEjbName()
                    + StringUtil.BLANK + extendsName);

        }
    }

    /**
     * Generate an Entity element.<br/>
     * 
     * @param weblogicEnterpriseBean
     *            XML element:WeblogicEnterpriseBean
     * @return XML element:Entity
     */
    private Entity createEntity(WeblogicEnterpriseBean weblogicEnterpriseBean) {
        // Acquisition of input information
        String ejbName = weblogicEnterpriseBean.getEjbName();

        String jndiname = weblogicEnterpriseBean.getJndiName();

        String localjndi = weblogicEnterpriseBean.getLocalJndiName();

        String webLogicEnableCallByReference = weblogicEnterpriseBean
                .getEnableCallByReference();

        // setEjbName
        Entity jbossEntity = new Entity();
        jbossEntity.setEjbName(ejbName);

        // setJndiName
        // It is optional to JndiName in weblogic-ejb-jar.xml,
        // but because the exception occurs (marshal) when you save the
        // jboss.xml file and
        // save it as a TODO value
        JndiName jndi = new JndiName();
        if (jndiname == null) {
            jndiname = "TODO:";
        }
        jndi.setvalue(jndiname);
        jbossEntity.setJndiName(jndi);

        // call-by-value
        if ("True".equals(webLogicEnableCallByReference)) {
            jbossEntity.setCallByValue("false");
        } else if ("False".equals(webLogicEnableCallByReference)) {
            jbossEntity.setCallByValue("true");
        }

        // ResourceRef<...>
        ResourceRef rrf = null;
        ReferenceDescriptor rd = weblogicEnterpriseBean
                .getReferenceDescriptor();
        if (rd != null) {
            List<ResourceDescription> rdList = rd.getResourceDescription();
            List<ResourceRef> rrfList = jbossEntity.getResourceRef();
            for (ResourceDescription resourcedescription : rdList) {
                rrf = createResRef(resourcedescription);
                rrfList.add(rrf);
            }

            List<ResourceEnvDescription> resourceEnvDescriptionList = rd
                    .getResourceEnvDescription();
            List<ResourceEnvRef> resourceEnvRefList = jbossEntity
                    .getResourceEnvRef();
            for (ResourceEnvDescription resourceEnvDescription : resourceEnvDescriptionList) {
                ResourceEnvRef ref = new ResourceEnvRef();
                ref.setResourceEnvRefName(resourceEnvDescription
                        .getResEnvRefName());

                JndiName jndiName = new JndiName();
                jndiName.setvalue(resourceEnvDescription.getJndiName());
                ref.setJndiName(jndiName);

                resourceEnvRefList.add(ref);
            }

            EjbRef erf = null;
            List<EjbReferenceDescription> erdList = rd
                    .getEjbReferenceDescription();
            List<EjbRef> erfList = jbossEntity.getEjbRef();
            for (EjbReferenceDescription description : erdList) {
                erf = createEjbRef(description);
                erfList.add(erf);
            }
        }

        jbossEntity.setLocalJndiName(localjndi);

        return jbossEntity;
    }

    /**
     * Generate Session element.<br/>
     * 
     * @param weblogicEnterpriseBean
     *            XML element:WeblogicEnterpriseBean
     * @return XML element:Session
     */
    private Session createSession(WeblogicEnterpriseBean weblogicEnterpriseBean) {
        // Getting Input Info
        String ejbName = weblogicEnterpriseBean.getEjbName();
        String jndiname = weblogicEnterpriseBean.getJndiName();
        String locJndi = weblogicEnterpriseBean.getLocalJndiName();
        String webLogicEnableCallByReference = weblogicEnterpriseBean
                .getEnableCallByReference();

        // Create Session Object
        Session jbossSession = new Session();

        // ejbName Setting
        jbossSession.setEjbName(ejbName);

        // JndiName Setting
        // setJndiName
        // It is optional to JndiName in weblogic-ejb-jar.xml,
        // but because the exception occurs (marshal) when you save the
        // jboss.xml file and
        // save it as a TODO value
        JndiName jndi = new JndiName();
        if (jndiname == null) {
            jndiname = "TODO:";
        }
        jndi.setvalue(jndiname);
        jbossSession.setJndiName(jndi);

        // LocalJndiName Setting
        jbossSession.setLocalJndiName(locJndi);

        // Call-by-value
        if ("True".equals(webLogicEnableCallByReference)) {
            jbossSession.setCallByValue("false");
        } else if ("False".equals(webLogicEnableCallByReference)) {
            jbossSession.setCallByValue("true");
        }

        // ResourceRef<...>
        ResourceRef rrf = null;
        ReferenceDescriptor rd = weblogicEnterpriseBean
                .getReferenceDescriptor();
        if (rd != null) {
            List<ResourceDescription> rdList = rd.getResourceDescription();
            List<ResourceRef> rrfList = jbossSession.getResourceRef();
            for (ResourceDescription resourcedescription : rdList) {
                rrf = createResRef(resourcedescription);
                rrfList.add(rrf);
            }
            List<ResourceEnvDescription> resourceEnvDescriptionList = rd
                    .getResourceEnvDescription();
            List<ResourceEnvRef> resourceEnvRefList = jbossSession
                    .getResourceEnvRef();
            for (ResourceEnvDescription resourceEnvDescription : resourceEnvDescriptionList) {
                ResourceEnvRef ref = new ResourceEnvRef();
                ref.setResourceEnvRefName(resourceEnvDescription
                        .getResEnvRefName());

                JndiName jndiName = new JndiName();
                jndiName.setvalue(resourceEnvDescription.getJndiName());
                ref.setJndiName(jndiName);

                resourceEnvRefList.add(ref);
            }

            // EjbReferenceDescription<...>
            EjbRef erf = null;
            List<EjbReferenceDescription> erdList = rd
                    .getEjbReferenceDescription();
            List<EjbRef> erfList = jbossSession.getEjbRef();
            for (EjbReferenceDescription description : erdList) {
                erf = createEjbRef(description);
                erfList.add(erf);
            }
        }

        return jbossSession;
    }

    /**
     * Pool and tags, <br/>
     * determine the Container created from the presence or absence of
     * DispatchPolicy tag.<br/>
     * <br/>
     * DispatchPolicy none Pool none => false<br/>
     * DispatchPolicy existence Pool none => true<br/>
     * DispatchPolicy none Pool existence => true<br/>
     * DispatchPolicy existence => true<br/>
     * Pool existence => true<br/>
     * 
     * @param pool
     *            Pool tags
     * @param dispath
     *            DispatchPolicy tag
     * @return true:The need of the Container creation false:No need to create
     *         Container
     */
    private boolean isDispathAndPoolStatus(Pool pool, String dispath) {
        // Dipath non , pool non => not create Container
        if (super.isTagNull(dispath) && pool == null) {
            return false;
        }
        // Dispath pool non => create Container
        if (!super.isTagEmpty(dispath) && pool == null) {
            return true;
        }
        // Pool non dispath => create Container
        if (super.isTagEmpty(dispath) && pool != null) {
            return true;
        }
        // Pool => create Container
        if (pool != null) {
            return true;
        }
        // Pispath => create Container
        if (!super.isTagEmpty(dispath)) {
            return true;
        }

        return false;
    }

    /**
     * Generate MessageDriven element.<br/>
     * 
     * @param weblogicEnterpriseBean
     *            XML element:WeblogicEnterpriseBean
     * @param jboss
     *            XML element:Jboss
     * @param weblogicMessageDrivenDescriptor
     *            XML element:MessageDrivenDescriptor
     * @param pluginDir
     *            Plug-in directory path
     * @return XML element:MessageDriven
     * @throws JAXBException
     *             XML operation failure
     * @throws JbmException
     *             File operation failure
     */
    private MessageDriven createMessageDriven(
            WeblogicEnterpriseBean weblogicEnterpriseBean, Jboss jboss,
            MessageDrivenDescriptor weblogicMessageDrivenDescriptor,
            String pluginDir) throws JAXBException, JbmException {

        // Generate the jboss object to read the standardjboss.xml
        Jboss standardJboss = createStandardJboss(pluginDir);

        // Getting Input Info
        String localjndi = weblogicEnterpriseBean.getLocalJndiName();

        String weblogicEjbName = weblogicEnterpriseBean.getEjbName();
        String weblogicConfigurationName = weblogicEjbName
                + " Standard Message Driven Bean";

        Pool pool = null;
        if (weblogicMessageDrivenDescriptor != null) {
            pool = weblogicMessageDrivenDescriptor.getPool();
        }

        // SetEjbName
        MessageDriven messageDriven = new MessageDriven();
        messageDriven.setEjbName(weblogicEjbName);
        if (weblogicMessageDrivenDescriptor != null) {
            messageDriven
                    .setDestinationJndiName(weblogicMessageDrivenDescriptor
                            .getDestinationJndiName());
        }

        // Modified to set in v5.1
        // * There need to be in the order min, and max
        String webLogicDispatchPolicy = weblogicEnterpriseBean
                .getDispatchPolicy();
        // If there is a dispatch-policy tag in weblogic-ejb-jar.xml,
        // and Zui string to ExecuteQueue of config.xml,
        // setting the thread pool using the run queue of WebLogic is performed.
        // * Do not read the config.xml for WebLogic

        // if (webLogicDispatchPolicy != null) {
        // <container-configurations>

        // dipath non , pool non => not create Container
        // pool => create Container
        // dispath => create Container
        // dispath pool non => create Container
        // pool non dispath => create Container
        if (isDispathAndPoolStatus(pool, webLogicDispatchPolicy)) {
            ContainerConfigurations containerConfigurations = jboss
                    .getContainerConfigurations();
            if (containerConfigurations == null) {
                containerConfigurations = new ContainerConfigurations();
                jboss.setContainerConfigurations(containerConfigurations);
            }

            List<ContainerConfiguration> listContainerConfiguration = containerConfigurations
                    .getContainerConfiguration();

            ContainerConfiguration containerConfiguration = new ContainerConfiguration();

            listContainerConfiguration.add(containerConfiguration);
            containerConfiguration.setContainerName(weblogicConfigurationName);
            containerConfiguration.setExtends("Standard Message Driven Bean");
            ContainerPoolConf containerPoolConf = new ContainerPoolConf();
            if (pool != null) {
                List<Object> listContainerPoolConf = containerPoolConf
                        .getMinimumSizeOrMaximumSizeOrStrictMaximumSizeOrStrictTimeoutOrSynchronized();

                if (!super.isTagNull(pool.getInitialBeansInFreePool())) {
                    MinimumSize minimumSizeContainer = new MinimumSize();
                    if (!super.isTagEmpty(pool.getInitialBeansInFreePool())) {
                        minimumSizeContainer.setvalue(pool
                                .getInitialBeansInFreePool());
                    } else {
                        // pool.getInitialBeansInFreePool() => empty
                        minimumSizeContainer.setvalue(TODOVALUE);
                    }
                    listContainerPoolConf.add(minimumSizeContainer);
                }

                if (!super.isTagNull(pool.getMaxBeansInFreePool())) {
                    MaximumSize maximumSizeContainer = new MaximumSize();
                    if (!super.isTagEmpty(pool.getMaxBeansInFreePool())) {
                        maximumSizeContainer.setvalue(pool
                                .getMaxBeansInFreePool());
                    } else {
                        // pool.getMaxBeansInFreePool() => empty
                        maximumSizeContainer.setvalue(TODOVALUE);
                    }
                    listContainerPoolConf.add(maximumSizeContainer);
                }
                containerConfiguration.setContainerPoolConf(containerPoolConf);
            }
            messageDriven.setConfigurationName(weblogicEjbName
                    + StringUtil.BLANK + "Standard Message Driven Bean");
            // }
            if (!super.isTagNull(webLogicDispatchPolicy)) {

                // <invoker-proxy-bindings>
                InvokerProxyBindings invokerProxyBindings = jboss
                        .getInvokerProxyBindings();
                if (invokerProxyBindings == null) {
                    invokerProxyBindings = new InvokerProxyBindings();
                    jboss.setInvokerProxyBindings(invokerProxyBindings);
                }

                // <invoker-proxy-binding>
                InvokerProxyBinding invokerProxyBinding = getStandardJbossInvokerProxyBinding(standardJboss);
                List<InvokerProxyBinding> listInvokerProxyBinding = invokerProxyBindings
                        .getInvokerProxyBinding();
                listInvokerProxyBinding.add(invokerProxyBinding);

                MaximumSize maximumSizeInvoker = new MaximumSize();
                MinimumSize minimumSizeInvoker = new MinimumSize();
                // Only be set if the setting value is present
                maximumSizeInvoker.setvalue(TODO);
                invokerProxyBinding.getProxyFactoryConfig().setMaximumSize(
                        maximumSizeInvoker);
                // Only be set if the setting value is present
                minimumSizeInvoker.setvalue(TODO);
                invokerProxyBinding.getProxyFactoryConfig().setMinimumSize(
                        minimumSizeInvoker);
                messageDriven.setConfigurationName(weblogicConfigurationName);

                invokerProxyBinding.setName(weblogicEjbName
                        + "-message-driven-bean");
                List<InvokerProxyBindingName> listInvokerProxyBindingName = containerConfiguration
                        .getInvokerProxyBindingName();
                InvokerProxyBindingName invokerProxyBindingName = new InvokerProxyBindingName();
                invokerProxyBindingName.setvalue(weblogicEjbName
                        + "-message-driven-bean");
                listInvokerProxyBindingName.add(invokerProxyBindingName);
            }
        }

        // ResourceRef<...>
        ResourceRef rrf = null;
        ReferenceDescriptor rd = weblogicEnterpriseBean
                .getReferenceDescriptor();
        if (rd != null) {
            List<ResourceDescription> rdList = rd.getResourceDescription();
            List<ResourceRef> rrfList = messageDriven.getResourceRef();
            for (ResourceDescription resourcedescription : rdList) {
                rrf = createResRef(resourcedescription);
                rrfList.add(rrf);
            }

            List<ResourceEnvDescription> resourceEnvDescriptionList = rd
                    .getResourceEnvDescription();
            List<ResourceEnvRef> resourceEnvRefList = messageDriven
                    .getResourceEnvRef();
            for (ResourceEnvDescription resourceEnvDescription : resourceEnvDescriptionList) {
                ResourceEnvRef ref = new ResourceEnvRef();
                ref.setResourceEnvRefName(resourceEnvDescription
                        .getResEnvRefName());

                JndiName jndiName = new JndiName();
                jndiName.setvalue(resourceEnvDescription.getJndiName());
                ref.setJndiName(jndiName);

                resourceEnvRefList.add(ref);
            }

            EjbRef erf = null;
            List<EjbReferenceDescription> erdList = rd
                    .getEjbReferenceDescription();
            List<EjbRef> erfList = messageDriven.getEjbRef();
            for (EjbReferenceDescription description : erdList) {
                erf = createEjbRef(description);
                erfList.add(erf);
            }
        }
        messageDriven.setLocalJndiName(localjndi);
        return messageDriven;
    }

    /**
     * InvokerProxyBinding Acquires InvokerProxyBinding element of the
     * message-driven-bean.<br/>
     * 
     * @param standardJboss
     *            XML element:Jboss
     * @return XML element:InvokerProxyBinding
     */
    private InvokerProxyBinding getStandardJbossInvokerProxyBinding(
            Jboss standardJboss) {
        List<InvokerProxyBinding> listStandardJbossInvokerProxyBinding = standardJboss
                .getInvokerProxyBindings().getInvokerProxyBinding();
        InvokerProxyBinding retInvokerProxyBinding = null;
        for (InvokerProxyBinding invokerProxyBinding : listStandardJbossInvokerProxyBinding) {
            String name = invokerProxyBinding.getName();
            if ("message-driven-bean".equals(name)) {
                retInvokerProxyBinding = invokerProxyBinding;
            }
        }
        return retInvokerProxyBinding;
    }

    /**
     * Generate ResourceRef element.<br/>
     * 
     * @param rd
     *            XML element:ResourceDescription
     * @return XML element:ResourceRef
     */
    private ResourceRef createResRef(ResourceDescription rd) {
        ResourceRef resourceRef = new ResourceRef();

        // ResourceRefName
        String resRefName = rd.getResRefName();
        resourceRef.setResRefName(resRefName);

        // JndiName
        String jndiName = rd.getJndiName();
        JndiName jndi = new JndiName();
        jndi.setvalue(jndiName);
        resourceRef.getResourceNameOrJndiNameOrResUrl().add(jndi);

        return resourceRef;
    }

    /**
     * Generate EjbRef element.<br/>
     * 
     * @param ejbReferenceDescription
     *            XML element:EjbReferenceDescription
     * @return XML element:EjbRef
     */
    private EjbRef createEjbRef(EjbReferenceDescription ejbReferenceDescription) {
        EjbRef ejbRef = new EjbRef();

        // EjbRefName
        String ejbRefName = ejbReferenceDescription.getEjbRefName();
        ejbRef.setEjbRefName(ejbRefName);

        // JndiName
        String jndiName = ejbReferenceDescription.getJndiName();
        JndiName jndi = new JndiName();
        jndi.setvalue(jndiName);
        ejbRef.setJndiName(jndi);

        return ejbRef;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public ConvertType getConverType() {
        return ConvertType.EJB_JAR_XML_CONVERT;
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
        return AbstractXmlConvertBase.JBOSS_XML_FILENAME;
    }

}
