package org.fna.fnasearch.query;

public class IRIBinding extends Binding {
	
	private String name;
	
	public IRIBinding(String name){
		this.name = name;
	}
	
	@Override
	public String toString() {
		return this.name;
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
