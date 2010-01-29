package org.fna.fnasearch;



/**
 * @author Kenneth
 *
 */

public class URIFactory {
	
	/*
	 * base URI and Properties string
	 */
	public static final String baseURI = "http://www.fna.org/";
	public static final String has= "has_";
	public static final String haspart = "has_part";
	public static final String haslifestyel = "has_life_style";
	public static final String hastext = "has_text";
	public static final String belongsto = "belongs_to";
	public static final String isinstanceof = "is_instance_of";
	
	/*
	 * regex strings for filtering
	 */
	public static final String nomen_ignore = "^((sub)?family|(sub)?tribe|(sub)?section|(sub)?genus|(sub)?species|variety)_name|number$" ;
	public static final String nomen_taxon_hierarchy = "^taxon_hierarchy$" ;
	public static final String desc_breakdown = "_(or|and)_";
	public static final String desc_attr_breakdown = "_(or|and)_";
	
}
