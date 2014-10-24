/*
 * WaitConvertHtmlProgress.java
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
package tubame.knowhow.plugin.ui.dialog;

import java.io.BufferedInputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.IOException;
import java.io.InputStream;
import java.lang.reflect.InvocationTargetException;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.stream.XMLInputFactory;
import javax.xml.stream.XMLStreamConstants;
import javax.xml.stream.XMLStreamReader;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpression;
import javax.xml.xpath.XPathFactory;

import org.eclipse.core.runtime.IProgressMonitor;
import org.eclipse.jface.operation.IRunnableWithProgress;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tubame.common.logic.converter.CmnDocBookConverter;
import tubame.common.util.CmnFileUtil;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.logic.FileManagement;
import tubame.knowhow.plugin.logic.FileManagement.XML_TYPE;
import tubame.knowhow.util.FileUtil;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Wait progress dialog class when performing the conversion to HTML file.<br/>
 */
public class WaitConvertHtmlProgress implements IRunnableWithProgress {
	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(WaitConvertHtmlProgress.class);
	private static final int CONVERT_RESULT = CmnDocBookConverter.RETCODE_SUCCESS;
	/** Original file */
	private String orgFilePath;
	/** Output destination file */
	private String outputFilePath;
	/** XML type */
	private XML_TYPE xmlType;

	/**
	 * Constructor.<br/>
	 * 
	 * @param orgFilePath
	 *            Original file
	 * @param outputFilePath
	 *            Destination file
	 * @param xmlType
	 *            XML type
	 */
	public WaitConvertHtmlProgress(String orgFilePath, String outputFilePath, XML_TYPE xmlType) {
		this.orgFilePath = orgFilePath;
		this.outputFilePath = outputFilePath;
		this.xmlType = xmlType;
		WaitConvertHtmlProgress.LOGGER.debug("[orgFilePath]" + orgFilePath + "[outputFilePath]" + outputFilePath
				+ "[xmlType]" + xmlType);
	}

	/**
	 * {@inheritDoc}
	 */
	@Override
	public void run(IProgressMonitor monitor) throws InvocationTargetException, InterruptedException {
		WaitConvertHtmlProgress.LOGGER.debug("[monitor]" + monitor);
		WaitConvertHtmlProgress.LOGGER.info(MessagePropertiesUtil
				.getMessage(MessagePropertiesUtil.LOG_START_CONVERT_HTML));

		monitor.beginTask(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.CONVERT_HTML),
				IProgressMonitor.UNKNOWN);

		// ナレッジXMLより、programlisting/textobject/textdataタグのfileref属性で参照しているファイルパスを取得する
		// ただし、ナレッジエントリビューにあるナレッジでなく、Chapterにあるカテゴリに紐づくナレッジの情報のみ抽出
		if(ResourceUtil.isCopyProgramlistingTextDataFile.equals("true")){
			try {
				List<String> refFiles = getRefFilesFromKnowledgeXml(orgFilePath);
				copyToPluginResouce(refFiles,new File(orgFilePath).getParentFile());
			} catch (Exception e) {
				throw new InvocationTargetException(e,
						MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERROR_HTML_CONVERT));
			}
		}

		int convResult = FileManagement.outputHtml(orgFilePath, outputFilePath, xmlType);

		if (convResult == CmnDocBookConverter.RETCODE_ERR_OUTPUT_DIR_NOT_FOUND) {
			JbmException.outputExceptionLog(LOGGER, null,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERROR_SAVE_DOCBOOK));
			// Throw InvocationTargetException as the error handling
			throw new InvocationTargetException(null,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERROR_SAVE_DOCBOOK));
		} else if (convResult != CmnDocBookConverter.RETCODE_WARN_CLOSE_OUTPUT_FILE_FAILED) {
			// Failed to close processing of the output file
			// The process is continued by logs a warning
			LOGGER.warn(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.LOG_WARN_CLOSE_OUTPUT_FILE_FAILED));

		} else if (convResult != CONVERT_RESULT) {
			JbmException.outputExceptionLog(LOGGER, null,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERROR_HTML_CONVERT));
			// Throw InvocationTargetException as the error handling
			throw new InvocationTargetException(null,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.ERROR_HTML_CONVERT));
		}
		if (monitor.isCanceled()) {
			LOGGER.info(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.PRESS_CANCEL_BUTTON));
			throw new InterruptedException(MessagePropertiesUtil.getMessage(MessagePropertiesUtil.PRESS_CANCEL_BUTTON));
		}
		WaitConvertHtmlProgress.LOGGER.info(MessagePropertiesUtil
				.getMessage(MessagePropertiesUtil.LOG_STOP_CONVERT_HTML));
		monitor.done();
	}

	private void copyToPluginResouce(List<String> refFiles, File knowledgeXmlParentFolder) throws Exception {
		File pluginXSLResouceFile = getPluginXSLResoucePath();
		for (String reativeFilePath : refFiles) {
			File refFile = new File(knowledgeXmlParentFolder,reativeFilePath);
			
			File copyTarget = new File(pluginXSLResouceFile,reativeFilePath);
			//ナレッジXMLで参照されているファイルの親ディレクトリがプロジェクト内のナレッジXMLを保持している親ディレクトかどうか確認する(プロジェクト内のカレント以外かどうか確認）
			if(!isParentDirMatchKnowledgeXmlParentFolder(refFile,knowledgeXmlParentFolder)){
				File parentDir = copyTarget.getParentFile();
				if(!parentDir.exists()){
					parentDir.mkdirs();
				}
			}
			LOGGER.debug("copy from:"+refFile.getPath());
			LOGGER.debug("copy dest:"+copyTarget.getPath());
			FileUtil.copyFile(refFile, copyTarget);
		}
		
	}
	
    private boolean isParentDirMatchKnowledgeXmlParentFolder(File targetFile, File knowledgeXmlParentFolder) {
		if(targetFile.getParentFile().getPath().equals(knowledgeXmlParentFolder.getPath())){
			return true;
		}
		return false;
	}

	private File getPluginXSLResoucePath() {
		return new File(PluginUtil.getRealPluginDirPath()+File.separator+"resources"+ File.separator +"xsl");
	}

	private List<String> getRefFilesFromKnowledgeXml(String knowledgeXmlFilePath) throws Exception {
		List<String> knowhowDetailIds = getKnowhowDetailRefKeys(knowledgeXmlFilePath);
		List<String> files = searchXpath(knowhowDetailIds, knowledgeXmlFilePath);
		return files;
	}

	private List<String> searchXpath(List<String> knowhowDetailRefKeys, String knowledgeXmlFilePath) throws Exception {
		
		List<String> list = new ArrayList();
		DocumentBuilder builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
		Document doc = builder.parse(new File(knowledgeXmlFilePath));
		XPath xpath = XPathFactory.newInstance().newXPath();
		
		for (String knowhowDetailId : knowhowDetailRefKeys) {

			//knowhowDetailIdが一致するsectionを取得
			NodeList dockBookNodes = (NodeList) xpath.evaluate(
					"//*[local-name()='DocBookList']/*[local-name()='DocBook'][@articleId='" + knowhowDetailId
							+ "']/*[local-name()='article']/*[local-name()='section']", doc, XPathConstants.NODESET);
			
			for (int i = 0; i < dockBookNodes.getLength(); i++) {
				
				Node item = dockBookNodes.item(i);
				
				NodeList nodeList = (NodeList) xpath.evaluate(".//*[local-name()='textdata']", item,
						XPathConstants.NODESET);
				
				for (int j = 0; j < nodeList.getLength(); j++) {
					Node item2 = nodeList.item(j);
					list.add(item2.getAttributes().getNamedItem("fileref").getNodeValue());
				}
			}
		}
		return list;
	}

	private List<String> getKnowhowDetailRefKeys(String knowledgeXmlFilePath) throws Exception {
		List<String> chapterCategorys = new ArrayList();
		Map<String, String> KnowhowRefKeyMap = new HashMap();
		Map<String, String> knowhowDetailRefKeyMap = new HashMap();
		XMLInputFactory factory = XMLInputFactory.newInstance();
		BufferedInputStream stream = null;
		XMLStreamReader reader = null;

		try {
			stream = new BufferedInputStream(new FileInputStream(knowledgeXmlFilePath));
			reader = factory.createXMLStreamReader(stream);

			String categoryId = null;
			while (reader.hasNext()) {
				reader.next();
				switch (reader.getEventType()) {

				case XMLStreamConstants.START_ELEMENT:
					if (reader.getLocalName().equals("ChapterCategoryRefKey")) {
						// ChapterCategoryRefKey
						reader.next();
						LOGGER.debug("ChapterCategoryRefKey:" + reader.getText());
						chapterCategorys.add(reader.getText());
					} else if (reader.getLocalName().equals("Category")) {
						// Category
						categoryId = reader.getAttributeValue(null, "categoryId");
					} else if (reader.getLocalName().equals("KnowhowRefKey")) {
						// KnowhowRefKey
						if (categoryId != null) {
							reader.next();
							LOGGER.debug("KnowhowRefKey:" + categoryId + "," + reader.getText());
							KnowhowRefKeyMap.put(categoryId, reader.getText());
							categoryId = null;
						}
					} else if (reader.getLocalName().equals("KnowhowInfomation")) {
						// KnowhowInfomation
						String knowhowInfomationId = reader.getAttributeValue(null, "knowhowId");
						String knowhowDetailRefKey = reader.getAttributeValue(null, "knowhowDetailRefKey");
						knowhowDetailRefKeyMap.put(knowhowInfomationId, knowhowDetailRefKey);
					}
					break;
				}
			}
		} catch (Exception ex) {
			throw ex;
		} finally {
			try {
				if (reader != null)
					reader.close();
			} catch (Exception e) {

			}
		}

		List<String> knowhowDetailIds = new ArrayList();
		// ChapterCategoryRefKeyタグの値(カテゴリID）をすべて取得し、knowhowDetailIdを取得する。
		for (String cateId : chapterCategorys) {
			if (KnowhowRefKeyMap.containsKey(cateId)) {
				String knowhowRefKey = KnowhowRefKeyMap.get(cateId);
				String knowhowDetailId = knowhowDetailRefKeyMap.get(knowhowRefKey);
				knowhowDetailIds.add(knowhowDetailId);
			}
		}
		return knowhowDetailIds;
	}
	
	
}
