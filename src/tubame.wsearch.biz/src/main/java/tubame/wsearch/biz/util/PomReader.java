/*
 * PomReader.java
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
package tubame.wsearch.biz.util;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.util.Arrays;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Properties;
import java.util.Set;
import java.util.regex.Pattern;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Reader;
import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.wsearch.biz.model.PomSetting;
import tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * The class that reads the POM file.<br/>
 */
public class PomReader {

    /**
     * Model that contains the information of the POM file
     */
    private Model model;

    /**
     * Pattern of the XSD file name tag
     */
    private Pattern xsdPattern;
    /**
     * Pattern of the DTD file name tag
     */
    private Pattern dtdPattern;
    /**
     * Pattern of the TLD file name tag
     */
    private Pattern tldPattern;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PomReader.class);

    /**
     * Read the POM file.<br/>
     * 
     * @param fileName
     *            File name
     * @throws IOException
     *             If it fails to load the file
     * @throws XmlPullParserException
     *             If it fails to parse XML
     */
    public void loadPomFile(String fileName) throws IOException,
            XmlPullParserException {
        if (new File(fileName).exists()) {
            MavenXpp3Reader reader = new MavenXpp3Reader();
            InputStream in = null;
            try {
                LOGGER.debug(MessageUtil
                        .getResourceString("debug.msg.read.file") + fileName);
                in = new BufferedInputStream(new FileInputStream(new File(
                        fileName)));
                this.model = reader.read(in);
            } finally {
                if (in != null) {
                    in.close();
                }
            }
        } else {
            this.model = new Model();
        }
        this.xsdPattern = Pattern.compile("^xsd\\.[0-9]+\\.filename$");
        this.dtdPattern = Pattern.compile("^dtd\\.[0-9]+\\.filename$");
        this.tldPattern = Pattern.compile("^tld\\.[0-9]+\\.filename$");
    }

    /**
     * Get the basic settings of the POM file.<br/>
     * 
     * @return Basic settings of the POM file
     */
    public PomSetting getPomSetting() {
        if (this.model == null) {
            return null;
        } else {
            PomSetting setting = new PomSetting();
            String groupId = model.getGroupId();
            if (groupId == null) {
                groupId = model.getParent().getGroupId();
            }
            setting.setGroupId(groupId);
            setting.setArtifactId(model.getArtifactId());
            String version = model.getVersion();
            if (version == null) {
                version = model.getParent().getVersion();
            }
            setting.setVersion(version);
            return setting;
        }
    }

    /**
     * Get the package information that is included in the POM file.<br/>
     * 
     * @return Package information
     */
    public String[] getPackages() {
        String packagesString = model.getProperties().getProperty("packages");
        if (packagesString == null) {
            return new String[0];
        }
        return packagesString.split(",");
    }

    /**
     * Get the class information that is included in the package.get the class
     * information that is included in the package.<br/>
     * 
     * @param packageName
     *            Package Name
     * @return Class information
     */
    public Set<String> getClasses(String packageName) {
        String classesString = model.getProperties().getProperty(
                "classes." + packageName);
        if (classesString == null) {
            return new HashSet<String>();
        }
        return new HashSet<String>(Arrays.asList(classesString.split(",")));
    }

    /**
     * Get the information of deprecated class that is included in the package.<br/>
     * 
     * @param packageName
     *            Package Name
     * @return Deprecated class information
     */
    public Set<String> getDeprecated(String packageName) {
        String classesString = model.getProperties().getProperty(
                "classes.deprecated." + packageName);
        if (classesString == null) {
            return new HashSet<String>();
        }
        return new HashSet<String>(Arrays.asList(classesString.split(",")));
    }

    /**
     * Get the information in the XML namespace and XSD that is included in the
     * POM file.<br/>
     * 
     * @return Information in the XML namespace and XSD
     */
    public Map<String, String> getXsdList() {
        Map<String, String> xsdList = new HashMap<String, String>();
        Properties properties = model.getProperties();
        for (String key : properties.stringPropertyNames()) {
            if (this.xsdPattern.matcher(key).matches()) {
                xsdList.put(properties.getProperty(key), properties
                        .getProperty(key.replaceAll("filename", "tns")));
            }
        }
        return xsdList;
    }

    /**
     * Get the DTD information that is included in the POM file.<br/>
     * 
     * @return DTD information
     */
    public Set<String> getDtdList() {
        Set<String> dtdList = new HashSet<String>();
        Properties properties = model.getProperties();
        for (String key : properties.stringPropertyNames()) {
            if (this.dtdPattern.matcher(key).matches()) {
                dtdList.add(properties.getProperty(key));
            }
        }
        return dtdList;
    }

    /**
     * Get the TLD information contained in the POM file.
     * 
     * @return TLD information
     */
    public Set<String> getTldList() {
        Set<String> tldList = new HashSet<String>();
        Properties properties = model.getProperties();
        for (String key : properties.stringPropertyNames()) {
            if (this.tldPattern.matcher(key).matches()) {
                tldList.add(properties.getProperty(key));
            }
        }
        return tldList;
    }

    /**
     * Get the library path.<br/>
     * 
     * @return Library path
     */
    public String getLibraryPath() {
        return model.getProperties().getProperty("library.path");
    }

    /**
     * Check library whether Maven library.<br/>
     * 
     * @return Whether Maven library
     */
    public boolean isMavenType() {
        return Boolean.valueOf(model.getProperties().getProperty(
                "library.type.maven"));
    }

    /**
     * Determine if the cache information of embedded plug-in.<br/>
     * 
     * @return Whether cache information of embedded plug-in
     */
    public boolean isEmbeddedCache() {
        return Boolean.valueOf(model.getProperties().getProperty(
                "library.plugin.embedded"));
    }
}
