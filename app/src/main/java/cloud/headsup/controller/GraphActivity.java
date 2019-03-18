package cloud.headsup.controller;

import android.provider.ContactsContract;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;
import com.jjoe64.graphview.GraphView;
import com.jjoe64.graphview.helper.DateAsXAxisLabelFormatter;
import com.jjoe64.graphview.series.DataPoint;
import com.jjoe64.graphview.series.LineGraphSeries;

import org.json.JSONArray;
import org.json.JSONException;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.Date;

import cloud.headsup.R;
import cloud.headsup.model.MyVolley;

public class GraphActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_graph);

        final GraphView graphView = findViewById(R.id.graph);


        final ArrayList<String> results = new ArrayList<>();

        RequestQueue queue = MyVolley.getRequestQueue();
        JsonArrayRequest myReq = new JsonArrayRequest(Request.Method.GET,
                "http://headsupapi.azurewebsites.net/sensor/getsensordata",
                null,
                new Response.Listener<JSONArray>() {
                    @Override
                    public void onResponse(JSONArray response) {
                        try {
                            for (int i = 0; i < response.length(); i++) {
                                results.add(response.get(i).toString());
                            }
                            Log.d("Array result ", Arrays.toString(results.toArray()));
                            ArrayList<String> dateStrings = new ArrayList<>();
                            ArrayList<Float> distances = new ArrayList<>();

                            for (String s : results) {
                                dateStrings.add(s.substring(13, 32).replace('T', ' '));
                                distances.add(Float.parseFloat(s.substring(45, 49)));
                            }
                            SimpleDateFormat format = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss");
                            ArrayList<Date> dates = new ArrayList<>();

                            try {
                                for (String s : dateStrings) {
                                    dates.add(format.parse(s));
                                }
                            } catch (ParseException pe) {
                                pe.printStackTrace();
                            }

                            DataPoint[] dataPoints = new DataPoint[dates.size()];

                            for (int i = 0; i < dataPoints.length; i++) {
                                dataPoints[i] = new DataPoint(dates.get(i), distances.get(i));
                            }

                            LineGraphSeries<DataPoint> series = new LineGraphSeries<>(dataPoints);

                            graphView.addSeries(series);

                            graphView.getGridLabelRenderer().setLabelFormatter(new DateAsXAxisLabelFormatter(GraphActivity.this));
                            graphView.getGridLabelRenderer().setNumHorizontalLabels(dates.size()); // only 4 because of the space

                            // set manual x bounds to have nice steps
                            graphView.getViewport().setMinX(dates.get(0).getTime());
                            graphView.getViewport().setMaxX(dates.get(dates.size() - 1).getTime());
                            graphView.getViewport().setXAxisBoundsManual(true);

                            // as we use dates as labels, the human rounding to nice readable numbers
                            // is not necessary
                            graphView.getGridLabelRenderer().setHumanRounding(false);

                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                    }
                },
                new Response.ErrorListener() {
                    @Override
                    public void onErrorResponse(VolleyError error) {

                    }
                });

        queue.add(myReq);


    }
}
