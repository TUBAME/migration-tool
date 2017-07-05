/*
 * FileManagement.java
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

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.URL;
import java.util.Map;

import org.eclipse.ui.IViewPart;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.common.logic.converter.CmnDocBookConverter;
import tubame.common.util.CmnFileUtil;
import tubame.common.util.CmnStringUtil;
import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.logic.SearchModuleFacade;
import tubame.knowhow.biz.logic.converter.AsciiDocConverter;
import tubame.knowhow.biz.logic.converter.SearchModuleConverter;
import tubame.knowhow.biz.model.generated.python.PortabilitySearchModule;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.ui.view.ViewRefresh;
import tubame.knowhow.util.PluginUtil;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Class for managing files.<br/>
 * manage the various files used in the XML know-how created.<br/>
 * 
 */
public final class FileManagement {
	/**
	 * define the type of the XML.<br/>
	 * 
	 */
	public static enum XML_TYPE {
		// XML know-how
		KNOWHOW_XML,
		// DocBook format
		DOCBOOK_XML;
	}

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileManagement.class);
	/** Know-how XML file path */
	private static String portabilityKnowhowFilePath;
	/** Search module information map */
	private static Map<String, String> searchModuleMap;
	/** File DocBookHtml one o'clock */
	private static String docBookHtmlTempFilePath;
	/** File KnowhowHtml one o'clock */
	private static String knowhowHtmlTempFilePath;

	private static final String TEMP1_FOR_ASCIIDOC = "temp1_docbook.xml";

	private static final String TEMP2_FOR_ASCIIDOC = "temp2_docbook.xml";

	private static final String TEMP3_FOR_ASCIIDOC = "temp3_docbook.adoc";

	/**
	 * Constructor.<br/>
	 * 
	 */
	private FileManagement() {
		// no operation/
	}

	/**
	 * Gget the know-how XML full path.<br/>
	 * 
	 * @return XML know-how full path
	 */
	public static String getPortabilityKnowhowFilePath() {
		FileManagement.LOGGER.debug(CmnStringUtil.EMPTY);
		return portabilityKnowhowFilePath;
	}

	/**
	 * Set the full path XML know-how.<br/>
	 * 
	 * @param portabilityKnowhowFilePath
	 *            XML know-how full path
	 */
	public static void setPortabilityKnowhowFilePath(String portabilityKnowhowFilePath) {
		FileManagement.LOGGER.debug("[portabilityKnowhowFilePath]" + portabilityKnowhowFilePath);
		FileManagement.portabilityKnowhowFilePath = portabilityKnowhowFilePath;
	}

	/**
	 * Do a refresh of know-how entry view.<br/>
	 * After that is read various definition file, refresh the know-how entry
	 * view.<br/>
	 * 
	 * @throws JbmException
	 *             JBM exception
	 * 
	 */
	public static void refresh() throws JbmException {
		FileManagement.LOGGER.debug(CmnStringUtil.EMPTY);
		// View redraw
		ViewRefresh refresh = FileManagement.createRefreshList();
		refresh.refresh();
		KnowhowManagement.refresh();
	}

	/**
	 * Get the list view to be refreshed.<br/>
	 * Get a class list that implements the @see ViewRefresh.<br/>
	 * 
	 * @return List view to refresh
	 * @throws JbmException
	 *             If View acquisition failure
	 */
	private static ViewRefresh createRefreshList() throws JbmException {
		FileManagement.LOGGER.debug(CmnStringUtil.EMPTY);
		return FileManagement.getViewRefresh(PluginUtil.getKnowhowEntryView(),
				MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FAIL_XML_READER));
	}

	/**
	 * get the View of refreshed.
	 * 
	 * @param iViewPart
	 *            View
	 * @param key
	 *            Key message of error
	 * @return ViewRefresh
	 * @throws JbmException
	 *             If this is not View of ViewRefresh
	 */
	private static ViewRefresh getViewRefresh(IViewPart iViewPart, String key) throws JbmException {
		if (!(iViewPart instanceof ViewRefresh)) {
			throw new JbmException(MessagePropertiesUtil.getMessage(key));
		}
		return (ViewRefresh) iViewPart;
	}

	/**
	 * Create a search module information.<br/>
	 * create a search module information. It does not generate if the search
	 * module information already exists.<br/>
	 * 
	 * @param searchModuleFullPath
	 *            Search module file full path
	 */
	public static void createSearchModuleMap(String searchModuleFullPath) {
		FileManagement.LOGGER.debug("[searchModuleFullPath]" + searchModuleFullPath);
		if (FileManagement.searchModuleMap == null) {
			try {
				PortabilitySearchModule portabilitySearchModule = SearchModuleFacade.readFullPath(searchModuleFullPath);
				SearchModuleConverter searchModuleConverter = SearchModuleFacade.getSearchModuleConverter();
				searchModuleConverter.convert(portabilitySearchModule);
				FileManagement.searchModuleMap = searchModuleConverter.getSearchModuleMap();
			} catch (JbmException e) {
				JbmException.outputExceptionLog(LOGGER, e,
						MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FAIL_READ_SEARCH_MODULE));
			}
		}
	}

	/**
	 * Get searchModuleMap.<br/>
	 * 
	 * @return searchModuleMap
	 */
	public static Map<String, String> getSearchModuleMap() {
		return FileManagement.searchModuleMap;
	}

	/**
	 * Create an HTML file.<br/>
	 * After changing to the InputStream object file path specified, and passes
	 * it to the conversion logic.<br/>
	 * 
	 * @param orgFilePath
	 *            Target file path
	 * @param outputFilePath
	 *            Destination file path
	 * @param xmlType
	 *            Type of XML
	 * @return Conversion result
	 */
	public static int outputHtml(String orgFilePath, String outputFilePath, XML_TYPE xmlType) {
		FileManagement.LOGGER
				.debug("[orgFilePath]" + orgFilePath + "[outputFilePath]" + outputFilePath + "[xmlType]" + xmlType);
		// Get the default XSL path of resource and xslt file path
		String xsltFilePath = ResourceUtil.knowhowXslPath;
		String defaultXsltPath = ResourceUtil.defaultKnowhowXslPath;
		if (!XML_TYPE.KNOWHOW_XML.equals(xmlType)) {
			xsltFilePath = ResourceUtil.docBookXslPath;
			defaultXsltPath = ResourceUtil.defaultDocBookXslPath;
		}

		String xslPath = null;
		InputStream xslInputStream = null;
		URL xslUrl = null;
		// Check XSL files that are specified in the xslt file path for the
		// existence
		if (CmnFileUtil.fileExists(xsltFilePath)) {
			// If present, to specify the XSL file path
			xslPath = xsltFilePath;
		} else {
			// If it does not exist, specify a default XSL of resource
			ClassLoader cl = FileManagement.class.getClassLoader();
			xslInputStream = cl.getResourceAsStream(defaultXsltPath);
			xslUrl = cl.getResource(defaultXsltPath);
		}
		return CmnDocBookConverter.convertHtml(CmnFileUtil.getInputStream(orgFilePath), outputFilePath, xslPath,
				xslInputStream, xslUrl);
	}

	public static int outputDocBookXml(String orgFilePath, String outputFilePath) {
		// If it does not exist, specify a default XSL of resource
		ClassLoader cl = FileManagement.class.getClassLoader();
		InputStream xslInputStream = cl.getResourceAsStream(ResourceUtil.defaultKnowhowToDocBookXslPath);
		URL xslUrl = cl.getResource(ResourceUtil.defaultKnowhowToDocBookXslPath);
		return CmnDocBookConverter.convertHtml(CmnFileUtil.getInputStream(orgFilePath), outputFilePath, null,
				xslInputStream, xslUrl);

	}


	public static void trimLineBreak(File in, File out) throws IOException {
		BufferedReader bufferedReader = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			bufferedReader = new BufferedReader(new InputStreamReader(new FileInputStream(in), "UTF-8"));
			String s = null;
			bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(out));

			int lineBreakCount = 0;
			while ((s = bufferedReader.readLine()) != null) {
				if (s.equals("")) {
					lineBreakCount++;
				} else {
					if (lineBreakCount == 1 | lineBreakCount >= 2) {
						bufferedOutputStream.write(("\n" + s + "\n").getBytes("UTF-8"));
						lineBreakCount = 0;
					} else {
						bufferedOutputStream.write((s + "\n").getBytes("UTF-8"));
					}
				}
			}
		} catch (IOException e) {
			throw e;
		} finally {
			if (bufferedReader != null) {
				bufferedReader.close();
			}
			if (bufferedOutputStream != null) {
				bufferedOutputStream.flush();
				bufferedOutputStream.close();
			}
		}
	}

	public static int removePrefix(String orgFilePath, String outputFilePath) {
		// If it does not exist, specify a default XSL of resource
		ClassLoader cl = FileManagement.class.getClassLoader();
		InputStream xslInputStream = cl.getResourceAsStream(ResourceUtil.defaultKnowhowRemovePrefixXslPath);
		URL xslUrl = cl.getResource(ResourceUtil.defaultKnowhowRemovePrefixXslPath);
		return CmnDocBookConverter.convertHtml(CmnFileUtil.getInputStream(orgFilePath), outputFilePath, null,
				xslInputStream, xslUrl);

	}

	public static void removeTempFileForAsciiDoc(String outputFilePath) {
		File file = new File(outputFilePath);
		String temp1 = file.getParent() + File.separator + TEMP1_FOR_ASCIIDOC;
		File tempFile = new File(temp1);
		if (tempFile.exists()) {
			tempFile.delete();
		}

		String temp2 = file.getParent() + File.separator + TEMP2_FOR_ASCIIDOC;
		tempFile = new File(temp2);
		if (tempFile.exists()) {
			tempFile.delete();
		}
		
		String temp3 = file.getParent() + File.separator + TEMP3_FOR_ASCIIDOC;
		tempFile = new File(temp3);
		if (tempFile.exists()) {
			tempFile.delete();
		}
	}

	public static void saveAsciiDoc(String orgFilePath, String outputFilePath,InputStream xslAsciiDoc) throws Exception {
		// step1 convert docbook
		File file = new File(outputFilePath);
		String temp1 = file.getParent() + File.separator + TEMP1_FOR_ASCIIDOC;
		outputDocBookXml(orgFilePath, temp1);

		// step2 remove prefix
		String temp2 = file.getParent() + File.separator + TEMP2_FOR_ASCIIDOC;
		removePrefix(temp1, temp2);

		// step3 convert asciidoc
		String temp3 = file.getParent() + File.separator + TEMP3_FOR_ASCIIDOC;
		AsciiDocConverter.convert(temp2, temp3,xslAsciiDoc);

		// step4 trim line break
		trimLineBreak(new File(temp3), file);

	}

	/**
	 * Create an XML file.<br/>
	 * Used to output the file specified in the source file path that you
	 * specify.<br/>
	 * 
	 * @param fromPath
	 *            Source file path
	 * @param toPath
	 *            Destination file path
	 */
	public static void outputXML(String fromPath, String toPath) {
		FileManagement.LOGGER.debug("[fromPath]" + fromPath + "[toPath]" + toPath);
		CmnFileUtil.copyFile(fromPath, toPath);
	}

	/**
	 * Get docBookHtmlTempFilePath.<br/>
	 * 
	 * @return docBookHtmlTempFilePath
	 */
	public static String getDocBookHtmlTempFilePath() {
		return docBookHtmlTempFilePath;
	}

	/**
	 * Set the docBookHtmlTempFilePath.<br/>
	 * 
	 * @param docBookHtmlTempFilePath
	 *            docBookHtmlTempFilePath
	 */
	public static void setDocBookHtmlTempFilePath(String docBookHtmlTempFilePath) {
		FileManagement.LOGGER.debug("[docBookHtmlTempFilePath]" + docBookHtmlTempFilePath);
		FileManagement.docBookHtmlTempFilePath = docBookHtmlTempFilePath;
	}

	/**
	 * Get knowhowHtmlTempFilePath.<br/>
	 * 
	 * @return knowhowHtmlTempFilePath
	 */
	public static String getKnowhowHtmlTempFilePath() {
		return knowhowHtmlTempFilePath;
	}

	/**
	 * Set the knowhowHtmlTempFilePath.<br/>
	 * 
	 * @param knowhowHtmlTempFilePath
	 *            knowhowHtmlTempFilePath
	 */
	public static void setKnowhowHtmlTempFilePath(String knowhowHtmlTempFilePath) {
		FileManagement.LOGGER.debug("[knowhowHtmlTempFilePath]" + knowhowHtmlTempFilePath);
		FileManagement.knowhowHtmlTempFilePath = knowhowHtmlTempFilePath;
	}
}
