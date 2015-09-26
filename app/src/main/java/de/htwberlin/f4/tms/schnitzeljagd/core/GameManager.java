package de.htwberlin.f4.tms.schnitzeljagd.core;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;

import de.htwberlin.f4.tms.schnitzeljagd.util.IOHelper;
import de.htwberlin.f4.tms.schnitzeljagd.util.JsonHelper;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class GameManager {
    private GameManager() {

    }

    /**
     * @param id
     * @return
     */
    public static GameRoute loadGameRoute(int id) {
        List<String> gameStationsFromDB =
                GameSQLiteHelper.getInstance().getAllGameStationsWithGameRouteIdFromDatabase(id);
        ArrayList<GameStation> gameStationList = new ArrayList<GameStation>();
        Iterator tempItr = gameStationsFromDB.iterator();
        while (tempItr.hasNext()) {
            GameStation newGameStation = new GameStation(tempItr.next().toString(),
                    tempItr.next().toString(), Double.valueOf(tempItr.next().toString()),
                    Double.valueOf(tempItr.next().toString()), tempItr.next().toString());
            gameStationList.add(newGameStation);
        }
        List<String> gameRoutesFromDB =
                GameSQLiteHelper.getInstance().getGameRouteWithoutGameSationsFromDatabase(id);
        int gameRouteID = Integer.valueOf(gameRoutesFromDB.get(0));
        String gameRouteFilepath = gameRoutesFromDB.get(1);

        return new GameRoute(gameRouteID, gameRouteFilepath, gameStationList);
    }

    /**
     * @return
     */
    public static List<List<String>> getGameRoutesWithoutGameStations() {
        return (GameSQLiteHelper.getInstance().getAllGameRoutesWithoutGameSationsFromDatabase());
    }

    /**
     * @param id
     */
    public static void deleteGameRoute(int id) {
        GameSQLiteHelper.getInstance().deleteGameRouteWithGameStations(id);
    }

    /**
     * @param gameRoute
     * @throws JSONException
     * @throws IOException
     */
    public static void exportToFile(GameRoute gameRoute) throws JSONException, IOException {
        SchnitzelFileManager sfManager =
                ApplicationController.getInstance().getSchnitzelFileManager();
        String jsonGameRoute = convertToJSON(gameRoute);
        if (!sfManager.isSchnitzelFilesDirExistent())
            sfManager.cleanSchnitzelFilesDir();
        String filename = gameRoute.getFileName() + ".schnitzel";
        IOHelper.writeText(jsonGameRoute, filename, sfManager.getSchnitzelFilesDir());
        if (!IOHelper.isExistent(sfManager.getSchnitzelFilesDir() + File.separator + filename))
            throw new IOException("Error during writing the schnitzelfile " + filename + ".");
    }

    /**
     * @param file
     * @return
     * @throws IOException
     * @throws JSONException
     */
    public static GameRoute importFromFile(File file) throws IOException, JSONException {
        String jsonDataFromFile = IOHelper.readText(file.getAbsolutePath());
        String name = file.getName();
        if (name.lastIndexOf(".") > 0)
            name = name.substring(0, name.lastIndexOf("."));
        GameRoute gameRoute = convertToGameRoute(jsonDataFromFile, name);
        return gameRoute;
    }

    /**
     * @param gameRoute
     * @return
     * @throws JSONException
     */
    private static String convertToJSON(GameRoute gameRoute) throws JSONException {
        return JsonHelper.toJson(gameRoute).toString();
    }

    /**
     * @param jsonData
     * @param filename
     * @return
     * @throws JSONException
     */
    private static GameRoute convertToGameRoute(String jsonData, String filename)
            throws JSONException {
        JSONObject jsonRoute = JsonHelper.parse(jsonData);
        return JsonHelper.toGameRoute(jsonRoute, filename);
    }

    /**
     * @param theGameroute
     */
    public static void insertNewGameRouteWithGameStaionsInDatabase(GameRoute theGameroute) {
        GameSQLiteHelper.getInstance().createNewGameRouteWithoutGameStationsInDatabase(
                theGameroute.getFileName());
        for (GameStation gamestation : theGameroute.getGameStations()) {
            GameSQLiteHelper.getInstance().createNewGameStationInDatabase(gamestation.getQuestion(),
                    gamestation.getInfo(),
                    String.valueOf(gamestation.getGeoPoint().getLatitude()),
                    String.valueOf(gamestation.getGeoPoint().getLongitude()),
                    gamestation.getAnswer(),
                    GameSQLiteHelper.getInstance().getLastInsertID());
        }
    }

    /**
     * @param gameRouteID
     */
    public static void updateGameRouteAsFinished(int gameRouteID) {
        GameSQLiteHelper.getInstance().updateGameRouteAsFinished(gameRouteID);
    }
}
