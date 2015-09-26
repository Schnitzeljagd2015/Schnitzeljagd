package de.htwberlin.f4.tms.schnitzeljagd.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Intent;
import android.content.SharedPreferences;
import android.os.Bundle;
import android.text.Html;
import android.text.method.LinkMovementMethod;
import android.view.LayoutInflater;
import android.view.View;
import android.view.animation.AlphaAnimation;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.RelativeLayout;
import android.widget.TextView;

import java.io.IOException;
import java.util.ArrayList;

import de.htwberlin.f4.tms.schnitzeljagd.R;
import de.htwberlin.f4.tms.schnitzeljagd.core.ApplicationController;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameManager;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameRoute;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameSQLiteHelper;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameStation;
import de.htwberlin.f4.tms.schnitzeljagd.core.GeoPoint;
import de.htwberlin.f4.tms.schnitzeljagd.core.LocationService;
import de.htwberlin.f4.tms.schnitzeljagd.core.SchnitzelFileManager;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class MainActivity extends Activity {
    private ImageView welcomeImageView;
    private Button playGameRouteButton;
    private Button manageGameRoutesButton;
    private LocationService locationServiceObject;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        welcomeImageView = (ImageView) findViewById(R.id.welcomeImageView);
        playGameRouteButton = (Button) findViewById(R.id.playGameRouteButton);
        manageGameRoutesButton = (Button) findViewById(R.id.manageGameRoutesButton);
        RelativeLayout layout = (RelativeLayout) findViewById(R.id.activity_main);
        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setFillAfter(true);
        animation.setDuration(1500);
        layout.startAnimation(animation);
        SharedPreferences shPref = ApplicationController.getInstance().getSchnitzelPreferences();
        if (!shPref.contains("IsFirstStart")) {
            testInsertGameRouteInDatabase();
            SharedPreferences.Editor editor = shPref.edit();
            editor.putString("IsFirstStart", "false");
            editor.commit();
        }
    }

    /**
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        try {
            ApplicationController.getInstance().getSchnitzelFileManager();
        } catch (IOException ioex) {
            showAlertWithInfo("Kein externer Speicher gefunden",
                    "Um Spielrouten zu importieren oder "
                            + "exportieren wird auf dem Gerät ein externer Speicher benötigt.",
                    R.drawable.icon_info);
        }
    }

    /**
     * @param v
     */
    public void onPlayGameRouteButtonClick(View v) {
        Intent intent = new Intent(this, SelectGameRoutesActivity.class);
        intent.addFlags(Intent.FLAG_ACTIVITY_NO_HISTORY);
        MainActivity.this.startActivity(intent);
    }

    /**
     * @param v
     */
    public void onManageGameRoutesButtonClick(View v) {
        Intent intent = new Intent(this, ManageGameRoutesActivity.class);
        MainActivity.this.startActivity(intent);
    }

    /**
     * @param v
     */
    public void onAboutTextViewClicked(View v) {
        String text = "Schnitzeljagd wurde von<br>" +
                "-Tino Herrmann (s0542709)<br>" +
                "-Fabian Schmöker (s0542541)<br>" +
                "im Rahmen der Veranstaltung<br>" +
                "\"Technik mobiler Systeme\"<br>an der " +
                "<a href=\"http://www.htw-berlin.de\">" +
                "Hochschule für Technik und Wirtschaft Berlin" +
                "</a> entwickelt.<br>";
        try {
            ApplicationController appContr = ApplicationController.getInstance();
            text += "<br>Der aktuelle Schnitzelfiles-Ordner befindet sich unter<br><b>" +
                    appContr.getSchnitzelFileManager().getSchnitzelFilesDir() +
                    "</b>.";
        } catch (IOException ioex) {
            text += "<br>Der aktuelle Schnitzelfiles-Ordner befindet sich unter<br>" +
                    "<b>Es konnte kein externer Speicher auf dem Gerät gefunden werden!</b>";
        }
        String header = "About";
        int image = R.drawable.htw_logo_rgb;
        showAlertWithInfo(header, text, image);
    }

    /**
     * @param v
     */
    public void onLicenceTextViewClicked(View v) {
        String text = "Schnitzeljagd is open source under LGPL 3.0 (or later).<br><br>" +
                "References:<br>" +
                "1. \"<a href=\"https://www.flickr.com/photos/gsfc/6760135001\">" +
                "Blue Marble 2012" +
                "</a>\" NASA/NOAA/GSFC/Suomi NPP/VIIRS/Norman Kuring, " +
                "<a href=\"https://creativecommons.org/licenses/by/2.0/\">" +
                "CC BY 2.0" +
                "</a><br><br>" +
                "2. \"<a href=\"https://commons.wikimedia.org/wiki/File:Stanley_compass_1.jpg#/media/File:Stanley_compass_1.jpg\">" +
                "Stanley compass 1" +
                "</a>\" " +
                "via <a href=\"https://commons.wikimedia.org/wiki/\">" +
                "Wikimedia Commons" +
                "</a>, " +
                "<a href=\"http://creativecommons.org/licenses/by-sa/3.0/\">" +
                "CC BY-SA 3.0" +
                "</a><br><br>" +
                "3. \"<a href=\"https://gnu.org/graphics/license-logos.html\">" +
                "LGPLv3 Logo" +
                "</a>\" Free Software Foundation, " +
                "<a href=\"https://creativecommons.org/licenses/by-nd/3.0/us/\">" +
                "CC BY-ND 3.0 US" +
                "</a><br><br>" +
                "4. \"<a href=\"http://www.htw-berlin.de/hochschulstruktur/zentrale-referate/presse-und-oeffentlichkeitsarbeit/corporate-design/\">" +
                "HTW Berlin Logo" +
                "</a>\" - " +
                "Für Hochschulmitglieder ausschließlich zu Hochschulzwecken erlaubt";
        String header = "Licence";
        int image = R.drawable.lgplv3;
        showAlertWithInfo(header, text, image);
    }

    /**
     * @param header
     * @param text
     * @param image
     */
    public void showAlertWithInfo(String header, String text, int image) {
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.about_alert_dialog, null);
        ImageView imageHeaderView = (ImageView) view.findViewById(R.id.headerImageView);
        imageHeaderView.setImageResource(image);

        AlphaAnimation animation = new AlphaAnimation(0.0f, 1.0f);
        animation.setFillAfter(true);
        animation.setDuration(500);
        view.setAnimation(animation);

        TextView messageHeaderTextView = (TextView) view.findViewById(R.id.headerTextView);
        messageHeaderTextView.setText(header);
        TextView messageTextView = (TextView) view.findViewById(R.id.bodyTextView);
        messageTextView.setText(Html.fromHtml(text));
        messageTextView.setMovementMethod(LinkMovementMethod.getInstance());
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setView(view);
        AlertDialog alert = builder.create();
        alert.show();
    }


    /**
     *
     */
    private void testInsertGameRouteInDatabase() {
        GameSQLiteHelper.getInstance().databaseQueryOnStartup();
        GameStation gameStation1 = new GameStation("Was siehst du?",
                new GeoPoint(52.514162, 13.350180, 4326),
                "Siegessäule",
                "Ist ein Wahrzeichen.");
        GameStation gameStation2 = new GameStation("Welche Institution ist dort?",
                new GeoPoint(52.457949, 13.526890, 4326),
                "HTW Berlin");
        GameStation gameStation3 = new GameStation("Was ragt da weit in den Himmel hinein?",
                new GeoPoint(52.518650, 13.404887, 4326),
                "Fernsehturm");
        ArrayList<GameStation> firstGame = new ArrayList<GameStation>();
        firstGame.add(gameStation1);
        firstGame.add(gameStation2);
        firstGame.add(gameStation3);
        GameManager.insertNewGameRouteWithGameStaionsInDatabase(
                new GameRoute(0, "Berlin sightseeing", firstGame));

        gameStation1 = new GameStation("Was steht hier?",
                new GeoPoint(52.4888, 13.4301, 4326),
                "St. Christophorus",
                "Kirchengemeinde");
        gameStation2 = new GameStation("An welcher Straße stehst du?",
                new GeoPoint(52.4944, 13.4254, 4326),
                "Maybachufer",
                "Berühmte Automarke");
        gameStation3 = new GameStation("Welche Art von medizinischer Einrichtung steht hier?",
                new GeoPoint(52.495149, 13.409406, 4326),
                "Krankenhaus",
                "Typ?");
        GameStation gameStation4 = new GameStation("Was befindet sich hier?",
                new GeoPoint(52.483647, 13.417062, 4326),
                "Kino",
                "Du stehst vor dem Eingang");
        ArrayList<GameStation> secondGame = new ArrayList<GameStation>();
        secondGame.add(gameStation1);
        secondGame.add(gameStation2);
        secondGame.add(gameStation3);
        secondGame.add((gameStation4));
        GameManager.insertNewGameRouteWithGameStaionsInDatabase(
                new GameRoute("Kreuzkölln", secondGame));
    }
}
