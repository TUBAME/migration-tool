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

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.util.MissingResourceException;
import java.util.PropertyResourceBundle;
import java.util.ResourceBundle;

import tubame.common.logging.CmnJbmToolsLoggingUtil;
import tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;

import org.eclipse.core.runtime.preferences.IEclipsePreferences;
import org.eclipse.core.runtime.preferences.InstanceScope;
import org.eclipse.jface.resource.ImageDescriptor;
import org.eclipse.swt.widgets.Display;
import org.eclipse.swt.widgets.Shell;
import org.eclipse.ui.ISelectionService;
import org.eclipse.ui.IWorkbenchWindow;
import org.eclipse.ui.plugin.AbstractUIPlugin;
import org.osgi.framework.BundleContext;
import org.osgi.service.prefs.BackingStoreException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.portability.logic.InitializePotability;
import tubame.portability.plugin.dialog.ErrorDialog;
import tubame.portability.util.FileUtil;
import tubame.portability.util.PluginUtil;
import tubame.portability.util.PythonUtil;
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
	
	
	private static String OS_NAME;

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
        
        OS_NAME = System.getProperty("os.name").toLowerCase();
        if(isSupportPyPlatform() && !isExistJbmstModulePath()){
        	//win,linux又はmacの場合で、かつ、JbmstModuleが存在しない場合に、bin配下のjbmstを実行可能にするようために、bin配下のjbmst_{os}.zipを展開する。
        	String jbmstModuleZipPath = getJbmstModuleZipPath();
        	File file = new File(jbmstModuleZipPath);
        	FileUtil.unzip(file,file.getParentFile());
        	
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

    private boolean isExistJbmstModulePath() throws IOException {
		String jbmstModulePath = getJbmstModulePath();
		return new File(jbmstModulePath).exists();
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
    
    /**
     * Get a set of Plugin.
     * 
     * @return Setting Plugin
     */
    public static IEclipsePreferences getPreferences() {
        IEclipsePreferences prefs = InstanceScope.INSTANCE.getNode(PLUGIN_ID);
        return prefs;
    }
    
    /**
     * Save the settings of Plugin.<br/>
     * 
     */
    public static void savePreferences(String key,String val) {
        IEclipsePreferences prefs = getPreferences();
        
        try {
        	prefs.put(key, val);
            prefs.flush();
        } catch (BackingStoreException e) {
            String message = getResourceString(Activator.class.getName()
                    + ".err.msg.PrefSaveFailed");
            ErrorDialog.openErrorDialog(getActiveWorkbenchShell(), e, message);
        }
    }
    
    /**
     * Save the settings of Plugin.<br/>
     * 
     */
    public static String getPreferences(String key) {
        IEclipsePreferences prefs = getPreferences();
        if (prefs != null){
        	return prefs.get(key, null);
        }else{
        	return null;
        }

    }
    
    
    /**
     * Get the shell of the active workbench.<br/>
     * 
     * @return shell Shell of workbench active
     */
    public static Shell getActiveWorkbenchShell() {
        IWorkbenchWindow window = getActiveWorkbenchWindow();
        return window != null ? window.getShell() : getStandardDisplay()
                .getActiveShell();
    }
    /**
     * Get the window of the workbench active.<br/>
     * 
     * @return IWorkbenchWindow Window of the workbench active
     */
    public static IWorkbenchWindow getActiveWorkbenchWindow() {
        return Activator.getDefault().getWorkbench().getActiveWorkbenchWindow();
    }

    /**
     * Get the display.<br/>
     * If the thread that is calling this method is in possession of the
     * relevant display, <br/>
     * get the related display, and get the default display if not in
     * possession.<br/>
     * 
     * @return Display
     */
    private static Display getStandardDisplay() {
        Display display = Display.getCurrent();
        if (display == null) {
            display = Display.getDefault();
        }
        return display;
    }
    
    public static boolean isLinuxPlatform() {
        return OS_NAME.startsWith("linux");
    }

    public static boolean isMacPlatform() {
        return OS_NAME.startsWith("mac");
    }

    public static boolean isWindowsPlatform() {
        return OS_NAME.startsWith("windows");
    }
    
    public static boolean isSupportPyPlatform(){
    	return isWindowsPlatform() || isLinuxPlatform() || isMacPlatform();
    }
    
    public static String getJbmstModulePath() throws IOException{
    	if(isWindowsPlatform()){
    		return PythonUtil.getWinSearchModulePath();
    	}else if (isMacPlatform()){
    		return PythonUtil.getMacSearchModulePath();
    	}else if (isLinuxPlatform()){
    		return PythonUtil.getLinuxSearchModulePath();
    	}
    	return null;
    }
    

    public static String getJbmstModuleZipPath() throws IOException{
    	if(isWindowsPlatform()){
    		return PythonUtil.getWinSearchModuleZipPath();
    	}else if (isMacPlatform()){
    		return PythonUtil.getMacSearchModuleZipPath();
    	}else if (isLinuxPlatform()){
    		return PythonUtil.getLinuxSearchModuleZipPath();
    	}
    	return null;
    }
    
}
