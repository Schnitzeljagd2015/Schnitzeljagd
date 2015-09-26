package de.htwberlin.f4.tms.schnitzeljagd.core;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class GameRoute implements FinishedListener {
    private int routeID;
    private String fileName;
    private boolean finished;
    private List<GameStation> gameStations;

    /**
     * @param theFileName
     * @param theGameStations
     */
    public GameRoute(String theFileName, ArrayList<GameStation> theGameStations) {
        boolean inputOk = ((theFileName != null) && (theGameStations != null) &&
                (theGameStations.size() > 0) && (theFileName.length() > 0));
        if (inputOk) {
            routeID = -1;
            fileName = theFileName;
            gameStations = new ArrayList<GameStation>(theGameStations);
            finished = false;
            for (GameStation gs : gameStations)
                gs.addFinishedListener(this);
        } else
            throw new IllegalArgumentException("Invalid arguments to initialize GameRoute.");
    }

    /**
     * @param theGameRouteID
     * @param theFileName
     * @param theGameStations
     */
    public GameRoute(int theGameRouteID, String theFileName, ArrayList<GameStation> theGameStations) {
        boolean inputOk = ((theFileName != null) && (theGameStations != null) &&
                (theGameStations.size() > 0) && (theFileName.length() > 0));
        if (inputOk) {
            routeID = theGameRouteID;
            fileName = theFileName;
            gameStations = new ArrayList<GameStation>(theGameStations);
            finished = false;
            for (GameStation gs : gameStations)
                gs.addFinishedListener(this);
        } else
            throw new IllegalArgumentException("Invalid arguments to initialize GameRoute.");
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
    public ArrayList<GameStation> getGameStations() {
        return new ArrayList<GameStation>(gameStations);
    }

    /**
     * @return
     */
    public int getRouteID() {
        return routeID;
    }

    /**
     * @return
     */
    public String getFileName() {
        return fileName;
    }

    /**
     * @return
     */
    public boolean hasID() {
        return routeID != -1;
    }

    /**
     * @param sender
     */
    @Override
    public void hasFinished(Object sender) {
        boolean inputOk = ((sender != null) && (sender.getClass().equals(GameStation.class)));
        if (sender != null) {
            if (gameStations.contains(sender) && checkIsFinished()) {
                finished = true;
                if (hasID()) {
                    GameManager.updateGameRouteAsFinished(this.routeID);
                }
            }
        } else
            throw new IllegalArgumentException("Invalid Argument.");
    }

    /**
     * @return
     */
    private boolean checkIsFinished() {
        boolean f = true;
        for (GameStation gr : gameStations) {
            if (!gr.isFinished()) {
                f = false;
                break;
            }
        }
        return f;
    }
}
