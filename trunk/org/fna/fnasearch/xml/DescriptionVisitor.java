package org.fna.fnasearch.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.fna.fnasearch.URIFactory;

public class DescriptionVisitor extends VisitorSupport {

	public String treatmentid = null;
	private List<HashMap<String,String>> statement = new ArrayList<HashMap<String,String>>();
	
	public DescriptionVisitor(String treatmentid){
		this.treatmentid = treatmentid;
	}
	
	public List<HashMap<String,String>> getVisited(){
		return this.statement;
	}
	
	public void visit(Element element){
       
		String name = element.getName();
		String modifier = element.attributeValue("modifier");
	    String stringvalue = element.getTextTrim();
	    
	    if(name=="description")return;
	    if(Pattern.compile(URIFactory.desc_breakdown).matcher(name).find()){this.parseAndOr(name,modifier);return;}
	    
	    statement.add(new HashMap<String,String>());
		statement.get(statement.size()-1).put("s",URIFactory.baseURI+treatmentid);
		statement.get(statement.size()-1).put("p",URIFactory.baseURI+URIFactory.haspart);
		statement.get(statement.size()-1).put("o",URIFactory.baseURI+treatmentid+"/"+(modifier==null?name:modifier+"_"+name));
		statement.get(statement.size()-1).put("isres","true");
		
		statement.add(new HashMap<String,String>());
		statement.get(statement.size()-1).put("s",URIFactory.baseURI+treatmentid+"/"+(modifier==null?name:modifier+"_"+name));
		statement.get(statement.size()-1).put("p",URIFactory.baseURI+URIFactory.hastext);
		statement.get(statement.size()-1).put("o",stringvalue);
		statement.get(statement.size()-1).put("isres","false");
		
		statement.add(new HashMap<String,String>());
		statement.get(statement.size()-1).put("s",URIFactory.baseURI+treatmentid+"/"+(modifier==null?name:modifier+"_"+name));
		statement.get(statement.size()-1).put("p",URIFactory.baseURI+URIFactory.isinstanceof);
		statement.get(statement.size()-1).put("o",URIFactory.baseURI+(modifier==null?name:modifier+"_"+name));
		statement.get(statement.size()-1).put("isres","true");
    }
	
	public void parseAndOr(String name,String modifier){
		String[] brokendown = Pattern.compile(URIFactory.desc_breakdown).split(name);
		//iterate through tagname
		for(int i=0;i<brokendown.length;i++){
			
			statement.add(new HashMap<String,String>());
			statement.get(statement.size()-1).put("s",URIFactory.baseURI+treatmentid);
			statement.get(statement.size()-1).put("p",URIFactory.baseURI+URIFactory.haspart);
			statement.get(statement.size()-1).put("o",URIFactory.baseURI+treatmentid+"/"+(modifier==null?brokendown[i]:modifier+brokendown[i]));
			statement.get(statement.size()-1).put("isres","true");
			
			statement.add(new HashMap<String,String>());
			statement.get(statement.size()-1).put("s",URIFactory.baseURI+treatmentid+"/"+(modifier==null?brokendown[i]:modifier+brokendown[i]));
			statement.get(statement.size()-1).put("p",URIFactory.baseURI+URIFactory.isinstanceof);
			statement.get(statement.size()-1).put("o",URIFactory.baseURI+(modifier==null?brokendown[i]:modifier+brokendown[i]));
			statement.get(statement.size()-1).put("isres","true");
		}//end of iterate through tagname
	}
	
    public void visit(Attribute attr){
    	String name = attr.getName();
		String modifier = attr.getParent().attributeValue("modifier");
		String parentname = attr.getParent().getName();
	    String stringvalue = attr.getStringValue();
	    
	    if(name=="modifier")return;
	    if(Pattern.compile(URIFactory.desc_attr_breakdown).matcher(name).find()){this.parseAttrAndOr(parentname,name,modifier,stringvalue);return;}
	    
	    statement.add(new HashMap<String,String>());
	    statement.get(statement.size()-1).put("s", URIFactory.baseURI+treatmentid+"/"+(modifier==null? parentname:modifier+"_"+parentname));
	    statement.get(statement.size()-1).put("p", URIFactory.baseURI+URIFactory.has+name);
	    statement.get(statement.size()-1).put("o", stringvalue);
	    statement.get(statement.size()-1).put("isres", "false");
    }
    
    public void parseAttrAndOr(String parentname,String name,String modifier,String stringvalue){
    	String[] brokendown = Pattern.compile(URIFactory.desc_attr_breakdown).split(name);
		//iterate through tagname
		for(int i=0;i<brokendown.length;i++){
			
			statement.add(new HashMap<String,String>());
			statement.get(statement.size()-1).put("s",URIFactory.baseURI+treatmentid+"/"+(modifier==null? parentname:modifier+"_"+parentname));
			statement.get(statement.size()-1).put("p",URIFactory.baseURI+URIFactory.has+brokendown[i]);
			statement.get(statement.size()-1).put("o",stringvalue);
			statement.get(statement.size()-1).put("isres","false");
		}
    }
}
