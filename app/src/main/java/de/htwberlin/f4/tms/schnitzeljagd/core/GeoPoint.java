package de.htwberlin.f4.tms.schnitzeljagd.core;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class GeoPoint {
    private double longitude;
    private double latitude;
    private int srid;

    /**
     * @param theLongitude
     * @param theLatitude
     * @param theSrid
     */
    public GeoPoint(double theLatitude, double theLongitude, int theSrid) {
        if (theSrid > 0) {
            latitude = theLatitude;
            longitude = theLongitude;
            srid = theSrid;
        } else
            throw new IllegalArgumentException("Invalid arguments.");
    }

    /**
     * @return
     */
    public double getLongitude() {
        return longitude;
    }

    /**
     * @return
     */
    public double getLatitude() {
        return latitude;
    }

    /**
     * @return
     */
    public int getSrid() {
        return srid;
    }

}
