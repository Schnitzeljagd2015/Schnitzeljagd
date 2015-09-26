package de.htwberlin.f4.tms.schnitzeljagd.core;

import android.app.Activity;
import android.content.Context;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import java.util.List;

import de.htwberlin.f4.tms.schnitzeljagd.R;

/**
 * @author Tino Herrmann, Fabian Schmöker
 * @version 1.0
 */
public class AvailableGameRoutesAdapter extends ArrayAdapter<List<String>> {
    private Context context;
    private int layoutResourceId;
    private List<List<String>> availableGameRoutes; // structure: id, filename, solved

    /**
     * @param context
     * @param layoutResourceId
     * @param data
     */
    public AvailableGameRoutesAdapter(Context context,
                                      int layoutResourceId,
                                      List<List<String>> data) {
        super(context, layoutResourceId, data);
        this.layoutResourceId = layoutResourceId;
        this.context = context;
        this.availableGameRoutes = data;
    }

    /**
     * @param position
     * @param convertView
     * @param parent
     * @return
     */
    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        View row = convertView;
        if (row == null) {
            LayoutInflater inflater = ((Activity) context).getLayoutInflater();
            row = inflater.inflate(layoutResourceId, parent, false);
        }
        TextView gameRouteFileNameTextView =
                (TextView) row.findViewById(R.id.gameRouteFileNameTextView);
        ImageView gameRouteStatusImageView =
                (ImageView) row.findViewById(R.id.gameRouteStatusImageView);

        List<String> avGameRoute = availableGameRoutes.get(position);
        if (avGameRoute != null) {
            if (gameRouteFileNameTextView != null) {
                gameRouteFileNameTextView.setText(avGameRoute.get(1));
            }
            if (gameRouteStatusImageView != null) {
                if (avGameRoute.get(2).equals("true"))
                    gameRouteStatusImageView.setImageResource(R.drawable.icon_true);
                else if (avGameRoute.get(2).equals("false"))
                    gameRouteStatusImageView.setImageResource(R.drawable.icon_false);
            }
        }
        return row;
    }
}
