package org.fna.fnasearch.rdf;

/*
 * RDFStatement represents the combination of subject, predict, object and other necessary
 * data to form a rdf statement
 */
public abstract class RDFStatement {
	public abstract String getSubject();
	public abstract String getPredict();
	public abstract String getObject();
	public abstract String toString();
}
