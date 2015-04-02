package org.cl.main;

import org.cl.crawler.Crawler_UserInfo;
import org.cl.service.RWUid;
import org.cl.service.SaveInfo;

import com.gargoylesoftware.htmlunit.WebClient;

public class Main_UserInfo {
	public static int deepId = 1;
	public static void main_UserInfo(WebClient wc, int deepId,RWUid userid_src){
		RWUid userid = userid_src.copy();
		SaveInfo.initForUserInfoCrawler(deepId);
		idFilter(deepId,userid);
		String uid = "";
		while((uid=userid.getUid())!=null){
			Crawler_UserInfo userinfo = new Crawler_UserInfo(wc,uid);
			userinfo.run();
		}
		SaveInfo.closeForUserInfoCrawler();
	}
	
	/**
	 * 清除已经获取Timeline的ID
	 * 包括：Timelines文件下出现的ID
	 * Stat/UesrNotExist.txt
	 * @param deepId 
	 * @param userid
	 */
	public static void idFilter(int deepId, RWUid userid){
		RWUid clearid = SaveInfo.getUserId("/"+deepId+"/UserInfo.txt","\t",0);
		while(clearid.getNum()!=0){
			String id = clearid.getUid();
			userid.delete(id);
		}
		clearid = SaveInfo.getUserId("/"+deepId+"/Stat/UserNotExist.txt");
		while(clearid.getNum()!=0){
			String id = clearid.getUid();
			userid.delete(id);
		}
	}
}
