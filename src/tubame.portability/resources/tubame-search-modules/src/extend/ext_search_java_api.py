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
from multiprocessing import Pool


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
    
    if len(pList) == 0 :
        return
    search_ext = common_module.getExtension(pList[0])

    search_input_list=[(t, search_target_package_name,True) for t in pList]
    p = common_module.getPool()
    if not PACKAGE_MAP.has_key(search_ext+","+package_name):
        match_list = p.map(jbmst_search_java.wrapSearchOpenFile, search_input_list)
        for result_tuple in match_list:
            if len(result_tuple[0]) != 0:
                if PACKAGE_MAP.has_key(search_ext+","+package_name):
                    PACKAGE_MAP.get(search_ext+","+package_name).append(result_tuple[1])
                else:
                    filePathList = []
                    filePathList.append(result_tuple[1])
                    PACKAGE_MAP[search_ext+","+package_name] = filePathList
        if not PACKAGE_MAP.has_key(search_ext+","+package_name):
            PACKAGE_MAP[search_ext+","+package_name] = []

    targets = []
    for filePath in pList:
        rsl_list = [] 
        search_ext = common_module.getExtension(filePath)
        if PACKAGE_MAP.has_key(search_ext+","+package_name):
            if filePath in PACKAGE_MAP.get(search_ext+","+package_name):
                keyword1 = search_target_package_name+'.(\*|'+class_name+')'
                if pKey2== "":
                    keyword2= class_name
                else:
                    keyword2 = '\.'+pKey2+'\('
                targets.append((filePath,keyword1,keyword2))
    
    map_result_list = p.map(jbmst_search_java.wrapSearchByFile, targets)
    for result_tuple in map_result_list:
        if len(result_tuple[0]) != 0:
            common_module.print_csv(pNo, pPriority, pFlag, result_tuple[1],result_tuple[0], pChapterNo, pCheck_Status)