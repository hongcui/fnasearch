package org.fna.fnasearch.query;

import java.util.ArrayList;
import java.util.HashMap;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

/*
 * The class represents the Sparql query object
 */
public abstract class Query {
	protected ArrayList<Prefix> prefixes;
	protected HashMap<String,VariableBinding> bindings;
	protected ArrayList<Group> groups;
	protected ArrayList<OrderBy> orderbys;
	
	public Query(){
		
	}
	
	public abstract void addPrefix(Prefix p);
	public abstract int getVarSeq();
	public abstract VariableBinding getVarBinding(String name);
	public abstract IRIBinding getIRIBinding(String name);
	public abstract LiteralBinding getLiteralBinding(String name);
	public abstract LiteralBinding getLiteralBinding(String name, XSDDatatype type);
	public abstract void addGroup(Group p);
	public abstract void addOderBy(OrderBy o);
	public abstract String toString();
	
}
