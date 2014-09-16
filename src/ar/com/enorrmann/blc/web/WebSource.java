package ar.com.enorrmann.blc.web;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.InetSocketAddress;
import java.net.MalformedURLException;
import java.net.Proxy;
import java.net.URL;
import java.util.concurrent.TimeUnit;

import com.google.common.cache.CacheBuilder;
import com.google.common.cache.CacheLoader;
import com.google.common.cache.LoadingCache;

public class WebSource extends Thread {

	final static boolean USE_PROXY = false;
	private String sourceCode;
	private String url;
	static LoadingCache<String, String> cache;
	static {
		cache = CacheBuilder.newBuilder()
				.expireAfterAccess(30, TimeUnit.MINUTES)
				.build(
						new CacheLoader<String, String>() {
							public String load(String key) { 
								return queryUrl(key);
							}
						});
	}
	public WebSource(String url){
		this.url = url;
	}

	@Override
	public void run() {
		//sourceCode = queryUrl(url);
		sourceCode = cache.getUnchecked(url);
	}

	private static String queryUrl(String url) {
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
