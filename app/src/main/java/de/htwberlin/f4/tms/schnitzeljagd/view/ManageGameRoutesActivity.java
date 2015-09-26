package de.htwberlin.f4.tms.schnitzeljagd.view;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.ListView;

import org.json.JSONException;

import java.io.File;
import java.io.IOException;
import java.util.List;

import de.htwberlin.f4.tms.schnitzeljagd.R;
import de.htwberlin.f4.tms.schnitzeljagd.core.ApplicationController;
import de.htwberlin.f4.tms.schnitzeljagd.core.AvailableGameRoutesAdapter;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameManager;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameRoute;
import de.htwberlin.f4.tms.schnitzeljagd.core.SchnitzelFileManager;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class ManageGameRoutesActivity extends Activity {
    private List<List<String>> availableGameRoutes;
    private AvailableGameRoutesAdapter avGRAdapter;
    private ListView avGRListView;
    private int positionSelectedListElement;
    private Button deleteButton;
    private Button exportButton;
    private Button importButton;
    private Button createButton;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_manage_game_routes);
        initalizeUIComponents();
        availableGameRoutes = GameManager.getGameRoutesWithoutGameStations();
        avGRAdapter = new AvailableGameRoutesAdapter(this,
                R.layout.row_available_gameroute,
                availableGameRoutes);
        avGRListView.setAdapter(avGRAdapter);
        avGRListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                view.setSelected(true);
                positionSelectedListElement = position;
                deleteButton.setEnabled(true);
                exportButton.setEnabled(true);
            }
        });
    }

    /**
     *
     */
    @Override
    protected void onStart() {
        super.onStart();
        deleteButton.setEnabled(false);
        exportButton.setEnabled(false);
        positionSelectedListElement = -1;
        refreshAvailableGameRoutesInListView();
    }

    /**
     *
     */
    @Override
    public void onResume() {
        super.onResume();
        deleteButton.setEnabled(false);
        exportButton.setEnabled(false);
        positionSelectedListElement = -1;
        refreshAvailableGameRoutesInListView();
    }

    /**
     *
     */
    private void initalizeUIComponents() {
        deleteButton = (Button) findViewById(R.id.deleteRouteButton);
        exportButton = (Button) findViewById(R.id.exportRouteButton);
        avGRListView = (ListView) findViewById(R.id.availableGameRoutesListView);
        importButton = (Button) findViewById(R.id.importRouteButton);
        createButton = (Button) findViewById(R.id.createRouteButton);
    }

    /**
     * @param view
     */
    public void onCreateButtonClicked(View view) {
        Intent intent = new Intent(view.getContext(), CreateGameRouteActivity.class);
        view.getContext().startActivity(intent);
    }

    /**
     * @param view
     */
    public void onDeleteButtonClicked(View view) {
        if (positionSelectedListElement > -1) {
            showDeleteAlert(availableGameRoutes.get(positionSelectedListElement).get(1));
        }
    }

    /**
     * @param view
     */
    public void onImportButtonClicked(View view) {
        try {
            SchnitzelFileManager sfManager =
                    ApplicationController.getInstance().getSchnitzelFileManager();
            showSchnitzelFileChooserAlert(sfManager.getSchnitzelFiles());
        } catch (IOException ioex) {
            showGeneralAlert("Kein externer Speicher gefunden",
                    "Um Spielrouten zu importieren oder exportieren "
                            + "wird auf dem Gerät ein externer Speicher benötigt.",
                    "OK");
        }
    }

    /**
     * @param view
     */
    public void onExportButtonClicked(View view) {
        if (positionSelectedListElement > -1) {
            SchnitzelFileManager sfManager = null;
            try {
                sfManager = ApplicationController.getInstance().getSchnitzelFileManager();
            } catch (IOException ex) {
                showGeneralAlert("Kein externer Speicher gefunden",
                        "Um Spielrouten zu importieren oder exportieren "
                                + "wird auf dem Gerät ein externer Speicher benötigt.",
                        "OK");
            }
            try {
                GameRoute selectedGameRoute = GameManager.loadGameRoute(
                        Integer.parseInt(availableGameRoutes.get(positionSelectedListElement).get(0)));
                exportGameRoute(selectedGameRoute);
                showGeneralAlert("Export erfolgreich", "Die Route \""
                        + selectedGameRoute.getFileName() + "\" wurde erfolgreich in \n\""
                        + sfManager.getSchnitzelFilesDir() + File.separator
                        + selectedGameRoute.getFileName() + "\" geschrieben.", "OK");
            } catch (Exception ex) {
                showGeneralAlert("Export fehlgeschlagen",
                        "Ein Fehler ist beim Speichern der Route aufgetreten.", "OK");
            }
        }
    }

    /**
     * @param gameRouteName
     */
    private void showDeleteAlert(String gameRouteName) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Route löschen")
                .setMessage("Soll die Route " + gameRouteName + " wirklich gelöscht werden?")
                .setPositiveButton("Ja, löschen!", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        GameRoute selectedGameRoute = GameManager.loadGameRoute(Integer.parseInt(
                                availableGameRoutes.get(positionSelectedListElement).get(0)));
                        deleteGameRoute(selectedGameRoute);
                        dialog.cancel();
                    }
                })
                .setNegativeButton("Nein", new DialogInterface.OnClickListener() {
                    public void onClick(DialogInterface dialog, int id) {
                        dialog.cancel();
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
     * @param schnitzelFiles
     */
    private void showSchnitzelFileChooserAlert(List<File> schnitzelFiles) {
        AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setTitle("Welche Route soll importiert werden?");
        final ArrayAdapter<String> arrayAdapter =
                new ArrayAdapter<String>(this, android.R.layout.select_dialog_singlechoice);
        for (File schnitzelFile : schnitzelFiles)
            arrayAdapter.add(schnitzelFile.getName());
        builder.setAdapter(arrayAdapter, new DialogInterface.OnClickListener() {
            @Override
            public void onClick(final DialogInterface dialog, int position) {
                try {
                    importGameRoute(arrayAdapter.getItem(position));
                } catch (Exception ex) {
                    showGeneralAlert("Import fehlgeschlagen",
                            "Ein Fehler ist beim Importieren der Route aufgetreten.", "OK");
                } finally {
                    dialog.dismiss();
                }
            }
        });
        builder.show();
    }

    /**
     * @param gameRouteToDelete
     */
    private void deleteGameRoute(GameRoute gameRouteToDelete) {
        GameManager.deleteGameRoute(gameRouteToDelete.getRouteID());
        availableGameRoutes.remove(positionSelectedListElement);
        avGRAdapter.notifyDataSetChanged();
        positionSelectedListElement = -1;
        deleteButton.setEnabled(false);
        exportButton.setEnabled(false);
    }

    /**
     * @param gameRouteToExport
     * @throws IOException
     * @throws JSONException
     */
    private void exportGameRoute(GameRoute gameRouteToExport) throws IOException, JSONException {
        GameManager.exportToFile(gameRouteToExport);
        positionSelectedListElement = -1;
        exportButton.setEnabled(false);
        deleteButton.setEnabled(false);
    }

    /**
     * @param schnitzelFile
     * @throws IOException
     * @throws JSONException
     */
    private void importGameRoute(String schnitzelFile) throws IOException, JSONException {
        SchnitzelFileManager sfManager =
                ApplicationController.getInstance().getSchnitzelFileManager();
        String path = sfManager.getSchnitzelFilesDir() + File.separator + schnitzelFile;
        GameRoute importedGameroute = GameManager.importFromFile(new File(path));
        GameManager.insertNewGameRouteWithGameStaionsInDatabase(importedGameroute);
        refreshAvailableGameRoutesInListView();
    }

    /**
     *
     */
    private void refreshAvailableGameRoutesInListView() {
        int size = availableGameRoutes.size();
        for (int i = 0; i < size; i++)
            availableGameRoutes.remove(0);
        List<List<String>> tempGameRoutes = GameManager.getGameRoutesWithoutGameStations();
        for (List<String> gRoute : tempGameRoutes)
            availableGameRoutes.add(gRoute);
        avGRAdapter.notifyDataSetChanged();
    }
}
