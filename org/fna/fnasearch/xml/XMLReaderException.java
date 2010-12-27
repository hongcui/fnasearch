package org.fna.fnasearch.xml;

public class XMLReaderException extends Exception {

	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;

	public XMLReaderException(String message, Throwable t){
		super(message, t);
	}
	
	public XMLReaderException(String message){
		super(message);
	}
}
