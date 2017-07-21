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
properties search process.
Extension to search for the file processing of properties.
Do not search for the comment statement
以下のような、行末の\マークがある場合は、SELECT SMP.SAMPLE_NO,SMP.SAMPLE_NAME FROM S00.SMP_TABLE AS SMP WHERE　SMP.SAMPLE_NO = '{SAMPLE_NO}'
として、検索を行う。この場合は、検索キーワード1にSELECT.*WHEREを指定すると1行目がヒットする。
1>SQL0001 = SELECT SMP.SAMPLE_NO,SMP.SAMPLE_NAME FROM S00.SMP_TABLE AS SMP \
2>WHERE \
3>SMP.SAMPLE_NO = '{SAMPLE_NO}'
If only Search Keyword1, and returns the results of the search in Search Keyword1.
If the Search Keyword2 is also present, and returns the results to find the search file again by the Search Keyword2.
[Environment] Python 2.7
"""

import re, sys

SINGLE_COMMENT = "SINGLE_COMMENT"
SOURCE = "PROPS_SOURCE"
SOURCE_CONTINUE = "SOURCE_CONTINUE"
SOURCE_CONTINUE_END = "SOURCE_CONTINUE_END"
"""
Check whether the source or single comment

@param pLine:Record to search for files
@retutn Status of the statement to search for records
"""
def is_line_type(pLine):

    #propertiesの行頭#か、判別する
    m = re.search("^\s*#",pLine)
    if m:
        return SINGLE_COMMENT

    # prpertiesで、行末に\マークで改行されているか、判別する
    m = re.search("\\\\$",pLine)
    if m:
        return SOURCE_CONTINUE
    return SOURCE

"""
Search records it is determined whether continue properties.

@param pLine:Record to search for files
@retutn Status of the statement of one line to search for file
"""
def is_source_continue_end(pLine):
    m = re.search("[^\\$]",pLine)
    if m:
        return SOURCE_CONTINUE_END
    return SOURCE_CONTINUE


"""
Search the status of the search record.
@param pSearchFile:File to be searched
@param pSearchStr:Search Keyword1 or Search Keyword2
@return Line list that hit the search
"""
def search_open_file(pSearchFile,pSearchStr):
    current_line_status = None
    line_count = 0
    line_count_list = []
    row = None

    line_continue_row_count = 0
    f = open(pSearchFile, "r")
    for line in f:
        line_count += 1
        line_type = is_line_type(line)
        if line_type == SOURCE_CONTINUE:
            if line_continue_row_count == 0:
                row =""
            row += line.strip()[:-1]
            current_line_status = SOURCE_CONTINUE
            line_continue_row_count += 1
            continue
        elif line_type == SINGLE_COMMENT:
            continue
        else:
            if current_line_status == SOURCE_CONTINUE:
                if is_source_continue_end(line) == SOURCE_CONTINUE_END:
                    row += line
                    current_line_status = None
                    line_continue_row_count += 1
            else:
                row = line

        m = findAll(pSearchStr,row)
        if m:
            for hit in m:
                if line_continue_row_count != 0:
                    line_count_list.append( (line_count - line_continue_row_count) +1)
                else:
                    line_count_list.append(line_count)
        line_continue_row_count = 0
    f.close()
    return line_count_list

def findAll(pSearchStr,pLine):
    return  re.findall(pSearchStr,pLine)

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
