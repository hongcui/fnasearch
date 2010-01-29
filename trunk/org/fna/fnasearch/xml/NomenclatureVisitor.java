package org.fna.fnasearch.xml;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.fna.fnasearch.URIFactory;

public class NomenclatureVisitor extends VisitorSupport {
	public String treatmentid = null;
	private List<HashMap<String,String>> statement = new ArrayList<HashMap<String,String>>();
	
	public NomenclatureVisitor(String treatmentid){
		this.treatmentid = treatmentid;
	}
	
	public List<HashMap<String,String>> getVisited(){
		return this.statement;
	}
	
	public void visit(Element element){
        
		String name = element.getName();
		String modifier = element.attributeValue("modifier");   
	    String stringvalue = element.getTextTrim();
	    
	    if(Pattern.compile(URIFactory.nomen_ignore).matcher(name).find()||name=="nomenclature"||stringvalue==null)return;
	    if(Pattern.compile(URIFactory.nomen_taxon_hierarchy).matcher(name).find()){this.parseTaxon(name, modifier, stringvalue);return;}
	    
	    statement.add(new HashMap<String,String>());
		statement.get(statement.size()-1).put("s",URIFactory.baseURI+this.treatmentid);
		statement.get(statement.size()-1).put("p",URIFactory.baseURI+URIFactory.has+name);
		statement.get(statement.size()-1).put("o",stringvalue);
		statement.get(statement.size()-1).put("isres","false");	 
	    
    }
	
	public void parseTaxon(String name,String modifier,String stringvalue){
		Matcher m;
		String last_found = treatmentid;
		//enumerate through taxon
		for(Taxon t: Taxon.values()){
			m = Pattern.compile(t.getregex(),Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ).matcher(stringvalue);
			
			if(m.find()){
				
				statement.add(new HashMap<String,String>());
				statement.get(statement.size()-1).put("s",URIFactory.baseURI+last_found);
				statement.get(statement.size()-1).put("p",URIFactory.baseURI+URIFactory.belongsto);
				statement.get(statement.size()-1).put("o",m.group());
				statement.get(statement.size()-1).put("isres","true");
				
				statement.add(new HashMap<String,String>());
				statement.get(statement.size()-1).put("s",m.group());
				statement.get(statement.size()-1).put("p",URIFactory.baseURI+URIFactory.isinstanceof);
				statement.get(statement.size()-1).put("o",URIFactory.baseURI+t.name());
				statement.get(statement.size()-1).put("isres","true");
				
			}
		}//end of enumerate through taxon
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
