package org.fna.fnasearch.rdf;

import com.hp.hpl.jena.datatypes.RDFDatatype;
import com.hp.hpl.jena.datatypes.xsd.XSDDatatype;
import com.hp.hpl.jena.rdf.model.Model;
import com.hp.hpl.jena.rdf.model.Statement;

/*
 * The class tree takes care of converting RDFStatment objects to Jena model specific statments
 * Then make it convenient to handle in the JenaRDFStoreHandler
 */
public abstract class JenaStmtConvert {
	public JenaStmtConvert(){
		//do nothing
	}
	
	public static Statement toJenaStmt(Model m, RDFStatement s){
		String name = s.getClass().getSimpleName();
		if(name.equals("ResourceStatement")){
			return toJenaStmt(m,(ResourceStatement)s);
		}else if(name.equals("StringLiteralStatement")){
			return toJenaStmt(m,(StringLiteralStatement)s);
		}else if(name.equals("IntLiteralStatement")){
			return toJenaStmt(m,(IntLiteralStatement)s);
		}else if(name.equals("BooleanLiteralStatement")){
			return toJenaStmt(m,(BooleanLiteralStatement)s);
		}else if(name.equals("PosIntLiteralStatement")){
			return toJenaStmt(m,(PosIntLiteralStatement)s);
		}else if(name.equals("DoubleLiteralStatement")){
			return toJenaStmt(m,(DoubleLiteralStatement)s);
		}else{
			return null;
		}
	}
	
	private static Statement toJenaStmt(Model m, ResourceStatement s){
		return m.createStatement(m.createResource(s.getSubject()), m.createProperty(s.getPredict()), m.createResource(s.getObject()));
	}
	
	private static Statement toJenaStmt(Model m, StringLiteralStatement s){
		return m.createStatement(m.createResource(s.getSubject()), m.createProperty(s.getPredict()), s.getObject());
	}
	
	private static Statement toJenaStmt(Model m, IntLiteralStatement s){
		return m.createLiteralStatement(m.createResource(s.getSubject()), m.createProperty(s.getPredict()), m.createTypedLiteral(s.getObjectInt(), XSDDatatype.XSDinteger));
	}
	
	private static Statement toJenaStmt(Model m, BooleanLiteralStatement s){
		return m.createLiteralStatement(m.createResource(s.getSubject()), m.createProperty(s.getPredict()), s.getObjectBool());
	}
	
	private static Statement toJenaStmt(Model m, PosIntLiteralStatement s){
		return m.createLiteralStatement(m.createResource(s.getSubject()), m.createProperty(s.getPredict()), m.createTypedLiteral(s.getObjectInt(), XSDDatatype.XSDpositiveInteger));		
	}
	private static Statement toJenaStmt(Model m, DoubleLiteralStatement s){
		return m.createLiteralStatement(m.createResource(s.getSubject()), m.createProperty(s.getPredict()), m.createTypedLiteral(s.getObjectDouble(), XSDDatatype.XSDdouble));		
	}
}
