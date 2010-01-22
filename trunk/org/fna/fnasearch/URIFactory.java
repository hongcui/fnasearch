package org.fna.fnasearch;

import java.util.HashMap;
import java.util.Map;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


/**
 * @author Kenneth
 *
 */

public class URIFactory {
	/*
	 * the URIFactory takes XML parser's strings as input
	 * URI factory has to know an element's:
	 * 1. position(relationship to the control tags:root, description, description/general)
	 * suspposingly, the relationship shall be deduced from the element's path.
	 * 2. element name, if the name is <treatment><description><general>, do nothing
	 * 3. does the element has child or attributes? if yes, then it should be created as a resource
	 * 
	 */
	
	static String baseURIprefix = "http://www.fna.org/fnasearch/";
	static String characterURIprefix = "http://www.fna.org/fnasearch/relations/has_";
	
	public static Map<String,String> encodeURI(String treatmentid , String path,String parentpath,String value,boolean has_child,boolean has_attr){
		
		Pattern path_replace = Pattern.compile("^/treatment/description/general|^/treatment/description|^/treatment",Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ);
		Pattern path_validate = Pattern.compile("(?<=/(@)?)(author[^/@\\s]*|unknown|conserved|key|number|discussion|reference[^/@\\s]*|x+|[^/@\\s]*publication[^/@\\s]*)",Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ);
		Pattern get_name = Pattern.compile("[^/@\\s]+$",Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ);
		//Pattern get_tags = Pattern.compile("[^/@\\s]+",Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ);
		Pattern get_rank = Pattern.compile("(?<=/)(sub)?family|(sub)?tribe|(sub)?genus|(sub)?section|(sub)?species|variety(?=_name)",Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ);
		Matcher path_validate_matcher = path_validate.matcher(path);
		Matcher get_rank_matcher = get_rank.matcher(path);
		Matcher path_replace_matcher = path_replace.matcher(parentpath);
		Matcher get_name_matcher = get_name.matcher(path);
		
		Map<String,String> statement = new HashMap<String,String>();
		String s,p,o = null;
		boolean isres = true;
		if(!has_child && !has_attr)isres = false;
		
		//shall the node be imported as a triple?
		//root node <treatment>  control node <description> <general> should be ignored
		if(path.equals(parentpath)|| path.equals("/treatment/description/general")||path.equals("/treatment/description")){
			path_validate_matcher.reset();
			get_rank_matcher.reset();
			path_replace_matcher.reset();
			get_name_matcher.reset();
			return statement;
		}
		//node including unwanted words should be ignored
		else if(path_validate_matcher.find()){
			path_validate_matcher.reset();
			get_rank_matcher.reset();
			path_replace_matcher.reset();
			get_name_matcher.reset();
			return statement;
		}
		//the remaining nodes shall be imported
		else{
			//specific tag handling, such as taxon names
			if(get_rank_matcher.find()){
				s = path_replace_matcher.replaceFirst(baseURIprefix+treatmentid);
				p = characterURIprefix+get_rank_matcher.group();
				o = value;
			}else{
				//general tag handling
				path_replace_matcher.find();
				s = path_replace_matcher.replaceFirst(baseURIprefix+treatmentid);
				get_name_matcher.find();
				p = characterURIprefix+get_name_matcher.group();
				if(isres){
					path_replace_matcher.reset(path).find();
					o = path_replace_matcher.replaceFirst(baseURIprefix+treatmentid);
				}else{
					o = value;
				}
			}
			path_validate_matcher.reset();
			get_rank_matcher.reset();
			path_replace_matcher.reset();
			get_name_matcher.reset();
			
			statement.put("s", s);
			statement.put("p", p);
			statement.put("o", o);
			statement.put("isres", Boolean.toString(isres));
			System.out.println(s+"\t\t\t"+p+"\t\t\t"+o+"\t\t\t"+isres);
			
			//It is only for test! Remove later!!
			//statement.clear();
			return statement;
		}
		
	}
	
}
