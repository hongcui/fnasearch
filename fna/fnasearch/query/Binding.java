package org.fna.fnasearch.query;

public abstract class Binding {
	public abstract String toString();
	public abstract boolean isLiteral();
	public abstract boolean isRetrieve();
}
