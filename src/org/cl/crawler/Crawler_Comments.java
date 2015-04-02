package org.cl.crawler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import org.cl.conf.Config;
import org.cl.model.Comment;
import org.cl.service.HttpRequest;
import org.cl.service.SaveInfo;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Crawler_Comments {
	protected WebClient wc;
	protected String href;
	public Crawler_Comments(WebClient wc,String href){
		this.wc = wc;
		this.href = href;
	}
	public List<Comment> run(){

		int number = 0;
		List<Comment> comments = new ArrayList<Comment>();
		try{
			boolean flag;
			do{
				flag = false;
				HtmlPage page = HttpRequest.getPage(wc,"https://m.facebook.com"+href);
				if(page==null){return comments;}//获取页面失败(评论获取失败，不影响状态获取)
				if(page.getTitleText().equals("Content Not Found")){return comments;}//页面不存在
				DomNodeList<DomNode> comment_list = page.getElementById("ufiCommentList").getChildNodes();
				Iterator<DomNode> comment_list_it = comment_list.iterator();
				while(comment_list_it.hasNext()){
					HtmlDivision comment_div = (HtmlDivision) comment_list_it.next();
					if(comment_div.getTextContent().equals("View next comments")){
						continue;
					}else if(comment_div.getTextContent().equals("View previous comments")){
						href = comment_div.getFirstElementChild().getAttribute("href");
						flag = true;
						continue;
					}else{
						String domain = "",username="",text="",time="";
						Comment comment = new Comment();
						DomNodeList<DomNode> nodes = comment_div.getChildNodes();
						if(nodes.size()==2){
							HtmlDivision node1 = (HtmlDivision) nodes.get(0);
							domain = node1.getElementsByTagName("a").get(0).getAttribute("href");
							domain = domain.split("/|\\?")[1];
							username = node1.getElementsByTagName("strong").get(0).getTextContent();
							text = node1.getTextContent();
							text = text.split(username)[1];
							HtmlDivision node2 = (HtmlDivision) nodes.get(1);
							time = node2.getElementsByTagName("abbr").get(0).getTextContent();
							comment.setDomain(domain);
							comment.setText(text);
							comment.setTime(time);
							comment.setUsername(username);
							comments.add(comment);
							//System.out.println(comment.toString());
							number++;
						}
					}
				}
			}while(flag&&number<Config.COMMENT_MAX);
		}catch (Exception e){
			e.printStackTrace();
			SaveInfo.saveErrorTimelines("comments_error!"+e.getMessage());
		}
		return comments;
	}
}
