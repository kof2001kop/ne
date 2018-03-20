package com.example.show;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.HashMap;
import java.util.Map;
import java.util.Vector;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import com.example.getWeb.getWeb;
import com.spreada.utils.chinese.ZHConverter;

import android.app.Activity;
import android.app.DownloadManager;
import android.app.DownloadManager.Request;
import android.content.BroadcastReceiver;
import android.content.ClipData;
import android.content.ClipboardManager;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Typeface;
import android.net.Uri;
import android.os.Bundle;
import android.os.Environment;
import android.view.View;
import android.widget.TextView;
import android.widget.Toast;


public class Apple_Activity extends Activity
{
	@Override
	protected void onCreate(Bundle savedInstanceState)
	{
		clean();
		
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_apple);
		
		getWeb content = new getWeb(url);
		content.start();
		try { content.join(); } catch (InterruptedException e) { e.printStackTrace(); }
		
		//繁体转简
		ZHConverter converter = ZHConverter.getInstance(ZHConverter.SIMPLIFIED);
		
		title = content.getContent().getElementsByTag("title").text().replace(" | 蘋果日報", "");
		
		if(null!=title && !"".equals(title))
			title = title.replaceAll("^[　*| *| *|//s*]*", "").replaceAll("[　*| *| *|//s*]*$", "");  
		
		title = title.replaceAll("(　)|(】)", "，");		
		title = title.replaceAll("(【)|(「)|(」)|(爆乳)|(獨家)|(台灣)|(中國)", "");		
		title = title.replace("台視", "電視");
		title = title.replace("屌", "厉害");		
		title = converter.convert(title);
		
		Elements pTag = content.getContent().getElementsByClass("ndArticle_contentBox").first().getElementsByClass("ndArticle_margin");
		
		c = pTag.first().getElementsByTag("p").first().toString();
		c = c.replace("<br>", "@#");
		c = c.replace("『", "“");
		c = c.replace("』", "”");		
		c = c.replace("</p>", "");	
		c = c.replace("報導）", "報導）</p>");
		c = c.replace("報導)", "報導）</p>");	
		
		Document cd = Jsoup.parse(c);
		c = cd.getElementsByTag("p").text();
		c = c.replace("@#", "\n");
		c = c.replaceAll("[\\(|（](.*)報導）", "");
		c = c.replaceAll("[\\(|（](.*)新聞[\\)|）]", "");
		c = c.replaceAll("[\\(|（]新增(.*)[\\)|）]", "");	
		c = c.replace("大陸", "内地");
		c = c.replaceAll("(「)|(」)|(陸星)|(爆乳)|(台灣)|(《蘋果》)|(中國)|(臉書)|(紛紛)", "");
		c = c.replace("台視", "電視");
		c = c.replace("屌", "厉害");	
		c = c.replace("國中", "中學");			
		c = c.trim();
		c = converter.convert(c);
		
		//图
		if (!content.getContent().getElementsByClass("ndAritcle_headPic").isEmpty())
		{
			String headPic = content.getContent().getElementsByClass("ndAritcle_headPic").first().getElementsByTag("img").attr("src").toString();		
			p.add(headPic);
		}
		
		Elements pic = pTag.first().getElementsByTag("figure");
		if (!pic.isEmpty())
		{
			for (Element link : pic) 
			{
				p.add(link.getElementsByTag("img").attr("src").toString());
			}
		}
		
		/////////////////////////////// SHOW /////////////////////////////////

		TextView titleView = (TextView) this.findViewById(R.id.AppleTitle);
		titleView.setText(title);
		
		TextView contentView = (TextView) this.findViewById(R.id.AppleContent);
		contentView.setText(c);	
	}
	
	
	public void Deal(View view) throws IOException, InterruptedException
	{
		//标题点击
		if (view.getId() == R.id.AppleTitle)
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
		//内容点击
		else if (view.getId() == R.id.AppleContent)
		{  
		    ClipboardManager cm = (ClipboardManager) getSystemService(Context.CLIPBOARD_SERVICE);  
		    ClipData mClipData = ClipData.newPlainText("Label", c);   
		    cm.setPrimaryClip(mClipData); 
        	Toast.makeText(getApplicationContext(), "已复制！", Toast.LENGTH_LONG).show();  	                

			TextView tv = (TextView) findViewById(view.getId());
			tv.setTypeface(Typeface.defaultFromStyle(Typeface.BOLD));
			
			if (p.isEmpty())
				return;
		
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
			Thread.sleep(500);
			listener(downloadID);		
			}

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
		downloadCount = 0;
		jumpCount = 0;
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
	
	
	public static String url;
	private String c;
	private String title;
	private static Vector<String> p = new Vector<String>();
	private static Map<Long, String> m_ID_URL = new HashMap<Long, String>();
	private static int downloadCount = 0;
	private static int jumpCount = 0;	
}
