package org.fna.fnasearch.xml.parse;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.dom4j.Attribute;
import org.dom4j.Element;
import org.fna.fnasearch.rdf.ResourceStatement;
import org.fna.fnasearch.rdf.StringLiteralStatement;
import org.fna.fnasearch.util.Taxon;
import org.fna.fnasearch.util.URI;

public class TaxonHierarchyVisitor extends NomenclatureVisitor {

	public TaxonHierarchyVisitor(String treatmentid, String hierarchy) {
		super(treatmentid,hierarchy);
	}
	
	public void doVisit(Element e){
	    String text = e.getTextTrim();
	    Matcher m;
		String last_found = this.hierarchy;
		//enumerate through taxon
		int i = 0;
		for(Taxon t: Taxon.values()){
			m = Pattern.compile(t.getregex(),Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ).matcher(text);
			if(m.find()){
				i++;
				//change the subject to the hierarchy,the first match will be the same as the hierarchy therefore should be skipped
				//the first match is gonna find the whole hierarchy, add the name label statement here
				if(i==1){
					//stmt: hierarchy fna:is_instance_of rank
					String s = URI.baseURI+this.hierarchy;
					String p = URI.baseURI+URI.isinstanceof;
					String o = URI.baseURI+t.toString().toUpperCase();
					this.statements.add(new ResourceStatement(s,p,o));
					
					//differentiate the name label based on ranking
					if(t.getRank()<6/*genus and above*/){
						//only show the lowest rank
						//stmt: this hierarchy has_name_label xxx
						s = URI.baseURI+this.hierarchy;
						p = URI.baseURI+URI.haslabel;
						o = m.group();
						this.statements.add(new StringLiteralStatement(s,p,o));
					}else
					if(t.getRank()>=6){
						//start with the genus
						//stmt: this hierarchy has_name_label genus subgenus.....current rank
						s = URI.baseURI+hierarchy;
						p = URI.baseURI+URI.haslabel;
						//match the string after genus
						Matcher match = Pattern.compile("(?<=genus_)[^\\s]*$",Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ).matcher(this.hierarchy);
						o = match.find()? match.group():this.hierarchy;
						//replace all rank names
						match = Pattern.compile("_(subgenus|section|subsection|species|subspecies|variety)_",Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ).matcher(o);
						o = match.find()? match.replaceAll(" "): o;
						this.statements.add(new StringLiteralStatement(s,p,o));
					}
				}else
				
				if(i>1){
					Pattern pattern = Pattern.compile("^[^\\s]*?"+t.name()+"_", Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ );
					Matcher mt = pattern.matcher(text);
					String rank = "";
					if(mt.find()){
						rank = mt.group();
					}
					
					//stmt: this hierarchy belongs to higher hierarchy
					String s = URI.baseURI+last_found;
					String p = URI.baseURI+URI.belongsto;
					String o = URI.baseURI+rank+m.group();
					this.statements.add(new ResourceStatement(s,p,o));
					
					//stmt: this hierarchy is an instance of family/genus/tribe...
					s = URI.baseURI+rank+m.group();
					p = URI.baseURI+URI.isinstanceof;
					o = URI.baseURI+t.name().toUpperCase();
					this.statements.add(new ResourceStatement(s,p,o));
					
					//move up a level in the hierarchy
					last_found = rank+m.group();
				}
			}
		}//end of enumerate through taxon
	}
	
	public void doVisit(Attribute a){
		//do nothing
	}
}
