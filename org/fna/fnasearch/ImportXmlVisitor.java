/**
 * 
 */
package org.fna.fnasearch;


import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Document;
import org.dom4j.DocumentException;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.dom4j.io.SAXReader;

/**
 * @author Kenneth
 *
 */
public class ImportXmlVisitor extends VisitorSupport{
	
	Document doc = null;
	SAXReader reader = null;
	TripleStoreHandler store = null;
	
	String treatmentid;
	
	public ImportXmlVisitor(TripleStoreHandler newstore){
		reader = new SAXReader();
		store = newstore;
	}
	
	public void getDoc(String filename){
		/*
		 * initiate file reading and start parsing for basic information
		 * such as the URI of this treatment contained in this file,
		 * source information like volumn, FNA, and all the names the treatment might have
		 */
		try
	      {
	         this.doc = reader.read(filename);
	         Pattern filename_pattern = Pattern.compile("(?<=\\\\)\\d+(?=\\.[a-zA-Z]{3}$)", Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ);
	         Matcher filename_matcher = filename_pattern.matcher(filename);
	         if(filename_matcher.find()){
	        	 String fileid = filename_matcher.group();
	        	 //has to change after new XML formats
	        	 String vol = doc.getRootElement().elements("volumn").size()!=0? doc.getRootElement().element("volumn").getTextTrim():"19";
	        	 this.treatmentid = vol +"-"+fileid;

	         }else{
	        	 throw new DocumentException();
	         }
	     
	      }
	      catch (DocumentException e)
	      {
	         e.printStackTrace();
	      }   
	}
	
	public void visitDoc(){
		try{
			this.doc.getRootElement().accept(this);
		}catch(Exception e){
			System.out.println(e.getClass().toString()+e.getCause()+e.getStackTrace()+e.getMessage());
		}
	}
	
	
	public void visit(Element element){
        
		/*
		 * URIFactory shall decide based on element name, path, parent element, child element, attributes and string value
		 * whether or not and how shall the triple be stated.
		 * A mapping with s,p,o shall then be returned.
		 * The mapping is empty if nothing is supposed to be inserted.
		 */
		
		//retrieve information for the element
		String path = element.getPath();
		String parentpath = element.isRootElement()?element.getPath():element.getParent().getPath();      
	    String value = element.getTextTrim();
	    boolean has_child = (element.elements().size()== 0 ? false:true);
	    boolean has_attr = (element.attributes().size() == 0 ? false:true);
	    
	    //pass the retrieved information to URIFactory
	    Map<String , String> statement = URIFactory.encodeURI(treatmentid , path, parentpath, value, has_child, has_attr);
	    if(!statement.isEmpty()){
	    	//insert the statement into triple store
	    	store.insertTriple(statement.get("s"), statement.get("p"), statement.get("o"), Boolean.getBoolean(statement.get("isres")));
	    }
	    
    }
	
    public void visit(Attribute attr){
    	//retrieve information for the element
		String path = attr.getPath();
		String parentpath = attr.getParent().getPath();      
	    String value = attr.getStringValue();
	    
	    //pass the retrieved information to URIFactory
	    Map<String , String> statement = URIFactory.encodeURI(treatmentid , path, parentpath, value, false, false);
	    if(!statement.isEmpty()){
	    	//insert the statement into triple store
	    	store.insertTriple(statement.get("s"), statement.get("p"), statement.get("o"), Boolean.getBoolean(statement.get("isres")));
	    }
    }
}