# -*- coding: utf-8 -*-
"""
[概 要]javaのAPIを検索する。
[詳 細]第一引数にFQCNでクラス名を指定する。第２引数に検索したいメソッド名を定義すし、メソッドの実行箇所を検索する。
[備 考]
[環 境] Python 2.7
"""

__author__ = ' Copyright (C) 2011 Nippon Telegraph and Telephone Corporation, All rights reserved.'

import os
import sys
import re
from lxml import etree
from lxml import html


PACKAGE_MAP = {}

"""
・パラメータチェック
正常時は0を返却する
"""
def paramCheck(pKey1,pKey2):
    global unsupported_retry_counter
    unsupported_retry_counter = 0
    #パラメータチェック
    if len(pKey1) > 0:
        return 0
    return 1

"""
・エラーがあったファイルパスを取得する
"""
def getErrorFilePath():
    global g_targetFilePath
    return g_targetFilePath


"""
・検索キー1で指定されたFQCNのパッケージを定義しているJavaファイル群を取得し、パッケージ名ともにキャシュする。
そのキャシュされた情報をもとに検索を実施する。

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
    jbmst_search_java = sys.modules["migration.jbmst_search_java"]
    
    token = pKey1.split('.')
    package_name = ".".join(token[:-1])
    search_target_package_name = "\.".join(token[:-1])
    class_name= token[len(token) -1]

    if not PACKAGE_MAP.has_key(package_name):
        for filePath in pList:
            match_list = jbmst_search_java.search_open_file(filePath,search_target_package_name,True)
            if len(match_list) != 0 :
                if PACKAGE_MAP.has_key(package_name):
                    PACKAGE_MAP.get(package_name).append(filePath)
                else:
                    filePathList = []
                    filePathList.append(filePath)
                    PACKAGE_MAP[package_name] = filePathList
    
    for filePath in pList:
        rsl_list = []    
        if PACKAGE_MAP.has_key(package_name):
            if filePath in PACKAGE_MAP.get(package_name):
                keyword1 = search_target_package_name+'('+class_name+'|' +'|.\*)'
                if pKey2== "":
                    keyword2= class_name
                else:
                    keyword2 = '\.'+pKey2+'\('
                rsl_list =jbmst_search_java.searchByFile(filePath,keyword1,keyword2)
                common_module.print_csv(pNo, pPriority, pFlag, filePath, rsl_list, pChapterNo, pCheck_Status)
    