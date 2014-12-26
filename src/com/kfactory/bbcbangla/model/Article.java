package com.kfactory.bbcbangla.model;

public class Article {
	
	private String title, url, thumbnail, pubDate, content;
	
	public Article(){}
	
	public Article(String title, String thumbnail, String url, String pubDate, String content){
		
		this.content = content;
		this.pubDate = pubDate;
		this.thumbnail = thumbnail;
		this.title = title;
		this.url = url;
		
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public String getUrl() {
		return url;
	}

	public void setUrl(String url) {
		this.url = url;
	}

	public String getThumbnail() {
		return thumbnail;
	}

	public void setThumbnail(String thumbnail) {
		this.thumbnail = thumbnail;
	}

	public String getPubDate() {
		return pubDate;
	}

	public void setPubDate(String pubDate) {
		this.pubDate = pubDate;
	}

	public String getContent() {
		return content;
	}

	public void setContent(String content) {
		this.content = content;
	}

}
