package de.htwberlin.f4.tms.schnitzeljagd.core;

import android.content.ContentValues;
import android.content.Context;
import android.database.Cursor;
import android.database.sqlite.SQLiteDatabase;
import android.database.sqlite.SQLiteOpenHelper;
import android.util.Log;

import java.util.ArrayList;
import java.util.List;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class GameSQLiteHelper extends SQLiteOpenHelper {
    private static GameSQLiteHelper sqLiteHelper;
    private static final String LOG = "DatabaseHelper";

    private static final int DATABASE_VERSION = 1;
    private static final String DATABASE_NAME = "gameDatabase";
    private static final String TABLE_GameRoute = "gameroute";
    private static final String TABLE_GameStation = "gamestation";

    private static final String GameRoute_ID = "id";
    private static final String GameRoute_Filename = "filename";
    private static final String GameRoute_Finished = "finished";
    private static final String GameStation_ID = "id";
    private static final String GameStation_Question = "question";
    private static final String GameStation_Info = "info";
    private static final String GameStation_GPS_Latitude = "gps_latitude";
    private static final String GameStation_GPS_Longitude = "gps_longtitude";
    private static final String GameStation_Answer = "answer";
    private static final String GameStation_GameRoute_ID = "gameroute_id";

    private static final String CREATE_TABLE_GameRoute = "CREATE TABLE "
            + TABLE_GameRoute + "(" + GameRoute_ID + " INTEGER PRIMARY KEY,"
            + GameRoute_Filename + " TEXT,"
            + GameRoute_Finished + " NUMERIC" + ")";

    private static final String CREATE_TABLE_GameStation = "CREATE TABLE "
            + TABLE_GameStation + "(" + GameStation_ID + " INTEGER PRIMARY KEY,"
            + GameStation_Question + " TEXT," + GameStation_Info + " TEXT,"
            + GameStation_GPS_Latitude + " REAL," + GameStation_GPS_Longitude + " REAL,"
            + GameStation_Answer + " TEXT," + GameStation_GameRoute_ID + " INTEGER,"
            + "FOREIGN KEY(" + GameStation_GameRoute_ID + ") REFERENCES " + TABLE_GameRoute
            + "(" + GameRoute_ID + "))";

    /**
     * @param context
     */
    private GameSQLiteHelper(Context context) {
        super(context, DATABASE_NAME, null, DATABASE_VERSION);
        this.loadDatabase();
    }

    /**
     * @param context
     * @return
     */
    public static synchronized GameSQLiteHelper getInstance(Context context) {
        if (sqLiteHelper == null) {
            sqLiteHelper = new GameSQLiteHelper(context);
        }
        return sqLiteHelper;
    }

    /**
     * @return
     */
    public static synchronized GameSQLiteHelper getInstance() {
        if (sqLiteHelper == null) {
            throw new IllegalStateException();
        }
        return sqLiteHelper;
    }

    /**
     * @return
     */
    @Override
    public String getDatabaseName() {
        return DATABASE_NAME;
    }

    /**
     * @param db
     */
    @Override
    public void onCreate(SQLiteDatabase db) {
        db.execSQL(CREATE_TABLE_GameRoute);
        db.execSQL(CREATE_TABLE_GameStation);
    }

    /**
     * @param db
     * @param oldVersion
     * @param newVersion
     */
    @Override
    public void onUpgrade(SQLiteDatabase db, int oldVersion, int newVersion) {
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GameRoute);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GameStation);
        onCreate(db);
    }

    /**
     *
     */
    private void loadDatabase() {
        SQLiteDatabase db = this.getWritableDatabase();
    }

    /**
     * closing database
     */
    public void closeDB() {
        SQLiteDatabase db = this.getReadableDatabase();
        if (db != null && db.isOpen())
            db.close();
    }

    /**
     *
     */
    public void databaseQueryOnStartup() {
        SQLiteDatabase db = this.getWritableDatabase();
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GameRoute);
        db.execSQL("DROP TABLE IF EXISTS " + TABLE_GameStation);
        db.execSQL(CREATE_TABLE_GameRoute);
        db.execSQL(CREATE_TABLE_GameStation);
    }

    /**
     * @param filename
     */
    public void createNewGameRouteWithoutGameStationsInDatabase(String filename) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GameRoute_Filename, filename);
        values.put(GameRoute_Finished, 0);

        long todo_id = db.insert(TABLE_GameRoute, null, values);
        System.out.println(todo_id);
    }

    /**
     * @param question
     * @param info
     * @param gps_latitude
     * @param gps_longitude
     * @param answer
     * @param gameRoute_ID
     */
    public void createNewGameStationInDatabase(String question, String info,
                                               String gps_latitude, String gps_longitude,
                                               String answer, int gameRoute_ID) {
        SQLiteDatabase db = this.getWritableDatabase();

        ContentValues values = new ContentValues();
        values.put(GameStation_Question, question);
        values.put(GameStation_Info, info);
        values.put(GameStation_GPS_Latitude, gps_latitude);
        values.put(GameStation_GPS_Longitude, gps_longitude);
        values.put(GameStation_Answer, answer);
        values.put(GameStation_GameRoute_ID, gameRoute_ID);

        long todo_id = db.insert(TABLE_GameStation, null, values);
        System.out.println(todo_id);
    }

    /**
     * @return List with string structure (id, filename, finished)
     */
    public List<String> getGameRouteWithoutGameSationsFromDatabase(int searchedGameRouteID) {
        List<String> gameRouteList = new ArrayList<String>();
        String selectQuery = "SELECT * FROM " + TABLE_GameRoute + " WHERE "
                + GameRoute_ID + " = " + searchedGameRouteID;
        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Integer gameID = c.getInt((c.getColumnIndex(GameRoute_ID)));
                String filename = c.getString((c.getColumnIndex(GameRoute_Filename)));
                Boolean finished = c.getInt((c.getColumnIndex(GameRoute_Finished))) > 0;
                gameRouteList.add(gameID.toString());
                gameRouteList.add(filename);
                gameRouteList.add(finished.toString());
            } while (c.moveToNext());
        }
        return gameRouteList;
    }

    /**
     * @return List with string structure (id, filename, finished)
     */
    public List<List<String>> getAllGameRoutesWithoutGameSationsFromDatabase() {
        List<List<String>> gameRouteList = new ArrayList<List<String>>();
        String selectQuery = "SELECT  * FROM " + TABLE_GameRoute;
        Log.e(LOG, selectQuery);

        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery(selectQuery, null);

        if (c.moveToFirst()) {
            do {
                Integer gameID = c.getInt((c.getColumnIndex(GameRoute_ID)));
                String filename = c.getString((c.getColumnIndex(GameRoute_Filename)));
                Boolean finished = c.getInt((c.getColumnIndex(GameRoute_Finished))) > 0;
                List<String> tempList = new ArrayList<String>();
                tempList.add(gameID.toString());
                tempList.add(filename);
                tempList.add(finished.toString());
                gameRouteList.add(tempList);
            } while (c.moveToNext());
        }
        return gameRouteList;
    }

    /**
     * @param searchedGameRouteID
     * @return
     */
    public List<String> getAllGameStationsWithGameRouteIdFromDatabase(int searchedGameRouteID) {
        SQLiteDatabase db = this.getReadableDatabase();
        String selectQuery = "SELECT * FROM " + TABLE_GameStation + " WHERE "
                + GameStation_GameRoute_ID + " = " + searchedGameRouteID;
        Log.e(LOG, selectQuery);

        Cursor c = db.rawQuery(selectQuery, null);
        List<String> gameStationList = new ArrayList<String>();

        if (c.moveToFirst()) {
            do {
                String question = c.getString(c.getColumnIndex(GameStation_Question));
                String info = c.getString(c.getColumnIndex(GameStation_Info));
                String gps_latitude = c.getString(c.getColumnIndex(GameStation_GPS_Latitude));
                String gps_longitude = c.getString(c.getColumnIndex(GameStation_GPS_Longitude));
                String answer = c.getString(c.getColumnIndex(GameStation_Answer));
                gameStationList.add(question);
                gameStationList.add(info);
                gameStationList.add(gps_latitude);
                gameStationList.add(gps_longitude);
                gameStationList.add(answer);
            } while (c.moveToNext());
        }
        return gameStationList;
    }

    /**
     * @param transmittedGameRouteID
     */
    public void deleteGameRouteWithGameStations(int transmittedGameRouteID) {
        SQLiteDatabase db = this.getReadableDatabase();
        db.delete(TABLE_GameStation, GameStation_GameRoute_ID + " = ?",
                new String[]{String.valueOf(transmittedGameRouteID)});
        db.delete(TABLE_GameRoute, GameRoute_ID + " = ?",
                new String[]{String.valueOf(transmittedGameRouteID)});
    }

    /**
     * @param transmittedGameRouteID
     * @return
     */
    public int updateGameRouteAsFinished(int transmittedGameRouteID) {
        SQLiteDatabase db = this.getWritableDatabase();
        ContentValues values = new ContentValues();
        values.put(GameRoute_Finished, 1);
        return db.update(TABLE_GameRoute, values, GameRoute_ID + " = ?",
                new String[]{String.valueOf(transmittedGameRouteID)});
    }

    /**
     * @return
     */
    public int getLastInsertID() {
        SQLiteDatabase db = this.getReadableDatabase();
        Cursor c = db.rawQuery("SELECT MAX(id) FROM " + TABLE_GameRoute, null);
        c.moveToFirst();
        int id = c.getInt(0);
        return id;
    }
}
