/*
 * PomWriter.java
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

import java.io.BufferedWriter;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStreamWriter;
import java.util.Map;
import java.util.Set;

import org.apache.maven.model.Model;
import org.apache.maven.model.io.xpp3.MavenXpp3Writer;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.wsearch.biz.model.PomSetting;
import tubame.wsearch.biz.util.resource.MessageUtil;
import tubame.wsearch.biz.util.resource.ResourceUtil;

/**
 * It is a class that writes POM file.<br/>
 */
public class PomWriter {

    /**
     * Version of POM model (4.0.0 fixed)
     */
    private static final String POM_MODEL_VERSION = "4.0.0";

    /**
     * Basic settings of the POM file
     */
    private PomSetting setting;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(PomWriter.class);

    /**
     * Set the basic settings of the POM file.<br/>
     * 
     * @param setting
     *            Basic settings of the POM file
     */
    public void setPomSetting(PomSetting setting) {
        this.setting = setting;
    }

    /**
     * Write the POM file.<br/>
     * 
     * @param fileName
     *            File name to be written and
     * @param classMap
     *            Map of classes per package
     * @param deprecatedMap
     *            Map of deprecated classes per package
     * @param dtdList
     *            List of DTD
     * @param tldList
     *            List of TLD
     * @param xsdList
     *            List of XML namespace and XSD
     * @throws IOException
     *             If it fails to write the file
     */
    public void write(String fileName, Map<String, Set<String>> classMap,
            Map<String, Set<String>> deprecatedMap, Set<String> dtdList,
            Set<String> tldList, Map<String, String> xsdList)
            throws IOException {
        Model model = new Model();

        model.setModelVersion(POM_MODEL_VERSION);
        model.setGroupId(setting.getGroupId());
        model.setArtifactId(setting.getArtifactId());
        model.setVersion(setting.getVersion());

        // Writing a Package List.
        if (classMap.size() > 0) {
            StringBuilder packages = new StringBuilder();
            for (String packageName : classMap.keySet()) {
                packages.append(packageName + ",");
            }
            packages.delete((packages.length() - 1), packages.length());
            model.addProperty("packages", packages.toString());
        } else {
            model.addProperty("packages", "");
        }

        // Writing Class List.
        for (String packageName : classMap.keySet()) {
            StringBuilder classes = new StringBuilder();
            for (String className : classMap.get(packageName)) {
                classes.append(className + ",");
            }
            classes.delete((classes.length() - 1), classes.length());
            model.addProperty(("classes." + packageName), classes.toString());
        }

        // Writing deprecated Class List.
        for (String packageName : deprecatedMap.keySet()) {
            StringBuilder classes = new StringBuilder();
            for (String className : deprecatedMap.get(packageName)) {
                classes.append(className + ",");
            }
            classes.delete((classes.length() - 1), classes.length());
            model.addProperty(("classes.deprecated." + packageName),
                    classes.toString());
        }

        // Write the XSD information.
        int counter = 0;
        for (String xsdFileName : xsdList.keySet()) {
            counter++;
            model.addProperty(("xsd." + counter + ".filename"), xsdFileName);
            String tns = xsdList.get(xsdFileName);
            if (tns != null) {
                model.addProperty(("xsd." + counter + ".tns"), tns);
            }
        }

        // Writing of the DTD information.
        counter = 0;
        for (String dtdFileName : dtdList) {
            counter++;
            model.addProperty(("dtd." + counter + ".filename"), dtdFileName);
        }

        // Write the TLD information.
        counter = 0;
        for (String tldFileName : tldList) {
            counter++;
            model.addProperty(("tld." + counter + ".filename"), tldFileName);
        }

        MavenXpp3Writer writer = new MavenXpp3Writer();
        LOGGER.debug(MessageUtil.getResourceString("debug.msg.write.file")
                + fileName);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName),
                    ResourceUtil
                            .getResourceString("default.xml.character.code")));
            writer.write(bw, model);
        } finally {
            if (bw != null) {
                bw.flush();
                bw.close();
            }
        }
    }

    /**
     * Write the POM file.<br/>
     * 
     * @param fileName
     *            File name to be written and
     * @param libraryPath
     *            Library path
     * @param mavenType
     *            Whether Maven library
     * @param pluginEmbedded
     *            Whether information embedded plug-in
     * @throws IOException
     *             If it fails to write the file
     */
    public void write(String fileName, String libraryPath, boolean mavenType,
            boolean pluginEmbedded) throws IOException {
        Model model = new Model();
        model.setModelVersion(POM_MODEL_VERSION);
        model.setGroupId(setting.getGroupId());
        model.setArtifactId(setting.getArtifactId());
        model.setVersion(setting.getVersion());
        model.setDescription(setting.getDescription());
        model.addProperty("library.path", libraryPath);
        model.addProperty("library.type.maven", Boolean.valueOf(mavenType)
                .toString());
        model.addProperty("library.plugin.embedded",
                Boolean.valueOf(pluginEmbedded).toString());
        MavenXpp3Writer writer = new MavenXpp3Writer();
        LOGGER.debug(MessageUtil.getResourceString("debug.msg.write.file")
                + fileName);
        BufferedWriter bw = null;
        try {
            bw = new BufferedWriter(new OutputStreamWriter(
                    new FileOutputStream(fileName),
                    ResourceUtil
                            .getResourceString("default.xml.character.code")));
            writer.write(bw, model);
        } finally {
            if (bw != null) {
                bw.flush();
                bw.close();
            }
        }
    }
}
