package org.cl.crawler;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cl.conf.Config;
import org.cl.model.Timeline;
import org.cl.service.HttpRequest;
import org.cl.service.SaveInfo;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNode;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;

public class Crawler_Timelines implements Runnable {
	protected WebClient wc;
	protected String uid;
	public Crawler_Timelines(WebClient wc,String uid){
		this.wc = wc;
		this.uid = uid;
	}
	public void run(){
		HtmlPage page = HttpRequest.getPage(wc,"https://m.facebook.com/profile.php?v=timeline&id="+uid+"&refid=17");
		if(page==null){SaveInfo.saveErrorTimelines(uid+"\t:page==null----https://m.facebook.com/profile.php?v=timeline&id="+uid+"&refid=17");return;}//获取页面失败
		String title =page.getTitleText();
		if(title.equals("Content Not Found")||title.equals("Facebook")){SaveInfo.saveErrorTimelines(uid+"\t:Content Not Found----https://m.facebook.com/profile.php?v=timeline&id="+uid+"&refid=17");return;}//页面不存在
		//System.out.println(page.asText());
		try{
			HtmlDivision recent = (HtmlDivision) page.getElementById("recent");
			//如果没有recent，则放弃获取改用户的timeline
			String recent_text = recent.getTextContent();
			if(recent_text.equals("")){return;}
			//按年份，逐年获取timeline,直到获取300条或全部获取完成
			HtmlDivision year_url = (HtmlDivision) recent.getNextElementSibling();
			if(year_url==null)return;
			short relation = year_url.compareDocumentPosition(recent);
			int number = 0;//已获取的状态数目
			while(number<Config.TIMELINE_MAX&&relation==2){//
				if(year_url.getTextContent().contains("20")){//2014/2013……
					String href = year_url.getFirstElementChild().getAttribute("href");
					number = getYear(wc,uid,href,number,year_url.getTextContent());
					System.out.println(uid+":Already got "+number+" status.[now - "+year_url.getTextContent()+"].");
				}
				HtmlDivision year_url_next = (HtmlDivision) year_url.getNextElementSibling();
				if(year_url_next==null){break;}
				relation = year_url_next.compareDocumentPosition(year_url);
				year_url = year_url_next;
			}
		}catch (Exception e){
			e.printStackTrace();
			SaveInfo.saveErrorTimelines(uid+"\t:Exception----"+e.getMessage());
			SaveInfo.deleteFiles(uid);
		}
	}

	public static int getYear(WebClient wc,String uid,String href,int number,String year){
		HtmlPage page = HttpRequest.getPage(wc,"https://m.facebook.com"+href);
		if(page==null){SaveInfo.saveErrorTimelines(uid+"\t:page==null----https://m.facebook.com"+href);return number;}//获取页面失败
		if(page.getTitleText().equals("Content Not Found")){SaveInfo.saveErrorTimelines(uid+"\t:Content Not Found----https://m.facebook.com"+href);return number;}//页面不存在
		//获取本页面的状态
		String id = "structured_composer_async_container";
		HtmlDivision container_node = (HtmlDivision) page.getElementById(id);
		HtmlDivision year_node = (HtmlDivision) page.getElementById("year_"+year);
			//如果该页面没有状态则直接跳退出该年度，否则会一直访问show more
		String year_node_text = year_node.getTextContent();
		if(year_node_text.contains("No stories available")){return number;}
			//否则获取该页面的状态
		if(container_node!=null){
			number = getTimeline_async(uid,page,number,year);
		}else{
			number = getTimeline(uid,page,number,year);
		}
		//获取本年度更多的状态
		HtmlDivision more_url = (HtmlDivision) year_node.getNextElementSibling();
		if(more_url!=null){
			short relation = more_url.compareDocumentPosition(year_node);
			if(number<Config.TIMELINE_MAX&&relation==2){//
				String more_url_text = more_url.getTextContent();
				if(more_url_text.equals("See More")||more_url_text.equals("Show more")){
					href = more_url.getFirstElementChild().getAttribute("href");
					number = getYear(wc,uid,href,number,year);
				}
			}
		}
		return number;
	}
	/**
	 * 有两种页面呈现方式，该方式的例子：
	 * https://m.facebook.com/mara.soljancic?timecutoff=1408290374&sectionLoadingID=m_timeline_loading_div_1420099199_1388563200_8_&timeend=1420099199&timestart=1388563200&tm=AQDEQetfW_3GinFT&refid=17
	 * @param uid
	 * @param page
	 * @param number
	 * @param year
	 * @return
	 */
	private static int getTimeline_async(String uid, HtmlPage page,int number, String year) {
		String username = page.getTitleText();
		List<Timeline> timeline_list = new ArrayList<Timeline>();
		HtmlDivision container_node = (HtmlDivision) page.getElementById("year_"+year);
		DomNode	tmp_node = container_node.getLastChild().getFirstChild();//tmp_node每个状态的节点的最近父节点
		@SuppressWarnings("unchecked")
		List<HtmlDivision> timeline_node_list = (List<HtmlDivision>) tmp_node.getByXPath("div[@class and @id]");
		Iterator<HtmlDivision> timeline_list_it = timeline_node_list.iterator();
		while(timeline_list_it.hasNext()){//逐个获取该页面的状态
			Timeline timeline = new Timeline();
			String text = "",time = "",addr = "";//改版后没有地点
			HtmlDivision timeline_node = timeline_list_it.next();
			DomNodeList<DomNode> nodes = timeline_node.getChildNodes();
			Iterator<DomNode> nodes_it = nodes.iterator();
			//节点一：判断状态的发布者
			if(!nodes_it.hasNext()){continue;}
			HtmlDivision node = (HtmlDivision) nodes_it.next();
			if(!username.equals(node.getTextContent())){continue;}//如若该状态不是用户主动发布，则放弃获取
			//节点二：获取状态的内容
			if(!nodes_it.hasNext()){continue;}
			node = (HtmlDivision) nodes_it.next();
			if(!node.getFirstChild().getNodeName().equals("span")){continue;}//如果该状态不是文字类消息，则放弃获取
			else{text = node.getTextContent();timeline.setText(text);}
			//获取状态发布时间和地点，以及评论
			if(!nodes_it.hasNext()){continue;}
			node = (HtmlDivision) nodes_it.next();
			while(node.getChildNodes().size()!=2&&nodes_it.hasNext()){node = (HtmlDivision) nodes_it.next();}
			if(node.getChildNodes().size()==2){
				DomNodeList<DomNode> childnodes = node.getChildNodes();
				Iterator<DomNode> childnodes_it = childnodes.iterator();
				//获取时间地点
				HtmlDivision timenode = (HtmlDivision) childnodes_it.next();
				DomNodeList<HtmlElement> abbrs = timenode.getElementsByTagName("abbr");
				if(abbrs.size()>0){time = abbrs.get(0).getTextContent();}
				DomNodeList<HtmlElement> anchors = timenode.getElementsByTagName("a");
				if(anchors.size()>0){addr = anchors.get(0).getTextContent();}
				timeline.setTime(time);
				timeline.setAddr(addr);
				/*//获取评论
					HtmlDivision commentnode = (HtmlDivision) childnodes_it.next();
					DomNodeList<HtmlElement> anchor_list = commentnode.getElementsByTagName("a");
					Iterator<HtmlElement> anchors_it = anchor_list.iterator();
					while(anchors_it.hasNext()){
						HtmlAnchor anchor = (HtmlAnchor) anchors_it.next();
						if(anchor.getTextContent().contains(" Comment")){
							String comment_href = anchor.getAttribute("href");
							Crawler_Comments crawler_comments = new Crawler_Comments(wc,comment_href);
							List<Comment> comments = crawler_comments.run();
							timeline.setComments(comments);
							break;
						}
					}*/
			}
			number++;
			//System.out.println(timeline.toString());
			timeline_list.add(timeline);
		}
		if(timeline_list.size()>0){SaveInfo.savetimelines(uid, timeline_list);}//每获取一个页面的状态，将状态写入文件
		return number;
	}



	/**
	 * 有两种页面呈现方式，该方式的例子：
	 * https://m.facebook.com/ilpaninotondomontebello?v=timeline&timecutoff=1414046795&sectionLoadingID=m_timeline_loading_div_1420099199_1388563200_8_&timeend=1420099199&timestart=1388563200&tm=AQALqB9WBlCOhgEL&refid=17
	 * @param uid
	 * @param page
	 * @param number
	 * @param year
	 * @return
	 */
	private static int getTimeline(String uid,HtmlPage page,int number, String year) {
		String username = page.getTitleText();
		List<Timeline> timeline_list = new ArrayList<Timeline>();
		HtmlDivision container_node = (HtmlDivision) page.getElementById("year_"+year);
		DomNode	tmp_node = container_node.getLastChild().getFirstChild();//tmp_node每个状态的节点的最近父节点
		@SuppressWarnings("unchecked")
		List<HtmlDivision> timeline_node_list = (List<HtmlDivision>) tmp_node.getByXPath("div[@class and @id]");
		Iterator<HtmlDivision> timeline_list_it = timeline_node_list.iterator();
		while(timeline_list_it.hasNext()){//逐个获取该页面的状态
			Timeline timeline = new Timeline();
			String text = "",time = "";//这个版本没有地点
			HtmlDivision timeline_node = timeline_list_it.next();
			DomNodeList<DomNode> nodes = timeline_node.getFirstChild().getChildNodes();
			Iterator<DomNode> nodes_it = nodes.iterator();
			//节点一：判断状态的发布者和发布时间
			if(!nodes_it.hasNext()){continue;}
			HtmlDivision node = (HtmlDivision) nodes_it.next();
			DomNodeList<HtmlElement> spans = node.getElementsByTagName("span");
			String poster = "";int i = 0;
			for(HtmlElement sp : spans ){
				if(i==0){
					poster = sp.getTextContent();//第一个span存放着发布者
				}else if(i==1){
					time = sp.getFirstChild().getTextContent();//第二个span存放着时间
				}else{
					break;
				}
				i++;
			}
			if(!username.equals(poster)){continue;}//如若该状态不是用户主动发布，则放弃获取
			timeline.setTime(time);
			//节点二：获取状态的内容
			if(!nodes_it.hasNext()){continue;}
			node = (HtmlDivision) nodes_it.next();
			if(!node.getFirstChild().getNodeName().equals("span")){continue;}//如果该状态不是文字类消息，则放弃获取
			else{text = node.getTextContent();timeline.setText(text);}

			number++;
			timeline_list.add(timeline);
		}
		if(timeline_list.size()>0){SaveInfo.savetimelines(uid, timeline_list);}//每获取一个页面的状态，将状态写入文件
		return number;
	}

}
