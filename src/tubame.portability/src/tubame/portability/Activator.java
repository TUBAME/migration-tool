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
package tubame.portability;

import java.net.URL;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import tubame.common.logging.CmnJbmToolsLoggingUtil;
import tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.logic.InitializePotability;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.resource.ApplicationPropertyUtil;
import tubame.portability.util.resource.MessageUtil;

/**
 * Make the management of the life cycle of the Plugin.<br/>
 * It is called initialization and run, at the timing of the end of the Plugin.<br/>
 */
public class Activator extends AbstractUIPlugin {
    /**
     * Logger
     */
    private static Logger LOGGER = LoggerFactory.getLogger(Activator.class);

    // The plug-in ID
    public static final String PLUGIN_ID = "tubame.portability"; //$NON-NLS-1$

    // The shared instance
    private static Activator plugin;

	private ResourceBundle resourceBundle;

    /**
     * The constructor.<br/>
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
        try {
            this.resourceBundle = PropertyResourceBundle
                    .getBundle("resources.properties.message");
        } catch (MissingResourceException x) {
            this.resourceBundle = null;
        }
        
        
        // Bundling
        CmnJbmToolsLoggingUtil.configureLoggerForPlugin(PLUGIN_ID, Activator
                .getDefault().getStateLocation().toFile(),
                getResources("/logback_template.xml"));
        // Know-how Biz properties file bundle configuration
        MessagePropertiesUtil.setBundle(getResources(
                ApplicationPropertyUtil.PROPERTIES_PATH_MSG_KNOWHOW)
                .openStream());
        ApplicationPropertiesUtil.setBundle(getResources(
                ApplicationPropertyUtil.PROPERTIES_PATH_APP_KNOWHOW)
                .openStream());
        // JAXB initial processing
        InitializePotability.initializeMarshaller();
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
        LOGGER.info(MessageUtil.INF_END);
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
     * Returns an image descriptor for the image file at the given.<br/>
     * plug-in relative path.<br/>
     * 
     * @param path
     *            the path
     * @return the image descriptor
     */
    public static ImageDescriptor getImageDescriptor(String path) {
        return imageDescriptorFromPlugin(PLUGIN_ID, path);
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
    
    /**
     * Get the value corresponding to the key from the message file by the
     * resource bundle mechanism.<br/>
     * 
     * @param key
     *            Message key
     * @return Message
     */
    public static String getResourceString(String key) {
        try {
            return plugin.resourceBundle.getString(key);
        } catch (MissingResourceException e) {
            return key;
        }
    }
    
}
