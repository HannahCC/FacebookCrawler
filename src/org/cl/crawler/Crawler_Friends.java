package org.cl.crawler;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cl.conf.Config;
import org.cl.service.HttpRequest;
import org.cl.service.SaveInfo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomElement;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlHeading3;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Crawler_Friends implements Runnable {
	protected WebClient wc;
	protected String uid;
	public Crawler_Friends(WebClient wc,String uid){
		this.wc = wc;
		this.uid = uid;
	}
	public void run(){
		//https://m.facebook.com/profile.php?v=friends&mutual&id=733564178&startindex=0&refid=17
		HtmlPage page = HttpRequest.getPage(wc,"https://m.facebook.com/profile.php?v=friends&mutual&id="+uid+"&startindex=0&refid=17");
		if(page==null){SaveInfo.saveErrorFriends(uid+"\t:page==null----https://m.facebook.com/profile.php?v=friends&mutual&id="+uid+"&startindex=0&refid=17");}//获取页面失败
		if(page.getTitleText().equals("Content Not Found")){SaveInfo.saveErrorFriends(uid+"\t:Content Not Found----https://m.facebook.com/profile.php?v=friends&mutual&id="+uid+"&startindex=0&refid=17");return;}//页面不存在
		try{
			//检查好友个数，若超过一个定值，则放弃获取好友ID
			if(!isTooMuch(page,uid)){return;}
			//获取好友ID
			List<String> friends_id = getFriends(wc,page,uid);
			SaveInfo.saveFriends(uid,friends_id);
			SaveInfo.saveExpandID(friends_id);
			System.out.println("Already got "+friends_id.size()+" friend_id of "+uid);
		}catch (Exception e){
			e.printStackTrace();
			SaveInfo.saveErrorFriends(uid+"\t:Exception---------:"+e.getMessage());
		}
	}
	private static boolean isTooMuch(HtmlPage page,String uid){
		DomNodeList<DomElement> h3_list = page.getElementsByTagName("h3");
		Iterator<DomElement> h3_list_it = h3_list.iterator();
		int number = 0;
		while(h3_list_it.hasNext()){
			HtmlHeading3 h3 = (HtmlHeading3) h3_list_it.next();
			String text = h3.getTextContent();
			if(text.contains("Friends (")){
				String number_str = text.split("\\(|\\)")[1];
				if(!number_str.contains(",")){number = Integer.parseInt(number_str);}
				else{number = 1000;}
				break;
			}
		}
		//System.out.println("number="+number);
		if(number>=Config.FRIENDS_MAX){SaveInfo.saveUsertooMany(uid);return false;}
		else{return true;}
	}
	private static List<String> getFriends(WebClient wc,HtmlPage page,String uid){
		List<String> friend_id = new ArrayList<String>();
		//获取本页面的好友ID
		//System.out.println(followerpage.getBody().asText());
		HtmlDivision root = (HtmlDivision) page.getElementById("root").getFirstChild();
		DomNodeList<DomNode> div_list = root.getChildNodes();
		Iterator<DomNode> div_list_it = div_list.iterator();
		while(div_list_it.hasNext()){
			DomNode div = div_list_it.next();
			if(div.getNodeName().equals("div")&&!div.hasAttributes()){
				List<DomNode> friend_list = div.getChildNodes();
				Iterator<DomNode> friend_list_it = friend_list.iterator();
				while(friend_list_it.hasNext()){
					HtmlDivision friend_div = (HtmlDivision) friend_list_it.next();
					DomNodeList<HtmlElement> div_nodes = friend_div.getFirstElementChild().getElementsByTagName("div");
					DomNodeList<HtmlElement> a_nodes = div_nodes.get(0).getElementsByTagName("a");
					if(a_nodes.size()==2){
						String friendid = a_nodes.get(1).getAttribute("href");
						String id = friendid.split("=|&")[1];
						friend_id.add(id);
					}
				}
			}
		}
		//获取更多的好友id
		HtmlDivision m_more_friends = (HtmlDivision) page.getElementById("m_more_friends");
		if(m_more_friends!=null){
			DomElement fc = m_more_friends.getFirstElementChild();
			String attr = fc.getAttribute("href");
			page = HttpRequest.getPage(wc,"https://m.facebook.com"+attr);
			if(page==null){SaveInfo.saveErrorFriends(uid+"\t:page==null----https://m.facebook.com"+attr);return friend_id;}//获取页面失败
			if(page.getTitleText().equals("Content Not Found")){SaveInfo.saveErrorFriends(uid+"\t:Content Not Found----https://m.facebook.com"+attr);return friend_id;}//页面不存在
			List<String> more_friend_id= getFriends(wc,page,uid);
			Merge(friend_id,more_friend_id);
		}
		return friend_id;
	}
	private static void Merge(List<String> friend_id,List<String> more_friend_id) {
		Iterator<String> it = more_friend_id.iterator();
		while(it.hasNext()){
			friend_id.add(it.next());
		}
	}
}
