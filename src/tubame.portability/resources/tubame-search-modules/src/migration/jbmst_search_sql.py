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

import re, sys



"""
If only Search Keyword1, and returns the results of the search in Search Keyword1. 
If the Search Keyword2 is also present, and returns the results to find the search file again by the Search Keyword2.

@param pSearchFile:File to be searched
@param pSearchStr1:Search Keyword1
@param pSearchStr2:Search Keyword2 is ignore
@retutn List of lines that hit the search
"""
def searchByFile(pSearchFile,pSearchStr1,pSearchStr2):
    jbmst_search_java = sys.modules["migration.jbmst_search_java"]
    jbmst_search_java.LINE_HEAD_COMMENT_STR = "--"
    return jbmst_search_java.searchByFile(pSearchFile,pSearchStr1,pSearchStr2)

