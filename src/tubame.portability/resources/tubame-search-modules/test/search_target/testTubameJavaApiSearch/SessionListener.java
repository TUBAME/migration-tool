package ex.servlet;

import javax.servlet.http.HttpSessionEvent;
import javax.servlet.http.HttpSessionListener;

public class SessionListener implements HttpSessionListener {

	public SessionListener() {
		super();
	}

	public void sessionCreated(HttpSessionEvent httpSessionEvent) {
		System.out.println(httpSessionEvent.getSession().getServletContext().getContextPath()+ " sessionCreated:"+httpSessionEvent.getSession().getId());
	}

	public void sessionDestroyed(HttpSessionEvent httpSessionEvent) {
		System.out.println(httpSessionEvent.getSession().getServletContext().getContextPath()+" sessionDestroyed:"+httpSessionEvent.getSession().getId());
	}

}
