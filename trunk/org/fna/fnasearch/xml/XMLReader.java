/**
 * 
 */
package org.fna.fnasearch.xml;


import java.lang.reflect.Constructor;
import java.lang.reflect.InvocationTargetException;
import java.lang.reflect.Method;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Visitor;
import org.dom4j.io.SAXReader;
import org.fna.fnasearch.rdfstore.TripleStoreHandler;


/**
 * @author Kenneth
 *
 */
public class XMLReader {
	
	Document doc = null;
	SAXReader reader = null;
	public TripleStoreHandler store = null;

	String treatmentid;
	
	public XMLReader(){
		this.reader = new SAXReader();
	}
	
	public XMLReader(TripleStoreHandler newstore){
		this.reader = new SAXReader();
		this.store = newstore;
	}

	public void getDoc(String filename){
		/*
		 * initiate file reading and start parsing for basic information
		 * 
		 */
		try
	      {
	         this.doc = reader.read(filename);
	         Pattern filename_pattern = Pattern.compile("(?<=\\\\)\\d+(?=\\.[a-zA-Z]{3}$)", Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ);
	         Matcher filename_matcher = filename_pattern.matcher(filename);
	         if(filename_matcher.find()){
	        	 String fileid = filename_matcher.group();
	        	 String src = doc.getRootElement().element("meta").elements("source").size()!=0? doc.getRootElement().element("meta").element("source").getTextTrim():"source_missing";
	        	 String vol = doc.getRootElement().element("meta").elements("volume").size()!=0? doc.getRootElement().element("meta").element("volume").getTextTrim():"vol_id_missing";
	        	 this.treatmentid = src+"-"+vol +"-"+fileid;

	         }else{
	        	 throw new DocumentException();
	         }
	     
	      }
	      catch (DocumentException e)
	      {
	         e.printStackTrace();
	      }   
	}
	
	@SuppressWarnings("unchecked")
	public void visitDoc(){
		
		for(Xmlsections sec: Xmlsections.values()){
			try {
					
					Class cls = Class.forName("org.fna.fnasearch.xml."+sec.getClassname());
					Class[] types = new Class[] { String.class};
					Constructor<Visitor> constructor = cls.getConstructor(types);
					Object[] args = new Object[] {this.treatmentid };
					Visitor visitor = constructor.newInstance(args);
					Method method = cls.getMethod("getVisited");
										
					this.doc.getRootElement().element(sec.name().toString()).accept(visitor);
					
					//pass the retrieved information to URIFactory
				    List<HashMap<String , String>> statement =  (List<HashMap<String, String>>) method.invoke(visitor);
				    
				    if(!statement.isEmpty()){
				    	Iterator<HashMap<String, String>> itr = statement.iterator();
				    	HashMap<String, String> stmt = new HashMap<String, String>();
				    	while(itr.hasNext()){
				    		stmt = itr.next();
				    		System.out.println(stmt.get("s")+"\t"+stmt.get("p")+"\t"+stmt.get("o")+"\t"+stmt.get("isres"));
				    		//insert the statement into triple store
					    	store.insertTriple(stmt.get("s"), stmt.get("p"), stmt.get("o"), Boolean.getBoolean(stmt.get("isres")));
				    	}
				    }
					
					
			}  catch (ClassNotFoundException e){
				e.printStackTrace();
			} catch (SecurityException e) {
				e.printStackTrace();
			} catch (NoSuchMethodException e) {
				e.printStackTrace();
			} catch (IllegalArgumentException e) {
				e.printStackTrace();
			} catch (InstantiationException e) {
				e.printStackTrace();
			} catch (IllegalAccessException e) {
				e.printStackTrace();
			} catch (InvocationTargetException e) {
				e.printStackTrace();
			} 
		}
		
	}
	
}