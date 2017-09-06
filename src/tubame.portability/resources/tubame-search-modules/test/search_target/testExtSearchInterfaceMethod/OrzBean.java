package orz.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBException;
import javax.ejb.SessionBean;
import javax.ejb.SessionContext;

public class OrzBean implements SessionBean {

	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public SessionContext context;

	public void ejbCreate() throws CreateException, RemoteException, EJBException {
	}

	public void ejbRemove() throws RemoteException, EJBException {
	}

	public void setSessionContext(SessionContext sc) throws RemoteException, EJBException {
		context = sc;
	}

	public void ejbActivate() throws RemoteException, EJBException {
	}

	public void ejbPassivate() throws RemoteException, EJBException {
	}

	public String orz(String input) throws RemoteException {
		//return "orz..." + input;
//		propValue = HogeHogeUtil.getProps("key1");
		return "Hi! " + input;
	}
}
