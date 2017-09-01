/*
 * CmnDocBookConverter.java
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
package tubame.common.logic.converter;

import java.io.BufferedOutputStream;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.net.URL;
import java.util.Collection;
import java.util.Map.Entry;
import java.util.Properties;
import java.util.Set;

import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.stream.StreamResult;
import javax.xml.transform.stream.StreamSource;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import tubame.common.util.resource.CmnResourceUtil;

/**
 * This is a class to convert XML to HTML to the DocBook format.<br/>
 * The format conversion to HTML of an XML DocBook format, output your data to a
 * specified file.<br/>
 */
public class CmnDocBookConverter {

    /**
     * Conversion result code - Successful completion
     */
    public static final int RETCODE_SUCCESS = 0;

    /**
     * Conversion result code - Conversion failure to a specified format
     */
    public static final int RETCODE_ERR_CONVERT_DOCBOOK = 1;

    /**
     * Conversion result code - The destination directory does not exist
     */
    public static final int RETCODE_ERR_OUTPUT_DIR_NOT_FOUND = 2;

    /**
     * Conversion result code - XSL file does not exist
     */
    public static final int RETCODE_ERR_XSL_FILE_NOT_FOUND = 3;

    /**
     * Conversion result code - Style sheet definition fraud
     */
    public static final int RETCODE_ERR_STYLESHEET_INACCURATE = 4;

    /**
     * Conversion result code - Output file close failure
     */
    public static final int RETCODE_WARN_CLOSE_OUTPUT_FILE_FAILED = 10;

    /**
     * Logger
     */
    private static final Logger LOGGER = LoggerFactory
            .getLogger(CmnDocBookConverter.class);

    /**
     * Output format - HTML format
     */
    private static final int OUTPUTTYPE_HTML = 0;

    /**
     * Convert to XML to HTML the DocBook format.<br/>
     * Output to file into HTML format of the XML DocBook format.<br/>
     * Specified in the URL and the input stream, or specified in the file path
     * XSL file.<br/>
     * (If you both are specified, and effective to specify a file path)<br/>
     * 
     * @param inputDocBook
     *            DocBookXML input stream
     * @param outputFilePath
     *            Output file path
     * @param xslFilePath
     *            XSL file path(To assign null to specified in the URL and input
     *            stream)
     * @param inputXsl
     *            XSL file input stream(To assign null to specify a file path)
     * @param xslUrl
     *            XSL file URL(To assign null to specify a file path)
     * @return Result code
     */
    public static int convertHtml(InputStream inputDocBook,
            String outputFilePath, String xslFilePath, InputStream inputXsl,
            URL xslUrl) {
        return convert(inputDocBook, CmnDocBookConverter.OUTPUTTYPE_HTML,
                outputFilePath, xslFilePath, inputXsl, xslUrl,null);
    }
    
    
    public static int convertWithProps(InputStream inputDocBook,
            String outputFilePath, String xslFilePath, InputStream inputXsl,
            URL xslUrl,Properties props) {
        return convert(inputDocBook, CmnDocBookConverter.OUTPUTTYPE_HTML,
                outputFilePath, xslFilePath, inputXsl, xslUrl,props);
    }

    /**
     * Converted to the specified format of the XML DocBook format.<br/>
     * Output to a file by converting to (HTML) format to the specified XML
     * format of DocBook.<br/>
     * Specified in the URL and the input stream, or specified in the file path
     * XSL file.<br/>
     * (If both are specified, and effective to specify a file path)<br/>
     * 
     * @param inputDocBook
     *            DocBookXML input stream
     * @param outputType
     *            Conversion format (HTML)
     * @param outputFilePath
     *            Output file path
     * @param xslFilePath
     *            XSL file path(Substitute the null to specify the URL to the
     *            input stream)
     * @param inputXsl
     *            XSL file input stream(Substitute the null to specify a file
     *            path)
     * @param xslUrl
     *            xslUrl XSL file URL(Substitute the null to specify a file
     *            path)
     * @return Result code
     */
    private static int convert(InputStream inputDocBook, int outputType,
            String outputFilePath, String xslFilePath, InputStream inputXsl,
            URL xslUrl,Properties props) {
        int resultCode = RETCODE_SUCCESS;

        OutputStream output = null;
        try {
            output = new BufferedOutputStream(new FileOutputStream(
                    outputFilePath));
            // XML source
            Source source = null;
            // Result object for storing the results
            Result result = null;

            // Output format for HTML
            if (outputType == OUTPUTTYPE_HTML) {
                // Specifying an XML
                source = new StreamSource(inputDocBook);

                // Generate Result object for storing the results of
                result = new StreamResult(output);
            }

            // Specifying the XSL file
            Source xslSource = null;
            if (xslFilePath != null) {
                try {
                    xslSource = new StreamSource(new FileInputStream(
                            xslFilePath));

                } catch (FileNotFoundException e) {
                    // XSL file does not exist
                    CmnDocBookConverter.LOGGER
                            .error(CmnResourceUtil
                                    .getMessage(CmnResourceUtil.LOG_ERR_XSL_FILE_NOT_FOUND),
                                    e);
                    return RETCODE_ERR_XSL_FILE_NOT_FOUND;

                }
            } else {
                xslSource = new StreamSource(inputXsl);
                xslSource.setSystemId(xslUrl.toExternalForm());
            }

            // Generation for conversion object
            TransformerFactory factory = TransformerFactory.newInstance();
            Transformer transformer = factory.newTransformer(xslSource);
            if(props !=null){
            	Set<Entry<Object, Object>> entrySet = props.entrySet();
            	for (Entry<Object, Object> entry : entrySet) {
            		transformer.setParameter((String)entry.getKey(), entry.getValue());
				}
            }
            // Perform the conversion
            transformer.transform(source, result);

        } catch (FileNotFoundException e) {
            // Destination directory does not exist
            CmnDocBookConverter.LOGGER.error(CmnResourceUtil
                    .getMessage(CmnResourceUtil.LOG_ERR_OUTPUT_DIR_NOT_FOUND),
                    e);
            resultCode = RETCODE_ERR_OUTPUT_DIR_NOT_FOUND;

        } catch (TransformerConfigurationException e) {
            // Style sheet definition error
            CmnDocBookConverter.LOGGER.error(CmnResourceUtil
                    .getMessage(CmnResourceUtil.LOG_ERR_STYLESHEET_INACCURATE),
                    e);
            resultCode = RETCODE_ERR_STYLESHEET_INACCURATE;

        } catch (TransformerException e) {
            // Conversion failure
            CmnDocBookConverter.LOGGER.error(CmnResourceUtil
                    .getMessage(CmnResourceUtil.LOG_ERR_DOCBOOK_CONVERT), e);
            resultCode = RETCODE_ERR_CONVERT_DOCBOOK;

        } finally {
            if (output != null) {
                try {
                    output.close();
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        }

        return resultCode;
    }
}
