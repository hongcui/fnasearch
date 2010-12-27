package org.fna.fnasearch.query;

public class MatchFilter extends AtomFilter {	
	
	private Binding b;
	private MatchType m;
	private Binding v;
	
	public MatchFilter(Binding b, MatchType m, Binding v){
		this.b = b;
		this.m = m;
		this.v = v;
	}
	
	@Override
	public String toString() {
		return b.toString()+" "+m.toString()+" "+v.toString();
	}

	@Override
	public boolean isComposite() {
		return false;
	}

}
