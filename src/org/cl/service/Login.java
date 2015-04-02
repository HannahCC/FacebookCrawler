package org.cl.service;
import org.cl.conf.Config;

import com.gargoylesoftware.htmlunit.BrowserVersion;
import com.gargoylesoftware.htmlunit.WebClient;
import com.gargoylesoftware.htmlunit.html.HtmlForm;
import com.gargoylesoftware.htmlunit.html.HtmlPage;
import com.gargoylesoftware.htmlunit.html.HtmlPasswordInput;
import com.gargoylesoftware.htmlunit.html.HtmlSubmitInput;
import com.gargoylesoftware.htmlunit.html.HtmlTextInput;

public class Login {

	public static WebClient login(){
		WebClient wc = null;
		if(Config.PROXY.equals("")){wc = new WebClient(BrowserVersion.CHROME);}
		else{wc = new WebClient(BrowserVersion.CHROME,Config.PROXY,Config.PROT);}
		/*HttpClient httpclient = new DefaultHttpClient();
		HttpClientParams.setCookiePolicy(httpclient.getParams(), CookiePolicy.BROWSER_COMPATIBILITY);*/
		//WebClient wc = new WebClient(BrowserVersion.CHROME);
		wc.getOptions().setCssEnabled(false);
		wc.getOptions().setActiveXNative(false);
		wc.getOptions().setJavaScriptEnabled(false);
		
		HtmlPage page = HttpRequest.getPage(wc, "https://www.facebook.com/");
		//如果得到的页面标题不对，则登录失败，否则执行提交登录请求
		if(!"Welcome to Facebook - Log In, Sign Up or Learn More".endsWith(page.getTitleText())){
			System.out.println("get the wrong page:"+page.getTitleText());return null;
		}
		//获取登录表单，提交登录请求
		HtmlForm login_form = (HtmlForm) page.getElementById("login_form");
		HtmlTextInput username = login_form.getInputByName("email");
		
		if(Config.USERNAME.size()==0||Config.PASSWORD.size()==0){System.out.println("Please write your Account into Account.txt file.");System.exit(-1);}
		username.setValueAttribute(Config.USERNAME.get(0));
		Config.USERNAME.remove(0);
		HtmlPasswordInput password = login_form.getInputByName("pass");
		password.setValueAttribute(Config.PASSWORD.get(0));
		Config.PASSWORD.remove(0);
		HtmlSubmitInput login = login_form.getInputByValue("Log In");
		HtmlPage index = HttpRequest.click(login);
		
		//判断是否登录成功
		//System.out.println(index.asText());
		if(!"Facebook".endsWith(index.getTitleText())){System.out.println("login fail!:"+index.getTitleText());return null;}
		System.out.println("login successfully!");
		return wc;

	}
}
