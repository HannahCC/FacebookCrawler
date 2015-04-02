package org.cl.model;

import java.util.Iterator;
import java.util.List;

public class Timeline {
	public String text;
	public String time;
	public String addr;
	public List<Comment> comments;
	public String toString(){
		String str = text+"\t"+time+"\t"+addr+"\t";
		if(comments!=null){
			Iterator<Comment> it = comments.iterator();
			while(it.hasNext()){
				Comment c = it.next();
				str += c.toString();
				if(it.hasNext()){str += "|^^^|";}
			}
		}
		str+="\r\n";
		return str;
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
	public String getAddr() {
		return addr;
	}
	public void setAddr(String addr) {
		this.addr = addr;
	}
	public List<Comment> getComments() {
		return comments;
	}
	public void setComments(List<Comment> comments) {
		this.comments = comments;
	}
}
