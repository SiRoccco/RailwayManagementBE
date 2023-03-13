package com.rwm.filters;

import java.io.IOException;
import java.security.Key;
import java.sql.SQLException;
import java.util.Base64;
import java.util.Enumeration;
import java.util.Iterator;

import javax.crypto.spec.SecretKeySpec;

import org.json.JSONObject;

import com.rwm.beans.User;
import com.rwm.customexceptions.CustomException;
import com.rwm.dao.RwmDao;

import io.jsonwebtoken.Claims;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.Jws;
import io.jsonwebtoken.Jwt;
import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;
import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;

public class AuthFilter implements Filter{
	
	protected RwmDao dao = new RwmDao();
	
	protected JSONObject responsebody = new JSONObject();
	
	public void init(FilterConfig fConfig) {
		responsebody.put("success", false);
		responsebody.put("msg", "Unable to process request");
	}
	
	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain fChain)
			throws IOException, ServletException {
		
		HttpServletRequest req = (HttpServletRequest) request;
		
		String requestheader = req.getHeader("x-authorization");
		
		System.out.println("Authtoken " + requestheader);
		
//		Enumeration<String> headers = req.getHeaderNames();
//		
//		while(headers.hasMoreElements()) {
//			System.out.println("Header : " + headers.nextElement());
//		}
		
		String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";

		Key hmackey = new SecretKeySpec(Base64.getDecoder().decode(secret) , SignatureAlgorithm.HS256.getJcaName());
		
		
		try {
			Jws<Claims> jwt = Jwts.parserBuilder().setSigningKey(hmackey).build().parseClaimsJws(requestheader);
			
//			String role = jwt.getBody().getSubject();
			
			String email = jwt.getBody().get("email").toString();
			User user = new User();
			user.setEmail(email);
			
			String role = user.getRole();
			System.out.println("Role : " + role);
			System.out.println("Subject : " + jwt.getBody().getSubject()); 
			System.out.println("User ID : " + jwt.getBody().get("userid"));
			System.out.println(req.getContextPath());
			System.out.println(req.getRequestURL());
			System.out.println(req.getRequestURI().substring(19));
			System.out.println(req.getMethod());
			
			String endpoint = req.getRequestURI().substring(19);
			
			if(role.equals("user") && (endpoint.equals("station") || endpoint.equals("search")  || endpoint.equals("ticket") || endpoint.equals("passenger")  || endpoint.equals("cancel") )) {
				req.setAttribute("userid", jwt.getBody().get("userid"));
				fChain.doFilter(req, response);
			}else if(role == "admin" && (endpoint == "station" || endpoint == "train" || endpoint == "trip")) {
				fChain.doFilter(req, response);
			}else {
				responsebody.put("success", false);
				responsebody.put("msg", "Forbidden");
				response.getWriter().println(responsebody.toString());
			}
			
//			if(role == "user" && (endpoint == "train" || endpoint == "station" || endpoint == "trip" )) {
//				responsebody.put("success", false);
//				responsebody.put("msg", "Forbidden");
//				response.getWriter().println(responsebody.toString());
//			}else if(role == "admin" && (endpoint == "search" || endpoint == "booking" || endpoint == "passenger" || endpoint == "cancel")) {
//				responsebody.put("success", false);
//				responsebody.put("msg", "Forbidden");
//				response.getWriter().println(responsebody.toString());
//			}else {
//			
//				responsebody.put("success", true);
//				responsebody.put("msg", "Logged in as " + jwt.getBody().getSubject());
//	
//				response.getWriter().println(responsebody.toString());
//				
//				fChain.doFilter(req, response);
//			}
		}catch(ExpiredJwtException eje) {
			responsebody.put("success", false);
			responsebody.put("msg", "Session Expired... Please login again");
			response.getWriter().println(responsebody.toString());
		} catch (SQLException e) {
			responsebody.put("success", false);
			responsebody.put("msg", "Unable to process request");
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		} catch (ClassNotFoundException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
			responsebody.put("success", false);
			responsebody.put("msg", e.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
		} catch (CustomException e) {
			responsebody.put("success", false);
			responsebody.put("msg", e.getLocalizedMessage());
			response.getWriter().println(responsebody.toString());
			e.printStackTrace();
		}
		
	}
	
	public void destroy() {}

}
