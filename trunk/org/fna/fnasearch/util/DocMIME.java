package org.fna.fnasearch.util;

public enum DocMIME {

	docx("application/msword"),
	xml("text/xml"),
	pdf("application/pdf");
	
	private String mime;
	
	private DocMIME(String mime){
		this.mime = mime;
	}
	
	public String getMIME(){
		return this.mime;
	}
}
