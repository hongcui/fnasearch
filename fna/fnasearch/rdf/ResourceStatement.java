package org.fna.fnasearch.rdf;


public class ResourceStatement extends RDFStatement {
	
	private String s;
	private String p;
	private String o;
	
	public ResourceStatement(String s, String p, String o){
		this.s = s;
		this.p = p;
		this.o = o;
	}
	
	@Override
	public String toString() {
		return s+"\t"+p+"\t"+o;
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
		return o;
	}
}
