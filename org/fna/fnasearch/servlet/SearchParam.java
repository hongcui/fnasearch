package org.fna.fnasearch.servlet;

import java.io.IOException;
import java.io.PrintWriter;
import java.util.HashMap;
import java.util.Iterator;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.servlet.ServletConfig;
import javax.servlet.ServletException;
import javax.servlet.http.HttpServlet;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.apache.log4j.Logger;
import org.fna.fnasearch.query.Filter;
import org.fna.fnasearch.query.Group;
import org.fna.fnasearch.query.GroupLine;
import org.fna.fnasearch.query.MatchFilter;
import org.fna.fnasearch.query.OrderBy;
import org.fna.fnasearch.query.Prefix;
import org.fna.fnasearch.query.Query;
import org.fna.fnasearch.query.SelectQuery;
import org.fna.fnasearch.rdf.JenaRDFStoreHandler;
import org.fna.fnasearch.rdf.RDFStoreHandlerException;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;

public class SearchParam extends HttpServlet {
	
	/**
	 * 
	 */
	private static final long serialVersionUID = 1L;
	private String storepath = null;
	private JenaRDFStoreHandler store = null;
	private Logger logger;
	private String error;
	
	
	public void init(ServletConfig config) throws ServletException
	{
	    super.init(config);
	    this.storepath = getServletContext().getRealPath("/WEB-INF/sdb.ttl");
	    this.storepath = "file:///"+this.storepath;
	    this.logger = Logger.getLogger(this.getClass().getName());
	    
	}
	
	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException,ServletException{
		PrintWriter output = response.getWriter();
		JSONObject result = null;
		try {
			this.store = new JenaRDFStoreHandler(this.storepath);

			switch(org.fna.fnasearch.util.Categories.valueOf(request.getParameter("cate"))){
			case meta: result = this.queryMeta(request);break;
			case nomenclature: result =  this.queryNomenclature(request);break;
			case description: result =  this.queryDescription(request);break;
			case ecological_info: result =  this.queryEcological_info(request);break;
			case distribution: result =  this.queryDistribution(request);break;
			case taxonomy: result =  this.queryTaxonomy(request);break;
			default: result =  null;break;
			}
			
			response.setContentType("json");
			response.setCharacterEncoding("UTF-8");
			response.setHeader("Connection", "close");
			response.setHeader("Cache-Control","no-cache"); //HTTP 1.1
			response.setHeader("Pragma","no-cache"); //HTTP 1.0
			response.setDateHeader ("Expires", 0); //prevents caching at the proxy server
			output.print(result);
			this.logger.trace(result);
		} catch (RDFStoreHandlerException e1) {
			this.error = "Servlet SearchParam - connect to store failed";
			this.logger.error(this.error, e1);
			output.print(this.error);
		}catch (Throwable t){
			this.error = "Servlet SearchParam - error";
			this.logger.error(this.error, t);
		}finally{
			output.close();
		}

	}
	
	private JSONObject queryMeta(HttpServletRequest r) throws RDFStoreHandlerException{
		//not used any more
		return null;

	}
	
	@SuppressWarnings("unchecked")
	private JSONObject queryNomenclature(HttpServletRequest r) throws RDFStoreHandlerException{
		
		JSONObject category = new JSONObject();
		JSONArray list = new JSONArray();

		if(r.getParameter("action").equals("1")){
			
			Query q = new SelectQuery(SelectQuery.DISTINCT);
			q.addPrefix(new Prefix("fna","http://www.fna.org/"));
			q.addGroup(new GroupLine(q.getVarBinding("x"),q.getIRIBinding("fna:belongs_to"),q.getVarBinding("y")));
			q.addGroup(new GroupLine(q.getVarBinding("y"),q.getIRIBinding("fna:has_name_label"),q.getVarBinding("p")));
			q.addOderBy(new OrderBy(q.getVarBinding("p")));
			q.getVarBinding("p").setRetrieve();
			logger.debug(q.toString());

//			"PREFIX fna: <http://www.fna.org/> "+
//			"select DISTINCT ?p "+
//			" where { " +
//			"?y fna:has_label ?p. "+
//			"{?x fna:belongs_to ?y.} " +
//			" } "+
//			" order by ?p";
			if(store!=null){
				List<HashMap<String, String>> resultset = store.queryStoreList(q.toString());		
				Iterator<HashMap<String, String>> itr = resultset.iterator();
				for(;itr.hasNext();){
					String temp = itr.next().get("p").replaceFirst("http://www.fna.org/","");
					JSONObject item = new JSONObject();
					item.put("name", temp);
					list.add(item);
				}
			}		
		}else if(r.getParameter("action").equals("2")){
			
			Query q = new SelectQuery(SelectQuery.DISTINCT);
			q.addPrefix(new Prefix("fna","http://www.fna.org/"));
			q.addGroup(new GroupLine(q.getVarBinding("p"),q.getIRIBinding("fna:has_common_name"),q.getVarBinding("x")).appendFilter(new MatchFilter(q.getVarBinding("x"),Filter.MatchType.ne, q.getLiteralBinding(" "))));
			q.addOderBy(new OrderBy(q.getVarBinding("x")));
			q.getVarBinding("x").setRetrieve();
			logger.debug(q.toString());
			
//			"PREFIX fna: <http://www.fna.org/> "+
//			"select DISTINCT ?x "+
//			" where { " +
//			"?p fna:has_common_name ?x.filter(?x != \"\") "+
//			" } " +
//			" order by ?x ";
			if(store!=null){
				List<HashMap<String, String>> resultset = store.queryStoreList(q.toString());		
				Iterator<HashMap<String, String>> itr = resultset.iterator();
				for(;itr.hasNext();){
					String temp = itr.next().get("x").replaceFirst("http://www.fna.org/","");
					JSONObject item = new JSONObject();
					item.put("name", temp);
					list.add(item);
				}
			}
		}
		
		category.put("identifier", "name");
		category.put("items", list);
		return category;
	}
	
	
	@SuppressWarnings("unchecked")
	private JSONObject queryDescription(HttpServletRequest r) throws RDFStoreHandlerException{
		
		JSONObject category = new JSONObject();
		JSONArray list = new JSONArray();

		if(r.getParameter("action").equals("1")){
			Query q = new SelectQuery(SelectQuery.DISTINCT);
			q.addPrefix(new Prefix("fna","http://www.fna.org/"));
			q.addGroup(new GroupLine(q.getVarBinding("x"),q.getIRIBinding("fna:has_structure"),q.getVarBinding("y")));
			q.addGroup(new GroupLine(q.getVarBinding("y"),q.getIRIBinding("fna:is_instance_of"),q.getVarBinding("p")));
			q.addOderBy(new OrderBy(q.getVarBinding("p")));
			q.getVarBinding("p").setRetrieve();
			logger.debug(q.toString());
			
//			"PREFIX fna:<http://www.fna.org/>"+
//			"SELECT distinct ?p " +
//			"where { " +
//			" ?p fna:has_property_category \"description\". " +
//			" }" +
//			" order by ?p ";
			if(store!=null){
				List<HashMap<String, String>> resultset = store.queryStoreList(q.toString());		
				Iterator<HashMap<String, String>> itr = resultset.iterator();
				for(;itr.hasNext();){
					String temp = itr.next().get("p").replaceFirst("http://www.fna.org/","");
					JSONObject item = new JSONObject();
					item.put("name", temp);
					list.add(item);
				}
			}		
		}else if(r.getParameter("action").equals("2")){
			Query q = new SelectQuery(SelectQuery.DISTINCT);
			q.addPrefix(new Prefix("fna","http://www.fna.org/"));
			q.addGroup(new GroupLine(q.getVarBinding("y"),q.getIRIBinding("fna:is_instance_of"),q.getIRIBinding("fna:"+r.getParameter("organ").toUpperCase())));
			q.addGroup(new GroupLine(q.getVarBinding("y"),q.getVarBinding("p"),q.getVarBinding("z"))
				.appendFilter(new MatchFilter(q.getVarBinding("p"), Filter.MatchType.ne, q.getIRIBinding("fna:has_text"))
								.AND(new MatchFilter(q.getVarBinding("p"), Filter.MatchType.ne, q.getIRIBinding("fna:is_instance_of"))
			)));
			q.addOderBy(new OrderBy(q.getVarBinding("p")));
			q.getVarBinding("p").setRetrieve();
			logger.debug(q.toString());

//			"PREFIX fna:<http://www.fna.org/>"+
//			"SELECT distinct ?p " +
//			"where { " +
//			" ?y fna:is_instance_of fna:"+r.getParameter("organ")+"."+
//			" ?y ?p ?z. filter(?p != fna:has_text && ?p!= fna:is_instance_of )" +
//			" }" +
//			" order by ?p ";
			if(store!=null){
				List<HashMap<String, String>> resultset = store.queryStoreList(q.toString());		
				Iterator<HashMap<String, String>> itr = resultset.iterator();
				for(;itr.hasNext();){
					String temp = itr.next().get("p").replaceFirst("http://www.fna.org/","");
					temp = temp.replaceFirst("has_", "");
					JSONObject item = new JSONObject();
					item.put("name", temp);
					//if the value is possibly a range
					if(Pattern.compile("count|size|width|height|area|length").matcher(temp).find())
					item.put("isRange", true);
					list.add(item);
				}
			}
		}else if(r.getParameter("action").equals("3")){
			Query q = new SelectQuery(SelectQuery.DISTINCT);
			q.addPrefix(new Prefix("fna","http://www.fna.org/"));
			q.addGroup(new GroupLine(q.getVarBinding("y"),q.getIRIBinding("fna:is_instance_of"),q.getIRIBinding("fna:"+r.getParameter("organ"))));
			q.addGroup(new GroupLine(q.getVarBinding("y"),q.getIRIBinding("fna:has_"+r.getParameter("property")),q.getVarBinding("z")));
			q.addOderBy(new OrderBy(q.getVarBinding("z")));
			q.getVarBinding("z").setRetrieve();
			logger.debug(q.toString());
			
//			"PREFIX fna:<http://www.fna.org/>"+
//			"SELECT distinct ?z " +
//			"where { " +
//			" ?y fna:is_instance_of fna:"+r.getParameter("organ")+"."+
//			" ?y fna:has_"+r.getParameter("property")+" ?z." +
//			" }" ;
			if(store!=null){
				List<HashMap<String, String>> resultset = store.queryStoreList(q.toString());		
				Iterator<HashMap<String, String>> itr = resultset.iterator();
				for(;itr.hasNext();){
					String temp = itr.next().get("z").replaceFirst("http://www.fna.org/","");
					temp = temp.replaceFirst("has_", "");
					JSONObject item = new JSONObject();
					item.put("name", temp);
					list.add(item);
				}
			}
		}
		
		category.put("identifier", "name");
		category.put("items", list);
		return category;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject queryEcological_info(HttpServletRequest r) throws RDFStoreHandlerException{
		
		JSONObject category = new JSONObject();
		JSONArray list = new JSONArray();
		
		if(r.getParameter("action").equals("1")){
			Query q = new SelectQuery(SelectQuery.DISTINCT);
			q.addPrefix(new Prefix("fna","http://www.fna.org/"));
			q.addGroup(new GroupLine(q.getVarBinding("p"),q.getIRIBinding("fna:has_property_category"),q.getLiteralBinding("ecological_info")));
			q.addOderBy(new OrderBy(q.getVarBinding("p")));
			q.getVarBinding("p").setRetrieve();
			logger.debug(q.toString());			
//			"PREFIX fna: <http://www.fna.org/> "+
//			"select DISTINCT ?p "+
//			" where { " +
//			"?p fna:has_property_category \"ecological_info\". "+
//			" } " +
//			" order by ?p";
			if(store!=null){
				List<HashMap<String, String>> resultset = store.queryStoreList(q.toString());		
				Iterator<HashMap<String, String>> itr = resultset.iterator();
				for(;itr.hasNext();){
					String temp = itr.next().get("p").replaceFirst("http://www.fna.org/","");
					temp = temp.replaceFirst("has_", "");
					JSONObject item = new JSONObject();
					item.put("name", temp);
					list.add(item);
				}
			}		
		}else if(r.getParameter("action").equals("2")){
			Query q = new SelectQuery(SelectQuery.DISTINCT);
			q.addPrefix(new Prefix("fna","http://www.fna.org/"));
			q.addGroup(new GroupLine(q.getVarBinding("p"),q.getIRIBinding("fna:has_"+r.getParameter("field")),q.getVarBinding("x")));
			q.addOderBy(new OrderBy(q.getVarBinding("x")));
			q.getVarBinding("x").setRetrieve();
			logger.debug(q.toString());
//			"PREFIX fna: <http://www.fna.org/> "+
//			"select DISTINCT ?x "+
//			" where { " +
//			"?p fna:has_"+r.getParameter("field") +" ?x "+
//			" } " +
//			" order by ?x";
			if(store!=null){
				List<HashMap<String, String>> resultset = store.queryStoreList(q.toString());		
				Iterator<HashMap<String, String>> itr = resultset.iterator();
				for(;itr.hasNext();){
					String temp = itr.next().get("x").replaceFirst("http://www.fna.org/","");
					JSONObject item = new JSONObject();
					item.put("name", temp);
					list.add(item);
				}
			}
		}
		
		category.put("identifier", "name");
		category.put("items", list);
		return category;
	}
	
	@SuppressWarnings("unchecked")
	private JSONObject queryDistribution(HttpServletRequest r) throws RDFStoreHandlerException{
		
		JSONObject category = new JSONObject();
		JSONArray list = new JSONArray();
		
		if(r.getParameter("action").equals("1")){
			Query q = new SelectQuery(SelectQuery.DISTINCT);
			q.addPrefix(new Prefix("fna","http://www.fna.org/"));
			q.addGroup(new GroupLine(q.getVarBinding("p"),q.getIRIBinding("fna:has_property_category"),q.getLiteralBinding("distribution")));
			q.addOderBy(new OrderBy(q.getVarBinding("p")));
			q.getVarBinding("p").setRetrieve();
			logger.debug(q.toString());
//			"PREFIX fna: <http://www.fna.org/> "+
//			"select DISTINCT ?p "+
//			" where { " +
//			"?p fna:has_property_category \"distribution\". "+
//			" } " +
//			" order by ?p";
			if(store!=null){
				List<HashMap<String, String>> resultset = store.queryStoreList(q.toString());		
				Iterator<HashMap<String, String>> itr = resultset.iterator();
				for(;itr.hasNext();){
					String temp = itr.next().get("p").replaceFirst("http://www.fna.org/","");
					temp = temp.replaceFirst("has_", "");
					JSONObject item = new JSONObject();
					item.put("name", temp);
					list.add(item);
				}
			}		
		}else if(r.getParameter("action").equals("2")){
			Query q = new SelectQuery(SelectQuery.DISTINCT);
			q.addPrefix(new Prefix("fna","http://www.fna.org/"));
			q.addGroup(new GroupLine(q.getVarBinding("p"),q.getIRIBinding("fna:has_"+r.getParameter("field")),q.getVarBinding("x")));
			q.addOderBy(new OrderBy(q.getVarBinding("x")));
			q.getVarBinding("x").setRetrieve();
			logger.debug(q.toString());
//			"PREFIX fna: <http://www.fna.org/> "+
//			"select DISTINCT ?x "+
//			" where { " +
//			"?p fna:has_"+r.getParameter("field") +" ?x "+
//			" } " +
//			" order by ?x";
			if(store!=null){
				List<HashMap<String, String>> resultset = store.queryStoreList(q.toString());		
				Iterator<HashMap<String, String>> itr = resultset.iterator();
				for(;itr.hasNext();){
					String temp = itr.next().get("x").replaceFirst("http://www.fna.org/","");
					JSONObject item = new JSONObject();
					item.put("name", temp);
					list.add(item);
				}
			}
		}
		
		category.put("identifier", "name");
		category.put("items", list);
		return category;
	}
	
	
	
	
	@SuppressWarnings("unchecked")
	private JSONObject queryTaxonomy(HttpServletRequest r) throws RDFStoreHandlerException{
		
		JSONObject category = new JSONObject();
		JSONArray list = new JSONArray();
		Pattern p = Pattern.compile("(?<=_)[a-zA-Z]+$",Pattern.CASE_INSENSITIVE|Pattern.UNICODE_CASE|Pattern.CANON_EQ);
		if(r.getParameter("action").equals("1")){
			Query q = new SelectQuery(SelectQuery.DISTINCT);
			q.addPrefix(new Prefix("fna","http://www.fna.org/"));
			q.addGroup(new GroupLine(q.getVarBinding("x"),q.getIRIBinding("fna:is_instance_of"),q.getIRIBinding("fna:FAMILY")));
			q.addOderBy(new OrderBy(q.getVarBinding("x")));
			q.getVarBinding("x").setRetrieve();
			logger.debug(q.toString());
			
//			"PREFIX fna:<http://www.fna.org/>"+
//			"SELECT distinct ?x " +
//			"where { " +
//			" ?p fna:belongs_to ?x." +
//			" {?x fna:is_instance_of fna:family.}" +
//			" }" +
//			" order by ?x ";
			if(store!=null){
				List<HashMap<String, String>> resultset = store.queryStoreList(q.toString());		
				Iterator<HashMap<String, String>> itr = resultset.iterator();
				for(;itr.hasNext();){
					String name =null;
					String value = itr.next().get("x").replaceFirst("http://www.fna.org/","");
					Matcher m = p.matcher(value);
					if(m.find()){
						name = m.group();
					}
					JSONObject item = new JSONObject();
					
					item.put("value", value);
					item.put("name", name);
					item.put("rank", "family");
					
					list.add(item);
				}
			}		
		}else if(Integer.parseInt(r.getParameter("action"))>1){
			Query q = new SelectQuery(SelectQuery.DISTINCT);
			q.addPrefix(new Prefix("fna","http://www.fna.org/"));
			q.addGroup(new GroupLine(q.getVarBinding("x"),q.getIRIBinding("fna:belongs_to"),q.getIRIBinding("fna:"+r.getParameter("taxon"))));
			q.addGroup(new GroupLine(q.getVarBinding("x"),q.getIRIBinding("fna:is_instance_of"),q.getVarBinding("p")));
			q.addOderBy(new OrderBy(q.getVarBinding("x")));
			q.getVarBinding("x").setRetrieve();
			q.getVarBinding("p").setRetrieve();
			logger.debug(q.toString());
			
//			"PREFIX fna:<http://www.fna.org/>"+
//			"SELECT distinct ?x ?p " +
//			"where { " +
//			" ?x fna:belongs_to fna:"+r.getParameter("taxon")+"." +
//			" ?x fna:is_instance_of ?p. " +
//			" }" +
//			" order by ?x ";
			if(store!=null){
				List<HashMap<String, String>> resultset = store.queryStoreList(q.toString());		
				Iterator<HashMap<String, String>> itr = resultset.iterator();
				
				for(;itr.hasNext();){
					HashMap<String,String> result = itr.next();
					String value = result.get("x").replaceFirst("http://www.fna.org/","");
					String name = null;
					Matcher m = p.matcher(value);
					if(m.find()){
						name = m.group();
					}
					String rank = result.get("p").replaceFirst("http://www.fna.org/","");
					JSONObject item = new JSONObject();
					item.put("name", name);
					item.put("value", value);
					item.put("rank", rank);
					list.add(item);
				}
			}
		}
		
		category.put("items", list);
		category.put("label", "name");
		category.put("identifier", "value");
		return category;
	}
}
