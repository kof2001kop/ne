package com.example.show;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Collection;
import java.util.Collections;
import java.util.Comparator;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Map;
import java.util.Vector;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import org.ansj.app.keyword.KeyWordComputer;
import org.ansj.app.keyword.Keyword;
import org.ansj.domain.Result;
import org.ansj.splitWord.analysis.ToAnalysis;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.getWeb.getWeb;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.AlertDialog.Builder;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.CheckBox;
import android.widget.EditText;
import android.widget.LinearLayout;
import android.widget.RadioButton;
import android.widget.TextView;
import android.widget.Toast;

public class DoubanActivity extends Activity
{	
	public static native String StrToJson(String str);
	
	static
	{
		System.loadLibrary("crystax");
        System.loadLibrary("show");
	}
	
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		clean();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_douban);
		
		getWeb content = new getWeb(url);
		content.start();
		try { content.join(); } catch (InterruptedException e) { e.printStackTrace(); }
	
		Vector<String> v_Result = new Vector<String>();
		title = "";
		
		if (!content.getContent().getElementsByTag("h1").isEmpty())
			title = content.getContent().getElementsByTag("h1").first().text();
		
		if (title.equals("豆瓣") || title.isEmpty())
			title = "无法爬虫！";
		else	
		{
		title = title.replaceAll("(。。)|(…)|(233)|(33)|(【)|(】)|(\\.)|(～)|(~~)|(哈)|(啊啊)|(卧槽)|(！！)|(!!)|(？？)", "");
		title = title.replaceAll("^[，*|~*|！*|。*|//s*]*", "").replaceAll("[，*|~*|！*|。*|//s*]*$", "");  

		//判断是否有图片
		Element detail = content.getContent().getElementsByClass("topic-doc").first();
		if (!detail.getElementsByClass("topic-richtext").isEmpty())
			detail = detail.getElementsByClass("topic-richtext").first();
		else
			detail = detail.getElementsByClass("topic-content").first();

		if (detail.getElementsByTag("img").isEmpty())
		{}
		else
		{
			int Ltimes = 0;
			for (Element each : detail.getElementsByTag("img"))
			{
				if (Ltimes++ > 10) break;
				p.add(each.attr("src"));	
			}
		}
		

		/////////////////////// 回复 /////////////////////////
		
		if (!content.getContent().getElementsByClass("reply-doc content").isEmpty())
		{
		Elements in = content.getContent().getElementsByClass("reply-doc content");
		HashSet<String> set = new HashSet<String>();
		
		//内容
		//set.add(detail.text());
		for (Element each : in)
		{
			set.add(each.getElementsByTag("p").text());
		}

		Vector<String> v_new = new Vector<String>();
		v_new.addAll(set);
		
		Vector<String> v_del = new Vector<String>();
		for (String x : v_new)
		{
			if (x.contains("？") || x.contains("?") || x.contains("吗") || x.contains("屁事"))
				v_del.add(x);
		}	
		v_new.removeAll(v_del);
		
		Collections.sort(v_new, new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				return o1.length() < o2.length() ? 1 : -1;
			}
		});
		
		int cutCounts = v_new.size() / 3;
		for (int i = 0; i < cutCounts && v_new.size() > 10; i++)
			v_new.removeElementAt(v_new.size() - 1);
		
		v_new.add(0, detail.text());
		for (String x : v_new)
		{
			x = x.replaceAll("(。。)|(…)|(\\.)|(233)|(33)|(～)|(~~)|(哈)|(啊啊)|(溜了)|(QAQ)|(嗯)|(嘎)|(\\*)|(hh)|(【)|(】)|(em)|(mm)|(卧槽)|(哎)|(！！)|(!!)", "");
			x = x.replaceAll("^[，*|~*|！*|。*|//s*]*", "").replaceAll("[，*|~*|！*|。*|（*|//s*]*$", "");  

			if (!x.isEmpty())
				v_Result.add(x);
		}
		
		Collections.sort(v_Result, new Comparator<String>()
		{
			public int compare(String o1, String o2)
			{
				return o1.length() < o2.length() ? 1 : -1;
			}
		});
		
		} // if
		
		//按关键字下载图片
		if (p.isEmpty())
		{
			String KeyStr = ToAnalysis.parse(title).toString();
			String[] pieces = KeyStr.split(",");
			for (String x : pieces)
			{
				if (x.contains("/nr"))
				{
					x = x.replace("/nr", "");
					KeyVec.add(x);
				}
			}
		}
		
		} // if
		
		/////////////////////////////// SHOW /////////////////////////////////
		
		//标题
		TextView titleView = (TextView) this.findViewById(R.id.DoubanTitle);
		titleView.setText(title);
		
		if (title.equals("无法爬虫！"))
			return;
		
		int ids = 100;
		
		//默认关键字
		for (String x : KeyVec)
		{
			RadioButton rb_in = new RadioButton(this);
			
			rb_in.setId(ids++);
			rb_in.setTextSize(22);	
			rb_in.setBackgroundColor(Color.rgb(204,232,207));
			rb_in.setPadding(50, 25, 0, 25);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(50, 0, 50, 15);
			rb_in.setLayoutParams(layoutParams);
			rb_in.setOnClickListener(new OnClickListener()
			{     
	            @Override    
	            public void onClick(View v)
	            {   
	            	int ic = 0;
	            	for (RadioButton r_ii : rb)
	            	{
	            		if (v.getId() != r_ii.getId())
	            		{
	            			r_ii.setChecked(false);
	            			rb.set(ic, r_ii);
	            		}
	            		ic++;
	            	}
	            }    
	        }); 
			
			rb_in.setText(x);			
			rb.add(rb_in);
			
			LinearLayout ll = (LinearLayout) this.findViewById(R.id.DoubanLayout);
			ll.addView(rb.lastElement());
		}
		
		//自定义关键字
		if (p.isEmpty())
		{
			et = new EditText(this);
			et.setId(ids++);
			et.setTextSize(22);	
			et.setBackgroundColor(Color.rgb(204,232,207));
			et.setPadding(50, 25, 50, 25);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(50, 0, 50, 50);
			et.setLayoutParams(layoutParams);
			et.setText("");

			LinearLayout ll = (LinearLayout) this.findViewById(R.id.DoubanLayout);
			ll.addView(et);
		}
		
		//内容
		for (String x : v_Result)
		{
			CheckBox contentBox = new CheckBox(this);
			contentBox.setId(ids++);
			contentBox.setTextSize(22);	
			contentBox.setBackgroundColor(Color.rgb(204,232,207));
			contentBox.setPadding(0, 25, 0, 25);
			LinearLayout.LayoutParams layoutParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
			layoutParams.setMargins(0, 0, 0, 15);
			contentBox.setLayoutParams(layoutParams);
			contentBox.setText(x);			
			cb.add(contentBox);
			
			LinearLayout ll = (LinearLayout) this.findViewById(R.id.DoubanLayout);
			ll.addView(cb.lastElement());
		}
		
	}
	
	
	public void Deal(View view) throws IOException, InterruptedException
	{
		//标题点击
		if (view.getId() == R.id.DoubanTitle)
		{
		    //获取剪贴板管理器：  
		    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);  
		    // 创建普通字符型ClipData  
		    ClipData mClipData = ClipData.newPlainText("Label", title);  
		    // 将ClipData内容放到系统剪贴板里。  
		    cm.setPrimaryClip(mClipData); 
        	Toast.makeText(getApplicationContext(), "已复制！", Toast.LENGTH_LONG).show();  	                

		    
			TextView tv = (TextView) findViewById(view.getId());
			tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
		}

	}	
	
	
	private void listener(final long Id)
	{  
		// 注册广播监听系统的下载完成事件。  
		IntentFilter intentFilter = new IntentFilter(DownloadManager.ACTION_DOWNLOAD_COMPLETE);  
	    BroadcastReceiver broadcastReceiver = new BroadcastReceiver() 
	    {  
	        @Override  
	        public void onReceive(Context context, Intent intent) 
	        {  
	            long ID = intent.getLongExtra(DownloadManager.EXTRA_DOWNLOAD_ID, -1);  
	            if (ID == Id) 
	            {  
	            	String progress = String.valueOf(++downloadCount) + "/" + String.valueOf(p.size());
	            	
	            	if ((jumpCount++ % 2 == 0) || (downloadCount == p.size()))
	            		Toast.makeText(getApplicationContext(), "已完成：" + progress, Toast.LENGTH_LONG).show();  	                
	            	
	    			//修剪图片	        
	    			int beg = m_ID_URL.get(Id).lastIndexOf("/") + 1;
	    			String rootPathSD = Environment.getExternalStorageDirectory().getPath() + "/Download/";	    			 
	            	File fileD = new File(rootPathSD + m_ID_URL.get(Id).substring(beg));
	            	
	            	for (int loopT = 0; loopT < 10; loopT++)
	            	{
	            		if (fileD.exists()) break;
	            		try
	            		{
	            			Thread.sleep(500);
	            		} catch (InterruptedException e1)
	            		{
	            			// TODO 自动生成的 catch 块
	            			e1.printStackTrace();
	            		}
	            	}
	            	
	    			if (BitmapFactory.decodeFile(rootPathSD + m_ID_URL.get(Id).substring(beg)) != null)
	    			{
	    			//tv.setTextColor(Color.rgb(46, 139, 87));
	    			Bitmap bm = BitmapFactory.decodeFile(rootPathSD + m_ID_URL.get(Id).substring(beg));
	    			int width = bm.getWidth();
	    			int height = bm.getHeight() - 48;
	    			Bitmap newPic = Bitmap.createBitmap(bm, 0, 0, width, height);

	    			File file2 = new File(rootPathSD + m_ID_URL.get(Id).substring(beg));
	    			if(file2.exists())
	    				file2.delete();
	                
	                try
	                {
	               	 	file2.createNewFile();
	                    FileOutputStream fos = new FileOutputStream(file2);
	                    newPic.compress(Bitmap.CompressFormat.JPEG, 100, fos);
	                    fos.flush();
	                    fos.close();
	                } catch (IOException e)
	                {
	                    e.printStackTrace();
	                }
	    			}
	    			
	            }  
	        }  
	    };  
	  
	    registerReceiver(broadcastReceiver, intentFilter);  
	} 
	
	
	public static void clean()
	{
		String rootPathSD = Environment.getExternalStorageDirectory().getPath() + "/Download/";
		for (String p_url : p)
		{
			int beg = p_url.lastIndexOf("/") + 1;
			File file = new File(rootPathSD + p_url.substring(beg));
			if (file.exists()) 
				file.delete(); 
		}
		

		p.clear();
		m_ID_URL.clear();
		cb.clear();
		rb.clear();
		KeyVec.clear();
		downloadCount = 0;
		jumpCount = 0;
		Menu_key_Which = 0;
	}
	
	
    public void onBackPressed() 
    {    
        super.onBackPressed();    
        clean();         
    }    
	
	protected void finalize()
	{
		clean();
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu)
	{
		// Inflate the menu; this adds items to the action bar if it is present.
		getMenuInflater().inflate(R.menu.douban, menu);
		return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item)
	{
		if (title.equals("无法爬虫！"))
		{
			Toast.makeText(getApplicationContext(), "无法爬虫！", Toast.LENGTH_LONG).show();
			return true;
		}
			
		int id = item.getItemId();

		if (id == R.id.confirm)
		{
			boolean isSelect = false;
			for (CheckBox cb_in: cb)
			{
				if (cb_in.isChecked())
				{
					isSelect = true;
					c += cb_in.getText().toString() + "。\n";
				}
			}
			
			if (!isSelect)
			{
		        Toast.makeText(getApplicationContext(), "未选择内容！", Toast.LENGTH_LONG).show(); 
		        return true;
			}
			
			//处理关键字链接
			if (p.isEmpty())
			{
				//选择关键字
				String KeyCur = "";
				if(!et.getText().toString().isEmpty())
					KeyCur = et.getText().toString();
				else if (!rb.isEmpty())
				{
					boolean found = false;
					for (RadioButton x : rb)
					{
						if (x.isChecked())
						{
							KeyCur = x.getText().toString();
							found = true;
							break;
						}
					}
					
					if (!found)
					{
				        Toast.makeText(getApplicationContext(), "未选择关键字！", Toast.LENGTH_LONG).show(); 
				        return true;
					}
				}
				else
				{
					Toast.makeText(getApplicationContext(), "未选择关键字！", Toast.LENGTH_LONG).show(); 
				    return true;
				}
					
					
				String keyURL = "https://cn.bing.com/images/async?async=content&q=" + KeyCur + "&first=118&count=10";
				
				getWeb content = new getWeb(keyURL);
				content.start();
				try { content.join(); } catch (InterruptedException e) { e.printStackTrace(); }
				
				String keyStr = content.getContent().toString();
				while (keyStr.contains("murl&quot;:&quot;"))
				{
					int beg = keyStr.indexOf("murl&quot;:&quot;") + 17;
					keyStr = keyStr.substring(beg);
					int end = keyStr.indexOf("&quot;");
					String link = keyStr.substring(0, end);
					keyStr = keyStr.substring(end + 6);
					p.add(link);
				}
			}

			//内容点击
			ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);  
			ClipData mClipData = ClipData.newPlainText("Label", c);   
			cm.setPrimaryClip(mClipData); 
	        Toast.makeText(getApplicationContext(), "已复制！", Toast.LENGTH_LONG).show();  	                
				
			if (p.isEmpty())
				return true;
			
			for (String p_url : p)
			{
				int beg = p_url.lastIndexOf("/") + 1;
				String rootPathSD = Environment.getExternalStorageDirectory().getPath() + "/Download/";
				File file = new File(rootPathSD + p_url.substring(beg));
				String FileName = p_url.substring(beg);
				
				if (file.exists())
					break;
				
				DownloadManager.Request request = new DownloadManager.Request(Uri.parse(p_url));
				
				//设置在什么网络情况下进行下载
				request.setAllowedNetworkTypes(Request.NETWORK_WIFI);
				//设置通知栏标题
				request.setNotificationVisibility(Request.VISIBILITY_VISIBLE);
				request.setTitle("下载");
				request.setDescription("正在下载");
				request.setAllowedOverRoaming(true);
					   
				//设置文件存放目录
				request.setDestinationInExternalPublicDir("Download", FileName);
				DownloadManager downManager = (DownloadManager)getSystemService(Context.DOWNLOAD_SERVICE);
				long downloadID = downManager.enqueue(request);
				m_ID_URL.put(downloadID, p_url);
				try
				{
					Thread.sleep(500);
				} catch (InterruptedException e)
				{
					// TODO 自动生成的 catch 块
					e.printStackTrace();
				}
				listener(downloadID);		
			}
 
			return true;
		}
		else if (id == R.id.check_repeat)
		{	
			//获取关键字
			String KeyStr = ToAnalysis.parse(title).toString();
			String[] pieces = KeyStr.split(",");
			Vector<String> vsK = new Vector<String>();
			for (String x : pieces)
			{
				if (x.contains("/nr"))
				{
					x = x.replace("/nr", "");
					vsK.add(x);
				}
			}
			final String[] vsKA = vsK.toArray(new String[vsK.size()]);
			
			Builder checkDialog = new AlertDialog.Builder(this);
			checkDialog.setTitle(title).setIcon(android.R.drawable.ic_dialog_info);
			
			if (vsKA.length != 0)
			{
				checkDialog.setSingleChoiceItems(vsKA, 0, new DialogInterface.OnClickListener() 
				{
				 public void onClick(DialogInterface dialog, int which)
				 {
					 Menu_key_Which = which;
					 //dialog.dismiss();
				 }
				});
			}
			
			final EditText keyET = new EditText(this);
			keyET.setId(500);
	
			checkDialog.setView(keyET)
			 .setPositiveButton("确定",new DialogInterface.OnClickListener()
			 {
				 @Override
				 public void onClick(DialogInterface dialog, int which)
				 {					
					String finalKey = "";
					
					if (!keyET.getText().toString().isEmpty())
						finalKey = keyET.getText().toString();
					else if (vsKA.length == 0)
					{
				        Toast.makeText(DoubanActivity.this, "关键字不能为空！", Toast.LENGTH_LONG).show();  	                
						return;
					}
					else
						finalKey = vsKA[Menu_key_Which];
					
					showRepeat(finalKey);
				 }
			 })
			 .setNegativeButton("取消", null).show();
			
			 return true;
		}

		return super.onOptionsItemSelected(item);
	}
	
	private void showRepeat(String sr)
	{
		sr = sr.trim();
		String urlK = "https://www.toutiao.com/search_content/?offset=0&format=json&keyword=" + sr + "&autoload=true&count=20&cur_tab=1&from=search_tab";
		getWeb content = new getWeb(urlK);
		content.start();
		try { content.join(); } catch (InterruptedException e) { e.printStackTrace(); }
		
		String ss = content.getContent().body().text().toString();
		String endStr = StrToJson(ss);

		new AlertDialog.Builder(this).setMessage(endStr).show();
	}

	public static String url;
	private String c = "";
	private String title = "";
	private static Vector<String> p = new Vector<String>();
	private static Map<Long, String> m_ID_URL = new HashMap<Long, String>();
	private static Vector<CheckBox> cb = new Vector<CheckBox>();
	private static Vector<RadioButton> rb = new Vector<RadioButton>();
	private static EditText et;
	private static Vector<String> KeyVec = new Vector<String>();
	private static int downloadCount = 0;
	private static int jumpCount = 0;
	private static int Menu_key_Which = 0;
}
