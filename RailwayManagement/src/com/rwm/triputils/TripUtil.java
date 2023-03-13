package com.rwm.triputils;

import java.sql.Connection;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashMap;
import java.util.HashSet;
import java.util.Hashtable;
import java.util.Iterator;
import java.util.LinkedHashMap;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rwm.beans.Station;
import com.rwm.beans.Trip;
import com.rwm.customexceptions.CustomException;
import com.rwm.dao.DBInstance;
import com.rwm.dao.RwmDao;
import com.rwm.pojo.StationPojo;

public class TripUtil {

	private static RwmDao dao = new RwmDao();

	public void init() {

	}

	public TripUtil() {
		
	}
	
	public static List<Station> getStations(Trip t) throws SQLException, ClassNotFoundException, CustomException{
		
//		List<Station> liststations = null;
//		
//		liststations = dao.getStationFromTrip(t);
		
		Connection con = DBInstance.getInstance().getConnection();
		
		List<Station> liststation = new ArrayList<Station>();
		
		String selectstation = "select * from Trip_Station_Rel where Trip_ID = ?";
		PreparedStatement ps = con.prepareStatement(selectstation);
		
		String getstationinfo = "select * from Station where Station_ID = ?";
		PreparedStatement ps2 = con.prepareStatement(getstationinfo);
		
		ps.setInt(1,t.getTripid());
		
		ResultSet rs = ps.executeQuery();
		
		ResultSet rs2 = null;
		
		while(rs.next()) {
			ps2.setInt(1, rs.getInt("Station_ID"));
			
			rs2 = ps2.executeQuery();
			
			rs2.next();
			
			Station sp = new Station(rs2.getInt("Station_ID") , rs2.getString("Station_Name") , rs2.getInt("PosX") , rs2.getInt("PosY"));
			liststation.add(sp);
		}
		
		
		return liststation;
	}

	@SuppressWarnings("null")
	public static Trip checkParam(JSONObject tripdata) throws CustomException {
		Trip trip = new Trip();

		try {
			trip.setTrainid(tripdata.getInt("trainid"));
			Integer costperkm = tripdata.getInt("costperkm");
			trip.setCostperkm(costperkm);
		} catch (Exception e) {
			throw new CustomException("Train ID and Cost Per Km must be a integer");
		}

//		System.out.println(tripdata.getJSONArray("data").toList());

		List<Object> triplist = tripdata.getJSONArray("data").toList();

		if (triplist.size() == 1) {
			throw new CustomException("Input only has one station... Two stations are mandatory");
		}

		JSONArray triparray = tripdata.getJSONArray("data");

		List<String> stationlist = new ArrayList<String>(); // list of all stations includes redundant stations

		HashSet<String> distinctstation = new HashSet<String>(); // list of all distinct stations

		List<LocalDateTime> arrivaltimelist = new ArrayList<LocalDateTime>(); // list of arrival times of a train at
																				// each stations
		List<LocalDateTime> departuretimelist = new ArrayList<LocalDateTime>(); // list of departure times of a train at
																				// each stations

		for (int i = 0; i < triparray.length(); i++) {

			JSONObject tempstation = triparray.getJSONObject(i); // getting each station info from the array of stations

			String tempstationname = tempstation.getString("stationname");
			stationlist.add(tempstationname);
			distinctstation.add(tempstationname);

			LocalDateTime at = null;
			LocalDateTime dt = null;

			DateTimeFormatter dtf = DateTimeFormatter.ofPattern("yyyy-MM-dd HH:mm:ss"); // formating the input string to
																						// localdatetime

			try {

				if (i == 0) {
					if (tempstation.isNull("departuretime")) {
						throw new CustomException("Departure time cannot be null for stating station");
					} else {
						arrivaltimelist.add(at);

						dt = LocalDateTime.parse(tempstation.getString("departuretime"), dtf);
						departuretimelist.add(dt);
					}
				} else if (i == triparray.length() - 1) {

					if (tempstation.isNull("arrivaltime")) {
						throw new CustomException("Arrival time cannot be null for final station");
					} else {
						at = LocalDateTime.parse(tempstation.getString("arrivaltime"), dtf);
						arrivaltimelist.add(at);
						departuretimelist.add(dt);
						
						
					}
				} else {

					if (tempstation.isNull("arrivaltime") || tempstation.isNull("departuretime")) {

						throw new CustomException(
								"Arrival time or departure time cannot be null for intermediate stations...");
					} else {
						try {
							at = LocalDateTime.parse(tempstation.getString("arrivaltime"), dtf);
							dt = LocalDateTime.parse(tempstation.getString("departuretime"), dtf);
							
							System.out.println("Arrival time at trip util : " + at.toString());
							System.out.println("Departure time at trip util : " + dt.toString());
							
						} catch (Exception e) {
							throw new CustomException("Please enter valid time...");
						}
						
						if(at.isBefore(LocalDateTime.now()) || dt.isBefore(LocalDateTime.now()) ) {
							throw new CustomException("Cannot add trips in the past...");
						}
						
						if(at.isAfter(at.plusWeeks(3)) || dt.isAfter(dt.plusWeeks(3))) {
							throw new CustomException("Trips cannot be added ahead of 3 weeks from now...");
						}
						
						arrivaltimelist.add(at);
						departuretimelist.add(dt);
					}

				}

			} catch (Exception e) {
				e.printStackTrace();
				throw new CustomException(e.getLocalizedMessage());
			}

			System.out.println(tempstation.get("stationname"));

		}
		System.out.println("Arrival Time list size : " + arrivaltimelist.size());

		for (LocalDateTime d : arrivaltimelist) {
			System.out.println(d);
		}

		for (int i = 1; i < arrivaltimelist.size(); i++) { // validation of arrival and departure time between different
															// stations

			if (arrivaltimelist.get(i).isBefore(departuretimelist.get(i - 1))) {
				throw new CustomException(
						"Arrival Time of station " + (i - 1) + "cannot be before departure time of station " + i);
			}
		}

		for (int i = 1; i < triparray.length() - 2; i++) { // arrival and departure time cannot be for a specific
															// station

			if (arrivaltimelist.get(i).isEqual(departuretimelist.get(i))) {
				throw new CustomException("Arrival Time and Departure Time cannot be same");
			}
		}

//		for(i=0 ; i < departuretimelist.size() ; i++) {
//			if(departuretimelist.get(i).isAfter(arrivaltimelist.get(i+1))){
//				throw new CustomException("Departure Time of station " + (i+1) + "cannot be before departure time of station " + (i - 1) );
//			}
//		}

		for (String s : distinctstation) { // check for duplicate stations

			if (Collections.frequency(stationlist, s) > 1) {
				throw new CustomException("Stations cannot be added more than once");
			}
		}

		trip.setTripname(stationlist.get(0) + '_' + stationlist.get(triparray.length() - 1));

//		HashMap<String , List<LocalDateTime>> arrival_dept_time = new HashMap<String , List<LocalDateTime>>();
//		arrival_dept_time.put("arrivaltimelist", arrivaltimelist);
//		arrival_dept_time.put("departuretimelist", departuretimelist);

		LinkedHashMap<String, HashMap<String, LocalDateTime>> stationdata = new LinkedHashMap<String, HashMap<String, LocalDateTime>>();

		for (int i = 0; i < stationlist.size(); i++) {

			HashMap<String, LocalDateTime> type_time = new HashMap<String, LocalDateTime>();
			type_time.put("arrivaltime", arrivaltimelist.get(i));
			type_time.put("departuretime", departuretimelist.get(i));
			System.out.println(stationlist.get(i));
			stationdata.put(stationlist.get(i), type_time);
		}

		trip.setStationdata(stationdata);

		return trip;
	}

	public static JSONObject getTripObjectFromPOJO(Trip trip) {

		Iterator<StationPojo> stationiterator = trip.getStationlist().iterator();

		JSONObject singletrip = new JSONObject();

		singletrip.put("tripid", trip.getTripid());

		singletrip.put("trainid", trip.getTrainid());

		singletrip.put("costperkm", trip.getCostperkm());
		
		singletrip.put("tripname", trip.getTripname());

		JSONArray stationarray = new JSONArray();

		while (stationiterator.hasNext()) {
			JSONObject singlestation = new JSONObject();

			StationPojo aset = stationiterator.next();

			singlestation.put("stationid", aset.getStationid());
			singlestation.put("order", aset.getOrder());
			singlestation.put("departuretime",
					aset.getDeparturetime() == null ? JSONObject.NULL : aset.getDeparturetime());
			singlestation.put("arrivaltime", aset.getArrivaltime() == null ? JSONObject.NULL : aset.getArrivaltime());

			stationarray.put(singlestation);

		}

		singletrip.put("stations", stationarray);

		return singletrip;
		// {}
	}

	public static JSONArray getTrips(Integer id) throws ClassNotFoundException, SQLException {

		JSONArray triparray = new JSONArray();

		if (id == null) {
			List<Trip> trips = dao.getAllTrips();

			for (Trip t : trips) {

				JSONObject tripobj = TripUtil.getTripObjectFromPOJO(t);

				triparray.put(tripobj);
			}

			return triparray; // [{},{},{}]
		} else {

			Trip t = dao.getTrip(id);

			JSONObject tobj = TripUtil.getTripObjectFromPOJO(t);

			triparray.put(tobj);

			return triparray;

		}

		// [{}]
	}

}
