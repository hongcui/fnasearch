/**
 * 
 */
package org.fna.fnasearch;


import java.io.File;

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
	
	static String baseURI = "http://www.fna.org/fnasearch/";
	Document doc = null;
	SAXReader reader = null;
	TripleStoreHandler store = null;
	
	String treatment = null;
	
	public ImportXmlVisitor(){
		reader = new SAXReader();
		store = new TripleStoreHandler("f:\\projects\\fnasearch\\sdb.ttl");
	}
	
	public void getDoc(String filename){
		try
	      {
	         this.doc = reader.read(filename);
	         Element element = doc.getRootElement();
	         String treatment_id = element.element("number").getStringValue();
	       	 String volumn = "volumn_19"; //should be read somewhere
	       	 this.treatment = ImportXmlVisitor.encodeTreatmentURI(volumn, treatment_id);
	       	 
	      }
	      catch (DocumentException e)
	      {
	         e.printStackTrace();
	      }   
	}
	
	public void visitDoc(){
		try{
			this.doc.getRootElement().element("description").accept(this);
		}catch(Exception e){
			System.out.println(e.getClass().toString()+e.getCause()+e.getStackTrace()+e.getMessage());
		}
	}
	
	public static String encodeTreatmentURI(String volumn, String treatment_id){
		return baseURI+volumn+"/"+treatment_id;
	}
	
	public static String encodeCharacterURI(String character ){
		return baseURI+"characters#"+character;
	}
	
	public void visit(Element element){
        String element_name = element.getName();
        if(element_name != "description"){
	        String element_value = element.getStringValue();
	        boolean element_has_child = (element.elements().size()== 0 ? false:true);
	        boolean element_has_attr = (element.attributes().size() == 0 ? false:true);
	        boolean isres;
	        
	        String s = element.getParent().getPath().replace("/treatment/description", this.treatment);
	        String p = ImportXmlVisitor.encodeCharacterURI(element_name);
	        String o = null;
	        if (element_has_child == false && element_has_attr == false){
	        	o = element_value;
	        	isres = false;
	        }else{
	        	o = element.getPath().replace("/treatment/description", this.treatment);
	        	isres = true;
	        }
	        //System.out.println(s+"\t\t\t"+p+"\t\t\t"+o+"\t"+isres);
	        store.insertTriple(s, p, o, isres);
        }
    }
	
    public void visit(Attribute attr){
        String attr_name = attr.getName();
        String attr_value = attr.getValue();
        
        String s = attr.getParent().getPath().replace("/treatment/description", this.treatment);;
        String p = attr_name;
        Element temp = attr.getParent();
        do{
        	p = temp.getName()+"_"+p;
        	temp = temp.getParent();
        }while(temp.getName() != "description");
        p = ImportXmlVisitor.encodeCharacterURI(p);
        String o = attr_value;
        boolean isres = false;
        
        //System.out.println(s+"\t\t\t"+p+"\t\t\t"+o+"\t"+isres);
        store.insertTriple(s, p, o, isres);
    }
    
	public static void main(String[] args){
				/*
		        File dir = new File("f:\\projects\\fnasearch\\final"); 
		        File[] files = dir.listFiles(); 
		        if (files == null){
		        	System.out.print("No file(s) found.");
		        	return;
		        }else{
		        	ImportXmlVisitor myvisitor = new ImportXmlVisitor();
		        	myvisitor.store.formatStore();
		        	for (int i = 0; i < files.length; i++) { 
				            if (!files[i].isDirectory()) {
				            	String filename = files[i].getName();
				                String extname = filename.substring(filename.indexOf(".")+1);
				            	//System.out.println(extname);
				                if (extname.equals("xml")){
				                	String fullpath = files[i].getAbsolutePath();
				                	System.out.println(fullpath);
				                	myvisitor.getDoc(fullpath);
				                	//System.out.println(myvisitor.treatment);
				                	myvisitor.visitDoc();
				            	}    
				            } 
				        } 
		        	myvisitor.store.closeStore();
		        }
		        */	        
		
		ImportXmlVisitor myvisitor = new ImportXmlVisitor();
		myvisitor.store.formatStore();
		myvisitor.getDoc("f:\\projects\\fnasearch\\final\\1.xml");
		myvisitor.visitDoc();
		myvisitor.store.writeStore();
		myvisitor.store.closeStore();
	}
}