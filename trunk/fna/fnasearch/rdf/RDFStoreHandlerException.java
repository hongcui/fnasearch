package org.fna.fnasearch.rdf;

public class RDFStoreHandlerException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public RDFStoreHandlerException(String message, Throwable t){
		super(message, t);
	}
	
	public RDFStoreHandlerException(String message){
		super(message);
	}
}
