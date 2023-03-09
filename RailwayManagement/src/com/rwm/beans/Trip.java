package com.rwm.beans;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Hashtable;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rwm.pojo.StationPojo;

public class Trip {
	private int trainid;
	private int tripid;
	private String tripname;
	private int costperkm;
	private ArrayList<Integer> order;
	private LinkedHashMap<String , HashMap<String , LocalDateTime>> stationdata;
	private LinkedHashMap<Integer , HashMap<String , LocalDateTime>> stationid_timings;
	
	private LinkedList<StationPojo> stationlist;
	
	
	
	public LinkedList<StationPojo> getStationlist() {
		return stationlist;
	}
	public void setStationlist(LinkedList<StationPojo> stationlist) {
		this.stationlist = stationlist;
	}
	public LinkedHashMap<Integer, HashMap<String, LocalDateTime>> getStationid_timings() {
		return stationid_timings;
	}
	public void setStationid_timings(LinkedHashMap<Integer, HashMap<String, LocalDateTime>> stationid_timings) {
		this.stationid_timings = stationid_timings;
	}
	public int getCostperkm() {
		return costperkm;
	}
	public void setCostperkm(int costperkm) {
		this.costperkm = costperkm;
	}
	public ArrayList<Integer> getorder() {
		return order;
	}
	public void setorder(ArrayList<Integer> order) {
		this.order = order;
	}
	public int getTrainid() {
		return trainid;
	}
	public void setTrainid(int trainid) {
		this.trainid = trainid;
	}
	public int getTripid() {
		return tripid;
	}
	public void setTripid(int tripid) {
		this.tripid = tripid;
	}
	public String getTripname() {
		return tripname;
	}
	public void setTripname(String tripname) {
		this.tripname = tripname;
	}
	public LinkedHashMap<String, HashMap<String, LocalDateTime>> getStationdata() {
		return stationdata;
	}
	public void setStationdata(LinkedHashMap<String, HashMap<String, LocalDateTime>> stationdata) {
		this.stationdata = stationdata;
	}
	
	

}
