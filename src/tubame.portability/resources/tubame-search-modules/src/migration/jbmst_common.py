# -*- coding: utf-8 -*-
"""
jbmst_common.py
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
Common process.
Common processing that is used in the migration Search Tools.
[Environment] Python 2.7
"""

import sys
import string
import codecs
import re
import os
import imp

"""
Generate the file path if you want to search for files that match the extension specified
 in the command line parameter exists.

@param pSeachFolder:Folder name to search
@param pExtension:Extension to search for file name
@return Path list to search for files
"""
def searchFileByExtension(pSeachFolder,pExtension):

    SearchFileList = []
    for root, dirs, files in os.walk(pSeachFolder):
        for file in files:
            m = re.search("[^.]+$",file)
            extension = m.group()
            if extension == pExtension:
                SearchFileList.append(os.path.join(root, file))
    return SearchFileList

"""
Generate the file path if you want to search for files that match the extension specified
 in the command line parameter exists.

@param pSeachFolder:Folder name to search
@param pFileName:File name to search for
@return Path list to search for files
"""
def searchFileByFileName(pSeachFolder,pFileName):

    SearchFileList = []
    for root, dirs, files in os.walk(pSeachFolder):
        for file in files:
            m = re.search("^"+pFileName+"$",file)
            if m:
                SearchFileList.append(os.path.join(root, file))
    return SearchFileList

"""
If the "*." First character string, and performs a search by the extension.
If it is not "*." Is the first character string, and performs a search by file name.

@param pSeachFolder:Folder name to search
@param pSearchTarget:File name to search for that is extracted
@return Path list to search for files
"""
def searchFileBySearchTarget(pSeachFolder,pSearchTarget):

    m = re.search("^\*\.",pSearchTarget)
    if m:
        extension = pSearchTarget.split(".")[1]
        return searchFileByExtension(pSeachFolder,extension)
    else:
        return searchFileByFileName(pSeachFolder,pSearchTarget)

"""
Get the extension from the filename specified.

@param pTarget:Search for files search file
@return Extension to search for files
"""
def getExtension(pTarget):

    extension = ""
    m = re.search("[^\*\.]+$",pTarget)
    if m:
        extension = m.group()
    return extension

"""
Display in the following order the search results.
Number of the search condition file, Search Hits File, Search number of hits, Search corresponding line,
Degree of importance, Convert Flag, Guide Chapter, Check Status.

@param pNo:Number of the search condition file
@param pPriority:Degree of importance
@param pFlag:Convert Flag
@param pSearchFile:Search files
@param pResultRecCntList:Search result list
@param pChapterNo:Guide Chapter
@param pCheckStatus:Status
"""
def print_csv(pNo,pPriority,pFlag,pSearchFile,pResultRecCntList,pChapterNo,pCheckStatus):

    tmp = ""
    for hit in pResultRecCntList:
        tmp += str(hit)
        tmp += " "

    if tmp != "":
        tmp = tmp[:-1]
        line = pNo + "," + pSearchFile + "," + str(len(pResultRecCntList)) + ","+ tmp + "," + pPriority + "," + pFlag+ "," + pChapterNo+ "," + pCheckStatus
        print line

"""
Display in the following order the search results.
Number of the search condition file, 1, "", Search corresponding line,
Degree of importance, Convert Flag, Guide Chapter, Check Status.
Using only ext_search_jsp_use_bean.py extended search module.

@param pNo:Number of the search condition file
@param pPriority:Degree of importance
@param pFlag:Convert Flag
@param pResultRecCntList:Line list that hit the search
@param pChapterNo:Guide Chapter
@param pCheckStatus:Check Status
"""
def print_csv2(pNo,pPriority,pFlag,pResultRecCntList,pChapterNo,pCheckStatus):

    line = ""
    for filePath in pResultRecCntList:
        line = pNo + "," + filePath + ",1"+ ",1," + pPriority + "," + pFlag+ "," + pChapterNo+ "," + pCheckStatus
        print line

"""
Display in the following order the search results.
Number of the search condition file, Search Hits File, 1, "",
Degree of importance, Convert Flag, Guide Chapter, Check Status.

@param pNo:Number of the search condition file
@param pFilePath:Line list that hit the search
@param pPriority:Degree of importance
@param pFlag:Convert Flag
@param pChapterNo:Guide Chapter
@param pCheckStatus:Check Status
"""
def print_csv3(pNo,pFilePath,pPriority,pFlag,pChapterNo,pCheckStatus):

    print pNo + "," + pFilePath + ",1" +",," + pPriority + "," + pFlag+ "," + pChapterNo+ "," + pCheckStatus

"""
Display in the following order the search results.
Number of the search condition file, Search Hits File, 0, 0,
Degree of importance, Convert Flag, Guide Chapter, Check Status.

@param pNo:Number of the search condition file
@param pPriority:Degree of importance
@param pFlag:Convert Flag
@param pSearchFile:File search
@param pChapterNo:Guide Chapter
@param pCheckStatus:Check Status
"""
def print_csv4(pNo,pPriority,pFlag,pSearchFile,pChapterNo,pCheckStatus):
    print pNo + "," + pSearchFile + ",0"+",0," + pPriority + "," + pFlag+ "," + pChapterNo+ "," + pCheckStatus

"""
To convert the record read.
Return the string as it is when searching.
Override this method to portability new search.

@param pTempRow:One line search condition record
@return One line search condition record
"""
def load(pTempRow):

    row = pTempRow
    return row

"""
Setter method (search files).

@param self:Object of its own
@param searchTarget:File to be searched
"""
def search_target(self, searchTarget):
    self.searchTarget=searchTarget

"""
Getter method (search files).

@param self:Object of its own
"""
def search_target(self):
    return self.searchTarget

"""
Initial processing of accessor methods (search files).

@param self:Object of its own
"""
def __init__(self):
    self.searchTarget = ""
