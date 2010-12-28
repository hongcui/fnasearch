package org.fna.fnasearch.query;

import java.util.Iterator;
import java.util.Map.Entry;

public class CountQuery extends SelectQuery {
	
	public CountQuery(int distinct){
		super();
		if(distinct == SelectQuery.DISTINCT){
			this.isDistinct = 1;
		}
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
			if(this.isDistinct == SelectQuery.DISTINCT){
				s += "select (count(DISTINCT ";
			}else{
				s += "select (count(";
			}
			Iterator<Entry<String, VariableBinding>> itr = this.bindings.entrySet().iterator();
			while(itr.hasNext()){
				Entry<String, VariableBinding> e = itr.next();
				if(e.getValue().isRetrieve()){
					s += e.getValue().toString();
					s += " ";
				}
			}
			s += ") AS ?count)";
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
