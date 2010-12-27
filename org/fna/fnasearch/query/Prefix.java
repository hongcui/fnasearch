package org.fna.fnasearch.query;

public class Prefix {
	
	private String alias;
	private String uri;
	
	public Prefix(String alias, String u/*uri without the parentheses*/){
		this.alias = alias;
		this.uri = u;
	}
	
	public String alias(){
		return this.alias;
	}
	
	public String toString(){
		return "Prefix "+this.alias+": <"+this.uri+">";
	}
}
