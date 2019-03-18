package cloud.headsup.controller;

import android.content.Intent;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import com.android.volley.Request;
import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.JsonObjectRequest;
import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.management.ManagementException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.UserProfile;
import com.jaredrummler.android.device.DeviceName;


import org.json.JSONObject;

import cloud.headsup.R;
import cloud.headsup.model.JSONRequestHandler;
import cloud.headsup.model.MyVolley;

public class MainActivity extends AppCompatActivity {

   private static final int BG_INTENT = 1000;
   private Intent mServiceIntent;
    public static final String EXTRA_ACCESS_TOKEN = "com.auth0.ACCESS_TOKEN";
    private UserProfile userProfile;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        //Obtain the token from the Intent's extras
        final String accessToken = getIntent().getStringExtra(LogInActivity.EXTRA_ACCESS_TOKEN);

        MyVolley.init(this);
        Button btnStart = findViewById(R.id.btn_start);
        Button btnStop = findViewById(R.id.btn_stop);
        Button btnPlot = findViewById(R.id.btn_plot);

        String deviceName = DeviceName.getDeviceName();
        Log.d("Device name: ", deviceName);

        DeviceName.with(this).request(new DeviceName.Callback() {

            @Override public void onFinished(DeviceName.DeviceInfo info, Exception error) {
                String manufacturer = info.manufacturer;  // "Samsung"
                String name = info.marketName;            // "Galaxy S8+"
                String model = info.model;                // "SM-G955W"
                String codename = info.codename;          // "dream2qltecan"
                String deviceName = info.getName();       // "Galaxy S8+"
                Log.d("Manufacturer: ", manufacturer);
                Log.d("Name: ", name);
                Log.d("Model: ", model);
                Log.d("Device name: ", deviceName);
            }
        });

        btnStart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Toast.makeText(MainActivity.this, "Heads Up is running in the background", Toast.LENGTH_LONG).show();
                mServiceIntent = new Intent();
                mServiceIntent.putExtra(accessToken,EXTRA_ACCESS_TOKEN);

                BackgroundIntent.enqueueWork(MainActivity.this, BackgroundIntent.class, BG_INTENT, mServiceIntent);
            }
        });

        btnStop.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                int pid = android.os.Process.myPid();
                android.os.Process.killProcess(pid);

                Intent intent = new Intent(Intent.ACTION_MAIN);
                intent.addCategory(Intent.CATEGORY_HOME);
                startActivity(intent);
            }
        });

        btnPlot.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Intent intent = new Intent(MainActivity.this, GraphActivity.class);
                startActivity(intent);
            }
        });

        Button logoutButton = (Button) findViewById(R.id.logout);
        logoutButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                logout();
            }
        });


    }

    @Override
    protected void onPause() {
        super.onPause();
    }

    private Response.Listener<JSONObject> createMyReqSuccessListener() {
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


    private Response.ErrorListener createMyReqErrorListener() {
        return new Response.ErrorListener() {
            @Override
            public void onErrorResponse(VolleyError error) {

            }
        };
    }

    private void logout() {
        Intent intent = new Intent(this, LogInActivity.class);
        intent.putExtra(LogInActivity.KEY_CLEAR_CREDENTIALS, true);
        startActivity(intent);
        finish();
    }
}
