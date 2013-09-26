/*
 * JaxbUtil.java
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
package org.tubame.knowhow.biz.util;

import java.net.MalformedURLException;
import java.util.HashMap;
import java.util.Map;

import javax.xml.bind.JAXBContext;
import javax.xml.bind.JAXBException;
import javax.xml.bind.Marshaller;
import javax.xml.bind.Unmarshaller;
import javax.xml.validation.Schema;
import javax.xml.validation.SchemaFactory;

import org.docbook.ns.docbook.Article;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.logic.JaxbValidationEventHandler;
import org.tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow;
import org.tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.xml.sax.SAXException;

/**
 * Defines a common processing of Ann Marshall.<br/>
 */
public final class JaxbUtil {
    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(JaxbUtil.class);
    /** Know-how marshaler */
    private static final Marshaller KNOWHOW_MARSHALLER = getKnowhowMarshaller();
    /** Know-how unmarshaller */
    private static final Unmarshaller KNOWHOW_UNMARSHALLER = getKnowhowUnmarshaller();
    /** DocBook marshaler */
    private static final Marshaller DOCBOOK_MARSHALLER = getDocbookMarshaller();
    /** DocBook unmarshaller */
    private static final Unmarshaller DOCBOOK_UNMARSHALLER = getDocbookUnmarshaller();

    /**
     * Constructor.<br/>
     * 
     */
    private JaxbUtil() {
        // no operation
    }

    /**
     * This is the initialization process of the class. <br/>
     * Called only once at the beginning.<br/>
     * 
     */
    public static void init() {
        // no operation
    }

    /**
     * Get unmarshaller of DocBook.<br/>
     * 
     * @return DocBookunmarshaller
     */
    private static final Unmarshaller getDocbookUnmarshaller() {
        try {
            LOGGER.trace(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_START_CREATE_DOCBOOK_UNMARSHALLER));
            JAXBContext jaxbContext = JAXBContext.newInstance(Article.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(JaxbUtil.getSchema(ApplicationPropertiesUtil
                    .getProperty(ApplicationPropertiesUtil.DOCBOOKSCHEMA_PATH),
                    Article.class));
            LOGGER.trace(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_STOP_CREATE_DOCBOOK_UNMARSHALLER));
            return unmarshaller;
        } catch (JAXBException e) {
            return null;
        } catch (SAXException e) {
            return null;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Get marshalers DocBook.<br/>
     * 
     * @return DocBook marshaler
     */
    private static final Marshaller getDocbookMarshaller() {
        try {
            LOGGER.trace(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_START_CREATE_DOCBOOK_MARSHALLER));
            JAXBContext jaxbContext = JAXBContext.newInstance(Article.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setSchema(JaxbUtil.getSchema(ApplicationPropertiesUtil
                    .getProperty(ApplicationPropertiesUtil.DOCBOOKSCHEMA_PATH),
                    Article.class));
            LOGGER.trace(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_STOP_CREATE_DOCBOOK_MARSHALLER));
            return marshaller;
        } catch (JAXBException e) {
            return null;
        } catch (SAXException e) {
            return null;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Get unmarshaller of know-how.<br/>
     * 
     * @return Know-how unmarshaller
     */
    private static final Unmarshaller getKnowhowUnmarshaller() {
        try {
            LOGGER.trace(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_START_CREATE_KNOWHOW_UNMARSHALLER));
            JAXBContext jaxbContext = JAXBContext
                    .newInstance(PortabilityKnowhow.class);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller
                    .setSchema(JaxbUtil.getSchema(
                            ApplicationPropertiesUtil
                                    .getProperty(ApplicationPropertiesUtil.PORTABILITYKNOWHOWSCHEMA_PATH),
                            PortabilityKnowhow.class));
            LOGGER.trace(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_STOP_CREATE_KNOWHOW_UNMARSHALLER));
            return unmarshaller;
        } catch (JAXBException e) {
            return null;
        } catch (SAXException e) {
            return null;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Get marshaler of know-how.<br/>
     * 
     * @return Know-how marshaler
     */
    private static final Marshaller getKnowhowMarshaller() {
        try {
            LOGGER.trace(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_START_CREATE_KNOWHOW_MARSHALLER));
            JAXBContext jaxbContext = JAXBContext
                    .newInstance(PortabilityKnowhow.class);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller
                    .setSchema(JaxbUtil.getSchema(
                            ApplicationPropertiesUtil
                                    .getProperty(ApplicationPropertiesUtil.PORTABILITYKNOWHOWSCHEMA_PATH),
                            PortabilityKnowhow.class));
            LOGGER.trace(MessagePropertiesUtil
                    .getMessage(MessagePropertiesUtil.LOG_STOP_CREATE_KNOWHOW_MARSHALLER));
            return marshaller;
        } catch (JAXBException e) {
            return null;
        } catch (SAXException e) {
            return null;
        } catch (MalformedURLException e) {
            return null;
        }
    }

    /**
     * Get unmarshaller.<br/>
     * 
     * @param targetClass
     *            Class instantiation time
     * @param resourcePath
     *            The path of the schema file
     * @param errMap
     *            Error information storage map
     * @return Unmarshaller
     * @throws JAXBException
     *             Unmarshaller generation error
     * @throws SAXException
     *             Schema acquisition error
     * @throws MalformedURLException
     *             MalformedURL error
     */
    public static Unmarshaller getUnmarshaller(Class<?> targetClass,
            String resourcePath, Map<String, Integer> errMap)
            throws JAXBException, SAXException, MalformedURLException {
        Unmarshaller unmarshaller = null;
        if (targetClass.equals(Article.class)) {
            unmarshaller = JaxbUtil.getDocBookUnmarshallerPlain(targetClass,
                    resourcePath);
        } else if (targetClass.equals(PortabilityKnowhow.class)) {
            unmarshaller = JaxbUtil.getKnowhowUnmarshallerPlain(targetClass,
                    resourcePath);
        } else {
            unmarshaller = JaxbUtil.getUnmarshallerPlain(targetClass,
                    resourcePath);
        }
        unmarshaller.setEventHandler(new JaxbValidationEventHandler(errMap));
        return unmarshaller;
    }

    /**
     * Get unmarshaller plain without EventHandler.<br/>
     * 
     * @param targetClass
     *            Class instantiation time
     * @param resourcePath
     *            The path of the schema file
     * @return Unmarshaller
     * @throws JAXBException
     *             Unmarshaller generation error
     * @throws SAXException
     *             Schema acquisition error
     * @throws MalformedURLException
     *             MalformedURL error
     */
    private static Unmarshaller getUnmarshallerPlain(Class<?> targetClass,
            String resourcePath) throws JAXBException, SAXException,
            MalformedURLException {

        JAXBContext jaxbContext = JAXBContext.newInstance(targetClass);
        Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
        unmarshaller.setSchema(JaxbUtil.getSchema(resourcePath, targetClass));
        return unmarshaller;
    }

    /**
     * Get unmarshaller plain without EventHandler.<br/>
     * 
     * @param targetClass
     *            Class instantiation time
     * @param resourcePath
     *            The path of the schema file
     * @return Unmarshaller
     * @throws JAXBException
     *             Unmarshaller generation error
     * @throws SAXException
     *             Schema acquisition error
     * @throws MalformedURLException
     *             MalformedURL error
     */
    private static Unmarshaller getDocBookUnmarshallerPlain(
            Class<?> targetClass, String resourcePath) throws JAXBException,
            SAXException, MalformedURLException {
        if (DOCBOOK_UNMARSHALLER == null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(targetClass);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(JaxbUtil
                    .getSchema(resourcePath, targetClass));
            return unmarshaller;
        } else {
            return DOCBOOK_UNMARSHALLER;
        }
    }

    /**
     * Get unmarshaller plain without EventHandler.<br/>
     * 
     * @param targetClass
     *            Class instantiation time
     * @param resourcePath
     *            The path of the schema file
     * @return Unmarshaller
     * @throws JAXBException
     *             Unmarshaller generation error
     * @throws SAXException
     *             Schema acquisition error
     * @throws MalformedURLException
     *             MalformedURL error
     */
    private static Unmarshaller getKnowhowUnmarshallerPlain(
            Class<?> targetClass, String resourcePath) throws JAXBException,
            SAXException, MalformedURLException {
        if (KNOWHOW_UNMARSHALLER == null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(targetClass);
            Unmarshaller unmarshaller = jaxbContext.createUnmarshaller();
            unmarshaller.setSchema(JaxbUtil
                    .getSchema(resourcePath, targetClass));
            return unmarshaller;
        } else {
            return KNOWHOW_UNMARSHALLER;
        }
    }

    /**
     * Get marshaler.<br/>
     * 
     * @param targetClass
     *            Class instantiation time
     * @param resourcePath
     *            The path of the schema file
     * @return Unmarshaller
     * @throws JAXBException
     *             Unmarshaller generation error
     * @throws SAXException
     *             Schema acquisition error
     * @throws MalformedURLException
     *             MalformedURL error
     */
    public static Marshaller getMarshaller(Class<?> targetClass,
            String resourcePath) throws JAXBException, SAXException,
            MalformedURLException {
        Marshaller marshaller = null;
        if (targetClass.equals(Article.class)) {
            marshaller = JaxbUtil.getDocBookMarshallerPlain(targetClass,
                    resourcePath);
        } else if (targetClass.equals(PortabilityKnowhow.class)) {
            marshaller = JaxbUtil.getKnowhowMarshallerPlain(targetClass,
                    resourcePath);
        } else {
            marshaller = JaxbUtil.getMarshallerPlain(targetClass, resourcePath);
        }
        marshaller.setEventHandler(new JaxbValidationEventHandler(
                new HashMap<String, Integer>()));
        return marshaller;
    }

    /**
     * Get marshallers plain without EventHandler.<br/>
     * 
     * @param targetClass
     *            Class instantiation time
     * @param resourcePath
     *            The path of the schema file
     * @return unmarshaller
     * @throws JAXBException
     *             Unmarshaller generation error
     * @throws SAXException
     *             Schema acquisition error
     * @throws MalformedURLException
     *             MalformedURL error
     */
    private static Marshaller getMarshallerPlain(Class<?> targetClass,
            String resourcePath) throws JAXBException, SAXException,
            MalformedURLException {

        JAXBContext jaxbContext = JAXBContext.newInstance(targetClass);
        Marshaller marshaller = jaxbContext.createMarshaller();
        marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);

        marshaller.setSchema(JaxbUtil.getSchema(resourcePath, targetClass));
        return marshaller;
    }

    /**
     * Get marshallers plain without EventHandler.<br/>
     * 
     * @param targetClass
     *            Class instantiation time
     * @param resourcePath
     *            The path of the schema file
     * @return Unmarshaller
     * @throws JAXBException
     *             Unmarshaller generation error
     * @throws SAXException
     *             Schema acquisition error
     * @throws MalformedURLException
     *             MalformedURL error
     */
    private static Marshaller getDocBookMarshallerPlain(Class<?> targetClass,
            String resourcePath) throws JAXBException, SAXException,
            MalformedURLException {
        if (DOCBOOK_MARSHALLER == null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(targetClass);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setSchema(JaxbUtil.getSchema(resourcePath, targetClass));
            return marshaller;
        } else {
            return DOCBOOK_MARSHALLER;
        }
    }

    /**
     * Get marshallers plain without EventHandler.<br/>
     * 
     * @param targetClass
     *            Class instantiation time
     * @param resourcePath
     *            The path of the schema file
     * @return Unmarshaller
     * @throws JAXBException
     *             Unmarshaller generation error
     * @throws SAXException
     *             Schema acquisition error
     * @throws MalformedURLException
     *             MalformedURL error
     */
    private static Marshaller getKnowhowMarshallerPlain(Class<?> targetClass,
            String resourcePath) throws JAXBException, SAXException,
            MalformedURLException {
        if (KNOWHOW_MARSHALLER == null) {
            JAXBContext jaxbContext = JAXBContext.newInstance(targetClass);
            Marshaller marshaller = jaxbContext.createMarshaller();
            marshaller.setProperty(Marshaller.JAXB_FORMATTED_OUTPUT, true);
            marshaller.setSchema(JaxbUtil.getSchema(resourcePath, targetClass));
            return marshaller;
        } else {
            return KNOWHOW_MARSHALLER;
        }
    }

    /**
     * Obtaining Schema.<br/>
     * 
     * @param resourcePath
     *            The path of the schema file
     * @param targetClass
     *            Class object
     * @return Schema
     * @throws SAXException
     *             Schema acquisition error
     * @throws MalformedURLException
     *             MalformedURL error
     */
    public static Schema getSchema(String resourcePath, Class<?> targetClass)
            throws SAXException, MalformedURLException {
        SchemaFactory sf = SchemaFactory
                .newInstance(javax.xml.XMLConstants.W3C_XML_SCHEMA_NS_URI);
        return sf.newSchema(targetClass.getClassLoader().getResource(
                resourcePath));
    }

    /**
     * Create an error message of Anne Marshall processing.<br/>
     * 
     * @param errorMsg
     *            Error message
     * @param errMap
     *            Error information storage map
     * @return Error message + line-number information
     */
    public static String getUnmarshalErrorMessage(String errorMsg,
            Map<String, Integer> errMap) {
        String resultMessage = "";
        return errorMsg + resultMessage;
    }
}
