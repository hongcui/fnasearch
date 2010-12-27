package org.fna.fnasearch.util;

import java.io.File;

import org.apache.log4j.Logger;



/**
 * Includes a static method that given a uri, tries to retrieve the key file name
 * return the file name as string or null on nofilefound
 */
public class GetKey {
	
	public final static Logger logger = Logger.getLogger("org.fna.fnasearch.util.GetKey");
	
	public GetKey(){}
	
	public static String getKey(String uri, String docpath){
		uri = uri.replace("http://www.fna.org/", "");
		String[] breakdownuri = uri.split("[-.]");
		for(int i=0;i<breakdownuri.length;i++)logger.debug("break down uri index "+i+", value is "+breakdownuri[i]);
		//check for each possible file type

		//check uri-key.pdf
		File doc = new File(docpath+"/"+breakdownuri[0]+"/"+breakdownuri[1]+"/"+uri+"-key.pdf");
		logger.debug(doc.getAbsolutePath());
		if(doc.isFile()&&doc.canRead()){
			return doc.getName();
		}
		//if non found, try again with the file name reduced to the document name
		//i.e. if above FNA-19-1.* has nothing found in /FNA/19/ now try with /FNA/19/1.*
		doc = new File(docpath+"/"+breakdownuri[0]+"/"+breakdownuri[1]+"/"+breakdownuri[breakdownuri.length-1]+"-key.pdf");
		logger.debug(doc.getAbsolutePath());
		if(doc.isFile()&&doc.canRead()){
			return doc.getName();
		}
		//if still non found, throw exception
		logger.debug("File not found with uri - "+uri);
		return null;
	}
}
