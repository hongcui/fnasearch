package org.fna.fnasearch.util;

public enum DocTypes {
	
	keypdf("-key",".pdf"),
	pdf("",".pdf"),
	xml("",".xml"),
	key("-key",".docx"),
	doc("",".docx");
	
	
	private String pattern;
	private String ext;
	
	private DocTypes(String pattern, String ext){
		this.pattern = pattern;
		this.ext = ext;
	}
	
	public String getPattern(){
		return this.pattern;
	}
	
	public String getExt(){
		return this.ext;
	}
}
