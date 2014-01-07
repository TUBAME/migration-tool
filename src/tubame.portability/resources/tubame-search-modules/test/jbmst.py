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
        input = self.input + self._testMethodName + ".csv"
        target = self.target + self._testMethodName
        cmd = "python ..\\src\\jbmst.py " + input + " " + target
        p = subprocess.Popen(cmd, stdout=subprocess.PIPE)
        result = p.stdout.read()
        print result
        if result != "":
            self.rslt = result
            rsltList = result.split(',')
            self.rslt_number = rsltList[0].strip()
            self.rslt_filepath = rsltList[1].strip()
            self.rslt_hit = rsltList[2].strip()
            steps = rsltList[3].strip()
            if(steps != ""):
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

        
    def testTubameKnowhowReportJaToDefaultOutputDir(self):
        self.searchExecute()
        hitfile = "knowhow_result.jbm"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        #self.assertEqual(self.rslt_steps, 1)
        self.fileCompare("KnowhowDegreeOfDifficultySumCalclator_fromAp\\getresult.js","..\\..\\report\\js\\KnowhowDegreeOfDifficultySumCalclator_fromAp\\getresult.js")
        self.fileCompare("KnowhowDegreeOfDifficultySumCalclator_toAp\\getresult.js","..\\..\\report\\js\\KnowhowDegreeOfDifficultySumCalclator_toAp\\getresult.js")
        self.fileCompare("KnowhowFactorGraphSumCalclator_graph\\getresult.js","..\\..\\report\\js\\KnowhowFactorGraphSumCalclator_graph\\getresult.js")
        self.fileCompare("KnowhowFactorSumCalclator_fromAp\\getresult.js","..\\..\\report\\js\\KnowhowFactorSumCalclator_fromAp\\getresult.js")
        self.fileCompare("KnowhowFactorSumCalclator_toAp\\getresult.js","..\\..\\report\\js\\KnowhowFactorSumCalclator_toAp\\getresult.js")
        self.fileCompare("KnowhowFileCategorySumCalclator_fromAp\\getresult.js","..\\..\\report\\js\\KnowhowFileCategorySumCalclator_fromAp\\getresult.js")
        self.fileCompare("KnowhowFileCategorySumCalclator_toAp\\getresult.js","..\\..\\report\\js\\KnowhowFileCategorySumCalclator_toAp\\getresult.js")
        self.fileCompare("KnowhowMigrationItemCalclator\\getresult.js","..\\..\\report\\js\\KnowhowMigrationItemCalclator\\getresult.js")
        
    def testTubameDependsReportJaToDefaultOutputDir(self):
        self.searchExecute()
        hitfile = "depends_result.gjbm"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.fileCompare("DependsErrSumCalclator\\getresult.js","..\\..\\report\\js\\DependsErrSumCalclator\\getresult.js")
        self.fileCompare("DependsPackagePicGrapthSumCalclator\\getresult.js","..\\..\\report\\js\\DependsPackagePicGrapthSumCalclator\\getresult.js")
        self.fileCompare("DependsPackageSumCalclator\\getresult.js","..\\..\\report\\js\\DependsPackageSumCalclator\\getresult.js")
        
    def testTubameDependsReportJaToCustomOutputDir(self):
        #reportディレクトリの有無
        if os.path.isdir("c://report"):
             shutil.rmtree("c://report")
        
        self.searchExecute()
        hitfile = "depends_result.gjbm"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(True, os.path.isdir("c://report"), "レポート出力ディレクトリが生成されていない")
        
        self.assertEqual(True, os.path.isfile("c://report//TubameReport_ja.html"), "レポートファイルが生成されていない")
        self.assertEqual(True, os.path.isfile("c://report//TubameReport_en.html"), "レポートファイルが生成されていない")
        
        
    def testTubameKnowhowReportJaToCustomOutputDir(self):
        #reportディレクトリの有無
        if os.path.isdir("c://report"):
             shutil.rmtree("c://report")
        
        self.searchExecute()
        hitfile = "knowhow_result.jbm"
        self.assertEqual(str(self.rslt_filepath), self.target + self._testMethodName +"\\" + hitfile)
        self.assertEqual(int(self.rslt_hit), 1)
        self.assertEqual(True, os.path.isdir("c://report"), "レポート出力ディレクトリが生成されていない")
        
        self.assertEqual(True, os.path.isfile("c://report//TubameReport_ja.html"), "レポートファイルが生成されていない")
        self.assertEqual(True, os.path.isfile("c://report//TubameReport_en.html"), "レポートファイルが生成されていない")
        
    def testTubameReportGenerator(self):
        self.searchExecute()
        self.assertEqual(True, os.path.isfile("..\\..\\report\\js\\KnowhowDegreeOfDifficultySumCalclator_fromAp\\getresult.js"), "JSが生成されていない")
        self.assertEqual(True, os.path.isfile("..\\..\\report\\js\\KnowhowDegreeOfDifficultySumCalclator_toAp\\getresult.js"), "JSが生成されていない")
        self.assertEqual(True, os.path.isfile("..\\..\\report\\js\\KnowhowFactorGraphSumCalclator_graph\\getresult.js"), "JSが生成されていない")
        self.assertEqual(True, os.path.isfile("..\\..\\report\\js\\KnowhowFactorSumCalclator_fromAp\\getresult.js"), "JSが生成されていない")
        self.assertEqual(True, os.path.isfile("..\\..\\report\\js\\KnowhowFactorSumCalclator_toAp\\getresult.js"), "JSが生成されていない")
        self.assertEqual(True, os.path.isfile("..\\..\\report\\js\\KnowhowFileCategorySumCalclator_fromAp\\getresult.js"), "JSが生成されていない")
        self.assertEqual(True, os.path.isfile("..\\..\\report\\js\\KnowhowFileCategorySumCalclator_toAp\\getresult.js"), "JSが生成されていない")
        self.assertEqual(True, os.path.isfile("..\\..\\report\\js\\DependsErrSumCalclator\\getresult.js"), "JSが生成されていない")
        self.assertEqual(True, os.path.isfile("..\\..\\report\\js\\DependsPackagePicGrapthSumCalclator\\getresult.js"), "JSが生成されていない")
        self.assertEqual(True, os.path.isfile("..\\..\\report\\js\\DependsPackageSumCalclator\\getresult.js"), "JSが生成されていない")
        
    def testTubameReportGeneratorInputNotCsvJbm(self):
        if os.path.isdir("c://report"):
             shutil.rmtree("c://report")
        self.searchExecute()
        self.assertEqual(True, os.path.isdir("c://report"), "レポート出力ディレクトリが生成されていない")
        self.assertEqual(True, os.path.isfile("c://report//TubameReport_ja.html"), "レポートファイルが生成されていない")
        self.assertEqual(True, os.path.isfile("c://report//TubameReport_en.html"), "レポートファイルが生成されていない")
        
class JbmstTestSuite(unittest.TestSuite):
    def __init__(self):
        tests = ['testTubameReportGeneratorInputNotCsvJbm']
        unittest.TestSuite.__init__(self, map(JbmstTestCase, tests))

if __name__ == '__main__':
    #unittest.main()
    suite1 = unittest.TestLoader().loadTestsFromTestCase(JbmstTestCase)
    suite2 = unittest.makeSuite(JbmstTestCase)
    suite3 = JbmstTestSuite()
    alltests = unittest.TestSuite([suite3])
    unittest.TextTestRunner(verbosity=2).run(alltests)
    
        
        
         
            
                