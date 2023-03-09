package com.rwm.serveltutils;

import java.util.Scanner;

import jakarta.servlet.ServletInputStream;

public class InputStreamReader {
	
	public static String RequestStreamReader(ServletInputStream sis) {
		StringBuilder sb = new StringBuilder();
		
		Scanner sr = new Scanner(sis);
		
		while(sr.hasNext()) {
			sb.append(sr.nextLine());
		}
		
		String res = sb.toString();
		
		return res;
	}

}
