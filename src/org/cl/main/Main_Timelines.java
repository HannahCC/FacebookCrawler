package org.cl.main;

import java.io.File;

import org.cl.conf.Config;
import org.cl.crawler.Crawler_Timelines;
import org.cl.service.RWUid;
import org.cl.service.SaveInfo;

import com.gargoylesoftware.htmlunit.WebClient;

public class Main_Timelines {
	public static int deepId = 1;
	public static void main_Timelines(WebClient wc, int deepId,RWUid userid_src){
		SaveInfo.initForTimelinesCrawler(deepId);
		RWUid userid = userid_src.copy();
		idFilter(deepId,userid);
		String uid = "";
		while((uid=userid.getUid())!=null){
			System.out.println(uid);
			Crawler_Timelines timelines = new Crawler_Timelines(wc,uid);
			timelines.run();
		}
		SaveInfo.closeForTimelineCrawler();
	}

	/**
	 * 清除已经获取Timeline的ID
	 * 包括：Timelines文件下出现的ID
	 * Stat/UesrNotExist.txt
	 * @param deepId 
	 * @param userid
	 */
	public static void idFilter(int deepId, RWUid userid){
		File dir = new File(Config.ROOT_PATH+"/"+deepId+"/"+"Timelines/");
		File[] files = dir.listFiles();
		for(File f : files){
			String name = f.getName();
			name = name.replaceAll(".txt", "");
			userid.delete(name);
		}

		RWUid clearid = SaveInfo.getUserId("/"+deepId+"/Stat/UserNotExist.txt");
		while(clearid.getNum()!=0){
			String id = clearid.getUid();
			userid.delete(id);
		}
	}
}
