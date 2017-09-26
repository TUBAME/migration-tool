# -*- coding: utf-8 -*-
"""
jbmst_search_default.py
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
Search processing of other files.
Extension to search processing of files java, jsp, non-xml.
If only Search Keyword1, and returns the results of the search in Search Keyword1. 
If the Search Keyword2 is also present, and returns the results to find the search file again by the Search Keyword2.
[Environment] Python 2.7
"""
import re, sys

"""
Reads the records from the search file, to determine if it matches the search key.
Set the search list the corresponding line number of the line that matches the above.

@param pSearchFile File to be searched
@param pSearchStr Search Keyword1 or Search Keyword2
@return Line list that hit the search
"""
def search_open_file(pSearchFile,pSearchStr,isFirstMatchExit=False):

    current_line_status = "NONE"
    line_count = 0
    line_count_list = []
    f = open(pSearchFile, "r")
    for line in f:
        line_count += 1
        m = re.findall(pSearchStr,line)
        if m:
            for hit in m:
                line_count_list.append(line_count)
                if isFirstMatchExit == True:
                    f.close()
                    return line_count_list
    f.close()
    return line_count_list

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
    if pSearchStr2 != "":
        result_hit_count_list = search_open_file(pSearchFile,pSearchStr1,True)
    else:
        result_hit_count_list = search_open_file(pSearchFile,pSearchStr1)
    
    hit_total_cnt = len(result_hit_count_list)
    
    if hit_total_cnt!= 0 and pSearchStr2 != "":
        result_hit_count_list = search_open_file(pSearchFile,pSearchStr2)

    return result_hit_count_list

def wrapSearchByFile(param):
    try:
        return (searchByFile(*param),param[0])
    except Exception,e:
        raise Exception, '%s , searchTargetFile = %s' % (e,param[0])
