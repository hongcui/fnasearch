/**
 * 
 */
package org.fna.fnasearch;


import java.io.InputStream;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;

import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryException;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.util.FileManager;
/**
 * @author Kenneth
 *
 */
public class TripleStoreHandler {
	
	private Store store = null;
	
	public TripleStoreHandler(String sdb){
		this.store = SDBFactory.connectStore(sdb) ;
	}
	
	public void formatStore(){
		this.store.getTableFormatter().create();
	}
	
	public void closeStore(){
		this.store.getConnection().close();
		this.store.close();
	}
		
	public void insertTriple(String s,String p, String o, boolean isres){
		Resource subject = null;
		Property property = null;
				
		Model model = SDBFactory.connectDefaultModel(store) ;
		subject = model.createResource(s);
		property = model.createProperty(p);
		if (isres==true ){ 
			Resource object = model.createResource(o);
			model.add(subject, property, object);}
		else{
			model.add(subject, property, o);
		}
	}
	
	public List<HashMap<String, String>> queryStore(String querystring){
		
		Model model = SDBFactory.connectDefaultModel(store);
		Model resultmodel = null;
		ResultSet results = null;
		Query query = null;
		QueryExecution qe = null;
		
		//HashMap<String,String> row = new HashMap<String, String>();
		List<HashMap<String,String>> resultset = new ArrayList<HashMap<String,String>>();
		String var = null;
		
		try{
			query = QueryFactory.create(querystring);
			qe = QueryExecutionFactory.create(query, model);
		}catch(QueryException e){
			e.printStackTrace();
		}
		//decide the query type
		//select query
		if(query.isSelectType()){
			results = qe.execSelect();
			//put the results into an arraylist
			int i = 0;
			for(; results.hasNext();){
				resultset.add(i ,new HashMap<String,String>());
				QuerySolution result = results.next();
				Iterator<String> j = results.getResultVars().iterator();
				for(;j.hasNext();){
					var = j.next();
					resultset.get(i).put(var, result.get(var).toString());
				}
				i++;
			}
		}
		
		//describe query
		if(query.isDescribeType()){
			resultmodel = qe.execDescribe();
			//put results into the list
			
			StmtIterator iter = resultmodel.listStatements();
	        int i = 0;
			while (iter.hasNext()) {
	            Statement stmt      = iter.nextStatement();
	            Resource  subject   = stmt.getSubject();
	            Property  predicate = stmt.getPredicate();   
	            RDFNode   object    = stmt.getObject(); 
	            resultset.add(i ,new HashMap<String,String>());
	            resultset.get(i).put("s", subject.toString());
	            resultset.get(i).put("p", predicate.toString());
	            resultset.get(i).put("o", object.toString());
	            i++;
	            }
		}
		//ask query
		if(query.isAskType()){
			String match = new Boolean(qe.execAsk()).toString();
			resultset.add(new HashMap<String,String>());
			resultset.get(0).put("match", match);
		}
		//construct query
		if(query.isConstructType()){
			results = ResultSetFactory.makeResults(qe.execConstruct());
			//should seldom be used
		}
		if(query.isUnknownType()){
			//throw some exception
		}
		
		
		qe.close();
		return resultset;
	}
	
	public void readStore(String rdffile){
		
        Model model = SDBFactory.connectDefaultModel(store) ;
        InputStream in = FileManager.get().open( rdffile );
       if (in == null) {
           throw new IllegalArgumentException("File: " + " not found");
       }
       model.read(in, null);
	}
	
	public void writeWholeStore(){
		Model model = SDBFactory.connectDefaultModel(store) ;
		model.setNsPrefix("FNACharacter", "http://www.fna.org/fnasearch/characters#");
		model.write(System.out);
	}
	
	public void writeModel(Model inmodel){
		inmodel.write(System.out);
	}
	
	public void iterateWholeStore(){
		
		Model model = SDBFactory.connectDefaultModel(store) ;
		
		StmtIterator iter = model.listStatements();

        // print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();  // get next statement
            Resource  subject   = stmt.getSubject();     // get the subject
            Property  predicate = stmt.getPredicate();   // get the predicate
            RDFNode   object    = stmt.getObject();      // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
               System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }

            System.out.println(" .");
        }
	}
	
public void iterateModel(Model model){
				
		StmtIterator iter = model.listStatements();

        // print out the predicate, subject and object of each statement
        while (iter.hasNext()) {
            Statement stmt      = iter.nextStatement();  // get next statement
            Resource  subject   = stmt.getSubject();     // get the subject
            Property  predicate = stmt.getPredicate();   // get the predicate
            RDFNode   object    = stmt.getObject();      // get the object

            System.out.print(subject.toString());
            System.out.print(" " + predicate.toString() + " ");
            if (object instanceof Resource) {
               System.out.print(object.toString());
            } else {
                // object is a literal
                System.out.print(" \"" + object.toString() + "\"");
            }

            System.out.println(" .");
        }
	}
	
}