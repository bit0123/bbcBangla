package com.kfactory.bbcbangla;


import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;

import android.app.AlertDialog;
import android.app.ListActivity;
import android.app.ProgressDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.AsyncTask;
import android.os.Bundle;
import android.util.SparseBooleanArray;
import android.view.ActionMode;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.widget.AbsListView.MultiChoiceModeListener;
import android.widget.AdapterView;
import android.widget.AdapterView.OnItemClickListener;
import android.widget.ListView;
import android.widget.Toast;

import com.kfactory.bbcbangla.adaptor.CustomListAdapter;
import com.kfactory.bbcbangla.model.Article;

public class MainListActivity extends ListActivity {

	private static final String url = "http://www.bbc.co.uk/bengali/news/index.xml";

	private SharedPreferences sp;
	private CustomListAdapter customAdapter;
	private List<Article> articles;
	private ListView listView;
	private Boolean flag;
	
	private void restartApplication(){
		Intent intent = getIntent();
        overridePendingTransition(0, 0);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_ANIMATION);
        finish();
        overridePendingTransition(0, 0);
        startActivity(intent);
	}
	
	
	@Override
	public void onCreate(Bundle savedInstanceState) {
		super.onCreate(savedInstanceState);
		setContentView(R.layout.activity_main);
		//getActionBar().setBackgroundDrawable(new ColorDrawable(Color.RED)); 
		flag = false;
		articles = new ArrayList<Article>();
		customAdapter = new CustomListAdapter(MainListActivity.this, articles);
		sp = this.getPreferences(Context.MODE_PRIVATE);

//		if(sp.getInt("pocketCount", 0) == 0){
//			Toast.makeText(MainListActivity.this, "PocketCount: 0", Toast.LENGTH_LONG).show();
//		}else{
//			Toast.makeText(MainListActivity.this, "PocketCount:"+sp.getInt("pocketCount", -1), Toast.LENGTH_LONG).show();
//		}
				
		if(new IConnectivityManager(MainListActivity.this).isConnected()){
			new RequestTask().execute(url);
		}
		else{
			new AlertDialog.Builder(this)
		    .setTitle("Retry")
		    .setMessage("Are you sure you want to retry?")
		    .setPositiveButton(android.R.string.yes, new DialogInterface.OnClickListener() {
		        public void onClick(DialogInterface dialog, int which) { 
		        	restartApplication();
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
		    .setCancelable(false)
		    .show();
		}
		
		listView = (ListView) this.findViewById(android.R.id.list);
				
		listView.setChoiceMode(ListView.CHOICE_MODE_MULTIPLE_MODAL);
		listView.setMultiChoiceModeListener(new MultiChoiceModeListener() {

		    @Override
		    public void onItemCheckedStateChanged(ActionMode mode, int position,
		                                          long id, boolean checked) {
		    	// Capture total checked items
				final int checkedCount = listView.getCheckedItemCount();
				// Set the CAB title according to total checked items
				mode.setTitle(checkedCount + " Article Selected");
				// Calls toggleSelection method from ListViewAdapter Class
				//customAdapter.toggleSelection(position);
		    }

		    @Override
		    public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
		        // Respond to clicks on the actions in the CAB
		        switch (item.getItemId()) {
		            case R.id.pocket:
		            	SharedPreferences.Editor editor = sp.edit();
		            	int currentIndex = sp.getInt("pocketCount", 0);		        					        			
		        				        		
		        		SparseBooleanArray checkedItems = listView.getCheckedItemPositions();
		        		if(flag){
		        			editor.clear();
		        			//editor.commit();
		        			currentIndex = 0;
		        		}
		        		if(flag == false){
		        			for(int i=0; i<checkedItems.size(); i++){
		        				if (checkedItems.valueAt(i) && !checkExistanceByTitle(((Article)customAdapter.getItem(checkedItems.keyAt(i))).getTitle())) {
		        						        					
		        					editor.putString("url"+currentIndex, ((Article)customAdapter.getItem(checkedItems.keyAt(i))).getUrl());
		        					editor.putString("title"+currentIndex, ((Article)customAdapter.getItem(checkedItems.keyAt(i))).getTitle());
		        					editor.putString("thumbnail"+currentIndex, ((Article)customAdapter.getItem(checkedItems.keyAt(i))).getThumbnail());
		        					currentIndex++;
		        				}	        				
		        			}
	        			}else if(flag){
	        				for(int i=checkedItems.size()-1; i>=0; i--)
	        				if(checkedItems.valueAt(i)){
	        					articles.remove(checkedItems.keyAt(i));
	        				}
	        				for(int i=0; i<articles.size();i++){
	        					editor.putString("url"+currentIndex, articles.get(i).getUrl());
	        					editor.putString("title"+currentIndex, articles.get(i).getTitle());
	        					editor.putString("thumbnail"+currentIndex, articles.get(i).getThumbnail());
	        					currentIndex++;
	        				}	        				
	        			}
	        			editor.putInt("pocketCount", currentIndex);
	        			editor.commit();
	        			invalidateOptionsMenu();
	        			if(flag)Toast.makeText(MainListActivity.this, "Succesfully deleted.", Toast.LENGTH_LONG).show();
	        			else Toast.makeText(MainListActivity.this, "Succesfully added to pocket.", Toast.LENGTH_LONG).show();
	        			if(flag)listView.invalidateViews();
	        			mode.finish();
	        			if(articles.size() == 0)restartApplication();
		                return true;
		            default:
		                return false;
		        }
		    }

		    @Override
		    public boolean onCreateActionMode(ActionMode mode, Menu menu) {
		        // Inflate the menu for the CAB
		        MenuInflater inflater = mode.getMenuInflater();
		        inflater.inflate(R.menu.options_menu, menu);
		        if(flag == true){
		        	menu.findItem(R.id.pocket).setIcon(android.R.drawable.ic_delete);
		        	menu.findItem(R.id.pocket).setTitle("Delete");
		        }		        	
		        menu.findItem(R.id.pocket).setVisible(true);
		        menu.findItem(R.id.refresh).setVisible(false);
		        return true;
		    }

		    @Override
		    public void onDestroyActionMode(ActionMode mode) {
		        // Here you can make any necessary updates to the activity when
		        // the CAB is removed. By default, selected items are deselected/unchecked.
		  
		    }

		    @Override
		    public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
		        // Here you can perform updates to the CAB due to
		        // an invalidate() request
		        return false;
		    }
		});
		listView.setOnItemClickListener(new OnItemClickListener() {

			@Override
			public void onItemClick(AdapterView<?> av, View v, int position,
					long id) {

				String urltoload = ((Article)((CustomListAdapter)getListAdapter()).getItem(position)).getUrl();
				String title = ((Article)((CustomListAdapter)getListAdapter()).getItem(position)).getTitle();
				Intent i = new Intent(MainListActivity.this, DetailActivity.class);
				i.putExtra("url", urltoload);
				i.putExtra("title", title);
				startActivity(i);
				
			}
		});
				
	}
	
    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        MenuInflater inflater = getMenuInflater();
        inflater.inflate(R.menu.options_menu, menu);
        
        if(sp.getInt("pocketCount", 0) != 0){
        	MenuItem item = menu.findItem(R.id.pocket);
        	item.setIcon(android.R.drawable.ic_input_get);
        	item.setTitle(""+"("+sp.getInt("pocketCount", 0)+") Article in Pocket");
        	item.setVisible(true);
        }
        
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        if(item.getItemId() == R.id.refresh){
        	restartApplication();
        	
        }
        if(item.getItemId() == R.id.pocket){
        	flag = true;
        	new ReadDBRequest().execute();
        }
        item.setChecked(true);
    	item.setEnabled(true);
        return true;
    }
    
    @Override
    public void onBackPressed() {
    	// TODO Auto-generated method stub
    	if(flag == true){
    		restartApplication();
    	}else{
    		MainListActivity.super.onBackPressed();
    	}
    }
    
    private Boolean checkExistanceByTitle(String title){
    	Boolean isExist = false; 
    	    	
    	int size = sp.getInt("pocketCount", 0);  
	    for(int i=0;i<size;i++){
	    		if(sp.getString("title"+ i, "").equals(title))
	    			isExist = true;
	    	}
	    return isExist;
    }
    
    class ReadDBRequest extends AsyncTask<Void, Void, Void>{
    	private ProgressDialog pDialog;
    	
    	@Override
    	protected void onPreExecute() {
    		// TODO Auto-generated method stub
    		super.onPreExecute();
    		pDialog = new ProgressDialog(MainListActivity.this);
    		pDialog.setMessage("Loading...");
    		pDialog.setCancelable(false);
    		pDialog.setCanceledOnTouchOutside(false);
    		pDialog.show();
    	}
		@Override
		protected Void doInBackground(Void... params) {
			// TODO Auto-generated method stub 
		    int size = sp.getInt("pocketCount", 0);  
		    articles.clear();
		    for(int i=0;i<size;i++){
		    	Article article = new Article();
		    	article.setTitle(sp.getString("title"+ i, ""));
				article.setThumbnail(sp.getString("thumbnail"+ i, ""));
				article.setContent("");
				article.setPubDate("");
				article.setUrl(sp.getString("url"+ i, ""));
//				Log.d(TAG, sp.getString("title"+ i, ""));
				articles.add(article);
		    }

			return null;
		}
		
		@Override
		protected void onPostExecute(Void result) {
			// TODO Auto-generated method stub
			super.onPostExecute(result);
			pDialog.dismiss();
		    //customAdapter.notifyDataSetChanged();
		    listView.invalidateViews();
		}
    	
    }
    
	class RequestTask extends AsyncTask<String, String, Void>{
		private ProgressDialog pDialog;
		
		@Override
		protected void onPreExecute() {
			// TODO Auto-generated method stub
			super.onPreExecute();
			pDialog = new ProgressDialog(MainListActivity.this);
			pDialog.setMessage("Loading...");
			pDialog.setCancelable(false);
			pDialog.setCanceledOnTouchOutside(false);
			pDialog.show();
		}
		
	    @Override
	    protected Void doInBackground(String... uri) {
			Document doc = null;
			String baseURL = "http://www.bbc.co.uk";
			try {
				doc = Jsoup.connect(uri[0]).maxBodySize(0).timeout(0).get();

		        Elements elements = doc.select("entry");
				
				for(Element element : elements) {

				    Article article = new Article();
					
				    article.setTitle(element.select("title").text());
					article.setThumbnail(element.select("img").get(0).attr("src").toString());
					article.setContent(element.select("summary").text());
					article.setPubDate(element.select("published").text());
					article.setUrl(baseURL+element.select("dc|identifier").text());
					
					articles.add(article);
					
//				    Log.d(TAG, "thumb link: "+element.select("img").get(0).attr("src").toString());
				}
				
			} catch (IOException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
			}
			return null;
	    }

	    @Override
	    protected void onPostExecute(Void result) {
	        super.onPostExecute(result);
	        //Do anything with response..
	        setListAdapter(customAdapter);
	        pDialog.dismiss();
	    }
	}

}