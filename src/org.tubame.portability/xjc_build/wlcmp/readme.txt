Add the following dtd files to this directory.

 - jbosscmp-jdbc_4_0.dtd:
   http://www.jboss.org/j2ee/dtd/jbosscmp-jdbc_4_0.dtd
   * Fix at line 510: before> (ejb-relationship-role, ejb-relationship-role)?
   *                  after>  (ejb-relationship-role*)

 - weblogic-rdbms-persistence-510.dtd:
   http://www.bea.com/servers/wls510/dtd/weblogic-rdbms-persistence.dtd

 - weblogic-rdbms20-persistence-600.dtd:
   http://www.bea.com/servers/wls600/dtd/weblogic-rdbms20-persistence-600.dtd
   * Fix at line 275,276: before> weblogic-relationship-role, weblogic-relationship-role?
   *                      after>  weblogic-relationship-role+

 - weblogic-rdbms20-persistence-810.dtd:
   http://www.bea.com/servers/wls810/dtd/weblogic-rdbms20-persistence-810.dtd
   * Fix at line 506,507: before> weblogic-relationship-role, weblogic-relationship-role?
   *                      after>  weblogic-relationship-role+
