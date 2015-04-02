package org.cl.model;

public class User {

	private String uid;						//用户id
	private String name;					//用户姓名
	private String domain = "";                  //用户个性域名
	//Basic-info
	private String brithday = "";			//生日
	private String gender = "";				//性别
	private String lang="";               	//用户语言
	//living
	private String hometown = "";			//家乡
	private String living = "";			//所在地
	//education
	private	String education = "";			//学历
	//work
	private	String profession = "";			//职业
	
	public String toString(){
		StringBuffer str = new StringBuffer();
		str.append(uid+"\t"+domain+"\t"+name+"\t"+brithday+"\t"+gender+"\t"+lang+"\t"+hometown+"\t"+living+"\t"+education+"\t"+profession+"\r\n");
		return str.toString();
	}
	public String getUid() {
		return uid;
	}
	public void setUid(String uid) {
		this.uid = uid;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getName() {
		return name;
	}
	public void setName(String name) {
		this.name = name;
	}
	public String getBrithday() {
		return brithday;
	}
	public void setBrithday(String brithday) {
		this.brithday = brithday;
	}
	public String getGender() {
		return gender;
	}
	public void setGender(String gender) {
		this.gender = gender;
	}
	public String getLang() {
		return lang;
	}
	public void setLang(String lang) {
		this.lang = lang;
	}
	public String getHometown() {
		return hometown;
	}
	public void setHometown(String hometown) {
		this.hometown = hometown;
	}
	public String getLiving() {
		return living;
	}
	public void setLiving(String living) {
		this.living = living;
	}
	public String getEducation() {
		return education;
	}
	public void setEducation(String education) {
		this.education = education;
	}
	public String getProfession() {
		return profession;
	}
	public void setProfession(String profession) {
		this.profession = profession;
	}

}
