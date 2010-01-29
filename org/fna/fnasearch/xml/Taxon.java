package org.fna.fnasearch.xml;

public enum Taxon {
    variety("(?<=variety_)[^_]+"), 
    subspecies ("(?<=subspecies_)[^_]+"),
	species("(?<=([^b]|^)species_)[^_]+"),
	subsection("(?<=subsection_)[^_]+"),
	section("(?<=([^b]|^)section_)[^_]+"),
	subgenus("(?<=subgenus_)[^_]+"),
	genus("(?<=([^b]|^)genus_)[^_]+"),
	subtribe("(?<=subtribe_)[^_]+"),
	tribe("(?<=([^b]|^)tribe_)[^_]+"),
	subfamily("(?<=subfamily_)[^_]+"),
	family("(?<=([^b]|^)family_)[^_]+");
	
    private String regex;

	private Taxon(String regex) {
	   this.regex = regex;
	    }

	public String getregex() {
	   return regex;
	    }
}
