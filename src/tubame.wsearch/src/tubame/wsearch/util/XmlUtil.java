/*
 * XmlUtil.java
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
/**
 *
 */
package tubame.wsearch.util;

import java.io.ByteArrayInputStream;
import java.io.ByteArrayOutputStream;
import java.io.InputStream;
import java.util.List;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Result;
import javax.xml.transform.Source;
import javax.xml.transform.TransformerConfigurationException;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.TransformerFactoryConfigurationError;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

import tubame.wsearch.Activator;
import tubame.wsearch.models.WSearchEditorEnum;
import tubame.wsearch.models.WSearchEditorMigrationRow;
import org.w3c.dom.Document;
import org.w3c.dom.Element;

/**
 * It Is XmlUtil class.<br/>
 * It is a utility for saving in XML format, reading etc..<br/>
 */
public class XmlUtil {

    /**
     * Output in XML format general-purpose search result data.<br/>
     * 
     * @param rows
     *            General-purpose search result data
     * @return Streams enter the output result
     * @throws ParserConfigurationException
     *             Exception of XML-related
     * @throws TransformerConfigurationException
     *             Exception of XML-related
     * @throws TransformerException
     *             Exception of XML-related
     * @throws TransformerFactoryConfigurationError
     *             Exception of XML-related
     */
    public static InputStream outputAsXml(List<WSearchEditorMigrationRow> rows)
            throws ParserConfigurationException,
            TransformerConfigurationException, TransformerException,
            TransformerFactoryConfigurationError {
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder = factory.newDocumentBuilder();
        Document document = builder.newDocument();
        Element article = document.createElement("article");
        document.appendChild(article);
        Element title = document.createElement("title");
        title.appendChild(document.createTextNode(Activator
                .getResourceString(XmlUtil.class.getName() + ".label.TitleText")));
        article.appendChild(title);
        Element table = document.createElement("table");
        article.appendChild(table);
        title = document.createElement("title");
        title.appendChild(document.createTextNode(Activator
                .getResourceString(XmlUtil.class.getName()
                        + ".label.TableTitleText")));
        table.appendChild(title);
        Element tgroup = document.createElement("tgroup");
        tgroup.setAttribute("cols",
                Integer.toString(WSearchEditorEnum.values().length));
        table.appendChild(tgroup);
        Element tbody = document.createElement("tbody");
        tgroup.appendChild(tbody);

        // Header line
        Element header = document.createElement("row");
        for (WSearchEditorEnum index : WSearchEditorEnum.values()) {
            Element th = document.createElement("entry");
            th.appendChild(document.createTextNode(index.getTitle()));
            header.appendChild(th);
        }

        tbody.appendChild(header);
        appendTableData(document, tbody, rows);
        ByteArrayOutputStream outputStream = new ByteArrayOutputStream();
        Source xmlSource = new DOMSource(document);
        Result outputTarget = new StreamResult(outputStream);
        TransformerFactory.newInstance().newTransformer()
                .transform(xmlSource, outputTarget);
        return new ByteArrayInputStream(outputStream.toByteArray());
    }

    /**
     * Add a table data.<br/>
     * 
     * @param document
     *            Document object
     * @param tbody
     *            Table body object
     * @param list
     *            List of generic search result data
     */
    private static void appendTableData(Document document, Element tbody,
            List<WSearchEditorMigrationRow> list) {
        for (WSearchEditorMigrationRow row : list) {
            Element tr = document.createElement("row");
            for (WSearchEditorEnum index : WSearchEditorEnum.values()) {
                Element td = document.createElement("entry");
                String value = row.getColumnText(index.getIndex());
                if (index == WSearchEditorEnum.INDEX_HIT_NUM) {
                    value = row.getHitNum();
                }
                td.appendChild(document.createTextNode(value));
                tr.appendChild(td);
            }
            tbody.appendChild(tr);
            if (row.hasChildren()) {
                appendTableData(document, tbody, row.getChildren());
            }
        }
    }
}