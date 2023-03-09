package com.rwm.bookingutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import org.json.JSONObject;

import com.rwm.customexceptions.CustomException;
import com.rwm.dao.DBInstance;

public class CancelUtil {

	public CancelUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static void cancelTickets(JSONObject cancelinfo) throws ClassNotFoundException, SQLException, CustomException {
		
		Connection con = DBInstance.getInstance().getConnection();
		
		String checkuserexist = "select * from users where user_id = ?";
		PreparedStatement ps = con.prepareStatement(checkuserexist);
		ps.setInt(1, cancelinfo.getInt("userid"));
		if(ps.executeQuery().next()) {
			
			String checktripexist = "select * from trip where trip_id = ?";
			PreparedStatement ps1 = con.prepareStatement(checktripexist);
			ps1.setInt(1, cancelinfo.getInt("tripid"));
			
			if(ps1.executeQuery().next()) {
			
				String checkuserintrip =  "select * from ticket_user where trip_id = ? and user_id = ?";
				PreparedStatement ps2 = con.prepareStatement(checkuserintrip);
				ps2.setInt(1, cancelinfo.getInt("tripid"));
				ps2.setInt(2, cancelinfo.getInt("userid"));
				
				if(ps2.executeQuery().next()) {
					
					String checkpidsuser = "select * from passenger_user_rel where passenger_id = ? and user_id = ?";
					PreparedStatement ps3 = con.prepareStatement(checkpidsuser);
					ps3.setInt(2, cancelinfo.getInt("userid"));
					
					String canceltickets = "update ticket_seat_rel left join ticket_user on ticket_seat_rel.ticket_id = ticket_user.ticket_id set present = 0"
							+ " where passenger_id = ? and ticket_seat_rel.ticket_id = ? and user_id = ?";
					PreparedStatement ps4 = con.prepareStatement(canceltickets);
					ps4.setInt(2, cancelinfo.getInt("ticketid"));
					ps4.setInt(3, cancelinfo.getInt("userid"));
					con.setAutoCommit(false);
					for(int i =0 ; i < cancelinfo.getJSONArray("passengerids").length() ; i++) {
						ps3.setInt(1, cancelinfo.getJSONArray("passengerids").getInt(i));
						if(ps3.executeQuery().next()) {
							
							
							ps4.setInt(1,cancelinfo.getJSONArray("passengerids").getInt(i) );
							boolean res = ps4.executeUpdate() > 0;
							if(res == false) {
								con.rollback();
								throw new CustomException("Could not cancel tickets");
							}
							
						}else {
							throw new CustomException("Passenger ID : " + cancelinfo.getJSONArray("passengerids").getInt(i) + " not found for this user");
						}
					}
					con.commit();
					
					
				}else {
					throw new CustomException("User does not exist in this trip");
				}
			}else {
				throw new CustomException("Trip not found");
			}
		}else {
			throw new CustomException("User not found");
		}
		
	}
	
}
