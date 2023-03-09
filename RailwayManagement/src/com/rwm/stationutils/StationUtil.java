package com.rwm.stationutils;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rwm.beans.Station;
import com.rwm.customexceptions.CustomException;
import com.rwm.dao.RwmDao;

public class StationUtil {
	
	private static RwmDao dao = new RwmDao();
	
	public void init() {
		
	}
	
	public StationUtil() {
		
	}

	public static void checkparamStation(Station station) {
		
		
	}
	
	public static JSONObject getJSONObject(Station st) {
		
		JSONObject station = new JSONObject(st);
		
		return station;
	}
	
	public static JSONArray getStations(Integer stationid) throws ClassNotFoundException, SQLException, CustomException {
		
		JSONArray stations = new JSONArray();
		
		if(stationid == null) {
			List<Station> stationlist = dao.getAllStations();
			
			for(Station s : stationlist) {
				
				JSONObject stationobj = StationUtil.getJSONObject(s);
				
				stations.put(stationobj);
				
			}
			
			return stations;
			
		}else {
			
			Station st = dao.getStation(stationid);
			
			JSONObject stationobj = StationUtil.getJSONObject(st);
			
			stations.put(stationobj);
			
			return stations;
		}
		
	}
}
