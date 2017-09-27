# -*- coding: utf-8 -*-
"""
[概 要]XMLファイル要素検索
[詳 細]XMLファイルから、
  第一引数のXPATHより該当ノードが存在するかカウントする。
[備 考]
[環 境] Python 2.7
"""

__author__ = ' Copyright (C) 2011 Nippon Telegraph and Telephone Corporation, All rights reserved.'

import os
import sys
import re
from lxml import etree
from lxml import html



"""
・パラメータチェック
正常時は0を返却する
"""
def paramCheck(pKey1,pKey2):
    global unsupported_retry_counter
    unsupported_retry_counter = 0
    #パラメータチェック
    if (len(pKey1) > 0):
        return 0
    return 1

"""
・エラーがあったファイルパスを取得する
"""
def getErrorFilePath():
    global g_targetFilePath
    return g_targetFilePath

def checkUnsupportedEncoding(ex,UNSUPPORTED_ENCODING_STR='Unsupported encoding'):
    if UNSUPPORTED_ENCODING_STR in ex.message:
        token=ex.message.split(',')
        if len(token) > 1:
            unsuport_msg = token[0]
            line_msg = token[1]
            return dict(unsupportedEncoding=True, charset=unsuport_msg.split(UNSUPPORTED_ENCODING_STR)[1].strip(),lineNum=line_msg.split('line')[1].strip()) 
    else:
        return dict(unsupportedEncoding=False)
    
    
def replaceUnsupportedCharset(checkedDict,body):
    if str(checkedDict['charset']) == "Windows-31J" and int(checkedDict['lineNum']) == 1:
        body= body.replace('Windows-31J','cp932',1)
    return body
    
def isRetryForUnsupportedCharset(key):
    return key != None and isinstance(key,dict)

def result_path_handle(path,pKey2):
    result_list = []
    result_list.append(path.sourceline)
    return result_list


def delegate_ext_search(pFile, pKey1, pKey2,result_path_handle_func=None,try_charset_dict=None,unsupported_retry_counter=0):
    common_module = sys.modules["migration.jbmst_common"]
    global  g_targetFilePath
    rsl_list = []
    try :
        g_targetFilePath = pFile
        #tree = etree.parse(fname) # 返値はElementTree型
        #elem = tree.getroot() # ルート要素を取得(Element型)
        line = open(pFile, 'rU').read()
        if line == None or line == "":
            return rsl_list

        #pKey2がdictの場合、pythonがサポートしていない文字コード(Windows-31j)をcp932に置き換える
        if isRetryForUnsupportedCharset(try_charset_dict) == True:
            line = replaceUnsupportedCharset(try_charset_dict,line)
        elem = etree.fromstring(line)

        if elem == None:
             return rsl_list
        pathList = elem.xpath(pKey1)
        for path in pathList:
            if result_path_handle_func != None:
                rsl_list = rsl_list +result_path_handle_func(path,pKey2)
            else:
                rsl_list = rsl_list +result_path_handle(path,pKey2)
        return rsl_list
    except Exception ,ex:
        unsupported_retry_counter = unsupported_retry_counter + 1
        checkedDict = checkUnsupportedEncoding(ex)

        if checkedDict['unsupportedEncoding'] == True and unsupported_retry_counter == 1:
            try_charset_dict = checkedDict
            try:
                return delegate_ext_search(pFile, pKey1, pKey2,result_path_handle_func,try_charset_dict,unsupported_retry_counter)
                unsupported_retry_counter = 0
                try_charset_dict = None
            except Exception ,ex:
                try_charset_dict = None
                raise ex
        else:
            raise ex

"""
・XMLファイルを検索キー1で指定されたXPATHで検索を行う。
・検索結果を以下の順で表示する。
  検索条件ファイルのNo、検索該当ファイル、検索該当数、検索該当行、重要度、
  変換フラグ、章番号、確認ステータス

@param pNo 検索条件ファイルのNo
@param pPriority 重要度
@param pFlag 変換フラグ
@param pList 検索対象ファイルのリスト
@param pKey1 検索キー1
@param pKey2 検索キー2
@param pInputCsv 検索条件ファイルのパス
@param pTargetDir 検索対象ディレクトリのパス
@param pChapterNo 章番号
@param pCheck_Status 確認ステータス
"""
def ext_search(pNo, pPriority, pFlag, pList, pKey1, pKey2, pInputCsv, pTargetDir, pChapterNo, pCheck_Status,result_path_handle_func=None,try_charset_dict=None,unsupported_retry_counter=0):
    common_module = sys.modules["migration.jbmst_common"]
    result = []
    #検索対象ファイルリスト中の全ファイルを対象とする
    search_input_list=[(t, pKey1,pKey2,result_path_handle_func) for t in pList]
    p = common_module.getPool()
    result = p.map(wrap_ext_search, search_input_list)
    for result_tuple in result:
        if len(result_tuple[0]) != 0:
            common_module.print_csv(pNo, pPriority, pFlag,result_tuple[1],result_tuple[0], pChapterNo, pCheck_Status)

def wrap_ext_search(param):
    try:
        return (delegate_ext_search(*param),param[0])
    except Exception,e:
        raise Exception, '%s , searchTargetFile = %s' % (e,param[0])