package org.fna.fnasearch.query;

public class OrderBy {
	
	public enum Order{
		ASC,
		DESC;
	}
	public static String DESC = "DESC";
	public static String ASC = "ASC";
	
	private Binding b;
	private String o;
	
	public OrderBy(Binding b){
		this.b = b;
		this.o = ASC;
	}
	
	public OrderBy(Binding b, Order o){
		switch(o){
		case ASC:
			this.b = b;
			this.o = ASC;
			break;
		case DESC:
			this.b = b;
			this.o = DESC;
			break;
		default:
			break;
		}
	}
	
	public String toString(){
		return this.o+"("+this.b.toString()+")";
	}
}
