/**
 * 
 */
package org.fna.fnasearch;


import java.io.InputStream;

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
	
	public void readStore(String rdffile){
		
        Model model = SDBFactory.connectDefaultModel(store) ;
        InputStream in = FileManager.get().open( rdffile );
       if (in == null) {
           throw new IllegalArgumentException("File: " + " not found");
       }
       model.read(in, null);
	}
	
	public void writeStore(){
		Model model = SDBFactory.connectDefaultModel(store) ;
		model.setNsPrefix("FNACharacter", "http://www.fna.org/fnasearch/characters#");
		model.write(System.out);
	}
	
	
	public void iterateStore(){
		
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
	
	public void testStore(){
		System.out.println(store.getSize());
		System.out.println(store.toString());
		System.out.println(store.getConfiguration());
		System.out.println(store.getTripleTableDesc());
	}
}