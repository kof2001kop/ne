package com.example.LeadTo;
import com.example.show.Apple_Activity;
import com.example.show.Douban_Activity;

import android.content.Context;
import android.content.Intent;

public class LeadTo
{
	public LeadTo(String where, Context here_this, String url)
	{
		if (where == "Apple")
		{
			Intent intent = new Intent(here_this, Apple_Activity.class);
			Apple_Activity.url = url;
			here_this.startActivity(intent);//打开新的activity
		}
		else if (where == "Douban")
		{
			Intent intent = new Intent(here_this, Douban_Activity.class);
			Douban_Activity.url = url;
			here_this.startActivity(intent);//打开新的activity			
		}
			
	}
}
