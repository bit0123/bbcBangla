package com.kfactory.bbcbangla.adaptor;

import java.util.List;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import com.kfactory.bbcbangla.R;
import com.kfactory.bbcbangla.model.Article;
import com.squareup.picasso.Picasso;

public class CustomListAdapter extends BaseAdapter{

	private Activity activity;
	private List<Article> articles;
	private LayoutInflater inflater;
	
	private ImageView thumbnail;
	private TextView title;	
	
	public CustomListAdapter(Activity activity, List<Article> articles) {
		this.activity = activity;
		this.articles = articles;		
	}
	
	@Override
	public int getCount() {
		// TODO Auto-generated method stub
		return articles.size();
	}
	@Override
	public Object getItem(int position) {
		// TODO Auto-generated method stub
		return articles.get(position);
	}
	@Override
	public View getView(int position, View convertView, ViewGroup parent) {
		if (inflater == null)
			inflater = (LayoutInflater) activity
					.getSystemService(Context.LAYOUT_INFLATER_SERVICE);
		if (convertView == null)
			convertView = inflater.inflate(R.layout.list_article, parent, false);
		
		thumbnail = (ImageView) convertView.findViewById(R.id.thumbnail);
		title = (TextView) convertView.findViewById(R.id.title);
			
		Article article = articles.get(position);
		Picasso.with(activity).load(article.getThumbnail()).placeholder(R.drawable.bbc_bangla).error(R.drawable.bbc_bangla).into(thumbnail);
		title.setText(article.getTitle());
		
		return convertView;
	}

	@Override
	public long getItemId(int position) {
		// TODO Auto-generated method stub
		return position;
	}
	
	public List<Article> getArticles() {
		return articles;
	}
	
}
