package org.cl.test;

import java.io.IOException;
import java.net.MalformedURLException;

import org.cl.crawler.Crawler_Friends;
import org.cl.service.Login;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;

public class Test {

	public static void main(String args[]) throws FailingHttpStatusCodeException, MalformedURLException, IOException{
		WebClient wc = Login.login();
		/*SaveInfo.initForUserInfoCrawler(1, "Facebook_res");
		SaveInfo.initForFriendsCrawler(1, "Facebook_res");
		SaveInfo.initForTimelinesCrawler(1, "Facebook_res");
		*/
		//100000843720231		Facebook
		//50883326604		ARTINFO		
		//399956561000		UNINOVE							
		//	334403363297706		Facebook
		String uid = "100004058723986";//"100001954342906";////"100002812187295";//"100002680625831";//Jinhua Ouyang;//yang zhang//100007715404094
		/*Crawler_UserInfo userinfo = new Crawler_UserInfo(wc,uid);
		userinfo.run();*/
		Crawler_Friends friends = new Crawler_Friends(wc,uid);
		friends.run();
		/*Crawler_Timelines timelines = new Crawler_Timelines(wc,uid);
		timelines.run();*/
		/*String comment_href = "/photo.php?fbid=331581850278885&id=100002812187295&set=a.147276432042762.19157.100002812187295&refid=17&_ft_=app_id.0";
		Crawler_Comments commenst = new Crawler_Comments(wc,comment_href);
		commenst.run();*/

	}
}
