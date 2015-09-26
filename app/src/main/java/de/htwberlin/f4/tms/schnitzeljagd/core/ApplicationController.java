package de.htwberlin.f4.tms.schnitzeljagd.core;

import android.app.Application;
import android.content.Context;
import android.content.SharedPreferences;

import java.io.IOException;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class ApplicationController extends Application {
    public static final float VERSION = 1.0f;
    private static ApplicationController instance;
    private GameRoute currentGameRoute;
    private SharedPreferences sharedPreferences;

    @Override
    public void onCreate() {
        super.onCreate();
        instance = this;
        GameSQLiteHelper.getInstance(getApplicationContext());
        sharedPreferences = getApplicationContext().getSharedPreferences("Schnitzeljagd",
                                                                         Context.MODE_PRIVATE);
    }

    /**
     * @return
     */
    public static synchronized ApplicationController getInstance() {
        return instance;
    }

    /**
     * @return
     * @throws IOException
     */
    public SchnitzelFileManager getSchnitzelFileManager() throws IOException {
        return SchnitzelFileManager.getInstance("Schnitzelfiles");
    }

    /**
     * @return currentGameRoute
     */
    public GameRoute getCurrentGameRoute() {
        return currentGameRoute;
    }

    /**
     * @param currentGameRoute
     */
    public void setCurrentGameRoute(GameRoute currentGameRoute) {
        this.currentGameRoute = currentGameRoute;
    }

    /**
     * @return
     */
    public boolean hasCurrentGameRoute() {
        return (currentGameRoute != null);
    }

    /**
     * @return
     */
    public SharedPreferences getSchnitzelPreferences() {
        return sharedPreferences;
    }
}
