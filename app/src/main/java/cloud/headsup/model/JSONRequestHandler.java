package cloud.headsup.model;

import android.util.Log;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonArrayRequest;
import com.android.volley.toolbox.JsonObjectRequest;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.Locale;

/**
 * Created by Federica on 04/03/2019.
 */

public class JSONRequestHandler {

    public static void sendJSONPostRequest(Date timestamp, float distance, String userId) {
        RequestQueue queue = MyVolley.getRequestQueue();
        HashMap<String, String> params = new HashMap<>();
        params.put("DateTime", formatdateString(timestamp));
        params.put("Distance", distance + "");
        params.put("UserId", userId);


        JsonObjectRequest myReq = new JsonObjectRequest(Request.Method.POST,
                "http://headsupapi.azurewebsites.net/sensor/postdata",
                new JSONObject(params),
                createMyReqSuccessListener(),
                createMyReqErrorListener());

        queue.add(myReq);
    }

    public static ArrayList<String> sendJSONGetRequest() {

        RequestQueue queue = MyVolley.getRequestQueue();
        final ArrayList<String> results = new ArrayList<>();


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
                        } catch (JSONException je) {
                            je.printStackTrace();
                        }
                    }
                },
                createMyReqErrorListener());

        queue.add(myReq);
        return results;
    }

    private static Response.Listener<JSONObject> createMyReqSuccessListener() {
        return new Response.Listener<JSONObject>() {
            @Override
            public void onResponse(JSONObject response) {
                try {
                    Log.d("JSON answer", response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }

    private static Response.Listener<JSONArray> createMyReqSuccessListenerArray() {
        return new Response.Listener<JSONArray>() {
            @Override
            public void onResponse(JSONArray response) {
                try {
                    Log.d("JSON answer", response.toString());
                } catch (Exception e) {
                    e.printStackTrace();
                }
            }
        };
    }


    private static Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
    }

    private static String formatdateString(Date date) {
        SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd hh:mm:ss", Locale.UK);

        return sdf.format(date);
    }
}
