package com.bea.medrec.xml;

import com.bea.medrec.controller.AdminSession;
import com.bea.medrec.exceptions.MedRecException;
import com.bea.medrec.utils.JNDINames;
import com.bea.medrec.utils.ServiceLocator;
import com.bea.medrec.utils.XMLFilter;
import com.bea.medrec.value.MedicalRecord;
import com.bea.medrec.value.XMLImportFile;
import java.io.*;
import java.rmi.RemoteException;
import java.text.SimpleDateFormat;
import java.util.*;
import javax.ejb.EJBException;
import javax.naming.NamingException;
import org.apache.log4j.Logger;
import weblogic.xml.stream.XMLEvent;
import weblogic.xml.stream.XMLInputStream;
import weblogic.xml.stream.XMLInputStreamFactory;
import weblogic.xml.stream.util.TypeFilter;

public class MedRecXMLProcessor
{
  private static Logger logger =
    Logger.getLogger(MedRecXMLProcessor.class.getName());

  private String xmlDirLoc;

  private MedRecXMLProcessor()
  {
    // Incoming XML medical record location
    xmlDirLoc = System.getProperty("com.bea.medrec.xml.incoming");
    logger.info("XML directory: "+xmlDirLoc);
  }

  public static MedRecXMLProcessor getInstance()
  {
    return new MedRecXMLProcessor();
  }

      //  G E T   P E N D I N G ,   I N C O M I N G   X M L   F I L E S
 /**
  * <p>Get pending, incoming xml files containing new patients and
  * their medical record.</p>
  *
  * @return Collection  Collection of XMLImportFile objects.
  *
  * @exception javax.naming.NamingException
  * @exception java.rmi.RemoteException
  * @exception Exception
  */
  public Collection getIncomingXMLFiles()
    throws RemoteException, NamingException, Exception
 {
   // Declare local variables.
    Collection col = new ArrayList();

    // if the log4j-init-file is not set, then no point in trying
    if(xmlDirLoc != null) {
      File xmlDir = new File(xmlDirLoc);
      if (xmlDir.isDirectory()) {
        FilenameFilter filter = new XMLFilter();
        File[] files = xmlDir.listFiles(filter);
        for (int i=0; i<files.length; i++) {
          Calendar cal = new GregorianCalendar();
          cal.setTimeInMillis(files[i].lastModified());
          XMLImportFile xmlFile = new XMLImportFile(files[i].getName(),
            xmlDirLoc, cal, files[i].length());
          logger.debug(xmlFile.toString());
          col.add(xmlFile);
        }
      }
    }
    return col;
  }

      //   S A V E   X M L    R E C O R D
 /**
  * <p>Parses XML stream into MedRec value objects.
  * Stores resultant objects.</p>
  *
  * @param pBis
  *
  * @exception MedRecException
  */
  public void saveXMLRecord(BufferedInputStream pBis)
  throws MedRecException, Exception
  {
    logger.info("Parsing xml and saving resultant.");

    // Declare local variables.
    AdminSession adminSession = null;
    XMLInputStreamFactory factory = null;
    XMLInputStream stream = null;
    RecordXMLParser parser = null;
    Collection medicalRecordCol = null;

    // XML parser factory.
    factory = XMLInputStreamFactory.newInstance();

    try {
      // Session bean homes.
      adminSession = getAdminSession();

      stream = factory.newInputStream(pBis,
        new TypeFilter(XMLEvent.START_ELEMENT | XMLEvent.CHARACTER_DATA));
      parser = RecordXMLParser.getInstance();
      logger.debug("Parsing xml doc.");
      parser.parse(stream);

      logger.debug("Getting MedicalRecords collection.");
      medicalRecordCol = parser.getMedicalRecords();
      if (logger.isDebugEnabled()) printMedicalRecords(medicalRecordCol);
      adminSession.insertMedicalRecord(medicalRecordCol);
    }
    catch(NamingException ne) {
      logger.error(ne.getLocalizedMessage(), ne);
      throw new MedRecException(ne);
    }
    catch (MedRecException me) {
      logger.error(me.getLocalizedMessage(), me);
      throw me;
    }
    catch (EJBException ejbe) {
      logger.error(ejbe.getLocalizedMessage(), ejbe);
      throw new MedRecException(ejbe);
    }
    catch (Exception e) {
      logger.error(e.getLocalizedMessage(), e);
      throw e;
    }
  }

 /**
  * <p>Parses given XML file found in xml.incoming System property
  * into MedRec value objects.  Stores resultant objects.</p>
  *
  * @param pFilename
  *
  * @exception MedRecException
  */
  public void saveXMLRecord(String pFilename)
    throws MedRecException, Exception
  {
    logger.info("Parsing xml file: "+pFilename);

    // Declare local variables.
    File xmlFile = null;
    File archivedFile = null;
    FileInputStream fis = null;

    xmlFile = new File(xmlDirLoc+"/"+pFilename);
    logger.debug("XML filepath: "+xmlFile.getAbsolutePath());

    if (!xmlFile.exists() || !xmlFile.isFile())
      throw new MedRecException("File not found.");

    try {
      fis = new FileInputStream(xmlFile);
      saveXMLRecord(new BufferedInputStream(fis));
      archivedFile = new File(xmlDirLoc+"/"+pFilename+"_"+getDateTimeStr());
      xmlFile.renameTo(archivedFile);
    }
    catch (Exception e) {
      throw e;
    }
    finally {
      fis.close();
    }
  }

      //   P R I V A T E   M E T H O D S
 /**
  * <p>Prints contents of medical record collection.</p>
  */
  private void printMedicalRecords(Collection pMedRecCol)
  {
    // Declare local variables.
    Iterator itr = null;
    MedicalRecord medicalRecord = null;

    itr = pMedRecCol.iterator();
    while (itr.hasNext()) {
      medicalRecord = (MedicalRecord)itr.next();
      logger.debug("******* Medical Record **********");
      logger.debug(medicalRecord.toString());
    }
    
    String hoge = "Declare";
  }

 /**
  * <p>Prints contents of medical record collection.</p>
  */
  private String getDateTimeStr()
  {
    SimpleDateFormat dateFormat = null;
    dateFormat = new SimpleDateFormat("yyMMddHHmmss");
    return dateFormat.format(new Date());

  }

 /**
  * Get AdminSession
  */
  private AdminSession getAdminSession()
   throws NamingException
 {
    ServiceLocator locator = ServiceLocator.getInstance();
    Object obj = locator.getObj(JNDINames.ADMIN_SESSION_REMOTE_HOME,
      com.bea.medrec.controller.AdminSessionHome.class);
    return (AdminSession)obj;
  }
}
