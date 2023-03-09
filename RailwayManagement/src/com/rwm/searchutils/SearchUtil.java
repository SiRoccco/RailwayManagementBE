package com.rwm.searchutils;

import java.sql.SQLException;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rwm.customexceptions.CustomException;
import com.rwm.dao.RwmDao;
import com.rwm.pojo.SearchPojo;
import com.rwm.pojo.SearchResult;


public class SearchUtil {
	
	private static RwmDao dao = new RwmDao();
	
	public SearchUtil() {
		// TODO Auto-generated constructor stub
	}
	
	
	public static void checkParamSearch(SearchPojo sp) throws CustomException {
		
		LocalDateTime date = sp.getDate();
		
		if(date.isBefore(date.minusDays(1))) {
			throw new CustomException("Select dates from " + LocalDateTime.now());
		}
		
		if(sp.getSource().equals(sp.getDestination()) || sp.getDestination().equals(sp.getSource())) {
			throw new CustomException("Source and Destination cannot be same");
		}
		
		if(sp.getSource().equals("") || sp.getSource() == null) {
			throw new CustomException("Source station cannot be empty");
			
		}
		
		if(sp.getDestination().equals("") || sp.getDestination() == null) {
			throw new CustomException("Destination station cannot be empty");
		}
		
		if(sp.getDate() == null) {
			throw new CustomException("Date cannot be empty");
		}
		
	}
	
	public static JSONObject getTripInfoObject(SearchResult sr) {
		
		
		
		JSONObject res = new JSONObject(sr);
		
		res.put("duration", sr.getDuration());
		res.put("trainname", sr.getTrainname());
		res.put("trainid", sr.getTrainid());
		
		JSONObject trip = new JSONObject();
		trip.put("tripid", sr.gettrip().get("trip_id"));
		trip.put("starting_station" , sr.gettrip().get("starting_station"));
		trip.put("ending_station" , sr.gettrip().get("ending_station"));
		res.put("trip", sr.gettrip());
		
		JSONObject source = new JSONObject();
		source.put("arrivaltime", sr.getSource().getArrivaltime() == null ? JSONObject.NULL : sr.getSource().getArrivaltime().toString());
		source.put("departuretime", sr.getSource().getDeparturetime() == null ? JSONObject.NULL : sr.getSource().getDeparturetime().toString());
		source.put("order", sr.getSource().getOrder());
		source.put("stationid", sr.getSource().getStationid());
		source.put("stationname", sr.getSource().getStationname());
		
		JSONObject destination = new JSONObject();
		destination.put("arrivaltime", sr.getDestination().getArrivaltime() == null ? JSONObject.NULL : sr.getDestination().getArrivaltime().toString());
		destination.put("departuretime", sr.getDestination().getDeparturetime() == null ? JSONObject.NULL : sr.getDestination().getDeparturetime().toString());
		destination.put("order", sr.getDestination().getOrder());
		destination.put("stationid", sr.getDestination().getStationid());
		destination.put("stationname", sr.getDestination().getStationname());
		
		res.put("source", source);
		res.put("destination", destination);
		
		
		return res;
	}

	@SuppressWarnings("unused")
	public static JSONArray getTripInfo(SearchPojo sp) throws ClassNotFoundException, SQLException, CustomException {
		
		List<SearchResult> searchresult = dao.searchtrips(sp);
		
		System.out.println(searchresult.size());
		
		
		if(searchresult.size() == 0) {
			throw new CustomException("OOPS :((( No trips found");
		}
		
		JSONArray data = new JSONArray();
		
		for(SearchResult sr : searchresult) {
			
			JSONObject obj = SearchUtil.getTripInfoObject(sr);
			
			data.put(obj);
		}
		
		return data;
	}
}
