package org.fna.fnasearch.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.fna.fnasearch.query.QueryExec;
import org.fna.fnasearch.query.QueryExecException;

public class Search extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String storepath;
	private Logger logger;
	private String error;
	
	public void init(ServletConfig config)
	throws ServletException
	{
	    super.init(config);
	    this.storepath = getServletContext().getRealPath("/WEB-INF/sdb.ttl");
	    this.storepath = "file:///"+this.storepath;
	    this.logger = Logger.getLogger(this.getClass().getName());
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		this.doPost(request, response);
//		response.setContentType("text/html");
//		PrintWriter output = response.getWriter();
//		output.println("Invalid parameter. If you copied or input the URL, please use the interface.");
//		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		
		String querytext = "";
		QueryExec query = new QueryExec(this.storepath);
		query.setDocPath(getServletContext().getRealPath(getServletContext().getInitParameter("docspath")));
		PrintWriter output = response.getWriter();
		try{
			do{
				querytext += request.getReader().readLine();
			}while(request.getReader().readLine()!=null);
			
			//call the querybuilder to build the text into valid sparql
			query.buildSelect(querytext);
			
			//test the query error, if error, return error msg, otherwise, submit query to triplestore
			if(query.getError()!= null){
				
				response.setContentType("text");
				output.println("text-received: "+querytext+"<br/>");
				output.println("Query Error: "+query.getError()+"<br/>");
			}else{
				//query the store
				query.execQuery();
				
				response.setContentType("json");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Connection", "close");
				response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
				response.setHeader("Pragma","no-cache"); //HTTP 1.0
				response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
				output.print(query.getJsonresult());
				this.logger.trace(query.getJsonresult());
				
			}
		}catch(QueryExecException e){
			this.error = "Servlet Search - "+e.getMessage()+" "+e.getQuery();
			this.logger.error(this.error, e);
		}catch (Throwable t){
			this.error = "Servlet Search - error";
			this.logger.error(this.error, t);
		}finally{
			output.close();
		}
		
	}
	
}