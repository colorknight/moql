package org.datayoo.moql.simulation;

public class ScoreItem {
	protected int id;
	
	protected String student;
	
	protected String subject;
	
	protected int term1;
	
	protected int term2;
	
	protected int term3;
	
	protected int term4;
	
	public ScoreItem(int id) {
		this.id = id;
	}
	
	public String getStudent() {
		return student;
	}

	public void setStudent(String student) {
		this.student = student;
	}

	public String getSubject() {
		return subject;
	}

	public void setSubject(String subject) {
		this.subject = subject;
	}

	public int getTerm1() {
		return term1;
	}

	public void setTerm1(int term1) {
		this.term1 = term1;
	}

	public int getTerm2() {
		return term2;
	}

	public void setTerm2(int term2) {
		this.term2 = term2;
	}

	public int getTerm3() {
		return term3;
	}

	public void setTerm3(int term3) {
		this.term3 = term3;
	}

	public int getTerm4() {
		return term4;
	}

	public void setTerm4(int term4) {
		this.term4 = term4;
	}

	public int getId() {
		return id;
	}

}
