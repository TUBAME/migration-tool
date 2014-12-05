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

class JbmstTestCase(unittest.TestCase):

        
    def setUp(self):
        self.input = ".\\input_csv\\"
        self.target = ".\\search_target\\"
        self.rslt_number = None
        self.rslt_filepath = None
        self.rslt_hit = None
        self.rslt_steps = None
        self.rslt =None
        
    def searchExecute(self):
        print 'searchExecute'
        input = self.input + self._testMethodName + ".csv"
        target = self.target + self._testMethodName
        cmd = "python ..\\src\\jbmst.py " + input + " " + target
        
        p = subprocess.Popen(cmd, stdout=subprocess.PIPE)
        result = p.stdout.read()
        print result
        if result != "":
            self.rslt = result
            rsltList = result.split(',')
            if "," in result:
                self.rslt_number = rsltList[0].strip()
                self.rslt_filepath = rsltList[1].strip()
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
        src_path = os.path.normpath(os.path.join(base, self.target + "\\"+self._testMethodName +"\\"+src_path))
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
    
    def getInputCsvFolderDir(self,inputCsvdir):
        base = os.path.dirname(os.path.abspath(__file__))
        base2 = os.path.normpath(os.path.join(base, inputCsvdir))
        return os.path.normpath(os.path.join(base2, ".."))

    def testStandardFileSearch(self):
        self.searchExecute()
        hitfile = "hoge.txt"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        #self.assertEqual(self.rslt_steps, 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 1)

    def testStandardFile1ExpressionSearch(self):
        self.searchExecute()
        hitfile = "config.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(int(self.rslt_steps[0]), 1)

    def testStandardFile2ExpressionSearch(self):
        self.searchExecute()
        hitfile = "aaaconfig.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 1)
        
        
    def testStandardFile3ExpressionSearch(self):
        self.searchExecute()
        hitfile = "web.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 1)

    def testKeyword1XMLFileSearch(self):
        self.searchExecute()
        
        hitfile = "web.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 4)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 12)
        self.assertEqual(int(self.rslt_steps[1]), 12)
        self.assertEqual(int(self.rslt_steps[2]), 17)
        self.assertEqual(int(self.rslt_steps[3]), 17)

    def testKeyword2XMLFileSearch(self):
        self.searchExecute()
        hitfile = "web.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 8)

    def testKeyword1JSPFileSearch(self):
        self.searchExecute()
        hitfile = "AlarmSearchControl.jsp"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 2)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 99)
        self.assertEqual(int(self.rslt_steps[1]), 177)

    def testKeyword2JSPFileSearch(self):
        self.searchExecute()
        hitfile = "AlarmSearchControl.jsp"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 8)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 99)
        self.assertEqual(int(self.rslt_steps[1]), 99)
        self.assertEqual(int(self.rslt_steps[2]), 137)
        self.assertEqual(int(self.rslt_steps[3]), 137)
        self.assertEqual(int(self.rslt_steps[4]), 173)
        self.assertEqual(int(self.rslt_steps[5]), 173)
        self.assertEqual(int(self.rslt_steps[6]), 177)
        self.assertEqual(int(self.rslt_steps[7]), 177)

    def testKeyword1JavaFileSearch(self):
        self.searchExecute()
        hitfile = "MedRecXMLProcessor.java"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 189)

    def testKeyword2JavaFileSearch(self):
        self.searchExecute()
        hitfile = "MedRecXMLProcessor.java"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 2)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 114)
        self.assertEqual(int(self.rslt_steps[1]), 114)

    def testKeywordRegularExpressionJavaFileSearch(self):
        self.searchExecute()
        hitfile = "Hoge.java"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 2)

    def testKeywordRegularExpressionJSPFileSearch(self):
        self.searchExecute()
        hitfile = "Meta.jsp"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 1)

    def testKeywordRegularExpressionXMLFileSearch(self):
        self.searchExecute()
        hitfile = "web.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
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
        
    def testTubameFrameworkKnowhowReportJaNotIncludeModelFactor(self):
        body = "1,*.jbm,%s,,ext_report_generator.py,Unknown,," % self.getReportPath()
        f = codecs.open(self.getBasePath()+"/input_csv/"+self._testMethodName+".csv", "w", "utf-8")
        f.write(body)
        f.close()
        
        reportTypeJsonFile= self.getPluginReportTplPath()
        if os.path.exists(reportTypeJsonFile): 
            os.remove(reportTypeJsonFile)

  
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
     
    def testTubameStrutsKnowhowReportJaToCustomOutputDir(self):
        
        body = "1,*.jbm,%s,,ext_report_generator.py,Unknown,," % self.getReportPath()
        f = codecs.open(self.getBasePath()+"/input_csv/"+self._testMethodName+".csv", "w", "utf-8")
        f.write(body)
        f.close()
        
        reportTypeJsonFile= self.getPluginReportTplPath()
        if os.path.exists(reportTypeJsonFile): 
            os.remove(reportTypeJsonFile)

  
        body = "{ \"template\" : \"struts\" }"
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
        

    def testTubameReportGeneratorInputNotCsvJbm(self):
        print "testTubameReportGeneratorInputNotCsvJbm start"
        body = "1,*.jbm,%s,,ext_report_generator.py,Unknown,," % self.getReportPath()
        f = codecs.open(self.getBasePath()+"/input_csv/"+self._testMethodName+".csv", "w", "utf-8")
        f.write(body)
        f.close()
        
        #reportディレクトリの有無
        if os.path.isdir(self.getReportPath()):
             shutil.rmtree(self.getReportPath())
             
        self.searchExecute()
        self.assertEqual(True, os.path.isdir(self.getReportPath()), "レポート出力ディレクトリが生成されていない")
        self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameReport_ja.html"), "レポートファイルが生成されていない")
        #self.assertEqual(True, os.path.isfile(self.getReportPath()+"//TubameReport_en.html"), "レポートファイルが生成されていない")
        
    def testTubameXpathSearchSearchParamEscape(self):
        try:
            self.searchExecute()
        except Exceptin as e:
            print 'occur error!!!!!'
        hitfile = "hoge.xml"
#         self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
#         self.assertEqual(int(self.rslt_hit), 3)
#         self.assertNotEqual(self.rslt_steps, None)
#         self.assertEqual(int(self.rslt_steps[0]), 3)
#         self.assertEqual(int(self.rslt_steps[1]), 4)
#         self.assertEqual(int(self.rslt_steps[2]), 5)
        
        
    def testTubameXpathSearchUsedSchemaFind(self):
        self.searchExecute()
        hitfile = "weblogic.xml"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_steps[0]), 3)
        
    def testTubameXpathSearchSearchInvalidParam(self):
        self.searchExecute()
        self.assertEqual(self.rslt_steps, None)
        
    def testTubameXpathSearchUsedSchemaNotFind(self):
        self.searchExecute()
        self.assertNotEqual(self.rslt_steps, None)
        self.assertEqual(int(self.rslt_hit), 1)
        
    def testTubameXpathSearchUsedEntityXml(self):
        #self.assertRaises(Exception, self.searchExecute())
        self.searchExecute()
        self.assertEqual(self.rslt_steps, None)
            
        #self.assertEqual(int(self.rslt_hit), 1)
        
    def testIgnoreXMLFileSearch(self):
        body = "search_target\\testIgnoreXMLFileSearch\\web.xml"
        f = codecs.open("../src/ignore.list", "w", "utf-8")
        f.write(body)
        f.close()
        self.searchExecute()
        self.assertEqual(self.rslt_hit, None)
        os.remove("../src/ignore.list")
    
    def testExtSearchXmlDefinedclass(self):
        self.searchExecute()
        hitfile = "UploadAction.java"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        
    def testExtSearchXmlDefinedclass2(self):
        self.searchExecute()
        hitfile = "ImageAction.java"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\exercise\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertNotEqual(self.rslt_steps, None)
        
class JbmstTestSuite(unittest.TestSuite):
    def __init__(self):
        tests = ['testTubameFrameworkKnowhowReportJaNotIncludeModelFactor']
        unittest.TestSuite.__init__(self, map(JbmstTestCase, tests))

if __name__ == '__main__':
    #unittest.main()
#     suite1 = unittest.TestLoader().loadTestsFromTestCase(JbmstTestCase)
#     suite2 = unittest.makeSuite(JbmstTestCase)
    suite1 = JbmstTestSuite()
    alltests = unittest.TestSuite([suite1])
    unittest.TextTestRunner(verbosity=2).run(alltests)
    
         
            
                