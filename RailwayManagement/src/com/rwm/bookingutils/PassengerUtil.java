package com.rwm.bookingutils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Savepoint;
import java.sql.Statement;
import java.util.Iterator;

import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rwm.beans.User;
import com.rwm.customexceptions.CustomException;
import com.rwm.dao.DBInstance;
import com.rwm.pojo.PassengerPojo;

public class PassengerUtil {

	public PassengerUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static void putPassenger(PassengerPojo pp) throws ClassNotFoundException, SQLException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String updatepassenger = "update passenger_user_rel set passenger_name = ? , gender = ? , age = ? "
				+ "where passenger_id = ?";
		PreparedStatement ps = con.prepareStatement(updatepassenger);
		
		ps.setString(1, pp.getPassengername());
		ps.setString(2, pp.getGender());
		ps.setInt(3, pp.getAge());
		ps.setInt(4, pp.getPassengerid());
		
		
		con.setAutoCommit(false);
		boolean res = ps.executeUpdate() > 0;
		
		if(res == false) {
			throw new CustomException("Could not update passenger details");
		}
		
		con.commit();
		
	}
	
	protected static PassengerPojo getSinglePassenger(Integer passengerid) throws ClassNotFoundException, SQLException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String getpassenger = "select * from passenger_user_rel where passenger_id = ?";
		
		PreparedStatement ps = con.prepareStatement(getpassenger);
		ps.setInt(1, passengerid);
		
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()) {
			PassengerPojo pp = new PassengerPojo();
			
			pp.setGender(rs.getString("gender"));
			pp.setAge(rs.getInt("age"));
			
			return pp;
		}else {
			throw new CustomException("Could not find passenger");
		}
		
	}
	
	public static void deletePassenger(int userid , int passengerid) throws ClassNotFoundException, SQLException, CustomException {
		Connection con = DBInstance.getInstance().getConnection();
		
		String deletepassenger = "delete from passenger_user_rel where user_id = ? and passenger_id = ?";
		
		PreparedStatement ps = con.prepareStatement(deletepassenger);
		ps.setInt(1, userid);
		ps.setInt(2, passengerid);
		
		con.setAutoCommit(false);
		
		try {
			ps.executeUpdate();
			con.commit();
		}catch(SQLException e) {
			try {
				con.rollback();
				e.printStackTrace();
			}catch(SQLException e1) {
				throw new CustomException("Could not rollback");
			}
			throw new CustomException("Could not delete passenger");
		}
	}
	
	public static LinkedList<PassengerPojo> getPassengers(User u) throws ClassNotFoundException, SQLException, CustomException {
		LinkedList<PassengerPojo> passengerlist = new LinkedList<PassengerPojo>();
		
		Connection con = DBInstance.getInstance().getConnection();
		
		String getpassengers = "select * from Passenger_User_Rel where user_id = ?";
		
		PreparedStatement ps = con.prepareStatement(getpassengers);
		ps.setInt(1, u.getUserid());
		
		ResultSet rs = ps.executeQuery();
		
		while(rs.next()) {
			
			PassengerPojo pp = new PassengerPojo();
			
			pp.setPassengerid(rs.getInt("passenger_id"));
			pp.setPassengername(rs.getString("passenger_name"));
			pp.setGender(rs.getString("gender"));
			pp.setAge(rs.getInt("age"));
			
			passengerlist.add(pp);
		}
		
		if(passengerlist.size() == 0) {
			throw new CustomException("No passengers found for this user");
		}
			
		return passengerlist;
		
	}
	
	public static User addPassengers(JSONObject passengerinfo) throws CustomException {
		
		LinkedList<PassengerPojo> passengerlist = new LinkedList<PassengerPojo>();  
		
		JSONArray passarray = passengerinfo.getJSONArray("passengers");
		
		User u = new User();
		
		for(int i =0 ; i < passarray.length() ; i++) {
			
			JSONObject singlepass = passarray.getJSONObject(i);
			
			int passengerage;
			
			try {
				passengerage = singlepass.getInt("age");
			}catch(Exception e) {
				throw new CustomException("Passenger age must be a integer");
			}
			
			PassengerPojo pp = new PassengerPojo(singlepass.getString("passengername") , singlepass.getString("gender") , passengerage);
			
			passengerlist.add(pp);
			
		}
		
		u.setPassengersinfo(passengerlist);
		
		return u;
		
	}
	
	public static int insertPassengers(User u) throws ClassNotFoundException, SQLException, CustomException {
		
		Connection con = null;
		
		int passid = -99;
		try {
			con = DBInstance.getInstance().getConnection();
		}catch(NullPointerException e) {
			throw new CustomException("Could not connect to db");
		}
		String checkuserexist = "select * from users where user_id = ?";
		
		PreparedStatement ps = con.prepareStatement(checkuserexist);
		
		ps.setInt(1, u.getUserid());
		
		Iterator<PassengerPojo> iterator = u.getPassengersinfo().iterator();
		
		if(ps.executeQuery().next()) {
			
			String inserpassengers = "insert into passenger_user_rel (passenger_name , gender , age , user_id) value (? , ? , ? , ?)";
			PreparedStatement ps1 = con.prepareStatement(inserpassengers , Statement.RETURN_GENERATED_KEYS);
			
			Savepoint sp = con.setSavepoint("savepoint1");
			
			con.setAutoCommit(false);
			
			while(iterator.hasNext()) {
				
				PassengerPojo pp = iterator.next();
				ps1.setString(1, pp.getPassengername());
				ps1.setString(2, pp.getGender());
				ps1.setInt(3, pp.getAge());
				ps1.setInt(4, u.getUserid());
				try {
					ps1.executeUpdate();
					ResultSet key = ps1.getGeneratedKeys();
					if(key.next()) {
						passid = key.getInt(1);
					}
					
					con.commit();
				}catch(SQLException e) {
					
					try {
						con.rollback(sp);
					}catch(SQLException e1) {
						throw new CustomException("Could not rollback to savepoint");
					}
					throw new CustomException("Error inserting passenger details");
				}
			}
			
			
			
		}else {
			throw new CustomException("User does not exist");
		}
		return passid;
		
	}
	
	public static JSONObject getPassengersRoot(Integer userid , Integer passengerid) throws ClassNotFoundException, SQLException, CustomException {
		
		if(userid != null && passengerid != null) {
			
			PassengerPojo pp = PassengerUtil.getSinglePassenger(passengerid);
			JSONObject jobj = new JSONObject();
			jobj.put("passenger", new JSONObject(pp) );
			return jobj;
			
		}else if (passengerid == null && userid != null){
			User u = new User(userid);
			LinkedList<PassengerPojo> plist = PassengerUtil.getPassengers(u);
			
			JSONArray parray = new JSONArray(plist);
			
			JSONObject jobj = new JSONObject();
			jobj.put("passengers", parray);
			return jobj;
		}else {
			throw new CustomException("Login to continue");
		}
		
		
	}

	public static PassengerPojo convertjsontoobj(JSONObject passinfo) {
		
		PassengerPojo pp = new PassengerPojo();
		
		pp.setPassengerid(passinfo.getInt("passengerid"));
		pp.setPassengername(passinfo.getString("passengername"));
		pp.setAge(passinfo.getInt("age"));
		pp.setGender(passinfo.getString("gender"));
		
		return pp;
	}
	
}
