package de.htwberlin.f4.tms.schnitzeljagd.view;

import android.app.Activity;
import android.content.Intent;
import android.os.Bundle;
import android.view.View;
import android.widget.AdapterView;
import android.widget.ListView;

import java.util.List;

import de.htwberlin.f4.tms.schnitzeljagd.R;
import de.htwberlin.f4.tms.schnitzeljagd.core.ApplicationController;
import de.htwberlin.f4.tms.schnitzeljagd.core.AvailableGameRoutesAdapter;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameManager;
import de.htwberlin.f4.tms.schnitzeljagd.core.GameRoute;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class SelectGameRoutesActivity extends Activity {
    private List<List<String>> availableGameRoutes;
    private AvailableGameRoutesAdapter avGRAdapter;
    private ListView avGRListView;

    /**
     * @param savedInstanceState
     */
    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_select_game_routes);
        availableGameRoutes = GameManager.getGameRoutesWithoutGameStations();
        avGRListView = (ListView) findViewById(R.id.availableGameRoutesListView);
        avGRAdapter = new AvailableGameRoutesAdapter(
                this, R.layout.row_available_gameroute, availableGameRoutes);
        avGRListView.setAdapter(avGRAdapter);
        avGRListView.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                GameRoute selectedGameRoute = GameManager.loadGameRoute(Integer.parseInt(
                        availableGameRoutes.get(position).get(0)));
                ApplicationController.getInstance().setCurrentGameRoute(selectedGameRoute);

                Intent intent = new Intent(view.getContext(), GameStationActivity.class);
                intent.putExtra("gameStationIndex", 0);
                SelectGameRoutesActivity.this.startActivity(intent);
            }
        });
    }
}
