/**
 * 
 */
package org.fna.fnasearch.xml;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.io.SAXReader;
import org.fna.fnasearch.rdf.JenaRDFStoreHandler;
import org.fna.fnasearch.rdf.RDFStatement;
import org.fna.fnasearch.rdf.RDFStoreHandlerException;
import org.fna.fnasearch.xml.parse.XMLVisitor;
import org.fna.fnasearch.xml.parse.XMLsections;


/**
 * @author Kenneth
 *
 */
public class XMLReader extends Reader {
	
	private Document doc = null;
	private SAXReader reader = null;
	public JenaRDFStoreHandler store = null;
	public String fnasrc;
	public String fnavol;
	public String treatmentid;
	public String hierarchy;
	private Logger logger;
	
	public XMLReader(){
		this.logger = Logger.getLogger(this.getClass().getName());
	}
	
	public XMLReader(JenaRDFStoreHandler newstore){
		this.store = newstore;
		this.logger = Logger.getLogger(this.getClass().getName());
	}

	public void getDoc(String filename){
		/*
		 * initiate file reading and start parsing for basic information
		 * 
		 */
		try
	      {
			this.reader = new SAXReader(); 
			this.doc = reader.read(filename);
			this.logger.debug("loading file: "+filename);
	        Pattern filename_pattern = Pattern.compile("(?<=[\\\\/])\\d+(?=.[a-zA-Z]{3}$)", Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ);
	        Matcher filename_matcher = filename_pattern.matcher(filename);
	        if(filename_matcher.find()){
	        	 String fileid = filename_matcher.group();
	        	 String src = doc.getRootElement().element("meta").elements("source").size()!=0? doc.getRootElement().element("meta").element("source").getTextTrim():"source_missing";
	        	 String vol = doc.getRootElement().element("meta").elements("volume").size()!=0? doc.getRootElement().element("meta").element("volume").getTextTrim():"vol_id_missing";
	        	 this.hierarchy = doc.getRootElement().element("nomenclature").elements("taxon_hierarchy").size()!=0? doc.getRootElement().element("nomenclature").element("taxon_hierarchy").getTextTrim():"hierarchy_missing";
	        	 this.fnasrc = src;
	        	 this.fnavol = vol;
	        	 this.treatmentid = src+"-"+vol +"-"+fileid;

	         }else{
	        	 throw new DocumentException();
	         }
	     
	      }
	      catch (DocumentException e)
	      {
	         this.logger.error("Read file:"+filename+"failed");
	      }   
	}
		
	@SuppressWarnings({ "rawtypes", "unchecked" })
	public void readDoc() throws XMLReaderException{
		if(this.treatmentid==null||this.hierarchy==null){
			return;
		}
		for(XMLsections sec: XMLsections.values()){
			try {

				Class<?> cls = Class.forName(sec.getClassname());
				Class[] types = new Class[] { String.class, String.class};
				Constructor<XMLVisitor> constructor = (Constructor<XMLVisitor>) cls.getDeclaredConstructor(types);
				this.logger.debug(constructor.getName());
				Object[] args = new Object[] {this.treatmentid,this.hierarchy };
				XMLVisitor visitor = constructor.newInstance(args);
				Method method = cls.getMethod("getVisited");
				if(this.doc.getRootElement().element(sec.name().toString())!=null){
					this.doc.getRootElement().element(sec.name().toString()).accept(visitor);
				}
				
				@SuppressWarnings("unchecked")
				ArrayList<RDFStatement> statements =  (ArrayList<RDFStatement>) method.invoke(visitor);
				
				if(!statements.isEmpty()){
					Iterator<RDFStatement> itr = statements.iterator();
					while(itr.hasNext()){
						RDFStatement stmt = itr.next();
						this.logger.debug(stmt.toString());
						if(this.store!=null){
							store.insertStmt(stmt);
						}
					}
				}

			}catch(ClassNotFoundException e){
				this.logger.error("class: org.fna.fnasearch.xml."+sec.getClassname()+" is not found", e);
			}catch (SecurityException e) {
				this.logger.error(e.getMessage(),e);
			} catch (NoSuchMethodException e) {
				this.logger.error(e.getMessage(),e);
			} catch (IllegalArgumentException e) {
				this.logger.error(e.getMessage(),e);
			} catch (InstantiationException e) {
				this.logger.error(e.getMessage(),e);
			} catch (IllegalAccessException e) {
				this.logger.error(e.getMessage(),e);
			} catch (InvocationTargetException e) {
				this.logger.error(e.getMessage(),e);
			}catch (RDFStoreHandlerException e){
				throw new XMLReaderException("visit doc failed", e);
			}finally{

			}
		}
	}

}