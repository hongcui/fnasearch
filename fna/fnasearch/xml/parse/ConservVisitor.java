package org.fna.fnasearch.xml.parse;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.fna.fnasearch.rdf.BooleanLiteralStatement;
import org.fna.fnasearch.rdf.StringLiteralStatement;
import org.fna.fnasearch.util.URI;

public class ConservVisitor extends XMLVisitor {
	
	public ConservVisitor(String treatmentid, String hierarchy) {
		super(treatmentid,hierarchy);
	}
	
	public void doVisit(Element e){
		
		String name = URI.escape(e.getName());
	    String text = e.getTextTrim();
	    
	    String s = URI.baseURI+this.hierarchy;
	    String p = URI.baseURI+URI.has+name;
	    boolean o = Boolean.valueOf(text);
	    //stmt: hierarchy has_conservation true/false
	    this.statements.add(new BooleanLiteralStatement(s,p,o));
	    
	    //stmt: this conservation has_property_category conservation
	    s = URI.baseURI+URI.has+name;
	    p = URI.baseURI+URI.haspropertycate;
	    this.statements.add(new StringLiteralStatement(s,p,"conservation"));
		
    }
	
    public void doVisit(Attribute attr){
    	//do nothing
    }
}
