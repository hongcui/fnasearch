package org.fna.fnasearch.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class SearchControl extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public void init(ServletConfig config)
	throws ServletException
	{
	    super.init(config);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		String url = request.getRequestURI();
		String path = request.getPathTranslated();
		
		response.setContentType("text/html");
		PrintWriter output = response.getWriter();
		output.println("This is the search servlet!");
		output.println("url: "+url);
		output.println("path: "+path);
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		response.setContentType("text/html");
		String uri = request.getParameter("uri");
		PrintWriter output = response.getWriter();
		output.println(uri);
	}

	
}