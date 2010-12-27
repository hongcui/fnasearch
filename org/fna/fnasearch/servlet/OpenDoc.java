package org.fna.fnasearch.servlet;

import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.PrintWriter;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.ServletOutputStream;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.fna.fnasearch.util.DocMIME;
import org.fna.fnasearch.util.DocTypes;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class OpenDoc extends HttpServlet{

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String docpath;
	private Logger logger;
	
	public void init(ServletConfig config)
	throws ServletException
	{
	    super.init(config);
	    this.docpath = getServletContext().getRealPath(getServletContext().getInitParameter("docspath"));
	    this.logger = Logger.getLogger(this.getClass().getName());
	    this.logger.debug("Docs path is :"+this.docpath);
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		this.doPost(request, response);
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		String uri = request.getParameter("uri")!=null?request.getParameter("uri").replace("http://www.fna.org/", ""):null;
		String docname = request.getParameter("docname")!=null?request.getParameter("docname"):null;
		this.logger.debug("parameter uri= "+uri);
		this.logger.debug("parameter docname= "+docname);
		
		//check supplied parameters, if request by URI, return in json all files attached to the URI
		//if request by docname, return a MIME type based on the document
		if(uri!=null&&docname==null){
			//return json listing all files attached to the uri
			PrintWriter output = response.getWriter();
			JSONObject result = new JSONObject();
			JSONArray list = new JSONArray();
			String[] breakdownuri = uri.split("[-.]");
			for(int i=0;i<breakdownuri.length;i++)
			this.logger.debug("break down uri index "+i+", value is "+breakdownuri[i]);
			
			try{
				//check for each possible file type
				for(DocTypes doctypes: DocTypes.values()){
					File doc = new File(this.docpath+"/"+breakdownuri[0]+"/"+breakdownuri[1]+"/"+uri+doctypes.getPattern()+doctypes.getExt());
					this.logger.debug(doc.getAbsolutePath());
					if(doc.isFile()&&doc.canRead()){
						JSONObject item = new JSONObject();
						item.put("docname",doc.getName());
						list.add(item);
					}
					//if non found, try again with the file name reduced to the document name
					//i.e. if above FNA-19-1.* has nothing found in /FNA/19/ now try with /FNA/19/1.*
					doc = new File(this.docpath+"/"+breakdownuri[0]+"/"+breakdownuri[1]+"/"+breakdownuri[breakdownuri.length-1]+doctypes.getPattern()+doctypes.getExt());
					this.logger.debug(doc.getAbsolutePath());
					if(doc.isFile()&&doc.canRead()){
						JSONObject item = new JSONObject();
						item.put("docname",doc.getName());
						list.add(item);
					}
				}
				//if still non found, throw exception
				if(list.size()==0)throw new FileNotFoundException("File not found with uri - "+uri);
				result.put("docs", list);
				result.put("uri", uri);
				response.setContentType("json");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Connection", "close");
				response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
				response.setHeader("Pragma","no-cache"); //HTTP 1.0
				response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
				output.print(result);
				
			}catch(FileNotFoundException e){
				result.put("error", e.getMessage());
				output.print(result);
				this.logger.debug("File not found with uri - "+uri);
			}catch(Throwable t){
				this.logger.error("Servlet OpenDoc - retrieve doc list erros", t);
			}finally{
				output.close();
			}
			
			
		}else if(docname!=null&&uri!=null){
			//return MIME stream
			//set output writer
			ServletOutputStream  out = response.getOutputStream ();
			//initialize file streams
			BufferedInputStream  instream = null; 
			BufferedOutputStream outstream = null;
			//parse the file name,extension,path
			String[] breakdownuri = uri.split("[-.]");
			String[] breakdowndocname = docname.split("[-.]");
			
			//try to get the file
			try{
				//construct the file
				File doc = new File(this.docpath+"/"+breakdownuri[0]+"/"+breakdownuri[1]+"/"+docname);
				this.logger.debug(doc.getAbsolutePath());
				if(doc.isFile()&&doc.canRead()){
					//set response header
					response.setContentType(DocMIME.valueOf(breakdowndocname[breakdowndocname.length-1]).getMIME());
					response.setHeader("Content-disposition","inline;filename="+docname );
					
					//read the file
					instream = new BufferedInputStream(new FileInputStream(doc));
					outstream = new BufferedOutputStream(out);
					byte[] buff = new byte[2048];
					int bytesRead;
					// Simple read/write loop.
					while(-1 != (bytesRead = instream.read(buff, 0, buff.length))) {
						outstream.write(buff, 0, bytesRead);
					}
				}else{
					throw new FileNotFoundException("File not found - "+doc.getName());
				}
			}catch(FileNotFoundException e){
				out.write(e.getMessage().getBytes());
			}catch(Throwable t){
				this.logger.error("Servlet OpenDoc - open doc error",t);
			}finally{
				if(instream != null) instream.close();
				if(outstream != null)outstream.close();
				out.close();
			}
		}
	}
	
}
