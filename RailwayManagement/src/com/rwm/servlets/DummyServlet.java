package com.rwm.servlets;

import java.io.IOException;

import org.json.JSONException;
import org.json.JSONObject;

import com.rwm.serveltutils.InputStreamReader;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class DummyServlet extends HttpServlet{

	private static final long serialVersionUID = 1L;
	protected JSONObject responsebody = new JSONObject();
	
	public DummyServlet() {
		// TODO Auto-generated constructor stub
	}
	
	public void init() {

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");
	}
	
	protected void doGet(HttpServletRequest request , HttpServletResponse response) throws IOException {
		
		String requestdata = InputStreamReader.RequestStreamReader(request.getInputStream());
		
		JSONObject filtermsg = new JSONObject();
		
		try {
			filtermsg = new JSONObject();
			filtermsg.accumulate("msg", "hello from dummy");
			
			response.getWriter().println(filtermsg.toString());
		}catch(JSONException e) {
			
			System.out.println(requestdata);
			filtermsg.put("servletmsg", e.getLocalizedMessage());
			response.getWriter().println(filtermsg.toString());
			
		}
		
		
	}
	
}
