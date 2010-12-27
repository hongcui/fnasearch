package org.fna.fnasearch.query;

public class VariableBinding extends Binding {
	
	private String name;
	private boolean retrieve = false;
	
	public VariableBinding(String name){
		this.name = name;
	}
	
	@Override
	public String toString() {
		return "?"+this.name;
	}

	@Override
	public boolean isLiteral() {
		return false;
	}
	
	public void setRetrieve(){
		this.retrieve = true;
	}

	@Override
	public boolean isRetrieve() {
		return this.retrieve;
	}

}
