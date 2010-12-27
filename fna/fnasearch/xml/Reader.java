package org.fna.fnasearch.xml;


public abstract class Reader {
	
	//Overview: load a document, get basic information
	//Throw DocumentException on failure
	public abstract void getDoc(String filepath);
	
	//Overview: read the document, convert each element into RDFStatement
	public abstract void readDoc() throws XMLReaderException;
	
}
