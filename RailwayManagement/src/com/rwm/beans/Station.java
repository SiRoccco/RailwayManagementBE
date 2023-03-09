package com.rwm.beans;

public class Station {

	private int StationID;
	private String StationName;
	private int posx;
	private int posy;
	
	public Station() {
		// TODO Auto-generated constructor stub
	}
	
	public Station(int sid , String sname , int x , int y) {
		this.StationID = sid;
		this.StationName = sname;
		this.posx = x;
		this.posy = y;
	}
	
	public Station( String sname , int x , int y) {
	
		this.StationName = sname;
		this.posx = x;
		this.posy = y;
	}

	public Station(Integer stationid2) {
		this.StationID = stationid2;
	}

	public int getStationID() {
		return StationID;
	}

	public void setStationID(int stationID) {
		StationID = stationID;
	}

	public String getStationName() {
		return StationName;
	}

	public void setStationName(String stationName) {
		StationName = stationName;
	}

	public int getPosx() {
		return posx;
	}

	public void setPosx(int posx) {
		this.posx = posx;
	}

	public int getPosy() {
		return posy;
	}

	public void setPosy(int posy) {
		this.posy = posy;
	}
	
	
	

}
