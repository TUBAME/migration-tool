# -*- coding: utf-8 -*-
"""
[概 要]
[詳 細]
[備 考]
[環 境] Python 2.7
"""

__author__ = ' Copyright (C) 2011 Nippon Telegraph and Telephone Corporation, All rights reserved.'

import os
import sys
import re
import csv
import abc
import codecs
import locale
import shutil
import json

from lxml import etree


import ext_report_generator

class CalcModel(object):
    """Model class for the calculation.
    
    """
    
    def __init__(self,label,hitCount):
        '''
        Constructor
        '''
        self.label = label
        self.hitCount =hitCount


class KnowledgeBaseCalcModel(CalcModel):
    """Calc Model of knowledge-based search.
    
    """
    
    def __init__(self,label,calcCount,factor,ext,id,hit,line_basic,manual_check_status):
        '''
        Constructor
        '''
        CalcModel.__init__(self,label,calcCount)
        self.factor = factor
        self.ext = ext
        self.id = id
        self.hit = hit
        self.line_basic = line_basic
        self.manual_check_status = manual_check_status

class DependsCalcModel(CalcModel):
    """Calc Model of Dependence Search.
    
    """
    
    def __init__(self,label,hitCount,package,classname,ext):
        '''
        Constructor
        '''
        CalcModel.__init__(self,label,hitCount)
        if package == "":
            package ="UnknownPackage"
        self.package = package
        if classname== "":
            classname = "UnknownClassName"
        self.classname = classname
        self.ext = ext
        

class ResultJsWriter(object):
    """JsFile(getresult.js) Writer.
    
    """
    
    def __init__(self,dirname,JS_BASE_DIR="../resources/report/js/"):
        '''
        Constructor
        '''
        self.dirname=dirname
        self.JS_BASE_DIR = JS_BASE_DIR
        
    def makeDir(self,path):
        
        base = os.path.dirname(os.path.abspath(__file__))
        dirname = os.path.normpath(os.path.join(base, path))
        if os.path.isdir(dirname) == False:
            os.mkdir(dirname)
        else:
            shutil.rmtree(dirname)
            os.mkdir(dirname)
    
    
    def write(self,body,JSNAME="getresult.js"):
        pathname = self.getPath(self.JS_BASE_DIR+self.dirname+"/"+JSNAME)
        f = codecs.open(pathname, "w", "utf-8")
        f.write(body)
        f.close()
            
    def save(self,input):
        self.makeDir(self.JS_BASE_DIR+self.dirname)
        RESULT_JS_TPL1='''
        (function($) {
            $.fn.%s_getResult = function(options) {
                var result = %s
                return result;
            };
        })(jQuery);
        '''
        RESULT_JS_TPL2='''
        (function($) {
            $.fn.%s_getResult = function(options) {
                var result = %s
                return result;
            };
        })(jQuery);
        '''
        #難易度別集計 For Knowhow
        if "KnowhowDegreeOfDifficultySumCalclator" in self.dirname:
            body = RESULT_JS_TPL1 % (self.dirname,input)
        #要因別集計  For Knowhow
        elif "KnowhowFactorSumCalclator" in self.dirname:
            body = RESULT_JS_TPL1 % (self.dirname,input)
        #ファイル別集計 For Knowhow
        elif "KnowhowFileCategorySumCalclator" in self.dirname:
            body = RESULT_JS_TPL1 % (self.dirname,input)
            
        #移植項目集計用 For Knowhow
        elif "KnowhowMigrationItemCalclator" in self.dirname:
            body = self.createJsTpl3(input)
        #要因別棒グラフ For Knowhow
        elif self.dirname.startswith("KnowhowFactorGraphSumCalclator"):
            num1, num2,num3 = input['JavaEESpecChange']+input['JavaVersionUpgradeChange']+input['APlibrary']+input['DBMSChange'],input['ApServerDependsDepricatedChange'],input['WeblogicSpecChange']+input['ApServerDependsChange']
            
            if "ja" in locale.getdefaultlocale()[0]:
                result_tpl ='''[{"data": [[0, 0],[0, %s],[1, %s]], "label": "JavaSE/JavaEEバージョンアップに伴う修正"}, {"data": [[0, %s],[1, %s]], "label": "APサーバ非推奨機能に関する修正"}, {"data": [[1, %s]], "label": "APサーバ固有機能に関する修正"}]'''
                 
            else:
                result_tpl ='''[{"data": [[0, 0],[0, %s],[1, %s]], "label": "Migration Item associated with the JavaSE/JavaEE version up"}, {"data": [[0, %s],[1, %s]], "label": "Migration Item associated with deprecated function of its own application server"}, {"data": [[1, %s]], "label": "Migration Item associated with function of its own application server"}]'''
                 
            result_body = result_tpl % (num1,num1,num2,num2,num3)
            body = RESULT_JS_TPL2 % (self.dirname,result_body)
            
        elif self.dirname.startswith("FrameworkKnowhowFactorGraphSumCalclator"):
            num1, num2,num3 ,num4, num5 = input['mvcFrameworkM'],input['mvcFrameworkV'],input['mvcFrameworkC'],input['mvcFrameworkSpecificNonBackwardCompati'],input['mvcFrameworkSpecificBackwardCompati']
            
            if "ja" in locale.getdefaultlocale()[0]:
                result_tpl ='''[{"data": [[0, 0],[0, %s],[1, %s]], "label": "Model機能にともなう修正"}, {"data": [[0, %s],[1, %s]], "label": "View機能にともなう修正"}, {"data": [[0, %s],[1, %s]], "label": "Controller機能にともなう修正"},{"data": [[0, %s],[1, %s]], "label": "MVCフレーム独自機能(上位互換なし)の修正"},{"data": [[1, %s]], "label": "MVCフレーム独自機能(上位互換あり)の修正"}]'''
            else:
                result_tpl ='''[{"data": [[0, 0],[0, %s],[1, %s]], "label": "Migration Items related to porting of Model function"}, {"data": [[0, %s],[1, %s]], "label": "Migration Items  related to porting of View function"}, {"data": [[0, %s],[1, %s]], "label": "Migration Items  related to porting of Controller function"},{"data": [[0, %s],[1, %s]], "label": "Specific features of the MVC framework(Non-Backward-Compatible)"},{"data": [[1, %s]], "label": "Specific features of the MVC framework(Backward-Compatible)"}]'''
                 
            result_body = result_tpl % (num1,num1,num2,num2,num3,num3,num4,num4,num5)
            body = RESULT_JS_TPL2 % (self.dirname,result_body)
        
        elif self.dirname.startswith("StrutsFrameworkKnowhowFactorGraphSumCalclator"):
            num1, num2,num3 ,num4, num5 = input['mvcFrameworkM'],input['mvcFrameworkV'],input['mvcFrameworkC'],input['mvcFrameworkSpecificNonBackwardCompati'],input['mvcFrameworkSpecificBackwardCompati']
            
            if "ja" in locale.getdefaultlocale()[0]:
                result_tpl ='''[{"data": [[0, 0],[0, %s],[1, %s]], "label": "Model機能にともなう修正"}, {"data": [[0, %s],[1, %s]], "label": "View機能にともなう修正"}, {"data": [[0, %s],[1, %s]], "label": "Controller機能にともなう修正"},{"data": [[0, %s],[1, %s]], "label": "MVCフレーム独自機能(上位互換なし)の修正"},{"data": [[1, %s]], "label": "MVCフレーム独自機能(上位互換あり)の修正"}]'''
            else:
                result_tpl ='''[{"data": [[0, 0],[0, %s],[1, %s]], "label": "Migration Items related to porting of Model function"}, {"data": [[0, %s],[1, %s]], "label": "Migration Items  related to porting of View function"}, {"data": [[0, %s],[1, %s]], "label": "Migration Items  related to porting of Controller function"},{"data": [[0, %s],[1, %s]], "label": "Specific features of the MVC framework(Non-Backward-Compatible)"},{"data": [[1, %s]], "label": "Specific features of the MVC framework(Backward-Compatible)"}]'''
                 
            result_body = result_tpl % (num1,num1,num2,num2,0,num3,num4,num4,num5)
            body = RESULT_JS_TPL2 % (self.dirname,result_body)
        #エラー集計 For depends
        elif "DependsErrSumCalclator" in self.dirname:
            RESULT_JS_TPL='''
            (function($) {
                $.fn.%s_getResult = function(options) {
                    var result = %s
                    return result;
                };
            })(jQuery);
            '''
            body = RESULT_JS_TPL % (self.dirname,input)
            
        elif "DependsPackagePicGrapthSumCalclator" in self.dirname:
            body= self.createJsTpl('''{ data : [[0, %s]] , label : "%s" }''',input)
            
            
        elif "DependsPackageSumCalclator" in self.dirname:
            body = self.createJsTpl2('''{ data :  %s , label : "%s" , classNameResult: "%s" }''',input)
        
        elif "DependsJspUsedTldFileSumCalclator" in self.dirname:
            body = self.createJsTpl2('''{ data :  %s , label : "%s" , classNameResult: "%s" }''',input)
        
        elif "DependsXmlUsedSchemaFileSumCalclator" in self.dirname:
            body = self.createJsTpl2('''{ data :  %s , label : "%s" , classNameResult: "%s" }''',input)
        
        self.write(body)
            
  
    def createJsTpl(self,obj_tpl,calcModels):
        RESULT_JS_TPL='''
        (function($) {
            $.fn.%s_getResult = function(options) {
                var result = [ %s ]
                return result;
            };
        })(jQuery);
        '''
        result_tpl = ""
        for calcModel in calcModels:
            mergeStr = obj_tpl % (calcModel.hitCount,calcModel.label)
            result_tpl =  result_tpl + mergeStr+','
        result_tpl = result_tpl[:-1]
        return RESULT_JS_TPL % (self.dirname,result_tpl)
    
    def createJsTpl2(self,obj_tpl,calcModels):
        RESULT_JS_TPL='''
        (function($) {
            $.fn.%s_getResult = function(options) {
                var result = [ %s ]
                return result;
            };
        })(jQuery);
        '''
        result_tpl = ""
        for calcModel in calcModels:
            mergeStr = obj_tpl % (calcModel.hitCount,calcModel.label,calcModel.classSumResult)
            result_tpl =  result_tpl + mergeStr+','
        result_tpl = result_tpl[:-1]
        return RESULT_JS_TPL % (self.dirname,result_tpl)
        
    def createJsTpl3(self,calcModels):
        RESULT_JS_TPL='''
        (function($) {
            $.fn.%s_getResult = function(options) {
                var result = [ %s ]
                return result;
            };
        })(jQuery);
        '''
        OBJ_TPL = '''{ id : "%s" , major: "%s" ,minor:"%s" ,degreeDetail: "%s" ,portabilityFactor: "%s" ,guideRef: "%s" ,hitCount:%s ,stepCount:%s, lineBasic:%s ,manualCheckStatus:"%s"}'''
        result_tpl = ""
        for calcModel in calcModels:
            create_str = OBJ_TPL %( calcModel.label ,calcModel.major,calcModel.minor,calcModel.degreeDetail, calcModel.portabilityFactor, calcModel.guideRef,calcModel.hitTotal,calcModel.stepTotal,calcModel.lineBasic,calcModel.manualCheckStatus)
            result_tpl = result_tpl + create_str +","
        result_tpl = result_tpl[:-1]
        return RESULT_JS_TPL % (self.dirname,result_tpl)
    
    def getPath(self,path):
        base = os.path.dirname(os.path.abspath(__file__))
        return os.path.normpath(os.path.join(base, path))
    

        
        
class CalclatorBase(object):
    """Calclator　Base.
    
    """
    
    def __init__(self):
        '''
        Constructor
        '''
    
    @abc.abstractmethod
    def calc(self):
        """"""

class Calclator(CalclatorBase):
    """Calclator　.
    
    """
    
    def __init__(self,name,inputModels):
        '''
        Constructor
        '''
        map = {}
        for inputModel in inputModels:
            if map.has_key(inputModel.label) :
                models = map.get(inputModel.label)
                models.append(inputModel)
            else:
                list = [inputModel]
                map[inputModel.label]  = list 
        self.data = map
        self.name = name    
        self.jswriter = ResultJsWriter(name)
        
    @abc.abstractmethod
    def calcBefore(self):
        """サブクラスによって、それぞれひつような処理を実装する"""
        pass
    
    def createBasedResultMap(self,resultMap,calReuslt):
        for result in calReuslt:
            resultMap[result.label] = result.hitCount
        return resultMap
    
    @abc.abstractmethod
    def createResultMap(self,calReuslt):
        """サブクラスによって、それぞれひつような処理を実装する"""
        pass
    
    def calc(self):
        self.calcBefore()
        calcResult = []
        labels = self.data.keys()
        for label in labels:
            calcModels = self.data.get(label)
            createModel = CalcModel(label,0)
            for calcModel in calcModels:
                createModel.hitCount = int(createModel.hitCount) + int(calcModel.hitCount)
            calcResult.append(createModel)
        return sorted(calcResult, key=lambda label: label.hitCount,reverse=True) 
    
class KnowhowDegreeOfDifficultySumCalclator(Calclator):
    """Calculators of difficulty for each of the knowledge-based search.
    
    """
    def __init__(self,type,inputsModels):
        '''
        Constructor
        '''
        Calclator.__init__(self,self.__class__.__name__ + "_"+type,inputsModels)
        self.type = type

    def calcBefore(self):
        if self.type == "fromAp":
            '''factorがAP server specificのものは削除する'''
            labels = self.data.keys()
            for label in labels:
                calcModels = self.data.get(label)
                delTargets = self.getCalcModelsFromAp(calcModels)
                for delTarget in delTargets:
                    calcModels.remove(delTarget)
   
                
    def getCalcModelsFromAp(self,calcModels):
        results = []
        for calcModel in calcModels:
            if calcModel.factor == "AP server specific" or calcModel.factor =="AP サーバ固有" or calcModel.factor == "Weblogic specific" or calcModel.factor == "Weblogic 固有":
                results.append(calcModel)
        return results
    
    def createResultMap(self,calReuslt):
        resultMap ={'High':0,'Middle':0,'Low1':0,'Low2':0,'Unknown1':0,'Unknown2':0}
        return Calclator.createBasedResultMap(self, resultMap, calReuslt)
        
                
class FrameworkKnowhowDegreeOfDifficultySumCalclator(Calclator):
    """Calculators of difficulty for each of the knowledge-based search.
    
    """
    def __init__(self,type,inputsModels):
        '''
        Constructor
        '''
        Calclator.__init__(self,self.__class__.__name__ + "_"+type,inputsModels)
        self.type = type

    def calcBefore(self):
        if self.type == "fromFw" or self.type == "fromStrutsFw":
            '''factorが移行元フレームワーク独自機能(上位互換あり)及びMVCFrameworkSpecific(BackwardCompati）のものは削除する'''
            labels = self.data.keys()
            for label in labels:
                calcModels = self.data.get(label)
                delTargets = self.getDeltarget(calcModels)
                for delTarget in delTargets:
                    calcModels.remove(delTarget)
                    
        if self.type == "fromStrutsFw":
            '''strutsPlugin対応を想定し、factorがコントローラ機能のものは削除する'''
            labels = self.data.keys()
            for label in labels:
                calcModels = self.data.get(label)
                delTargets = self.getDelTargetForStrutsPlugin(calcModels)
                for delTarget in delTargets:
                    calcModels.remove(delTarget)
                
    def getDeltarget(self,calcModels):
        results = []
        for calcModel in calcModels:
            if calcModel.factor == "MVCフレームワーク独自機能(上位互換あり)" or calcModel.factor =="MVCFrameworkSpecific(BackwardCompati)":
                results.append(calcModel)
        return results
    
    def getDelTargetForStrutsPlugin(self,calcModels):
        results = []
        for calcModel in calcModels:
            if calcModel.factor == "MVCフレームワーク(Controller機能)" or calcModel.factor =="MVCFramework(Controller)":
                results.append(calcModel)
        return results
    
    def createResultMap(self,calReuslt):
        resultMap ={'High':0,'Middle':0,'Low1':0,'Low2':0,'Unknown1':0,'Unknown2':0}
        return Calclator.createBasedResultMap(self, resultMap, calReuslt)
    

class KnowhowFileCategorySumCalclator(KnowhowDegreeOfDifficultySumCalclator):
    
    def __init__(self,type,inputsModels):
        '''
        Constructor
        '''
        KnowhowDegreeOfDifficultySumCalclator.__init__(self,type,inputsModels)
        self.type = type

    def calcBefore(self):
        KnowhowDegreeOfDifficultySumCalclator.calcBefore(self)
        '''self.dataのマップにあるすべてのオブジェクトをext種別でマップしなおす'''
        self.exts = self.getAllExts()
        calcModels = self.getAllData()
        newMap ={}
        for ext in self.exts:
            list =[] 
            for calcModel in calcModels:
                if calcModel.ext == ext:
                    list.append(calcModel)
            newMap[ext] = list
        self.data = newMap
                
    def getExts(self,calcModels):
        results = []
        for model in calcModels:
            results.append(model.ext)
        return results
     
    def getAllExts(self):
        results = set([])
        
        labels = self.data.keys()
        for label in labels:
            calcModels = self.data.get(label)
            results =results.union(set(self.getExts(calcModels)))
        return results
    
    def getAllData(self):
        results = []
        labels = self.data.keys()
        for label in labels:
            calcModels = self.data.get(label)
            results = results +calcModels
        return results
    
    def createResultMap(self,calReuslt):
        resultMap = {'java':0,'jsp':0,'xml':0,'properties':0}
        exts = self.exts
        for ext in exts:
            resultMap[ext] = 0
        return Calclator.createBasedResultMap(self, resultMap, calReuslt)
   
    
class FrameworkKnowhowFileCategorySumCalclator(FrameworkKnowhowDegreeOfDifficultySumCalclator,KnowhowFileCategorySumCalclator):
    
    def __init__(self,type,inputsModels):
        '''
        Constructor
        '''
        FrameworkKnowhowDegreeOfDifficultySumCalclator.__init__(self,type,inputsModels)
        self.type = type
        
    def calcBefore(self):
        FrameworkKnowhowDegreeOfDifficultySumCalclator.calcBefore(self)
        '''self.dataのマップにあるすべてのオブジェクトをext種別でマップしなおす'''
        self.exts = self.getAllExts()
        calcModels = self.getAllData()
        newMap ={}
        for ext in self.exts:
            list =[] 
            for calcModel in calcModels:
                if calcModel.ext == ext:
                    list.append(calcModel)
            newMap[ext] = list
        self.data = newMap
        
    def createResultMap(self,calReuslt):
        resultMap = {'java':0,'jsp':0,'xml':0,'properties':0}
        exts = self.exts
        for ext in exts:
            resultMap[ext] = 0
        return Calclator.createBasedResultMap(self, resultMap, calReuslt)
    
class KnowhowFactorSumCalclator(KnowhowDegreeOfDifficultySumCalclator):
    """Calculators of factor for the knowledge-based search.
    
    """
    
    def __init__(self,type,inputsModels):
        '''
        Constructor
        '''
        KnowhowDegreeOfDifficultySumCalclator.__init__(self,type,inputsModels)
        self.type = type
        
    
    def createResultMap(self,calReuslt):
        resultMap ={'WeblogicSpecChange':0,'ApServerDependsChange':0,'ApServerDependsDepricatedChange':0,'JavaEESpecChange':0,'JavaVersionUpgradeChange':0,'APlibrary':0,'DBMSChange':0}
        return Calclator.createBasedResultMap(self, resultMap, calReuslt)

class FrameworkKnowhowFactorSumCalclator(KnowhowDegreeOfDifficultySumCalclator):
    """Calculators of factor for the knowledge-based search.
    
    """
    
    def __init__(self,type,inputsModels):
        '''
        Constructor
        '''
        KnowhowDegreeOfDifficultySumCalclator.__init__(self,type,inputsModels)
        self.type = type
        
    def calcBefore(self):
        if self.type == "fromFw":
            '''factorが移行元フレームワーク独自機能(上位互換あり)及びMVCFrameworkSpecific(BackwardCompati）のものは削除する'''
            labels = self.data.keys()
            for label in labels:
                calcModels = self.data.get(label)
                delTargets = self.getCalcModelsFromFw(calcModels)
                for delTarget in delTargets:
                    calcModels.remove(delTarget)
        elif self.type == "fromStrutsFw":
            '''factorが移行元フレームワーク独自機能(上位互換あり)及びMVCFrameworkSpecific(BackwardCompati）及びMVCフレームワーク(Controller機能)、MVCFramework(Controller)のものは削除する'''
            labels = self.data.keys()
            for label in labels:
                calcModels = self.data.get(label)
                delTargets = self.getDelTargetForStrutsPlugin(calcModels)
                for delTarget in delTargets:
                    calcModels.remove(delTarget)
                         
    def getCalcModelsFromFw(self,calcModels):
        results = []
        '''移行元フレームワークのバージョンアップ時には、MVCフレームワーク独自機能(上位互換あり)はグラフに積み上げない'''
        for calcModel in calcModels:
            if calcModel.factor == "MVCフレームワーク独自機能(上位互換あり)" or calcModel.factor =="MVCFrameworkSpecific(BackwardCompati)":
                results.append(calcModel)
        return results
    
    def getDelTargetForStrutsPlugin(self,calcModels):
        results = []
        for calcModel in calcModels:
            if calcModel.factor == "MVCフレームワーク(Controller機能)" or calcModel.factor =="MVCFramework(Controller)":
                results.append(calcModel)
        return results
    
    
    def createResultMap(self,calReuslt):
        resultMap ={'mvcFrameworkM':0,'mvcFrameworkV':0,'mvcFrameworkC':0,'mvcFrameworkSpecificNonBackwardCompati':0,'mvcFrameworkSpecificBackwardCompati':0}
        return Calclator.createBasedResultMap(self, resultMap, calReuslt)
    
        
class KnowhowFactorGraphSumCalclator(KnowhowFactorSumCalclator):
    """Calculators of factor bars graph for the knowledge-based search.
    
    """
    def __init__(self,type,inputsModels):
        '''
        Constructor
        '''
        KnowhowFactorSumCalclator.__init__(self,type,inputsModels)

class FrameworkKnowhowFactorGraphSumCalclator(KnowhowFactorSumCalclator):
    """Calculators of factor bars graph for the knowledge-based search.
    
    """
    def __init__(self,type,inputsModels):
        '''
        Constructor
        '''
        KnowhowFactorSumCalclator.__init__(self,type,inputsModels)
        

class StrutsFrameworkKnowhowFactorGraphSumCalclator(KnowhowFactorSumCalclator):
    """Calculators of factor bars graph for the knowledge-based search.
    
    """
    def __init__(self,type,inputsModels):
        '''
        Constructor
        '''
        KnowhowFactorSumCalclator.__init__(self,type,inputsModels)
        
class KnowhowMigrationItemCalclator(Calclator):
    """Calculators of migration item  for the knowledge-based search.
    
    """
    
    def __init__(self,type,inputsModels):
        '''
        Constructor
        '''
        Calclator.__init__(self,self.__class__.__name__,inputsModels)
        
    def calcBefore(self):
        '''self.dataのマップにあるすべてのオブジェクトをid種別でマップしなおす'''
        self.ids = self.getAllId()
        calcModels = self.getAllData()
        newMap ={}
        for id in self.ids:
            list =[] 
            for calcModel in calcModels:
                if calcModel.id == id:
                    list.append(calcModel)
            newMap[id] = list
        self.data = newMap
    
    def readCheckListInformation(self,DEF_CHECKLIST_INFO_PATH="../../../xml/checkListInformation_ja.xml"):
        
        base = os.path.dirname(os.path.abspath(__file__))
        #path = os.path.normpath(os.path.join(base, DEF_CHECKLIST_INFO_PATH))
        path = getCheckListInformationPath()
        tree = etree.parse(path)
        root = tree.getroot()
        results = []
        try :
            for id in self.ids:
                map = {}
                big = root.xpath("//description[@no='"+id +"']/big")[0].text
                middle = root.xpath("//description[@no='"+id +"']/middle")[0].text
                portabilityFactor = root.xpath("//description[@no='"+id +"']/portabilityFactor")[0].text
                degreeDetail = root.xpath("//description[@no='"+id +"']/degreeDetail")[0].text
                visualConfirm = root.xpath("//description[@no='"+id +"']/visualConfirm")[0].text
                map['id'] = id
                map['major'] = big
                map['minor'] = middle            
                map['degreeDetail'] = degreeDetail
                map['portabilityFactor'] = portabilityFactor
                map['guideRef'] = id.split("-")[0]
                map['visualConfirm'] = visualConfirm
                results.append(map)
        except Exception, inst:
             raise Exception('checkListInformation xml is invalid')
        return results
            
    def calc(self):
        calcResults = Calclator.calc(self)
        checkListInfos = self.readCheckListInformation()
        for calcResult in calcResults:
            for checkListInfoMap in checkListInfos:
                if calcResult.label == checkListInfoMap['id'] :
                     calcModels =self.data[calcResult.label]
                     hitAndStepMap = self.getHitandStepTotalMap(calcModels)
                     setattr(calcResult, 'hitTotal', hitAndStepMap['hit'])
                     setattr(calcResult, 'stepTotal', hitAndStepMap['step'])
                     setattr(calcResult, 'major', checkListInfoMap['major'])
                     setattr(calcResult, 'minor', checkListInfoMap['minor'])
                     setattr(calcResult, 'degreeDetail', checkListInfoMap['degreeDetail'])
                     setattr(calcResult, 'portabilityFactor', checkListInfoMap['portabilityFactor'])
                     setattr(calcResult, 'guideRef', checkListInfoMap['guideRef'])
                     #同じIDであれば、line_basicはどれでも同じはずなので、リストの最初のline_basicを取得する
                     setattr(calcResult, 'lineBasic', calcModels[0].line_basic)
                     setattr(calcResult, 'manualCheckStatus',self.getmanualCheckStatus(calcModels,checkListInfoMap['visualConfirm']))
                     
        return calcResults
        
    def getmanualCheckStatus(self,calcModels,visualConfirm):
        for calcModel in calcModels:
            if "0" in calcModel.manual_check_status:
                if visualConfirm != None:
                    return "NG"
        return "OK"
        
    def getHitandStepTotalMap(self,calcModels):
        hit = 0
        step = 0
        for calcModel in calcModels:
            hit = hit + int(calcModel.hit)
            step = step + int(calcModel.hitCount)
        return {'hit':hit,'step':step}
        
    def getIds(self,calcModels):
        results = []
        for model in calcModels:
            results.append(model.id)
        return results
     
    def getAllId(self):
        results = set([])
        
        labels = self.data.keys()
        for label in labels:
            calcModels = self.data.get(label)
            results =results.union(set(self.getIds(calcModels)))
        return results
    
    def getAllData(self):
        results = []
        labels = self.data.keys()
        for label in labels:
            calcModels = self.data.get(label)
            results = results +calcModels
        return results
        
    def createResultMap(self,calReuslt):
        pass



class DependsErrSumCalclator(Calclator):
    """Calculators of error for aggregation in dependence Search.
    
    """
    
    def __init__(self,type,inputsModels):
        '''
        Constructor
        '''
        Calclator.__init__(self,self.__class__.__name__,inputsModels)

    def calcBefore(self):
        pass
    
    def createResultMap(self,calReuslt):
        resultMap = {'Java':0,'Jsp':0,'Xml':0}
        for result in calReuslt:
            resultMap[result.label] = result.hitCount
        return resultMap
    
class DependsPackagePicGrapthSumCalclator(Calclator):
    """Calculators of Package pie chart in dependence Search.
    
    """
    
    def __init__(self,rules,inputsModels):
        '''
        Constructor
        '''
        Calclator.__init__(self,self.__class__.__name__,inputsModels)
        self.rules = rules
        
    def calcBefore(self):
        '''self.dataのマップにあるすべてのオブジェクトをパッケージ名でマップしなおす '''
        self.packages = self.getAllPackages()
        calcModels = self.getAllData()
        newMap ={}
        for packageName in self.packages:
            list =[] 
            for calcModel in calcModels:
                if calcModel.package == packageName:
                    list.append(calcModel)
            newMap[packageName] = list
        self.data = newMap
        '''rulesで指定された(strtus=org.apache.struts;...)パッケージが含む場合は該当パッケージに集約させる。この例の場合はorg.apache.strutsパッケージではじまるすべてのパッケージをstrutsと表示する'''
        for rule in self.rules:
            ruleName,rulePackage= rule.split("=")[0], rule.split("=")[1]
            for key in self.data.keys():
                if rulePackage in key:
                    updateTargetList = self.data.get(key)
                    if not self.data.has_key(rulePackage):
                        list = []
                        list = list + updateTargetList
                        self.data[rulePackage] = list
                    else:
                        list = self.data.get(rulePackage) + updateTargetList
                        self.data[rulePackage] = list
                    del self.data[key]
        
    def createResultMap(self,calReuslt):
        pass
    
    
    def getPackages(self,calcModels):
        results = []
        for model in calcModels:
            results.append(model.package)
        return results
     
    def getAllPackages(self):
        results = set([])
        
        labels = self.data.keys()
        for label in labels:
            calcModels = self.data.get(label)
            results =results.union(set(self.getPackages(calcModels)))
        return results
    
    def getAllData(self):
        results = []
        labels = self.data.keys()
        for label in labels:
            calcModels = self.data.get(label)
            results = results +calcModels
        return results
    


    
class DependsPackageSumCalclator(DependsPackagePicGrapthSumCalclator):
    """Calculators of Package  in dependence Search.
    
    """
    def __init__(self,type,inputsModels):
        '''
        Constructor
        '''
        DependsPackagePicGrapthSumCalclator.__init__(self, [],inputsModels)
        
    def calc(self):
        '''パッケージ毎のクラス情報についても取得するため、オーバライドする'''
        DependsPackagePicGrapthSumCalclator.calcBefore(self)
        calcResult = []
        labels = self.data.keys()
        for label in labels:
            calcModels = self.data.get(label)
            createModel = CalcModel(label,0)
            map = {}
            for calcModel in calcModels:
                createModel.hitCount = int(createModel.hitCount) + int(calcModel.hitCount)
                if not map.has_key(calcModel.classname):
                    map[calcModel.classname] = calcModel.hitCount
                else:
                    map[calcModel.classname] = int(map.get(calcModel.classname)) + int(calcModel.hitCount)
                classSumResultStr = self.createClassSumResultStr(map)
                setattr(createModel, 'classSumResult', classSumResultStr)
            calcResult.append(createModel)
        return sorted(calcResult, key=lambda label: label.hitCount)
    
    def createClassSumResultStr(self,map):
        classnames = map.keys()
        resultStr = ""
        for classname in classnames:
            hitCount= map.get(classname)
            resultStr = resultStr+ classname+":"+str(hitCount)+ ","
        return resultStr[:-1]


class DependsClassNameSumCalclator(Calclator):
    """Calculators of Package pie chart in dependence Search.
    
    """
    
    def __init__(self,rules,inputsModels):
        '''
        Constructor
        '''
        Calclator.__init__(self,self.__class__.__name__,inputsModels)
        self.rules = rules
        
    def calc(self):
        '''パッケージ毎のクラス情報についても取得するため、オーバライドする'''
        self.calcBefore()
        
        calcResult = []
        labels = self.data.keys()
        for label in labels:
            calcModels = self.data.get(label)
            createModel = CalcModel(label,0)
            map = {}
            for calcModel in calcModels:
                createModel.hitCount = int(createModel.hitCount) + int(calcModel.hitCount)
                if not map.has_key(calcModel.classname):
                    map[calcModel.classname] = calcModel.hitCount
                else:
                    map[calcModel.classname] = int(map.get(calcModel.classname)) + int(calcModel.hitCount)
                classSumResultStr = self.createClassSumResultStr(map)
                setattr(createModel, 'classSumResult', classSumResultStr)
            calcResult.append(createModel)
        return sorted(calcResult, key=lambda label: label.hitCount)
    def createResultMap(self,calReuslt):
        pass
    
    
    def getClassname(self,calcModels):
        results = []
        for model in calcModels:
            results.append(model.classname)
        return results
     
    def getClassnameAll(self):
        results = set([])
        
        labels = self.data.keys()
        for label in labels:
            calcModels = self.data.get(label)
            results =results.union(set(self.getClassname(calcModels)))
        return results
    
    def getAllData(self):
        results = []
        labels = self.data.keys()
        for label in labels:
            calcModels = self.data.get(label)
            results = results +calcModels
        return results
    
    def createClassSumResultStr(self,map):
        classnames = map.keys()
        resultStr = ""
        for classname in classnames:
            hitCount= map.get(classname)
            resultStr = resultStr+ classname+":"+str(hitCount)+ ","
        return resultStr[:-1]
    
class DependsJspUsedTldFileSumCalclator(DependsClassNameSumCalclator):
    """Calculators of Package pie chart in dependence Search.
    
    """
    
    def __init__(self,rules,inputsModels):
        '''
        Constructor
        '''
        DependsClassNameSumCalclator.__init__(self,self.__class__.__name__,inputsModels)
        
    def calcBefore(self):
        '''self.dataのマップにあるすべてのオブジェクトをクラス名でマップしなおす '''
        self.classnames = self.getClassnameAll()
        calcModels = self.getAllData()
        newMap ={}
        for className in self.classnames:
            list =[]
            if ".tld" not in className:
                 continue
            for calcModel in calcModels:
                if calcModel.classname == className:
                    list.append(calcModel)
            newMap[className] = list
        self.data = newMap
        
class DependsXmlUsedSchemaFileSumCalclator(DependsClassNameSumCalclator):
    """Calculators of Package pie chart in dependence Search.
    
    """
    
    def __init__(self,rules,inputsModels):
        '''
        Constructor
        '''
        DependsClassNameSumCalclator.__init__(self,self.__class__.__name__,inputsModels)
        
    def calcBefore(self):
        '''self.dataのマップにあるすべてのオブジェクトをクラス名でマップしなおす '''
        self.classnames = self.getClassnameAll()
        calcModels = self.getAllData()
        newMap ={}
        for className in self.classnames:
            list =[]
            for calcModel in calcModels:
                if calcModel.classname == className:
                    list.append(calcModel)
            newMap[className] = list
        self.data = newMap
        
"""
・エラーがあったファイルパスを取得する
"""
def getErrorFilePath():
    global g_targetFilePath
    return g_targetFilePath
    


"""
・パラメータチェック
正常時は0を返却する
"""
def paramCheck(pKey1,pKey2):
    #パラメータチェック
    return 0


def getFieldNum(key):
    return key.split(":f")[1]


def getRuleName(key):
    return key.split(":")[0]


def getExt(target):
    m = re.search("[^.]+$",target)
    extension = m.group()
    return extension
    
def createDependsCalcModelMap(ruleName,fieldNum,compareValue,dataFile,DEFAULT_ERR_FIELD_NUMS=1,DEFAULT_EXT_FIELD_NUMS=2,DEFAULT_PACKAGE_NUMS=3,DEFAULT_CLASSNAME_NUMS=6):
    dependsCalcModels =[]
    f = codecs.open(dataFile, "r", "utf-8")
    rows = csv.reader(f)
    for row in rows:
        if row[fieldNum-1] == compareValue  and row[DEFAULT_ERR_FIELD_NUMS-1] == "error":
            dependsCalcModels.append(DependsCalcModel(ruleName,1,row[DEFAULT_PACKAGE_NUMS-1],row[DEFAULT_CLASSNAME_NUMS-1],row[DEFAULT_EXT_FIELD_NUMS-1]))
    
    return dependsCalcModels

def createKnowledgeBaseCalcModelMap(ruleName,fieldNum,compareValue,dataFile,DEFAULT_LEVEL_FIELD_NUMS=1,DEFAULT_STEP_FIELD_NUMS=12,DEFAULT_STEP_TOTAL_FIELD_NUMS=13,DEFAULT_FACTOR_FIELD_NUMS=9,DEFAULT_TARGETFILE_NUMS=2,DEFAULT_GUIDE_FIELD_NUM=11,DEFAULT_HIT_FIELD_NUMS=3,DEFAUL_MANUAL_CHECK_STATUS=8,DEF_HIT_LINE=4):
    knowledgeBaseCalcModels =[]
    f = codecs.open(dataFile, "r", "utf-8")
    rows = csv.reader(f)
    for row in rows:
        if row[fieldNum-1] == compareValue and row[DEFAULT_STEP_TOTAL_FIELD_NUMS-1] != ""  and row[DEFAULT_HIT_FIELD_NUMS-1] != "" and row[DEFAULT_HIT_FIELD_NUMS-1] != 0 and row[DEFAULT_STEP_TOTAL_FIELD_NUMS-1] != "" and int(row[DEFAULT_STEP_TOTAL_FIELD_NUMS-1]) != 0 and row[3] != '0':
            knowledgeBaseCalcModels.append(KnowledgeBaseCalcModel(ruleName,row[DEFAULT_STEP_TOTAL_FIELD_NUMS-1],row[DEFAULT_FACTOR_FIELD_NUMS-1],getExt(row[DEFAULT_TARGETFILE_NUMS-1]),row[0],row[DEFAULT_HIT_FIELD_NUMS-1],row[10],row[DEFAUL_MANUAL_CHECK_STATUS-1]))
    
    return knowledgeBaseCalcModels


def createInputsModel(calcName,pRuleList,pDataFile):
    models =[]
    for rule in pRuleList:
        ruleName,fieldNum ,compareValue = getRuleName(rule.split("=")[0]),getFieldNum(rule.split("=")[0]),rule.split("=")[1]
        if "Knowhow" in calcName:
            models = models +createKnowledgeBaseCalcModelMap(ruleName,int(fieldNum),compareValue,pDataFile)
        else:
            models = models +createDependsCalcModelMap(ruleName,int(fieldNum),compareValue,pDataFile)
        
    return models


def copyToOutputDir(outputDir):
    #reportディレクトリの有無
    if os.path.isdir(outputDir):
            shutil.rmtree(outputDir)
            
    base = os.path.dirname(os.path.abspath(__file__))
    fromdir = os.path.normpath(os.path.join(base, "../resources/report"))
    shutil.copytree(fromdir,outputDir)
   
def getReportTplJonFilePath(REPORT_TYPE_JSON_FILE=".report_tpl.json"):
    base = os.path.dirname(os.path.abspath(__file__))
    return os.path.normpath(os.path.join(base, "../resources/"+REPORT_TYPE_JSON_FILE))
    
def deleteJsParentDir(calclators):
    for calclator in calclators:
        if calclator.get('type') != "" and calclator.get('calclator') != "DependsPackagePicGrapthSumCalclator":
            dirpath= "../resources/report/js/" + calclator.get('calclator') + "_"+ calclator.get('type')
        else:
            dirpath= "../resources/report/js/" +calclator.get('calclator')
        base = os.path.dirname(os.path.abspath(__file__))
        dirpath = os.path.normpath(os.path.join(base, dirpath))
        if os.path.isdir(dirpath):
            shutil.rmtree(dirpath)

def getCheckListInformationPath():
    base = os.path.dirname(os.path.abspath(g_targetFilePath))
    filepath = os.path.normpath(os.path.join(base, "./.checkListInformation_ja.xml"))
    if os.path.isfile(filepath):
        return filepath
    filepath = os.path.normpath(os.path.join(base, "./.checkListInformation.xml"))
    if os.path.isfile(filepath):
        return filepath
    raise Exception("checkListInformation.xml or checkListInformation_ja.xml is required in search target dir")


def getTemplateTypeFromPluginReportDir(REPORT_TYPES=["ap","mvc","struts"],REPORT_TYPE_JSON_FILE=".report_tpl.json"):
    pluginReportFolder = getPluginReportDir()
    reportTypeJsonFile = os.path.join(pluginReportFolder, REPORT_TYPE_JSON_FILE)
    if not os.path.isfile(reportTypeJsonFile):
        return REPORT_TYPES[0]
    f = open(reportTypeJsonFile, 'r')
    jsondata= json.load(f)
    type=jsondata['template'] 
    if type ==None:
        return REPORT_TYPES[0]
    if type in REPORT_TYPES:
        return type
    else:
        return REPORT_TYPES[0]
    
def filterCalcators(templateType,calcators):
    new_calcs = []
    for calcator in calcators:
        if templateType in calcator['tpl']:
            new_calcs.append(calcator)
    return new_calcs


def removeOtherReport(templateType,outputdir):
    REPORT_TYPE_DICT= {'ap':"TubameReport",'mvc':"TubameMVCFrameworkReport",'struts':"TubameStrutsFrameworkReport"}
    types = REPORT_TYPE_DICT.keys()
    for type in types:
        if type != templateType:
            delTargetFile = REPORT_TYPE_DICT[type]
            deltarget_ja = os.path.normpath(os.path.join(outputdir, ".//"+delTargetFile+"_ja.html"))
            deltarget_en = os.path.normpath(os.path.join(outputdir, ".//"+delTargetFile+"_en.html"))
            if os.path.isfile(deltarget_ja):
                os.remove(deltarget_ja)
            if os.path.isfile(deltarget_en):
                os.remove(deltarget_en)
                
def getPluginReportDir():
    base = os.path.dirname(os.path.abspath(__file__))
    return os.path.normpath(os.path.join(base, "../../../report/"))
                        
"""
・TUBAMEレポート出力を行う。
検索キー1はレポート出力ディレクトリを指定できる。オプショナルでデフォルトはeclipse\plugins\tubame.portability*\resources/report配下にレポートを出力する


ex1) c://report配下にレポートを出力した場合は以下のように指定する

検索キー1: c://report
検索キー2:


ex2 ) 検索キー2は依存性検索ツールのパッケージ円グラフのためのオプションです。依存性検索の結果、移行先の環境で利用できない多くのパッケージが存在する場合に有効となる、
たとえば、weblogicからはじまるパッケージ(weblogic.A,weblogic.B,...)が多く、weblogicとしてまとめた場合はweblogic=weblogicを検索キー2に指定する。
　
  検索キー1: c://report
  検索キー2: weblogic=weblogic

ex3 ) たとえば、weblogicからはじまるパッケージ(weblogic.A,weblogic.B,...)をweblogicとしてまとめ、org.apache.strutsをstrutsとしてまとめたい場合は以下のようにする
　         (セミコロン区切りで、複数指定可能です）
  検索キー1: c://report
  検索キー2: weblogic=weblogic;struts=org.apache.struts

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
def ext_search(pNo, pPriority, pFlag, pList, pGenTargetDir, pRules, pInputCsv, pTargetDir, pChapterNo, pCheck_Status):
    reload(sys)
    sys.setdefaultencoding("UTF-8")
    common_module = sys.modules["migration.jbmst_common"]
    rsl_list = []
    if pRules != "":
        if not ";" in pRules:
            pDependPackageGroupingRules = [pRules]
        else:
            pDependPackageGroupingRules = pRules.split(";")
    else:
        pDependPackageGroupingRules = []
    
    sets = locale.getdefaultlocale()
    if "ja" in sets[0]:
        condtions1 = "High:f10=高;Middle:f10=中;Low1:f10=低1;Low2:f10=低2;Unknown1:f10=不明1;Unknown2:f10=不明2"
        condtions2 = "WeblogicSpecChange:f9=Weblogic 固有;ApServerDependsChange:f9=AP サーバ固有;ApServerDependsDepricatedChange:f9=AP サーバ仕様の変更;JavaEESpecChange:f9=JSP/Servlet 仕様の変更;JavaVersionUpgradeChange:f9=Java バージョンアップによる変更;APlibrary:f9=AP 使用ライブラリ;DBMSChange:f9=DBMS の変更"
        condtions3 = "mvcFrameworkM:f9=MVCフレームワーク(Model機能);mvcFrameworkC:f9=MVCフレームワーク(Controller機能);mvcFrameworkV:f9=MVCフレームワーク(View機能);mvcFrameworkSpecificNonBackwardCompati:f9=MVCフレームワーク独自機能(上位互換なし);mvcFrameworkSpecificBackwardCompati:f9=MVCフレームワーク独自機能(上位互換あり)"
    else:
        condtions1 = "High:f10=High;Middle:f10=Middle;Low1:f10=Low1;Low2:f10=Low1;Unknown1:f10=Unknown1;Unknown2:f10=Unknown2"
        condtions2 = "WeblogicSpecChange:f9=Weblogic specific;ApServerDependsChange:f9=AP server specific;ApServerDependsDepricatedChange:f9=AP server specification change;JavaEESpecChange:f9=JSP/servelet specification change;JavaVersionUpgradeChange:f9=Java version upgrade change;APlibrary:f9=AP library;DBMSChange:f9=DBMS change"
        condtions3 = "mvcFrameworkM:f9=MVCFramework(Model);mvcFrameworkC:f9=MVCFramework(Controller);mvcFrameworkV:f9=MVCFramework(View);mvcFrameworkSpecificNonBackwardCompati:f9=MVCFrameworkSpecific(BackwardCompati);mvcFrameworkSpecificBackwardCompati:f9=MVCFrameworkSpecific(NonBackwardCompati)"

    calcators = [{"calclator":"KnowhowDegreeOfDifficultySumCalclator", "type": "fromAp","condtions":condtions1,"execCreateResultMap":True,"tpl" :["ap"]},
                 {"calclator":"KnowhowDegreeOfDifficultySumCalclator", "type": "toAp","condtions":condtions1,"execCreateResultMap":True,"tpl" :["ap"]} ,
                 {"calclator":"FrameworkKnowhowDegreeOfDifficultySumCalclator", "type": "fromFw","condtions":condtions1,"execCreateResultMap":True,"tpl" :["mvc"]} ,
                 {"calclator":"FrameworkKnowhowDegreeOfDifficultySumCalclator", "type": "fromStrutsFw","condtions":condtions1,"execCreateResultMap":True,"tpl" :["struts"]} ,
                 {"calclator":"FrameworkKnowhowDegreeOfDifficultySumCalclator", "type": "toFw","condtions":condtions1,"execCreateResultMap":True,"tpl" :["mvc"]} ,
                 {"calclator":"KnowhowMigrationItemCalclator","type":"","condtions":condtions1,"execCreateResultMap":False,"tpl" :["ap","mvc","struts"]},
                 {"calclator":"KnowhowFactorSumCalclator", "type": "fromAp","condtions":condtions2,"execCreateResultMap":True,"tpl" :["ap"]} ,
                 {"calclator":"KnowhowFactorSumCalclator", "type": "toAp","condtions":condtions2,"execCreateResultMap":True,"tpl" :["ap"]} ,
                 #{"calclator":"FrameworkKnowhowFactorSumCalclator", "type": "fromStrutsFw","condtions":condtions3,"execCreateResultMap":True} ,
                 {"calclator":"FrameworkKnowhowFactorSumCalclator", "type": "fromFw","condtions":condtions3,"execCreateResultMap":True,"tpl" :["mvc"]} ,
                 {"calclator":"FrameworkKnowhowFactorSumCalclator", "type": "toFw","condtions":condtions3,"execCreateResultMap":True,"tpl" :["mvc"]} ,
                 {"calclator":"KnowhowFileCategorySumCalclator", "type": "fromAp","condtions":condtions2,"execCreateResultMap":True,"tpl" :["ap"]}, 
                 {"calclator":"KnowhowFileCategorySumCalclator", "type": "toAp","condtions":condtions2,"execCreateResultMap":True,"tpl" :["ap"]},
                 {"calclator":"FrameworkKnowhowFileCategorySumCalclator", "type": "fromFw","condtions":condtions3,"execCreateResultMap":True,"tpl" :["mvc"]}, 
                 {"calclator":"FrameworkKnowhowFileCategorySumCalclator", "type": "fromStrutsFw","condtions":condtions3,"execCreateResultMap":True,"tpl" :["struts"]},
                 {"calclator":"FrameworkKnowhowFileCategorySumCalclator", "type": "toFw","condtions":condtions3,"execCreateResultMap":True,"tpl" :["mvc"]},
                 {"calclator":"FrameworkKnowhowFileCategorySumCalclator", "type": "fromStrutsFw","condtions":condtions3,"execCreateResultMap":True,"tpl" :["struts"]},
                 {"calclator":"KnowhowFactorGraphSumCalclator","type":"graph","condtions":condtions2,"execCreateResultMap":True,"tpl" :["ap"]},
                 {"calclator":"FrameworkKnowhowFactorGraphSumCalclator","type":"graph","condtions":condtions3,"execCreateResultMap":True,"tpl" :["mvc"]},
                 {"calclator":"StrutsFrameworkKnowhowFactorGraphSumCalclator","type":"graph","condtions":condtions3,"execCreateResultMap":True,"tpl" :["struts"]},
                 {"calclator":"DependsErrSumCalclator", "type":"","condtions":"Java:f2=Java;Jsp:f2=Jsp;Xml:f2=Xml","execCreateResultMap":True,"tpl" :["ap","mvc"]},
                 {"calclator":"DependsPackagePicGrapthSumCalclator", "type":pDependPackageGroupingRules,"condtions":"Java:f2=Java;Jsp:f2=Jsp","execCreateResultMap":False,"tpl" :["ap","mvc"]},
                 {"calclator":"DependsPackageSumCalclator","type":"","condtions":"Java:f2=Java;Jsp:f2=Jsp","execCreateResultMap":False,"tpl" :["ap","mvc"]},
                 {"calclator":"DependsJspUsedTldFileSumCalclator","type":"","condtions":"Jsp:f2=Jsp","execCreateResultMap":False,"tpl" :["ap","mvc"]},
                 {"calclator":"DependsXmlUsedSchemaFileSumCalclator","type":"","condtions":"Xml:f2=Xml","execCreateResultMap":False,"tpl" :["ap","mvc"]}
                ]
    templateType=getTemplateTypeFromPluginReportDir()
    calcators = filterCalcators(templateType,calcators)
    #getresultの親ディレクトリが存在する場合は、削除する。
    deleteJsParentDir(calcators)
    
    global g_targetFilePath
    
    #検索対象ファイルリスト中の全ファイルを対象とする
    for fname in pList:
        ext = common_module.getExtension(fname)
        if ext == "jbm" or ext == "gjbm":
            rsl_list.append(1)
            common_module.print_csv(pNo, pPriority, pFlag, fname, [1], pChapterNo, pCheck_Status)
            g_targetFilePath = fname

            for calcator in calcators:
                if ext=="jbm" and  not "Knowhow" in calcator.get('calclator'):
                    continue
                elif ext=="gjbm" and  not "Depend" in calcator.get('calclator'):
                    continue
                else:
                    inputModels = createInputsModel(calcator.get('calclator'),calcator.get('condtions').split(";"),fname)
                    calcator_cls = getattr(sys.modules['extend.ext_report_generator'],calcator.get('calclator'))
                    calcator_instance = calcator_cls(calcator.get('type'),inputModels)
                    results = calcator_instance.calc()
                    if calcator.get('execCreateResultMap'):
                        results = calcator_instance.createResultMap(results)
                    calcator_instance.jswriter.save(results)
    #if len(rsl_list) == 0:
    #        common_module.print_csv4(pNo, pPriority, pFlag,common_module.searchTarget , pChapterNo, pCheck_Status)
            
    else:
        if pGenTargetDir == "":
            base = os.path.dirname(os.path.abspath(__file__))
            pGenTargetDir  = os.path.normpath(os.path.join(base, "../../../report/"))

        copyToOutputDir(pGenTargetDir)
        removeOtherReport(templateType,pGenTargetDir)
if __name__ == '__main__':
    pass
