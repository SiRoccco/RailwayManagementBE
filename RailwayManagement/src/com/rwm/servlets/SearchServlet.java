package com.rwm.servlets;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rwm.customexceptions.CustomException;
import com.rwm.pojo.SearchPojo;
import com.rwm.searchutils.SearchUtil;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class SearchServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;
	protected JSONObject responsebody = new JSONObject();
	
	public SearchServlet() {
		// TODO Auto-generated constructor stub
	}
	
	public void init() {

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");
	}
	
	protected void doGet(HttpServletRequest request , HttpServletResponse response) throws IOException {
		
		try {
			SearchPojo sp = new SearchPojo(request.getParameter("source") , request.getParameter("destination") , request.getParameter("date"));
			
			SearchUtil.checkParamSearch(sp);
			
			JSONArray searchresults = SearchUtil.getTripInfo(sp);
			
			JSONObject jobj = new JSONObject();
			
			jobj.put("trains", searchresults);
			
			responsebody.put("data" , jobj);
			responsebody.put("success", true);
			responsebody.put("msg", "Request Processed");
			response.getWriter().println(responsebody.toString());
		} catch (CustomException | ClassNotFoundException | SQLException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responsebody.clear();
			responsebody.put("success", false);
			responsebody.put("msg", e.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
		}
		
	}
	
}
