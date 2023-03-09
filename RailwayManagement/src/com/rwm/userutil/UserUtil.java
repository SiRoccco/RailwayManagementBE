package com.rwm.userutil;

import java.security.Key;
import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.SQLException;
import java.time.Instant;
import java.util.Base64;
import java.util.Date;
import java.util.UUID;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import javax.crypto.spec.SecretKeySpec;

import com.rwm.beans.User;
import com.rwm.customexceptions.CustomException;
import com.rwm.dao.DBInstance;
import com.rwm.dao.RwmDao;

import io.jsonwebtoken.Jwts;
import io.jsonwebtoken.SignatureAlgorithm;

public class UserUtil {
	
	private static RwmDao dao = new RwmDao();
	
	public UserUtil() {
		// TODO Auto-generated constructor stub
	}
	
	public static final Pattern VALID_EMAIL_ADDRESS_REGEX =  Pattern.compile("^[A-Z0-9._%+-]+@[A-Z0-9.-]+\\.[A-Z]{2,6}$", Pattern.CASE_INSENSITIVE);

	public static boolean validate(String emailStr) {
		Matcher matcher = VALID_EMAIL_ADDRESS_REGEX.matcher(emailStr);
		return matcher.matches();
	}
	
	public static void checkParamLogin(User u) throws CustomException {
		
		if(!validate(u.getEmail()) || u.getEmail() == "" || u.getEmail() == null ) {
			throw new CustomException("Please enter valid email");
			
		}
		
		if(u.getPassword() == "" || u.getPassword() == null) {
			throw new CustomException("Please enter password");
		}
		
	}
	
	public static void checkParamUser(User u) throws CustomException, SQLException, ClassNotFoundException {
	
	if(u.getUsername().equals("") || u.getUsername() == null) {
		throw new CustomException("Please enter valid username");
	}	
	
	if(!validate(u.getEmail()) || u.getEmail().equals("") || u.getEmail() == null ) {
		throw new CustomException("Please enter valid email");
		
	}
	
	if(u.getPassword().equals("") || u.getPassword() == null) {
		throw new CustomException("Please enter valid password");
	}

	dao.checkuserexist(u);
	
	}
	
	
	public static String generateToken(User u) {
		
		String role = (u.isIsadmin()?"admin":"user");
		
		String secret = "asdfSFS34wfsdfsdfSDSD32dfsddDDerQSNCK34SOWEK5354fdgdf4";

		Key hmackey = new SecretKeySpec(Base64.getDecoder().decode(secret) , SignatureAlgorithm.HS256.getJcaName());
		
		String token = Jwts.builder().claim("username", u.getUsername()).claim("userid", u.getUserid())
				.claim("email", u.getEmail()).claim("password", u.getPassword())
				.setSubject(role)
				.setId(UUID.randomUUID().toString())
				.setIssuedAt(Date.from(Instant.now())).setExpiration(Date.from(Instant.now().plusSeconds(1200))).signWith(hmackey).compact();
		
		return token;
		
	}
}
