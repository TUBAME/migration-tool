# -*- mode: python -*-

"""The described how to distribute the TUBAME Search Modules For Windows, Linux ,Mac.
 * Requirements
  * pyinstaller
  * lxml
  * sqlparse

 * how to distribute
  * 1.change dir
   * cd migration-tool/src/tubame.portability/resources/tubame-search-modules/src
   
  * 2.execute pyinstaller
   
   * pyinstaller jbmst.spec
   
   ***linux only problem***
   http://stackoverflow.com/questions/35477674/pyinstaller-attributeerror-module-object-has-no-attribute-rsa
   
   please fix the jbmst.py for pyinstaller ,becase 'import *' can not be recognized by pyinstaller, 
   
   ---
   #from migration import *
   from migration import jbmst_common,jbmst_search_default,jbmst_search_java,jbmst_search_jsp,jbmst_search_xml,jbmst_search_properties,jbmst_search_sh

   #from extend import *
   from extend import ext_search_xml_xpath,ext_report_generator,ext_search_xml_definedclass,ext_search_sql_parser
   ---
   
  * 3.compress a directory
    * manual compress
     * compresses dist/jbmst directory, and please locate it in the following folders.
      * win
       * migration-tool/src/tubame.portability/resources/tubame-search-modules/jbmst_win.zip
      * mac
       * migration-tool/src/tubame.portability/resources/tubame-search-modules/jbmst_mac.zip
      * linux
       * migration-tool/src/tubame.portability/resources/tubame-search-modules/jbmst_linux.zip
       
  * 4. clean 
    * delete dir
      * migration-tool/src/tubame.portability/resources/tubame-search-modules/dist
      * migration-tool/src/tubame.portability/resources/tubame-search-modules/build
     
"""

import os
import glob

def extra_datas(mydir):
    def rec_glob(p, files):
        for d in glob.glob(p):
            if os.path.isfile(d):
                files.append(d)
            rec_glob("%s/*" % d, files)
    files = []
    rec_glob("%s/*" % mydir, files)
    extra_datas = []
    for f in files:
        extra_datas.append((f, f, 'DATA'))
    return extra_datas


def gen_jbmst_zip():
    import os
    import zipfile
    search_modules_src_folder = os.path.dirname(os.path.abspath('__file__'))
    search_modules_src_dist_folder = os.path.normpath(os.path.join(search_modules_src_folder, "./dist"))
    search_modules_bin_folder = os.path.normpath(os.path.join(search_modules_src_folder, "../bin"))
    if not os.path.exists(search_modules_bin_folder):
    	os.path.mkdir(search_modules_bin_folder)
    	
    search_modules_zipfile = os.path.normpath(os.path.join(search_modules_bin_folder, "./jbmst_"+os.name+".zip"))
    if os.path.isfile(search_modules_zipfile):
        os.remove(search_modules_zipfile)
    zip_targets = []
    for dirpath, dirnames, filenames in os.walk(search_modules_src_dist_folder):
        for filename in filenames:
            filepath = os.path.join(dirpath, filename)
            arc_name = os.path.relpath(filepath, os.path.dirname(search_modules_src_dist_folder))
            zip_targets.append((filepath, arc_name))
        for dirname in dirnames:
                filepath = os.path.join(dirpath, dirname)
                arc_name = os.path.relpath(filepath, os.path.dirname(search_modules_src_dist_folder)) + os.path.sep
                zip_targets.append((filepath, arc_name))
                
	zip = zipfile.ZipFile("../bin/jbmst_"+os.name+".zip", 'w',zipfile.ZIP_DEFLATED)
	for filepath, name in zip_targets:
		zip.write(filepath, name)
	zip.close()

search_modules_src_folder = os.path.dirname(os.path.abspath('__file__'))
a = Analysis(['jbmst.py'],
             pathex=[search_modules_src_folder],
             hiddenimports=[],
             hookspath=['.'],
             runtime_hooks=None)
             
a.datas += extra_datas('resources')         
pyz = PYZ(a.pure)
exe = EXE(pyz,
          a.scripts,
          exclude_binaries=True,
          name='jbmst.exe',
          debug=False,
          strip=None,
          upx=True,
          console=True)
coll = COLLECT(exe,
               a.binaries,
               a.zipfiles,
               a.datas,
               strip=None,
               upx=True,
               name='jbmst')

