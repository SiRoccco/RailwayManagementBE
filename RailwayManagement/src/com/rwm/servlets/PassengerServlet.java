package com.rwm.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.LinkedList;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rwm.beans.User;
import com.rwm.bookingutils.PassengerUtil;
import com.rwm.customexceptions.CustomException;
import com.rwm.pojo.PassengerPojo;
import com.rwm.serveltutils.InputStreamReader;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class PassengerServlet extends HttpServlet{

//	private RwmDao dao = new RwmDao();
	
	private static final long serialVersionUID = 1L;
	protected JSONObject responsebody = new JSONObject();
	
	public PassengerServlet() {
		// TODO Auto-generated constructor stub
	}
	
	public void init() {

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");
	}
	
	protected void doGet(HttpServletRequest request , HttpServletResponse response) throws IOException {
		
		System.out.println(request.getHeader("x-authorization"));
		
		Integer userid = null;
		try {
			userid = Integer.parseInt( request.getParameter("userid"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		Integer passengerid = null;
		try {
			passengerid = Integer.parseInt( request.getParameter("passengerid"));
		} catch (NumberFormatException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
		
		JSONObject data = new JSONObject();
		
		try {
			data = PassengerUtil.getPassengersRoot(userid, passengerid);
			if(data == null) {
				throw new CustomException("Error");
			}
			responsebody.put("data", data);
			responsebody.put("success", true);
			responsebody.put("msg", "Request Processed");
			response.getWriter().println(responsebody.toString());
		} catch (ClassNotFoundException e1) {
			response.getWriter().println(responsebody.toString());
			e1.printStackTrace();
		} catch (SQLException e1) {
			response.getWriter().println(responsebody.toString());
			e1.printStackTrace();
		} catch (CustomException e1) {
			// TODO Auto-generated catch block
			responsebody.put("success", false);
			responsebody.put("msg", e1.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
			e1.printStackTrace();
		}
		

	}
	
	
	
	protected void doPost(HttpServletRequest request , HttpServletResponse response) throws IOException {
		
		String requestdata = InputStreamReader.RequestStreamReader(request.getInputStream());
		
		JSONObject passengerinfo = new JSONObject(requestdata);
		
		User u = null;
		try {
			u = PassengerUtil.addPassengers(passengerinfo);
		} catch (CustomException e1) {
			responsebody.put("success", false);
			responsebody.put("msg", e1.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
			e1.printStackTrace();
		}
		
		u.setUserid(passengerinfo.getInt("userid"));
		u.setUsername(passengerinfo.getString("username"));
		
		try {
			int key = PassengerUtil.insertPassengers(u);
			if(key == -99) {
				throw new CustomException("Error");
			}
			responsebody.put("lastpassengerid", key);
			responsebody.put("success", true);
			responsebody.put("msg", "Request Processed");
			response.getWriter().println(responsebody.toString());
		} catch (ClassNotFoundException e) {
			responsebody.put("success", false);
			responsebody.put("msg", "Unable to process request");
			e.printStackTrace();
			response.getWriter().println(responsebody.toString());
		} catch (SQLException e) {
			responsebody.put("success", false);
			responsebody.put("msg", "Unable to process request");
			e.printStackTrace();
			response.getWriter().println(responsebody.toString());
		} catch (CustomException e) {
			responsebody.put("success", false);
			responsebody.put("msg", e.getLocalizedMessage());
			e.printStackTrace();
			response.getWriter().println(responsebody.toString());
		}
		
	}
	
}
