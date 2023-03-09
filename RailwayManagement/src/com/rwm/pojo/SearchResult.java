package com.rwm.pojo;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import org.json.JSONObject;

import com.rwm.beans.Trip;
import com.rwm.customexceptions.CustomException;
import com.rwm.dao.DBInstance;

public class SearchResult {
	
	private String trainname;
	private int trainid;
	private int availableseats;
	
	private int duration;
	
	private StationPojo source;
	
	private StationPojo destination;
	
	private JSONObject trip;
	
	private float fare;
	
	public void setavailableseats() throws ClassNotFoundException, SQLException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String gettraininfo = "select * from trains where train_id = ? ";
		PreparedStatement ps = con.prepareStatement(gettraininfo);
		ps.setInt(1, this.trainid);
		ResultSet rs = ps.executeQuery();
		
		int noofcoaches = 0;
		int seatspercoach = 0;
		
		if(rs.next()) {
			noofcoaches = rs.getInt("coaches");
			seatspercoach = rs.getInt("seatspercoach");
			
			BookingPojo bp = new BookingPojo();
			bp.setTripid(this.gettrip().getInt("trip_id"));
			bp.setstartingstationid(this.getSource().getStationid());
			bp.setendingstationid(this.getDestination().getStationid());
			
			String checkallseats = "SELECT tsr.* , t1.`Order` as Starting_Station_Order , t2.`Order` as Ending_Station_Order"
					+ " FROM Ticket_Seat_Rel tsr"
					+ " join Trip_Station_Rel t1 on tsr.Trip_ID = t1.Trip_ID" 
					+ "	and tsr.Starting_Station_ID  = t1.Station_ID " 
					+ " join Trip_Station_Rel t2 on tsr.Trip_ID = t2.Trip_ID" 
					+ " and tsr.Ending_Station_ID = t2.Station_ID"
					+ " where tsr.trip_id = ? and t1.Order <= ? and t2.Order >= ? and tsr.present = 1";
			PreparedStatement ps1 = con.prepareStatement(checkallseats,ResultSet.TYPE_SCROLL_INSENSITIVE , ResultSet.CONCUR_UPDATABLE );
			ps1.setInt(1, bp.getTripid());
			ps1.setInt(2, bp.getSSO());
			ps1.setInt(3, bp.getESO());
			
			ResultSet rs1 = ps1.executeQuery();
			
			if(rs1.next()) {
				rs1.last();
				int noofseatsbooked = rs1.getRow();
				int availableseats = (noofcoaches * seatspercoach) - noofseatsbooked;
				this.availableseats = availableseats;
			}else {
				this.availableseats = noofcoaches * seatspercoach;
				
			}
			
			
		}else {
			throw new CustomException("No train found for this id");
		}
	}
	
	public float getFare() {
		return fare;
	}

	public void setFare(float fare) {
		this.fare = fare;
	}

	public JSONObject gettrip() {
		return trip;
	}

	public void settrip(JSONObject trip) {
		this.trip = trip;
	}

	public SearchResult() {
		// TODO Auto-generated constructor stub
	}

	public String getTrainname() {
		return trainname;
	}

	public void setTrainname(String trainname) {
		this.trainname = trainname;
	}

	public int getTrainid() {
		return trainid;
	}

	public void setTrainid(int trainid) {
		this.trainid = trainid;
	}

	public int getAvailableseats() {
		return availableseats;
	}

	public void setAvailableseats(int availableseats) {
		this.availableseats = availableseats;
	}

	public int getDuration() {
		return duration;
	}

	public void setDuration(int duration) {
		this.duration = duration;
	}

	public StationPojo getSource() {
		return source;
	}

	public void setSource(StationPojo source) {
		this.source = source;
	}

	public StationPojo getDestination() {
		return destination;
	}

	public void setDestination(StationPojo destination) {
		this.destination = destination;
	}
	
	
	
}
