package org.fna.fnasearch.util;

import org.apache.log4j.Logger;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

public class ParseJSON {
	
	public static JSONObject parse(String s){
		JSONObject queryobj = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			queryobj = (JSONObject)parser.parse(s);
			return queryobj;
		} catch (org.json.simple.parser.ParseException e) {
			Logger.getLogger(ParseJSON.class.getName()).debug(e.getMessage());
			return null;
		}
	}
}
