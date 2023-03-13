package com.rwm.filters;

import java.io.IOException;
import java.util.Enumeration;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.FilterConfig;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;

public class CorsFilter implements Filter {

	public void doDestroy() {
		
	}
	
	public void init(FilterConfig fConfig) {
		
	}
	
	public void doFilter(ServletRequest request , ServletResponse response , FilterChain fchain) throws IOException, ServletException {
		
		HttpServletResponse res = (HttpServletResponse) response;
		HttpServletRequest req = (HttpServletRequest) request;
		
		res.addHeader("Access-Control-Allow-Origin", "*");
		res.addHeader("Access-Control-Allow-Headers", "*");
		res.addHeader("Access-Control-Allow-Methods", "*");
		
		
//		Enumeration<String> headers = req.getHeaderNames();
//		
//		while(headers.hasMoreElements()) {
//			System.out.println("Header : " + headers.nextElement());
//		}
		
		System.out.println("Git Commit Check");
		
		fchain.doFilter(req, res);
	}

	
}
