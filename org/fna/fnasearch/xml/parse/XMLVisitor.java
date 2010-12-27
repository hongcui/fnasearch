package org.fna.fnasearch.xml.parse;

import java.util.ArrayList;

import org.apache.log4j.Logger;
import org.dom4j.Attribute;
import org.dom4j.Element;
import org.dom4j.VisitorSupport;
import org.fna.fnasearch.rdf.RDFStatement;

public abstract class XMLVisitor extends VisitorSupport {
	
	public String treatmentid;
	public String hierarchy;
	protected Logger logger;
	protected ArrayList<RDFStatement> statements;
	
	public XMLVisitor(String treatmentid,String hierarchy){
		this.logger = Logger.getLogger(this.getClass().getName());
		this.treatmentid = treatmentid;
		this.hierarchy = hierarchy;
		this.statements = new ArrayList<RDFStatement>();
	}
	
	public ArrayList<RDFStatement> getVisited(){
		return this.statements;
	}
	
	public void visit(Element e){
		try{
			this.doVisit(e);
		}catch(Throwable t){
			this.logger.debug(t.getMessage());
		}
	}
	public void visit(Attribute a){
		try{
			this.doVisit(a);
		}catch(Throwable t){
			this.logger.debug(t.getMessage());
		}
	}
	
	protected abstract void doVisit(Element e);
	protected abstract void doVisit(Attribute a);
	
}
