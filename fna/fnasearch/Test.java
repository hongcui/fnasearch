package org.fna.fnasearch;

import java.io.File;
import java.io.FileInputStream;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.util.Date;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;
import java.util.zip.CRC32;
import java.util.zip.ZipEntry;
import java.util.zip.ZipOutputStream;

import org.apache.log4j.ConsoleAppender;
import org.apache.log4j.Level;
import org.apache.log4j.Logger;
import org.apache.log4j.PatternLayout;
import org.fna.fnasearch.query.CompositeGroup;
import org.fna.fnasearch.query.Filter;
import org.fna.fnasearch.query.Group;
import org.fna.fnasearch.query.GroupLine;
import org.fna.fnasearch.query.MatchFilter;
import org.fna.fnasearch.query.OrderBy;
import org.fna.fnasearch.query.Prefix;
import org.fna.fnasearch.query.Query;
import org.fna.fnasearch.query.QueryExec;
import org.fna.fnasearch.query.QueryExecException;
import org.fna.fnasearch.query.SelectQuery;
import org.fna.fnasearch.rdf.JenaRDFStoreHandler;
import org.fna.fnasearch.rdf.RDFStoreHandlerException;
import org.fna.fnasearch.util.DocTypes;
import org.fna.fnasearch.util.GetKey;
import org.fna.fnasearch.util.ParseJSON;
import org.fna.fnasearch.util.Taxon;
import org.fna.fnasearch.xml.ImporterException;
import org.fna.fnasearch.xml.XMLImporter;
import org.json.simple.JSONArray;
import org.json.simple.JSONObject;
import org.json.simple.parser.JSONParser;

import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.query.QuerySolution;
import com.hp.hpl.jena.query.ResultSet;
import com.hp.hpl.jena.rdf.arp.ParseException;

public class Test {
	@SuppressWarnings("unchecked")
	public static void main(String args[]) throws ImporterException, RDFStoreHandlerException, QueryExecException, IOException{
		Logger logger = Logger.getRootLogger();
		logger.setLevel(Level.TRACE);
		logger.removeAllAppenders();
		logger.addAppender(new ConsoleAppender(new PatternLayout("%d{yyyy-MM-dd HH:mm:ss.SSSS} [%t] [%-5p] %c %x - %m%n")));
		
		JenaRDFStoreHandler store = new JenaRDFStoreHandler("F:\\Projects\\fnasearch\\WEB-INF\\sdb.ttl");
//		JSONArray list = new JSONArray();
		
//		QueryExec query = new QueryExec("F:\\Projects\\fnasearch\\WEB-INF\\sdb.ttl");
//		query.setDocPath("F:\\Projects\\fnasearch\\docs");
//		query.buildCount("{\"items\":[{\"cate\": \"elevation\",\"max\": \"NaN\",\"min\": 59}]}");
//		//test the query error, if error, return error msg, otherwise, submit query to triplestore
//		logger.debug(query.getQuery());
//		query.execQuery();
//		logger.debug(query.getJsonresult());
		//xml reading test
//		XMLImporter importer = XMLImporter.getInstance();
//		importer.doImport(store, true, "F:\\Projects\\old\\xml\\20101214", "F:\\Projects\\fnasearch\\docs");
		
		
	}
}
