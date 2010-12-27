package org.fna.fnasearch.rdf;

public class DoubleLiteralStatement extends LiteralStatement {
	
	private String s;
	private String p;
	private double o;
	
	public DoubleLiteralStatement(String s, String p, double o){
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
		return Double.toString(o);
	}
	
	public double getObjectDouble(){
		return o;
	}

	@Override
	public String toString() {
		return s+"\t"+p+"\t"+Double.toString(o);
	}
}
