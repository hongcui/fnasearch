package org.fna.fnasearch.util;

public enum Taxon {
    variety(11,"(?<=variety_)[^_]+"), 
    subspecies (10,"(?<=subspecies_)[^_]+"),
	species(9,"(?<=([^b]|^)species_)[^_]+"),
	subsection(8,"(?<=subsection_)[^_]+"),
	section(7,"(?<=([^b]|^)section_)[^_]+"),
	subgenus(6,"(?<=subgenus_)[^_]+"),
	genus(5,"(?<=([^b]|^)genus_)[^_]+"),
	subtribe(4,"(?<=subtribe_)[^_]+"),
	tribe(3,"(?<=([^b]|^)tribe_)[^_]+"),
	subfamily(2,"(?<=subfamily_)[^_]+"),
	family(1,"(?<=([^b]|^)family_)[^_]+");
	
    private int rank;
    private String regex;

	private Taxon(int rank, String regex) {
	   this.rank = rank;
	   this.regex = regex;
	}
	
	public int getRank(){
		return rank;
	}
	
	public String getregex() {
	   return regex;
	}
}
