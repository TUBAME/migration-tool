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
Extension to search for the file processing of sh.
Do not search for the comment statement
If only Search Keyword1, and returns the results of the search in Search Keyword1.
If the Search Keyword2 is also present, and returns the results to find the search file again by the Search Keyword2.
[Environment] Python 2.7
"""

import re, sys


SINGLE_COMMENT = "SINGLE_COMMENT"
SOURCE = "PROPS_SOURCE"

"""
Check whether the source or single comment

@param pLine:Record to search for files
@retutn Status of the statement to search for records
"""
def override_is_line_type(pLine):
    #propertiesの行頭#か、判別する
    m = re.search("^\s*#",pLine)
    if m:
        return SINGLE_COMMENT
    return SOURCE



    
"""
If only Search Keyword1, and returns the results of the search in Search Keyword1. 
If the Search Keyword2 is also present, and returns the results to find the search file again by the Search Keyword2.

@param pSearchFile:File to be searched
@param pSearchStr1:Search Keyword1
@param pSearchStr2:Search Keyword2 is ignore
@retutn List of lines that hit the search
"""
def searchByFile(pSearchFile,pSearchStr1,pSearchStr2):
    jbmst_search_properties = sys.modules["migration.jbmst_search_properties"]
    return jbmst_search_properties.searchByFile(pSearchFile,pSearchStr1,pSearchStr2,override_is_line_type)

