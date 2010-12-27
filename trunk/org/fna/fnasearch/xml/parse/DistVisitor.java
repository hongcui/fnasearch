package org.fna.fnasearch.xml.parse;

import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.fna.fnasearch.rdf.StringLiteralStatement;
import org.fna.fnasearch.util.URI;

public class DistVisitor extends XMLVisitor {
	
	public DistVisitor(String treatmentid, String hierarchy) {
		super(treatmentid, hierarchy);
	}

	public void doVisit(Element e){
		
		String name = URI.escape(e.getName());
	    String text = e.getTextTrim();
	    
	    //skip the root tag of the section
	    if(name=="distribution"||text==null)return;
	    //skip the ignored tags
	    if(Pattern.compile(URI.eco_ignore).matcher(name).find())return;
	    
	    //handle special cases
	    if(name=="elevation"){
	    	XMLVisitor elev = new ElevationVisitor(this.treatmentid, this.hierarchy);
	    	e.accept(elev);
	    	this.statements.addAll(elev.getVisited());	 
	    }
	    
	    //default handling
	    String s = URI.baseURI+this.hierarchy;
	    String p = URI.baseURI+URI.has+name;
	    //stmt: hierarchy has_xxx text
	    this.statements.add(new StringLiteralStatement(s,p,text));
	    
		s = URI.baseURI+URI.has+name;
		p = URI.baseURI+URI.haspropertycate;
		//stmt: has_xxx has_property_category distribution
		this.statements.add(new StringLiteralStatement(s,p,"distribution"));
    }
	
    public void doVisit(Attribute attr){
    	//do nothing
    }
}
