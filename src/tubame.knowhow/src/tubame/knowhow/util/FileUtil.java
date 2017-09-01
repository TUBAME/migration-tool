/*
 * FileUtil.java
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
package tubame.knowhow.util;

import java.io.BufferedOutputStream;
import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.io.OutputStreamWriter;
import java.io.Reader;
import java.io.UnsupportedEncodingException;
import java.io.Writer;
import java.net.MalformedURLException;
import java.net.URL;

import javax.xml.bind.Element;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.XPath;
import javax.xml.xpath.XPathConstants;
import javax.xml.xpath.XPathExpressionException;
import javax.xml.xpath.XPathFactory;

import tubame.common.util.CmnFileUtil;
import tubame.common.util.CmnStringUtil;

import org.eclipse.core.resources.IContainer;
import org.eclipse.core.runtime.Path;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import tubame.knowhow.biz.exception.JbmException;
import tubame.knowhow.biz.util.resource.MessagePropertiesUtil;
import tubame.knowhow.plugin.logic.FileManagement;
import tubame.knowhow.util.resource.ResourceUtil;

/**
 * Utility classes for the File (Directory) operation.<br/>
 * Conducted acquisition of file separator or File system-specific
 * attributes.<br/>
 */
public final class FileUtil {

	/** Logger */
	private static final Logger LOGGER = LoggerFactory.getLogger(FileUtil.class);

	/**
	 * Default constructor.<br/>
	 * Disable the instance generation from other class.<br/>
	 * 
	 */
	private FileUtil() {
		// no operation
	}

	/**
	 * Create a Work directory of know-how detail editor for editing.<br/>
	 * 
	 * @param container
	 *            Container
	 * @param target
	 *            File Path
	 * @return true:Directory creation false:Not directory
	 * @throws IOException
	 *             IO exception
	 */
	public static boolean createWorkDir(IContainer container, String target) throws IOException {
		if (!CmnStringUtil.isEmpty(target)) {
			Path path = new Path(target);
			File targetFile = new File(
					container.getLocationURI().getPath() + CmnFileUtil.FILE_SEPARATOR + path.toOSString());
			if (targetFile.exists()) {
				return false;
			} else {
				return targetFile.mkdir();
			}
		}
		return false;
	}

	public static void copyFile(File in, File out) throws IOException {
		BufferedReader bufferedReader = null;
		BufferedOutputStream bufferedOutputStream = null;
		try {
			bufferedReader = new BufferedReader(
					new InputStreamReader(new FileInputStream(in), ResourceUtil.textdataReadEncode));
			String s = null;
			bufferedOutputStream = new BufferedOutputStream(new FileOutputStream(out));
			while ((s = bufferedReader.readLine()) != null) {
				bufferedOutputStream.write((s + "\n").getBytes(ResourceUtil.textdataWriteEncode));
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

	public static void copy(File in, File out, String enc) throws Exception {
		InputStreamReader isr = null;
		Reader reader = null;
		OutputStreamWriter osw = null;
		Writer writer = null;
		FileInputStream fileInputStream = null;
		int ch;
		try {
			fileInputStream = new FileInputStream(in);
			isr = new InputStreamReader(fileInputStream, enc);
			reader = new BufferedReader(isr);
			osw = new OutputStreamWriter(new FileOutputStream(out), enc);
			writer = new BufferedWriter(osw);

			while ((ch = reader.read()) > -1) {
				writer.write(ch);
			}
		} catch (Exception e) {
			throw e;
		} finally {

			try {
				reader.close();
				isr.close();
				fileInputStream.close();
			} catch (IOException e) {
				;
			}
			if (writer != null) {
				try {
					writer.close();
				} catch (IOException e) {
					;
				}
			}
		}

	}

	/**
	 * Generate a template file information.<br/>
	 * 
	 * @param tmplateFilePath
	 *            Template file path
	 * @param indentationSpace
	 *            Indent space
	 * @return Template string
	 * @throws JbmException
	 *             Jbm exception
	 */
	public static String createFileContentStr(String tmplateFilePath, String indentationSpace) throws JbmException {
		StringBuilder templateStr = new StringBuilder();
		File file = new File(tmplateFilePath);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String str = null;
			int insertIndentation = 0;
			while ((str = br.readLine()) != null) {
				if (insertIndentation != 0) {
					templateStr.append(CmnStringUtil.newLine(indentationSpace));
				}
				templateStr.append(str);
				insertIndentation++;
			}
		} catch (FileNotFoundException e) {
			throw new JbmException(e.getMessage(), e);
		} catch (IOException e) {
			throw new JbmException(e.getMessage(), e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return templateStr.toString();
	}

	/**
	 * Generate a template file information that was replaced by the
	 * string.<br/>
	 * 
	 * @param tmplateFilePath
	 *            Template file path
	 * @param indentationSpace
	 *            Indent space
	 * @param replacementStr
	 *            Replacement string
	 * @return Template string
	 * @throws JbmException
	 *             Jbm exception
	 */
	public static String createFileContentReplacementStr(String tmplateFilePath, String indentationSpace,
			String replacementStr) throws JbmException {
		StringBuilder templateStr = new StringBuilder();
		File file = new File(tmplateFilePath);
		BufferedReader br = null;
		try {
			br = new BufferedReader(new FileReader(file));
			String str = null;
			int insertIndentation = 0;
			while ((str = br.readLine()) != null) {
				if (insertIndentation != 0) {
					templateStr.append(CmnStringUtil.newLine(indentationSpace));
				}
				if (str.equals("[ReplacementStringElement]")) {
					String reStr = str.replace(str, replacementStr);
					templateStr.append(reStr);
				} else {
					templateStr.append(str);
				}
				insertIndentation++;
			}
		} catch (FileNotFoundException e) {
			throw new JbmException(e.getMessage(), e);
		} catch (IOException e) {
			throw new JbmException(e.getMessage(), e);
		} finally {
			if (br != null) {
				try {
					br.close();
				} catch (IOException e) {
					e.printStackTrace();
				}
			}
		}
		return templateStr.toString();
	}

	/**
	 * Converted into the URL format file path and returns a string.<br/>
	 * 
	 * @param filePath
	 *            File Path
	 * @return URL
	 */
	public static URL getURL(String filePath) {
		File file = new File(filePath);
		try {
			return file.toURI().toURL();
		} catch (MalformedURLException e) {
			JbmException.outputExceptionLog(LOGGER, e,
					MessagePropertiesUtil.getMessage(MessagePropertiesUtil.FAIL_GET_URL));
		}
		return null;
	}

}
