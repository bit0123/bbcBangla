package com.kfactory.bbcbangla;

import java.io.IOException;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.Activity;
import android.app.AlertDialog;
import android.app.ProgressDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.AsyncTask;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.webkit.WebView;
import android.webkit.WebViewClient;


public class DetailActivity extends Activity {
	
	private WebView mWebView;
	private IConnectivityManager connectionManager;
	private String url, title;

	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_detail); 
		
		mWebView = (WebView) findViewById(R.id.webView);
		mWebView.setWebViewClient(new CustomWebClient());
		mWebView.getSettings().setJavaScriptEnabled(true);
				
		connectionManager = new IConnectivityManager(DetailActivity.this);
		url = getIntent().getExtras().get("url").toString();
		title = getIntent().getExtras().get("title").toString();
		
		if(connectionManager.isConnected()){
			new ExcuteNetworkOperation().execute(url);
		}
		else{
			new AlertDialog.Builder(this)
		    .setTitle("Retry")
		    .setMessage("Are you sure you want to retry?")
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	Intent intent = getIntent();
		            overridePendingTransition(0, 0);
		            intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
		            finish();
		            overridePendingTransition(0, 0);
		            startActivity(intent);
		        }
		     })
		    .setNegativeButton(android.R.string.no, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	moveTaskToBack(true);
                    android.os.Process.killProcess(android.os.Process.myPid());
                    System.exit(1);
		       
		        }
		     })
		    .setIcon(android.R.drawable.ic_dialog_alert)
		     .show();
		}
	}
	
	@Override
	public boolean onCreateOptionsMenu(Menu menu) {
		// TODO Auto-generated method stub
		MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        menu.findItem(R.id.refresh).setVisible(false);
        menu.findItem(R.id.share).setVisible(true);
        return true;
	}
	
	@Override
	public boolean onOptionsItemSelected(MenuItem item) {
		// TODO Auto-generated method stub
		if(item.getItemId() == R.id.share){
			shareIt();
			//Toast.makeText(DetailActivity.this, "Share Button Clicked", Toast.LENGTH_LONG).show();
        }
        return true;
	}

	private void shareIt() {
		Intent sharingIntent = new Intent(android.content.Intent.ACTION_SEND);
		sharingIntent.setType("text/plain");
		sharingIntent.putExtra(android.content.Intent.EXTRA_SUBJECT, "BBC Bangla");
		sharingIntent.putExtra(android.content.Intent.EXTRA_TEXT, title+"\n"+url);
		startActivity(Intent.createChooser(sharingIntent, "Share via"));
	}
	
	public class CustomWebClient extends WebViewClient {

		@Override
		public boolean shouldOverrideUrlLoading(WebView view, String url) {
			// TODO Auto-generated method stub
			view.loadUrl(url);
			return true;
		}
	}

	public class ExcuteNetworkOperation extends AsyncTask<String, Void, String> {

		private ProgressDialog dialog;
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			dialog = new ProgressDialog(DetailActivity.this);
			dialog.setMessage("Loading...");
			dialog.setCancelable(false);
			dialog.setCanceledOnTouchOutside(false);
			dialog.show();
			
		}
		@Override
		protected String doInBackground(String... url) {
			Document doc = null;
			String data = "";
	        try {
	        	doc = Jsoup.connect(url[0]).maxBodySize(0).timeout(0).get();
	            
	            Elements elements = doc.getElementsByClass("story");
	            elements.select("a[class=share__back-to-top ghost-column]").remove();
	            elements.select("a[class=share__button]").remove();
	            elements.select("div[class=small-promo-group]").remove();
	            elements.select("div[class=share   show ghost-column]").remove();
	            elements.select("img").attr("width", "100%");
	            elements.select("img").attr("height", "100%");
//	            elements.select("img").removeAttr("width");
//	            elements.select("img").removeAttr("height");

	            elements.attr("text-align", "justify");
	            for(Element element : elements) {
	                data += element.html();
	                data += "<br/>";
	            }
	            return data;
	            
	        }
	        catch (IOException e) {
	            e.printStackTrace();
	        }
	       
	        return null;
		}
		
		@Override
		protected void onPostExecute(String result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);			
			mWebView.loadDataWithBaseURL("", result, "text/html", "UTF-8", "");
			dialog.dismiss();
		}

	}
}
