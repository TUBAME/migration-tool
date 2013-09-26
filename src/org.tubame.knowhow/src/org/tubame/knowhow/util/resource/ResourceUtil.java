/*
 * ResourceUtil.java
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
package org.tubame.knowhow.util.resource;

import org.eclipse.osgi.util.NLS;

/**
 * Perform various common process associated with the resource.<br/>
 */
public final class ResourceUtil extends NLS {

    /** Resource bundle */
    private static final String BUNDLE_NAME = "resources.properties.resource"; //$NON-NLS-1$
    /** messeges.properties */
    public static String massagesPropertiesPath;
    /** application.properties */
    public static String applicationPropertiesPath;
    /** Copy */
    public static String contextCopyMenuName;
    /** Paste */
    public static String contextPasteMenuName;
    /** Delete */
    public static String contextDelete;
    /** Heading name input */
    public static String inputChapterSubject;
    /** Search information registration */
    public static String registerSearchInfoTitle;
    /** Name change */
    public static String changeName;
    /** DocBook save */
    public static String saveDocBook;
    /** Confirmation hearing: */
    public static String hearingConfirm;
    /** Visual confirmation: */
    public static String visualConfirm;
    /** Difficulty details: */
    public static String degreeDetail;
    /** Difficulty: */
    public static String portabilityDegree;
    /** Porting factor: */
    public static String protabilityFactor;
    /** Search implementation: */
    public static String searchExistance;
    /** Search procedure: */
    public static String searchProcess;
    /** Check items */
    public static String checkItem;
    /** Edit check item information */
    public static String checkItemInfoDescription;
    /** Search information */
    public static String searchInfo;
    /** Edit search information */
    public static String searchInfoDescription;
    /** Survey content: */
    public static String investigationLabel;
    /** Line number basis: */
    public static String lineNumberContents;
    /** Number of lines: */
    public static String lineNumber;
    /** Recorded description: */
    public static String appropriateContents;
    /** Number of lines recorded: */
    public static String appropriateLineLabel;
    /** Search Module Overview: */
    public static String pythonModuleContextLabel;
    /** Search module: */
    public static String pythonModuleLabel;
    /** Search keyword 2: */
    public static String searchKeyword2Label;
    /** Search keyword 1: */
    public static String searchKeyword1Label;
    /** Search for files: */
    public static String fileTypeLabel;
    /** Documentation */
    public static String documentation;
    /** Create a heading */
    public static String documentationDescription;
    /** Documentation information */
    public static String documentationInfo;
    /** Subject Name */
    public static String subjectName;
    /** Top */
    public static String up;
    /** Down */
    public static String down;
    /** New */
    public static String newCreate;
    /** Obtained from the URI */
    public static String fromURI;
    /** URI: */
    public static String uriLabel;
    /** Reference */
    public static String reference;
    /** Subject Name: */
    public static String subjectNameLabel;
    /** Know-how XML file */
    public static String knowhowXml;
    /** Know-how information selection page */
    public static String commencementPage;
    /** Output destination selection page */
    public static String confirmationPage;
    /** Know how detail */
    public static String knowhowDetail;
    /** Check Item Information */
    public static String checkItemInfo;
    /** Check Item Information: */
    public static String checkItemInfoLabel;
    /** Know-how XML save */
    public static String saveKnowhowXml;
    /** Destination: */
    public static String outputLabel;
    /** Output destination selection */
    public static String selectOutput;
    /** Create categories */
    public static String createCategory;
    /** Category registration */
    public static String registerCategory;
    /** Create a category */
    public static String createCategoryDescription;
    /** Check item information creation */
    public static String createCheckItemInfo;
    /** Check item information registration */
    public static String registerCheckItemInfo;
    /** Create a check item information */
    public static String createCheckItemDescription;
    /** Know-how created */
    public static String createknowhow;
    /** Know-how registration */
    public static String registerKnowhow;
    /** Create a know-how */
    public static String createknowhowDescription;
    /** Search information creation */
    public static String createSearchInfo;
    /** Create a search information */
    public static String createSearchDescription;
    /** Category name: */
    public static String categoryNameLabel;
    /** Know-how name: */
    public static String knowhowNameLabel;
    /** xml */
    public static String xml;
    /** Heading create */
    public static String createChapter;
    /** Please enter the name heading */
    public static String inputChapterName;
    /** Change to the number of lines recorded */
    public static String changeAppropriate;
    /** Change to the line-a-number recorded */
    public static String changeNonAppropriate;
    /** 0 */
    public static String initialChapterNo;
    /** ToDo / Unknown: */
    public static String unKnownLineLabel;
    /** Row * column */
    public static String rowAndEntry;
    /** Line: */
    public static String row;
    /** Column: */
    public static String entry;
    /** Table tag insertion */
    public static String insertTableTag;
    /** category_1 */
    public static String initialCategoryID;
    /** Know how_1 */
    public static String initialKnowhowID;
    /** Know how Detail_1 */
    public static String initialKnowhowDetailID;
    /** Check Item_1 */
    public static String initialCheckItemID;
    /** Search Info_1 */
    public static String initialSearchInfoID;
    /** Chapter */
    public static String dummyChapterID;
    /** Subject */
    public static String subject;
    /** DocBookXSL default file path */
    public static String defaultDocBookXslPath;
    /** DocBookXSL file path */
    public static String docBookXslPath;
    /** KnowhowXSL default file path */
    public static String defaultKnowhowXslPath;
    /** KnowhowXSL file path */
    public static String knowhowXslPath;
    /** Image file extension (csv format) */
    public static String imageFileExtension;
    /** Image file selection */
    public static String selectImageFile;
    /** Template ImageTag File */
    public static String templateImageTagFile;
    /** Template CodeTag File */
    public static String templateCodeTagFile;
    /** Template TextTag File */
    public static String templateTextTagFile;
    /** Template TableTag File */
    public static String templateTableTagFile;
    /** Template XrefTag File */
    public static String templateXrefTagFile;
    /** Template FootnoteTag File */
    public static String templateFootnoteTagFile;
    /** Template LinkTag File */
    public static String templateLinkTagFile;
    /** Template VarlistentryTag File */
    public static String templateVarlistentryTagFile;
    /** Please enter the text */
    public static String temprateDocBookStr;
    /** Transplant initialization process know-how */
    public static String initializeKnowhow;
    /** HTML Conversion */
    public static String convertHtml;

    static {
        NLS.initializeMessages(BUNDLE_NAME, ResourceUtil.class);
    }

    /**
     * Constructor.<br/>
     */
    private ResourceUtil() {
        // no operation
    }
}
