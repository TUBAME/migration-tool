# -*- coding: utf-8 -*-
'''
Created on 2013/12/18

@author: kwatanabe
'''

import sys
import unittest
import subprocess
import os
import codecs
import shutil
from multiprocessing import Pool, freeze_support



class JbmstTestCase(unittest.TestCase):

        
    def setUp(self):
        self.input = "./input_csv/"
        self.target = "./search_target/"
        self.rslt_number = None
        self.rslt_filepath = None
        self.rslt_hit = None
        self.rslt_steps = None
        self.rslt =None

    def getResult(self,num):
        if self.rslt != None:
            lineTokens= self.rslt.split('\n')
            token = lineTokens[num].split(',')
            return token[1],token[2]

    def searchExecute(self):
        print 'searchExecute'
        input = self.input + self._testMethodName + ".csv"
        target = self.target + self._testMethodName
        cmd = "python ../src/jbmst.py " + input + " " + target
        freeze_support()
        p = subprocess.Popen(cmd, stdout=subprocess.PIPE,shell=True)
        result = p.stdout.read()
        print result
        if result != "":
            self.rslt = result
            rsltList = result.split(',')
            if "," in result:
                self.rslt_number = rsltList[0].strip()
                self.rslt_filepath = self.getNormpath(rsltList[1].strip())
                self.rslt_hit = rsltList[2].strip()
                steps = rsltList[3].strip()
                     
                if(steps != ""):
                    if(steps == 1):
                        self.rslt_steps = [1]
                    else:
                        self.rslt_steps = steps.split(' ')
                else:
                    self.rslt_steps = None


    def fileCompare(self,src_path,dest_path):
        base = os.path.dirname(os.path.abspath(__file__))
        src_path = os.path.normpath(os.path.join(base, self.target + "/"+self._testMethodName +"/"+src_path))
        dest_path = os.path.normpath(os.path.join(base, dest_path))
        src_f = codecs.open(src_path, "r", "utf-8")
        dest_f = codecs.open(dest_path, "r", "utf-8")
        self.assertEqual(src_f.read(), dest_f.read())

    def getBasePath(self):
        base = os.path.dirname(os.path.abspath(__file__))
        return base
    
    def getResoucePath(self):
        base = os.path.dirname(os.path.abspath(__file__))
        testResouceDir = os.path.normpath(os.path.join(base, "resource"))
        return testResouceDir
    
    def getReportPath(self):
        base = os.path.dirname(os.path.abspath(__file__))
        testResouceDir = os.path.normpath(os.path.join(base, "resource//report"))
        return testResouceDir




    def getPluginReportTplPath(self):
        base = os.path.dirname(os.path.abspath(__file__))
        return os.path.normpath(os.path.join(base, "../../report/.report_tpl.json"))

    def getPluginReportTplDir(self):
        base = os.path.dirname(os.path.abspath(__file__))
        return os.path.normpath(os.path.join(base, "../../report"))

    def getInputCsvFolderDir(self,inputCsvdir):
        base = os.path.dirname(os.path.abspath(__file__))
        base2 = os.path.normpath(os.path.join(base, inputCsvdir))
        return os.path.normpath(os.path.join(base2, ".."))

    def getNormpath(self,target):
        target = os.path.normpath(target)
        return "./"+target.replace("\\",'/')

    def testStandardFileSearch(self):
        self.searchExecute()
        hitfile = "hoge.txt"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        #self.assertEqual(self.rslt_steps, 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 1)

    def testStandardFile1ExpressionSearch(self):
        self.searchExecute()
        hitfile = "config.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(int(self.rslt_steps[0]), 1)

    def testStandardFile2ExpressionSearch(self):
        self.searchExecute()
        hitfile = "aaaconfig.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 1)
        
        
    def testStandardFile3ExpressionSearch(self):
        self.searchExecute()
        hitfile = "web.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 1)

    def testKeyword1XMLFileSearch(self):
        self.searchExecute()
        
        hitfile = "web.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 4)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 12)
        self.assertEqual(int(self.rslt_steps[1]), 12)
        self.assertEqual(int(self.rslt_steps[2]), 17)
        self.assertEqual(int(self.rslt_steps[3]), 17)
        
    def testKeyword1Windows31JXMLFileSearch(self):
        self.searchExecute()
       
        hitfile = "struts-config.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 6)
        
        

    def testKeyword2XMLFileSearch(self):
        self.searchExecute()
        hitfile = "web.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 8)

    def testKeyword1JavaFileSearch(self):
        self.searchExecute()
        hitfile = "MedRecXMLProcessor.java"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 189)

    def testKeyword2JavaFileSearch(self):
        self.searchExecute()
        hitfile = "MedRecXMLProcessor.java"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 2)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 114)
        self.assertEqual(int(self.rslt_steps[1]), 114)

    def testKeywordRegularExpressionJavaFileSearch(self):
        self.searchExecute()
        hitfile = "Hoge.java"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 2)



    def testKeywordRegularExpressionXMLFileSearch(self):
        self.searchExecute()
        hitfile = "web.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 2)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 32)
        self.assertEqual(int(self.rslt_steps[1]), 32)
        
    
    def testTubameDependsReportJaToCustomOutputDir(self):
        body = "1,*.gjbm,%s,,ext_report_generator.py,Unknown,," % self.getReportPath()
        f = codecs.open(self.getBasePath()+"/input_csv/"+self._testMethodName+".csv", "w", "utf-8")
        f.write(body)
        f.close()
        
        #reportディレクトリの有無
        if os.path.isdir(self.getReportPath()):
             shutil.rmtree(self.getReportPath())
         
        self.searchExecute()
        hitfile = "depends_result.gjbm"
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(True, os.path.isdir(self.getReportPath()), "レポート出力ディレクトリが生成されていない")
        self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameReport_ja.html"), "レポートファイルが生成されていない")
        self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameReport_en.html"), "レポートファイルが生成されていない")
        self.assertEqual(False, os.path.isfile(self.getReportPath()+"//TubameMVCFrameworkReport_ja.html"), "関係ないレポートファイルが生成されている")
        self.assertEqual(False, os.path.isfile(self.getReportPath()+"//TubameMVCFrameworkReport_en.html"), "関係ないレポートファイルが生成されている")
         
    def testTubameKnowhowReportJaToCustomOutputDir(self):
        body = "1,*.jbm,%s,,ext_report_generator.py,Unknown,," % self.getReportPath()
        f = codecs.open(self.getBasePath()+"/input_csv/"+self._testMethodName+".csv", "w", "utf-8")
        f.write(body)
        f.close()
        
        #reportディレクトリの有無
        if os.path.isdir(self.getReportPath()):
             shutil.rmtree(self.getReportPath())
         
        self.searchExecute()
        hitfile = "knowhow_result.jbm"
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(True, os.path.isdir(self.getReportPath()), "レポート出力ディレクトリが生成されていない")
        self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameReport_ja.html"), "レポートファイルが生成されていない")
        self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameReport_en.html"), "レポートファイルが生成されていない")
         
    def testTubameFrameworkKnowhowReportJaToCustomOutputDir(self):
        
        body = "1,*.jbm,%s,,ext_report_generator.py,Unknown,," % self.getReportPath()
        f = codecs.open(self.getBasePath()+"/input_csv/"+self._testMethodName+".csv", "w", "utf-8")
        f.write(body)
        f.close()
        
        reportTypeJsonFile= self.getPluginReportTplPath()
        if os.path.exists(reportTypeJsonFile): 
            os.remove(reportTypeJsonFile)

        if not os.path.exists(self.getPluginReportTplDir()):
            os.mkdir(self.getPluginReportTplDir())

        body = "{ \"template\" : \"mvc\" }"
        f = codecs.open(reportTypeJsonFile, "w", "utf-8")
        f.write(body)
        f.close()
        
        #reportディレクトリの有無
        if os.path.isdir(self.getReportPath()+"/report"):
             shutil.rmtree(self.getReportPath()+"/report")
        
        
        self.searchExecute()
        hitfile = "knowhow_result.jbm"
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(True, os.path.isdir(self.getReportPath()), "レポート出力ディレクトリが生成されていない")
        self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameMVCFrameworkReport_ja.html"), "レポートファイルが生成されていない")
        self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameMVCFrameworkReport_en.html"), "レポートファイルが生成されていない")
        self.assertEqual(False, os.path.isfile(self.getReportPath()+"//TubameReport_ja.html"), "関係ないレポートファイルが生成されている")
        self.assertEqual(False, os.path.isfile(self.getReportPath()+"//TubameReport_en.html"), "関係ないレポートファイルが生成されている")
        
        os.remove(reportTypeJsonFile)

        if os.path.isdir(self.getPluginReportTplDir()):
             shutil.rmtree(self.getPluginReportTplDir())
        
    def testTubameFrameworkKnowhowReportJaNotIncludeModelFactor(self):
        body = "1,*.jbm,%s,,ext_report_generator.py,Unknown,," % self.getReportPath()
        f = codecs.open(self.getBasePath()+"/input_csv/"+self._testMethodName+".csv", "w", "utf-8")
        f.write(body)
        f.close()
        
        reportTypeJsonFile= self.getPluginReportTplPath()
        if os.path.exists(reportTypeJsonFile): 
            os.remove(reportTypeJsonFile)

  
        body = "{ \"template\" : \"mvc\" }"

        #reportディレクトリの有無
        if not os.path.exists(self.getPluginReportTplDir()):
            os.mkdir(self.getPluginReportTplDir())


        f = codecs.open(reportTypeJsonFile, "w", "utf-8")
        f.write(body)
        f.close()
        
        self.searchExecute()
        hitfile = "knowhow_result.jbm"
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(True, os.path.isdir(self.getReportPath()), "レポート出力ディレクトリが生成されていない")
        self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameMVCFrameworkReport_ja.html"), "レポートファイルが生成されていない")
        self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameMVCFrameworkReport_en.html"), "レポートファイルが生成されていない")
        self.assertEqual(False, os.path.isfile(self.getReportPath()+"//TubameReport_ja.html"), "関係ないレポートファイルが生成されている")
        self.assertEqual(False, os.path.isfile(self.getReportPath()+"//TubameReport_en.html"), "関係ないレポートファイルが生成されている")
        
        os.remove(reportTypeJsonFile)
        #reportディレクトリの有無
        # if os.path.isdir(self.getReportPath()+"/report"):
        #      shutil.rmtree(self.getReportPath()+"/report")

        if os.path.isdir(self.getPluginReportTplDir()):
             shutil.rmtree(self.getPluginReportTplDir())

    def testTubameStrutsKnowhowReportJaToCustomOutputDir(self):
        
        body = "1,*.jbm,%s,,ext_report_generator.py,Unknown,," % self.getReportPath()
        f = codecs.open(self.getBasePath()+"/input_csv/"+self._testMethodName+".csv", "w", "utf-8")
        f.write(body)
        f.close()
        
        reportTypeJsonFile= self.getPluginReportTplPath()
        if os.path.exists(reportTypeJsonFile): 
            os.remove(reportTypeJsonFile)

  
        body = "{ \"template\" : \"struts\" }"

        #reportディレクトリの有無
        if not os.path.exists(self.getPluginReportTplDir()):
            os.mkdir(self.getPluginReportTplDir())

        f = codecs.open(reportTypeJsonFile, "w", "utf-8")
        f.write(body)
        f.close()
        
        #reportディレクトリの有無
        if os.path.isdir(self.getReportPath()+"/report"):
             shutil.rmtree(self.getReportPath()+"/report")
        
        self.searchExecute()
        hitfile = "knowhow_result.jbm"
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(True, os.path.isdir(self.getReportPath()), "レポート出力ディレクトリが生成されていない")
        self.assertEqual(False, os.path.isfile(self.getReportPath()+"//TubameMVCFrameworkReport_ja.html"), "関係ないレポートファイルが生成されている")
        self.assertEqual(False, os.path.isfile(self.getReportPath()+"//TubameMVCFrameworkReport_en.html"), "関係ないレポートファイルが生成されている")
        self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameStrutsFrameworkReport_ja.html"), "レポートファイルが生成されていない")
        self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameStrutsFrameworkReport_en.html"), "レポートファイルが生成されていない")
         

        os.remove(reportTypeJsonFile)
        if os.path.isdir(self.getPluginReportTplDir()):
             shutil.rmtree(self.getPluginReportTplDir())


    def testTubameKnowhowReportIncludeNotTran(self):
        body = "1,*.jbm,%s,,ext_report_generator.py,Unknown,," % self.getReportPath()
        f = codecs.open(self.getBasePath()+"/input_csv/"+self._testMethodName+".csv", "w", "utf-8")
        f.write(body)
        f.close()

        reportTypeJsonFile= self.getPluginReportTplPath()
        if os.path.exists(reportTypeJsonFile):
            os.remove(reportTypeJsonFile)

        if not os.path.exists(self.getPluginReportTplDir()):
            os.mkdir(self.getPluginReportTplDir())

        # body = "{ \"template\" : \"ap\" }"
        # f = codecs.open(reportTypeJsonFile, "w", "utf-8")
        # f.write(body)
        # f.close()

        #reportディレクトリの有無
        if os.path.isdir(self.getReportPath()+"/report"):
             shutil.rmtree(self.getReportPath()+"/report")


        self.searchExecute()
        hitfile = "result.jbm"
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(True, os.path.isdir(self.getReportPath()), "レポート出力ディレクトリが生成されていない")
        self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameReport_ja.html"), "関係ないレポートファイルが生成されている")
        self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameReport_en.html"), "関係ないレポートファイルが生成されている")



        if os.path.isdir(self.getPluginReportTplDir()):
            shutil.rmtree(self.getPluginReportTplDir())



    def testTubameXpathSearchSearchParamEscape(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 3)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 3)
        self.assertEqual(int(self.rslt_steps[1]), 4)
        self.assertEqual(int(self.rslt_steps[2]), 5)
        
        
    def testTubameXpathSearchUsedSchemaFind(self):
        self.searchExecute()
        hitfile = "weblogic.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 3)
        
    # def testTubameXpathSearchSearchInvalidParam(self):
    #     self.assertRaises(Exception,self.searchExecute())
    #     self.assertEqual(self.rslt_steps, None)
        
    def testTubameXpathSearchUsedSchemaNotFind(self):
        self.searchExecute()
        #検索対象のxmlファイルで外部スキーマを参照している場合は、local-nameを利用して検索する必要がある.
        #lxmlでtree.fromstring(xmlcontent)の場合はlocal-nameが必須で、html.fromstring(xmlcontent)の場合は
        #local-nameは必須ではない。本来は、xpath的にはlocal-nameを省略した指定のみで検索したかったが、mac環境のみhtml.fromstring
        #を利用した場合に、soureline(行カウント)が取れなくなったため、tree.fromstringに修正した。
        # http://stackoverflow.com/questions/3538248/any-one-have-an-example-that-uses-the-element-sourceline-method-from-lxml-html
        self.assertEqual(self.rslt_steps, None)
        
    def testTubameXpathSearchUsedEntityXml(self):
        self.assertRaises(Exception,self.searchExecute())
        self.assertEqual(self.rslt_steps, None)

    def testTubameXpathSearchBodyEmptyXml(self):
        self.searchExecute()
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/weblogic.xml")
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(int(self.rslt_steps[0]), 1)

    def testIgnoreXMLFileSearch(self):
        body = "search_target/testIgnoreXMLFileSearch/web.xml"
        f = codecs.open("../src/ignore.list", "w", "utf-8")
        f.write(body)
        f.close()
        self.searchExecute()
        self.assertEqual(self.rslt_hit, None)
        os.remove("../src/ignore.list")
    
    def testExtSearchXmlDefinedclass(self):
        self.searchExecute()
        hitfile = "UploadAction.java"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)


        
    def testExtSearchXmlDefinedclass2(self):
        self.searchExecute()
        hitfile = "ImageAction.java"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/exercise/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)


    def testExtSearchXmlDefinedclass3(self):
        self.searchExecute()
        hitfile = "UploadAction.java"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"/sub1/" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        result_line2 =self.rslt.split('\n')[2]
        self.assertNotEqual(result_line2, None)
        self.assertNotEqual(result_line2, "")
        self.assertEqual(str(self.getNormpath(result_line2.split(',')[1])), self.target + self._testMethodName +"/sub2/" + hitfile)

    def testExtSearchXmlDefinedclassFileNotFound(self):
        self.searchExecute()
        self.assertEqual(self.rslt_steps, None)

    def testTubameSqlSearchKey1CreateTable(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 38)

    def testTubameSqlSearchKey1CreateTableKey2Varchar2(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 92)

    def testTubameSqlSearchKey1AlterTableKey2AddConstraint(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 47)
        self.assertEqual(int(self.rslt_steps[0]), 402)
        self.assertEqual(int(self.rslt_steps[20]), 500)
        self.assertEqual(int(self.rslt_steps[46]), 629)

    def testTubameCSearchKeyword1(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 48)

    def testTubameCSearchKeyword2(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 7)
        self.assertEqual(int(self.rslt_steps[0]), 16)
        self.assertEqual(int(self.rslt_steps[1]), 18)
        self.assertEqual(int(self.rslt_steps[2]), 20)
        self.assertEqual(int(self.rslt_steps[3]), 23)
        self.assertEqual(int(self.rslt_steps[4]), 25)
        self.assertEqual(int(self.rslt_steps[5]), 47)
        self.assertEqual(int(self.rslt_steps[6]), 52)

    def testTubameCSearchKeyword3(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(int(self.rslt_steps[0]), 49)

    def testTubamePropertiesSearchKeyword1(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(int(self.rslt_steps[0]), 2)

    def testTubamePropertiesSearchKeyword2(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 2)
        self.assertEqual(int(self.rslt_steps[0]), 5)
        self.assertEqual(int(self.rslt_steps[1]), 13)

    def testTubameShellSearchKeyword1(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 7)
        self.assertEqual(int(self.rslt_steps[0]), 19)
        self.assertEqual(int(self.rslt_steps[1]), 20)
        self.assertEqual(int(self.rslt_steps[2]), 29)
        self.assertEqual(int(self.rslt_steps[3]), 33)
        self.assertEqual(int(self.rslt_steps[4]), 51)
        self.assertEqual(int(self.rslt_steps[5]), 56)
        self.assertEqual(int(self.rslt_steps[6]), 69)


    def testTubameSqlSearchKey1ForJava(self):
        self.searchExecute()
        self.assertEqual(self.rslt_steps, None)

    def testTubameSqlSearchKey1ForJava2(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(int(self.rslt_steps[0]), 773)

    def testTubameSqlSearchKey1ForJava3(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(int(self.rslt_steps[0]), 669)

    def testTubameSqlSearchKey1ForJava4(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(int(self.rslt_steps[0]), 773)

    def testTubameSqlSearchForProc1(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_steps[0]), 142)
        self.assertEqual(int(self.rslt_steps[1]), 145)

    def testTubameSqlSearch1ForXml(self):
        self.searchExecute()


    def testTubameStepCounterSearchForJava(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_steps[0]), 571)

    def testTubameStepCounterSearchForJsp(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_steps[0]), 7)

    def testTubameStepCounterSearchForProperties(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_steps[0]), 10)

    def testTubameStepCounterSearchForXml(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_steps[0]), 31)

    def testTubameStepCounterSearchForSql(self):
        self.searchExecute()


    def testTubameSqlSearchForProc2(self):
    #
    #In sqlparse, if there is a single quotation in the comment statement, the content of the comment statement
    #It can not be taken normally.
    #
    # Example of events.
    #
    #---
    # // If it was too long, there's be no newline. In that case, we flush
    # // To end of line so that it does not affect affect the next call.
    #----
    # Acquire missing information as follows.
    #---
    #// If it was too long, theret affect the next call.
    #---
    #Due to this event, line numbers may be shifted when searching.
    #
    #This test confirms that the line numbers are off.
    #
        self.searchExecute()
        #self.assertEqual(int(self.rslt_steps[0]), 142)
        self.assertEqual(int(self.rslt_steps[0]), 139)
        #self.assertEqual(int(self.rslt_steps[1]), 145)
        self.assertEqual(int(self.rslt_steps[1]), 141)

    def testKeyword1SqlFileSearch(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 92)

    def testKeyword1DDLFileSearch(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_steps[0]), 442)
        self.assertEqual(int(self.rslt_steps[1]), 530)

    def testTubameXpathSearchIBatisSql(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_steps[0]), 11)

    def testTubameSqlSearchOrCondition(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_steps[0]), 1)
        self.assertEqual(int(self.rslt_steps[1]), 2)
        self.assertEqual(int(self.rslt_steps[2]), 2)

    def testExtSearchInterfaceMethod(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_steps[0]), 26)

    def testExtSearchXmlText(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_steps[0]), 22)

    def testTubameJavaApiSearch(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_steps[0]), 23)
        self.assertEqual(int(self.rslt_steps[1]),37)

    def testTubameJavaApiSearchNotMatch(self):
        self.searchExecute()
        self.assertEqual(self.rslt_steps, None)

    def testExtSqlSearchKey1CreateTableKey2Varchar2(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 8)
        self.assertEqual(int(self.rslt_steps[0]), 10)
        self.assertEqual(int(self.rslt_steps[1]), 24)
        self.assertEqual(int(self.rslt_steps[2]), 25)
        self.assertEqual(int(self.rslt_steps[3]), 27)
        self.assertEqual(int(self.rslt_steps[4]), 28)
        self.assertEqual(int(self.rslt_steps[5]), 30)
        self.assertEqual(int(self.rslt_steps[6]), 33)
        self.assertEqual(int(self.rslt_steps[7]), 36)

    def testExtSqlSearchKey1CreateTable(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 38)
        self.assertEqual(int(self.rslt_steps[0]), 1)
        self.assertEqual(int(self.rslt_steps[37]), 387)

    def testExtSqlSearchKey1AlterTableKey2AddConstraint(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 47)
        self.assertEqual(int(self.rslt_steps[0]), 402)
        self.assertEqual(int(self.rslt_steps[20]), 500)
        self.assertEqual(int(self.rslt_steps[46]), 629)

    def testExtSqlSearchKey1ForJava(self):
        self.searchExecute()
        self.assertEqual(self.rslt_steps, None)

    def testExtSqlSearchKey1ForJava2(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(int(self.rslt_steps[0]), 774)

    def testExtSqlSearchKey1ForJava3(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(int(self.rslt_steps[0]), 670)

    def testExtSqlSearchKey1ForJava4(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(int(self.rslt_steps[0]), 774)

    def testExtSqlSearchForProc1(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_steps[0]), 142)
        self.assertEqual(int(self.rslt_steps[1]), 145)

    def testExtSqlSearchPropertiesKeyword1(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(int(self.rslt_steps[0]), 2)

    def testExtSqlSearchPropertiesKeyword2(self):
        self.searchExecute()
        self.assertEqual(int(self.rslt_hit), 2)
        self.assertEqual(int(self.rslt_steps[0]), 5)
        self.assertEqual(int(self.rslt_steps[1]), 13)

    def testExtSqlSearchForManyExt(self):
        self.searchExecute()
        match_file, match_cnt =self.getResult(1)
        self.assertTrue(match_file.endswith('readchar.c'))
        self.assertEqual(int(match_cnt), 1)

        match_file, match_cnt =self.getResult(2)
        self.assertTrue(match_file.endswith('JDBCStore.java'))
        self.assertEqual(int(match_cnt), 3)

        match_file, match_cnt =self.getResult(3)
        self.assertTrue(match_file.endswith('test.h'))
        self.assertEqual(int(match_cnt), 1)

        match_file, match_cnt =self.getResult(4)
        self.assertTrue(match_file.endswith('test.sql'))
        self.assertEqual(int(match_cnt), 1)

        match_file, match_cnt =self.getResult(5)
        self.assertTrue(match_file.endswith('hello.pc'))
        self.assertEqual(int(match_cnt), 2)

        match_file, match_cnt =self.getResult(6)
        self.assertTrue(match_file.endswith('test.sh'))
        self.assertEqual(int(match_cnt), 2)

        match_file, match_cnt =self.getResult(7)
        self.assertTrue(match_file.endswith('test.ddl'))
        self.assertEqual(int(match_cnt), 1)

        match_file, match_cnt =self.getResult(8)
        self.assertTrue(match_file.endswith('test.cpp'))
        self.assertEqual(int(match_cnt), 2)

        match_file, match_cnt =self.getResult(9)
        self.assertTrue(match_file.endswith('sql.properties'))
        self.assertEqual(int(match_cnt), 3)

class JbmstTestSuite(unittest.TestSuite):
    def __init__(self):
        tests = ['testTubameSqlSearch1ForXml']
        unittest.TestSuite.__init__(self, map(JbmstTestCase, tests))

if __name__ == '__main__':
    #run all test
    #unittest.main()
    #suite = unittest.TestLoader().loadTestsFromTestCase(JbmstTestCase)
    #unittest.TextTestRunner(verbosity=2).run(suite)

    #run specfic test
    suite2 = JbmstTestSuite()
    alltests = unittest.TestSuite([suite2])
    unittest.TextTestRunner(verbosity=2).run(alltests)
