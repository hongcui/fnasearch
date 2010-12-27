/**
 * 
 */
package org.fna.fnasearch.rdf;


import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import com.hp.hpl.jena.query.Dataset;
import com.hp.hpl.jena.query.Query;
import com.hp.hpl.jena.query.QueryExecution;
import com.hp.hpl.jena.query.QueryExecutionFactory;
import com.hp.hpl.jena.query.QueryFactory;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.query.ResultSetFactory;
import com.hp.hpl.jena.query.Syntax;
import com.hp.hpl.jena.rdf.model.Literal;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Property;
import com.hp.hpl.jena.rdf.model.RDFNode;
import com.hp.hpl.jena.rdf.model.Resource;
import com.hp.hpl.jena.rdf.model.Statement;
import com.hp.hpl.jena.rdf.model.StmtIterator;
import com.hp.hpl.jena.sdb.SDBFactory;
import com.hp.hpl.jena.sdb.Store;
import com.hp.hpl.jena.sdb.sql.SDBConnection;
import com.hp.hpl.jena.sdb.store.DatasetStore;
/**
 * @author Kenneth
 *
 */
public class JenaRDFStoreHandler {
	
	private Store store = null;
	private SDBConnection conn = null;
	private Query query = null;
	private QueryExecution qe = null;
	
	public JenaRDFStoreHandler(String sdb) throws RDFStoreHandlerException{
		try{
			this.store = SDBFactory.connectStore(sdb);
			
		}catch(Throwable t){
			throw new RDFStoreHandlerException("Connect to store failed",t);
		}
	}
	
	public boolean isStoreClosed(){
		return this.store.isClosed();
	}
	
	public void formatStore() throws RDFStoreHandlerException{
		try{
			this.store.getTableFormatter().create();
		}catch(Throwable t){
			throw new RDFStoreHandlerException("Format tables failed",t);
		}
	}
	public void closeQuery(){
		if(this.qe!=null)this.qe.close();
	}
	
	public void closeConn(){
		if(this.conn!=null)this.conn.close();
	}
	
	public void closeStore(){
		if(this.store!=null)this.store.close();
	}
	
	//insert a statement
	public void insertStmt(RDFStatement stmt) throws RDFStoreHandlerException{
		try{
			Model model = SDBFactory.connectDefaultModel(this.store);
			//convert the RDFStatement type to JENA model
			Statement s = JenaStmtConvert.toJenaStmt(model, stmt);
			model.add(s);
		}catch(Throwable t){
			throw new RDFStoreHandlerException("Insert triple failed -"+stmt.toString(), t);
		}
	}
	
	
	//insert a triple by specifying subject, predict, object and a boolean indicating if the object is a resource
	public void insertTriple(String s,String p, String o, boolean isres) throws RDFStoreHandlerException{
		try{
			
			Model model = SDBFactory.connectDefaultModel(this.store);
			Resource subject = null;
			Property property = null;
			subject = model.createResource(s);
			property = model.createProperty(p);
			if(isres){
				Resource object = model.createResource(o);
				model.add(subject, property, object);
			}else{
				
				Matcher m1 = Pattern.compile("^[0-9]+$").matcher(o);
				Matcher m2 = Pattern.compile("^(true|false)$").matcher(o);
				Literal object = null;
				if(m1.find()){
					
					object = model.createTypedLiteral(Integer.parseInt(o));
				}else if(m2.find()){
					object = model.createTypedLiteral(Boolean.valueOf(o));
				}else{
					object = model.createLiteral(o);
				}
				model.add(subject, property, object);
			}
		}catch(Throwable t){
			throw new RDFStoreHandlerException("Insert triple failed - s:"+s+" p:"+p+" o:"+o+" res:"+isres, t);
		}
		
	}
	
	
	/*
	 * return a list of hashmap strings of the specified query
	 * using default syntax
	 */
	public List<HashMap<String, String>> queryStoreList(String querystring) throws RDFStoreHandlerException{
		
		Dataset ds = DatasetStore.create(store) ;
		this.conn = this.store.getConnection();
		Model resultmodel = null;
		ResultSet results = null;
		
		List<HashMap<String,String>> resultset = new ArrayList<HashMap<String,String>>();
		String var = null;
		
		try{
			query = QueryFactory.create(querystring);
			qe = QueryExecutionFactory.create(query, ds);
		
			
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
						if(result.get(var)!=null){
						resultset.get(i).put(var, result.get(var).toString());
						}
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
			return resultset;
		}catch(Throwable t){
			throw new RDFStoreHandlerException("Query the store returning list failed", t);
		}finally{
			this.closeQuery();
			if(resultmodel!=null)resultmodel.close();
			ds.close();
			this.closeConn();
		}
		
	}

	/*
	 * return a list of hashmap values of the specified query
	 * using the ARQ syntax -- mainly to support functions like COUNT
	 */
	public List<HashMap<String, String>> queryStoreExt(String querystring) throws RDFStoreHandlerException{
		
		Dataset ds = DatasetStore.create(store) ;
		this.conn = this.store.getConnection();
		ResultSet results = null;	
		try{	
			List<HashMap<String,String>> resultset = new ArrayList<HashMap<String,String>>();
			String var = null;
		
			query = QueryFactory.create(querystring, Syntax.syntaxARQ);
			qe = QueryExecutionFactory.create(query,ds);
		
			results = qe.execSelect();
			//put the results into an arraylist
			int i = 0;
			for(; results.hasNext();){
					resultset.add(i ,new HashMap<String,String>());
					QuerySolution result = results.next();
					Iterator<String> j = results.getResultVars().iterator();
					for(;j.hasNext();){
						var = j.next();
						if(result.get(var)!=null){
							resultset.get(i).put(var, result.get(var).toString());
						}
					}
					i++;
			}
			return resultset;
		}catch(Throwable t){
			throw new RDFStoreHandlerException("Query store returning list using ARQ Syntax failed",t);
		}finally{
			this.closeQuery();
			ds.close();
			this.closeConn();
		}
	}
	

	/*
	 * Return a Jena Resultset of the specified query string
	 */
	public ResultSet queryStore(String querystring) throws RDFStoreHandlerException{
		this.conn = this.store.getConnection();
		Dataset ds = DatasetStore.create(store) ;
		ResultSet results = null;
		
		try{
			this.query = QueryFactory.create(querystring);
			
			this.qe = QueryExecutionFactory.create(query, ds);
			results = this.qe.execSelect();
			return results;
		}catch(Throwable t){
			throw new RDFStoreHandlerException("Query store returning resultset failed", t);
		}finally{
			//cannot close query or store here as the result set need to be parsed by the sevlet
			//resource will be release from there
		}
	}
	
	public ResultSet queryStore(Query q) throws RDFStoreHandlerException{
		this.conn = this.store.getConnection();
		Dataset ds = DatasetStore.create(store) ;
		ResultSet results = null;
		
		try{
			this.query = QueryFactory.create(q.toString());
			
			this.qe = QueryExecutionFactory.create(query, ds);
			results = this.qe.execSelect();
			return results;
		}catch(Throwable t){
			throw new RDFStoreHandlerException("Query store returning resultset failed", t);
		}finally{
			//cannot close query or store here as the result set need to be parsed by the sevlet
			//resource will be release from there
		}
	}
	
	/*
	 * toString method
	 */
	public String toString(){
		
		return store.toString();
		
	}
	
	
	/*
	 * Write the whole store into the console
	 */
	public void writeStore(){
		Model model = SDBFactory.connectDefaultModel(store) ;
		model.setNsPrefix("FNA","http://www.fna.org/");
		model.write(System.out);
	}
	
		
}