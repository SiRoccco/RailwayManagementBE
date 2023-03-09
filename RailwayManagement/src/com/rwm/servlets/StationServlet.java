package com.rwm.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rwm.beans.Station;

import com.rwm.customexceptions.CustomException;
import com.rwm.dao.RwmDao;
import com.rwm.serveltutils.InputStreamReader;
import com.rwm.stationutils.StationUtil;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class StationServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;

	protected JSONObject responsebody = new JSONObject();

	private RwmDao dao;

	public StationServlet() {
		dao = new RwmDao();
	}

	public void init() {

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {
		String jsondata = InputStreamReader.RequestStreamReader(request.getInputStream());

		JSONObject stationdata = new JSONObject(jsondata);

		Station station = new Station(stationdata.getInt("stationid"), stationdata.getString("stationname"),
				stationdata.getInt("posx"), stationdata.getInt("posy"));

		try {
			boolean res = dao.updateStation(station);
			if (res) {
				responsebody.put("success", true);
				responsebody.put("msg", "Request Processed");
				response.getWriter().println(responsebody.toString());
			} else {
				throw new CustomException("Could not update information");
			}
		} catch (ClassNotFoundException | SQLException | CustomException e) {
			responsebody.put("success", false);
			responsebody.put("msg", e.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		}
	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		responsebody.clear();
		
		Integer sid = null;

		try {
			sid = Integer.parseInt(request.getParameter("sid"));
		} catch (Exception e) {
			e.printStackTrace();

		}

		try {
			JSONArray stationdata = StationUtil.getStations(sid);
			
			JSONObject data = new JSONObject().put("stations", stationdata);

			responsebody.put("data", data);
			responsebody.put("success", true);
			responsebody.put("msg", "Request Processed");
			response.getWriter().println(responsebody.toString());

		} catch (ClassNotFoundException | SQLException | CustomException e) {
			
			responsebody.put("success", false);
			responsebody.put("msg", e.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		}

	}

	@SuppressWarnings("unused")
	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		responsebody.clear();
		
		String requestdata = InputStreamReader.RequestStreamReader(request.getInputStream());
		
		JSONObject jsondata = new JSONObject(requestdata);
		
		System.out.println(jsondata.toString());
		
		Integer stationid = jsondata.getInt("sid");
//		try {
//			stationid = Integer.parseInt(request.getParameter("sid"));
//
//		} catch (Exception e) {
//			responsebody.put("success", false);
//			responsebody.put("msg", "Station ID cannot be string");
//			response.getWriter().println(responsebody.toString());
//			return;
//		}

		if (stationid == null) {
			responsebody.put("success", false);
			responsebody.put("msg", "Station ID is mandatory");
			response.getWriter().println(responsebody.toString());
		} else {
			try {
				Station station = new Station(stationid);
				
				boolean res = dao.deleteStation(station);
				if (res) {
					responsebody.put("success", true);
					responsebody.put("msg", "Request Processed");
					response.getWriter().println(responsebody.toString());
				} else {
					throw new CustomException("Could not delete station");
				}
			} catch (SQLException | CustomException | ClassNotFoundException e) {
				responsebody.put("success", false);
				responsebody.put("msg", e.getLocalizedMessage());
				response.getWriter().println(responsebody.toString());
				e.printStackTrace();
			}
		}

	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {
		
		responsebody.clear();
		
		String requestbody = InputStreamReader.RequestStreamReader(request.getInputStream());

		JSONObject stationdata = new JSONObject(requestbody);

		Station station = new Station(stationdata.getString("stationname"), stationdata.getInt("posx"),
				stationdata.getInt("posy"));

		StationUtil.checkparamStation(station);

		try {
			boolean res = dao.insertStation(station);
			if (res) {
				responsebody.put("success", true);
				responsebody.put("msg", "Request Processed");
				response.getWriter().println(responsebody.toString());
			} else {
				throw new CustomException("Coult not add station");
			}
		} catch (ClassNotFoundException | SQLException | CustomException e) {
			responsebody.put("msg", e.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		}

	}

}
