package org.fna.fnasearch.util;

import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;




/**
 * @author Kenneth
 *
 */

public class URI {
	
	/*
	 * base URI and Properties string
	 */
	public static final String baseURI = "http://www.fna.org/";
	public static final String has= "has_";
	public static final String hassource = "has_source";
	public static final String hasvolume = "has_volume";
	public static final String ispartof = "is_part_of";
	public static final String haspart = "has_structure";
	public static final String hascomname = "has_commonname";
	public static final String haslifestyel = "has_life_style";
	public static final String hastext = "has_text";
	public static final String belongsto = "belongs_to";
	public static final String subclassof = "is_subclass_of";
	public static final String isinstanceof = "is_instance_of";
	public static final String haspropertycate = "has_property_category";
	public static final String haslabel = "has_name_label";
	public static final String haselevation = "has_elevation";
	public static final String hasrangefrom = "has_range_from";
	public static final String hasrangeto = "has_range_to";
	public static final String hasrangeunit = "has_range_unit";
	
	
	/*
	 * regex strings for filtering
	 */
	public static final String nomen_ignore = "^((sub)?family|(sub)?tribe|(sub)?section|(sub)?genus|(sub)?species|variety)_name|number$" ;
	public static final String nomen_taxon_hierarchy = "^taxon_hierarchy$" ;
	public static final String desc_breakdown = "_(or|and)_";
	public static final String desc_attr_breakdown = "_(or|and)_";
	public static final String nomen_non_sci_name = "publication|place|author|common";
	public static final String nomen_common_name = "common_name";
	public static final String eco_ignore = "reference|key";
	
	public static String escape(String input){
		String output = null;
		if(input!=null){
			output = input;
			Matcher m = Pattern.compile("[\\(\\)\\[\\]{}\\s]").matcher(output);
			if(m.find()){
				output = m.replaceAll("_");
			}
			output = output.toLowerCase(Locale.ENGLISH);
		}
		return output;
	}
	
}
