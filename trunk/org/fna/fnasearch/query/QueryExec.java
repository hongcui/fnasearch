package org.fna.fnasearch.query;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.apache.log4j.Logger;
import org.fna.fnasearch.rdf.JenaRDFStoreHandler;
import org.fna.fnasearch.rdf.RDFStoreHandlerException;
import org.fna.fnasearch.util.GetKey;
import org.fna.fnasearch.util.Taxon;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;

public class QueryExec{
	
	private JenaRDFStoreHandler store;
	private String storepath;
	private Query select;
	private Query count;
	private String error;
	private ResultSet resultset;
	private JSONObject jsonresult;
	private Logger logger;
	private String docpath;
	
	public QueryExec(String storepath){
		this.storepath = storepath;
		this.select = null;
		this.count = null;
		this.error = null;
		this.jsonresult = new JSONObject();
		this.logger = Logger.getLogger(QueryExec.class.getName());
	}	
	
	@SuppressWarnings("unchecked")
	public void buildSelect(String querytext) throws QueryExecException{
		this.logger.debug("this is a select query");
		this.select = new SelectQuery();
		JSONObject queryobj = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			queryobj = (JSONObject)parser.parse(querytext);
			List<JSONObject> queries = (List<JSONObject>) queryobj.get("items");
			Iterator<JSONObject> itr = queries.iterator();
			
//			this.select += "}";
//			String queryprefix ="PREFIX fna:<http://www.fna.org/> " +
//					" PREFIX xsd:<http://www.w3.org/2001/XMLSchema#> ";
//			String queryselect = "select ?uri ?taxoname ?taxon ?taxontree ";
//			for(int i=0;i<this.keys.size();i++){
//				queryselect += "?key"+i+" ";
//			}
//			this.select = " where { "+
//			"?taxontree fna:has_label ?taxoname. "+
//			"?taxontree fna:is_instance_of ?taxon."+
//			"?uri fna:belongs_to ?taxontree.";
//			
//			this.select = queryprefix + queryselect +this.select;
//			
			//add prefix
			select.addPrefix(new Prefix("fna","http://www.fna.org/"));
			select.addPrefix(new Prefix("xsd","http://www.w3.org/2001/XMLSchema#"));
			//add default groups
			select.addGroup(new GroupLine(select.getVarBinding("taxontree"),select.getIRIBinding("fna:has_name_label"),select.getVarBinding("taxoname")));
			select.addGroup(new GroupLine(select.getVarBinding("taxontree"),select.getIRIBinding("fna:is_instance_of"),select.getVarBinding("taxon")));
			select.addGroup(new GroupLine(select.getVarBinding("doc"),select.getIRIBinding("fna:belongs_to"),select.getVarBinding("taxontree")));
			select.addGroup(new GroupLine(select.getVarBinding("doc"),select.getIRIBinding("fna:has_source"),select.getVarBinding("source")));
			select.addGroup(new GroupLine(select.getVarBinding("source"),select.getIRIBinding("fna:is_instance_of"),select.getIRIBinding("fna:SOURCE")));
			select.getVarBinding("taxontree").setRetrieve();
			select.getVarBinding("taxoname").setRetrieve();
			select.getVarBinding("taxon").setRetrieve();
			select.getVarBinding("doc").setRetrieve();
			//add category groups
			
			while(itr.hasNext()){
			    JSONObject queryline = itr.next();
			    
			    switch(org.fna.fnasearch.util.Categories.valueOf((String)queryline.get("cate"))){
				case meta: this.metaQuery(queryline,select);break;
				case ecological_info: this.ecoQuery(queryline,select);break;
				case elevation:this.elevationQuery(queryline,select);break;
				case conservation: this.conservationQuery(queryline,select);break;
				case nomenclature: this.nomQuery(queryline,select);break;
				case description: this.descQuery(queryline,select);break;
				case taxonomy: this.taxonQuery(queryline,select);break;
				default: break;
				}
			}
			this.logger.debug(this.select);
		} catch (ParseException e) {
			this.error = "position: " + e.getPosition()+";"+e;
			throw new QueryExecException(this.error,e,this.select.toString());
		} catch (Throwable t){
			this.error = "Query building failed "+t;
			t.printStackTrace();
			throw new QueryExecException(this.error,t,this.select.toString());
			
		}
	}
	
	@SuppressWarnings("unchecked")
	public void buildCount(String querytext) throws QueryExecException{
		this.logger.debug("this is a count query");
		this.count = new CountQuery();
		JSONObject queryobj = new JSONObject();
		JSONParser parser = new JSONParser();
		try {
			queryobj = (JSONObject)parser.parse(querytext);
			List<JSONObject> queries = (List<JSONObject>) queryobj.get("items");
			Iterator<JSONObject> itr = queries.iterator();
			
			//add prefix
			count.addPrefix(new Prefix("fna","http://www.fna.org/"));
			count.addPrefix(new Prefix("xsd","http://www.w3.org/2001/XMLSchema#"));
			count.addGroup(new GroupLine(count.getVarBinding("taxontree"),count.getIRIBinding("fna:has_name_label"),count.getVarBinding("taxoname")));
			count.addGroup(new GroupLine(count.getVarBinding("taxontree"),count.getIRIBinding("fna:is_instance_of"),count.getVarBinding("taxon")));
			count.addGroup(new GroupLine(count.getVarBinding("doc"),count.getIRIBinding("fna:belongs_to"),count.getVarBinding("taxontree")));
			count.addGroup(new GroupLine(count.getVarBinding("doc"),count.getIRIBinding("fna:has_source"),count.getVarBinding("source")));
			count.addGroup(new GroupLine(count.getVarBinding("source"),count.getIRIBinding("fna:is_instance_of"),count.getIRIBinding("fna:SOURCE")));
			//add category groups
			
			
			while(itr.hasNext()){
			    JSONObject queryline = itr.next();
			    
			    switch(org.fna.fnasearch.util.Categories.valueOf((String)queryline.get("cate"))){
				case meta: this.metaQuery(queryline,count);break;
				case elevation: this.elevationQuery(queryline,count);break;
				case conservation: this.conservationQuery(queryline,count);break;
				case ecological_info: this.ecoQuery(queryline,count);break;
				case nomenclature: this.nomQuery(queryline,count);break;
				case description: this.descQuery(queryline,count);break;
				case taxonomy: this.taxonQuery(queryline,count);break;
				default: break;
				}
			}
			
			this.logger.debug(this.count.toString());
			
		} catch (ParseException e) {
			this.error = "position: " + e.getPosition()+";"+e;
			throw new QueryExecException(this.error,e,this.count.toString());
		} catch (Throwable t){
			this.error = "Query building failed"+t;
			throw new QueryExecException(this.error,t,this.count.toString());
		}
	}

	private void metaQuery(JSONObject queryline,Query q){
		int i = q.getVarSeq();
		q.addGroup(new GroupLine(q.getVarBinding("doc"),q.getIRIBinding("fna:has_"+queryline.get("field")),q.getVarBinding("m"+i))
			.appendFilter(new RegexFilter(q.getVarBinding("m"+i),queryline.get("keyword").toString() , "i"))
		);
	}
	
	private void ecoQuery(JSONObject queryline,Query q){
		int i = q.getVarSeq();
		q.addGroup(new GroupLine(q.getVarBinding("taxontree"),q.getIRIBinding("fna:has_"+queryline.get("field")),q.getVarBinding("e"+i))
			.appendFilter(new RegexFilter(q.getVarBinding("e"+i),queryline.get("keyword").toString() , "i"))
		);
	}
	
	private void elevationQuery(JSONObject queryline,Query q){
		int i = q.getVarSeq();
		int min;
		int max;
		try{
			min = Integer.parseInt(queryline.get("min").toString());
		}catch(NumberFormatException e){
			min = 0;
		}
		try{
			max = Integer.parseInt(queryline.get("max").toString());
		}catch(NumberFormatException e){
			max = Integer.MAX_VALUE;
		}
		q.addGroup(new GroupLine(q.getVarBinding("taxontree"),q.getIRIBinding("fna:has_elevation"),q.getVarBinding("elevation"+i)));
		CompositeGroup g = new CompositeGroup();
		g.add(new GroupLine(q.getVarBinding("elevation"+i),q.getIRIBinding("fna:has_range_from"),q.getVarBinding("el"+i)));
		g.add(new GroupLine(q.getVarBinding("elevation"+i),q.getIRIBinding("fna:has_range_to"),q.getVarBinding("eh"+i)));
		Filter f1 = new MatchFilter(q.getVarBinding("el"+i),Filter.MatchType.ge,q.getLiteralBinding(Integer.toString(min), XSDDatatype.XSDinteger));
		Filter f2 = new MatchFilter(q.getVarBinding("el"+i),Filter.MatchType.le,q.getLiteralBinding(Integer.toString(max), XSDDatatype.XSDinteger));
		Filter f3 = new MatchFilter(q.getVarBinding("eh"+i),Filter.MatchType.ge,q.getLiteralBinding(Integer.toString(min), XSDDatatype.XSDinteger));
		Filter f4 = new MatchFilter(q.getVarBinding("eh"+i),Filter.MatchType.le,q.getLiteralBinding(Integer.toString(max), XSDDatatype.XSDinteger));
		g.appendFilter((f1.AND(f2)).OR(f3.AND(f4)));
		q.addGroup(g);
//			result = "{?uri fna:has_elevation ?elevation. " +
//			" ?elevation fna:has_range_from ?key"+seq+". " +
//			" ?elevation fna:has_range_to ?key"+(seq+1)+". "+
//			" filter ((?key"+seq+" >= \""+min+"\"^^xsd:int && ?key"+seq+" <= \""+max+"\"^^xsd:int )||" +
//			" (?key"+(seq+1)+" >= \""+min+"\"^^xsd:int && ?key"+(seq+1)+" <= \""+max+"\"^^xsd:int))" +
//			"}";
	}
	
	
	private void conservationQuery(JSONObject queryline,Query q){
		int i = q.getVarSeq();
		if(Boolean.valueOf(queryline.get("conservation").toString())){
			q.addGroup(new GroupLine(q.getVarBinding("taxontree"),q.getIRIBinding("fna:has_conservation"),q.getVarBinding("c"+i)).appendFilter(new BooleanFilter(q.getVarBinding("c"+i))));
//			"{?uri fna:has_conservation ?key"+seq+". filter xsd:boolean(?key"+seq+")}";
		}
	}
	
	private void nomQuery(JSONObject queryline,Query q){
		int i = q.getVarSeq();	
		if(!queryline.get("comname").toString().equals("")){
			q.addGroup(new GroupLine(q.getVarBinding("taxontree"),q.getIRIBinding("fna:has_common_name"),q.getVarBinding("cn"+i)).appendFilter(new RegexFilter(q.getVarBinding("cn"+i),queryline.get("comname").toString(),"i")));
//			"{ ?uri fna:has_common_name ?key"+seq+". filter regex(?key"+seq+",\""+queryline.get("comname")+"\",\"i\")}";
		}
	}
	
	private void taxonQuery(JSONObject queryline,Query q){
		int i = q.getVarSeq();
		if(!queryline.get("taxon").toString().equals("")){
			q.addGroup(new GroupLine(q.getVarBinding("taxontree"),q.getIRIBinding("fna:belongs_to"),q.getVarBinding("t"+i)).appendFilter(new RegexFilter(q.getVarBinding("t"+i),queryline.get("taxon").toString(),"i")));
//		"?uri fna:has_source ?anything. {?uri fna:belongs_to ?key"+seq+". filter regex(str(?key"+seq+"),\""+queryline.get("taxon")+"\",\"i\")}";
		}
	}
	
	private void descQuery(JSONObject queryline,Query q){
		int i = q.getVarSeq();
		if(!queryline.get("property").toString().equals("") && !queryline.get("keyword").toString().equals("")){
			Group g1 = new GroupLine(q.getVarBinding("desc"+i),q.getIRIBinding("fna:has_"+queryline.get("property")),q.getVarBinding("desc"+(i+1))).appendFilter(new RegexFilter(q.getVarBinding("desc"+(i+1)),queryline.get("keyword").toString(),"i"));
			Group g2 = new GroupLine(q.getVarBinding("desc"+i),q.getIRIBinding("fna:is_instance_of"),q.getIRIBinding("fna:"+queryline.get("organ").toString().toUpperCase()));
			Group g3 = new GroupLine(q.getVarBinding("taxontree"),q.getIRIBinding("fna:has_structure"),q.getVarBinding("desc"+i));
//			" {{{?key"+seq+" fna:has_"+queryline.get("property")+" ?key"+(seq+1)+". filter regex(?key"+(seq+1)+",\""+queryline.get("keyword")+"\",\"i\")}";
//			" ?key"+seq+" fna:is_instance_of fna:"+queryline.get("organ")+". }";
//			" ?uri fna:has_part ?key"+seq+".}";
			q.addGroup(g1);
			q.addGroup(g2);
			q.addGroup(g3);
		}else{
			Group g2 = new GroupLine(q.getVarBinding("desc"+i),q.getIRIBinding("fna:is_instance_of"),q.getIRIBinding("fna:"+queryline.get("organ").toString().toUpperCase()));
			Group g3 = new GroupLine(q.getVarBinding("taxontree"),q.getIRIBinding("fna:has_structure"),q.getVarBinding("desc"+i));
			q.addGroup(g2);
			q.addGroup(g3);
		}
	}
	
	@SuppressWarnings({ "unchecked", "rawtypes" })
	public void execQuery() throws QueryExecException{
		try{
			String key = null;
			this.store = new JenaRDFStoreHandler(this.storepath);
			if(this.select!=null&&this.count==null){
				this.logger.debug(this.select);
				this.resultset = store.queryStore(this.select.toString());
				JSONArray items = new JSONArray();
				JSONArray treeitems = new JSONArray();
				QuerySolution result = null;
				JSONObject resultitem = null;
				List itemsintree = new LinkedList();
				
				for(;this.resultset.hasNext();){
					result = resultset.next();
					this.logger.debug(result.get("taxontree").toString());
					resultitem = new JSONObject();
					resultitem.put("uri",result.get("taxontree").toString().replaceFirst("http://www.fna.org/",""));
					resultitem.put("taxoname", result.get("taxoname").toString().replaceFirst("http://www.fna.org/",""));
					resultitem.put("taxon", result.get("taxon").toString().replaceFirst("http://www.fna.org/",""));
					resultitem.put("doc", result.get("doc").toString().replaceFirst("http://www.fna.org/",""));
					
					
					//check for key files here, if found, add a new json attribute with file name
					
					if(this.docpath==null){
						//if docpath is unknown to the query object, no can do, log the situation
						this.logger.debug("The docpath is not set, key file look up is not performed.");
					}else{
						//call the GetKey.getkey(uri, docpath)
						this.logger.debug("The docpath is set, look up key file.");
						this.logger.debug(result.get("doc"));
						key = GetKey.getKey(result.get("doc").toString(), this.docpath);
						if(key!=null){
							//if there is a return, put in as key attribute
							resultitem.put("key",key);
						}
					}
					items.add(resultitem);
					
					String taxontree = result.get("taxontree").toString().replaceFirst("http://www.fna.org/","");
					Matcher m;
					String last_found = null;
					//enumerate through taxon
					for(Taxon t: Taxon.values()){
						m = Pattern.compile(t.getregex(),Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ).matcher(taxontree);
						if(m.find()){
							Matcher mt = Pattern.compile("^[^\\s]*?"+t.name()+"_", Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ ).matcher(taxontree);
							if(mt.find()){
								//if the taxon has not already been put into the tree
								if(!itemsintree.contains(mt.group()+m.group())){
									itemsintree.add(mt.group()+m.group());
									JSONObject treeitem = new JSONObject();
									treeitem.put("id", mt.group()+m.group());
									treeitem.put("name", m.group());
									treeitem.put("rank", t.name());
									treeitem.put("label", t.name()+":"+m.group());
									//has children?yes
									if(last_found != null){
										JSONObject child = new JSONObject();
										child.put("_reference", last_found);
										JSONArray children = new JSONArray();
										children.add(child);
										treeitem.put("children", children);
									}
									//no children, this item should be a result hit
									else{
										treeitem.put("uri",result.get("taxontree").toString());
										if(key!=null)treeitem.put("key",key);
									}
									treeitems.add(treeitem);
								}
								//if the taxon exists in the tree already
								else{							
									//has children?yes
									if(last_found != null){
										
										JSONObject newchild = new JSONObject();
										newchild.put("_reference", last_found);
										int index = itemsintree.indexOf(mt.group()+m.group());
										//System.out.println(index);
										//System.out.println(treeitems.get(index));
										
										JSONObject treeitem = (JSONObject) treeitems.get(index);
										//previousely have childeren
										if(treeitem.get("children") != null){
											JSONArray children = (JSONArray) treeitem.get("children");
											if(!children.contains(newchild))
											children.add(newchild);
										}
										//no children, create new children
										else{
											JSONArray children = new JSONArray();
											children.add(newchild);
											treeitem.put("children", children);
										}
										
									}
									//previously exists but no children, meaning this item is now a hit result
									else{
										int index = itemsintree.indexOf(mt.group()+m.group());					
										JSONObject treeitem = (JSONObject) treeitems.get(index);
										treeitem.put("uri",result.get("taxontree").toString());
										if(key!=null)treeitem.put("key",key);
									}
								}
								last_found = mt.group()+m.group();
							}
						}
					}			
				}
				
				this.jsonresult.put("items", items);
				this.jsonresult.put("query", this.select.toString());
				this.jsonresult.put("tree", treeitems);

			}else if(this.select==null&&this.count!=null){
				this.logger.debug(this.count);
				List<HashMap<String, String>> counted = store.queryStoreExt(this.count.toString());
				this.jsonresult.put("count", counted.get(0).get("count").replaceFirst("\\^\\^http:\\/\\/www.w3.org\\/2001\\/XMLSchema#integer", ""));
			}
		}catch(RDFStoreHandlerException e){
			e.printStackTrace();
		}catch(Throwable t){
			this.error = "Query execution failed "+t;
			throw new QueryExecException(this.error,t,this.select.toString());
		} finally{
			this.store.closeQuery();
			this.store.closeConn();
			this.store.closeStore();
			this.logger.debug(this.jsonresult);
		}
	}
	
	
	public String getQuery() {
		if(this.select != null){
			return select.toString();
		}else{
			return count.toString();
		}
		
	}
	
	public String getError() {
		return error;
	}

	public JSONObject getJsonresult() {
		return jsonresult;
	}
	
	public void setDocPath(String docpath){
		this.docpath = docpath;
	}
	
	public String getDocPath(){
		return this.docpath;
	}
}