package com.rwm.pojo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.util.LinkedList;

import org.json.JSONArray;

import com.rwm.customexceptions.CustomException;
import com.rwm.dao.DBInstance;

public class BookingPojo {
	
	private int userid;
	
	private int tripid;
	
	private LinkedList<PassengerPojo> passengerlist;
	
	private int startingstationid;
	
	private int endingstationid;
	
	boolean consecutiveseatpref;
	
	float fare;
	
	
	
	public float getFare() {
		return fare;
	}

	public void setFare(float fare) {
		this.fare = fare * this.getSize();
	}

	public BookingPojo() {
		// TODO Auto-generated constructor stub
	}
	
	public int getSize() {
		return passengerlist.size();
	}
	
	public int getSSO() throws ClassNotFoundException, SQLException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String getsso = "select trip_station_rel.`Order` from trip_station_rel where trip_id = ? and station_id = ?";
		PreparedStatement ps = con.prepareStatement(getsso);
		ps.setInt(1, this.tripid);
		ps.setInt(2, this.startingstationid);
		
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()) {
			return rs.getInt("order");
		}else {
			throw new CustomException("Could not get order of source station");
		}
		
		
	}
	
	public int getESO() throws ClassNotFoundException, SQLException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String getsso = "select trip_station_rel.`Order` from trip_station_rel where trip_id = ? and station_id = ?";
		PreparedStatement ps = con.prepareStatement(getsso);
		ps.setInt(1, this.tripid);
		ps.setInt(2, this.endingstationid);
		
		
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()) {
			return rs.getInt("order");
		}else {
			throw new CustomException("Could not get order of source station");
		}
		
		
	}
	
	public BookingPojo(int uid , int tid , JSONArray plist , int sso , int eso , boolean csp) {
		this.userid = uid;
		this.tripid = tid;
		
		LinkedList<PassengerPojo> parray = new LinkedList<PassengerPojo>();
		
		for(int i =0 ; i < plist.length() ; i++) {
			PassengerPojo pp = new PassengerPojo();
			
			pp.setPassengerid(plist.getJSONObject(i).getInt("passengerid"));
			
			pp.setPassengername(plist.getJSONObject(i).getString("passengername"));
			
			pp.setGender(plist.getJSONObject(i).getString("gender"));
			
			pp.setAge(plist.getJSONObject(i).getInt("age"));
			
			parray.add(pp);		
		}
		this.passengerlist = parray;
		
		this.startingstationid = sso;
		
		this.endingstationid = eso;
		
		this.consecutiveseatpref = csp;
	}
	
	public BookingPojo(int uid , int tid , JSONArray plist , int sso , int eso) {
		this.userid = uid;
		this.tripid = tid;
		
		LinkedList<PassengerPojo> parray = new LinkedList<PassengerPojo>();
		
		for(int i =0 ; i < plist.length() ; i++) {
			PassengerPojo pp = new PassengerPojo();
			
			pp.setPassengerid(plist.getJSONObject(i).getInt("passengerid"));
			
			pp.setPassengername(plist.getJSONObject(i).getString("passengername"));
			
			pp.setGender(plist.getJSONObject(i).getString("gender"));
			
			pp.setAge(plist.getJSONObject(i).getInt("age"));
			
			parray.add(pp);		
		}
		this.passengerlist = parray;
		
		this.startingstationid = sso;
		
		this.endingstationid = eso;
	}

	public int getUserid() {
		return userid;
	}

	public void setUserid(int userid) {
		this.userid = userid;
	}

	public int getTripid() {
		return tripid;
	}

	public void setTripid(int tripid) {
		this.tripid = tripid;
	}

	public LinkedList<PassengerPojo> getPassengerlist() {
		return passengerlist;
	}

	public void setPassengerlist(LinkedList<PassengerPojo> passengerlist) {
		this.passengerlist = passengerlist;
	}

	public int getstartingstationid() {
		return startingstationid;
	}

	public void setstartingstationid(int startingstationid) {
		this.startingstationid = startingstationid;
	}

	public int getendingstationid() {
		return endingstationid;
	}

	public void setendingstationid(int endingstationid) {
		this.endingstationid = endingstationid;
	}

	public boolean isConsecutiveseatpref() {
		return consecutiveseatpref;
	}

	public void setConsecutiveseatpref(boolean consecutiveseatpref) {
		this.consecutiveseatpref = consecutiveseatpref;
	}
	
	
	
}
