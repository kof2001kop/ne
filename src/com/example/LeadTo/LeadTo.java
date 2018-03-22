package com.example.LeadTo;
import com.example.show.AppleActivity;
import com.example.show.DoubanActivity;

import android.content.Context;
import android.content.Intent;

public class LeadTo
{
	public LeadTo(String where, Context here_this, String url)
	{
		if (where == "Apple")
		{
			Intent intent = new Intent(here_this, AppleActivity.class);
			AppleActivity.url = url;
			here_this.startActivity(intent);//打开新的activity
		}
		else if (where == "Douban")
		{
			Intent intent = new Intent(here_this, DoubanActivity.class);
			DoubanActivity.url = url;
			here_this.startActivity(intent);//打开新的activity			
		}
			
	}
}
