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
import codecs
import sqlparse




"""
検証条件の入力値チェック.

@param pKey1
@param pKey2
@return result
"""
def paramCheck(pKey1,pKey2):

    #パラメータチェック
    if (len(pKey1) > 0):
        return 0
    return 1

"""
検索エラーファイル設定.

"""
def getErrorFilePath():
    """ エラー発生時の検索対象ファイル"""
    global g_targetFilePath
    return g_targetFilePath


"""
検索.

@param pSeachKey
@param pLine
@return
"""
def searchByLine(pSeachKey,pLine):
    m = re.findall(pSeachKey,pLine,flags=re.IGNORECASE)
    if m:
        hit = len(m)
        if hit != 0:
            return hit
    return None

"""
検索.

@param key1
@param key2
@param target_line
@param key1_skip
@return
"""
def search(key1,key2,target_line,key1_skip=False):
    if key1_skip == True:
        return searchByLine(key2,target_line)
    return searchByLine(key1,target_line)


"""
文字コードutf-8以外も対応可能なように、decode処理を行い、encodingを特定する.

@param data
@return
"""
def check_encoding(data):
    lookup = ('utf_8', 'euc_jp', 'euc_jis_2004', 'euc_jisx0213',
            'shift_jis', 'shift_jis_2004','shift_jisx0213', 'ascii')
    encode = None
    for encoding in lookup:
      try:
        data = data.decode(encoding)
        encode = encoding
        break
      except:
        pass
    if isinstance(data, unicode):
        return data,encode
    else:
        raise LookupError


def isSlashComment(pLine):
    m = re.search("^\s*//",pLine)
    if m:
        return True
    return False

"""
sqlparserで取得したtoken配列から、検索対象の文字列(セミコロンまでのデータ）を取得する。
また、行番号のカウント処理を行う.

"""
def searchGroup(list,lineNum,pKey1,pKey2,hit_count,rsl_list,key1_match=False):
    global g_search_str
    global g_targetFileExt
    for item in list:
        #print unicode(item.__repr__)
        #item.is_group() method. It works when sqlparser version is 0.2.0. but It does not work  version 0.2.3
        #if item.is_group() and unicode(item.__repr__).find("of <Comment") == -1:
        if item.is_group and unicode(item.__repr__).find("of <Comment") == -1:
            #print '''group item ''',  item.__repr__
            lineNum,hit_count=searchGroup(item.tokens,lineNum,pKey1,pKey2,hit_count,rsl_list,key1_match=key1_match)
        elif unicode(item.__repr__).find("of <Single")!= -1:
            pass
            #print 'single_comment_line_break_line = ',line_break_len,unicode(item)
        elif unicode(item.__repr__).find("of <Comment") != -1:
            '''コメントなので改行マークでパースして、何レコードあるか確認'''
            line_break_len = len(unicode(item).split('\n'))
            #print 'comment_line_break_line = ',line_break_len,unicode(item)
            lineNum  = lineNum+line_break_len -1
        elif unicode(item.__repr__).find("of <Newline") != -1:
            lineNum =lineNum + 1
        else:
            #print unicode(item.__repr__)
            g_search_str += unicode(item)
            #print "target:",g_search_str,",linenum:",lineNum+1
            #print unicode(item.__repr__)

            if g_targetFileExt == 'java' or g_targetFileExt == 'c' or g_targetFileExt == 'vc':
                 if isSlashComment(g_search_str):
                    continue
            #print 'search_target_line = ',g_search_str,'lineNum=',lineNum
            #print item.__repr__

            if pKey2=="":
                match_cnt =search(pKey1,pKey2,g_search_str,key1_match)
                if match_cnt != None:
                    if hit_count != match_cnt:
                        rsl_list.append(lineNum+1)
                        hit_count = match_cnt
                        #print 'key1_match true =',g_search_str
            elif  pKey2 !="" and key1_match == False:
                match_cnt =search(pKey1,pKey2,g_search_str)
                if match_cnt != None:
                    #print 'key1_match false =',g_search_str,lineNum+1
                    key1_match = True
            elif pKey2 !="" and key1_match == True:
                match_cnt =search(pKey1,pKey2,g_search_str,key1_skip=True)
                if match_cnt != None:
                     if hit_count != match_cnt:
                        #print 'key2_match true =',g_search_str , match_cnt ,hit_count, 'num=',lineNum+1
                        rsl_list.append(lineNum+1)
                        hit_count = match_cnt

    return lineNum,hit_count


"""
・SQL/DDファイルを検索キー1、検索キー２で指定された条件で検索を行う。
・sqlparserは、セミコロンまでのデータをひとつの塊として扱い、この塊を対象に検索を行う。

例えば以下のような定義があった場合は、CREATE TABLESPACE～(INITIAL 10K NEXT 10K MINEXTENTS 1 MAXEXTENTS UNLIMITED);
までのデータをひとつの塊として、扱い検索する。第１、第２キーワードが設定された場合、第一キワードでマッチした
トークンに対して、第２キーワードでマッチした行を出力する。
　例1) 検索キワードが、CREATE\s+TABLESPACE のみであれば　1行がマッチ
  例2) 検索キワード1が、CREATE\s+TABLESPACE,検索キワード2がDEFAULT\s+STORAGE であれば　3行がマッチ

>1 CREATE TABLESPACE RASPBERRYDEMO
>2	DATAFILE 'C:\oracle\oradata\DEMO.DBF' SIZE 3M
>3	DEFAULT STORAGE
>4	(INITIAL 10K NEXT 10K MINEXTENTS 1 MAXEXTENTS UNLIMITED);


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

    global g_targetFilePath

    global g_targetFileExt

    global g_search_str
    g_search_str = u""
    rsl_list = []
    for fname in pList:
        try :
            g_targetFilePath = fname
            g_targetFileExt = common_module.getExtension(fname)
            str,encoding = None,None
            body = open(fname, 'rU').read()
            sql_data,encoding = check_encoding(body)
            parsed = sqlparse.parse(sql_data)
            line_num = 0

            for item in parsed:
                hit_count  = -1
                g_search_str = ""
                line_num,hit_count=searchGroup(item.tokens,line_num,pKey1,pKey2,hit_count,rsl_list)

            common_module.print_csv(pNo, pPriority, pFlag, fname, rsl_list, pChapterNo, pCheck_Status)
        except Exception ,ex:
            raise ex