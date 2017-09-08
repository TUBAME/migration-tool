package ex.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpSession;

public class SessionServlet extends HttpServlet {

	static String PAGE_HEADER = "<html><head /><body>";

	static String PAGE_FOOTER = "</body></html>";
	
	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {
		PrintWriter writer = resp.getWriter();
		writer.println(PAGE_HEADER);
		HttpSession session = req.getSession(false);

		Integer count = null;
		if (session != null){
			writer.println("<p>session:"+session.getId() +"</p>");
			count = (Integer) session.getAttribute("count");
			if( count == null){
				writer.println("count null in session:"+session.getId());
				count = 0;
			}
			count++ ;
			session.setAttribute("count", count);
		}else{
			count = 0;
			session = req.getSession(true);
			writer.println("<p>session new:"+session.getId()+"</p>");
			session.setAttribute("count", count);
		}
		
		writer.println("count:"+count);
		writer.close();

	}


}
