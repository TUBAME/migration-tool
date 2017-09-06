package examples.ejb.client;

import java.util.Hashtable;

import javax.naming.Context;
import javax.naming.InitialContext;
import javax.naming.NamingException;
import javax.rmi.PortableRemoteObject;

import orz.ejb.OrzHome;
import orz.ejb.OrzRemote;

public class OrzEJbClient {
	public final static String JNDI_FACTORY = "org.jnp.interfaces.NamingContextFactory";

	public static void main(String[] args) throws Exception {
		if (args.length != 1) {
			System.out
					.println("Usage: java examples.jms.queue.QueueSend ProviderURL");
			return;
		}
		InitialContext ic = getInitialContext(args[0]);
		Object lookup = ic.lookup("Orz");
		OrzHome orzHome = (OrzHome) PortableRemoteObject.narrow(lookup,OrzHome.class);
		OrzRemote orzRemote = orzHome.create();
		String result = orzRemote.orz("kaoru");
		System.out.println("result:"+result);
	}

	private static InitialContext getInitialContext(String url)
			throws NamingException {
		System.out.println("url:"+url);
		Hashtable env = new Hashtable();
		env.put(Context.INITIAL_CONTEXT_FACTORY, JNDI_FACTORY);
		env.put(Context.PROVIDER_URL, url);
		return new InitialContext(env);
	}

}
