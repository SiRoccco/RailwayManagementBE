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

public class LoginServlet extends HttpServlet{
	
	private static final long serialVersionUID = 1L;

	private RwmDao dao = new RwmDao();
	
	protected JSONObject responsebody = new JSONObject();
	
	public LoginServlet() {
		// TODO Auto-generated constructor stub
	}
	
	public void init() {

		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");
	}
	
	protected void doPost(HttpServletRequest request , HttpServletResponse response) throws IOException {
		
		String requestdata = InputStreamReader.RequestStreamReader(request.getInputStream());
		
		JSONObject logininfo = new JSONObject(requestdata);
		
		User u = new User();
		
		u.setEmail(logininfo.getString("email"));
		u.setPassword(logininfo.getString("password"));
		
		try {
			responsebody.clear();
			
			UserUtil.checkParamLogin(u);
			
			User user =dao.checklogininfo(u);
			
			String token = UserUtil.generateToken(user);
			
			responsebody.put("authtoken", token);
			responsebody.put("username", user.getUsername());
			responsebody.put("userid", user.getUserid());
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
