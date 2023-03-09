package com.rwm.servlets;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONObject;

import com.rwm.bookingutils.BookingUtil;
import com.rwm.customexceptions.CustomException;
import com.rwm.dao.RwmDao;
import com.rwm.pojo.BookingPojo;
import com.rwm.serveltutils.InputStreamReader;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class BookingServlet extends HttpServlet{

	protected JSONObject responsebody = new JSONObject();
	
	private static final long serialVersionUID = 1L;


	public BookingServlet() {
		// TODO Auto-generated constructor stub
	}
	
	public void init() {

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");
	}
	
	protected void doPost(HttpServletRequest request , HttpServletResponse response) throws IOException {
		
		
		String requestdata = InputStreamReader.RequestStreamReader(request.getInputStream());
		
		JSONObject bookinginfo = new JSONObject(requestdata);
		
		BookingPojo bp = new BookingPojo(bookinginfo.getInt("userid") , bookinginfo.getInt("tripid"), bookinginfo.getJSONArray("passengers")  
				, bookinginfo.getInt("startingstationid"),bookinginfo.getInt("endingstationid") , bookinginfo.getBoolean("consecutiveseatpref") );
		
		try {
			
			BookingUtil.checkParam(bp);
			BookingUtil.startBooking(bp);
			responsebody.put("success", true);
			responsebody.put("msg", "Request Processed");
			response.getWriter().println(responsebody.toString());
		} catch (ClassNotFoundException e) {
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		} catch (SQLException e) {
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		} catch (CustomException e) {
			responsebody.put("msg", e.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		}
		
	}
	
}
