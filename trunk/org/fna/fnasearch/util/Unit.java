package org.fna.fnasearch.util;

public enum Unit {
	mm(0.1),
	cm(1),
	dm(10),
	m(100);
	
	private double convert;
	
	private Unit(double convert){
		this.convert = convert;
	}
	
	public double getConvert(){
		return this.convert;
	}
	
	
	
}
