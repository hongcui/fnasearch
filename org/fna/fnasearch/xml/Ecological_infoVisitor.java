package org.fna.fnasearch.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.fna.fnasearch.URIFactory;

public class Ecological_infoVisitor extends VisitorSupport {
	public String treatmentid = null;
	private List<HashMap<String,String>> statement = new ArrayList<HashMap<String,String>>();
	
	public Ecological_infoVisitor(String treatmentid){
		this.treatmentid = treatmentid;
	}
	
	public List<HashMap<String,String>> getVisited(){
		return this.statement;
	}
	
public void visit(Element element){
		
		String name = element.getName();
	    String stringvalue = element.getTextTrim();
	    
	    if(name=="ecological_info"||stringvalue==null)return;
	    
		statement.add(new HashMap<String,String>());
		statement.get(statement.size()-1).put("s",URIFactory.baseURI+this.treatmentid);
		statement.get(statement.size()-1).put("p",URIFactory.baseURI+URIFactory.has+name);
		statement.get(statement.size()-1).put("o",stringvalue);
		statement.get(statement.size()-1).put("isres","false");	    
    }
	
    public void visit(Attribute attr){
    	
		String name = attr.getName();
		String modifier = attr.getParent().attributeValue("modifier");
		String parentname = attr.getParent().getName();
	    String stringvalue = attr.getStringValue();
	    
	    if(name=="modifier")return;
	    statement.add(new HashMap<String,String>());
	    statement.get(statement.size()-1).put("s", URIFactory.baseURI+treatmentid+"/"+(modifier==null? parentname:modifier+"_"+parentname));
	    statement.get(statement.size()-1).put("p", URIFactory.baseURI+URIFactory.has+name);
	    statement.get(statement.size()-1).put("o", stringvalue);
	    statement.get(statement.size()-1).put("isres", "false");
    }
}
