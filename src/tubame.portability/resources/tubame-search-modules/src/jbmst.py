# -*- coding: utf-8 -*-
"""
jbmst.py
Created on 2013/06/28

Copyright (C) 2011-2013 Nippon Telegraph and Telephone Corporation

Licensed under the Apache License, Version 2.0 (the "License");
you may not use this file except in compliance with the License.
You may obtain a copy of the License at

     http://www.apache.org/licenses/LICENSE-2.0

Unless required by applicable law or agreed to in writing, software
distributed under the License is distributed on an "AS IS" BASIS,
WITHOUT WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied.
See the License for the specific language governing permissions and
limitations under the License.
"""

"""
Various search process.
Do the following process based on the criteria specified.
 - If the Extended search conditions are specified,
    and performs Extended search processing regardless of the presence or absence of search criteria.
 - If the search condition is specified, extended search condition is not specified,
    do a search process corresponding to the file extension.
 - If the search term is not specified, do the only search by file name.
Usage: jbmst.py <file path> <folder path>
[Environment] Python 2.7
"""

from migration import *
from extend import *
import sys
import csv
import os
import imp
import codecs
import traceback
bPARAMETER_CHECK = True        # Release Version


# Temporary storage destination of CSV line
tempCSVLine=[]


"""
Get a number data to be displayed in the search running.

@param no:Number of the search condition records
@return Number data
"""
def getSearchNum(no):

    currentSearchData=no.split("-")
    return currentSearchData[0]

"""
Get the denominator number to be displayed in the search running.

@param reader:All the records in the file search
@return Number data
"""
def getDenominatorSearchNum(reader):

    denominatorData=""
    for rowData in reader:
        # Save the CSV line
        tempCSVLine.append(rowData)
        row = jbmst_common.load(rowData)
        denominatorData = row[0]

    return getSearchNum(denominatorData)



# An error if the number of arguments in the run-time other than 3
if len(sys.argv) != 3:

    print "usage : jbmst.py input_file search_dir "
    if bPARAMETER_CHECK:
        sys.exit(-1)

# If the default encoding is set, set in utf-8.
if hasattr(sys,"setdefaultencoding"):
    sys.setdefaultencoding("utf-8")

# Initial processing
if bPARAMETER_CHECK:
    input_csv_file = sys.argv[1]            # Run Parameters [1] (search files)
    search_dir = sys.argv[2]
# Run Parameters [1] (Search target folder)
# Set the path to the file list of the key search_target
# Ex) search_targets_map = { "*.java": "A","B"
#                            "*.jsp" : "C","D"
search_targets_map = {}
searcher_map = {}


# Check keyword file is present
if not os.path.exists(input_csv_file):
    raise IOError,"Search keyword file does not exist: %s" % (input_csv_file)
# Keyword file size to check whether 0 or more
if os.path.getsize(input_csv_file) == 0:
    raise IOError, "Invalid search keyword file: %s" % (input_csv_file)

fsize=os.path.getsize(input_csv_file)

# Read all the records in the file search
reader = csv.reader(file(input_csv_file, "r"))

# 1) read one line search condition record.
# 2) If the Extended search conditions are specified:
#     Input the information in the search criteria in the extended search module.
# 3) If the Extended search condition is not specified:
#   - If the Search Keyword1 has been set:
#     Distributed processing (java, jsp, xml) by extension.
#   - For the extension, performs a process that does not search for a comment statement.
#   - If the extension other than the above, do processing in a search for comment text.
#   - If the Search Keyword1 is not input to display the following information.
#     No, Target File, 1, "", Degree of importance, Convert Flag, Guide Chapter, 0
count=0;



# Get the parameter of the search keyword file
denominatorCSV=getDenominatorSearchNum(reader)

ignore_list=jbmst_common.load_ignorelist()

isHit = 0;
errorFilePath = ""
for tempRow in tempCSVLine:

    count = count + 1
    # Check the number of columns
    if len(tempRow) < 6 :
        raise IOError, 'Invalid search keyword file (line %d): %s' % (count,input_csv_file)

    row = jbmst_common.load(tempRow)
    try :
        no = row[0]                         # No
        search_target = row[1]              # Target File
        search_str1 = row[2]                # Search Keyword1
        search_str2 = row[3]                # Search Keyword2
        extend_py_file = row[4]             # Extended search module name
        priority = row[5]                   # Degree of difficulty
        flag = row[6]                       # Convert Flag
        if (len(row) > 7):
            chapter_no = row[7]             # Guide Chapter
        else:
            chapter_no = ""                 # Empty string
        check_Status = "0"                  # Check Status
    
        # Display progress of a search (XX/XXX)
        print no+"/"+denominatorCSV
        # Flush the Buffer
        sys.stdout.flush()
    
        # Get the extension from the file name search.
        extension = jbmst_common.getExtension(search_target)
        target_list = []            # Initialize the object dictionary.
    
        # Check the same or search for file names that you already processed.
        # If the same file name exists, set the file path of the file that was already processed.
        # If the same file name does not exist,
        #  to get the file path of the file that meets your search criteria file name that is extracted.
        if search_targets_map.has_key(search_target):
            target_list = search_targets_map[search_target]
        else:
            target_list = jbmst_common.searchFileBySearchTarget(search_dir,search_target,ignore_list)
            search_targets_map[search_target] = target_list
    
    
        # If the extended search file name is specified, do an extended search processing.
        if extend_py_file != "":
            extend_py_file = extend_py_file[:-3]
            load_module_name = "extend." + extend_py_file
            ext_search_module = sys.modules[load_module_name]
    
            jbmst_common.searchTarget=search_target
            if ext_search_module.paramCheck(search_str1,search_str2) != 0:
                raise IOError, 'Invalid search keyword parameter (line %d): %s' % (count,input_csv_file)
    
            try :
                ext_search_module.ext_search(no,priority,flag,target_list,search_str1,search_str2,input_csv_file,search_dir,chapter_no,check_Status)
            except Exception, inst:
                try:
                    errorFilePath = ext_search_module.getErrorFilePath()
                except NameError:
                    errorFilePath = "None"
                error = inst
                raise Exception, 'Failed search (line %d): %s \n%s' % (count,input_csv_file, error)
            continue
    
        isHit = 0;
        for filePath in target_list:
            errorFilePath = filePath
            
            if search_str1 == "":
                jbmst_common.print_csv3(no,filePath,priority,flag,chapter_no,check_Status)
            else:
                result_line_cnt_list = []
                if extension == "java" or extension == "c" or extension == "h" :
                    result_line_cnt_list = jbmst_search_java.searchByFile(filePath,search_str1,search_str2)
    
                elif extension == "jsp" :
                    result_line_cnt_list = jbmst_search_jsp.searchByFile(filePath,search_str1,search_str2)
    
                elif extension == "xml" :
                    result_line_cnt_list = jbmst_search_xml.searchByFile(filePath,search_str1,search_str2)

                else:
                    result_line_cnt_list = jbmst_search_default.searchByFile(filePath,search_str1,search_str2)

                # Output to a CSV also number 0 line search hits.
                if (len(result_line_cnt_list) != 0):
                    #isHit += 1
                    jbmst_common.print_csv(no,priority,flag,filePath,result_line_cnt_list,chapter_no,check_Status)
                
        # If no results, output to CSV contents of the search keyword.
        if len(target_list) >= 1 and isHit != 0:
            result_line_cnt_list = "0"
            line = no + "," + search_target + "," + result_line_cnt_list + ","+ "0" + "," + priority + "," + flag+ "," + chapter_no+ "," + check_Status
            print line
    
    except Exception, ex:
        #print ex
        raise Exception, 'Search Err(csvinfo:%s ,SearchTargetFile:%s) \r\n%s ' % (row,errorFilePath,traceback.format_exc(sys.exc_info()[2]))
