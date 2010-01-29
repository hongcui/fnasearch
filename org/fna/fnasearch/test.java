package org.fna.fnasearch;

import java.io.File;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.fna.fnasearch.rdfstore.TripleStoreHandler;
import org.fna.fnasearch.xml.XMLReader;

public class test{
	public static void main(String[] args){
		TripleStoreHandler store = new TripleStoreHandler("f:\\projects\\fnasearch\\sdb.ttl");
		XMLReader myvisitor = new XMLReader(store);
	
		//Begin of import xml files in a folder	        
		/*
		File dir = new File("f:\\projects\\fnasearch\\final"); 
		        File[] files = dir.listFiles(); 
		        if (files == null){
		        	System.out.print("No file(s) found.");
		        	return;
		        }else{
		        	Pattern file_pattern = Pattern.compile("(?<=\\.)[a-zA-z0-9]{3}$");
		        	myvisitor.store.formatStore();
		        	for (int i = 0; i < files.length; i++) { 
				            if (!files[i].isDirectory()) {
				            	String filename = files[i].getName();
				                	Matcher file_matcher = file_pattern.matcher(filename);
				                	file_matcher.find();
				                	if(file_matcher.group().equals("xml")){
				                		String fullpath = files[i].getAbsolutePath();
					                	System.out.println(fullpath);
					                	myvisitor.getDoc(fullpath);
					                	System.out.println(myvisitor.treatmentid);
					                	myvisitor.visitDoc();
					                	file_matcher.reset();
				                	}
				                    
		        			} 
				        } 
		        	myvisitor.store.closeStore();
		        }
		     
		//End of import xml files in a certain folder
		*/        
		//Begin of import single xml file
		myvisitor.store.formatStore();
		myvisitor.getDoc("f:\\projects\\forken\\1.xml");
		myvisitor.visitDoc();
		//myvisitor.store.writeStore();
		myvisitor.store.closeStore();
		//End of import single xml file
		   
		//Begin of test queries
		/*
		String q = "select ?x ?y "+
					"where { ?x <http://www.fna.org/fnasearch/characters#herb> ?y }";
		List<HashMap<String, String>> resultset = store.queryStore(q);		
		Iterator<HashMap<String, String>> itr = resultset.iterator();
		int t = 0;
		for(;itr.hasNext();){
			itr.next();
			System.out.println(t+"\t"+resultset.get(t).get("x")+"\t"+resultset.get(t).get("y"));
			t++;
		}
		*/
		/*
		String q ="describe ?x" +
			"where {?x <http://www.fna.org/fnasearch/characters#herb> <http://www.fna.org/fnasearch/volumn_19/43/herb> }";
		List<HashMap<String, String>> resultset = store.queryStore(q);		
		
		Iterator<HashMap<String, String>> itr = resultset.iterator();
		int t = 0;
		for(;itr.hasNext();){
			itr.next();
			System.out.println(t+"\t"+resultset.get(t).get("s")+"\t"+resultset.get(t).get("p")+"\t"+resultset.get(t).get("o"));
			t++;
		}
		*/
		/*
		String q ="describe <http://www.fna.org/fnasearch/volumn_19/43>";
		
		List<HashMap<String, String>> resultset = store.queryStore(q);		
		
		Iterator<HashMap<String, String>> itr = resultset.iterator();
		int t = 0;
		for(;itr.hasNext();){
			itr.next();
			System.out.println(t+"\t"+resultset.get(t).get("s")+"\t"+resultset.get(t).get("p")+"\t"+resultset.get(t).get("o"));
			t++;
		}
		*/
		/*
		String q = "ask { ?x <http://www.fna.org/fnasearch/characters#herb> ?y }";
		List<HashMap<String, String>> resultset = store.queryStore(q);		
		System.out.println(resultset.get(0).get("match"));
		*/
		 
		//End of test queries
		   
		        
	}
}