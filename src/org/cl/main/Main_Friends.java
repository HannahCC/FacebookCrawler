package org.cl.main;

import java.io.File;

import org.cl.conf.Config;
import org.cl.crawler.Crawler_Friends;
import org.cl.service.RWUid;
import org.cl.service.SaveInfo;

import com.gargoylesoftware.htmlunit.WebClient;

public class Main_Friends {
	public static int deepId = 1;
	public static void main_Friends(WebClient wc, int deepId, RWUid userid_src){
		SaveInfo.initForFriendsCrawler(deepId);
		RWUid userid = userid_src.copy();
		idFilter(deepId,userid);
		String uid = "";
		while((uid=userid.getUid())!=null){
			Crawler_Friends friends = new Crawler_Friends(wc,uid);
			friends.run();
		}
		SaveInfo.closeForFriendsCrawler();
	}
	

	/**
	 * 清除已经获取Timeline的ID
	 * 包括：Timelines文件下出现的ID
	 * Stat/UesrNotExist.txt
	 * @param deepId 
	 * @param userid
	 */
	public static void idFilter(int deepId, RWUid userid){
		/*虽然toomany,但是还是要获取其本身的信息
		 * RWUid clearid = SaveInfo.getUserId("/"+deepId+"/UserInfo_tooManyShip.txt");
		while(clearid.getNum()!=0){
			String id = clearid.getUid();
			userid.delete(id);
		}*/
		File dir = new File(Config.ROOT_PATH+"/"+deepId+"/Friends/");
		File[] flist = dir.listFiles();
		for(File f : flist){
			String id = f.getName().replace(".txt", "");
			userid.delete(id);
		}
		RWUid clearid = SaveInfo.getUserId("/"+deepId+"/Stat/UserNotExist.txt");
		while(clearid.getNum()!=0){
			String id = clearid.getUid();
			userid.delete(id);
		}
	}
}
