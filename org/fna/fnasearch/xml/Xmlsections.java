package org.fna.fnasearch.xml;

public enum Xmlsections {
	meta("MetaVisitor"),
	nomenclature("NomenclatureVisitor"),
	description("DescriptionVisitor"),
	ecological_info("Ecological_infoVisitor");
	
	
	private String classname;
	private Xmlsections(String classname){
		this.classname = classname;
	}
	public String getClassname(){
		return this.classname;
	}
}
