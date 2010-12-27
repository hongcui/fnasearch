package org.fna.fnasearch.query;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

public class LiteralBinding extends Binding {
	
	private String name;
	private XSDDatatype type;
	
	public LiteralBinding(String name){
		this.name = name;
	}
	
	public LiteralBinding(String name, XSDDatatype type){
		this.name = name;
		this.type = type;
	}
	
	@Override
	public String toString() {
		if(this.type == null){
			return "\""+this.name+"\"";
		}else{
			return "\""+this.name+"\"^^"+type.getURI().replaceFirst("http://www.w3.org/2001/XMLSchema#","xsd:");
		}
		
	}
	@Override
	public boolean isLiteral() {
		return true;
	}

	@Override
	public boolean isRetrieve() {
		return false;
	}

}

