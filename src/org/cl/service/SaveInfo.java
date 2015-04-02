package org.cl.service;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.Iterator;
import java.util.List;

import org.cl.conf.Config;
import org.cl.model.Timeline;

/** 保存信息到文件*/
public class SaveInfo 
{

	private static String path = "";
	//拓展用户ID
	private static FileOutputStream user_id=null;
	//用户信息
	private static FileOutputStream user_info=null;
	//关系过多的用户信息
	private static FileOutputStream user_info_toomany=null;
	//好友	原用户ID	扩展用户ID		关系
	//private static FileOutputStream friends=null;
	//保存推特信息的文件夹名
	private static final String timeline_dir="/Timelines/";
	//保存推特信息的文件夹名
	private static final String friends_dir="/Friends/";
	//用户信息获取失败的用户ID
	//private static FileOutputStream failure_userinfo=null;
	//获取互粉关系失败的用户ID
	private static FileOutputStream user_notExist=null;
	//用户好友获取失败的用户ID
	//private static FileOutputStream failure_friends=null;
	//用户推特获取失败的用户ID
	//private static FileOutputStream failure_timelines=null;
	//获取用户信息时异常记录
	private static FileOutputStream error_userinfo=null;
	//获取用户好友时异常记录
	private static FileOutputStream error_friends=null;
	//获取用户状态时异常记录
	private static FileOutputStream error_timelines=null;
	public static void initForUserInfoCrawler(int deepId){
		path = Config.ROOT_PATH+deepId+"/";
		File file= new File(path+"Stat");//创建统计文件目录
		if(!file.exists()){file.mkdirs();}
		//创建结果存放文件
		File temp2=new File(path+"UserInfo.txt");
		File temp4=new File(path+"Stat/UserNotExist.txt");
		//File temp5=new File(path+"Stat/FailToGet_userInfo.txt");
		File temp6=new File(path+"Stat/Error_userInfo.txt");

		try 
		{
			user_info=new FileOutputStream(temp2,true);
			user_notExist=new FileOutputStream(temp4,true);
			//failure_userinfo=new FileOutputStream(temp5,true);
			error_userinfo=new FileOutputStream(temp6,true);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	public static void initForFriendsCrawler(int deepId){
		path = Config.ROOT_PATH+deepId+"/";
		File file= new File(path+"Stat");//创建统计文件目录
		if(!file.exists()){file.mkdirs();}
		//创建结果存放文件
		File temp2=new File(path+"Friends");
		if(!temp2.exists()){temp2.mkdirs();}
		File temp3=new File(path+"UserInfo_tooManyShip.txt");
		//File temp4=new File(path+"Stat/FailToGet_Friends.txt");
		File temp5=new File(Config.ROOT_PATH+"UserId"+(deepId+1)+".txt");
		File temp6=new File(path+"Stat/Error_userInfo.txt");
		try 
		{
			user_info_toomany=new FileOutputStream(temp3,true);
			//friends=new FileOutputStream(temp2,true);
			//failure_friends=new FileOutputStream(temp4,true);
			user_id=new FileOutputStream(temp5,true);
			error_friends=new FileOutputStream(temp6,true);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	public static void initForTimelinesCrawler(int deepId){
		path = Config.ROOT_PATH+deepId+"/";
		File file1= new File(path+"Stat");//创建统计文件目录
		if(!file1.exists()){file1.mkdirs();}
		File file2= new File(path+timeline_dir);//创建存储timelines的文件夹
		if(!file2.exists()){file2.mkdirs();}
		System.out.println(path);
		//创建结果存放文件
		//File temp=new File(path+"Stat/FailToGet_timelines.txt");
		File temp2=new File(path+"Stat/Error_userInfo.txt");
		try 
		{
			//failure_timelines=new FileOutputStream(temp,true);
			error_timelines=new FileOutputStream(temp2,true);
		} 
		catch (FileNotFoundException e) 
		{
			e.printStackTrace();
		}
	}
	/** 关闭所有与用户信息存储文件
	 * @throws IOException */
	public static void closeForUserInfoCrawler()
	{
		try {
			user_info.flush();
			user_info.close();
			//failure_userinfo.flush();
			//failure_userinfo.close();
			user_notExist.flush();
			user_notExist.close();
			error_userinfo.flush();
			error_userinfo.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/** 关闭所有与好友相关的文件
	 * @throws IOException */
	public static void closeForFriendsCrawler()
	{
		try {
			/*friends.flush();
			friends.close();*/
			user_info_toomany.flush();
			user_info_toomany.close();
			//failure_friends.flush();
			//failure_friends.close();
			user_id.flush();
			user_id.close();
			error_friends.flush();
			error_friends.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/** 关闭所有与推特相关的文件
	 * @throws IOException */
	public static void closeForTimelineCrawler()
	{
		try {
			//failure_timelines.flush();
			//failure_timelines.close();
			error_timelines.flush();
			error_timelines.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 保存扩展用户的ID
	 * @throws IOException */
	public synchronized static void saveExpandID(List<String> friends_id)
	{
		String info = "";
		try {
			Iterator<String> it = friends_id.iterator();
			while(it.hasNext()){
				info = it.next()+"\r\n";
				//System.out.print(info);
				user_id.write(info.getBytes());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}

	}
	/** 保存好友
	 * @param friends_id 
	 * @throws IOException */
	public synchronized static void saveFriends(String uid, List<String> friends_id)
	{
		if(friends_id.size()==0)return;
		File f = new File(path+friends_dir+uid+".txt");
		try {
			BufferedWriter w = new BufferedWriter(new FileWriter(f));
			for(String id : friends_id){
				w.write(id+"\r\n");
			}
			w.flush();
			w.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		/*String info = "";
		try {
			Iterator<String> it = friends_id.iterator();
			while(it.hasNext()){
				info = uid +"\t"+it.next()+"\r\n";
				friends.write(info.getBytes());
			}
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}*/
	}

	/** 保存用户信息
	 * @throws IOException */
	public synchronized static void saveUser(String info)
	{
		try {
			user_info.write(info.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 关系过多的用户信息
	 * @throws IOException */
	public synchronized static void saveUsertooMany(String info)
	{
		info += "\r\n";
		try {
			user_info_toomany.write(info.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 获取用户信息失败的用户ID
	 * @throws IOException */
	/*public synchronized static void saveFailure_userinfo(String info)
	{
		info += "\r\n";
		try {
			failure_userinfo.write(info.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	/** 获取好友失败的用户ID
	 * @throws IOException */
	/*public synchronized static void saveFailure_Friends(String info)
	{
		info += "\r\n";
		try {
			failure_friends.write(info.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/
	/** 获取微博失败的用户ID
	 * @throws IOException */
	/*public synchronized static void savefailure_timelines(String info)
	{
		info += "\r\n";
		try {
			failure_timelines.write(info.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}*/

	/** 不存在的用户ID
	 * @throws IOException */
	public synchronized static void saveUserNotExist(String info)
	{
		info += "\r\n";
		try {
			user_notExist.write(info.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	/** 保存推特
	 * @throws IOException */
	public synchronized static void savetimelines(String uid,List<Timeline> timelines)
	{
		File file=new File(path+timeline_dir+uid+".txt");
		FileOutputStream fout;
		try {
			fout = new FileOutputStream(file,true);
			for(Timeline t : timelines)
			{
				fout.write(t.toString().getBytes());
			}
			fout.flush();
			fout.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} finally{

		}
	}

	public static RWUid getUserId(String file_name){
		File f = new File(Config.ROOT_PATH+"/"+file_name);
		RWUid userId = new RWUid();
		BufferedReader br = null;
		try {
			br=new BufferedReader(new FileReader(f));
			String uid=null;
			while((uid=br.readLine())!=null)
			{
				if(!(uid.equals("")))userId.setUid(uid);
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userId;
	}

	//获取文件每行中第subscript个元祖，每个元祖之间使用regex隔开
	public static RWUid getUserId(String file_name,String regex,int subscript){
		File f = new File(Config.ROOT_PATH+"/"+file_name);
		RWUid userId = new RWUid();
		BufferedReader br = null;
		try {
			br=new BufferedReader(new FileReader(f));
			String uid=null;
			while((uid=br.readLine())!=null)
			{
				String[] tmp = uid.split(regex);
				if(tmp.length>subscript){
					uid = tmp[subscript];
					if(!(uid.equals("")))userId.setUid(uid);
				}
			}
			br.close();
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		return userId;
	}
	//获取用户timeline时，若中途遇到错误，则删除此前获取的timelines，防止下次重新获取时数据重复
	public synchronized static void deleteFiles(String uid) {
		// TODO Auto-generated method stub
		File file=new File(path+timeline_dir+"/"+uid+".txt");
		if(file.exists()){file.delete();}
	}

	public synchronized static void saveErrorUserInfo(String info) {
		info += "\r\n";
		try {
			error_userinfo.write(info.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized static void saveErrorFriends(String info) {
		info += "\r\n";
		try {
			error_friends.write(info.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
	public synchronized static void saveErrorTimelines(String info) {
		info += "\r\n";
		try {
			error_timelines.write(info.getBytes());
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}
}

