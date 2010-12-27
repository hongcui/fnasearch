package org.fna.fnasearch.query;

import java.util.Iterator;

public class CountQuery extends SelectQuery {

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
			s += "select (count(*) AS ?count)";
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
