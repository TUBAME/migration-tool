# -*- coding: utf-8 -*-
"""
jbmst_search_jsp.py
Created on 2013/06/28

Copyright (C) 2011-2013 Nippon Telegraph and Telephone Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

"""
JSP search process.
Extension to search for the file processing of jsp.
Do not search for the comment statement, but comments scriptret in javascript or is search target.
If only Search Keyword1, and returns the results of the search in Search Keyword1. 
If the Search Keyword2 is also present, and returns the results to find the search file again by the Search Keyword2.
[Environment] Python 2.7
"""

import re, sys

SINGLE_COMMENT = "SINGLE_COMMENT"
MULTI_COMMENT = "MULTI_COMMENT"
JAVA_MULTI_COMMENT ="JAVA_MULTI_COMMENT"
JSP_MULTI_COMMENT ="JSP_MULTI_COMMENT"
MULTI_COMMENT_END = "MULTI_COMMENT_END"
HTML_MULTI_COMMENT = "HTML_MULTI_COMMENT"
NON_JAVA_SCRIPT = "NON_JAVA_SCRIPT"
JAVA_SCRIPT = "JAVA_SCRIPT"
SOURCE = "SOURCE"

"""
Check whether the source or single or multi-comment comment, and returns a status according to the type of sentence.

@param pLine:Record to search for files
@retutn Status of the statement to search for records
"""
def isSingleComment(pLine):
    # Search comments or statements jsp
    m = re.search("^\s*<%--",pLine)
    if m:
        # Comments statement jsp search if it ends on a single line
        m = re.search("--%>",pLine)
        if m:
            return SINGLE_COMMENT
        else:
            return JSP_MULTI_COMMENT
    # Search comment or statement of html
    m = re.search("^\s*<!--",pLine)
    if m:
        # Comments statement html search if it ends on a single line
        m = re.search("-->",pLine)
        if m:
            return SINGLE_COMMENT
        else:
            return HTML_MULTI_COMMENT
    # Search or single sentence comment
    m = re.search("^^\s*<%\s*//.*%>\s*$",pLine)
    if m:
        return SINGLE_COMMENT
    # Search whether the javascript
    m = re.search("<[Ss][Cc][Rr][Ii][Pp][Tt]",pLine)
    if m:
        # Search termination or javascript
        m = re.search("</[Ss][Cc][Rr][Ii][Pp][Tt]>", pLine)
        if m:
            return SOURCE
        else:
            return JAVA_SCRIPT
    return SOURCE

"""
Search records it is determined whether the end of the comment text of html.

@param pLine:Record to search for files
@retutn Status of the statement of one line to search for file
"""
def isHtmlMultiCommentEnd(pLine):
    m = re.search("-->",pLine)
    if m:
        return MULTI_COMMENT_END
    return MULTI_COMMENT

"""
Search records it is determined whether the end of the comment statement jsp.

@param pLine:Record to search for files
@return Type of sentence of one line to search for file
"""
def isJspMultiCommentEnd(pLine):
    m = re.search("--%>",pLine)
    if m:
        return MULTI_COMMENT_END
    return MULTI_COMMENT

"""
Search records it is determined whether the end of the javascript statement.

@param pLine:Record to search for files
@return Type of sentence of one line to search for file
"""
def isJavaScriptEnd(pLine):
    m = re.search("</[Ss][Cc][Rr][Ii][Pp][Tt]>",pLine)
    if m:
        return MULTI_COMMENT_END
    return MULTI_COMMENT

"""
Function is not using.

@param pSeachKey
@param pLine
@return 
"""
def searchByLine(pSeachKey,pLine):
    m = re.search(pSeachKey,pLine)
    if m:
        return "FOUND"
    return "NOT_FOUND"

"""
Search the status of the search record.
Skip the case processing status of the search record is a comment statement.
If it was not a comment statement, to search for in the Search Keyword1 or Search Keyword2.
Set the search list the corresponding line number of the line that matches the above.

@param pSearchFile:File to be searched
@param pSearchStr:Search Keyword1 or Search Keyword2
@return Line list that hit the search
"""
def search_open_file(pSearchFile,pSearchStr):
    current_line_status = "NONE"
    line_count = 0
    line_count_list = []
    # Open the search files
    f = open(pSearchFile, "r")
    for line in f:
        line_count += 1
        # Determine the type of string
        line_status = isSingleComment(line)
        
        # Distributes the processing according to the type of character
        # Determined whether multi comment statement html
        if (current_line_status == HTML_MULTI_COMMENT):
            # Determine multi comments html or ended
            if (isHtmlMultiCommentEnd(line) == MULTI_COMMENT_END):
                current_line_status = SOURCE
        # Determined whether multi comment statement jsp
        elif (current_line_status == JSP_MULTI_COMMENT):
            # Determine multi-comment statement jsp or ended
            if (isJspMultiCommentEnd(line) == MULTI_COMMENT_END):
                current_line_status = SOURCE
        # Determined whether the javascript
        elif (current_line_status == JAVA_SCRIPT):
            # Determined the javascript been completed
            if (isJavaScriptEnd(line) == MULTI_COMMENT_END):
                current_line_status = SOURCE
            else:
                # search comment or statement of javascript (//).
                m = re.search("^\s*//",line)
                # Determines whether the comment statement javascript
                if m:
                    continue
                # If source
                else:
                    m = re.findall(pSearchStr,line)
                    if m:
                        for hit in m:
                            line_count_list.append(line_count)    
        else:
            if (line_status == SOURCE):
                m = re.findall(pSearchStr,line)
                if m:
                    #bug fix 
                    for hit in m:
                        line_count_list.append(line_count)
            current_line_status = line_status
    f.close()
    return line_count_list
    
"""
If only Search Keyword1, and returns the results of the search in Search Keyword1. 
If the Search Keyword2 is also present, and returns the results to find the search file again by the Search Keyword2.

@param pSearchFile:File to be searched
@param pSearchStr1:Search Keyword1
@param pSearchStr2:Search Keyword2
@retutn List of lines that hit the search
"""
def searchByFile(pSearchFile,pSearchStr1,pSearchStr2):

    result_hit_count_list = []
    result_hit_count_list = search_open_file(pSearchFile,pSearchStr1)
    
    hit_total_cnt = len(result_hit_count_list)

    if hit_total_cnt!= 0 and pSearchStr2 != "":

        result_hit_count_list = search_open_file(pSearchFile,pSearchStr2)

    return result_hit_count_list
