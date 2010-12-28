package org.fna.fnasearch.xml.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Element;
import org.fna.fnasearch.rdf.IntLiteralStatement;
import org.fna.fnasearch.rdf.ResourceStatement;
import org.fna.fnasearch.rdf.StringLiteralStatement;
import org.fna.fnasearch.util.URI;

public class ElevationVisitor extends DistVisitor {

	public ElevationVisitor(String treatmentid, String hierarchy) {
		super(treatmentid, hierarchy);
	}
	
	public void doVisit(Element e){
		String name = URI.escape(e.getName());
	    String text = e.getTextTrim();
		
		//parse elevation into from to
		Matcher range_low_matcher = Pattern.compile("^[0-9]+").matcher(text);
		Matcher range_high_matcher = Pattern.compile("(?<=-)[0-9]+(?=\\s)").matcher(text);
		Matcher range_unit_matcher = Pattern.compile("(?<=\\s)[a-zA-Z]+$").matcher(text);
		String range_low = null;
		String range_high = null;
		String range_unit = null;
		
		if(range_low_matcher.find()){
			range_low = range_low_matcher.group();
		}
		if(range_high_matcher.find()){
			range_high = range_high_matcher.group();
		}
		if(range_unit_matcher.find()){
			range_unit = range_unit_matcher.group();
		}
		
		String s = URI.baseURI+this.hierarchy;
		String p = URI.baseURI+URI.haselevation;
		String o = URI.baseURI+this.hierarchy+"/"+name;
		//stmt: hierarchy has_elevation treatmentid/elevation
		this.statements.add(new ResourceStatement(s,p,o));
		
		//if has range low limit
		if(range_low!=null){
			s = URI.baseURI+this.hierarchy+"/"+name;
			p = URI.baseURI+URI.hasrangefrom;
			//stmt: hierarchy/elevation has_range_from range_low
			this.statements.add(new IntLiteralStatement(s,p,Integer.parseInt(range_low)));
		}
		//if has range high limit
		if(range_high!=null){
			s = URI.baseURI+this.hierarchy+"/"+name;
			p = URI.baseURI+URI.hasrangeto;
			//stmt: hierarchy/elevation has_range_to range_to
			this.statements.add(new IntLiteralStatement(s,p,Integer.parseInt(range_high)));
		}
		//if has range unit
		if(range_unit!=null){
			s = URI.baseURI+this.hierarchy+"/"+name;
			p = URI.baseURI+URI.hasrangeunit;
			//stmt: hierarchy/elevation has_range_unit range_unit
			this.statements.add(new StringLiteralStatement(s,p,range_unit));
		}
	}

}
