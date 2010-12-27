package org.fna.fnasearch.xml.parse;

import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.fna.fnasearch.rdf.ResourceStatement;
import org.fna.fnasearch.util.URI;

public class NomenclatureVisitor extends XMLVisitor {
	
	public NomenclatureVisitor(String treatmentid,String hierarchy){
		super(treatmentid, hierarchy);
	}
	
	public void doVisit(Element element){
		String name = URI.escape(element.getName());
	    String text = element.getTextTrim();
	    //skip root tag for the section
	    if(Pattern.compile(URI.nomen_ignore).matcher(name).find()||name.equals("nomenclature")||text==null)return;
	    
	    //special handling for taxon_hierarchy tag
	    if(Pattern.compile(URI.nomen_taxon_hierarchy).matcher(name).find()){
	    	XMLVisitor h = new TaxonHierarchyVisitor(this.treatmentid,this.hierarchy);
	    	element.accept(h);
	    	this.statements.addAll(h.getVisited());
	    	return;
	    }
	    
	    //default handling
	    //stmt: hierarchy has_xxx yyy
	    String s = URI.baseURI+this.hierarchy;
	    String p = URI.baseURI+URI.has+name;
	    this.statements.add(new ResourceStatement(s,p,text));
	    //stmt: yyy is_instance_of xxx
	    s = text;
	    p = URI.baseURI+URI.isinstanceof;
	    String o = URI.baseURI+name.toUpperCase();
	    this.statements.add(new ResourceStatement(s,p,o));
    }
	
    public void doVisit(Attribute attr){
    	//do nothing
    }
}
