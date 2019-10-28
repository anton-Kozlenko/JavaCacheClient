package com.hit.gui;

import java.io.Serializable;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.Locale;

public class BookPojo implements Serializable{
	
	private static final long serialVersionUID = -1856138546620350844L;
	private String author;
	private String publisher;
	private String title;
	private Date date; // should be used properly: dd/mm/yyyy
	
	public BookPojo(String author, String publisher, String title, String date){
		
		this.author = author;
		this.publisher = publisher;
		this.title = title;
		try {
			this.date = new SimpleDateFormat("EEE MMM dd HH:mm:ss z yyyy", Locale.US).parse(date);
		} catch (ParseException e) {
			e.printStackTrace();
		} 
	}

	@Override
	public String toString() {
		return "Book [author=" + author + ", publisher=" + publisher + ", title=" + title + ", date=" + date + "]";
	}

	public String getAuthor() {
		return author;
	}

	public void setAuthor(String author) {
		this.author = author;
	}

	public String getPublisher() {
		return publisher;
	}

	public void setPublisher(String publisher) {
		this.publisher = publisher;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public Date getDate() {
		return date;
	}

	public void setDate(Date date) {
		this.date = date;
	}

}
