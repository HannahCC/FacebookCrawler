package org.cl.service;

import java.io.IOException;
import java.net.MalformedURLException;
import java.text.SimpleDateFormat;
import java.util.Date;

import org.cl.conf.Config;

import com.gargoylesoftware.htmlunit.FailingHttpStatusCodeException;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;

public class HttpRequest {
	/**
	 * 防止因为网络原因而获取网页失败，每次失败后进行重试，最多重试3次
	 * @param wc
	 * @param href
	 * @return
	 */
	private static long time = 0;
	private static int request_counts = 0;
	public synchronized static HtmlPage getPage(WebClient wc,String href){
		Date now_time = new Date();
		long now = now_time.getTime();
		//降低爬取速度，防止账号被锁
		try {
			if(now-time<=Config.REQUEST_INTERVAL){//每次请求相隔2s
				Thread.sleep(Config.REQUEST_INTERVAL);
			}
			time = now;
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		//当请求次数达到一定数量后，程序休眠30min，降低爬取频数
		try {
			if(request_counts>Config.REQUEST_MAX){
				System.out.println(getCurrentTime()+"----------------Sleeping for REQUEST_COUNT limitation!");
				Thread.sleep(Config.SLEEP_TIME);
				request_counts = 0;
			}
		} catch (InterruptedException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		HtmlPage page = null;
		int retry = 0;
		while(page==null&&retry<Config.RETRY_MAX){
			try {
				page = wc.getPage(href);
			} catch (FailingHttpStatusCodeException e) {
				e.printStackTrace();
				if(e.getMessage().contains("500")){
					try {
						System.out.println(getCurrentTime()+"---------------Sleeping for REQUEST_500!");
						Thread.sleep(Config.SLEEP_TIME);
						request_counts = 0;	
						retry = 0;
						page=null;
					} catch (InterruptedException e1) {
						// TODO Auto-generated catch block
						e1.printStackTrace();
					}
				}
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		request_counts++;
		return page;
	}

	public synchronized static HtmlPage click(HtmlSubmitInput login){
		HtmlPage page = null;
		int retry = 0;
		while(page==null&&retry<Config.RETRY_MAX){
			try {
				page = login.click();
			} catch (FailingHttpStatusCodeException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (MalformedURLException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
		}
		return page;
	}
	
	
	private  synchronized static String getCurrentTime(){
		Date date = new Date();
		SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd,HH:mm:ss");
		return sdf.format(date);
	}
}
