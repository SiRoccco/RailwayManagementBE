package com.rwm.beans;

public class Train {

	private int TrainID;
	private String TrainName;
	
	private int Coaches;
	
	private int SeatsPerCoach;

		
	public int getTrainID() {
		return TrainID;
	}

	public void setTrainID(int trainID) {
		TrainID = trainID;
	}

	public String getTrainName() {
		return TrainName;
	}

	public void setTrainName(String trainName) {
		TrainName = trainName;
	}

	public int getCoaches() {
		return Coaches;
	}

	public void setCoaches(int coaches) {
		Coaches = coaches;
	}

	public int getSeatsPerCoach() {
		return SeatsPerCoach;
	}

	public void setSeatsPerCoach(int seatsPerCoach) {
		SeatsPerCoach = seatsPerCoach;
	}

	public Train(int TID ,String TName , int Ts , int As) {
		this.TrainID = TID;
		this.TrainName = TName;
		this.Coaches = Ts;
		this.SeatsPerCoach = As;
	}
	
	public Train(String TName , int Ts , int As) {
		this.TrainName = TName;
		this.Coaches = Ts;
		this.SeatsPerCoach = As;
	}
	
	public Train() {}
	

}
