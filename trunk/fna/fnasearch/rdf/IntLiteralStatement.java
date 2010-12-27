package org.fna.fnasearch.rdf;

public class IntLiteralStatement extends LiteralStatement {
	
	private String s;
	private String p;
	private int o;
	
	public IntLiteralStatement(String s, String p, int o){
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
		return Integer.toString(o);
	}
	
	public int getObjectInt(){
		return o;
	}

	@Override
	public String toString() {
		return s+"\t"+p+"\t"+Integer.toString(o);
	}

}
