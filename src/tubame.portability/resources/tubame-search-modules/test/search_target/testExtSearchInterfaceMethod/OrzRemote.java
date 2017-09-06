package orz.ejb;

import java.rmi.RemoteException;
import java.util.Hashtable;

import javax.ejb.EJBObject;
import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;


public interface OrzRemote extends EJBObject{

	public String orz(String input)
			throws RemoteException; 
	
	public static InitialContext getInitialContext(String url)	throws NamingException {
		System.out.println("url:"+url);
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		env.put(Context.PROVIDER_URL, url);
		return new InitialContext(env);
	}

}
