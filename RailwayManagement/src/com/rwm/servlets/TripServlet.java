package com.rwm.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import com.google.gson.Gson;
import com.rwm.beans.Trip;
import com.rwm.customexceptions.CustomException;
import com.rwm.dao.RwmDao;
import com.rwm.pojo.StationPojo;
import com.rwm.serveltutils.InputStreamReader;
import com.rwm.triputils.TripUtil;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TripServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected JSONObject responsebody = new JSONObject();

	private RwmDao dao;

	public TripServlet() {
		dao = new RwmDao();
	}

	public void init() {

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");
	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		Integer tripid = null;

		try {
			tripid = Integer.parseInt(request.getParameter("tripid"));
		} catch (Exception e) {
			e.printStackTrace();
		}
		String tripname = request.getParameter("tripname");

		try {
			try {

				dao.deleteTrip(tripid, tripname);

				responsebody.put("success", true);
				responsebody.put("msg", "Request Processed");
				response.getWriter().println(responsebody.toString());
			} catch (CustomException e) {
				// TODO Auto-generated catch block
				e.printStackTrace();
				responsebody.put("success", false);
				responsebody.put("msg", e.getLocalizedMessage());
				response.getWriter().println(responsebody.toString());
			}

		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		} catch (SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		Integer tripid = null;

		try {
			tripid = Integer.parseInt(request.getParameter("tripid"));
			System.out.println(tripid);
		} catch (Exception e) {
			e.printStackTrace();

		}

		try {

			JSONArray trips = TripUtil.getTrips(tripid);
			
			JSONObject data = new JSONObject().put("trips", trips);
			
			responsebody.put("data", data);
			responsebody.put("success", true);
			responsebody.put("msg", "Request Processed");

			response.getWriter().println(responsebody.toString());

		} catch (ClassNotFoundException | SQLException e) {

			responsebody.put("success", false);
			responsebody.put("msg", e.getLocalizedMessage());

			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String requestdata = InputStreamReader.RequestStreamReader(request.getInputStream());

		
			JSONObject tripdata = new JSONObject(requestdata);
			try {
				Trip trip = TripUtil.checkParam(tripdata);
				dao.insertTrip(trip);
				
				responsebody.put("success", true);
				responsebody.put("msg", "Request Processed");
				response.getWriter().println(responsebody.toString());
			}catch(Exception e) {
				e.printStackTrace();
				responsebody.put("success", false);
				responsebody.put("msg", e.getLocalizedMessage());
				response.getWriter().println(responsebody.toString());
			}
			
		

	}
}
