package com.rwm.pojo;


import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneId;
import java.time.ZoneOffset;

public class StationPojo{

	private static final long serialVersionUID = 1L;

	private int stationid;
	
	private int order;
	
	private LocalDateTime arrivaltime;
	
	private LocalDateTime departuretime;
	
	private String stationname;
	
	
	
	public String getStationname() {
		return stationname;
	}



	public void setStationname(String stationname) {
		this.stationname = stationname;
	}



	public int getStationid() {
		return stationid;
	}



	public void setStationid(int stationid) {
		this.stationid = stationid;
	}



	public int getOrder() {
		return order;
	}



	public void setOrder(int order) {
		this.order = order;
	}







	public LocalDateTime getArrivaltime() {
		return arrivaltime;
	}



	public void setArrivaltime(LocalDateTime arrivaltime) {
		this.arrivaltime = arrivaltime;
	}



	public LocalDateTime getDeparturetime() {
		return departuretime;
	}



	public void setDeparturetime(LocalDateTime departuretime) {
		this.departuretime = departuretime;
	}



	public static long getSerialversionuid() {
		return serialVersionUID;
	}



	public StationPojo() {
		// TODO Auto-generated constructor stub
	}



	public StationPojo(int sid, int order, long arrtime, long depttime) {
		this.stationid = sid;
		this.order = order;
		this.arrivaltime = arrtime == 0 ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(arrtime), ZoneOffset.UTC);
		this.departuretime = depttime == 0 ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(depttime), ZoneOffset.UTC);
	}



	public StationPojo(int sid, int order, long arrtime, long depttime, String sname) {
		this.stationid = sid;
		this.order = order;
		this.arrivaltime = arrtime == 0 ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(arrtime), ZoneOffset.UTC);
		this.departuretime = depttime == 0 ? null : LocalDateTime.ofInstant(Instant.ofEpochMilli(depttime), ZoneOffset.UTC);
		this.stationname = sname;
	}

}
