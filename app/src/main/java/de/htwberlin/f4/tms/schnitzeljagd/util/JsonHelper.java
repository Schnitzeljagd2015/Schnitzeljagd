package de.htwberlin.f4.tms.schnitzeljagd.util;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.security.InvalidParameterException;
import java.util.ArrayList;

import de.htwberlin.f4.tms.schnitzeljagd.core.ApplicationController;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameRoute;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameStation;
import de.htwberlin.f4.tms.schnitzeljagd.core.GeoPoint;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class JsonHelper {
    private JsonHelper() {

    }

    /**
     * @param rawJson
     * @return
     * @throws JSONException
     */
    public static JSONObject parse(String rawJson) throws JSONException {
        return new JSONObject(rawJson);
    }

    /**
     * @param gameStation
     * @return
     * @throws JSONException
     */
    public static JSONObject toJson(GameStation gameStation) throws JSONException {
        JSONObject jObject = null;
        if (gameStation != null) {
            jObject = new JSONObject();
            jObject.put("question", gameStation.getQuestion());
            jObject.put("answer", gameStation.getAnswer());
            jObject.put("info", gameStation.getInfo());
            jObject.put("latitude", gameStation.getGeoPoint().getLatitude());
            jObject.put("longitude", gameStation.getGeoPoint().getLongitude());
            jObject.put("srid", gameStation.getGeoPoint().getSrid());
        } else
            throw new IllegalArgumentException("Invalid Arguments.");
        return jObject;
    }

    /**
     * @param gameRoute
     * @return
     * @throws JSONException
     */
    public static JSONObject toJson(GameRoute gameRoute) throws JSONException {
        JSONObject jObject = null;
        if (gameRoute != null) {
            jObject = new JSONObject();
            jObject.put("version", 1.0f);
            jObject.put("number of gameroutes", gameRoute.getGameStations().size());
            JSONArray jArray = new JSONArray();
            int stationCounter = 0;
            for (GameStation gStation : gameRoute.getGameStations()) {
                stationCounter++;
                JSONObject jObjectGS = toJson(gStation);
                jObjectGS.put("number", stationCounter);
                jArray.put(jObjectGS);
            }
            jObject.put("gameroutes", jArray);
        } else
            throw new IllegalArgumentException("Invalid Arguments.");
        return jObject;
    }

    /**
     * @param jsonObject
     * @return
     * @throws JSONException
     */
    public static GameStation toGameStation(JSONObject jsonObject) throws JSONException {
        GameStation gameStation = null;
        if (jsonObject != null) {
            String question = jsonObject.getString("question");
            String answer = jsonObject.getString("answer");
            String info = jsonObject.getString("info");
            double latitude = jsonObject.getDouble("latitude");
            double longitude = jsonObject.getDouble("longitude");
            int srid = jsonObject.getInt("srid");
            GeoPoint geoPoint = new GeoPoint(latitude, longitude, srid);
            gameStation = new GameStation(question, geoPoint, answer, info);
        } else
            throw new IllegalArgumentException("Invalid Arguments.");
        return gameStation;
    }

    /**
     * @param jsonObject
     * @param filename
     * @return
     * @throws JSONException
     * @throws InvalidParameterException
     */
    public static GameRoute toGameRoute(JSONObject jsonObject, String filename)
            throws JSONException, InvalidParameterException {
        GameRoute gameRoute = null;
        if (jsonObject != null) {
            float version = (float) jsonObject.getDouble("version");
            if (Float.compare(version, ApplicationController.VERSION) != 0)
                throw new InvalidParameterException("Wrong app-version in gameroute. Must be "
                        + ApplicationController.VERSION);
            int numberGameRoutes = jsonObject.getInt("number of gameroutes");
            JSONArray jArray = jsonObject.getJSONArray("gameroutes");
            ArrayList<GameStation> gameStations = new ArrayList<GameStation>();
            for (int i = 0; i < numberGameRoutes; i++) {
                GameStation gStation = toGameStation(jArray.getJSONObject(i));
                gameStations.add(gStation);
            }
            gameRoute = new GameRoute(0, filename, gameStations);
        } else
            throw new IllegalArgumentException("Invalid Arguments.");
        return gameRoute;
    }
}
