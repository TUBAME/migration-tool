/*
 * AbstractXmlConvert.java
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

import java.io.File;
import java.io.IOException;

import javax.xml.bind.JAXBException;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import org.w3c.dom.Document;
import org.xml.sax.SAXException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.exception.JbmException;
import tubame.portability.exception.JbmException.ERROR_LEVEL;
import tubame.portability.model.generated.jboss.jboss.ContainerConfiguration;
import tubame.portability.model.generated.jboss.jboss.ContainerConfigurations;
import tubame.portability.model.generated.jboss.jboss.Jboss;
import tubame.portability.util.FileUtil;
import tubame.portability.util.resource.MessageUtil;

/**
 * Conversion process abstract class to JBoss from WebLogic.<br/>
 * Abstract class that XML conversion, write the result.<br/>
 */
public abstract class AbstractXmlConvert extends AbstractXmlConvertBase {
    /**
     * Logger
     */
    private static Logger LOGGER = LoggerFactory
            .getLogger(AbstractXmlConvert.class);

    /**
     * TODO value
     */
    public static final String TODO = "TODO:You must edit this value";
    /**
     * TODO value
     */
    protected static final String TODOVALUE = "TODO";
    /**
     * XPath object
     */
    private XPath xpath;
    /**
     * Document object
     */
    private Document doc;
    /**
     * Ejb-jar.xml file path
     */
    private String filePath;

    /**
     * Constructor.<br/>
     * Call the constructor of {@link AbstractXmlConvertBase}.<br/>
     * 
     * @param pType
     *            Version information be converted, (WebLogic) @see
     *            AbstractXmlConvertBase.Type
     * @throws JAXBException
     *             XML operation failure
     */
    public AbstractXmlConvert(AbstractXmlConvertBase.Type pType)
            throws JAXBException {
        super(pType);
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void doConvert(String srcFilePath, String destFilePath,
            String pluginDir) throws JAXBException, JbmException {
        Object srcRoot = getSrcRootObject(srcFilePath);
        filePath = srcFilePath.substring(0,
                srcFilePath.lastIndexOf(FileUtil.FILE_SEPARATOR))
                + FileUtil.FILE_SEPARATOR + "ejb-jar.xml";
        Object analysis = analysis(srcRoot, pluginDir);
        if (xpath != null) {
            xpath.reset();
        }
        writeXml(destFilePath, analysis);
    }

    /**
     * The decision whether or not there is a Session EJBNAME Meets from
     * ejb-jar.xml.<br/>
     * In Xpath format, and determine whether there is a Session that EJBNAME
     * Meets from ejb-jar.xml.<br/>
     * 
     * @param ejbName
     *            EJBName
     * @return true:Session exists
     * @throws JbmException
     *             XPath operation exception
     */
    protected boolean isSession(String ejbName) throws JbmException {
        String location = "/ejb-jar/enterprise-beans/session/ejb-name='"
                + ejbName + "'";
        return getText(location);
    }

    /**
     * The decision whether or not there is the Entity EJBNAME Meets from
     * ejb-jar.xml.<br/>
     * In Xpath format, and determine whether or not there is the Entity EJBNAME
     * Meets from ejb-jar.xml.<br/>
     * 
     * @param ejbName
     *            EJBName
     * @return true:Entity exists
     * @throws JbmException
     *             XPath operation exception
     */
    protected boolean isEntity(String ejbName) throws JbmException {
        String location = "/ejb-jar/enterprise-beans/entity/ejb-name='"
                + ejbName + "'";
        return getText(location);
    }

    /**
     * The decision whether or not there is an MDB EJBNAME Meets from
     * ejb-jar.xml.<br/>
     * In Xpath format, and determine whether there is an MDB EJBNAME Meets from
     * ejb-jar.xml.<br/>
     * 
     * @param ejbName
     *            EJBName
     * @return true:MDB exists
     * @throws JbmException
     *             XPath operation exception
     */
    protected boolean isMdb(String ejbName) throws JbmException {
        String location = "/ejb-jar/enterprise-beans/message-driven/ejb-name='"
                + ejbName + "'";
        return getText(location);
    }

    /**
     * If the document and xpath does not exist,<br/>
     * you have to instantiate and get the value of the location in the xpath.<br/>
     * 
     * @param location
     *            Location
     * @return Acquisition value at xpath
     * @throws JbmException
     *             Xpath acquisition failure
     */
    private boolean getText(String location) throws JbmException {
        try {
            setInitila(filePath);
            String result = xpath.evaluate(location, doc);
            if ("true".equals(result)) {
                return true;
            }
            return false;
        } catch (XPathExpressionException e) {
            throw new JbmException(
                    e,
                    LOGGER,
                    ERROR_LEVEL.ERROR,
                    new String[] { MessageUtil.CHECKLIST_INFORMATION_FILE_ERROR });
        } catch (SAXException e) {
            throw new JbmException(
                    e,
                    LOGGER,
                    ERROR_LEVEL.ERROR,
                    new String[] { MessageUtil.CHECKLIST_INFORMATION_FILE_ERROR });
        } catch (IOException e) {
            throw new JbmException(
                    e,
                    LOGGER,
                    ERROR_LEVEL.ERROR,
                    new String[] { MessageUtil.CHECKLIST_INFORMATION_FILE_ERROR });
        } catch (ParserConfigurationException e) {
            throw new JbmException(
                    e,
                    LOGGER,
                    ERROR_LEVEL.ERROR,
                    new String[] { MessageUtil.CHECKLIST_INFORMATION_FILE_ERROR });
        }
    }

    /**
     * If the document and XPATH does not exist, instantiation.<br/>
     * 
     * @param filePath
     *            File Path
     * @throws ParserConfigurationException
     *             DocumentBuilder instantiation failure
     * @throws IOException
     *             Self Plugin directory failure
     * @throws SAXException
     *             XML parsing error
     */
    protected void setInitila(String filePath)
            throws ParserConfigurationException, SAXException, IOException {
        if (xpath == null || doc == null) {
            DocumentBuilder builder = DocumentBuilderFactory.newInstance()
                    .newDocumentBuilder();
            doc = builder.parse(new File(filePath));
            XPathFactory factory = XPathFactory.newInstance();
            xpath = factory.newXPath();
        }
    }

    /**
     * Do (analysis) XML parsing.<br/>
     * Each converter performs the analysis process to override this method.<br/>
     * 
     * @param tagetObj
     *            Root information for each XML
     * @param pluginDir
     *            Directory where the plug-in is stored
     * @return XMLRoot information after analysis
     * @throws JbmException
     *             XML operation failure
     */
    public abstract Object analysis(Object tagetObj, String pluginDir)
            throws JbmException;

    /**
     * Get ContainerConfiguration specific.<br/>
     * If ContainerConfiguration has not yet exist, a new generation.<br/>
     * 
     * @param containerConfigurations
     *            ContainerConfigurations
     * @param containerConfigurationName
     *            ContainerConfiguration name
     * @param extendsName
     *            Extended string
     * @return ContainerConfiguration
     */
    protected ContainerConfiguration getContainerConfiguration(
            ContainerConfigurations containerConfigurations,
            String containerConfigurationName, String extendsName) {
        for (ContainerConfiguration containerConfiguration : containerConfigurations
                .getContainerConfiguration()) {
            if (containerConfigurationName.equals(containerConfiguration
                    .getContainerName())) {
                return containerConfiguration;
            }
        }
        ContainerConfiguration containerConfiguration = new ContainerConfiguration();
        containerConfiguration.setExtends(extendsName);
        containerConfiguration.setContainerName(containerConfigurationName);
        containerConfigurations.getContainerConfiguration().add(
                containerConfiguration);
        return containerConfiguration;
    }

    /**
     * Get ContainerConfigurations.<br/>
     * 
     * @param jboss
     *            Jboss
     * @return ContainerConfigurations
     */
    protected ContainerConfigurations getContainerConfigurations(Jboss jboss) {
        ContainerConfigurations containerConfigurations = jboss
                .getContainerConfigurations();
        if (containerConfigurations == null) {
            containerConfigurations = new ContainerConfigurations();
            jboss.setContainerConfigurations(containerConfigurations);
        }
        return containerConfigurations;
    }

    /**
     * The Returns true if the string is an empty string and null.<br/>
     * 
     * @param target
     *            Investigation target string
     * @return true:Null character false:Non-null
     */
    protected boolean isTagEmpty(String target) {
        if (!isTagNull(target) && target.length() == 0) {
            return true;
        }
        return false;
    }

    /**
     * The Returns true if the string is null.<br/>
     * 
     * @param target
     *            Investigation target string
     * @return true:null false:non-null
     */
    protected boolean isTagNull(String target) {
        if (target == null) {
            return true;
        }
        return false;
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public void preConvert() {
        xpath = null;
        doc = null;
    }

}
