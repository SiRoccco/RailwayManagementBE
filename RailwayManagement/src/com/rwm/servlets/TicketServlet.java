package com.rwm.servlets;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONException;
import org.json.JSONObject;

import com.rwm.TicketUtil.TicketUtil;
import com.rwm.customexceptions.CustomException;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TicketServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;

	public TicketServlet() {
		// TODO Auto-generated constructor stub
	}
	
	protected JSONObject responsebody = new JSONObject();

	public void init() {

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");
	}
	
	protected void doGet(HttpServletRequest request , HttpServletResponse response) throws IOException {
		
		
		Integer ticketid = null;
		Integer userid = null;
		
		try {
			ticketid = Integer.parseInt(request.getParameter("ticketid"));
			
		}catch(Exception e) {
			
		}
		try {
			
			userid = Integer.parseInt(request.getParameter("userid"));
		}catch(Exception e) {
			
		}
		
		JSONObject tickets = new JSONObject();
		
		try {
			tickets.put("tickets", TicketUtil.getTicketRoot(userid, ticketid));
			responsebody.put("data", tickets);
			responsebody.put("success", true);
			responsebody.put("msg", "Request processed");
			response.getWriter().println(responsebody.toString());
		} catch (JSONException e) {
			responsebody.clear();
			responsebody.put("success", false);
			responsebody.put("msg", "Unable to process request");
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		} catch (CustomException e) {
			// TODO Auto-generated catch block
			responsebody.clear();
			responsebody.put("success", false);
			responsebody.put("msg", e.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		}
		

	}
}
