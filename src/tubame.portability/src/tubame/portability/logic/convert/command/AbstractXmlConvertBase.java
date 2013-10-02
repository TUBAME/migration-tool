/*
 * AbstractXmlConvertBase.java
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
import java.io.InputStream;
import java.util.jar.JarFile;
import java.util.zip.ZipEntry;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.exception.JbmException;
import tubame.portability.exception.JbmException.ERROR_LEVEL;
import tubame.portability.model.generated.jboss.jboss.Jboss;
import tubame.portability.util.StringUtil;
import tubame.portability.util.resource.ApplicationPropertyUtil;
import tubame.portability.util.resource.MessageUtil;

/**
 * Abstract class that manages file name to be converted, a package of
 * information model.<br/>
 */
public abstract class AbstractXmlConvertBase implements Convertor {
    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(AbstractXmlConvertBase.class);

    /**
     * JBossXML file name
     */
    protected static final String JBOSS_XML_FILENAME = "jboss.xml";

    /**
     * JbossCmp-jdbc file name
     */
    protected static final String JBOSS_CMP_XML_FILENAME = "jbosscmp-jdbc.xml";

    /**
     * Generated-ejb-jar file name
     */
    protected static final String GENERATED_EJB_JAR_XML_FILENAME = "generated-ejb-jar.xml";

    /**
     * Standardjboss file name
     */
    protected static final String STANDARD_JBOSS_XML_FILENAME = "standardjboss.xml";

    /**
     * WebLogic model automatic generation package place
     */
    protected static final String WEBLOGIC_GENERATED_PACKAGE_NAME = "tubame.portability.model.generated.weblogic";
    /**
     * Jboss model automatic generation package place
     */
    protected static final String JBOSS_GENERATED_PACKAGE_NAME = "tubame.portability.model.generated.jboss";

    /**
     * Class to manage the package of JBoss / WebLogic.<br/>
     */
    public enum Type {

        /**
         * Type of weblogic_ejb_jar for
         */
        WEBLOGIC5_EJB_JAR_XML_CONVERSE(WEBLOGIC_GENERATED_PACKAGE_NAME
                + ".wlejbjar.wlv5", JBOSS_GENERATED_PACKAGE_NAME + ".jboss", 5),
        /**
         * WebLogicV6 for
         */
        WEBLOGIC6_EJB_JAR_XML_CONVERSE(WEBLOGIC_GENERATED_PACKAGE_NAME
                + ".wlejbjar.wlv6", JBOSS_GENERATED_PACKAGE_NAME + ".jboss", 6),
        /**
         * WebLogicV8 for
         */
        WEBLOGIC8_EJB_JAR_XML_CONVERSE(WEBLOGIC_GENERATED_PACKAGE_NAME
                + ".wlejbjar.wlv8", JBOSS_GENERATED_PACKAGE_NAME + ".jboss", 8),
        /**
         * WebLogicV8 for
         */
        EJB_JAR_XML_CONVERSE("org.jboss.jbmct.generated.weblogic.ejbjar.wlv8",
                "com.sun.java.xml.ns.j2ee", 8),

        /**
         * WebLogicV5 for
         */
        WEBLOGIC5_CMP_RDBMS_JAR_XML_CONVERSE(WEBLOGIC_GENERATED_PACKAGE_NAME
                + ".wlcmp.wlv5", JBOSS_GENERATED_PACKAGE_NAME + ".jbosscmp", 5),
        /**
         * WebLogicV6 for
         */
        WEBLOGIC6_CMP_RDBMS_JAR_XML_CONVERSE(WEBLOGIC_GENERATED_PACKAGE_NAME
                + ".wlcmp.wlv6", JBOSS_GENERATED_PACKAGE_NAME + ".jbosscmp", 6),
        /**
         * WebLogicV8 for
         */
        WEBLOGIC8_CMP_RDBMS_JAR_XML_CONVERSE(WEBLOGIC_GENERATED_PACKAGE_NAME
                + ".wlcmp.wlv8", JBOSS_GENERATED_PACKAGE_NAME + ".jbosscmp", 8);

        /**
         * unMarshall for
         */
        private String unMarshallPackeage;

        /**
         * marshall for
         */
        private String marshallPackeage;

        /**
         * WebLogic for
         */
        private int weblogicVersion;

        /**
         * Constructor.<br/>
         * To set the parameters to class member variables.<br/>
         * 
         * @param unMarshallPackeage
         *            Anne Marshall
         * @param marshallPackeage
         *            Marshall
         * @param weblogicVersion
         *            WebLogic Version
         */
        Type(String unMarshallPackeage, String marshallPackeage,
                int weblogicVersion) {
            this.unMarshallPackeage = unMarshallPackeage;
            this.marshallPackeage = marshallPackeage;
            this.weblogicVersion = weblogicVersion;
        }

        /**
         * Get WeblogicVerion.<br/>
         * 
         * @return WeblogicVerion
         */
        public int getWeblogicVerion() {
            return weblogicVersion;
        }
    }

    /**
     * Unmarshaller type
     */
    private final Type type;

    /**
     * Marshall
     */
    private final Marshaller marshaller;

    /**
     * Un marshall
     */
    private final Unmarshaller unmarshaller;

    /**
     * Object of the root element that un marshall on XML.
     */
    private Object srcRootObject;

    /**
     * Constructor.<br/>
     * Generate marshall or un marshall.<br/>
     * 
     * @param pType
     *            Type
     * @throws JAXBException
     *             XML exception when
     */
    public AbstractXmlConvertBase(Type pType) throws JAXBException {
        super();
        type = pType;
        JAXBContext marshallContext = JAXBContext
                .newInstance(pType.marshallPackeage);
        marshaller = marshallContext.createMarshaller();

        JAXBContext unMarshallcontext = JAXBContext
                .newInstance(pType.unMarshallPackeage);
        unmarshaller = unMarshallcontext.createUnmarshaller();
    }

    /**
     * Returns an Object of rootElement for XML.<br/>
     * 
     * @param pFileName
     *            File name
     * @return Object of rootElement for XML
     * @throws JAXBException
     *             XML exception when
     */
    public Object getSrcRootObject(String pFileName) throws JAXBException {
        if (srcRootObject == null) {
            return unmarshaller.unmarshal(new File(pFileName));
        }
        return srcRootObject;
    }

    /**
     * Write the XML file.<br/>
     * 
     * @param pFileName
     *            XML file path to be written
     * @param pJaxbElement
     *            XML object
     * @throws JAXBException
     *             XML operation failure
     */
    public void writeXml(String pFileName, Object pJaxbElement)
            throws JAXBException {

        // Setting the DOCTYPE
        String documentType = getXmlDocumentType(getWriteFileName());
        marshaller.setProperty("com.sun.xml.internal.bind.xmlHeaders",
                documentType);

        marshaller.setProperty(Marshaller.JAXB_FRAGMENT, Boolean.TRUE);

        File createFile = new File(pFileName);
        String parent = createFile.getParent();
        File dir = new File(parent);
        if (!dir.exists()) {
            dir.mkdirs();
        }
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
        marshaller.marshal(pJaxbElement, createFile);
    }

    /**
     * Get DOCUMENT TYPE string.<br/>
     * 
     * @param xmlFileName
     *            XML file name
     * @return DOCUMENT TYPE
     */
    public String getXmlDocumentType(String xmlFileName) {
        // Setting the DOCTYPE
        StringBuffer sb = new StringBuffer();
        if (xmlFileName.equals(JBOSS_XML_FILENAME)) {
            sb.append("" + "<?xml version=\"1.0\"?>");
            sb.append(StringUtil.LINE_SEPARATOR);
            sb.append("<!DOCTYPE jboss PUBLIC");
            sb.append(StringUtil.LINE_SEPARATOR);
            sb.append("      \"-//JBoss//DTD JBOSS 5.0//EN\"");
            sb.append(StringUtil.LINE_SEPARATOR);
            sb.append("      \"http://www.jboss.org/j2ee/dtd/jboss_5_0.dtd\">");
        } else if (xmlFileName.equals(JBOSS_CMP_XML_FILENAME)) {
            String encord = ApplicationPropertyUtil.CHARSET_XML;
            sb.append("" + "<?xml version=\"1.0\" encoding=\"");
            sb.append(encord);
            sb.append("\"?>");
            sb.append(StringUtil.LINE_SEPARATOR);
            sb.append("<!DOCTYPE jbosscmp-jdbc PUBLIC");
            sb.append(StringUtil.LINE_SEPARATOR);
            sb.append("   \"-//JBoss//DTD JBOSSCMP-JDBC 4.2//EN\"");
            sb.append(StringUtil.LINE_SEPARATOR);
            sb.append("   \"http://www.jboss.org/j2ee/dtd/jbosscmp-jdbc_4_2.dtd\">");
        }
        return sb.toString();
    }

    /**
     * {@inheritDoc}
     */
    @Override
    public String getWriteFileName() {
        if (type.equals(Type.WEBLOGIC5_EJB_JAR_XML_CONVERSE)
                || type.equals(Type.WEBLOGIC6_EJB_JAR_XML_CONVERSE)
                || type.equals(Type.WEBLOGIC8_EJB_JAR_XML_CONVERSE)) {
            return JBOSS_XML_FILENAME;
        }
        if (type.equals(Type.WEBLOGIC5_CMP_RDBMS_JAR_XML_CONVERSE)
                || type.equals(Type.WEBLOGIC6_CMP_RDBMS_JAR_XML_CONVERSE)
                || type.equals(Type.WEBLOGIC8_CMP_RDBMS_JAR_XML_CONVERSE)) {
            return JBOSS_CMP_XML_FILENAME;
        }
        return null;
    }

    /**
     * Generates object jboss read the standardjboss.xml.<br/>
     * 
     * @param pluginDir
     *            Plug-in directory
     * @return Jobss object of XML
     * @throws JAXBException
     *             XML operation failure
     * @throws JbmException
     *             File operation failure
     */
    protected Jboss createStandardJboss(String pluginDir) throws JAXBException,
            JbmException {
        Jboss standardJboss = null;
        JAXBContext unMarshallcontext = JAXBContext
                .newInstance("tubame.portability.model.generated.jboss.jboss");
        Unmarshaller unMarshaller = unMarshallcontext.createUnmarshaller();
        InputStream inputStream = null;
        JarFile jar = null;
        try {
            String jbmctJarFileName = pluginDir + "lib/jbmct.jar";
            File jbmctJarFile = new File(jbmctJarFileName);

            // Determined to run from the plug-in project if the jar file is
            // present
            if (jbmctJarFile.exists()) {
                jar = new JarFile(jbmctJarFileName);
                ZipEntry zipEntry = jar.getEntry("META-INF/"
                        + STANDARD_JBOSS_XML_FILENAME);
                inputStream = jar.getInputStream(zipEntry);
                standardJboss = (Jboss) unMarshaller.unmarshal(inputStream);
            } else {
                // Jar file does not exist
                String standardJbossFileName = pluginDir + "resources/xml/"
                        + STANDARD_JBOSS_XML_FILENAME;
                standardJboss = (Jboss) unMarshaller.unmarshal(new File(
                        standardJbossFileName));
            }

        } catch (IOException e) {
            throw new JbmException(e, LOGGER, ERROR_LEVEL.ERROR,
                    new String[] { STANDARD_JBOSS_XML_FILENAME
                            + MessageUtil.ERR_CONVERT_FILE_READ });
        } finally {
            if (inputStream != null) {
                try {
                    inputStream.close();
                } catch (IOException e) {
                    // no operation
                }
            }
            if (jar != null) {
                try {
                    jar.close();
                } catch (IOException e) {
                    // no operation
                }
            }
        }

        return standardJboss;
    }
}
