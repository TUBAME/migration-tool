/*
 * WSDestLibMetaDataGeneratorService.java
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
package tubame.wsearch.biz;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import javassist.ClassPool;
import javassist.CtClass;

import javax.xml.parsers.ParserConfigurationException;

import org.codehaus.plexus.util.xml.pull.XmlPullParserException;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.xml.sax.SAXException;

import tubame.wsearch.biz.analyzer.xml.XsdAnalyzer;
import tubame.wsearch.biz.model.AbstractWSPhaseTask;
import tubame.wsearch.biz.model.LibraryMetaDataGenerationInput;
import tubame.wsearch.biz.model.LibraryModel;
import tubame.wsearch.biz.model.PhaseInbound;
import tubame.wsearch.biz.model.PhaseOutbound;
import tubame.wsearch.biz.model.PomSetting;
import tubame.wsearch.biz.model.LibraryModel.LIBRARY_TYPE;
import tubame.wsearch.biz.util.FileVisitor;
import tubame.wsearch.biz.util.PomReader;
import tubame.wsearch.biz.util.PomWriter;
import tubame.wsearch.biz.util.ZipUtil;
import tubame.wsearch.biz.util.resource.MessageUtil;

/**
 * This is a service class that generates porting library information.<br/>
 * Extracts information to help you scan jar that exist in the library, xsd,
 * dtd, the tld, etc to be used in the comparison process.<br/>
 * Write to POM file the extracted information.<br/>
 */
public class WSDestLibMetaDataGeneratorService implements
        WSPhaseService<LibraryMetaDataGenerationInput> {

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(WSDestLibMetaDataGeneratorService.class);

    /**
     * Map of the class held by the library
     */
    private Map<String, Set<String>> classMap;

    /**
     * Map of the deprecated class held by the library
     */
    private Map<String, Set<String>> deprecatedMap;

    /**
     * List of the DTD held by the library
     */
    private Set<String> dtdList;

    /**
     * List of the TLD held by the library
     */
    private Set<String> tldList;

    /**
     * List of the TNS and XSD that holds the library
     */
    private Map<String, String> xsdList;

    /**
     * Class pool for use Javassist
     */
    private ClassPool pool;

    /**
     * Analyzer for parsing the XSD file
     */
    private XsdAnalyzer xsdAnalyzer;

    /**
     * Format of the date
     */
    private SimpleDateFormat dateFormat;

    /**
     * Constructor.<br/>
     * make the generation of XSD and analyzer class pool.<br/>
     */
    public WSDestLibMetaDataGeneratorService() {
        dateFormat = new SimpleDateFormat("yyyyMMddHHmmssSSS");
        try {
            pool = ClassPool.getDefault();
            xsdAnalyzer = new XsdAnalyzer();

        } catch (ParserConfigurationException e) {
            // Just logged for now so do the execute within the error
            // notification.
            LOGGER.error(
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".exception.constructor"), e);

        } catch (SAXException e) {
            // Just logged for now so do the execute within the error
            // notification.
            LOGGER.error(
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".exception.constructor"), e);
        }
    }

    /**
     * {@inheritDoc}
     */
    public PhaseOutbound execute(
            final PhaseInbound<LibraryMetaDataGenerationInput> inbound) {
        LOGGER.info(MessageUtil.getResourceString("debug.msg.start.process")
                + this.getClass().getName() + "#execute()");

        this.initialize();

        // The error notification here if an exception has occurred in the
        // constructor.
        if ((this.pool == null) || (this.xsdAnalyzer == null)) {
            return new PhaseOutbound(AbstractWSPhaseTask.STATUS.ERR_EXIT);
        }

        AbstractWSPhaseTask.STATUS status = AbstractWSPhaseTask.STATUS.END;

        try {
            LibraryModel lib = inbound.getValue().getCurrentLibrary();

            if (lib.getType() == LIBRARY_TYPE.MAVEN) {
                File jar = lib.getCurrentTarget();
                this.analyzeJar(jar, inbound.getValue()
                        .getLibraryCacheTempPath());
                this.generatePom(lib, inbound.getValue()
                        .getLibraryCacheRootPath());
            } else {
                File dir = lib.getCurrentTarget();
                this.analyzeDir(dir, inbound.getValue()
                        .getLibraryCacheTempPath());

                if ((this.classMap.size() != 0) || (this.dtdList.size() != 0)
                        || (this.tldList.size() != 0)
                        || (this.xsdList.size() != 0)) {
                    this.generatePom(lib, inbound.getValue()
                            .getLibraryCacheRootPath());
                }
            }

        } catch (IOException e) {
            // When an exception occurs, you can continue the process by
            // recording the log.
            LOGGER.error(
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".exception.execute"), e);
            status = AbstractWSPhaseTask.STATUS.ERR_CONTINUE;

        } catch (SAXException e) {
            // When an exception occurs, you can continue the process by
            // recording the log.
            LOGGER.error(
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".exception.execute"), e);
            status = AbstractWSPhaseTask.STATUS.ERR_CONTINUE;

        } catch (XmlPullParserException e) {
            // When an exception occurs, you can continue the process by
            // recording the log.
            LOGGER.error(
                    MessageUtil.getResourceString(this.getClass().getName()
                            + ".exception.execute"), e);
            status = AbstractWSPhaseTask.STATUS.ERR_CONTINUE;
        }

        LOGGER.info(MessageUtil.getResourceString("debug.msg.end.process")
                + this.getClass().getName() + "#execute()");
        return new PhaseOutbound(status);
    }

    /**
     * Do the initialization of the library before analysis.<br/>
     * Create a new map or list that holds a variety of library information.<br/>
     */
    private void initialize() {
        classMap = new HashMap<String, Set<String>>();
        deprecatedMap = new HashMap<String, Set<String>>();
        dtdList = new HashSet<String>();
        tldList = new HashSet<String>();
        xsdList = new HashMap<String, String>();
    }

    /**
     * Analyze the Jar file.<br/>
     * It will remain at that information by reading class file that is included
     * in the Jar file, xsd file, dtd file, the tld file.<br/>
     * 
     * @param jar
     *            Jar file to be parsed
     * @param tmpPath
     *            The destination path of the temporary file
     * @throws IOException
     *             If the file I / O error occurs
     */
    private void analyzeJar(File jar, String tmpPath) throws IOException {

        String fileName = jar.getName();
        String tempName = fileName.substring(0, fileName.lastIndexOf("."))
                + dateFormat.format(Calendar.getInstance().getTime());
        File tempDir = new File(tmpPath, tempName);
        if (jar.getName().endsWith(".jar.pack")) {
            jar = ZipUtil.unpack(jar, tempDir);
            fileName = jar.getName();
            tempName = fileName.substring(0, fileName.lastIndexOf("."))
                    + dateFormat.format(Calendar.getInstance().getTime());
            tempDir = new File(tmpPath, tempName);
            ZipUtil.unzip(jar, tempDir);
            jar.delete();
            jar.getParentFile().delete();
        } else {
            ZipUtil.unzip(jar, tempDir);
        }
        FileVisitor.walkFileTree(tempDir, new FileVisitor() {
            @Override
            public FileVisitResult visitFile(final File file)
                    throws IOException {
                LOGGER.trace(MessageUtil
                        .getResourceString("debug.msg.read.file") + file);
                try {
                    String fileName = file.getName();
                    int dotIndex = fileName.lastIndexOf(".");
                    if (dotIndex >= 0) {
                        String extension = fileName.substring(dotIndex + 1);
                        if ("class".equals(extension)) {
                            analyzeClass(file);
                        } else if ("xsd".equals(extension)) {
                            xsdAnalyzer.analyze(file);
                            xsdList.put(fileName,
                                    xsdAnalyzer.getTargetNamespace());
                        } else if ("dtd".equals(extension)) {
                            dtdList.add(fileName);
                        } else if ("tld".equals(extension)) {
                            tldList.add(fileName);
                        }
                    }
                    file.delete();
                } catch (SAXException e) {
                    throw new IOException(e);
                }
                return FileVisitResult.CONTINUE;
            }

            @Override
            public FileVisitResult postVisitDirectory(File dir)
                    throws IOException {
                // Delete the directory after scanning.
                dir.delete();
                return FileVisitResult.CONTINUE;
            }
        });
        return;
    }

    /**
     * Analyze the directory.<br/>
     * It will remain at that information by reading class file that is included
     * in the directory, xsd file, dtd file, the tld file.<br/>
     * 
     * @param dir
     *            Directory to be parsed
     * @param tmpPath
     *            The destination path of the temporary file
     * @throws SAXException
     *             If a SAX error occurs
     * @throws IOException
     *             If the file I / O error occurs
     */
    private void analyzeDir(final File dir, final String tmpPath)
            throws SAXException, IOException {
        FileVisitor.walkFileTree(dir, new FileVisitor() {
            @Override
            public FileVisitResult visitFile(final File file)
                    throws IOException {
                LOGGER.trace(MessageUtil
                        .getResourceString("debug.msg.read.file") + file);
                String fileName = file.getName();
                int dotIndex = fileName.lastIndexOf(".");
                if (dotIndex >= 0) {
                    String extension = fileName.substring(dotIndex + 1);
                    if ("jar".equals(extension)) {
                        analyzeJar(file, tmpPath);
                    } else if ("pack".equals(extension)) {
                        int secondDotIndex = fileName.lastIndexOf(".",
                                (dotIndex - 1));
                        String secondExtenxion = fileName
                                .substring(secondDotIndex + 1);
                        if ("jar.pack".equals(secondExtenxion)) {
                            analyzeJar(file, tmpPath);
                        }
                    } else if ("xsd".equals(extension)) {
                        try {
                            xsdAnalyzer.analyze(file);
                        } catch (SAXException e) {
                            throw new IOException(e);
                        }
                        xsdList.put(file.getName(),
                                xsdAnalyzer.getTargetNamespace());
                    } else if ("dtd".equals(extension)) {
                        dtdList.add(fileName);
                    } else if ("tld".equals(extension)) {
                        tldList.add(fileName);
                    }
                }
                return FileVisitResult.CONTINUE;
            }
        });
    }

    /**
     * Create a POM file that contains the results of the analysis library.<br/>
     * Please set the null if the POM file of the analysis based information
     * does not exist.<br/>
     * 
     * @param library
     *            Model that contains the information of the library were
     *            analyzed
     * @param path
     *            Destination path of POM file
     * @throws IOException
     *             If an exception about the file input and output occurs
     * @throws XmlPullParserException
     *             If it fails to parse XML
     */
    private void generatePom(LibraryModel library, String path)
            throws IOException, XmlPullParserException {
        String sourcePom = null;
        if (library.getCurrentPom() != null) {
            sourcePom = library.getCurrentPom().toString();
        }
        String directory = library.getTargetDirectory();
        if (directory.endsWith("/")) {
            directory = directory.substring(0, directory.length() - 1);
        }

        // Set the POM setting unit.
        PomSetting setting;
        if (library.getType() == LIBRARY_TYPE.MAVEN) {
            PomReader reader = new PomReader();
            reader.loadPomFile(sourcePom);
            setting = reader.getPomSetting();
            setting.setGroupId(library.getLibName() + ".maven."
                    + setting.getGroupId());
        } else {
            setting = new PomSetting();
            String replaced = directory.replaceAll("\\\\", "/").replaceAll(
                    "\\/", ".");
            setting.setGroupId(library.getLibName() + ".file." + replaced);
            setting.setArtifactId("migration.wsearch");
            setting.setVersion("1.0.0");
        }

        // If POM export target directory does not exist, create a directory.
        File parent = new File(path + File.separator + library.getLibName()
                + File.separator + directory);
        if (!parent.exists()) {
            parent.mkdirs();
        }

        // Write the POM file.
        String pomFileName;
        if (sourcePom == null) {
            if ("".equals(directory)) {
                pomFileName = library.getLibName() + ".pom";
            } else {
                String replaced = directory.replaceAll("\\\\", "/").replaceAll(
                        "\\/", ".");
                pomFileName = replaced + ".pom";
            }
        } else {
            pomFileName = new File(sourcePom).getName();
        }
        String fileName = path + File.separator + library.getLibName()
                + File.separator + directory + File.separator + pomFileName;
        PomWriter pomUtil = new PomWriter();
        pomUtil.setPomSetting(setting);
        pomUtil.write(fileName, classMap, deprecatedMap, dtdList, tldList,
                xsdList);

        return;
    }

    /**
     * analyze the Java class information contained in the class file.<br/>
     * Read the information and non-recommended and package information that is
     * included in the class file using Javassist.<br/>
     * Register the name of the class was analyzed to map deprecated classes and
     * mapped according to the information that has been read.<br/>
     * 
     * @param file
     *            Class file to be parsed
     * @throws IOException
     *             If an exception about the file input and output occurs
     */
    private void analyzeClass(File file) throws IOException {
        InputStream in = null;
        try {
            LOGGER.trace(MessageUtil.getResourceString("debug.msg.read.file")
                    + file);
            in = new BufferedInputStream(new FileInputStream(file));
            CtClass clazz = pool.makeClass(in);
            String name = clazz.getSimpleName();
            if (!isNamelessClass(name)) {
                String packageName = clazz.getPackageName();

                // Convert to "." If the class name to "$" and contains
                // (internal class).
                name = name.replaceAll("\\$", ".");
                for (Object annotation : clazz.getAvailableAnnotations()) {
                    if ("@java.lang.Deprecated".equals(annotation.toString())) {
                        Set<String> deprecatedSet = deprecatedMap
                                .get(packageName);
                        if (deprecatedSet == null) {
                            deprecatedSet = new HashSet<String>();
                        }
                        deprecatedSet.add(name);
                        deprecatedMap.put(packageName, deprecatedSet);
                        break;
                    }
                }
                Set<String> classSet = classMap.get(packageName);
                if (classSet == null) {
                    classSet = new HashSet<String>();
                }
                classSet.add(name);
                classMap.put(packageName, classSet);
                clazz.detach();
            }
        } finally {
            if (in != null) {
                in.close();
            }
        }
    }

    /**
     * Determine whether an anonymous inner class from the class name.<br/>
     * 
     * @param name
     *            Class name
     * @return Determines whether an anonymous inner class
     */
    private static boolean isNamelessClass(String name) {
        int dollarIndex = name.indexOf("$");
        if (dollarIndex >= 0) {
            String innerClassName = name.substring(dollarIndex + 1);
            try {
                Integer.parseInt(innerClassName);
                // If no exception occurs, return the anonymous inner class.
                return true;
            } catch (NumberFormatException e) {
                // If an exception occurs, is an internal class normal.
            }
        }
        return false;
    }
}
