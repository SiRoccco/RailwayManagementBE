package com.rwm.dao;

import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.time.Instant;
import java.time.LocalDateTime;

import java.time.ZoneOffset;
import java.util.ArrayList;

import java.util.HashMap;

import java.util.Iterator;

import java.util.LinkedList;
import java.util.List;
import java.util.Map.Entry;
import java.util.Set;
import java.util.concurrent.TimeUnit;


import org.json.JSONObject;

import com.rwm.beans.Station;
import com.rwm.beans.Train;
import com.rwm.beans.Trip;
import com.rwm.beans.User;
import com.rwm.customexceptions.CustomException;
import com.rwm.pojo.SearchPojo;
import com.rwm.pojo.SearchResult;
import com.rwm.pojo.SeatPojo;
import com.rwm.pojo.StationPojo;
import com.rwm.triputils.TripUtil;

public class RwmDao {

	private String url = "jdbc:mysql://localhost/raildb";
	private String username = "root";
	private String password = "zoho12";

	private Connection con;

	protected void connect() throws SQLException, ClassNotFoundException {
		String driver = "com.mysql.cj.jdbc.Driver";

		if (con == null || con.isClosed() == true) {
			Class.forName(driver);

			con = DriverManager.getConnection(url, username, password);
		}
	}

	protected void disconnect() throws SQLException {
		if (con != null || con.isClosed() == false) {
			con.close();
		}
	}
	
	public String getRole(String email , String password) throws SQLException, CustomException, ClassNotFoundException {
		connect();
		
		String getrole = "select isadmin from users where email = ? and password = ?";
		PreparedStatement ps = con.prepareStatement(getrole);
		ps.setString(1, email);
		ps.setString(2, password);
		
		ResultSet rs = ps.executeQuery();
		
		if(rs.next()) {
			String role = (rs.getInt("isadmin") == 1) ? "admin" : "user";
			disconnect();
			return role;
		}else {
			disconnect();
			throw new CustomException("Error gettting info");
		}
		
	}
	
	public User checklogininfo(User u) throws SQLException, CustomException, ClassNotFoundException {
		
		connect();

		String checkemail = "select * from users where email = ?";
		PreparedStatement checkemailps = con.prepareStatement(checkemail);
		checkemailps.setString(1, u.getEmail());
		
		if(checkemailps.executeQuery().next()) {
			
			String checkpswd = "select * from users where email = ? and password = ?";
			PreparedStatement checkpswdps = con.prepareStatement(checkpswd);
			checkpswdps.setString(1, u.getEmail());
			checkpswdps.setString(2, u.getPassword());
			
			ResultSet rs = checkpswdps.executeQuery();
			
			if(rs.next()) {
				User user = new User();
				
				user.setUserid(rs.getInt("user_id"));
				
				user.setUsername(rs.getString("username"));
				
				user.setIsadmin(rs.getInt("isadmin")==1 ? true :false);
				
				user.setEmail(rs.getString("email"));
				
				user.setPassword(rs.getString("password"));
				
				disconnect();
				return user;	
			}else {
				disconnect();
				throw new CustomException("Invalid email or password");			
			}
			
		}else {
			disconnect();
			throw new CustomException("Email not found");
		}
		
		
	}
	public void insertuser(User u) throws SQLException, ClassNotFoundException {
		connect();
		
		
		
		String insertuser = "insert into users ( username , email , password) value ( ? , ? , ?) ";
		PreparedStatement ps = con.prepareStatement(insertuser);
		
		ps.setString(1, u.getUsername());
		ps.setString(2, u.getEmail());
		ps.setString(3, u.getPassword());
		
		con.setAutoCommit(false);
		
		try {
			ps.executeUpdate();
			con.commit();
		}catch(SQLException e) {
			e.printStackTrace();
			try {
				con.rollback();
				disconnect();
				throw e;
			}catch(SQLException e1) {
				e1.printStackTrace();
				disconnect();
				throw e1;
			}
		}
		disconnect();
	}
	
	public void checkuserexist(User u) throws SQLException, CustomException, ClassNotFoundException {
		connect();
		
		String checkusernameexist = "select * from users where Username = ?";
		PreparedStatement ps = con.prepareStatement(checkusernameexist);
		ps.setString(1,u.getUsername());
		
		if(ps.executeQuery().next()) {
			disconnect();
			throw new CustomException("Username already taken");
		}
		
		String checkemailexist  = "select * from users where Email = ?";
		PreparedStatement ps1 = con.prepareStatement(checkemailexist);
		ps1.setString(1, u.getEmail());
		
		if(ps1.executeQuery().next()) {
			disconnect();
			throw new CustomException("Email already taken");
		}
		
		disconnect();
		
	}
	
	public List<Station> getStationFromTrip(Trip t) throws SQLException, ClassNotFoundException{
		connect();
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
		disconnect();
		return liststation;
	}
	
	public float calcfare(Trip t , int srcstationid , int deststationid) throws SQLException, ClassNotFoundException, CustomException {
		
//		String selectstation = "select Station_ID from Trip_Station_Rel where trip_id = ?";
//		
//		PreparedStatement ps = con.prepareStatement(selectstation);
//		ps.setInt(1, tripid);
//		ResultSet rs = ps.executeQuery();
		connect();
		
		List<Station> liststation = TripUtil.getStations(t);
		
		float fare = 0;
		boolean flag = false;
		int currx , curry;
		int prevx = 0 , prevy = 0;
		
		String getpos = "select * from station where station_id = ? ";
		PreparedStatement ps2 = con.prepareStatement(getpos);
		
		for(Station s : liststation) {
			
			if(s.getStationID() == srcstationid) {
				flag = true;
				ps2.setInt(1, srcstationid);
				ResultSet rs2 = ps2.executeQuery();
				rs2.next();
				prevx = rs2.getInt("PosX");
				prevy = rs2.getInt("PosY");
				
				System.out.println(s.getStationID());
				
			}else if(s.getStationID() == deststationid) {
				ps2.setInt(1, deststationid);
				
				ResultSet rs2 = ps2.executeQuery();
				rs2.next();
				currx = rs2.getInt("PosX");
				curry = rs2.getInt("PosY");
				
				float distance = (float) Math.sqrt( (Math.pow((currx-prevx), 2)  + Math.pow((curry-prevy), 2)));
				
				fare += distance * t.getCostperkm();
				
				System.out.println(s.getStationID());
				
				break;
			}else {
				if(flag) {
					
					ps2.setInt(1,s.getStationID() );
					
					ResultSet rs2 = ps2.executeQuery();
					rs2.next();
					currx = rs2.getInt("PosX");
					curry = rs2.getInt("PosY");
					
					float distance = (float) Math.sqrt( (Math.pow((currx-prevx), 2)  + Math.pow((curry-prevy), 2)));
					
					fare += distance * t.getCostperkm();
					
					prevx = currx;
					prevy = curry;
					
					
					System.out.println(s.getStationID());
				}
			}
			
			
		}
		return fare;
	}

	public List<SearchResult> searchtrips(SearchPojo sp) throws ClassNotFoundException, SQLException, CustomException {
		connect();
		
		Long starttime = sp.getDate().toInstant(ZoneOffset.UTC).toEpochMilli();
		
		Long endtime = sp.getDate().plusHours(24).toInstant(ZoneOffset.UTC).toEpochMilli();
		
		String getsrcid = "select * from station where station_name = ?";
		String getdestid = "select * from station where station_name = ?";
		String search_trips = "select t1.Station_ID as src_sid , t1.Trip_ID , t1.`Order` as src_order , t1.Arrival_Time as src_arr_time ,"
				+ "t1.Departure_Time as src_dept_time , t2.Station_ID as dest_sid , t2.`Order` as dest_order , t2.Arrival_Time as dest_arr_time ,"
				+ "t2.Departure_Time as dest_dept_time"
				+ " from ( select * from Trip_Station_Rel where Station_ID = ? ) t1 inner join "
				+ "(select * from Trip_Station_Rel where Station_ID = ?)  t2 on t1.Trip_ID = t2.Trip_ID where (t1.`Order` < t2.`Order`) and (t1.Departure_Time between ? and ?)";
		
		PreparedStatement ps1 = con.prepareStatement(getsrcid);
		PreparedStatement ps2 = con.prepareStatement(getdestid);
		PreparedStatement ps3 = con.prepareStatement(search_trips);
		
		ps1.setString(1, sp.getSource());
		ps2.setString(1, sp.getDestination());

		ResultSet rs = ps1.executeQuery();
		
		int srcid , destid;
		
		if (rs.next()) {
			srcid = rs.getInt("Station_ID");
			ps3.setInt(1, srcid);
		} else {
			throw new CustomException("Source station not found");
		}

		rs.close();

		rs = ps2.executeQuery();

		if (rs.next()) {
			destid = rs.getInt("Station_ID");
			ps3.setInt(2, destid);
		} else {
			throw new CustomException("Destination station not found");
		}
		
		ps3.setLong(3, starttime);
		
		ps3.setLong(4, endtime);
		
		ResultSet rs1 = ps3.executeQuery();
		
		List<SearchResult> listsr = new ArrayList<SearchResult>();
		
		while (rs1.next()) {
			
			System.out.println("Difference bw dest arr time and src dept time " + (rs1.getLong("dest_arr_time") - rs1.getLong("src_dept_time")));
			
			int duration = (int) TimeUnit.MILLISECONDS.toMinutes( rs1.getLong("dest_arr_time") - rs1.getLong("src_dept_time") );
			
			Trip t = this.getTrip(rs1.getInt("Trip_ID"));
			
			JSONObject trip = new JSONObject();
			
			trip.put("trip_id", t.getTripid());
			
			ResultSet startingstationname = con.prepareStatement("select * from station where station_id = " + t.getStationlist().peekFirst().getStationid()).executeQuery();
			ResultSet finalstationname = con.prepareStatement("select * from station where station_id = " + t.getStationlist().peekLast().getStationid()).executeQuery();
			
			startingstationname.next();
			finalstationname.next();
			
			trip.put("starting_station", startingstationname.getString("Station_Name"));
			trip.put("ending_station", finalstationname.getString("Station_Name"));
			
//			System.out.println(trip.toString());
			
			SearchResult sr = new SearchResult();
			
			sr.setDuration(duration);
			
			sr.settrip(trip);														//set trip
			
			String trip_train = "select * from Trip left join Trains on Trains.Train_ID = Trip.Train_ID where Trip_ID = ?";		//get trainid
			PreparedStatement ps4 = con.prepareStatement(trip_train);
			ps4.setInt(1, rs1.getInt("Trip_ID"));
			ResultSet rs2 = ps4.executeQuery();

			if (rs2.next()) {
				t.setCostperkm(rs2.getInt("CostPerKm"));
				
				float fare = this.calcfare(t, srcid, destid);
				
				sr.setFare(fare);
				
				sr.setTrainid(rs2.getInt("Train_ID"));					//set train id

//				String getavailseats = "select Train_ID , count(*) as available_seats  from Seats WHERE Ticket_ID is null and Train_ID = ? group by Train_ID";
//				PreparedStatement ps6 = con.prepareStatement(getavailseats);
//				ps6.setInt(1, rs2.getInt("Train_ID"));
//				
//				ResultSet seatsrs = ps6.executeQuery(); 
//
//				if (seatsrs.next()) {
//
//					if (seatsrs.getInt("available_seats") == 0) {
//						throw new CustomException("OOPS :((( No seats available");
//					} else {
//						sr.setAvailableseats(seatsrs.getInt("available_seats")); 		//set available seats
//					}
//				}

				sr.setTrainname(rs2.getString("Train_Name"));							//set train name
			} else {
				throw new CustomException("OOPS :((( No trains found for this trip");
			}
			
			String gettationname = "select station_name from station where station_id = ?";
			PreparedStatement ps7 = con.prepareStatement(gettationname);
			
			ResultSet stationnamers;
			
			ps7.setInt(1, srcid);
			
			stationnamers = ps7.executeQuery();
			
			stationnamers.next();
			
			StationPojo source = new StationPojo(rs1.getInt("src_sid"), rs1.getInt("src_order"),
					rs1.getLong("src_arr_time"), rs1.getLong("src_dept_time") , stationnamers.getString("Station_Name"));
			
			ps7.setInt(1, destid);
			
			stationnamers = ps7.executeQuery();
			
			stationnamers.next();
			
			StationPojo dest = new StationPojo(rs1.getInt("dest_sid"), rs1.getInt("dest_order"),
					rs1.getLong("dest_arr_time"), rs1.getLong("dest_dept_time"),stationnamers.getString("Station_Name"));
			
			sr.setSource(source);														//set source dets
			sr.setDestination(dest);													//set dest dets
			sr.setavailableseats();
			
			listsr.add(sr);

		} 
//		else {
//			disconnect();
//			throw new CustomException("OOPS :((( No trips found");
//		}
		disconnect();
		return listsr;
	}

	public boolean deleteTrip(Integer tripid, String tripname)
			throws ClassNotFoundException, SQLException, CustomException {
		connect();

		boolean res = false;

		if (tripname == null) {
			String deletetrip = "delete from trip where Trip_ID = ?";

			con.setAutoCommit(false);

			PreparedStatement ps = con.prepareStatement(deletetrip);
			try {

				ps.setInt(1, tripid);

				this.deletetripdata(tripid);

				res = ps.executeUpdate() > 0;

				con.commit();

				if (!res) {
					throw new CustomException("Trip Not Found");
				}

				return res;
			} catch (SQLException e) {

				try {
					con.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}

		} else {

			String getid = "select * from Trip where Trip_Name = ?";
			String deletetripbyid = "delete from trip where Trip_ID = ?";

			PreparedStatement ps = con.prepareStatement(getid);
			ps.setString(1, tripname);

			PreparedStatement ps2 = con.prepareStatement(deletetripbyid);

			ResultSet rs = ps.executeQuery();

			if (rs.next()) {
				tripid = rs.getInt("Trip_ID");
			} else {
				throw new CustomException("Trip Not Found");
			}
			con.setAutoCommit(false);

			try {

				ps2.setInt(1, tripid);

				this.deletetripdata(tripid);

				res = ps2.executeUpdate() > 0;

				con.commit();

				return res;
			} catch (SQLException e) {

				try {
					con.rollback();
				} catch (SQLException e1) {
					e1.printStackTrace();
				}
			}

		}
		return false;
	}

	private boolean deletetripdata(Integer tripid) throws SQLException {

		boolean res = false;

		con.setAutoCommit(false);

		String deletetripdetails = "delete from Trip_Station_Rel where Trip_ID = ?";

		PreparedStatement ps = con.prepareStatement(deletetripdetails);

		ps.setInt(1, tripid);

		try {

			res = ps.executeUpdate() > 0;

			con.commit();

			return res;
		} catch (SQLException e) {

			try {
				con.rollback();

			} catch (SQLException e1) {
				throw e1;
			}

			throw e;
		}

	}

	public List<Trip> getAllTrips() throws ClassNotFoundException, SQLException {
		connect();

		List<Trip> triplist = new ArrayList<Trip>();

		String getalltrips = "select * from Trip ";

		Statement st = con.createStatement();
		ResultSet rs = st.executeQuery(getalltrips);

		while (rs.next()) {
			Trip trip = new Trip();

			trip.setTripid(rs.getInt("Trip_ID"));
			trip.setTrainid(rs.getInt("Train_ID"));
			trip.setCostperkm(rs.getInt("CostPerKm"));
			triplist.add(this.gettripdetails(trip));

		}

		return triplist;

	}

	public boolean insertTrip(Trip trip) throws SQLException, ClassNotFoundException, CustomException {
		connect();

		boolean res = false;

		String checktrain = "select * from trains where Train_ID = ?";

		PreparedStatement pst = con.prepareStatement(checktrain);
		pst.setInt(1, trip.getTrainid());

		if (!pst.executeQuery().next()) {
			throw new CustomException("Train does not exist");
		}

//		String checkexistingtrip = "select * from Trip where Trip_Name = ? and Train_ID = ?";
//		PreparedStatement ps0 = con.prepareStatement(checkexistingtrip);
//		ps0.setString(1, trip.getTripname());
//		ps0.setInt(2, trip.getTrainid());
//
//		if (ps0.executeQuery().next()) {
//			throw new CustomException("Trip already exists");
//		}

		String inserttrip = "insert into Trip ( Trip_Name , Train_ID , CostPerKm) ";
		inserttrip += "value (? , ? , ?)";

		PreparedStatement ps = con.prepareStatement(inserttrip, Statement.RETURN_GENERATED_KEYS);
		ps.setString(1, trip.getTripname());
		ps.setInt(2, trip.getTrainid());
		ps.setInt(3, trip.getCostperkm());

		try {
			con.setAutoCommit(false);

			ps.execute();

			ResultSet rs = ps.getGeneratedKeys();

			if (rs.next()) {
				int tripid = rs.getInt(1);
				trip.setTripid(tripid);
				res = this.insertTripData(trip);

				con.commit();

			}
		} catch (SQLException e) {
			try {
				con.rollback();
			} catch (SQLException e1) {
				throw e1;
			}
			throw e;
		}

		disconnect();
		return res;

	}

	public boolean checkoverlappingtrains(Trip trip) {

		return false;

	}

	private void checktrainbusy(int trainid, Set<Entry<String, HashMap<String, LocalDateTime>>> table)
			throws SQLException, CustomException {

		String checktrainbusy = "select * from Trip_Station_Rel left join Trip on Trip_Station_Rel.Trip_ID = Trip.Trip_ID"
				+ " where (Train_ID = ?) and (Arrival_Time between ? and ?) and (Departure_Time between ? and ?)";

		Entry<String, HashMap<String, LocalDateTime>> lastset = null;

		Iterator<Entry<String, HashMap<String, LocalDateTime>>> iterator = table.iterator();

		Entry<String, HashMap<String, LocalDateTime>> firstset = iterator.next();

		while (iterator.hasNext()) {
			lastset = iterator.next();
		}

		PreparedStatement ps = con.prepareStatement(checktrainbusy);

		ps.setInt(1, trainid);

		ps.setLong(2, firstset.getValue().get("departuretime").toInstant(ZoneOffset.UTC).toEpochMilli());
		
		System.out.println("Departure time at check train busy " + firstset.getValue().get("departure_time"));
		
		ps.setLong(3, lastset.getValue().get("arrivaltime").toInstant(ZoneOffset.UTC).toEpochMilli());

		ps.setLong(4, firstset.getValue().get("departuretime").toInstant(ZoneOffset.UTC).toEpochMilli());
		ps.setLong(5, lastset.getValue().get("arrivaltime").toInstant(ZoneOffset.UTC).toEpochMilli());

		if (ps.executeQuery().next()) {
			throw new CustomException("Train busy at stated time");
		}

	}

	// private void checkstationbusy(int stationid ,Entry<String, HashMap<String,
	// LocalDateTime>> theset) throws SQLException, CustomException {
	// String stationbusy = "select Station.Station_ID , Station_Name , Arrival_Time
	// , Departure_Time from Trip_Station_Rel"
	// + " join Station on Trip_Station_Rel.Station_ID = Station.Station_ID "
	// + " where Station_ID = ? and Arrival_Time = ? and DepartureTime = ?";
	//
	// PreparedStatement ps = con.prepareStatement(stationbusy);
	// ps.setInt(1, stationid);
	// ps.setLong(2,
	// theset.getValue().get("arrivaltime").toInstant(ZoneOffset.UTC).toEpochMilli());
	// ps.setLong(2,
	// theset.getValue().get("departuretime").toInstant(ZoneOffset.UTC).toEpochMilli());
	//
	// ResultSet rs = ps.executeQuery();
	//
	// if(rs.next()) {
	// throw new CustomException(rs.getString("Station_Name") + " busy at stated
	// time");
	// }
	//
	// }

	@SuppressWarnings("null")
	private boolean insertTripData(Trip trip) throws SQLException, CustomException {

		String insertripdata = "insert into Trip_Station_Rel (Trip_ID , Station_ID , Arrival_Time , Departure_Time , `Order`)";
		insertripdata += "value (? , ? , ? , ? , ?)";

		PreparedStatement ps = con.prepareStatement(insertripdata);
		ps.setInt(1, trip.getTripid());

		Set<Entry<String, HashMap<String, LocalDateTime>>> table = trip.getStationdata().entrySet();

		Iterator<Entry<String, HashMap<String, LocalDateTime>>> it = table.iterator();

		boolean res = false;

		this.checktrainbusy(trip.getTrainid(), table);

		int i = 1;
		while (it.hasNext()) {

			Entry<String, HashMap<String, LocalDateTime>> theset = it.next();

			String selectstationid = "select Station_ID from station where Station_Name = ?";
			PreparedStatement ps2 = con.prepareStatement(selectstationid);

			ps2.setString(1, theset.getKey());

			ResultSet temp = ps2.executeQuery();

			if (temp.next()) {
				ps.setInt(2, temp.getInt(("Station_ID")));
				this.checkstationbusy(temp.getInt("Station_ID"), theset);

			} else {
				throw new CustomException(theset.getKey() + " not found");
			}

//			System.out.println(theset.getKey());

			if (theset.getValue().get("arrivaltime") != null) {
				ps.setLong(3, theset.getValue().get("arrivaltime").toInstant(ZoneOffset.UTC).toEpochMilli());
				System.out.println("Arrival time at insert trip data : "+theset.getValue().get("arrivaltime"));
				long test = theset.getValue().get("arrivaltime").toInstant(ZoneOffset.UTC).toEpochMilli();
				System.out.println("Arrival time at insert trip data after coonverting to long " + LocalDateTime.ofInstant(Instant.ofEpochMilli(test), ZoneOffset.UTC ));
			} else {
				ps.setString(3, null);
			}

			if (theset.getValue().get("departuretime") != null) {
				ps.setLong(4, theset.getValue().get("departuretime").toInstant(ZoneOffset.UTC).toEpochMilli());
				System.out.println(theset.getValue().get("departuretime").toInstant(ZoneOffset.UTC).toEpochMilli());
				
				System.out.println("Departure time : "+theset.getValue().get("departuretime"));
			} else {
				ps.setString(4, null);
			}

			ps.setInt(5, i++);

			res = ps.executeUpdate() > 0;
		}

		if (!res) {
			throw new CustomException("Could not insert into tripdata");
		}

		return res;
	}

	private void checkstationbusy(int int1, Entry<String, HashMap<String, LocalDateTime>> theset) {
		
		
	}

	public Trip getTrip(int tripid) throws ClassNotFoundException, SQLException {
		connect();

		String gettrip = "select * from Trip where Trip_ID = ?";

		PreparedStatement ps = con.prepareStatement(gettrip);
		ps.setInt(1, tripid);

		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			Trip trip = new Trip();
			
			trip.setCostperkm(rs.getInt("costperkm"));
			trip.setTripid(tripid);
			trip.setTrainid(rs.getInt("Train_ID"));

			return this.gettripdetails(trip);

		}

		return null;

	}

	private Trip gettripdetails(Trip trip) throws SQLException {

		String gettripdata = "select * from Trip_Station_Rel where Trip_ID = ?";
		System.out.println(trip.getTripid());
		PreparedStatement ps = con.prepareStatement(gettripdata);
		ps.setInt(1, trip.getTripid());

		ResultSet rs = ps.executeQuery();

		LinkedList<StationPojo> splist = new LinkedList<StationPojo>();

		while (rs.next()) {
			StationPojo sp = new StationPojo();

			long arrivaltime = rs.getLong("Arrival_Time");
			long departuretime = rs.getLong("Departure_Time");

			// if (arrivaltime == 0) {
			// sp.setArrivaltime(null);
			// LocalDateTime dt =
			// LocalDateTime.ofInstant(Instant.ofEpochMilli(departuretime),
			// ZoneId.systemDefault());
			// sp.setDeparturetime(dt);
			// } else if (departuretime == 0) {
			// sp.setDeparturetime(null);
			// LocalDateTime at = LocalDateTime.ofInstant(Instant.ofEpochMilli(arrivaltime),
			// ZoneId.systemDefault());
			// sp.setArrivaltime(at);
			// } else {
			// LocalDateTime at = LocalDateTime.ofInstant(Instant.ofEpochMilli(arrivaltime),
			// ZoneId.systemDefault());
			// LocalDateTime dt =
			// LocalDateTime.ofInstant(Instant.ofEpochMilli(departuretime),
			// ZoneId.systemDefault());
			//
			// sp.setArrivaltime(at);
			// sp.setDeparturetime(dt);
			// }

			sp.setArrivaltime(arrivaltime == 0 ? null
					: LocalDateTime.ofInstant(Instant.ofEpochMilli(arrivaltime),ZoneOffset.UTC));
			sp.setDeparturetime(departuretime == 0 ? null
					: LocalDateTime.ofInstant(Instant.ofEpochMilli(departuretime), ZoneOffset.UTC));

			sp.setOrder(rs.getInt("Order"));
			sp.setStationid(rs.getInt("Station_ID"));

			splist.add(sp);

		}

		trip.setStationlist(splist);

		return trip;

	}

//	public boolean insertSeats(SeatPojo sp) throws SQLException {
//		int noofcoaches = sp.getCoachno();
//		int seatspercoach = sp.getSeatno();
//
//		String insertseats = "insert into Seats (Train_ID , Coach_No , Seat_No)";
//		insertseats += "value (? , ? , ? )";
//
//		int tempseatno = 1;
//		int tempcoachno = 1;
//
//		PreparedStatement ps = con.prepareStatement(insertseats);
//		ps.setInt(1, sp.getTrainid());
//		for (int i = 0; i < noofcoaches; i++, tempcoachno++) {
//
//			ps.setInt(2, tempcoachno);
//
//			for (int j = 0; j < seatspercoach; j++, tempseatno++) {
//				ps.setInt(3, tempseatno);
//
//				ps.executeUpdate();
//			}
//			tempseatno = 1;
//
//		}
//
//		return false;
//
//	}

	public boolean updateStation(Station station) throws ClassNotFoundException, SQLException, CustomException {
		boolean res = false;

		connect();

		this.checkstationbycoords(station);

		String stationexists = "select * from Station where Station_ID = ?";

		PreparedStatement ps = con.prepareStatement(stationexists);
		ps.setInt(1, station.getStationID());

		String stationexistswithsamename = "select * from Station where Station_Name = ?";

		PreparedStatement ps3 = con.prepareStatement(stationexistswithsamename);
		ps3.setString(1, station.getStationName());
		boolean stationexist = ps.executeQuery().next();
		boolean stationwithsamename = ps3.executeQuery().next();

		if (stationexist && !stationwithsamename) {

			String updatestation = "update Station set Station_Name = ? , PosX = ? , PosY = ? where Station_ID = ?";

			PreparedStatement ps2 = con.prepareStatement(updatestation);
			ps2.setString(1, station.getStationName());
			ps2.setInt(2, station.getPosx());
			ps2.setInt(3, station.getPosy());
			ps2.setInt(4, station.getStationID());
			res = ps2.executeUpdate() > 0;

			return res;
		} else if (stationwithsamename) {
			throw new CustomException("Another Station has this name");
		}

		else {
			throw new CustomException("Station does not exist");
		}

	}

	public List<Station> getAllStations() throws ClassNotFoundException, SQLException, CustomException {
		List<Station> stationlist = new ArrayList<Station>();

		connect();

		String getalltrains = "select * from Station";

		PreparedStatement ps = con.prepareStatement(getalltrains, ResultSet.TYPE_SCROLL_INSENSITIVE,
				ResultSet.CONCUR_UPDATABLE);
		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Station station = new Station(rs.getInt("Station_ID"), rs.getString("Station_Name"), rs.getInt("PosX"),
					rs.getInt("PosY"));
			stationlist.add(station);
		}
		disconnect();
		return stationlist;
	}

	public Station getStation(int sid) throws ClassNotFoundException, SQLException, CustomException {
		connect();

		String getstation = "select * from Station where Station_ID = ?";

		PreparedStatement ps = con.prepareStatement(getstation);
		ps.setInt(1, sid);

		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			Station station = new Station(rs.getInt("Station_ID"), rs.getString("Station_Name"), rs.getInt("PosX"),
					rs.getInt("PosY"));
			return station;
		} else {
			throw new CustomException("Station does not exist");
		}

	}

	public boolean deleteStation(Station station) throws SQLException, CustomException, ClassNotFoundException {
		connect();

		boolean res = false;

		String stationexists = "select * from Station where Station_ID = ?";

		PreparedStatement ps = con.prepareStatement(stationexists);
		ps.setInt(1, station.getStationID());

		boolean stationexist = ps.executeQuery().next();

		if (stationexist) {

			String deletestation = "delete from Station where Station_ID = ?";

			PreparedStatement ps2 = con.prepareStatement(deletestation);
			ps2.setInt(1, station.getStationID());

			res = ps2.executeUpdate() > 0;

			disconnect();
			return res;

		} else {
			throw new CustomException("Station does not exist");

		}

	}

	public boolean checkstationbycoords(Station station) throws SQLException, CustomException {

		String checkstation = "select * from station where PosX = ? and PosY = ?";

		PreparedStatement ps = con.prepareStatement(checkstation);
		ps.setInt(1, station.getPosx());
		ps.setInt(2, station.getPosy());

		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			throw new CustomException(rs.getString("Station_Name") + " already exists in the coordinates");
		}

		return false;
	}

	public boolean insertStation(Station station) throws ClassNotFoundException, SQLException, CustomException {
		connect();

		this.checkstationbycoords(station);

		String stationexists = "select * from Station where Station_Name = ?";

		PreparedStatement ps = con.prepareStatement(stationexists);
		ps.setString(1, station.getStationName());

		boolean stationexist = ps.executeQuery().next();

		if (stationexist) {
			disconnect();
			throw new CustomException("Station already exists");

		} else {
			String insertstation = "insert into Station (Station_Name , PosX , PosY) value ( ? , ? , ?)";
			PreparedStatement ps2 = con.prepareStatement(insertstation);

			ps2.setString(1, station.getStationName());
			ps2.setInt(2, station.getPosx());
			ps2.setInt(3, station.getPosy());

			boolean res = ps2.executeUpdate() > 0;
			System.out.print(res);
			disconnect();
			return res;

		}

	}

	public Train getTrain(int tid) throws ClassNotFoundException, SQLException, CustomException {
		connect();

		Train t = new Train();

		String gettrain = "select * from Trains where Train_ID = ?";

		PreparedStatement ps = con.prepareStatement(gettrain);
		ps.setInt(1, tid);

		ResultSet rs = ps.executeQuery();

		if (rs.next()) {
			t.setTrainID(rs.getInt("Train_ID"));
			t.setCoaches(rs.getInt("Coaches"));
			t.setSeatsPerCoach(rs.getInt("SeatsPerCoach"));
			t.setTrainName(rs.getString("Train_Name"));
			disconnect();
			return t;
		} else {
			disconnect();
			throw new CustomException("Train does not exist");
		}

	}

	public boolean updateTrain(Train train) throws ClassNotFoundException, SQLException, CustomException {

		connect();

		String updatetrain = "update Trains set Train_Name = ? , Coaches = ? , SeatsPerCoach = ? where Train_ID = ?";

		PreparedStatement ps = con.prepareStatement(updatetrain);
		ps.setString(1, train.getTrainName());

		ps.setInt(2, train.getCoaches());
		ps.setInt(3, train.getSeatsPerCoach());
		ps.setInt(4, train.getTrainID());

		boolean res = false;

		res = ps.executeUpdate() > 0;

		if (!res) {
			disconnect();
			throw new CustomException("Record does not exist");
		}

		disconnect();
		return res;

	}

	public boolean deleteTrain(int tid) throws ClassNotFoundException, SQLException, CustomException {
		connect();

		boolean res = false;

		String deletetrain = "delete from Trains where Train_ID = ?";

		PreparedStatement ps = con.prepareStatement(deletetrain);
		ps.setInt(1, tid);

		res = ps.executeUpdate() > 0;

		if (!res) {
			disconnect();
			throw new CustomException("Train Does Not Exist");
		} else {
//			boolean seatsdeleted = this.deleteSeats(new SeatPojo(tid));
//
//			if (!seatsdeleted) {
//				throw new CustomException("Seating information not available for this train");
//			}
		}

		disconnect();
		return res;
	}

//	private boolean deleteSeats(SeatPojo sp) throws SQLException {
//
//		String deleteseats = "delete from Seats where Train_ID = ?";
//
//		PreparedStatement ps = con.prepareStatement(deleteseats);
//
//		ps.setInt(1, sp.getTrainid());
//
//		boolean res = ps.executeUpdate() > 0;
//
//		return res;
//	}

	public List<Train> ListTrains() throws SQLException, ClassNotFoundException {
		connect();

		List<Train> trainList = new ArrayList<Train>();

		String gettrains = "select * from Trains";

		PreparedStatement ps = con.prepareStatement(gettrains);

		ResultSet rs = ps.executeQuery();

		while (rs.next()) {
			Train train = new Train(rs.getInt("Train_ID"), rs.getString("Train_Name"), rs.getInt("Coaches"),
					rs.getInt("SeatsPerCoach"));
			trainList.add(train);
		}

		disconnect();
		return trainList;
	}

	public boolean insertTrain(Train train) throws CustomException, SQLException, ClassNotFoundException {
		connect();

		String checkduplicate = "select * from Trains where Trains.Train_Name = ?";

		PreparedStatement ps1 = (PreparedStatement) con.prepareStatement(checkduplicate);
		ps1.setString(1, train.getTrainName());

		ResultSet rs = ps1.executeQuery();

		if (rs.next()) {
			throw new CustomException("Train exists...");
		}

		boolean res = false;
		String insertquery = "INSERT INTO Trains (Train_Name , Coaches , SeatsPerCoach) ";

		insertquery += "value ( ? , ? , ?) ";

		PreparedStatement ps = con.prepareStatement(insertquery, PreparedStatement.RETURN_GENERATED_KEYS);

		ps.setString(1, train.getTrainName());
		ps.setInt(2, train.getCoaches());
		ps.setInt(3, train.getSeatsPerCoach());

		res = ps.execute();

//		ResultSet rs1 = ps.getGeneratedKeys();
//
//		if (rs1.next()) {
//			int trainid = rs1.getInt(1);
//			System.out.println(trainid);
//			this.insertSeats(new SeatPojo(trainid, train.getCoaches(), train.getSeatsPerCoach()));
//		}

		disconnect();
		return res;
	}
}
