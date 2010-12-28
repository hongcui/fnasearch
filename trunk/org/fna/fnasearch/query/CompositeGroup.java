package org.fna.fnasearch.query;

import java.util.ArrayList;
import java.util.Iterator;

public class CompositeGroup extends Group {
	private ArrayList<Group> childGroups;
	private Filter f;
	private Group u;
	
	public CompositeGroup(){
		this.childGroups = new ArrayList<Group>();
	}

	@Override
	public boolean isComposite() {
		return true;
	}
	
	public void add(Group g){
		this.childGroups.add(g);
	}
	
	public Group appendFilter(Filter f){
		this.f = f;
		return this;
	}
	
	@Override
	public String toString() {
		String result = "";
		result += "{";
		Iterator<Group> itr = this.childGroups.iterator();
		while(itr.hasNext()){
			Group g = itr.next();
			result += g.toString()+"\r\n";
		}
		if(this.f!=null){
			result += "filter ("+f.toString()+" )";
		}
		result += "}";
		if(this.u!=null){
			result += "\r\nUNION\r\n";
			result += this.u.toString();
		}
		return result;
	}

	@Override
	public ArrayList<Binding> getBindings() {
		ArrayList<Binding> l = new ArrayList<Binding>();
		Iterator<Group> itr = this.childGroups.iterator();
		while(itr.hasNext()){
			l.addAll(itr.next().getBindings());
		}
		return l;
	}

	@Override
	public Group union(Group g) {
		this.u = g;
		return this;
	}

}
