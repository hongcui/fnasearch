package org.fna.fnasearch.xml.parse;

public enum XMLsections {
	meta("org.fna.fnasearch.xml.parse.MetaVisitor"),
	nomenclature("org.fna.fnasearch.xml.parse.NomenclatureVisitor"),
	description("org.fna.fnasearch.xml.parse.DescVisitor"),
	ecological_info("org.fna.fnasearch.xml.parse.EcoInfoVisitor"),
	distribution("org.fna.fnasearch.xml.parse.DistVisitor"),
	conservation("org.fna.fnasearch.xml.parse.ConservVisitor");
	
	
	private String classname;
	private XMLsections(String classname){
		this.classname = classname;
	}
	public String getClassname(){
		return this.classname;
	}
}
