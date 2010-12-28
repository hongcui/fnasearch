package org.fna.fnasearch.query;

import java.util.ArrayList;

public class GroupLine extends Group {
	
	private Binding sub;
	private Binding obj;
	private Binding r;
	private Filter f;
	
	public GroupLine(Binding sub, Binding r, Binding obj){
		this.sub = sub;
		this.obj = obj;
		this.r = r;
	}
	
	@Override
	public String toString() {
		return this.sub.toString()+" "+r.toString()+" "+obj.toString()+" ."+(this.f!= null?" filter ("+this.f.toString()+")":"");
	}


	@Override
	public boolean isComposite() {
		return false;
	}

	@Override
	public ArrayList<Binding> getBindings() {
		ArrayList<Binding> l = new ArrayList<Binding>();
		if(!this.sub.isLiteral())l.add(sub);
		if(!this.obj.isLiteral())l.add(obj);
		return l;
		
	}

	@Override
	public Group appendFilter(Filter f) {
		this.f = f;
		return this;
	}

	@Override
	public Group union(Group g) {
		CompositeGroup g1 = new CompositeGroup();
		g1.add(this);
		g1.union(g);
		return g1;
	}

}
