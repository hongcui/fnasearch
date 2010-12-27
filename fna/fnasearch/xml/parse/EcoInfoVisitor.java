package org.fna.fnasearch.xml.parse;

import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.fna.fnasearch.rdf.StringLiteralStatement;
import org.fna.fnasearch.util.URI;

public class EcoInfoVisitor extends XMLVisitor {
	
	public EcoInfoVisitor(String treatmentid, String hierarchy) {
		super(treatmentid, hierarchy);
	}
	
	public void doVisit(Element e){
		
		String name = URI.escape(e.getName());
	    String text = e.getTextTrim();
	    //skip root element for the section
	    if(name=="ecological_info"||text==null)return;
	    //skip the ignored tags
	    if(Pattern.compile(URI.eco_ignore).matcher(name).find())return;
	    
	    //default handling
	    String s = URI.baseURI+this.hierarchy;
	    String p = URI.baseURI+URI.has+name;
	    String o = text;
	    //stmt: hierarchy has_ecoinfo text
	    this.statements.add(new StringLiteralStatement(s,p,o));
	    
	    s = URI.baseURI+URI.has+name;
	    p = URI.baseURI+URI.haspropertycate;
	    o = "ecological_info";
		//stmt: this ecoinfo has property category ecological_info
	    this.statements.add(new StringLiteralStatement(s,p,o));
    }
	
    public void doVisit(Attribute attr){
    	//do nothing
    }
}
