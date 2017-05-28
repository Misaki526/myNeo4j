package com.zzr.myNeo4j;

public class RelationShips {
	private String from;
	private String to;
	private String re;
	private String attr;

	public RelationShips() {

	}

	public RelationShips(String from, String to, String re, String attr) {
		this.from = from;
		this.to = to;
		this.re = re;
		this.attr = attr;
	}

	public String getFrom() {
		return from;
	}

	public void setFrom(String from) {
		this.from = from;
	}

	public String getTo() {
		return to;
	}

	public void setTo(String to) {
		this.to = to;
	}

	public String getRe() {
		return re;
	}

	public void setRe(String re) {
		this.re = re;
	}

	public String getAttr() {
		return attr;
	}

	public void setAttr(String attr) {
		this.attr = attr;
	}

}
