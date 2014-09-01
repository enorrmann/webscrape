package ar.com.enorrmann.blc.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;

public class WebSource extends Thread {

	final boolean USE_PROXY = true;
	private String sourceCode;
	private String url;
	
	public WebSource(String url){
		this.url = url;
	}

	@Override
	public void run() {
		sourceCode = queryUrl(url);
	}

	private String queryUrl(String url) {
		URL website = null;
		try {
			website = new URL(url);
		} catch (MalformedURLException e) {
			e.printStackTrace();
			return null;
		}
		HttpURLConnection httpUrlConnetion;
		StringBuilder buffer = new StringBuilder();
		try {
			if (USE_PROXY) {
				Proxy proxy = new Proxy(Proxy.Type.SOCKS,new InetSocketAddress("127.0.01", 9050));
				httpUrlConnetion = (HttpURLConnection) website.openConnection(proxy);
			} else {
				httpUrlConnetion = (HttpURLConnection) website.openConnection();
			}
			httpUrlConnetion.connect();
			BufferedReader br = new BufferedReader(new InputStreamReader(httpUrlConnetion.getInputStream()));
			String str;

			while ((str = br.readLine()) != null) {
				buffer.append(str);
			}
		} catch (IOException ie) {
			ie.printStackTrace();
		}
		return buffer.toString();
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getSourceCode() {
		return sourceCode;
	}

}
