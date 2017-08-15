# -*- coding: utf-8 -*-
"""
[概 要]SQLファイル及びDDFファイルから指定された第一引数(pKey1)、第一引数(pKey2)で指定されたキワード(正規表現指定可能）より検索を行う。
[詳 細]第一引数(pKey1),第一引数(pKey2)で指定されたキワードにマッチしたレコード番号を出力する。SQLコメントのコメントは検索対象としない。
[備 考]
[環 境] Python 2.7
"""

__author__ = ' Copyright (C) 2011 Nippon Telegraph and Telephone Corporation, All rights reserved.'

import os
import sys
import re


def paramCheck(pKey1,pKey2):
        return 0


def getErrorFilePath():
    """ エラー発生時の検索対象ファイル"""
    global g_targetFilePath
    return g_targetFilePath

def find_for_step_counter(pSearchStr,pLine):
    global g_step_counter
    m = re.search("^\s*\n$",pLine)
    if not m:
        g_step_counter = g_step_counter + 1
    return  None

def find_for_jsp_step_counter(pSearchStr,pLine):
    global g_step_counter
    if re.search("^\s*\n$",pLine):
        return None
    if re.search("^\s*//",pLine):
        return None
    g_step_counter = g_step_counter + 1
    return  None


"""
・java/jsp/xml/propertiesファイルのステップ数を出力する。
・

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
def ext_search(pNo, pPriority, pFlag, pList, pKey1, pKey2, pInputCsv, pTargetDir, pChapterNo, pCheck_Status):
    common_module = sys.modules["migration.jbmst_common"]
    jbmst_search_java =sys.modules["migration.jbmst_search_java"]
    jbmst_search_java.findAll = find_for_step_counter
    jbmst_search_jsp =sys.modules["migration.jbmst_search_jsp"]
    jbmst_search_jsp.findAll = find_for_jsp_step_counter
    jbmst_search_xml =sys.modules["migration.jbmst_search_xml"]
    jbmst_search_xml.findAll = find_for_step_counter
    jbmst_search_properties =sys.modules["migration.jbmst_search_properties"]
    jbmst_search_properties.findAll = find_for_step_counter
    jbmst_search_sql =sys.modules["migration.jbmst_search_sql"]
    jbmst_search_sql.findAll = find_for_step_counter

    global g_targetFilePath,g_targetFileExt, g_search_str,g_step_counter

    g_search_str = u""
    
    for fname in pList:
        try :
            rsl_list = []
            g_step_counter  = 0
            g_targetFilePath = fname
            g_targetFileExt = common_module.getExtension(fname)
            if  g_targetFileExt == 'java' or g_targetFileExt == 'c' or g_targetFileExt == 'vc':
                jbmst_search_java.searchByFile(fname,pKey1,pKey2)
            elif g_targetFileExt == 'jsp':
                jbmst_search_jsp.searchByFile(fname,pKey1,pKey2)
            elif g_targetFileExt == 'xml':
                jbmst_search_xml.searchByFile(fname,pKey1,pKey2)
            elif g_targetFileExt == 'properties':
                jbmst_search_properties.searchByFile(fname,pKey1,pKey2)
            elif g_targetFileExt == 'sql' or g_targetFileExt == 'ddl':
                jbmst_search_sql.searchByFile(fname,pKey1,pKey2)
            else:
                raise "ext ("+g_targetFileExt+") is unsupport. supported(java/jsp/xml/properties/c/vc/sql/ddl"
            rsl_list.append(g_step_counter)
            common_module.print_csv(pNo, pPriority, pFlag, fname, rsl_list, pChapterNo, pCheck_Status)
        except Exception ,ex:
            raise ex