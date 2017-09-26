# -*- coding: utf-8 -*-
"""
[概 要]XMLに定義されたインタフェースのメソッド名をキーワードに検索を実施する。
[詳 細]
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
def ext_search(pNo, pPriority, pFlag, pList, pKey1, pKey2, pInputCsv, pTargetDir, pChapterNo, pCheck_Status):
    common_module = sys.modules["migration.jbmst_common"]
    java_module = sys.modules["migration.jbmst_search_java"]

    xPath = pKey1
    global g_targetFilePath
    defined_class_list = []
    for fname in pList:
        try :
            g_targetFilePath = fname
            line = open(fname, 'rU').read()
            elem = etree.fromstring(line)
            if elem == None:
                continue
            pathList = elem.xpath(xPath)

            for path in pathList:
                defined_class_list.append(path)
                
        except Exception ,ex:
            raise ex

    method_name_list=[]
    if len(pList)!=0 and defined_class_list != None:

        for defined_class_fullname in defined_class_list:
            packageName,className=common_module.getPackageAndClassName(defined_class_fullname)
            findTarget =common_module.searchFileByPackageAndFileName(pTargetDir,packageName,className,[])
            if findTarget !=None:
                method_name_list = method_name_list+ java_module.searchInterfaceMethod(findTarget)

    method_name_set = set()
    for m in method_name_list:
        method_name_set.add(m)

    if len(method_name_set)!=0:
        searchFileList = common_module.searchFileByExtension(pTargetDir, "java", [])
        for target in searchFileList:
            for method_name in method_name_set:
                result_list=java_module.searchByFile(target,"\."+method_name+"\s*\(","")
                common_module.print_csv(pNo, pPriority, pFlag, target, result_list, pChapterNo, pCheck_Status)
