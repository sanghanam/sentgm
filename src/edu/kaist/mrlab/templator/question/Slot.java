package edu.kaist.mrlab.templator.question;

public class Slot {
	
	private String verb;
	private String var;
	
	
	public Slot(String verb, String var){
		this.verb = verb;
		this.var = var;
	}

	public String getVar() {
		return var;
	}

	public void setVar(String var) {
		this.var = var;
	}

	public String getVerb() {
		return verb;
	}

	public void setVerb(String verb) {
		this.verb = verb;
	}
}
