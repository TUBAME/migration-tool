package orz.ejb;

import java.rmi.RemoteException;

import javax.ejb.CreateException;
import javax.ejb.EJBHome;

public interface OrzHome extends EJBHome {
	public OrzRemote create() throws CreateException, RemoteException;
}
