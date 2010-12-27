package org.fna.fnasearch.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.Map.Entry;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;

public class SelectQuery extends Query {
	
	public static int DISTINCT = 1;
	protected int isDistinct = 0;
	
	public SelectQuery(){
		super();
	}
	
	public SelectQuery(int distinct){
		super();
		if(distinct == SelectQuery.DISTINCT){
			this.isDistinct = 1;
		}
	}
	
	@Override
	public void addPrefix(Prefix p) {
		if(this.prefixes==null)this.prefixes=new ArrayList<Prefix>();
		this.prefixes.add(p);
	}
	
	@Override
	public VariableBinding getVarBinding(String name) {
		if(this.bindings==null)this.bindings = new HashMap<String,VariableBinding>();
		if(this.bindings.get(name)!=null){
			return this.bindings.get(name);
		}else{
			VariableBinding b = new VariableBinding(name);
			this.bindings.put(name, b);
			return b;
		}
	}
	
	public int getVarSeq(){
		if(this.bindings!=null){
			return this.bindings.size()+1;
		}else{
			return 1;
		}
	}
	
	@Override
	public IRIBinding getIRIBinding(String name) {
		return new IRIBinding(name);
	}
	
	@Override
	public LiteralBinding getLiteralBinding(String name) {
		return new LiteralBinding(name);
	}

	@Override
	public LiteralBinding getLiteralBinding(String name, XSDDatatype type) {
		return new LiteralBinding(name,type);
	}
	
	@Override
	public void addGroup(Group g) {		
		if(this.groups == null)this.groups = new ArrayList<Group>();
		//add the group
		this.groups.add(g);
	}

	@Override
	public void addOderBy(OrderBy o) {
		if(this.orderbys == null)this.orderbys = new ArrayList<OrderBy>();
		this.orderbys.add(o);
	}
	
	@Override
	public String toString() {
		String s = "";
		
		if(this.prefixes!=null&&this.prefixes.size()>0){
			Iterator<Prefix> itr = this.prefixes.iterator();
			while(itr.hasNext()){
				s += itr.next().toString();
				s += "\r\n";
			}
		}
		
		if(this.bindings!=null&&this.bindings.size()>0){
			Iterator<Entry<String, VariableBinding>> itr = this.bindings.entrySet().iterator();
			s += "select ";
			if(this.isDistinct==1)s += "DISTINCT ";
			while(itr.hasNext()){
				Entry<String, VariableBinding> e = itr.next();
				if(e.getValue().isRetrieve()){
					s += e.getValue().toString();
					s += " ";
				}
			}
		}
		
		if(this.groups!=null&&this.groups.size()>0){
			Iterator<Group> itr = this.groups.iterator();
			s += "\r\nwhere { \r\n";
			while(itr.hasNext()){
				s += itr.next().toString();
				s += "\r\n";
			}
			s += "}";
		}
		
		if(this.orderbys!=null&&this.orderbys.size()>0){
			s += "\r\norder by ";
			Iterator<OrderBy> itr = this.orderbys.iterator();
			while(itr.hasNext()){
				s += itr.next().toString();
				s += " ";
			}
		}
		return s;
	}

}
