package de.htwberlin.f4.tms.schnitzeljagd.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class GameStation {
    private String question;
    private String info;
    private GeoPoint geoPoint;
    private String answer;
    private boolean finished;
    private List<FinishedListener> listeners;

    private static final int WGS84 = 4326;

    /**
     * @param theQuestion
     * @param theGeoPoint
     * @param theAnswer
     */
    public GameStation(String theQuestion, GeoPoint theGeoPoint, String theAnswer) {
        boolean inputOk = ((theQuestion != null) && (theGeoPoint != null) &&
                (theAnswer != null));
        if (inputOk) {
            question = theQuestion;
            answer = theAnswer;
            info = "";
            finished = false;
            geoPoint = new GeoPoint(theGeoPoint.getLatitude(),
                                    theGeoPoint.getLongitude(),
                                    theGeoPoint.getSrid());
            listeners = new ArrayList<FinishedListener>();
        } else
            throw new IllegalArgumentException("Invalid Argument to initialize GameStation.");
    }

    /**
     * @param theQuestion
     * @param theGeoPoint
     * @param theAnswer
     * @param theInfo
     */
    public GameStation(String theQuestion, GeoPoint theGeoPoint, String theAnswer, String theInfo) {
        boolean inputOk = ((theQuestion != null) && (theGeoPoint != null) &&
                (theAnswer != null) && (theInfo != null));
        if (inputOk) {
            question = theQuestion;
            answer = theAnswer;
            info = theInfo;
            finished = false;
            geoPoint = new GeoPoint(theGeoPoint.getLatitude(),
                                    theGeoPoint.getLongitude(),
                                    theGeoPoint.getSrid());
            listeners = new ArrayList<FinishedListener>();
        } else
            throw new IllegalArgumentException("Invalid arguments to initialize GameStation.");
    }

    /**
     * @param theQuestion
     * @param theInfo
     * @param latitude
     * @param longitude
     * @param theAnswer
     */
    public GameStation(String theQuestion, String theInfo, double latitude,
                       double longitude, String theAnswer) {
        boolean inputOk = ((theQuestion != null) && (theInfo != null) && (theAnswer != null));
        if (inputOk) {
            question = theQuestion;
            info = theInfo;
            geoPoint = new GeoPoint(latitude, longitude, WGS84);
            answer = theAnswer;
            finished = false;
            listeners = new ArrayList<FinishedListener>();
        } else
            throw new IllegalArgumentException("Invalid arguments to initialize GameStation.");
    }

    /**
     * @param listenerToAdd
     */
    public void addFinishedListener(FinishedListener listenerToAdd) {
        if (listenerToAdd != null)
            listeners.add(listenerToAdd);
        else
            throw new IllegalArgumentException("Invalid argument to add FinishedListener.");
    }

    /**
     * @return
     */
    public String getQuestion() {
        return question;
    }

    /**
     * @return
     */
    public String getInfo() {
        return info;
    }

    /**
     * @return
     */
    public String getAnswer() {
        return answer;
    }

    /**
     * @return
     */
    public boolean isFinished() {
        return finished;
    }

    /**
     * @return
     */
    public GeoPoint getGeoPoint() {
        return new GeoPoint(geoPoint.getLatitude(), geoPoint.getLongitude(), geoPoint.getSrid());
    }

    /**
     *
     */
    public void setAsFinished() {
        finished = true;
        for (FinishedListener fl : listeners)
            fl.hasFinished(this);
    }
}
