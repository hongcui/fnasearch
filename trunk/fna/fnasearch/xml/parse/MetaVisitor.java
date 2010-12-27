package org.fna.fnasearch.xml.parse;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.fna.fnasearch.rdf.ResourceStatement;
import org.fna.fnasearch.util.URI;

public class MetaVisitor extends XMLVisitor {

	public MetaVisitor(String treatmentid, String hierarchy){
		super(treatmentid,hierarchy);
		//stmt: this file belongs_to this taxon hierarchy
	    String s = URI.baseURI+this.treatmentid;
	    String p = URI.baseURI+URI.belongsto;
	    String o = URI.baseURI+this.hierarchy;
	    this.statements.add(new ResourceStatement(s,p,o));
	}
	
	protected void doVisit(Element element){
		
		String name = URI.escape(element.getName());
		String text = element.getTextTrim();
	    //skip the root of this section
	    if(name.equals("meta")||text==null)return;
	    //any special case to delegate to subclass
	    //source
	    if(name.equals("source") && text != null){
	    	this.visitSource(element);
	    	return;
	    }
	    //volume
	    if(name.equals("volume") && text != null){
	    	this.visitVolume(element);
	    	return;
	    }
	    
	    //default statment
	    String s = URI.baseURI+this.treatmentid;
	    String p = URI.baseURI+URI.has+name;
	    //stmt: this_file has_xxxx "text"
		this.statements.add(new ResourceStatement(s,p,text));
		
    }
	
    protected void doVisit(Attribute attr){
    	//do nothing
    }
    
    private void visitSource(Element e){
		String text = e.getTextTrim();
		String s = URI.baseURI+this.treatmentid;
		//stmt: this_file has_source FNA
		this.statements.add(new ResourceStatement(s, URI.baseURI+URI.hassource, text));		
		//stmt: FNA is_instance_of SOURCE
		this.statements.add(new ResourceStatement(text, URI.baseURI+URI.isinstanceof, URI.baseURI+"SOURCE"));	
    }
    
    private void visitVolume(Element e){
		//stmt:this_file has_source "volume 19"
    	String s = URI.baseURI+this.treatmentid;
		String p = URI.baseURI+URI.hassource;
		String o = e.getTextTrim();
		this.statements.add(new ResourceStatement(s,p,o));
		//stmt: "volume_19" is_part_of "FNA"
		s = o;
		p = URI.baseURI+URI.ispartof;
		o = e.getParent().element("source").getTextTrim();
		this.statements.add(new ResourceStatement(s, p, o));
		//stmt: "volume 19" is an instance of VOLUME
		p = URI.baseURI+URI.isinstanceof;
		this.statements.add(new ResourceStatement(s,p,URI.baseURI+"VOLUME"));
		//SOURCE has_volume VOLUME
		p = URI.baseURI+URI.hasvolume;
		this.statements.add(new ResourceStatement(URI.baseURI+"SOURCE",p,URI.baseURI+"VOLUME"));
		//VOLUME is_part_of SOURCE
		p = URI.baseURI+URI.ispartof;
		this.statements.add(new ResourceStatement(URI.baseURI+"VOLUME",p,URI.baseURI+"SOURCE"));
    }
}
