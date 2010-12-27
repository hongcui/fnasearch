package org.fna.fnasearch.servlet;

import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.fna.fnasearch.rdf.JenaRDFStoreHandler;
import org.fna.fnasearch.xml.XMLImporter;
import org.fna.fnasearch.xml.ImporterException;

public class ImportFromXML extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String storepath;
	private XMLImporter importer;
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
		PrintWriter output = response.getWriter();
		try{
			//get importer instance
			importer = XMLImporter.getInstance();
			//parse the requrest
			boolean formatstore;
			String strformatstore = request.getParameter("format");
			if(strformatstore == null){
				formatstore = false;
			}else{
				formatstore = Boolean.valueOf(strformatstore);
			}
			String src = getServletContext().getRealPath(request.getParameter("src"));
			if(src == null)src = getServletContext().getRealPath(getServletContext().getInitParameter("importpath"));
			String arc = getServletContext().getRealPath(request.getParameter("arc"));
			if(arc == null)arc = getServletContext().getRealPath(getServletContext().getInitParameter("docspath"));
			
			//construct the importer
			this.logger.debug(this.storepath);
			importer.doImport(new JenaRDFStoreHandler(this.storepath), formatstore,src,arc);
			
		}catch(ImporterException e){
			this.error = "Servlet ImportFromXML - "+e.getMessage();
			this.logger.error(this.error, e);
		}catch (Throwable t){
			this.error = "Servlet ImportFromXML - error";
			this.logger.error(this.error, t);
		}finally{
			output.close();
		}
		
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
	
		
	}
	
}