package org.fna.fnasearch.servlet;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.io.PrintWriter;
import java.util.ArrayList;
import java.util.Date;
import java.util.Iterator;
import java.util.List;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.fna.fnasearch.util.DocTypes;
import org.fna.fnasearch.util.ParseJSON;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class Zipfiles extends HttpServlet{

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
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		this.doPost(request, response);
	}

	@SuppressWarnings("unchecked")
	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		try{
		//get the requested docs
		String reqdocs = request.getParameter("reqdocs");
		String[] docids = reqdocs.split(",");
		List<String> docs = new ArrayList<String>();
		for(int i=0;i<docids.length;i++)docs.add(docids[i]);

		//create a zipfile with timestamp as the name
		Date d = new Date();
		String zipname = Long.toString(d.getTime());
		CRC32 crc = new CRC32();
		byte[] buffer = new byte[4096]; // Create a buffer for copying
		int bytesRead;
		File temp = new File(this.docpath+"/temp");
		if(!temp.isDirectory())temp.mkdirs();
		ZipOutputStream zipout = new ZipOutputStream((OutputStream)new FileOutputStream(this.docpath+"/temp/"+zipname+".zip"));
		zipout.setLevel(6);
		try{
			//for each one look up files
			Iterator<String> itr = docs.iterator();
			while(itr.hasNext()){
				String docid = itr.next();
				String[] breakdownuri = docid.split("[-.]");
				for(DocTypes doctypes: DocTypes.values()){
					File doc = new File(this.docpath+"/"+breakdownuri[0]+"/"+breakdownuri[1]+"/"+docid+doctypes.getPattern()+doctypes.getExt());
					if(doc.isFile()&&doc.canRead()){
						//add to the zip
						this.logger.debug("adding file: "+doc.getAbsolutePath());
						FileInputStream zipin = new FileInputStream(doc); // Stream to read file
						ZipEntry entry = new ZipEntry(doc.getName()); // Make a ZipEntry
						entry.setSize((long)buffer.length);
						crc.reset();
						crc.update(buffer);
						entry.setCrc( crc.getValue());
						zipout.putNextEntry(entry); // Store entry
						while ((bytesRead = zipin.read(buffer)) != -1)
							zipout.write(buffer, 0, bytesRead);
						zipin.close(); 
					}else{
						//if non found, try again with the file name reduced to the document name
						//i.e. if above FNA-19-1.* has nothing found in /FNA/19/ now try with /FNA/19/1.*
						doc = new File(this.docpath+"/"+breakdownuri[0]+"/"+breakdownuri[1]+"/"+breakdownuri[breakdownuri.length-1]+doctypes.getPattern()+doctypes.getExt());
						if(doc.isFile()&&doc.canRead()){
							//add to the zip
							this.logger.debug("adding file: "+doc.getAbsolutePath());
							FileInputStream in = new FileInputStream(doc); // Stream to read file
							ZipEntry entry = new ZipEntry(doc.getName()); // Make a ZipEntry
							entry.setSize((long)buffer.length);
							crc.reset();
							crc.update(buffer);
							entry.setCrc( crc.getValue());
							zipout.putNextEntry(entry); // Store entry
							while ((bytesRead = in.read(buffer)) != -1)
								zipout.write(buffer, 0, bytesRead);
							in.close(); 
						}else{
							//ignore this doc
						}
					}
				}
			}
		}catch(Throwable t){
			this.logger.debug(t.getMessage());
		}finally{
			zipout.close();
		}

		//send the zipfile link back to the client
		//try to get the file
		try{
			//construct the file
			File doc = new File(this.docpath+"/temp/"+zipname+".zip");
			this.logger.debug(doc.getAbsolutePath());
			if(doc.isFile()&&doc.canRead()){
				//set response header
				response.setContentType("text");
				response.setCharacterEncoding("UTF-8");
				response.setHeader("Connection", "close");
				response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
				PrintWriter output = response.getWriter();
				output.print(getServletContext().getInitParameter("docspath")+"/temp/"+zipname+".zip");
			}else{
				throw new FileNotFoundException("File not found - "+doc.getName());
			}
		}catch(FileNotFoundException e){
			this.logger.debug(e.getMessage());
		}catch(Throwable t){
			this.logger.error("Servlet Zipfiles - open zip error",t);
		}
	
	}catch(Throwable t){
		this.logger.error(t.getMessage());
	}
	}

}