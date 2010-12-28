package org.fna.fnasearch.xml.parse;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.fna.fnasearch.rdf.DoubleLiteralStatement;
import org.fna.fnasearch.rdf.IntLiteralStatement;
import org.fna.fnasearch.rdf.LiteralStatement;
import org.fna.fnasearch.rdf.PosIntLiteralStatement;
import org.fna.fnasearch.rdf.ResourceStatement;
import org.fna.fnasearch.rdf.StringLiteralStatement;
import org.fna.fnasearch.util.URI;
import org.fna.fnasearch.util.Unit;

public class DescVisitor extends XMLVisitor {

	private String struct;
	
	public DescVisitor(String treatmentid, String hierarchy) {
		super(treatmentid, hierarchy);
	}
	
	@Override
	public void doVisit(Element e){
		
		String name = URI.escape(e.getName());	    
	    //skip the root tag of the section
	    if(name.equals("description"))return;
	    
	    //ignore the statement tag
	    if(name.equals("statement"))return;
	    
	    //handle structure
	    if(name.equals("structure")){
	    	this.visitStrucutre(e);
	    	return;
	    }
	    //handle character
	    if(name.equals("character")){
	    	this.visitCharacter(e);
	    	return;
	    }	   
    }
	
	private void visitStrucutre(Element e){
		String s = URI.baseURI+this.hierarchy;
    	String p = URI.baseURI+URI.haspart;
    	String c = e.attribute("constraint")!=null?e.attributeValue("constraint"):null;
    	String o = e.attribute("name").getValue();
    	String f = c!=null?c+"_"+o:o;
    	
    	//stmt: structure is_subclass_of STRUCTURE
    	this.statements.add(new ResourceStatement(URI.baseURI+o.toUpperCase(),URI.baseURI+URI.subclassof,URI.baseURI+"STRUCTURE"));
    	if(c!=null){
    		//stmt: constraint is_subclass_of STRUCTURE
        	this.statements.add(new ResourceStatement(URI.baseURI+c.toUpperCase(),URI.baseURI+URI.subclassof,URI.baseURI+"STRUCTURE"));
        	//stmt: constraint_structure is_subclass_of STRUCTURE
        	this.statements.add(new ResourceStatement((URI.baseURI+c+"_"+o).toUpperCase(),URI.baseURI+URI.subclassof,URI.baseURI+"STRUCTURE"));
        	//stmt: constraint_structure is_part_of constraint
        	this.statements.add(new ResourceStatement(URI.baseURI+(c+"_"+o).toUpperCase(),URI.baseURI+URI.ispartof,URI.baseURI+c.toUpperCase()));
    	}
    	
    	//stmt: hierarchy has_structure xxxx
    	this.statements.add(new ResourceStatement(s,p,s+"/"+f));
    	//take note the current structure
    	this.struct = s+"/"+f;
    	//stmt: xxxx is_instance_of XXXX
    	this.statements.add(new ResourceStatement(this.struct,URI.baseURI+URI.isinstanceof,URI.baseURI+f.toUpperCase()));
	}
	
	private void visitCharacter(Element e){
		if(e.attributeValue("from_unit")!=null||e.attributeValue("to_unit")!=null||e.attributeValue("unit")!=null||e.attributeValue("name").equals("count")){
			//quantitative
			if(e.attributeValue("char_type")==null){
				//ranged quantitative
				this.visitQuant(e);				
			}else if(e.attributeValue("char_type").equals("range_value")){
				this.visitRangeQuant(e);
			}
		}else{
			//categorical
			if(e.attributeValue("char_type")==null){
				//ranged quantitative
				this.visitCate(e);				
			}else if(e.attributeValue("char_type").equals("range_value")){
				this.visitRangeCate(e);
			}
		}
	}
	
	private void visitCate(Element e){
		String s = this.struct;
		String p = e.attributeValue("name");
		String o = e.attributeValue("value");
		String m = (e.attribute("modifier")!=null&&e.attributeValue("modifier").equals("not"))?"not":null;
		
		
		//stmt: YYY is_subclass_of XXXX
		this.statements.add(new ResourceStatement(URI.baseURI+o.toUpperCase(),URI.baseURI+URI.subclassof,URI.baseURI+p.toUpperCase()));	
		//stmt: this_struct has_xxxx yyy
		this.statements.add(new ResourceStatement(s,URI.baseURI+"has_"+p,m!=null?m+" "+o:o));
		//stmt: yyy is_instance_of YYY
		this.statements.add(new ResourceStatement(m!=null?m+" "+o:o,URI.baseURI+URI.isinstanceof,URI.baseURI+o.toUpperCase()));
	}
	
	private void visitRangeCate(Element e){
		//do nothing...yet
	}
	
	private void visitQuant(Element e){
		//handle count, or single value with unit
		//TYPICAL_SIZE is subclass of SIZE
		this.statements.add(new ResourceStatement(URI.baseURI+"TYPICAL_SIZE",URI.baseURI+URI.subclassof,"SIZE"));
		//ATYPICAL_SIZE is subclass of SIZE
		this.statements.add(new ResourceStatement(URI.baseURI+"ATYPICAL_SIZE",URI.baseURI+URI.subclassof,"SIZE"));
		
		//differentiate count integer from all the rest double
		
		//stmt: this struct has_xxxx this struct/xxxx
		this.statements.add(new ResourceStatement(struct, URI.baseURI+"has_"+e.attributeValue("name"), struct+"/"+e.attributeValue("name")));
		
		if(e.attributeValue("name").equals("count")){
			//stmt: this struct/xxxx has_value value^^xsd:Integer
			this.statements.add(new IntLiteralStatement(struct+"/"+e.attributeValue("name"), URI.baseURI+"has_value", Integer.parseInt(e.attributeValue("value"))));
		}else{
			//convert
			double value = Unit.valueOf(e.attributeValue("unit")).getConvert()*Double.parseDouble(e.attributeValue("value"));
			value = Math.round(value*100.0)/100.0;
			//stmt: this struct/xxxx has_value value^^xsd:double
			this.statements.add(new DoubleLiteralStatement(struct+"/"+e.attributeValue("name"), URI.baseURI+"has_value", value));
			//stmt: this struct/xxxx has_unit cm
			this.statements.add(new StringLiteralStatement(struct+"/"+e.attributeValue("name"), URI.baseURI+"has_value", "cm"));
		}
	}
	
	private void visitRangeQuant(Element e){
		//handle count or value with range
		if(e.attributeValue("name").equals("count")){
			//stmt: this struct has_xxxx this struct/xxxx
			this.statements.add(new ResourceStatement(struct, URI.baseURI+"has_"+e.attributeValue("name"), struct+"/"+e.attributeValue("name")));
			//stmt: this struct has_range_from value^^xsd:Integer
			if(e.attributeValue("from")!=null)
			this.statements.add(new IntLiteralStatement(struct+"/"+e.attributeValue("name"), URI.baseURI+"has_range_from", Integer.parseInt(e.attributeValue("from"))));
			//stmt: this struck has_range_to value^^xsd:Integer
			if(e.attributeValue("to")!=null){
				this.statements.add(new IntLiteralStatement(struct+"/"+e.attributeValue("name"), URI.baseURI+"has_range_to", Integer.parseInt(e.attributeValue("to"))));
			}else{
				if(e.attributeValue("upper_restriction")!=null&&e.attributeValue("upper_restriction").equals("false"))
				this.statements.add(new IntLiteralStatement(struct+"/"+e.attributeValue("name"), URI.baseURI+"has_range_to", Integer.MAX_VALUE));
			}
		}else{
			//stmt: this struct has_xxx this struct/xxxx
			this.statements.add(new ResourceStatement(struct, URI.baseURI+"has_"+e.attributeValue("name"), struct+"/"+e.attributeValue("name")));
			//stmt: this struct has_range_from value^^xsd:double
			if(e.attributeValue("from")!=null){
				double fromvalue = Unit.valueOf(e.attributeValue("from_unit")).getConvert()*Double.parseDouble(e.attributeValue("from"));
				fromvalue = Math.round(fromvalue*100.0)/100.0;
				this.statements.add(new DoubleLiteralStatement(struct+"/"+e.attributeValue("name"), URI.baseURI+"has_range_from", fromvalue));
				//stmt: this struct/xxxx has_from_unit cm
				this.statements.add(new StringLiteralStatement(struct+"/"+e.attributeValue("name"), URI.baseURI+"has_from_unit", "cm"));
			}
			
			//stmt: this struck has_range_to value^^xsd:double
			if(e.attributeValue("to")!=null){
				double tovalue = Unit.valueOf(e.attributeValue("to_unit")).getConvert()*Double.parseDouble(e.attributeValue("to"));
				tovalue = Math.round(tovalue*100.0)/100.0;
				this.statements.add(new DoubleLiteralStatement(struct+"/"+e.attributeValue("name"), URI.baseURI+"has_range_from", tovalue));
				//stmt: this struct/xxxx has_from_unit cm
				this.statements.add(new StringLiteralStatement(struct+"/"+e.attributeValue("name"), URI.baseURI+"has_to_unit", "cm"));
			}else{
				//do nothing
			}
		}
	}
	
    public void doVisit(Attribute attr){
    	//do nothing
    }
}
