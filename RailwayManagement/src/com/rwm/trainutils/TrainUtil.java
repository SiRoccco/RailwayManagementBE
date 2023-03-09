package com.rwm.trainutils;

import java.sql.SQLException;
import java.util.List;

import org.json.JSONArray;
import org.json.JSONObject;

import com.rwm.beans.Train;
import com.rwm.customexceptions.CustomException;
import com.rwm.dao.RwmDao;

public class TrainUtil {

	private static RwmDao dao = new RwmDao();

	public void init() {
		
	}

	public TrainUtil() {
		
	}

	public static void checkparamTrain(Train train) throws CustomException {

		if (train.getTrainName() == null || train.getTrainName() == "") {
			throw new CustomException("Train Name cannot be empty");
		}

		if (train.getCoaches() <= 0 || train.getSeatsPerCoach() <= 0) {
			throw new CustomException("Invalid seating info");
		}
	}

	public static JSONObject getObjectFromTrain(Train train) {

		JSONObject trainobj = new JSONObject(train);

		return trainobj;

	}

	public static JSONArray getTrains(Integer trainid) throws ClassNotFoundException, SQLException, CustomException {

		JSONArray trainlist = new JSONArray();

		if (trainid == null) {
			List<Train> trains = dao.ListTrains();

			for (Train t : trains) {

				JSONObject tobj = TrainUtil.getObjectFromTrain(t);

				trainlist.put(tobj);

			}

			return trainlist;
		} else {

			Train t = dao.getTrain(trainid);

			JSONObject tobj = TrainUtil.getObjectFromTrain(t);

			trainlist.put(tobj);

			return trainlist;

		}
	}

}
