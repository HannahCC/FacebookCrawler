package org.cl.model;

public class Comment {

	public String domain;
	public String username;
	public String text;
	public String time;
	public String toString(){
		return domain+"|**|"+username+"|**|"+text+"|**|"+time;
	}
	public String getDomain() {
		return domain;
	}
	public void setDomain(String domain) {
		this.domain = domain;
	}
	public String getUsername() {
		return username;
	}
	public void setUsername(String username) {
		this.username = username;
	}
	public String getText() {
		return text;
	}
	public void setText(String text) {
		this.text = text;
	}
	public String getTime() {
		return time;
	}
	public void setTime(String time) {
		this.time = time;
	}
	
}
