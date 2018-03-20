package com.example.show;

import com.example.getWeb.getWeb;
import com.spreada.utils.chinese.ZHConverter;

import org.ansj.splitWord.analysis.ToAnalysis;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.LeadTo.LeadTo;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.graphics.Color;
import android.graphics.Typeface;
import android.os.Bundle;
import android.os.Handler;
import android.os.Message;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;

import java.util.*;

public class MainActivity extends Activity  implements Runnable 
{
    public void run()  
    {  
    	ToAnalysis.parse("是");
    	isLoaded = true;
    }  
	
	public Map<String, String> getDouban()
	{
		getWeb douban = new getWeb("https://www.douban.com/group/blabla/");
		douban.start();
		try
		{
			douban.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		Elements doubanNews = douban.getContent().getElementsByClass("olt").first().getElementsByTag("tr");
		Map m2 = new HashMap<String, String>();

		int i = 0;
		for (Element each : doubanNews)
		{
			if (i++ <= 2)
				continue;
			if (each.getElementsByTag("a").isEmpty())
				continue;

			m2.put(each.getElementsByTag("a").attr("href"), each.getElementsByTag("a").text());
		}

		return m2;
	}

	public Map<String, String> getApple()
	{
		getWeb apple = new getWeb("https://tw.entertainment.appledaily.com/realtime");
		apple.start();
		try
		{
			apple.join();
		} catch (InterruptedException e)
		{
			e.printStackTrace();
		}

		Element appleNews = apple.getContent().getElementById("maincontent");
		Map m1 = new HashMap<String, String>();
		for (Element link : appleNews.getElementsByTag("a"))
		{
			String Links = "https://tw.entertainment.appledaily.com" + link.attr("href");
			String Titles = link.getElementsByTag("font").text().toString();

			if (Titles.replace("　", "").isEmpty())
				continue;

			Titles = Titles.replace("　", "，");
			int pos = Titles.lastIndexOf("(");
			if (pos == -1)
				pos = Titles.length();

			m1.put(Links, Titles.substring(0, pos));
		}

		return m1;
	}

	public Vector<Integer> ViewToMap()
	{
		Vector<Integer> textViews = new Vector<Integer>();
		textViews.add(R.id.TextView1);
		textViews.add(R.id.TextView2);
		textViews.add(R.id.TextView3);
		textViews.add(R.id.TextView4);
		textViews.add(R.id.TextView5);
		textViews.add(R.id.TextView6);
		textViews.add(R.id.TextView7);
		textViews.add(R.id.TextView8);
		textViews.add(R.id.TextView9);
		textViews.add(R.id.TextView10);
		textViews.add(R.id.TextView11);
		textViews.add(R.id.TextView12);
		textViews.add(R.id.TextView13);
		textViews.add(R.id.TextView14);
		textViews.add(R.id.TextView15);
		textViews.add(R.id.TextView16);
		textViews.add(R.id.TextView17);
		textViews.add(R.id.TextView18);
		textViews.add(R.id.TextView19);
		textViews.add(R.id.TextView20);
		textViews.add(R.id.TextView21);
		textViews.add(R.id.TextView22);
		textViews.add(R.id.TextView23);
		textViews.add(R.id.TextView24);
		textViews.add(R.id.TextView25);
		textViews.add(R.id.TextView26);
		textViews.add(R.id.TextView27);
		textViews.add(R.id.TextView28);
		textViews.add(R.id.TextView29);
		textViews.add(R.id.TextView30);
		textViews.add(R.id.TextView31);
		textViews.add(R.id.TextView32);
		textViews.add(R.id.TextView33);
		textViews.add(R.id.TextView34);
		textViews.add(R.id.TextView35);
		textViews.add(R.id.TextView36);
		textViews.add(R.id.TextView37);
		textViews.add(R.id.TextView38);
		textViews.add(R.id.TextView39);
		textViews.add(R.id.TextView40);
		textViews.add(R.id.TextView41);
		textViews.add(R.id.TextView42);
		textViews.add(R.id.TextView43);
		textViews.add(R.id.TextView44);
		textViews.add(R.id.TextView45);
		textViews.add(R.id.TextView46);
		textViews.add(R.id.TextView47);
		textViews.add(R.id.TextView48);
		textViews.add(R.id.TextView49);
		textViews.add(R.id.TextView50);
		textViews.add(R.id.TextView51);
		textViews.add(R.id.TextView52);
		textViews.add(R.id.TextView53);
		textViews.add(R.id.TextView54);
		textViews.add(R.id.TextView55);
		textViews.add(R.id.TextView56);
		textViews.add(R.id.TextView57);
		textViews.add(R.id.TextView58);
		textViews.add(R.id.TextView59);
		textViews.add(R.id.TextView60);
		textViews.add(R.id.TextView61);
		textViews.add(R.id.TextView62);
		textViews.add(R.id.TextView63);
		textViews.add(R.id.TextView64);
		textViews.add(R.id.TextView65);
		textViews.add(R.id.TextView66);
		textViews.add(R.id.TextView67);
		textViews.add(R.id.TextView68);
		textViews.add(R.id.TextView69);
		textViews.add(R.id.TextView70);
		textViews.add(R.id.TextView71);
		textViews.add(R.id.TextView72);
		textViews.add(R.id.TextView73);
		textViews.add(R.id.TextView74);
		textViews.add(R.id.TextView75);
		textViews.add(R.id.TextView76);
		textViews.add(R.id.TextView77);
		textViews.add(R.id.TextView78);
		return textViews;
	}

	@Override
	protected void onCreate(Bundle savedInstanceState)
	{ 
		new Thread(this, "新线程").start();  
        
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		
		mDouban = getDouban();
		Iterator<Map.Entry<String, String>> entries_Douban = mDouban.entrySet().iterator();
		viewMap = ViewToMap();

		int ic = 0;
		while (entries_Douban.hasNext()) // 27
		{
			Map.Entry<String, String> entry = entries_Douban.next(); // entry.getKey()
			TextView tv = (TextView) this.findViewById(viewMap.get(ic));
			m_ID_INT.put(viewMap.get(ic), entry.getKey());
			m_ID_AppleOrDouban.put(viewMap.get(ic), "Douban");
			tv.setText(entry.getValue());			
			ic++;
		}
		curIC = ic;
		
        Toast.makeText(getApplicationContext(), "载入中…", Toast.LENGTH_LONG).show(); 
		mTimeHandler.sendEmptyMessageDelayed(0, 500);
	}

	public void NextPage(View view)
	{
		if (m_ID_AppleOrDouban.get(view.getId()) == "Douban")
		{
			if (isLoaded)
			{
				TextView tv = (TextView) findViewById(view.getId());
				tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
				new LeadTo("Douban", this, m_ID_INT.get(view.getId()));
			}
			else
		        Toast.makeText(getApplicationContext(), "载入中…", Toast.LENGTH_LONG).show();  	                

		}
		else if (m_ID_AppleOrDouban.get(view.getId()) == "Apple")
		{
			TextView tv = (TextView) findViewById(view.getId());
			tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			new LeadTo("Apple", this, m_ID_INT.get(view.getId()));
		}
	}

	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.main, menu);
		return true;
	}

	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		// Handle action bar item clicks here. The action bar will
		// automatically handle clicks on the Home/Up button, so long
		// as you specify a parent activity in AndroidManifest.xml.
		int id = item.getItemId();
		if (id == R.id.action_settings)
		{
			return true;
		}

		return super.onOptionsItemSelected(item);
	}

	private Map<String, String> mApple, mDouban;
	private Map<Integer, String> m_ID_INT = new HashMap<Integer, String>();
	private Map<Integer, String> m_ID_AppleOrDouban = new HashMap<Integer, String>();
	private static boolean isLoaded = false;
	private static int curIC = 0;
	private static Vector<Integer> viewMap;
	
	private Handler mTimeHandler = new Handler() {
        public void handleMessage(android.os.Message msg) {
            if (msg.what == 0)
            {
        		mApple = getApple();
        		Iterator<Map.Entry<String, String>> entries_Apple = mApple.entrySet().iterator();

        		// 繁体转简
        		ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
        		while (entries_Apple.hasNext()) // 27
        		{	
        			Map.Entry<String, String> entry = entries_Apple.next(); // entry.getKey()
        			TextView tv = (TextView)findViewById(viewMap.get(curIC));
        			m_ID_INT.put(viewMap.get(curIC), entry.getKey());
        			m_ID_AppleOrDouban.put(viewMap.get(curIC), "Apple");
        			tv.setText(converter.convert(entry.getValue()));
        			tv.setBackgroundColor(Color.rgb(255, 211, 155));
        			curIC++;
        		}
            }
        }
    };
}
