package org.fna.fnasearch.query;

public class BooleanFilter extends AtomFilter {
	
	private VariableBinding b;
	
	public BooleanFilter(VariableBinding b){
		this.b = b;
	}
	
	@Override
	public boolean isComposite() {
		return false;
	}

	@Override
	public String toString() {
		return "xsd:boolean("+b.toString()+")";
	}

}
