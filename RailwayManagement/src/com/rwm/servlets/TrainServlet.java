package com.rwm.servlets;

import java.io.IOException;
import java.sql.SQLException;
import java.util.List;
import java.util.Scanner;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rwm.beans.Train;
import com.rwm.customexceptions.CustomException;
import com.rwm.dao.RwmDao;
import com.rwm.pojo.SeatPojo;
import com.rwm.trainutils.TrainUtil;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class TrainServlet extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private RwmDao dao;

	public TrainServlet() {
		dao = new RwmDao();
	}

	protected void doPut(HttpServletRequest request, HttpServletResponse response) throws IOException {

		StringBuilder sb = new StringBuilder();
		Scanner sr = new Scanner(request.getInputStream());

		JSONObject responsebody = new JSONObject();

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");

		while (sr.hasNext()) {
			sb.append(sr.nextLine());
		}

		String jsondata = sb.toString();
		JSONObject requestbody = new JSONObject(jsondata);

		Train train = new Train(requestbody.getInt("trainid"), requestbody.getString("trainname"),
				requestbody.getInt("coaches"), requestbody.getInt("seatspercoach"));

		try {
			TrainUtil.checkparamTrain(train);
			dao.updateTrain(train);
			responsebody.put("success", true);
			responsebody.put("msg", "Request Processed");
			responsebody.put("data", jsondata);
			response.getWriter().println(responsebody.toString());
		} catch (ClassNotFoundException | SQLException | CustomException e) {
			responsebody.put("msg", e.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		}

	}

	protected void doDelete(HttpServletRequest request, HttpServletResponse response) throws IOException {
		int tid = Integer.parseInt(request.getParameter("tid"));

		JSONObject responsebody = new JSONObject();

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");

		boolean res = false;

		try {
			res = dao.deleteTrain(tid);
			responsebody.put("success", res);
			responsebody.put("msg", "Request Processed");
			response.getWriter().println(responsebody.toString());

		} catch (ClassNotFoundException | SQLException | CustomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responsebody.put("msg", e.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
		}
		System.out.println(tid);
	}

	protected void doPost(HttpServletRequest request, HttpServletResponse response) throws IOException {

		StringBuilder sb = new StringBuilder();
		Scanner sr = new Scanner(request.getInputStream());

		JSONObject responsebody = new JSONObject();

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");

		while (sr.hasNext()) {
			sb.append(sr.nextLine());
		}

		String jsondata = sb.toString();
		JSONObject requestbody = new JSONObject(jsondata);

		if (requestbody.getString("trainname") == null || requestbody.getString("trainname") == "") {
			responsebody.put("msg", "Train Name cannot be empty");
			response.getWriter().println(responsebody.toString());
			// int ts = requestbody.getInt("coaches");
		}
//		} else if(requestbody.isNull("coaches")) {
//			responsebody.put("msg", "Total Seats cannot be empty");
//			response.getWriter().println(responsebody.toString());
//		}
		else {
			Train train = new Train(requestbody.getString("trainname"), requestbody.getInt("coaches"),
					requestbody.getInt("seatspercoach"));

			try {
				dao.insertTrain(train);

				responsebody.put("success", true);
				responsebody.put("msg", "Request Processed");
				responsebody.put("data", jsondata);
				response.getWriter().println(responsebody.toString());
			} catch (ClassNotFoundException | SQLException | CustomException e) {
				responsebody.put("msg", e.getLocalizedMessage());
				response.getWriter().println(responsebody.toString());
				e.printStackTrace();
			}

		}

	}

	protected void doGet(HttpServletRequest request, HttpServletResponse response) throws IOException {

		JSONObject responsebody = new JSONObject();

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");

		Integer tid = null;

		try {
			tid = Integer.parseInt(request.getParameter("tid"));
		} catch (Exception e) {

			responsebody.put("msg", e.getLocalizedMessage());
		}

		JSONArray traininfo;
		try {
			traininfo = TrainUtil.getTrains(tid);
			
			JSONObject data = new JSONObject().put("trains", traininfo);
			
			responsebody.put("success", true);
			responsebody.put("msg", "Request Processed");
			responsebody.put("total_records", traininfo.length());
			responsebody.put("data", data);
			
			response.getWriter().println(responsebody.toString());
		} catch (ClassNotFoundException | SQLException | CustomException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responsebody.put("success", false);
			responsebody.put("msg", e.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
		}

	}
}
