package com.rwm.servlets;

import java.io.IOException;
import java.sql.SQLException;

import org.json.JSONObject;

import com.rwm.beans.User;
import com.rwm.customexceptions.CustomException;
import com.rwm.dao.RwmDao;
import com.rwm.serveltutils.InputStreamReader;
import com.rwm.userutil.UserUtil;

import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class RegisterServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	protected JSONObject responsebody = new JSONObject();
	
	public void init() {

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");
	}
	
	private RwmDao dao = new RwmDao(); 
	
	public RegisterServlet() {
		// TODO Auto-generated constructor stub
	}
	

	
	protected void doPost(HttpServletRequest request , HttpServletResponse response) throws IOException {
		
		String requestdata = InputStreamReader.RequestStreamReader(request.getInputStream());

		JSONObject userdata = new JSONObject(requestdata);
		
		User user = new User(userdata.getString("username") , userdata.getString("email") , userdata.getString("password"));
		
		System.out.println(user.getUsername());
		System.out.println(user.getEmail());
		
		try {
			UserUtil.checkParamUser(user);
			
			dao.insertuser(user);
			
			responsebody.put("success", true);
			responsebody.put("msg", "Request Processed");
			response.getWriter().println(responsebody.toString());
		} catch (CustomException e) {
			responsebody.put("success", false);
			responsebody.put("msg", e.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		} catch (SQLException e) {
			responsebody.put("success", false);
			responsebody.put("msg", "Unable to process request");
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			responsebody.put("success", false);
			responsebody.put("msg", "Unable to process request");
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		}
		
	}
	
}
