package org.fna.fnasearch.rdf;

public class PosIntLiteralStatement extends LiteralStatement {
	
	private String s;
	private String p;
	private long o;
	
	public PosIntLiteralStatement(String s, String p, long o){
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
		return Long.toString(o);
	}
	
	public Long getObjectInt(){
		return o;
	}

	@Override
	public String toString() {
		return s+"\t"+p+"\t"+Long.toString(o);
	}
}
