package com.rwm.pojo;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import com.rwm.customexceptions.CustomException;

public class SearchPojo {

	private String source;
	private String destination;
	private LocalDateTime date;
	
	
	
	public SearchPojo() {
		// TODO Auto-generated constructor stub
	}

//	public SearchPojo(String src , String dst , LocalDateTime dt) {
//		this.source = src;
//		this.destination = dst;
//		this.date = dt;
//	}

	public SearchPojo(String src , String dst , String dt) throws CustomException {
		this.source = src;
		this.destination = dst;
		try {
			this.date = LocalDate.parse(dt, DateTimeFormatter.ISO_LOCAL_DATE).atStartOfDay();
		}catch(Exception e) {
			throw new CustomException("Please enter a valid date");
		}
	}

	public String getSource() {
		return source;
	}

	public void setSource(String source) {
		this.source = source;
	}

	public String getDestination() {
		return destination;
	}

	public void setDestination(String destination) {
		this.destination = destination;
	}

	public LocalDateTime getDate() {
		return date;
	}

	public void setDate(LocalDateTime date) {
		this.date = date;
	}

	
}
