/*
 * Activator.java
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
package org.tubame.knowhow;

import java.net.URL;

import org.tubame.common.logging.CmnJbmToolsLoggingUtil;

import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import org.tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import org.tubame.knowhow.plugin.logic.FileManagement;
import org.tubame.knowhow.plugin.logic.KnowhowManagement;
import org.tubame.knowhow.plugin.ui.preference.KnowhowPreferenceInitializer;
import org.tubame.knowhow.util.resource.ResourceUtil;

/**
 * Make the management of the life cycle of the Plugin.<br/>
 * It is called initialization and run, at the timing of the end of the Plugin.<br/>
 * 
 */
public class Activator extends AbstractUIPlugin {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(Activator.class);

    /** The plug-in ID */
    public static final String PLUGIN_ID = "org.tubame.knowhow"; //$NON-NLS-1$

    /** The shared instance. */
    private static Activator plugin;

    /**
     * The constructor.
     */
    public Activator() {
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#start(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void start(BundleContext context) throws Exception {
        super.start(context);
        plugin = this;
        // Bundling
        CmnJbmToolsLoggingUtil.configureLoggerForPlugin(PLUGIN_ID, Activator
                .getDefault().getStateLocation().toFile(),
                getResources("/logback_template.xml"));

        MessagePropertiesUtil.setBundle(getResources(
                ResourceUtil.massagesPropertiesPath).openStream());
        ApplicationPropertiesUtil.setBundle(getResources(
                ResourceUtil.applicationPropertiesPath).openStream());

        Activator.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_PLUGIN_START)
                + "Activator#start");

        // The planned changes to Jaxb initial processing background processing
        KnowhowManagement.initializeMarshaller();
        // Acquisition of know-how XML
        FileManagement
                .setPortabilityKnowhowFilePath(KnowhowPreferenceInitializer
                        .getKnowhowXml());
    }

    /*
     * (non-Javadoc)
     * 
     * @see
     * org.eclipse.ui.plugin.AbstractUIPlugin#stop(org.osgi.framework.BundleContext
     * )
     */
    @Override
    public void stop(BundleContext context) throws Exception {
        Activator.LOGGER.info(MessagePropertiesUtil
                .getMessage(MessagePropertiesUtil.LOG_PLUGIN_STOP)
                + "Activator#start");

        // Saving XML know-how
        KnowhowPreferenceInitializer.setKnowhowXml(FileManagement
                .getPortabilityKnowhowFilePath());
        plugin = null;
        super.stop(context);
    }

    /**
     * Returns the shared instance.<br/>
     * 
     * @return the shared instance
     */
    public static Activator getDefault() {
        return plugin;
    }

    /**
     * Read the resource file.<br/>
     * 
     * @param key
     *            Resource key name
     */
    private static URL getResources(String key) {
        return Activator.getDefault().getBundle().getResource(key);
    }
}
