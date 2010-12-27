package org.fna.fnasearch.query;

public class RegexFilter extends AtomFilter {
	
	private Binding b;//the binding
	private String k;//the keyword
	private String o;//the regex option
	
	public RegexFilter(Binding binding, String keyword, String option){
		this.b = binding;
		this.k = keyword;
		this.o = option;
	}
	
	@Override
	public String toString() {
		return "regex ( str("+b.toString()+"),\""+k+"\",\""+o+"\")";
	}

	@Override
	public boolean isComposite() {
		return false;
	}
}
