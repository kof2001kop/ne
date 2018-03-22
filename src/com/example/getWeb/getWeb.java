package com.example.getWeb;
import java.io.IOException;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;


public class getWeb extends Thread
{
	public getWeb(String url)
	{
		this.url = url;
	}
	
	public Document getContent()
	{
		return content;
	}
	
	public void run()
	{ 
		try
		{
			Document doc = Jsoup.connect(url).ignoreContentType(true).get();
			content = doc;
		} 
		catch (IOException e)
		{
			e.printStackTrace(); // TODO 自动生成的 catch 块
		}
	}
	
	private Document content;
	private String url;
}
