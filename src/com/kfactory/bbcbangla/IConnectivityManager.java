package com.kfactory.bbcbangla;

import android.app.Activity;
import android.content.Context;
import android.net.ConnectivityManager;
import android.net.NetworkInfo;

public class IConnectivityManager {

	private Activity activity;
	
	public IConnectivityManager(Activity activity){
		this.activity = activity;
	}
	
	public Boolean isConnected(){
		Boolean connectionStatus = false;
		
		ConnectivityManager connectivityManager = (ConnectivityManager) activity.getSystemService(Context.CONNECTIVITY_SERVICE);
		
		if(connectivityManager != null){
			NetworkInfo netInfo  = connectivityManager.getActiveNetworkInfo();
			if(netInfo != null && netInfo.isConnected())connectionStatus = true;
		}
		
		return connectionStatus;
	}
	
}
