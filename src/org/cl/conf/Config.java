package org.cl.conf;

import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
/**
PRO_NAME = Facebook_Crawler
RES_NAME = Facebook_res
REQUEST_INTERVAL = 2000
REQUEST_MAX = 450
FRIENDS_MAX = 1000
TIMELINE_MAX = 300
COMMENT_MAX = 50
RETRY_MAX = 2
 * @author Chenli
 *
 */
public class Config {
	private static String PRO_NAME = "Facebook_Crawler";
	private static String RES_NAME = "Facebook_res";
	public static String ROOT_PATH = "";
	public static List<String> USERNAME;
	public static List<String> PASSWORD;
	public static int	SLEEP_TIME = 3600000;//millisecond
	public static int	REQUEST_INTERVAL = 2000;//millisecond
	public static int	REQUEST_MAX = 450;//times
	public static int	FRIENDS_MAX = 1000;
	public static int	TIMELINE_MAX = 300;
	public static int	COMMENT_MAX = 50;
	public static int	RETRY_MAX = 2;
	public static String PROXY = "";
	public static int PROT = 0;
	
	static {
		ROOT_PATH = System.getProperty("user.dir");
		ROOT_PATH = Config.ROOT_PATH.replace(PRO_NAME, "");
		ROOT_PATH += "/"+RES_NAME+"/";
		//获取配置
		File f = new File(ROOT_PATH+"Config.txt");
		Map<String,String> confmap = new HashMap<String,String>();
		BufferedReader r;
		try {
			r = new BufferedReader(new FileReader(f));
			String conf = "";
			while((conf = r.readLine())!= null){
				String conf_name = conf.split(" = ")[0];
				String conf_value =  conf.split(" = ")[1];
				confmap.put(conf_name, conf_value);
			}
/*			if(confmap.containsKey("PRO_NAME")){PRO_NAME = confmap.get("PRO_NAME");}
			if(confmap.containsKey("RES_NAME")){RES_NAME = confmap.get("RES_NAME");}*/
			if(confmap.containsKey("SLEEP_TIME")){SLEEP_TIME = Integer.parseInt(confmap.get("SLEEP_TIME"));}
			if(confmap.containsKey("REQUEST_INTERVAL")){REQUEST_INTERVAL = Integer.parseInt(confmap.get("REQUEST_INTERVAL"));}
			if(confmap.containsKey("REQUEST_MAX")){REQUEST_MAX = Integer.parseInt(confmap.get("REQUEST_MAX"));}
			if(confmap.containsKey("FRIENDS_MAX")){FRIENDS_MAX = Integer.parseInt(confmap.get("FRIENDS_MAX"));}
			if(confmap.containsKey("TIMELINE_MAX")){TIMELINE_MAX = Integer.parseInt(confmap.get("TIMELINE_MAX"));}
			if(confmap.containsKey("COMMENT_MAX")){COMMENT_MAX = Integer.parseInt(confmap.get("COMMENT_MAX"));}
			if(confmap.containsKey("RETRY_MAX")){RETRY_MAX = Integer.parseInt(confmap.get("RETRY_MAX"));}
			if(confmap.containsKey("PROXY")){PROXY = confmap.get("PROXY");}
			if(confmap.containsKey("PROT")){PROT = Integer.parseInt(confmap.get("PROT"));}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//获取用户名与密码
		f = new File(ROOT_PATH+"Account.txt");
		try {
			r = new BufferedReader(new FileReader(f));
			String usr = "";
			USERNAME = new ArrayList<String>();
			PASSWORD = new ArrayList<String>();
			while((usr = r.readLine())!= null){
				USERNAME.add(usr.split("\t")[0]);
				PASSWORD.add(usr.split("\t")[1]);
			}
		} catch (FileNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
	}
	
}
