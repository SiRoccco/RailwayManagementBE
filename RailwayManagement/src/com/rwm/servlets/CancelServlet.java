package com.rwm.servlets;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rwm.bookingutils.CancelUtil;
import com.rwm.customexceptions.CustomException;
import com.rwm.serveltutils.InputStreamReader;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CancelServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	protected JSONObject responsebody = new JSONObject();
	
	public CancelServlet() {
		// TODO Auto-generated constructor stub
	}
	
	public void init() {

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");
	}
	
	protected void doPost(HttpServletRequest request , HttpServletResponse response) throws IOException {
		String requestdata = InputStreamReader.RequestStreamReader(request.getInputStream());
		JSONObject cancelinfo = new JSONObject(requestdata);
		
		try {
			CancelUtil.cancelTickets(cancelinfo);
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
