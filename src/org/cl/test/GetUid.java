package org.cl.test;

import java.io.BufferedReader;
import java.io.BufferedWriter;
import java.io.File;
import java.io.FileReader;
import java.io.FileWriter;
import java.io.IOException;

public class GetUid {
	public static void main(String args[]) throws IOException{
		File src = new File("E:\\Working\\Project_DataObtain\\success.txt");
		BufferedReader r = new BufferedReader(new FileReader(src));
		File src_twitter = new File("E:\\Working\\Project_DataObtain\\twitter.txt");
		BufferedWriter w1 = new BufferedWriter(new FileWriter(src_twitter));
		File src_facebook = new File("E:\\Working\\Project_DataObtain\\facebook.txt");
		BufferedWriter w2 = new BufferedWriter(new FileWriter(src_facebook));
		String line = "";
		while((line = r.readLine())!=null){
			if(line.contains("*"))continue;
			String[] item = line.split("\t");
			w2.write(item[2]+"\r\n");
			w1.write(item[3]+"\r\n");
		}
		r.close();
		w1.close();
		w2.close();
	}
}
