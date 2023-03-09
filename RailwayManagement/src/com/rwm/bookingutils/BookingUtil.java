package com.rwm.bookingutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;

import com.rwm.beans.Trip;
import com.rwm.customexceptions.CustomException;
import com.rwm.dao.DBInstance;
import com.rwm.dao.RwmDao;
import com.rwm.pojo.BookingPojo;
import com.rwm.pojo.PassengerPojo;

public class BookingUtil {

	private static RwmDao dao = new RwmDao();
	
	public BookingUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static void checkParam(BookingPojo bp) throws SQLException, ClassNotFoundException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		String checkpass ="select * from passenger_user_rel where user_id = ? and passenger_id = ? and passenger_name = ?";
		PreparedStatement ps = con.prepareStatement(checkpass);
		
		for(PassengerPojo pp : bp.getPassengerlist()) {
			ps.setInt(1, bp.getUserid());
			ps.setInt(2, pp.getPassengerid());
			ps.setString(3 , pp.getPassengername());
			
			if(!(ps.executeQuery().next())){
				throw new CustomException("Passenger " + pp.getPassengername() +  " not found for this user");
			}
		}
	}
	

	
//	protected static boolean bookTicket(BookingPojo bp , int currcoach, int lastbookedseat) throws ClassNotFoundException, SQLException, CustomException {
//	
//		
//		
//		Connection con = DBInstance.getInstance().getConnection();
//		
//		String insertticket = "insert into ticket_user (user_id , seats_booked , trip_id) value (? , ? , ? )";
//		PreparedStatement ps = con.prepareStatement(insertticket , PreparedStatement.RETURN_GENERATED_KEYS);
//		ps.setInt(1, bp.getUserid());
//		ps.setInt(2, bp.getPassengerlist().size());
//		ps.setInt(3, bp.getTripid());
//		
//		Trip t = new Trip();
//		t.setTripid(bp.getTripid());
//		
//		float fare = dao.calcfare(t, bp.getstartingstationid(), bp.getendingstationid());
//		
//		System.out.println("Fare : " + fare);
//		
//		String insertpassinfo = "insert into ticket_seat_rel (trip_id , coach_no , seat_no , passenger_id , ticket_id , starting_station_id , ending_station_id , present)"
//				+ " value (? , ? , ? , ? , ? , ? , ? , ?)";
//		
//		PreparedStatement ps1 = con.prepareStatement(insertpassinfo);
//		
//		con.setAutoCommit(false);
//		Savepoint sp = con.setSavepoint("savepoint2");
//		try {
//			Iterator<PassengerPojo> plist = bp.getPassengerlist().iterator();
//			ps.executeUpdate();
//			
//			ResultSet rs = ps.getGeneratedKeys();
//			rs.next();
//			boolean res = false;
//			
//			do {
//				PassengerPojo pp = plist.next();
//				ps1.setInt(1, bp.getTripid());
//				ps1.setInt(2, currcoach);
//				ps1.setInt(3, ++lastbookedseat);
//				ps1.setInt(4, pp.getPassengerid());
//				ps1.setInt(5, rs.getInt(1));
//				ps1.setInt(6, bp.getstartingstationid());
//				ps1.setInt(7, bp.getendingstationid());
//				ps1.setInt(8, 1);
//				
//				res = ps1.executeUpdate() > 0;
//				if(res == false) {
//					throw new CustomException("Error Booking Tickets");
//				}
//			} while(plist.hasNext() && res == true);
//			
//			con.commit();
//			return true;
//		}catch(SQLException e) {
//			con.rollback(sp);
//			e.printStackTrace();
//			throw new CustomException("Error Booking Tickets");
//		}
//		
//	}
	
	protected static int checkOverallSeats(BookingPojo bp) throws ClassNotFoundException, SQLException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String checkallseats = "SELECT tsr.* , t1.`Order` as Starting_Station_Order , t2.`Order` as Ending_Station_Order"
				+ " FROM Ticket_Seat_Rel tsr"
				+ " join Trip_Station_Rel t1 on tsr.Trip_ID = t1.Trip_ID" 
				+ "	and tsr.Starting_Station_ID  = t1.Station_ID " 
				+ " join Trip_Station_Rel t2 on tsr.Trip_ID = t2.Trip_ID" 
				+ " and tsr.Ending_Station_ID = t2.Station_ID"
				+ " where tsr.trip_id = ? and t1.Order <= ? and t2.Order >= ? and tsr.present = 1";
		
		PreparedStatement ps = con.prepareStatement(checkallseats , ResultSet.TYPE_SCROLL_INSENSITIVE , ResultSet.CONCUR_UPDATABLE);
		ps.setInt(1, bp.getTripid());
		ps.setInt(2, bp.getSSO());
		ps.setInt(3, bp.getESO());
		
		ResultSet rs = ps.executeQuery();
		
		rs.last();
		
		return rs.getRow();
	}
	
	protected static int checkSeatsbyCoach(BookingPojo bp , int coachno) throws ClassNotFoundException, SQLException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String checkallseats = "SELECT tsr.* , t1.`Order` as Starting_Station_Order , t2.`Order` as Ending_Station_Order"
				+ " FROM Ticket_Seat_Rel tsr"
				+ " join Trip_Station_Rel t1 on tsr.Trip_ID = t1.Trip_ID" 
				+ "	and tsr.Starting_Station_ID  = t1.Station_ID " 
				+ " join Trip_Station_Rel t2 on tsr.Trip_ID = t2.Trip_ID" 
				+ " and tsr.Ending_Station_ID = t2.Station_ID"
				+ " where tsr.trip_id = ? and coach_no = ? and t1.Order <= ? and t2.Order >= ? and tsr.present = 1";
		
		PreparedStatement ps = con.prepareStatement(checkallseats, ResultSet.TYPE_SCROLL_INSENSITIVE , ResultSet.CONCUR_UPDATABLE);
		ps.setInt(1, bp.getTripid());
		ps.setInt(2, coachno);
		ps.setInt(3, bp.getSSO());
		ps.setInt(4, bp.getESO());
		
		ResultSet rs = ps.executeQuery();
		
		rs.last();
		
		return rs.getRow();
	}
	
	protected static ResultSet getSeatsinCoach(BookingPojo bp, int coachno) throws SQLException, ClassNotFoundException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String getseats = "SELECT tsr.* , t1.`Order` as Starting_Station_Order , t2.`Order` as Ending_Station_Order"
				+ " FROM Ticket_Seat_Rel tsr"
				+ " join Trip_Station_Rel t1 on tsr.Trip_ID = t1.Trip_ID" 
				+ "	and tsr.Starting_Station_ID  = t1.Station_ID " 
				+ " join Trip_Station_Rel t2 on tsr.Trip_ID = t2.Trip_ID" 
				+ " and tsr.Ending_Station_ID = t2.Station_ID"
				+ " where tsr.trip_id = ? and coach_no = ? and t1.Order <= ? and t2.Order >= ?";
		
		PreparedStatement ps = con.prepareStatement(getseats, ResultSet.TYPE_SCROLL_INSENSITIVE , ResultSet.CONCUR_UPDATABLE);
		ps.setInt(1, bp.getTripid());
		ps.setInt(2, coachno);
		ps.setInt(3, bp.getSSO());
		ps.setInt(4, bp.getESO());
		
		ResultSet rs = ps.executeQuery();
		
		return rs;
	}
	
	protected static void checkCoaches(BookingPojo bp , int noofcoaches , int seatspercoach) throws ClassNotFoundException, SQLException, CustomException {
		
		Connection con = DBInstance.getInstance().getConnection();
		
		if(BookingUtil.checkOverallSeats(bp) + bp.getSize() > (noofcoaches * seatspercoach)) {
			throw new CustomException("Seats are full");
		}
		
		Trip t = dao.getTrip(bp.getTripid());
		
		if(bp.isConsecutiveseatpref()) {
			
			boolean ticketsbooked = false;
			for(int i =1 ; i <= noofcoaches && !ticketsbooked ; i++) {
				
				if(BookingUtil.checkSeatsbyCoach(bp, i) + bp.getSize() > seatspercoach) {
					continue;
				}else {
					
					ResultSet seats = BookingUtil.getSeatsinCoach(bp, i);
					
					HashSet<Integer> seatset = BookingUtil.genSeatSet(seatspercoach);
					
					while(seats.next()) {
						if(seats.getInt("present")==1) {
							seatset.remove(seats.getInt("seat_no"));
						}
					}
					
					con.setAutoCommit(false);
					bp.setFare(dao.calcfare(t, bp.getstartingstationid(), bp.getendingstationid()));
					int key = BookingUtil.insertTicket(bp);
					
					Iterator<PassengerPojo> plist = bp.getPassengerlist().iterator();
					Iterator<Integer> seatlist = seatset.iterator();
					
					while(plist.hasNext() && seatlist.hasNext()) {
						PassengerPojo pp = plist.next();
						
						boolean res = BookingUtil.bookTicket(bp.getTripid(), pp.getPassengerid(), i, seatlist.next(), key, bp.getstartingstationid(), bp.getendingstationid());
						if(!res) {
							con.rollback();
							throw new CustomException("Error booking tickets");
						}
					}
					con.commit();
					ticketsbooked = true;
					
					
				}
				
				
			}
			if(ticketsbooked == false) {
				throw new CustomException("Seats full");
			}
		}
		
		
		
		if(!bp.isConsecutiveseatpref()) {
			
			boolean ticketsbooked = false;
			
			if(BookingUtil.checkOverallSeats(bp) + bp.getSize() > (noofcoaches * seatspercoach)) {
				throw new CustomException("Seats full");
			}else {
				
				Iterator<PassengerPojo> plist = bp.getPassengerlist().iterator();
				
				con.setAutoCommit(false);
				
				t.setTripid(bp.getTripid());
				bp.setFare(dao.calcfare(t, bp.getstartingstationid(), bp.getendingstationid()));
				int key = BookingUtil.insertTicket(bp);
				for(int i = 1 ; i <= noofcoaches && !ticketsbooked && plist.hasNext(); i++) {
					ResultSet seats = BookingUtil.getSeatsinCoach(bp, i);
					HashSet<Integer> seatset = BookingUtil.genSeatSet(seatspercoach);
					
					while(seats.next()) {
						if(seats.getInt("present")==1) {
							seatset.remove(seats.getInt("seat_no"));
						}
					}
						
						Iterator<Integer> seatlist = seatset.iterator();
						
						while(plist.hasNext() && seatlist.hasNext()) {
							PassengerPojo pp = plist.next();
							
							boolean res = BookingUtil.bookTicket(bp.getTripid(), pp.getPassengerid(), i, seatlist.next(), key, bp.getstartingstationid(), bp.getendingstationid());
							if(!res) {
								con.rollback();
								throw new CustomException("Error booking tickets");
							}
							
							if(!plist.hasNext()) {
								ticketsbooked = true;
							}
						}
						
					}
				
				con.commit();
				
			}
			
			if(ticketsbooked == false) {
				throw new CustomException("Seats full");
			}
		}
	}
	
protected static boolean bookTicket(int tripid, int pid, int coachno, Integer seatno, int key , int ssi , int esi) throws ClassNotFoundException, SQLException, CustomException {
	Connection con = DBInstance.getInstance().getConnection();
	
	String insertpassenger = "insert into ticket_seat_rel (trip_id , coach_no , seat_no , passenger_id , ticket_id , starting_station_id , ending_station_id , present)"
			+ "value ( ? , ? , ? , ? , ? , ? , ? , ?)";
	PreparedStatement ps = con.prepareStatement(insertpassenger);
	ps.setInt(1, tripid);
	ps.setInt(2, coachno);
	ps.setInt(3, seatno);
	ps.setInt(4, pid);
	ps.setInt(5, key);
	ps.setInt(6, ssi);
	ps.setInt(7, esi);
	ps.setInt(8, 1);
	
	boolean res = ps.executeUpdate() > 0;
	return res;
		
	}
	
	private static int insertTicket(BookingPojo bp) throws ClassNotFoundException, SQLException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String insertticket = "insert into ticket_user (user_id , seats_booked , trip_id , fare) "
				+ "value( ? , ? , ? , ?)";
		PreparedStatement ps = con.prepareStatement(insertticket , Statement.RETURN_GENERATED_KEYS);
		ps.setInt(1, bp.getUserid());
		ps.setInt(2, bp.getSize());
		ps.setInt(3, bp.getTripid());
		ps.setFloat(4, bp.getFare());
		ps.executeUpdate();
		
		ResultSet key = ps.getGeneratedKeys();
		
		if(key.next()) {
			return key.getInt(1);
		}else {
			throw new CustomException("Errorrrr");
		}
		
	}

	
	
	protected static HashSet<Integer> genSeatSet(int seatspercoach) {
		HashSet<Integer> set = new HashSet<Integer>();
		for(int i=1 ; i <= seatspercoach ; i++) {
			set.add(i);
		}
		
		return set;
	}
	
	
	protected static void checkAvailability(BookingPojo bp) throws SQLException, ClassNotFoundException, CustomException {
		
		Connection con = DBInstance.getInstance().getConnection();
		
		String gettrainid = "select train_id from trip where trip_id = ?";
		PreparedStatement ps = con.prepareStatement(gettrainid);
		ps.setInt(1, bp.getTripid());
		
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()) {
			
			String gettraininfo = "select * from Trains where train_id = ?";
			PreparedStatement ps1 = con.prepareStatement(gettraininfo);
			ps1.setInt(1, rs.getInt("train_id"));
			
			ResultSet rs1 = ps1.executeQuery();
			
			if(rs1.next()) {
				int noofcoaches = rs1.getInt("coaches");
				
				int seatspercoach = rs1.getInt("seatspercoach");
				
				BookingUtil.checkCoaches(bp , noofcoaches , seatspercoach);
				
			}
			
		}else {
			throw new CustomException("Train info not found");
		}
		
	}
	
	public static void startBooking(BookingPojo bp) throws SQLException, ClassNotFoundException, CustomException {
		
		Connection con = DBInstance.getInstance().getConnection();
		
		String checkuser = "select * from users where user_id = ?";
		PreparedStatement ps = con.prepareStatement(checkuser);
		ps.setInt(1, bp.getUserid());
		
		if(ps.executeQuery().next()) {
			
			BookingUtil.checkAvailability(bp);
			
		}else {
			throw new CustomException("User does not exist");
		}
		
		
	}
	
}
