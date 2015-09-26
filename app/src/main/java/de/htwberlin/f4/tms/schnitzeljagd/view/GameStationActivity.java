package de.htwberlin.f4.tms.schnitzeljagd.view;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v4.app.FragmentActivity;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.TextView;
import android.widget.Toast;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.CameraPosition;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.MarkerOptions;

import de.htwberlin.f4.tms.schnitzeljagd.R;
import de.htwberlin.f4.tms.schnitzeljagd.core.ApplicationController;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameRoute;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameStation;
import de.htwberlin.f4.tms.schnitzeljagd.core.LocationService;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class GameStationActivity extends FragmentActivity {
    private TextView questionNumberTextView;
    private TextView questionTextView;
    private ImageView infoImageView;
    private EditText answerEditText;
    private Button mainMenuButton;
    private Button okButton;
    private GoogleMap googleMap;
    private LocationManager manager;
    private LocationService locationServiceObject;
    private GameStation currentGameStation;
    private int gameStationIndex;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_game_station);
        initalizeUIComponents();
        manager = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        locationServiceObject = new LocationService();
        gameStationIndex = getIntent().getExtras().getInt("gameStationIndex");
        loadGameStationInput(gameStationIndex);
    }

    /**
     *
     */
    @Override
    public void onBackPressed() {
        ApplicationController.getInstance().setCurrentGameRoute(null);
        this.finish();
    }

    /**
     *
     */
    @Override
    protected void onResume() {
        super.onResume();
        if (!manager.isProviderEnabled(LocationManager.GPS_PROVIDER)) {
            locationServiceObject.buildAlertMessageNoGps(this);
        }
        if (!locationServiceObject.isOnline(this)) {
            locationServiceObject.buildAlertMessageNoNetworkProvider(this);
        }
    }

    /**
     *
     */
    private void initalizeUIComponents() {
        questionNumberTextView = (TextView) findViewById(R.id.questionNumberTextView);
        questionTextView = (TextView) findViewById(R.id.questionTextView);
        infoImageView = (ImageView) findViewById(R.id.infoImageView);
        answerEditText = (EditText) findViewById(R.id.answerEditText);
        mainMenuButton = (Button) findViewById(R.id.mainMenuButton);
        okButton = (Button) findViewById(R.id.okButton);
        googleMap = ((SupportMapFragment) getSupportFragmentManager().
                findFragmentById(R.id.mapFragment)).getMap();
    }

    /**
     *
     */
    private void setGameStationValues() {
        questionNumberTextView.setText(String.valueOf(gameStationIndex + 1));
        if (gameStationIndex < 8)
            questionNumberTextView.setText("0" + questionNumberTextView.getText());
        questionTextView.setText(currentGameStation.getQuestion());
        answerEditText.setText("");
        if (currentGameStation.getInfo().isEmpty()) {
            infoImageView.setEnabled(false);
            infoImageView.setImageResource(R.drawable.icon_info_gray);
        } else {
            infoImageView.setEnabled(true);
            infoImageView.setImageResource(R.drawable.icon_info);
        }
        if (googleMap != null) {
            googleMap.clear();
            double latitude = currentGameStation.getGeoPoint().getLatitude();
            double longitude = currentGameStation.getGeoPoint().getLongitude();
            googleMap.addMarker(new MarkerOptions().position(
                    new LatLng(latitude, longitude)).title("Schnitzelpoint"));
            CameraPosition cameraPosition = new CameraPosition.Builder()
                    .target(new LatLng(latitude, longitude))
                    .zoom(15.0f)
                    .bearing(0.0f)
                    .tilt(45.0f)
                    .build();
            googleMap.animateCamera(CameraUpdateFactory.newCameraPosition(cameraPosition));
            googleMap.getUiSettings().setZoomControlsEnabled(true);
            googleMap.setMyLocationEnabled(true);
        }
    }

    /**
     * @param v
     */
    public void onOKButtonClick(View v) {
        String answer = answerEditText.getText().toString().toLowerCase().trim();
        if (currentGameStation.getAnswer().toLowerCase().compareTo(answer) == 0) {
            Toast.makeText(getApplicationContext(), "Richtig!", Toast.LENGTH_SHORT).show();
            currentGameStation.setAsFinished();
            GameRoute currentGameRoute = ApplicationController.getInstance().getCurrentGameRoute();
            if ((gameStationIndex + 1) < currentGameRoute.getGameStations().size()) {
                gameStationIndex++;
                loadGameStationInput(gameStationIndex);
            } else {
                showSuccessAlert();
            }
        } else {
            Toast.makeText(getApplicationContext(),
                    "Die Antwort ist leider falsch, versuche es nochmal!",
                    Toast.LENGTH_LONG).show();
        }
    }

    /**
     * @param v
     */
    public void onInfoImageViewClick(View v) {
        showInfoAlert();
    }

    /**
     * @param v
     */
    public void onMainMenuButtonClick(View v) {
        onBackPressed();
    }

    /**
     * @param currentGSIndex
     */
    private void loadGameStationInput(int currentGSIndex) {
        currentGameStation = ApplicationController.getInstance().
                getCurrentGameRoute().getGameStations().get(currentGSIndex);
        setGameStationValues();
    }

    /**
     *
     */
    private void showSuccessAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        LayoutInflater factory = LayoutInflater.from(this);
        final View view = factory.inflate(R.layout.success_alert_dialog, null);
        TextView messageTextView = (TextView) view.findViewById(R.id.messageTextView);
        messageTextView.setText("Route erfolgreich absolviert");
        builder.setView(view)
                .setCancelable(false)
                .setPositiveButton("Hauptmenü", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                        onBackPressed();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     *
     */
    private void showInfoAlert() {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Anmerkungen zur Frage")
                .setMessage(currentGameStation.getInfo())
                .setCancelable(false)
                .setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
                    }
                });
        AlertDialog alert = builder.create();
        alert.show();
    }

    /**
     * @param v
     */
    public void onEditTextChanged(View v) {
        EditText editText = (EditText) v.findViewById(R.id.answerEditText);
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
}
