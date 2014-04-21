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
    no_Results=[]

    # キー1をXPathで読める形に変形する
    xPath = pKey1
    global g_targetFilePath
    #検索対象ファイルリスト中の全ファイルを対象とする
    for fname in pList:
        try :
            g_targetFilePath = fname
            tree = etree.parse(fname) # 返値はElementTree型
            elem = tree.getroot() # ルート要素を取得(Element型)
    
            pathList = elem.xpath(xPath)
            rsl_list = []
    
            for path in pathList:
                
                rsl_list.append(path.sourceline)
    
                #検索結果の有無を確認するための一時リストオブジェクトに結果をコピー
                no_Results.extend(rsl_list)
                    #検索結果を表示する
            common_module.print_csv(pNo, pPriority, pFlag, fname, rsl_list, pChapterNo, pCheck_Status)
        except Exception ,ex:
            if 'Document is empty'  in ex.message :
                continue
            else:
               raise ex
             
            
    #結果が存在しない場合は結果なしのCSVを出力
    #if len(no_Results)==0 and len(pList)!=0:
    #    common_module.print_csv4(pNo, pPriority, pFlag,common_module.searchTarget , pChapterNo, pCheck_Status)