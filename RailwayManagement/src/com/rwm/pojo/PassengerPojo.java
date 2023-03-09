package com.rwm.pojo;

public class PassengerPojo {
	
	private String passengername;
	
	private int passengerid;
	
	private int age;
	
	private String gender;
	
	private int coachno;
	
	private int seatno;
	
	
	
	
	public int getCoachno() {
		return coachno;
	}

	public void setCoachno(int coachno) {
		this.coachno = coachno;
	}

	public int getSeatno() {
		return seatno;
	}

	public void setSeatno(int seatno) {
		this.seatno = seatno;
	}

	public PassengerPojo() {
		// TODO Auto-generated constructor stub
	}

	public PassengerPojo(String name , String gen , int age) {
		this.passengername = name;
		this.gender = gen;
		this.age = age;
	}
	
	public PassengerPojo(int id ,String name , String gen , int age) {
		this.passengername = name;
		this.gender = gen;
		this.age = age;
		this.passengerid = id;
	}

	public String getPassengername() {
		return passengername;
	}


	public void setPassengername(String passengername) {
		this.passengername = passengername;
	}


	public int getPassengerid() {
		return passengerid;
	}


	public void setPassengerid(int passengerid) {
		this.passengerid = passengerid;
	}


	public int getAge() {
		return age;
	}


	public void setAge(int age) {
		this.age = age;
	}


	public String getGender() {
		return gender;
	}


	public void setGender(String gender) {
		this.gender = gender;
	}
	
	

}
