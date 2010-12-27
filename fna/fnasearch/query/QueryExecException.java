package org.fna.fnasearch.query;

public class QueryExecException extends Exception{
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String query;
	
	
	QueryExecException(String message, Throwable t,String query ){
		super(message,t);
		this.setQuery(query);
	}
	
	private void setQuery(String q){
		this.query = q;
	}
	
	public String getQuery(){
		return this.query;
	}

}