package org.fna.fnasearch.rdf;

public class BooleanLiteralStatement extends LiteralStatement {
	
	private String s;
	private String p;
	private boolean o;
	
	public BooleanLiteralStatement(String s,String p, boolean o){
		this.s = s;
		this.p = p;
		this.o = o;
	}
	
	@Override
	public String getSubject() {
		return s;
	}

	@Override
	public String getPredict() {
		return p;
	}

	@Override
	public String getObject() {
		return Boolean.toString(o);
	}
	
	public boolean getObjectBool(){
		return o;
	}

	@Override
	public String toString() {
		return s+"\t"+p+"\t"+Boolean.toString(o);
	}
}