package org.cl.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import org.cl.service.RWUid;
import org.cl.service.SaveInfo;

public class GetUncatchedUid {
	public static int deepId = 1;
	public static void main(String args[]) throws IOException{
		if(args.length>0){deepId = Integer.parseInt(args[0]);}
		File uid_catched = new File("E:\\Working\\Project_DataObtain\\Facebook_res\\1\\UserInfo.txt");
		BufferedReader r2 = new BufferedReader(new FileReader(uid_catched));
		File uid_uncatched = new File("E:\\Working\\Project_DataObtain\\Facebook_res\\UserId"+deepId+"_uncatched.txt");
		BufferedWriter w = new BufferedWriter(new FileWriter(uid_uncatched));
		String line = "";
		List<String> uid_catched_list = new ArrayList<String>();
		while((line = r2.readLine())!=null){
			String[] item = line.split("\t");
			uid_catched_list.add(item[0]);
		}
		RWUid userid = SaveInfo.getUserId("\\UserId"+deepId+".txt");
		Iterator<String> it = uid_catched_list.iterator();
		while(it.hasNext()){
			String uid = it.next();
			userid.delete(uid);
		}
		String uid_u = "";
		while((uid_u = userid.getUid())!=null){
			w.write(uid_u+"\r\n");
		}
		r2.close();
		w.close();
	}
}
