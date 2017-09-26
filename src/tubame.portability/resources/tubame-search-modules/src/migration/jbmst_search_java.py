# -*- coding: utf-8 -*-
"""
jbmst_search_java.py
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
Java search process.
Extension to search for the file processing of java.
Do not search for the comment text.
If only Search Keyword1, and returns the results of the search in Search Keyword1. 
If the Search Keyword2 is also present, and returns the results to find the search file again by the Search Keyword2.
[Environment] Python 2.7
"""

import re, sys,os

SINGLE_COMMENT = "SINGLE_COMMENT"
MULTI_COMMENT = "MULTI_COMMENT"
MULTI_COMMENT_END = "MULTI_COMMENT_END"
JAVA_SOURCE = "JAVA_SOURCE"




"""
Check single comment, multi comment, whether the source is searched record,
 and returns a status corresponding to the type of statement.

@param pLine:Record to search for files
@retutn Type of sentence of one line to search for file
"""
def isSingleComment(pLine,LINE_HEAD_COMMENT_STR = "//"):
    JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT =None
    m = re.search("^\s*"+LINE_HEAD_COMMENT_STR,pLine)
    if m:
        return SINGLE_COMMENT,JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT
    else:
        #support end of line comment
        m = re.search("(\s*\w*)"+LINE_HEAD_COMMENT_STR,pLine)
        if m:
            m = re.search("[^"+LINE_HEAD_COMMENT_STR+"]*",pLine)
            if m != None:
                JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT  =  m.group()
    m = re.search("^\s*/\*",pLine)
    if m:
        m = re.search("\*/\s*$",pLine)
        if m:
            return SINGLE_COMMENT,JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT
        else:
            return MULTI_COMMENT,JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT
    else:
        #support end of line comment
        m = re.search("(\s*\w*)/\*.*\*/$",pLine)
        if m:
            result = m.group()
            if result != None:
                index = len(result)
                JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT =pLine[:-index]

    return JAVA_SOURCE,JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT

"""
Search records it is determined whether the end of the multi comment statement.

@param pLine:Record to search for files
@retutn Type of sentence of one line to search for file
"""
def isMultiCommentEnd(pLine):
    m = re.search("\*/\s*$",pLine)
    if m:
        return MULTI_COMMENT_END
    return MULTI_COMMENT

"""
Function is not using

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
If this is not the comment text, to search for in the Search Keyword1 or Search Keyword2.
Set the search list the corresponding line number of the line that matches the above.

@param pSearchFile File to be searched
@param pSearchStr Search Keyword1 or Search Keyword2
@return List of search corresponding line
"""
def search_open_file(pSearchFile,pSearchStr,isFirstMatchExit=False,LINE_HEAD_COMMENT_STR = "//",isSemicolonParser=False,pSearchStr2="",pFlag=0):
    current_line_status = "NONE"
    line_count = 0
    line_count_list = []
    searchTargetBody = ""
    searchTargetBodyIncludedComment= ""
    # Open the search files
    f = open(pSearchFile, "r")
    for line in f:
        searchTargetBodyIncludedComment += line
        line_count += 1
        # Determine the type of sentence
        line_status ,JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT= isSingleComment(line,LINE_HEAD_COMMENT_STR)

        # Distributes the processing according to the type of sentence
        if ( current_line_status == MULTI_COMMENT):
            # If multi-sentence comment
            if (isMultiCommentEnd(line) == MULTI_COMMENT_END):
                # If the multi-comment statement is completed
                current_line_status = JAVA_SOURCE
        else:
            if (line_status == JAVA_SOURCE):
                # If this is not the comment text
                # suuport end of line comment
                if JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT != None:
                    line = JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT

                #セミコロンまでをひとつのトークンとして検索を行う
                if isSemicolonParser == True:
                    searchTargetBody += line
                    if hasEndSemicolon(searchTargetBody) == True:
                        find_result = findByKeywords(pSearchStr,pSearchStr2,LINE_HEAD_COMMENT_STR,searchTargetBody,searchTargetBodyIncludedComment.rstrip(),line_count,pFlag)
                        line_count_list += find_result
                        searchTargetBodyIncludedComment = ""
                        searchTargetBody = ""

                else:
                    m = findAll(pSearchStr,line,pFlag)
                    if m:
                        for hit in m:
                            line_count_list.append(line_count)
                            if isFirstMatchExit == True:
                                f.close()
                                return line_count_list
    
            current_line_status = line_status

    f.close()
    return line_count_list

def findAll(pSearchStr,pLine,pFlag=0):
    return  re.findall(pSearchStr,pLine,pFlag)

def hasEndSemicolon(pTarget):
    if re.search(".*;\s*$",pTarget):
        return True
    return False

def hasEndBackSlash(pTarget):
    if re.search(".*\\s*$",pTarget):
        return True
    return False

# def getIndexBaseEndofLine(body,match):
#     print 'body:',body
#     tokens = body.split(';')
#     if len(tokens) != 0:
#         if not match.end() +1 > len(body):
#            match_after_line =  body[match.end()+1:]
#            print 'match_after_line' ,match_after_line
#            m = match_after_line.split(';')
#            if m:
#                 return m[0].count('\n')
#     else:
#         return 0
        

def getMatch(pSearchStr2,append_line):
    match = re.finditer(pSearchStr2,append_line)
    return len(match),match


def findByKeywords(pSearchStr1,pSearchStr2,LINE_HEAD_COMMENT_STR,pSearchTarget,pSearchTargetIncludedComment,pline_count,pFlag=0):
    result_list = []
    #print pSearchTarget
    #print pSearchStr1
    # コメントを除去したものを対象がヒットしない場合は処理しない
    m= re.findall(pSearchStr1,pSearchTarget.replace('\n',''),pFlag)
    if len(m) == 0:
        return result_list

    if pSearchStr2 == "":
        searchKey =pSearchStr1
    else:
        searchKey =pSearchStr2

    lines = pSearchTargetIncludedComment.split('\n')
    line_length = len(lines)
    line_count = 0
    current_line_status = "NONE"
    firstMatch = False
    append_line = ""
    match_len = 0    
    for line in lines:
        line_count += 1
        line_status ,JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT= isSingleComment(line,LINE_HEAD_COMMENT_STR)

        if current_line_status == MULTI_COMMENT:
            # If multi-sentence comment
            if isMultiCommentEnd(line) == MULTI_COMMENT_END:
                # If the multi-comment statement is completed
                current_line_status = JAVA_SOURCE
        else:
            if line_status == JAVA_SOURCE:
                # If this is not the comment text
                # suuport end of line comment
                if JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT != None:
                    line = JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT


                append_line += line
                if firstMatch == False:
                    match = re.finditer(searchKey,append_line,pFlag)
                    i = 0
                    for m in  match:
                        result_list.append(pline_count - (line_length -line_count))
                        i += 1
                        firstMatch = True
                    if i !=0:
                        match_len = i

                else:
                    match = re.finditer(searchKey,append_line,pFlag)
                    i = 0
                    for m in match:
                        if i  >=  match_len:
                            result_list.append(pline_count - (line_length -line_count))
                        i = i + 1
                    if i > 0:
                        match_len = i

            current_line_status = line_status

    return result_list

def searchInterfaceMethod(pSearchFile,LINE_HEAD_COMMENT_STR="//"):
    current_line_status = "NONE"
    line_count = 0
    methodname_list = []

    # Open the search files
    f = open(pSearchFile, "r")
    for line in f:
        line_count += 1
        # Determine the type of sentence
        line_status,JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT= isSingleComment(line,LINE_HEAD_COMMENT_STR)

        # Distributes the processing according to the type of sentence
        if ( current_line_status == MULTI_COMMENT):
            # If multi-sentence comment
            if (isMultiCommentEnd(line) == MULTI_COMMENT_END):
                # If the multi-comment statement is completed
                current_line_status = JAVA_SOURCE
        else:
            if (line_status == JAVA_SOURCE):
                # If this is not the comment text
                # suuport end of line comment
                if JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT != None:
                    line = JAVA_SOURCE_EXCLUSION_END_OF_LINE_COMMENT
                m = re.search("^(?!.*\s+(static|new)\s+).*$",line)
                if m != None:
                    m =re.search("\w+\s+(\w+)\s*\(.*",line)
                    if m:
                        method_name=m.group(1)
                        methodname_list.append(method_name)

    f.close()
    return methodname_list



"""
If only Search Keyword1, and returns the results of the search in Search Keyword1.
If the Search Keyword2 is also present, and returns the results to find the search file again by the Search Keyword2.

@param pSearchFile File to be searched
@param pSearchStr1 Search Keyword1
@param pSearchStr2 Search Keyword2
@retutn List of lines that hit the search
"""
def searchByFile(pSearchFile,pSearchStr1,pSearchStr2,isFirstMatchExit=False,LINE_HEAD_COMMENT_STR = "//",IS_SEMICOLON_PARSER=False,FLAG=0):
    result_hit_count_list = []
    if pSearchStr2 != "" and IS_SEMICOLON_PARSER == True:
        #SEMICOLON_PARSERの場合のみ、そのまま、第２キワードで検索を実施する。
        return  search_open_file(pSearchFile,pSearchStr1,True,LINE_HEAD_COMMENT_STR,IS_SEMICOLON_PARSER,pSearchStr2,FLAG)
    else:
        result_hit_count_list = search_open_file(pSearchFile,pSearchStr1,False,LINE_HEAD_COMMENT_STR,IS_SEMICOLON_PARSER,"",FLAG)
    
    hit_total_cnt = len(result_hit_count_list)
    
    if hit_total_cnt!= 0 and pSearchStr2 != "":
        result_hit_count_list = search_open_file(pSearchFile,pSearchStr2,isFirstMatchExit,LINE_HEAD_COMMENT_STR,IS_SEMICOLON_PARSER,"",FLAG)
        
    return result_hit_count_list

def wrapSearchByFile(param):
    try:
        return (searchByFile(*param),param[0])
    except Exception,e:
        raise Exception, '%s , searchTargetFile = %s' % (e,param[0])

def wrapSearchOpenFile(param):
    try:
        return (search_open_file(*param),param[0])
    except Exception,e:
        raise Exception, '%s , searchTargetFile = %s' % (e,param[0])