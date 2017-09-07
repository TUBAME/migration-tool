# -*- coding: utf-8 -*-
"""
[概 要]XMLファイルのタグの値(text)を検索する
[詳 細]第一引数のXPathにマッチするノード群を対象に、textの値が、第２引数のキーワードにマッチするか検索を実施する。
[備 考]第２引数のキーワードは大文字、小文字を区別しない。
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
    if len(pKey1) > 0 and len(pKey2) > 0:
        return 0
    return 1

"""
・エラーがあったファイルパスを取得する
"""
def getErrorFilePath():
    global g_targetFilePath
    return g_targetFilePath

def override_result_path_handle(path,pKey2):
    result_list = []
    if path.text != None and pKey2 !="":
        m=re.findall(pKey2.upper(),path.text.replace('\n','').upper())
        if m:
            for hit in m:
                result_list.append(path.sourceline)
    return result_list

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
    ext_search_xml_xpath = sys.modules["extend.ext_search_xml_xpath"]
    ext_search_xml_xpath.result_path_handle=override_result_path_handle
    ext_search_xml_xpath.ext_search(pNo, pPriority, pFlag, pList, pKey1, pKey2, pInputCsv, pTargetDir, pChapterNo, pCheck_Status)