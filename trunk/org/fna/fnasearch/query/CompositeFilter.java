package org.fna.fnasearch.query;

public class CompositeFilter extends Filter {
	
	public static String and = "&&";
	public static String or = "||";
	private Filter f1;
	private Filter f2;
	private String r;
	
	public CompositeFilter(Filter f1, Filter f2, String r){
		this.f1 = f1;
		this.f2 = f2;
		this.r = r;
	}
	
	@Override
	public boolean isComposite() {
		return true;
	}
	
	@Override
	public String toString() {
		return "("+f1.toString()+" "+this.r+" "+f2.toString()+")";
	}
}
