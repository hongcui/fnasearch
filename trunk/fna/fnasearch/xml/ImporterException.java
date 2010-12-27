package org.fna.fnasearch.xml;

public class ImporterException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	
	public ImporterException(String message, Throwable t){
		super(message, t);
	}
	
	public ImporterException(String message){
		super(message);
	}

}
