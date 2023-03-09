package com.rwm.beans;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;

import java.util.LinkedList;

import com.rwm.customexceptions.CustomException;
import com.rwm.dao.DBInstance;
import com.rwm.pojo.PassengerPojo;

public class User {
	
	private int userid;
	
	private String username;
	
	private String email;
	
	private String password;
	
	private boolean isadmin;
	
	private LinkedList<PassengerPojo> passengersinfo;
	
	
	
	public User() {
		// TODO Auto-generated constructor stub
	}

	

	public LinkedList<PassengerPojo> getPassengersinfo() {
		return passengersinfo;
	}



	public void setPassengersinfo(LinkedList<PassengerPojo> passengersinfo) {
		this.passengersinfo = passengersinfo;
	}



	public String getRole() throws ClassNotFoundException, SQLException, CustomException {
		
		Connection con = DBInstance.getInstance().getConnection();
		
		String getrole = "select * from users where email = ?";
		PreparedStatement ps = con.prepareStatement(getrole);
		ps.setString(1, this.getEmail());
		
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()) {
			return rs.getInt("isadmin") == 1 ? "admin" : "user";
		}else {
			return null;
		}
	}
	
	public User(int id , String uname , String mail , String password , boolean a) {
		
		this.userid = id;
		this.username = uname;
		this.email = mail;
		this.password = password;
		this.isadmin = a;
		
	}
	
	public User(String uname , String mail , String password ) {
			
			this.username = uname;
			this.email = mail;
			this.password = password;
			
			
		}
	
	public User(Integer userid2) {
		this.userid = userid2;
	}



	public int getUserid() {
		return userid;
	}
	
	public void setUserid(int userid) {
		this.userid = userid;
	}
	
	public String getUsername() {
		return username;
	}
	
	public void setUsername(String username) {
		this.username = username;
	}
	
	public String getEmail() {
		return email;
	}
	
	public void setEmail(String email) {
		this.email = email;
	}
	
	public String getPassword() {
		return password;
	}
	
	public void setPassword(String password) {
		this.password = password;
	}
	
	public boolean isIsadmin() {
		return isadmin;
	}
	
	public void setIsadmin(boolean isadmin) {
		this.isadmin = isadmin;
	}
	
		
	}
