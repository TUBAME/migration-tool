/*
 * WL5EjbJarXmlConvertor.java
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
import tubame.portability.model.generated.jboss.jboss.CachePolicyConf;
import tubame.portability.model.generated.jboss.jboss.ClusterConfig;
import tubame.portability.model.generated.jboss.jboss.ContainerCacheConf;
import tubame.portability.model.generated.jboss.jboss.ContainerConfiguration;
import tubame.portability.model.generated.jboss.jboss.ContainerConfigurations;
import tubame.portability.model.generated.jboss.jboss.ContainerPoolConf;
import tubame.portability.model.generated.jboss.jboss.EjbRef;
import tubame.portability.model.generated.jboss.jboss.EnterpriseBeans;
import tubame.portability.model.generated.jboss.jboss.Entity;
import tubame.portability.model.generated.jboss.jboss.Jboss;
import tubame.portability.model.generated.jboss.jboss.JndiName;
import tubame.portability.model.generated.jboss.jboss.MaximumSize;
import tubame.portability.model.generated.jboss.jboss.MethodAttributes;
import tubame.portability.model.generated.jboss.jboss.MinimumSize;
import tubame.portability.model.generated.jboss.jboss.ObjectFactory;
import tubame.portability.model.generated.jboss.jboss.ResourceRef;
import tubame.portability.model.generated.jboss.jboss.Session;
import tubame.portability.model.generated.model.ConvertType;
import tubame.portability.model.generated.model.WebLogic;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv5.CachingDescriptor;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv5.ClusteringDescriptor;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv5.EjbReferenceDescription;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv5.PersistenceDescriptor;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv5.ReferenceDescriptor;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv5.ResourceDescription;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv5.TransactionDescriptor;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv5.WeblogicEjbJar;
import tubame.portability.model.generated.weblogic.wlejbjar.wlv5.WeblogicEnterpriseBean;
import tubame.portability.util.StringUtil;

/**
 * Conversion to jboss.xml from weblogic-ejb-jar.xml of WebLogic Version 5.<br/>
 */
public class WL5EjbJarXmlConvertor extends AbstractXmlConvert {

    /**
     * Constructor.<br/>
     * Call the constructor of {@link AbstractXmlConvert}.<br/>
     * 
     * @throws JAXBException
     *             XML exception when
     * @throws SAXException
     *             Perth exceptional cases
     */
    public WL5EjbJarXmlConvertor() throws JAXBException, SAXException {
        super(AbstractXmlConvertBase.Type.WEBLOGIC5_EJB_JAR_XML_CONVERSE);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public Object analysis(Object tagetObj, String pluginDir)
            throws JbmException {
        // Targetobj the xml of wl
        WeblogicEjbJar weblogicEjbJar = (WeblogicEjbJar) tagetObj;

        List<WeblogicEnterpriseBean> weblogicEnterpriseBeanList = weblogicEjbJar
                .getWeblogicEnterpriseBean();

        ObjectFactory jbossElmentFactory = new ObjectFactory();

        Jboss jboss = jbossElmentFactory.createJboss();

        EnterpriseBeans enterpriseBeans = jbossElmentFactory
                .createEnterpriseBeans();

        List<Object> sessionOrEntityOrMessageDrivenList = enterpriseBeans
                .getSessionOrEntityOrMessageDriven();

        Entity entity = null;
        Session session = null;
        for (WeblogicEnterpriseBean weblogicEnterpriseBean : weblogicEnterpriseBeanList) {
            if (weblogicEnterpriseBean.getPersistenceDescriptor() != null) {
                entity = createEntity(weblogicEnterpriseBean);

                ClusteringDescriptor clusteringDescriptor = weblogicEnterpriseBean
                        .getClusteringDescriptor();
                if (clusteringDescriptor != null) {
                    entity.setClustered("true");
                    ClusterConfig clusterConfig = new ClusterConfig();
                    String clazz = getHomeLoadAlgorithm(clusteringDescriptor
                            .getHomeLoadAlgorithm());
                    if (!StringUtil.isEmpty(clazz)) {
                        clusterConfig.setHomeLoadBalancePolicy(clazz);
                        clusterConfig.setBeanLoadBalancePolicy(clazz);
                    }
                    clazz = getHomeLoadAlgorithm(clusteringDescriptor
                            .getStatelessBeanLoadAlgorithm());
                    if (!StringUtil.isEmpty(clazz)) {
                        clusterConfig.setHomeLoadBalancePolicy(clazz);
                        clusterConfig.setBeanLoadBalancePolicy(clazz);
                    }
                    entity.setClusterConfig(clusterConfig);
                }
                setBean(jboss, weblogicEnterpriseBean);
                MethodAttributes methodAttributes = addTransaction(
                        jbossElmentFactory, weblogicEnterpriseBean);
                if (methodAttributes != null) {
                    entity.setMethodAttributes(methodAttributes);
                }

                sessionOrEntityOrMessageDrivenList.add(entity);
            } else {
                // Session
                session = createSession(weblogicEnterpriseBean);

                ClusteringDescriptor clusteringDescriptor = weblogicEnterpriseBean
                        .getClusteringDescriptor();
                if (clusteringDescriptor != null) {
                    session.setClustered("true");
                    ClusterConfig clusterConfig = new ClusterConfig();
                    String clazz = getHomeLoadAlgorithm(clusteringDescriptor
                            .getHomeLoadAlgorithm());
                    if (!StringUtil.isEmpty(clazz)) {
                        clusterConfig.setHomeLoadBalancePolicy(clazz);
                        clusterConfig.setBeanLoadBalancePolicy(clazz);
                    }
                    clazz = getHomeLoadAlgorithm(clusteringDescriptor
                            .getStatelessBeanLoadAlgorithm());
                    if (!StringUtil.isEmpty(clazz)) {
                        clusterConfig.setHomeLoadBalancePolicy(clazz);
                        clusterConfig.setBeanLoadBalancePolicy(clazz);
                    }
                    session.setClusterConfig(clusterConfig);
                }
                setBean(jboss, weblogicEnterpriseBean);

                MethodAttributes methodAttributes = addTransaction(
                        jbossElmentFactory, weblogicEnterpriseBean);
                if (methodAttributes != null) {
                    session.setMethodAttributes(methodAttributes);
                }

                sessionOrEntityOrMessageDrivenList.add(session);
            }
        }

        jboss.setEnterpriseBeans(enterpriseBeans);

        return jboss;
    }

    /**
     * Set Pool / cache-container-conf / cache-policy.<br/>
     * 
     * @param jboss
     *            Jboss
     * @param weblogicEnterpriseBean
     *            WeblogicEnterpriseBean
     */
    private void setBean(Jboss jboss,
            WeblogicEnterpriseBean weblogicEnterpriseBean) {
        CachingDescriptor webLogicCachingDescriptor = weblogicEnterpriseBean
                .getCachingDescriptor();
        if (webLogicCachingDescriptor != null) {
            ContainerConfigurations containerConfigurations = getContainerConfigurations(jboss);
            ContainerConfiguration containerConfiguration = new ContainerConfiguration();
            // Pool
            ContainerPoolConf containerPoolConf = new ContainerPoolConf();
            String temp = webLogicCachingDescriptor.getInitialBeansInFreePool();
            if (!StringUtil.isEmpty(temp)) {
                MinimumSize minimumSize = new MinimumSize();
                minimumSize.setvalue(temp);
                containerPoolConf
                        .getMinimumSizeOrMaximumSizeOrStrictMaximumSizeOrStrictTimeoutOrSynchronized()
                        .add(minimumSize);
            }
            temp = webLogicCachingDescriptor.getMaxBeansInFreePool();
            if (!StringUtil.isEmpty(temp)) {
                MaximumSize maximumSize = new MaximumSize();
                maximumSize.setvalue(temp);
                containerPoolConf
                        .getMinimumSizeOrMaximumSizeOrStrictMaximumSizeOrStrictTimeoutOrSynchronized()
                        .add(maximumSize);
            }
            containerConfiguration.setContainerPoolConf(containerPoolConf);
            // cache-container-conf
            ContainerCacheConf containerCacheConf = new ContainerCacheConf();
            CachePolicyConf cachePolicyConf = new CachePolicyConf();
            cachePolicyConf.setMaxCapacity(webLogicCachingDescriptor
                    .getMaxBeansInCache());
            cachePolicyConf.setOveragerPeriod(webLogicCachingDescriptor
                    .getIdleTimeoutSeconds());
            cachePolicyConf.setMaxBeanAge(webLogicCachingDescriptor
                    .getIdleTimeoutSeconds());
            containerCacheConf.setCachePolicyConf(cachePolicyConf);
            containerConfiguration.setContainerCacheConf(containerCacheConf);
            // cache-policy
            containerCacheConf.setCachePolicy(webLogicCachingDescriptor
                    .getCacheType());

            containerConfigurations.getContainerConfiguration().add(
                    containerConfiguration);
        }
        PersistenceDescriptor webLogicPersistenceDescriptor = weblogicEnterpriseBean
                .getPersistenceDescriptor();
        if (webLogicPersistenceDescriptor != null) {
            String temp = webLogicPersistenceDescriptor
                    .getDelayUpdatesUntilEndOfTx();
            if (!StringUtil.isEmpty(temp)) {
                ContainerConfigurations containerConfigurations = getContainerConfigurations(jboss);
                ContainerConfiguration containerConfiguration = new ContainerConfiguration();
                containerConfiguration.setSyncOnCommitOnly(temp);

                containerConfigurations.getContainerConfiguration().add(
                        containerConfiguration);
            }
        }
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
                method.setTransactionTimeout(temp);
                methodAttributes.getMethod().add(method);

                return methodAttributes;
            }
        }
        return null;
    }

    /**
     * Get the algorithm class name.<br/>
     * 
     * @param value
     *            load-algorithm value
     * @return clazz
     */
    private String getHomeLoadAlgorithm(String value) {
        String clazz = StringUtil.BLANK;
        if ("round-robin".equals(value)) {
            clazz = "org.jboss.ha.framework.interfaces.RoundRobin";
        } else if ("random".equals(value)) {
            clazz = "org.jboss.ha.framework.interfaces.RandomRobin";
        } else if ("weight-based".equals(value)) {
            clazz = "org.jboss.ha.framework.interfaces.FirstAvailable";
        }
        return clazz;
    }

    /**
     * Generate Entity.<br/>
     * 
     * @param weblogicEnterpriseBean
     *            XML element:WeblogicEnterpriseBean
     * @return XML element:Entity
     */
    protected Entity createEntity(WeblogicEnterpriseBean weblogicEnterpriseBean) {
        // Acquisition of input information
        String ejbName = weblogicEnterpriseBean.getEjbName();

        String jndiname = weblogicEnterpriseBean.getJndiName();

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

            // ejb-reference-description<...>
            EjbRef erf = null;
            List<EjbReferenceDescription> erdList = rd
                    .getEjbReferenceDescription();
            List<EjbRef> erfList = jbossEntity.getEjbRef();
            for (EjbReferenceDescription description : erdList) {
                erf = createEjbRef(description);
                erfList.add(erf);
            }
        }

        return jbossEntity;
    }

    /**
     * Generate Session element.<br/>
     * 
     * @param weblogicEnterpriseBean
     *            XML element:WeblogicEnterpriseBean
     * @return XML element:Session
     */
    protected Session createSession(
            WeblogicEnterpriseBean weblogicEnterpriseBean) {
        // Acquisition of input information
        String ejbName = weblogicEnterpriseBean.getEjbName();

        String jndiname = weblogicEnterpriseBean.getJndiName();

        String webLogicEnableCallByReference = weblogicEnterpriseBean
                .getEnableCallByReference();

        // setEjbName
        Session jbossSession = new Session();
        jbossSession.setEjbName(ejbName);

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

        // call-by-value
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

            // ejb-reference-description<...>
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
     * Generate ResRef element.<br/>
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
        return WebLogic.V_5;
    }
}
