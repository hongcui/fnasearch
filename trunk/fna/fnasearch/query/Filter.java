package org.fna.fnasearch.query;

public abstract class Filter {
	
	public enum MatchType{
		gt(">"),
		ge(">="),
		lt("<"),
		le("<="),
		ne("!="),
		eq("==");
		
		private String s;
		private MatchType(String s){
			this.s = s;
		}
		
		public String toString(){
			return this.s;
		}
	}
	
	public Filter AND(Filter f) {
		return new CompositeFilter(this, f, CompositeFilter.and);
	}
	
	public Filter OR(Filter f) {
		return new CompositeFilter(this, f, CompositeFilter.or);
	}
	
	public abstract boolean isComposite();
	public abstract String toString();
	
}
