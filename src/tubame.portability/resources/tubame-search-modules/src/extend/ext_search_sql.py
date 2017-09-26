# -*- coding: utf-8 -*-
"""
[概 要]sqlを検索する。
[詳 細]拡張子がjava,c,h,pcの場合は、セミコロンまでを一つのトークンとして検索する。拡張子がproperties,sh,cshの場合、改行のエンマークを識別して、検索する。
拡張子がddl/sqlの場合は、セミコロンまでを一つのトークンとして検索する。拡張子がxmlの場合は、XMLのタグの値を検索する
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


def getTargetMap(pList):
    jbmst_common = sys.modules["migration.jbmst_common"]
    targetMap = {}
    for f in pList:
        ext = jbmst_common.getExtension(f)
        if not targetMap.has_key(ext):
            list = []
            list.append(f)
            targetMap[ext] = list
        else:
            list = targetMap.get(ext)
            list.append(f)
    return targetMap

"""
・検索キー1、検索キー2を利用して検索する。

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
    jbmst_search_default = sys.modules["migration.jbmst_search_default"]
    jbmst_search_properties = sys.modules["migration.jbmst_search_properties"]
    jbmst_search_sh = sys.modules["migration.jbmst_search_sh"]
    jbmst_search_sql = sys.modules["migration.jbmst_search_sql"]
    ext_search_xml_text = sys.modules["extend.ext_search_xml_text"]

    if len(pList) == 0:
        return


    p = common_module.getPool()
    target_map = getTargetMap(pList)

    keys =target_map.keys()
    for ext in keys:
        map_result_list = {}
        targets =target_map.get(ext)
        if ext == "java" or ext == "c" or ext == "cpp" or ext == "pc" :
            search_input_list=[(t, pKey1,pKey2,False,"//",True,re.IGNORECASE) for t in targets]
            map_result_list = p.map(jbmst_search_java.wrapSearchByFile, search_input_list)
        elif ext== "h":
            search_input_list=[(t, pKey1,pKey2,False,"//",False,re.IGNORECASE) for t in targets]
            map_result_list = p.map(jbmst_search_java.wrapSearchByFile, search_input_list)
        elif ext == "properties":
            search_input_list=[(t, pKey1,pKey2,None,re.IGNORECASE) for t in targets]
            map_result_list = p.map(jbmst_search_properties.wrapSearchByFile, search_input_list)
        elif ext == "sh" or ext == "csh":
            search_input_list=[(t, pKey1,pKey2,re.IGNORECASE) for t in targets]
            map_result_list = p.map(jbmst_search_sh.wrapSearchByFile, search_input_list)
        elif ext == "ddl" or ext == "sql":
            search_input_list=[(t, pKey1,pKey2,False,True,"--",re.IGNORECASE) for t in targets]
            map_result_list = p.map(jbmst_search_sql.wrapSearchByFile, search_input_list)
        else:
            search_input_list=[(t, pKey1,pKey2) for t in targets]
            map_result_list = p.map(jbmst_search_default.wrapSearchByFile, search_input_list)

        for result_tuple in map_result_list:
            if len(result_tuple[0]) != 0:
                common_module.print_csv(pNo, pPriority, pFlag, result_tuple[1],result_tuple[0], pChapterNo, pCheck_Status)
   