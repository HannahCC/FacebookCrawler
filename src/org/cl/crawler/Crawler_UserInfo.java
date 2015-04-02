package org.cl.crawler;

import java.util.Iterator;
import java.util.List;
import org.cl.model.User;
import org.cl.service.HttpRequest;
import org.cl.service.Login;
import org.cl.service.SaveInfo;

import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.DomNodeList;
import com.gargoylesoftware.htmlunit.html.HtmlDivision;
import com.gargoylesoftware.htmlunit.html.HtmlElement;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSpan;
import com.gargoylesoftware.htmlunit.html.HtmlTableDataCell;
import com.gargoylesoftware.htmlunit.html.HtmlTableRow;

public class Crawler_UserInfo implements Runnable {
	protected WebClient wc;
	protected String uid;
	public Crawler_UserInfo(WebClient wc,String uid){
		this.wc = wc;
		this.uid = uid;
	}
	public void run(){
		//https://m.facebook.com/profile.php?v=info&id=1315479518&refid=17
		//https://m.facebook.com/profile.php?v=info&id=100002680625831&refid=17
		HtmlPage page = HttpRequest.getPage(wc, "https://m.facebook.com/profile.php?v=info&id="+uid+"&refid=17");
		if(page==null){SaveInfo.saveErrorUserInfo(uid+"\t:page==null----https://m.facebook.com/profile.php?v=info&id="+uid+"&refid=17");return;}//获取页面失败
		if(page.getTitleText().equals("Content Not Found")){SaveInfo.saveUserNotExist(uid);return;}//页面不存在
		while(page.getTitleText().endsWith("Security Check")){
			System.out.println("Security Check!Re_login or exit!");
			Login.login();
			page = HttpRequest.getPage(wc, "https://m.facebook.com/profile.php?v=info&id="+uid+"&refid=17");
		}
		//System.out.println(page.asXml());
		//System.out.println(page.asText());
		User user = new User();
		user.setUid(uid);
		String name = page.getTitleText();
		if(name.equals("Facebook")){SaveInfo.saveUserNotExist(uid);return;}//用户不存在，自动转到首页
		user.setName(page.getTitleText());
		try{
			//获取screenname
			HtmlDivision contact_info = (HtmlDivision) page.getElementById("contact-info");
			if(contact_info!=null){
				List<HtmlElement> td_list = contact_info.getElementsByTagName("td");
				Iterator<HtmlElement> td_list_it = td_list.iterator();
				while(td_list_it.hasNext()){
					HtmlTableDataCell td = (HtmlTableDataCell) td_list_it.next();
					HtmlDivision div = (HtmlDivision) td.getFirstElementChild();
					String title = div.getTextContent();
					HtmlTableDataCell td2 = (HtmlTableDataCell) td_list_it.next();
					HtmlDivision div2 = (HtmlDivision) td2.getFirstElementChild();
					if(title.equals("Facebook")){
						String value = div2.getTextContent();
						value = value.split("/")[1];
						user.setDomain(value);break;
					}
				}
			}
			if(user.getDomain().equals("")){//如果用户没有ScreenName，则放弃获取它的ID，一则可以过滤企业用户，二则可以使信息完整性提高，便于后期处理
				return;
			}


			//获取基本信息
			HtmlDivision basic_info = (HtmlDivision) page.getElementById("basic-info");
			//System.out.println();
			if(basic_info!=null){
				List<HtmlElement> tr_list = basic_info.getElementsByTagName("tr");
				Iterator<HtmlElement> tr_list_it = tr_list.iterator();
				while(tr_list_it.hasNext()){
					HtmlTableRow tr = (HtmlTableRow) tr_list_it.next();
					List<HtmlElement> td_list = tr.getElementsByTagName("td");
					Iterator<HtmlElement> td_list_it = td_list.iterator();
					while(td_list_it.hasNext()){
						HtmlTableDataCell td = (HtmlTableDataCell) td_list_it.next();
						HtmlDivision div = (HtmlDivision) td.getFirstElementChild();
						HtmlTableDataCell td2 = (HtmlTableDataCell) td_list_it.next();
						HtmlDivision div2 = (HtmlDivision) td2.getFirstElementChild();
						String title = "";
						if(div.hasChildNodes()){
							title = div.getTextContent();
						}
						String value = div2.getTextContent();
						if(title.equals("Birthday")){user.setBrithday(value);}
						else if(title.equals("Gender")){user.setGender(value);}
						else if(title.equals("Languages")){user.setLang(value);}
					}
				}
			}
			//获取教育经历
			HtmlDivision education = (HtmlDivision) page.getElementById("education");
			if(education!=null){
				List<HtmlElement> span_list = education.getElementsByTagName("span");
				Iterator<HtmlElement> span_list_it = span_list.iterator();
				while(span_list_it.hasNext()){
					String title ="",value="";
					HtmlSpan span = (HtmlSpan) span_list_it.next();
					if(span.getFirstChild().getNodeName().equals("a")){
						title = span.getTextContent();//每一个新的教育经历都由这个结构开始<span><a></a></span>
						//无法确保每个用户教育经历都有其他属性，数据处理时无法辨认后面属性的归属
						/*boolean flag1 = true,flag2 = true;
						while(flag2&&span_list_it.hasNext()){
							span = (HtmlSpan) span_list_it.next();
							do{//检查span的子元素
								flag1 = true;
								Iterable<DomElement> c_span = span.getChildElements();
								Iterator<DomElement> c_span_it = c_span.iterator();
								while(c_span_it.hasNext()){
									DomNode ch = c_span_it.next();
									if(ch.getNodeName().equals("span")){//如果span有span子元素，则只取span子元素的值，如<span class="cg ch"><span class="cj t bi">  财务管理</span></span>
										span = (HtmlSpan) span_list_it.next();
										flag1 = false;
										break;
									}else if(ch.getNodeName().equals("a")){//如果span有a子元素，则该span是一个新的教育经历的开始结构，本程序只取第一个教育经历
										flag2 = false;
										break;
									}
								}
							}while(!flag1);
							if(flag2){value += "|##|"+span.getTextContent();}
						}*/
						user.setEducation(title+value);
						break;
					}
				}
			}
			//获取工作经历
			HtmlDivision work = (HtmlDivision) page.getElementById("work");
			if(work!=null){
				DomNodeList<HtmlElement> span_list = work.getElementsByTagName("span");
				Iterator<HtmlElement> span_list_it = span_list.iterator();
				while(span_list_it.hasNext()){
					String title ="",value="";
					HtmlSpan span = (HtmlSpan) span_list_it.next();
					if(span.getFirstChild().getNodeName().equals("a")){
						title = span.getTextContent();//每一个新的值都由这个结构开始<span><a></a></span>
						//无法确保每个用户工作经历都有其他属性，数据处理时无法辨认后面属性的归属
						/*boolean flag1 = true,flag2 = true;
						while(flag2&&span_list_it.hasNext()){
							span = (HtmlSpan) span_list_it.next();
							do{//检查span的子元素
								flag1 = true;
								Iterable<DomElement> c_span = span.getChildElements();
								Iterator<DomElement> c_span_it = c_span.iterator();
								while(c_span_it.hasNext()){
									DomNode ch = c_span_it.next();
									if(ch.getNodeName().equals("span")){//如果span有span子元素，则只取span子元素的值，如<span class="cg ch"><span class="cj t bi">  财务管理</span></span>
										span = (HtmlSpan) span_list_it.next();
										flag1 = false;
										break;
									}else if(ch.getNodeName().equals("a")){//如果span有a子元素，则该span是一个新的工作经历的开始结构，本程序只取第一个工作经历
										flag2 = false;
										break;
									}
								}
							}while(!flag1);
							if(flag2){value += "|##|"+span.getTextContent();}
						}*/
						//System.out.println(title+value);
						user.setProfession(title+value);
						break;
					}
				}
			}
			//获取居住地
			HtmlDivision living = (HtmlDivision) page.getElementById("living");
			if(living!=null){
				DomNodeList<HtmlElement> tr_list = living.getElementsByTagName("tr");
				Iterator<HtmlElement> tr_list_it = tr_list.iterator();
				while(tr_list_it.hasNext()){
					HtmlTableRow tr = (HtmlTableRow) tr_list_it.next();
					List<HtmlElement> td_list = tr.getElementsByTagName("td");
					Iterator<HtmlElement> td_list_it = td_list.iterator();
					if(td_list_it.hasNext()){
						HtmlTableDataCell td = (HtmlTableDataCell) td_list_it.next();
						HtmlDivision div = (HtmlDivision) td.getFirstElementChild();
						String title = div.getTextContent();
						HtmlTableDataCell td2 = (HtmlTableDataCell) td_list_it.next();
						String value = td2.getTextContent();
						//System.out.println(title+":"+value);
						if(title.equals("Hometown")){user.setHometown(value);}
						else if(title.equals("Current City")){user.setLiving(value);}
					}
				}
			}
			SaveInfo.saveUser(user.toString());
			System.out.println("Already got userinfo of "+uid);
		}catch (Exception e){
			e.printStackTrace();
			SaveInfo.saveErrorUserInfo(uid+"\t:Exception-----"+e.getMessage());
		}
	}
}
