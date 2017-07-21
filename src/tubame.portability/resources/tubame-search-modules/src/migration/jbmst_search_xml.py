# -*- coding: utf-8 -*-
"""
jbmst_search_xml.py
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
XML search process.
Extension search process in the case of xml.
Do not search for the comment text.
If only Search Keyword1, and returns the results of the search in Search Keyword1. 
If the Search Keyword2 is also present, and returns the results to find the search file again by the Search Keyword2.
[Environment] Python 2.7
"""

import re, sys

SINGLE_COMMENT = "SINGLE_COMMENT"
MULTI_COMMENT = "MULTI_COMMENT"
MULTI_COMMENT_END = "MULTI_COMMENT_END"
HTML_MULTI_COMMENT = "HTML_MULTI_COMMENT"
SOURCE = "SOURCE"

"""
Check single comment, multi comment, whether it is the source,
 and returns a status corresponding to the type of statement.

@param pLine:Record to search for files
@retutn Type of sentence of one line to search for file
"""
def isSingleComment(pLine):
    m = re.search("^\s*<!--",pLine)
    if m:
        m = re.search("-->",pLine)
        if m:
            return SINGLE_COMMENT
        else:
            return HTML_MULTI_COMMENT
    return SOURCE

"""
Check whether the end of the multi-sentence comment.

@param pLine:Record to search for files
@retutn Type of sentence of one line to search for file
"""
def isHtmlMultiCommentEnd(pLine):
    m = re.search("-->",pLine)
    if m:
        return MULTI_COMMENT_END
    return MULTI_COMMENT

"""
Function is not using

@param pSeachKey
@param pLine
@retutn 
"""
def searchByLine(pSeachKey,pLine):
    m = re.search(pSeachKey,pLine)
    if m:
        return "FOUND"
    return "NOT_FOUND"

"""
If it was not a comment statement, to search for in the Search Keyword1 or Search Keyword2.
Set the search list the corresponding line number of the line that matches the above.

@param pSearchFile:File to be searched
@param pSearchStr:Search Keyword1 or Search Keyword2
@retutn Line list that hit the search
"""
def search_open_file(pSearchFile,pSearchStr):
    current_line_status = "NONE"
    line_count = 0
    line_count_list = []
    # Open the search files
    f = open(pSearchFile, "r")
    for line in f:
        line_count += 1
        # Determine the type of sentence
        line_status = isSingleComment(line)
        
        # Distributes the processing according to the type of sentence
        if (current_line_status == HTML_MULTI_COMMENT):
            # For multi-sentence comment
            if (isHtmlMultiCommentEnd(line) == MULTI_COMMENT_END):
                # If the multi-comment statement is completed
                current_line_status = SOURCE
        else:
            if (line_status == SOURCE):
                # If this is not the comment text
                # bug fix
                m = findAll(pSearchStr,line)
                if m:
                    for hit in m:
                        line_count_list.append(line_count)
            current_line_status = line_status
    f.close()
    return line_count_list

def findAll(pSearchStr,pLine):
    return  re.findall(pSearchStr,pLine)


"""
If only Search Keyword1, and returns the results of the search in Search Keyword1. 
If the Search Keyword2 is also present, and returns the results to find the search file again by the Search Keyword2.

@param pSearchFile File to be searched
@param pSearchStr1 Search Keyword1
@param pSearchStr2 Search Keyword2
@retutn List of lines that hit the search
"""
def searchByFile(pSearchFile,pSearchStr1,pSearchStr2):
    
    result_hit_count_list = []
    result_hit_count_list = search_open_file(pSearchFile,pSearchStr1)

    hit_total_cnt = len(result_hit_count_list)
    if hit_total_cnt!= 0 and pSearchStr2 != "":

        result_hit_count_list = search_open_file(pSearchFile,pSearchStr2)

    return result_hit_count_list
        