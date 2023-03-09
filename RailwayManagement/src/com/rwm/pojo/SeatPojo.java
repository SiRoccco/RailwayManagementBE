package com.rwm.pojo;

public class SeatPojo {
	
	private int seatno;
	private int trainid;
	private int coachno;
	private int ticketid;

	public SeatPojo(int sno , int trid , int coachno , int tcid ) {
		this.coachno = coachno;
		this.seatno = sno;
		this.ticketid = tcid;
		this.trainid = trid;
	}
	
	public SeatPojo(int trid  ,  int coachno ,int sno) {
		this.coachno = coachno;
		this.seatno = sno;
		
		this.trainid = trid;
	}
	
	public SeatPojo(int trid) {
		this.trainid = trid;
	}
	
	public int getSeatno() {
		return seatno;
	}

	public void setSeatno(int seatno) {
		this.seatno = seatno;
	}

	public int getTrainid() {
		return trainid;
	}

	public void setTrainid(int trainid) {
		this.trainid = trainid;
	}

	public int getCoachno() {
		return coachno;
	}

	public void setCoachno(int coachno) {
		this.coachno = coachno;
	}

	public int getTicketid() {
		return ticketid;
	}

	public void setTicketid(int ticketid) {
		this.ticketid = ticketid;
	}

	public SeatPojo() {
		// TODO Auto-generated constructor stub
	}

}
