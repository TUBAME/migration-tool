/*
 * KnowhowManagement.java
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
package tubame.knowhow.plugin.logic;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.CountDownLatch;

import org.docbook.ns.docbook.Article;
import org.docbook.ns.docbook.Info;
import org.docbook.ns.docbook.Para;
import org.docbook.ns.docbook.Section;
import org.docbook.ns.docbook.Title;
import org.eclipse.core.runtime.jobs.Job;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.common.util.CmnFileUtil;
import tubame.common.util.CmnStringUtil;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.logic.DocBookFacade;
import tubame.knowhow.biz.logic.PortabilityKnowhowFacade;
import tubame.knowhow.biz.logic.converter.PortabilityKnowhowConverter;
import tubame.knowhow.biz.model.generated.knowhow.PortabilityKnowhow;
import tubame.knowhow.biz.util.JaxbUtil;
import tubame.knowhow.biz.util.resource.ApplicationPropertiesUtil;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.logic.command.CommandKnowhowDataList;
import tubame.knowhow.plugin.logic.convert.EntryToKnowhowXmlConvert;
import tubame.knowhow.plugin.model.editor.EntryOperator;
import tubame.knowhow.plugin.model.view.AbstractViewType;
import tubame.knowhow.plugin.model.view.CategoryViewType;
import tubame.knowhow.plugin.model.view.CheckItemViewType;
import tubame.knowhow.plugin.model.view.KnowhowDetailType;
import tubame.knowhow.plugin.model.view.KnowhowViewType;
import tubame.knowhow.plugin.model.view.PortabilityKnowhowListViewOperation;
import tubame.knowhow.plugin.model.view.SearchInfoViewType;
import tubame.knowhow.plugin.ui.dialog.InitializeMarshallerJob;
import tubame.knowhow.plugin.ui.editor.multi.MaintenanceKnowhowMultiPageEditor;
import tubame.knowhow.plugin.ui.view.KnowhowEntryTreeViewer;
import tubame.knowhow.plugin.ui.wizard.register.AbstractCreateKnowhowPage;
import tubame.knowhow.plugin.ui.wizard.register.CreateCategoryPage;
import tubame.knowhow.plugin.ui.wizard.register.CreateCheckItemPage;
import tubame.knowhow.plugin.ui.wizard.register.CreateKnowhowPage;
import tubame.knowhow.plugin.ui.wizard.register.CreateSearchInfoPage;
import tubame.knowhow.util.FileUtil;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * This is the class that controls the know-how information.<br/>
 * It is a class that does the read / write / update of the information
 * know-how.<br/>
 * JDK7
 */
public final class KnowhowManagement {

    /** Logger */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(KnowhowManagement.class);
    /** Parameters character of LOGGER output */
    private static final String LOGGER_FILEPATH = "[filePath]";
    /** Object to be converted to know-how GUI from know-how XML. */
    private static CommandKnowhowDataList convertKnonwhowDataList;
    /** Know-how XML information */
    private static PortabilityKnowhowConverter portabilityKnowhowConverter;
    /** Converter that converts the data know-how XML data know-how GUI. */
    private static EntryToKnowhowXmlConvert entryToKnowhowXmlConvert;
    /** Know how information list */
    private static List<KnowhowDetailType> knowhowDetailTypes = new ArrayList<KnowhowDetailType>();
    /** Know-how entry view */
    private static KnowhowEntryTreeViewer knowhowEntryTreeViewer;
    /** Transplant title know- */
    private static String subjectName;
    /** Transplant expertise editor */
    private static MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor;
    /** Initialization processing threads */
    private static CountDownLatch latch;

    /**
     * Constructor.
     * 
     */
    private KnowhowManagement() {
        // no operation
    }

    /**
     * Without validation, it is converted to display data know-how XML file.<br/>
     * Set the display data of the Know-how entry view and heading from the list
     * Know-how XML file.<br/>
     * validation is not performed.
     * 
     * @param filePath
     *            File full path
     * @param isViewCaution
     *            Whether or not to display a warning when it is incorrect data
     *            true:display false:Do not display.
     * @throws JbmException
     *             If file read is failure
     */
    private static void unCheckRead(String filePath, boolean isViewCaution)
            throws JbmException {
        KnowhowManagement.LOGGER.debug(KnowhowManagement.LOGGER_FILEPATH
                + filePath);
        KnowhowManagement.initializeMarshaller();
        if (CmnFileUtil.isFileSize(filePath)) {
            PortabilityKnowhow portabilityKnowhow = PortabilityKnowhowFacade
                    .readFullPath(filePath);
            portabilityKnowhowConverter = PortabilityKnowhowFacade
                    .getPortabilityKnowhowConverter(portabilityKnowhow);
            portabilityKnowhowConverter.createProtabilityKnowhow();
        } else {
            throw new JbmException(
                    MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.ERROR_READ_KNOWHWOW_XML_EMPTY));
        }
    }

    /**
     * Without validation, and then convert the know-how XML data.<br/>
     * Without validation, convert to Know-how XML data the Know-how entry view
     * information and Know-how editor.<br/>
     * 
     * @param filePath
     *            File location path
     * @param inputKnowhoEntry
     *            Registration information know-how
     * @param inputChapterEntry
     *            Heading list information
     * @throws JbmException
     *             If it fails the file write
     */
    public static void unCheckWrite(String filePath,
            List<PortabilityKnowhowListViewOperation> inputKnowhoEntry,
            List<EntryOperator> inputChapterEntry) throws JbmException {
        KnowhowManagement.LOGGER.debug(KnowhowManagement.LOGGER_FILEPATH
                + filePath + "[inputKnowhoEntry]" + inputKnowhoEntry
                + "[inputChapterEntry]" + inputChapterEntry);

        KnowhowManagement.initializeMarshaller();
        entryToKnowhowXmlConvert = new EntryToKnowhowXmlConvert();
        PortabilityKnowhow portabilityKnowhow = KnowhowManagement
                .getEntryToKnowhowXmlConvert().convertKnowhowEntry(
                        KnowhowManagement.subjectName, inputKnowhoEntry,
                        inputChapterEntry, knowhowDetailTypes);
        PortabilityKnowhowFacade.write(filePath, portabilityKnowhow);
        PluginUtil.refreshFile(filePath, null);
    }

    /**
     * Write to a temporary file DocBook data.<br/>
     * 
     * @param filePath
     *            Know how attachment path
     * @param knowhowDetailType
     *            Know how information
     * @throws JbmException
     *             Jbm exception
     */
    public static void docBookWrite(String filePath,
            KnowhowDetailType knowhowDetailType) throws JbmException {
        KnowhowManagement.LOGGER.debug(KnowhowManagement.LOGGER_FILEPATH
                + filePath + "[knowhowDetailType]" + knowhowDetailType);

        KnowhowManagement.initializeMarshaller();
        if (knowhowDetailType != null) {
            DocBookFacade.write(filePath, knowhowDetailType.getDocbookdata());
        }
    }

    /**
     * Convert the data in the Know-how GUI.<br/>
     * Convert the data in the GUI know-how for the know-how XML loaded.<br/>
     * 
     */
    private static void convertKnowhowXmlFile() {
        // Generation of data conversion
        KnowhowManagement.convertKnonwhowDataList = new CommandKnowhowDataList(
                KnowhowManagement.portabilityKnowhowConverter);
        KnowhowManagement.convertKnonwhowDataList.convert();
        KnowhowManagement
                .setSubjectName(KnowhowManagement.convertKnonwhowDataList
                        .getPortabilityKnowhowTitle());
        KnowhowManagement.knowhowDetailTypes
                .addAll(KnowhowManagement.convertKnonwhowDataList
                        .getKnowhowDetailTypes());
    }

    /**
     * Convert to the View display data for each registration data.<br/>
     * Convert to the View display data each item information that is registered
     * in the wizard.<br/>
     * 
     * @param knowhowPage
     *            AbstractCreateKnowhowPage
     * @param keyValue
     *            Id
     * @return AbstractSampleViewData
     */
    public static AbstractViewType registerPageToEntryView(
            AbstractCreateKnowhowPage knowhowPage, String keyValue) {
        KnowhowManagement.LOGGER.debug("[knowhowPage]" + knowhowPage
                + "[keyValue]" + keyValue);
        return KnowhowManagement.getRegisterPageToSampleViewData(knowhowPage,
                keyValue);
    }

    /**
     * Get the data in the View display from know-how XML.<br/>
     * 
     * @return Heading Display Data list
     */
    public static List<PortabilityKnowhowListViewOperation> getEntryViewConvetData() {
        KnowhowManagement.LOGGER.debug(CmnStringUtil.EMPTY);
        return convertKnonwhowDataList.getKnowhowListViewOperations();
    }

    /**
     * Get the data of heading display from XML know-how.<br/>
     * 
     * @return Heading Display Data list
     */
    public static List<EntryOperator> getEntryOperatorConvetData() {
        KnowhowManagement.LOGGER.debug(CmnStringUtil.EMPTY);
        if (convertKnonwhowDataList != null) {
            return convertKnonwhowDataList.getEntryOperators();
        }
        return new ArrayList<EntryOperator>();
    }

    /**
     * Initialize the data of this class.<br/>
     * 
     */
    public static void initializationConvetData() {
        KnowhowManagement.LOGGER.debug(CmnStringUtil.EMPTY);
        KnowhowManagement.portabilityKnowhowConverter = null;
        KnowhowManagement.convertKnonwhowDataList = null;
        KnowhowManagement.knowhowDetailTypes.clear();

    }

    /**
     * get the more know-how information.<br/>
     * 
     * @return Know how information list
     */
    public static List<KnowhowDetailType> getKnowhowDetailTypes() {
        KnowhowManagement.LOGGER.debug(CmnStringUtil.EMPTY);
        return knowhowDetailTypes;
    }

    /**
     * Reload the know-how XML file.<br/>
     * Reread the know-how XML file. When re-read of know-how XML files, please
     * Call this method.<br/>
     * 
     * @throws JbmException
     *             If know-how XML file read failure
     */
    public static void refresh() throws JbmException {
        KnowhowManagement.LOGGER.debug(CmnStringUtil.EMPTY);
        // Reload processing
        KnowhowManagement.unCheckRead(
                FileManagement.getPortabilityKnowhowFilePath(), false);
        KnowhowManagement.convertKnowhowXmlFile();
        
    }

    /**
     * Get a converter that converts know-how XML to display data.<br/>
     * 
     * @return EntryToKnowhowXmlConvert
     */
    private static EntryToKnowhowXmlConvert getEntryToKnowhowXmlConvert() {
        KnowhowManagement.LOGGER.debug(CmnStringUtil.EMPTY);
        return entryToKnowhowXmlConvert;
    }

    /**
     * A factory method of the view data model.<br/>
     * get the view data model of your choice.<br/>
     * 
     * @param knowhowPage
     *            AbstractCreateKnowhowPage
     * @param keyValue
     *            Item ID
     * @return AbstractSampleViewData
     */
    private static AbstractViewType getRegisterPageToSampleViewData(
            AbstractCreateKnowhowPage knowhowPage, String keyValue) {
        if (knowhowPage instanceof CreateCategoryPage) {
            return new CategoryViewType(knowhowPage, keyValue);
        } else if (knowhowPage instanceof CreateKnowhowPage) {
            return new KnowhowViewType(knowhowPage, keyValue);
        } else if (knowhowPage instanceof CreateCheckItemPage) {
            return new CheckItemViewType(knowhowPage, keyValue);
        } else if (knowhowPage instanceof CreateSearchInfoPage) {
            return new SearchInfoViewType(knowhowPage, keyValue);
        }
        return null;
    }

    /**
     * Overwrite the know-how detailed data.<br/>
     * Overwrite the know-how detailed data in the information that is in the
     * memory.<br/>
     * 
     * @param knowhowDetailType
     *            know-how detailed data
     */
    private static void overrideKnowhowDetail(
            KnowhowDetailType knowhowDetailType) {

        List<KnowhowDetailType> newDetailTypes = new ArrayList<KnowhowDetailType>();
        for (KnowhowDetailType detailType : KnowhowManagement.knowhowDetailTypes) {
            if (knowhowDetailType.getKnowhowDetailId().equals(
                    detailType.getKnowhowDetailId())) {
                newDetailTypes.add(knowhowDetailType);
            } else {
                newDetailTypes.add(detailType);
            }
        }
        KnowhowManagement.knowhowDetailTypes.clear();
        KnowhowManagement.knowhowDetailTypes.addAll(newDetailTypes);
    }

    /**
     * Determine if know-how has been registered already.<br/>
     * Return true if the same ID is present in the data that is deployed in a
     * memory.<br/>
     * Otherwise false.
     * 
     * @param knowhowDetailType
     *            Know how data
     * @return true:Exist false:Not exist
     */
    public static boolean isExistKnowhowDetail(
            KnowhowDetailType knowhowDetailType) {
        KnowhowManagement.LOGGER.debug("[knowhowDetailType]"
                + knowhowDetailType);
        for (KnowhowDetailType detailType : KnowhowManagement.knowhowDetailTypes) {
            if (knowhowDetailType.getKnowhowDetailId().equals(
                    detailType.getKnowhowDetailId())) {
                return true;
            }
        }
        return false;
    }

    /**
     * Get the know-how more information.<br/>
     * 
     * @param knowhowDetailRefKey
     *            Know how Detail reference key
     * @return KnowhowDetailType Know how Detail information
     */
    public static KnowhowDetailType getKnowhowDetailInfo(
            String knowhowDetailRefKey) {
        KnowhowManagement.LOGGER.debug("[knowhowDetailRefKey]"
                + knowhowDetailRefKey);

        for (KnowhowDetailType knowhowDetailType : KnowhowManagement
                .getKnowhowDetailTypes()) {
            if (knowhowDetailRefKey != null) {
                if (knowhowDetailRefKey.equals(knowhowDetailType
                        .getKnowhowDetailId())) {
                    return knowhowDetailType;
                }
            }
        }
        return null;
    }

    /**
     * Generate a template Know how file data.<br/>
     * 
     * @param knowhowDetailId
     *            Know how Detail ID
     * @param knowhowSubjectName
     *            Know-how subject name
     * 
     * @return KnowhowDetailType
     */
    public static KnowhowDetailType createTemplateKnowhowDetail(
            String knowhowDetailId, String knowhowSubjectName) {

        KnowhowManagement.LOGGER.debug("[knowhowDetailId]" + knowhowDetailId
                + "[knowhowSubjectName]" + knowhowSubjectName);

        KnowhowDetailType knowhowDetailType = new KnowhowDetailType();

        // create Article
        Article article = new Article();

        // create Info
        Info info = new Info();
        Title title = new Title();
        title.getContent().add(knowhowSubjectName);
        info.getTitlesAndTitleabbrevsAndSubtitles().add(title);
        article.setInfo(info);

        // create Section
        Section section = new Section();
        section.getTitlesAndTitleabbrevsAndSubtitles().add(title);
        Para para = new Para();
        para.getContent().add(ResourceUtil.temprateDocBookStr);
        section.getItemizedlistsAndOrderedlistsAndProcedures().add(para);
        List<Object> glossariesAndBibliographiesAndIndices = article
                .getGlossariesAndBibliographiesAndIndices();
        glossariesAndBibliographiesAndIndices.add(section);
        knowhowDetailType.setKnowhowDetailId(knowhowDetailId);
        knowhowDetailType.setDocbookdata(article);
        KnowhowManagement.addKnowhowDetail(knowhowDetailType);
        return knowhowDetailType;
    }

    /**
     * Get the template tag information.<br/>
     * 
     * @param templateFilePath
     *            Template file path
     * @param indentation
     *            Indentation
     * @return Template tag information
     * @throws JbmException
     *             Jbm exception
     */
    public static String getTemplateTag(String templateFilePath,
            String indentation) throws JbmException {
        KnowhowManagement.LOGGER.debug("[tmplateFilePath]" + templateFilePath
                + "[indentationSpace]" + indentation);

        return FileUtil.createFileContentStr(templateFilePath, indentation);
    }

    /**
     * Gets the data that was replaced to a string that you specify in the
     * template tag information.<br/>
     * Get the template string that is replaced with the string in any position
     * from template file path.<br/>
     * 
     * @param templateFilePath
     *            Template file path
     * @param indentation
     *            Indentation
     * @param replacementStr
     *            Replacement string
     * @return Template tag Information
     * @throws JbmException
     *             Jbm exception
     */
    public static String getReplacementTemplateTag(String templateFilePath,
            String indentation, String replacementStr) throws JbmException {
        KnowhowManagement.LOGGER.debug("[tmplateFilePath]" + templateFilePath
                + "[indentationSpace]" + indentation + "[replacementStr]"
                + replacementStr);

        return FileUtil.createFileContentReplacementStr(templateFilePath,
                indentation, replacementStr);
    }

    /**
     * Read the know-how detailed data being edited.<br/>
     * 
     * @param knowhowDetailTempFile
     *            Know how template file
     * @return Article object
     * @throws JbmException
     *             Jbm exception
     */
    public static Article readEditingDocBook(String knowhowDetailTempFile)
            throws JbmException {
        KnowhowManagement.LOGGER.debug("[knowhowDetailTempFile]"
                + knowhowDetailTempFile);

        KnowhowManagement.initializeMarshaller();
        return DocBookFacade.readFullPath(knowhowDetailTempFile);
    }

    /**
     * Add more know-how.<br/>
     * Overwritten if it is already registered, additional for new.<br/>
     * 
     * @param knowhowDetailType
     *            KnowhowDetailType
     */
    public static void addKnowhowDetail(KnowhowDetailType knowhowDetailType) {
        KnowhowManagement.LOGGER.debug("[knowhowDetailType]"
                + knowhowDetailType);

        if (!KnowhowManagement.isExistKnowhowDetail(knowhowDetailType)) {
            KnowhowManagement.knowhowDetailTypes.add(knowhowDetailType);
        } else {
            KnowhowManagement.overrideKnowhowDetail(knowhowDetailType);
        }

    }

    /**
     * Determine whether Section tags are nested.<br/>
     * Determine Section tags are present in Section tag under.<br/>
     * 
     * @param article
     *            Article tags
     * @return ture:Nestedfalse:Not nested
     */
    public static boolean isNestSection(Article article) {
        KnowhowManagement.LOGGER.debug("[article]" + article);
        for (Object docBookTag : article
                .getGlossariesAndBibliographiesAndIndices()) {
            if (docBookTag instanceof Section) {
                Section section = (Section) docBookTag;
                if (!section.getSections().isEmpty()) {
                    return true;
                }
            }
        }
        return false;
    }

    /**
     * Do the initial processing of JAXB.<br/>
     * 
     */
    private static void init() {
        KnowhowManagement.LOGGER.debug(CmnStringUtil.EMPTY);
        JaxbUtil.init();
        FileManagement
                .createSearchModuleMap(PluginUtil.getRealPluginDirPath()
                        + ApplicationPropertiesUtil
                                .getProperty(ApplicationPropertiesUtil.SEARCH_MODULE_XML_PATH));
    }

    /**
     * Get knowhowEntryTreeViewer.<br/>
     * 
     * @return knowhowEntryTreeViewer
     */
    public static KnowhowEntryTreeViewer getKnowhowEntryTreeViewer() {
        KnowhowManagement.LOGGER.debug(CmnStringUtil.EMPTY);
        return knowhowEntryTreeViewer;
    }

    /**
     * Set the knowhowEntryTreeViewer.<br/>
     * 
     * @param knowhowEntryTreeViewer
     *            knowhowEntryTreeViewer
     */
    public static void setKnowhowEntryTreeViewer(
            KnowhowEntryTreeViewer knowhowEntryTreeViewer) {
        KnowhowManagement.LOGGER.debug("[knowhowEntryTreeViewer]"
                + knowhowEntryTreeViewer);
        KnowhowManagement.knowhowEntryTreeViewer = knowhowEntryTreeViewer;
    }

    /**
     * Set the Know-how editor.<br/>
     * 
     * @param knowhowMultiPageEditor
     *            Know-how editor
     */
    public static void setKnowhowEditor(
            MaintenanceKnowhowMultiPageEditor knowhowMultiPageEditor) {
        KnowhowManagement.LOGGER.debug("[knowhowMultiPageEditor]"
                + knowhowMultiPageEditor);
        KnowhowManagement.knowhowMultiPageEditor = knowhowMultiPageEditor;
    }

    /**
     * Get the know-how editor<br/>
     * 
     * @return know-how editor
     */
    public static MaintenanceKnowhowMultiPageEditor getKnowhowEditor() {
        KnowhowManagement.LOGGER.debug(CmnStringUtil.EMPTY);
        return knowhowMultiPageEditor;
    }

    /**
     * Get subjectName.<br/>
     * 
     * @return subjectName
     */
    public static String getSubjectName() {
        KnowhowManagement.LOGGER.debug(CmnStringUtil.EMPTY);
        return subjectName;
    }

    /**
     * Set the subjectName.<br/>
     * 
     * @param subjectName
     *            subjectName
     */
    public static void setSubjectName(String subjectName) {
        KnowhowManagement.LOGGER.debug("[subjectName]" + subjectName);
        KnowhowManagement.subjectName = subjectName;
    }

    /**
     * Get CountDownLatch.<br/>
     * 
     * @return latch
     */
    private static CountDownLatch getLatch() {
        return KnowhowManagement.latch;
    }

    /**
     * Do the initial processing.<br/>
     * Processing is performed to generate each marshaler.<br/>
     * 
     */
    public synchronized static void initializeMarshaller() {
        KnowhowManagement.LOGGER.debug(CmnStringUtil.EMPTY);
        if (KnowhowManagement.latch == null) {
            // Only for the initialization process, the argument set to 1
            // processing
            KnowhowManagement.latch = new CountDownLatch(1);
            Job job = new InitializeMarshallerJob(
                    ResourceUtil.initializeKnowhow);
            job.setUser(true);
            job.schedule();
            // Develop and execute a thread
            new Thread(new Runnable() {
                /**
                 * {@inheritDoc}
                 */
                @Override
                public void run() {
                    KnowhowManagement.LOGGER.info(MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.LOG_INITIALIZE_MARSHAKKER_START));
                    // Background processing
                    KnowhowManagement.init();
                    // Countdown initialization process is complete
                    KnowhowManagement.getLatch().countDown();
                    KnowhowManagement.LOGGER.info(MessagePropertiesUtil
                            .getMessage(MessagePropertiesUtil.LOG_INITIALIZE_MARSHAKKER_STOP));
                }
            }).start();
        } else if (KnowhowManagement.latch.getCount() != 0) {
            inistalizeAwait();
        }
    }

    /**
     * Wait for the completion of the initialization process.<br/>
     * Wait for the generation process completion of each marshaler.<br/>
     * 
     */
    public static void inistalizeAwait() {
        KnowhowManagement.LOGGER.debug(CmnStringUtil.EMPTY);
        try {
            KnowhowManagement.latch.await();
        } catch (InterruptedException e) {
            JbmException
                    .outputExceptionLog(
                            LOGGER,
                            e,
                            MessagePropertiesUtil
                                    .getMessage(MessagePropertiesUtil.FAIL_INITIALIZE_PORTABILITY_KNOWHOW));
        }
    }
}
