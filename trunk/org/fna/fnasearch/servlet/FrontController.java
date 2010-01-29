package org.fna.fnasearch.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

public class FrontController extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 2L;

	/**
	 * 
	 */
		
	public void init(ServletConfig config)
	throws ServletException
	{
	    super.init(config);
	}
	
	protected void processRequest(HttpServletRequest request, HttpServletResponse response) throws IOException, ServletException{
		
		request.getRequestDispatcher("/index.jsp").forward(request, response);
		/*
		String url = request.getRequestURI();
		String path = request.getPathTranslated();
		
		response.setContentType("text/html");
		PrintWriter output = response.getWriter();
		output.println("This is the frontcontroller!");
		output.println("url: "+url);
		output.println("path: "+path);
		*/
		
	}
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		
		processRequest(request,response);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		processRequest(request,response);
	}

	
}