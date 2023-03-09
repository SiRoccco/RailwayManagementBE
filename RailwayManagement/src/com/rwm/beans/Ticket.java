package com.rwm.beans;

import java.time.LocalDateTime;
import java.util.LinkedList;

import com.rwm.pojo.PassengerPojo;
import com.rwm.pojo.StationPojo;

public class Ticket {
	
	private int ticketid;
	
	private String trainname;
	
	private int userid;
	
	private int tripid;
	
	private int numberofseatsbooked;
	
	private LinkedList<PassengerPojo> passengers;
	
	private StationPojo boardingstation;
	
	private StationPojo arrivalstation;
	
	private float fare;
	
	
	
	public String getTrainname() {
		return trainname;
	}



	public void setTrainname(String trainname) {
		this.trainname = trainname;
	}



	public int getTicketid() {
		return ticketid;
	}



	public void setTicketid(int ticketid) {
		this.ticketid = ticketid;
	}



	public int getUserid() {
		return userid;
	}



	public void setUserid(int userid) {
		this.userid = userid;
	}



	public int getTripid() {
		return tripid;
	}



	public void setTripid(int tripid) {
		this.tripid = tripid;
	}



	public int getNumberofseatsbooked() {
		return numberofseatsbooked;
	}



	public void setNumberofseatsbooked(int numberofseatsbooked) {
		this.numberofseatsbooked = this.passengers.size();
	}



	public LinkedList<PassengerPojo> getPassengers() {
		return passengers;
	}



	public void setPassengers(LinkedList<PassengerPojo> passengers) {
		this.passengers = passengers;
	}



	public StationPojo getBoardingstation() {
		return boardingstation;
	}



	public void setBoardingstation(StationPojo boardingstation) {
		this.boardingstation = boardingstation;
	}



	public StationPojo getArrivalstation() {
		return arrivalstation;
	}



	public void setArrivalstation(StationPojo arrivalstation) {
		this.arrivalstation = arrivalstation;
	}



	public float getFare() {
		return fare;
	}



	public void setFare(float fare) {
		this.fare = fare;
	}



	public Ticket() {
		// TODO Auto-generated constructor stub
	}

}
