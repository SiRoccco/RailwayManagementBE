package com.rwm.TicketUtil;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.Instant;
import java.time.LocalDateTime;
import java.time.ZoneOffset;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rwm.beans.Ticket;
import com.rwm.customexceptions.CustomException;
import com.rwm.dao.DBInstance;
import com.rwm.pojo.PassengerPojo;
import com.rwm.pojo.StationPojo;

public class TicketUtil {

	public TicketUtil() {
		System.out.println("Git commit check");
	}
	
	protected static Ticket getTicketByID(int ticketid) throws SQLException, ClassNotFoundException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String getallstations = "select * from ticket_user where ticket_id = ?";
		PreparedStatement ps = con.prepareStatement(getallstations);
		ps.setInt(1, ticketid);
		ResultSet rs = ps.executeQuery();
		
		Ticket t = new Ticket();
		
		if(rs.next()) {
			
			t.setTicketid(rs.getInt("ticket_id"));
			t.setUserid(rs.getInt("user_id"));
			t.setTripid(rs.getInt("trip_id"));
			t = TicketUtil.getTicketInfo(t);
		}else {
			throw new CustomException("Ticket not found");
		}
		 
		return t;
	}
	
	protected static LinkedList<Ticket> getAllTickets() throws SQLException, ClassNotFoundException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String getalltickets = "select * from ticket_user";
		PreparedStatement ps = con.prepareStatement(getalltickets);
		ResultSet rs = ps.executeQuery();
		
		LinkedList<Ticket> ticketlist = new LinkedList<Ticket>();
		
		while(rs.next()) {
			Ticket t = new Ticket();
			t.setTicketid(rs.getInt("ticket_id"));
			t.setUserid(rs.getInt("user_id"));
			t.setTripid(rs.getInt("trip_id"));
			t = TicketUtil.getTicketInfo(t);
			
			ticketlist.add(t);
		}
		 
		return ticketlist;
	}
	
	protected static Ticket getStationInfo(Ticket t) throws ClassNotFoundException, SQLException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String getstationinfo = "select * from ticket_seat_rel where ticket_id = ? and trip_id = ?";
		PreparedStatement ps = con.prepareStatement(getstationinfo);
		ps.setInt(1, t.getTicketid());
		ps.setInt(2, t.getTripid());
		
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()) {
			int ssi = rs.getInt("starting_station_id");
			int esi = rs.getInt("ending_station_id");
			
			String gettiming = "select * from trip_station_rel tsr left join station s on tsr.Station_ID = s.Station_ID "
					+ "where Trip_ID = ? and tsr.Station_ID = ?";
			PreparedStatement ps1 = con.prepareStatement(gettiming);
			PreparedStatement ps2 = con.prepareStatement(gettiming);
			ps1.setInt(1, t.getTripid());
			ps1.setInt(2, ssi);
			
			ResultSet startingstationrs = ps1.executeQuery();
			if(startingstationrs.next()) {
				StationPojo sspj = new StationPojo();
				sspj.setStationid(startingstationrs.getInt("station_id"));
				sspj.setStationname(startingstationrs.getString("Station_Name"));
				sspj.setOrder(startingstationrs.getInt("order"));
				
				LocalDateTime depttime = LocalDateTime.ofInstant(Instant.ofEpochMilli(startingstationrs.getLong("departure_time")), ZoneOffset.UTC );
				sspj.setDeparturetime(depttime);
				
				if(LocalDateTime.now().isAfter(depttime)){
					t.setExpired(true);
				}
				
				t.setBoardingstation(sspj);
			}else {
				throw new CustomException("Error");
			}
			
			ps2.setInt(1, t.getTripid());
			ps2.setInt(2, esi);
			
			ResultSet esrs = ps2.executeQuery();
			
			if(esrs.next()) {
				StationPojo espj = new StationPojo();
				espj.setStationid(esrs.getInt("station_id"));
				espj.setStationname(esrs.getString("Station_Name"));
				espj.setOrder(esrs.getInt("order"));
				
				LocalDateTime depttime = LocalDateTime.ofInstant(Instant.ofEpochMilli(esrs.getLong("arrival_time")), ZoneOffset.UTC );
				espj.setArrivaltime(depttime);
				
				t.setArrivalstation(espj);
			}else {
				throw new CustomException("Error");
			}
			
		}
		
		String gettrainname = "select * from trip t1 left join trains t2 on t1.train_id = t2.train_id where t1.trip_id = ?";
		PreparedStatement ps3 = con.prepareStatement(gettrainname);
		ps3.setInt(1, t.getTripid());
		
		ResultSet trainname = ps3.executeQuery();
		if(trainname.next()) {
			t.setTrainname(trainname.getString("train_name"));
		}
		
		return t;
	}
	
	protected static Ticket getPassInfo(Ticket t) throws ClassNotFoundException, SQLException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String getfareinfo = "select * from ticket_user where ticket_id = ?";
		PreparedStatement ps0 = con.prepareStatement(getfareinfo);
		ps0.setInt(1, t.getTicketid());
		
		ResultSet farers = ps0.executeQuery();
		
		if(farers.next()) {
			t.setFare(farers.getFloat("fare"));
		}else {
			throw new CustomException("Error");
		}
		
		String getpassids = "select coach_no , seat_no ,passenger_id , present from ticket_seat_rel where trip_id = ? and ticket_id = ?";
		PreparedStatement ps = con.prepareStatement(getpassids);
		ps.setInt(1, t.getTripid());
		ps.setInt(2, t.getTicketid());
		
		ResultSet rs = ps.executeQuery();
		
		String getpassinfo = "select * from passenger_user_rel where passenger_id = ?";
		PreparedStatement ps1 = con.prepareStatement(getpassinfo);
		
		LinkedList<PassengerPojo> plist = new LinkedList<PassengerPojo>();
		while(rs.next()) {
			PassengerPojo pp = new PassengerPojo();
			ps1.setInt(1, rs.getInt("passenger_id"));
			
			
			
			ResultSet passinfors = ps1.executeQuery();
			
			if(passinfors.next()) {
				pp.setTicketstatus((rs.getInt("present") > 0 )? "active" :"cancelled");
				pp.setPassengerid(passinfors.getInt("passenger_id"));
				pp.setPassengername(passinfors.getString("passenger_name"));
				pp.setAge(passinfors.getInt("age"));
				pp.setGender(passinfors.getString("gender"));
				pp.setCoachno(rs.getInt("coach_no"));
				pp.setSeatno(rs.getInt("seat_no"));
				plist.add(pp);
			}else {
				throw new CustomException("error");
			}
			
			
		}
		
		t.setPassengers(plist);
		t.setNumberofseatsbooked(plist.size());
		return t;
	}
	
	
	
	protected static Ticket getTicketInfo(Ticket t) throws ClassNotFoundException, SQLException, CustomException {
		
		t = TicketUtil.getPassInfo(t);
		t = TicketUtil.getStationInfo(t);
		
		
		return t;
	}

	protected static JSONObject getTicketObj(Ticket t) {
		JSONObject ticketobj = new JSONObject();
		
		ticketobj.put("ticketid", t.getTicketid());
		ticketobj.put("userid", t.getUserid());
		ticketobj.put("tripid", t.getTripid());
		ticketobj.put("boardingstation", new JSONObject(t.getBoardingstation()));
		ticketobj.put("arrivalstation", new JSONObject(t.getArrivalstation()));
		
		JSONArray passinfo = new JSONArray();
		for(PassengerPojo pp : t.getPassengers()) {
			passinfo.put(new JSONObject(pp));
		}
		
		ticketobj.put("passengers", passinfo);
		ticketobj.put("fare", t.getFare());
		
		return ticketobj;
	}
	
	public static JSONArray getTickets(Integer ticketid) throws ClassNotFoundException, SQLException, CustomException{
		
		JSONArray tickets = new JSONArray();
		
		if(ticketid == null) {
			LinkedList<Ticket> ticketlist = TicketUtil.getAllTickets();
			
			for(Ticket t : ticketlist) {
				JSONObject ticketobj = TicketUtil.getTicketObj(t);
				tickets.put(ticketobj);
			}
			
			
		}else {
			tickets.put(TicketUtil.getTicketByID(ticketid));
			
		}
		
		return tickets;
	}
	

	
	public static JSONArray getTicketsByUserId(Integer userid , Integer ticketid) throws ClassNotFoundException, SQLException, CustomException{
		Connection con = DBInstance.getInstance().getConnection();
		String getticketsofuser = null;
		PreparedStatement ps;
		if(ticketid == null) {
			getticketsofuser = "select * from ticket_user where user_id = ?";
			ps = con.prepareStatement(getticketsofuser);
			ps.setInt(1, userid);
		}else {
			getticketsofuser = "select * from ticket_user where user_id = ? and ticket_id = ?";
			ps = con.prepareStatement(getticketsofuser);
			ps.setInt(1, userid);
			ps.setInt(2, ticketid);
		}
		
		
		boolean ticketsfound = false;
		
		ResultSet rs = ps.executeQuery();
		
		JSONArray tickets = new JSONArray();
		
		while(rs.next()) {
			ticketsfound = true;
			Ticket t = new Ticket();
			t.setUserid(userid);
			t.setTripid(rs.getInt("trip_id"));
			t.setTicketid(rs.getInt("ticket_id"));
			t = TicketUtil.getTicketInfo(t);
			
			tickets.put(new JSONObject(t));
			
		}
		
		if(ticketsfound == false) {
			throw new CustomException("You haven't booked any tickets");
		}
		return tickets;
		
	}
	
	public static JSONArray getTicketRoot(Integer userid , Integer ticketid) throws ClassNotFoundException, SQLException, CustomException {
		
		if(userid == null && ticketid == null) {
			return TicketUtil.getTickets(ticketid);
		}
		
		if(userid !=null && ticketid != null) {
			return TicketUtil.getTicketsByUserId(userid,ticketid);
		}else if(userid != null && ticketid == null){
			return TicketUtil.getTicketsByUserId(userid,ticketid);
		}
		
		
		
		return null;
		
	}
	
}
