package de.htwberlin.f4.tms.schnitzeljagd.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.Location;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

import de.htwberlin.f4.tms.schnitzeljagd.R;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameManager;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameRoute;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameStation;
import de.htwberlin.f4.tms.schnitzeljagd.core.GeoPoint;
import de.htwberlin.f4.tms.schnitzeljagd.core.LocationService;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class CreateGameRouteActivity extends FragmentActivity {
    private TextView numberTextView;
    private EditText insertQuestionEditText;
    private EditText infoInputEditText;
    private GoogleMap googleMap;
    private EditText insertAnswerEditText;
    private Button returnButton;
    private Button finishedRouteButton;
    private Button nextGameStationButton;
    private ArrayList<GameStation> gameStations;
    private String gameRouteName;
    private LocationManager manager;
    private LocationService locationServiceObject;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_create_game_route);
        initalizeUIComponents();
        gameStations = new ArrayList<GameStation>();
        gameRouteName = "";
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationServiceObject = new LocationService();
    }

    /**
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        setCurrentLocation();
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationServiceObject.buildAlertMessageNoGps(this);
        }
        if (!locationServiceObject.isOnline(this)) {
            locationServiceObject.buildAlertMessageNoNetworkProvider(this);
        }
    }

    /**
     * @param v
     */
    public void onNextGameStationButtonClick(View v) {
        if (validateInput()) {
            createGameStationFromUIValues();
            Toast.makeText(getApplicationContext(),
                    "Spielstation erstellt",
                    Toast.LENGTH_SHORT).show();
            clearUIComponents();
            if ((gameStations.size() + 1) <= 9)
                numberTextView.setText("0");
            numberTextView.setText(numberTextView.getText()
                    + String.valueOf(gameStations.size() + 1));
        }
    }

    /**
     * @param v
     */
    public void onFinishedRouteButtonClick(View v) {
        try {
            if (validateInput()) {
                showNameOfGameRouteAlert();
            }
        } catch (Exception ex) {
            showGeneralAlert("Erstellen fehlgeschlagen",
                    "Beim Erstellen der Spielroute ist ein Fehler aufgetreten", "OK");
        }
    }

    /**
     * @param v
     */
    public void onReturnButtonClick(View v) {
        gameStations.clear();
        gameRouteName = "";
        onBackPressed();
    }

    /**
     *
     */
    private void initalizeUIComponents() {
        numberTextView = (TextView) findViewById(R.id.numberTextView);
        insertQuestionEditText = (EditText) findViewById(R.id.insertQuestionEditText);
        infoInputEditText = (EditText) findViewById(R.id.infoInputEditText);
        insertAnswerEditText = (EditText) findViewById(R.id.insertAnswerEditText);
        returnButton = (Button) findViewById(R.id.returnButton);
        finishedRouteButton = (Button) findViewById(R.id.finishRouteButton);
        nextGameStationButton = (Button) findViewById(R.id.nextGameStationButton);
        googleMap = ((SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.mapFragment)).getMap();
    }

    /**
     *
     */
    private void setCurrentLocation() {
        googleMap.setMyLocationEnabled(true);
        Location currentLocation = googleMap.getMyLocation();
        if (currentLocation != null)
            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(
                    new LatLng(currentLocation.getLatitude(),
                            currentLocation.getLongitude()), 15.0f));
        googleMap.getUiSettings().setZoomControlsEnabled(true);
    }

    /**
     *
     */
    private void createGameStationFromUIValues() {
        String question = insertQuestionEditText.getText().toString();
        String info = infoInputEditText.getText().toString();
        String answer = insertAnswerEditText.getText().toString();
        GeoPoint point = null;
        Location currentLocation = googleMap.getMyLocation();
        if (currentLocation != null)
            point = new GeoPoint(currentLocation.getLatitude(),
                    currentLocation.getLongitude(), 4326);
        else
            point = new GeoPoint(0, 0, 4326);
        gameStations.add(new GameStation(question, point, answer, info));
    }

    /**
     * @param name
     */
    private void createGameRouteWithGameStations(String name) {
        GameRoute gameRoute = new GameRoute(name, gameStations);
        GameManager.insertNewGameRouteWithGameStaionsInDatabase(gameRoute);
    }

    /**
     *
     */
    private void clearUIComponents() {
        numberTextView.setText("");
        insertQuestionEditText.setText("");
        infoInputEditText.setText("");
        insertAnswerEditText.setText("");
    }

    /**
     *
     */
    private void showSuccessAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.success_alert_dialog, null);
        TextView messageTextView = (TextView) view.findViewById(R.id.messageTextView);
        messageTextView.setText("Spielroute erfolgreich erstellt");
        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onBackPressed();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * @param title
     * @param message
     * @param buttonText
     */
    private void showGeneralAlert(String title, String message, String buttonText) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle(title)
                .setMessage(message)
                .setPositiveButton(buttonText, new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     *
     */
    private void showNameOfGameRouteAlert() {
        final EditText gameRouteNameEditText = new EditText(this);
        gameRouteNameEditText.setSingleLine();
        gameRouteNameEditText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    return true;
                }
                return false;
            }
        });
        final AlertDialog d = new AlertDialog.Builder(this)
                .setView(gameRouteNameEditText)
                .setTitle("Wie soll die Spielroute heißen?")
                .setPositiveButton("OK", null)
                .setNegativeButton("Zurück", null)
                .create();
        d.setOnShowListener(new DialogInterface.OnShowListener() {
            @Override
            public void onShow(DialogInterface dialog) {
                Button b = d.getButton(AlertDialog.BUTTON_POSITIVE);
                b.setOnClickListener(new View.OnClickListener() {
                    @Override
                    public void onClick(View view) {
                        if (gameRouteNameEditText.getText().toString().length() > 2) {
                            gameRouteName = gameRouteNameEditText.getText().toString();
                            createGameStationFromUIValues();
                            createGameRouteWithGameStations(gameRouteName);
                            showSuccessAlert();
                            d.cancel();
                        } else
                            Toast.makeText(getApplicationContext(),
                                    "Name muss aus mindestens 3 Zeichen bestehen",
                                    Toast.LENGTH_LONG).show();
                    }
                });
            }
        });
        d.show();
    }

    /**
     * @param v
     */
    public void onAnswerEditTextChanged(View v) {
        EditText editText = (EditText) v.findViewById(R.id.insertAnswerEditText);
        editText.setSingleLine();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * @param v
     */
    public void onInfoInputEditTextChanged(View v) {
        EditText editText = (EditText) v.findViewById(R.id.infoInputEditText);
        editText.setSingleLine();
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * @param v
     */
    public void onQuestionInputEditTextChanged(View v) {
        EditText editText = (EditText) v.findViewById(R.id.insertQuestionEditText);
        editText.setOnEditorActionListener(new TextView.OnEditorActionListener() {
            @Override
            public boolean onEditorAction(TextView v, int actionId, KeyEvent event) {
                if (event != null && (event.getKeyCode() == KeyEvent.KEYCODE_ENTER)) {
                    InputMethodManager in =
                            (InputMethodManager) getSystemService(Context.INPUT_METHOD_SERVICE);
                    return true;
                }
                return false;
            }
        });
    }

    /**
     * @return
     */
    private boolean validateInput() {
        insertQuestionEditText.setError(null);
        infoInputEditText.setError(null);
        insertAnswerEditText.setError(null);

        boolean isValid = true;
        View focusView = null;

        String question = insertQuestionEditText.getText().toString();
        String info = infoInputEditText.getText().toString();
        String answer = insertAnswerEditText.getText().toString();

        if (!isQuestionValid(question)) {
            insertQuestionEditText.setError("Frage muss aus 10 bis 200 Zeichen bestehen.");
            focusView = insertQuestionEditText;
            isValid = false;
        } else if (!isInfoValid(info)) {
            infoInputEditText.setError("Nur bis zu 100 Zeichen erlaubt.");
            focusView = infoInputEditText;
            isValid = false;
        } else if (!isAnswerValid(answer)) {
            insertAnswerEditText.setError("Antwort muss aus 1 bis 50 Zeichen bestehen.");
            focusView = insertAnswerEditText;
            isValid = false;
        }

        if (!isValid)
            focusView.requestFocus();

        return isValid;
    }

    /**
     * @param questionInput
     * @return
     */
    private boolean isQuestionValid(String questionInput) {
        return ((questionInput.length() >= 10) && (questionInput.length() <= 200));
    }

    /**
     * @param infoInput
     * @return
     */
    private boolean isInfoValid(String infoInput) {
        return (infoInput.length() <= 100);
    }

    /**
     * @param answerInput
     * @return
     */
    private boolean isAnswerValid(String answerInput) {
        return ((answerInput.length() >= 1) && (answerInput.length() <= 50));
    }
}
