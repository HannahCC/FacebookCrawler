package org.cl.main;

import org.cl.service.Login;
import org.cl.service.RWUid;
import org.cl.service.SaveInfo;

import com.gargoylesoftware.htmlunit.WebClient;

public class Main {
	public static int deepId = 1;
	public static void main(String args[]) throws InterruptedException{
		if(args.length>0)deepId = Integer.parseInt(args[0]);
		//爬两层
		for(int i=1;i<=deepId;i++){
			//633039562
			RWUid userid = SaveInfo.getUserId("/UserId"+i+".txt");
			WebClient wc = Login.login();
			Main_UserInfo.main_UserInfo(wc,i,userid);
			Main_Friends.main_Friends(wc,i,userid);
			Main_Timelines.main_Timelines(wc,i,userid);
		}
	}
}
