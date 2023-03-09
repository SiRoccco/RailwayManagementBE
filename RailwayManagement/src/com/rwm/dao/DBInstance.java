package com.rwm.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.SQLException;

import com.rwm.customexceptions.CustomException;

public class DBInstance {

	private static DBInstance connectst;
	
	private static String url = "jdbc:mysql://localhost/raildb";
	private static String username = "root";
	private static String password = "zoho12";
	
	private static Connection con;
	
	private DBInstance() throws ClassNotFoundException, SQLException, CustomException {
		
		Class.forName("com.mysql.cj.jdbc.Driver");
		con = DriverManager.getConnection(url , username , password);
		
		if(con == null) {
			throw new CustomException("Could not connect to db");
		}
	}
	
	public static DBInstance getInstance() throws ClassNotFoundException, SQLException, CustomException{
		
		if(connectst == null) {
			connectst = new DBInstance();
		}
		
		return connectst;
		
	}
	
	
	public Connection getConnection() throws SQLException, ClassNotFoundException {

		return con;
	}
	
	
}
