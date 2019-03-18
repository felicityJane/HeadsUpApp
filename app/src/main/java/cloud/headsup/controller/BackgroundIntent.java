package cloud.headsup.controller;

import android.content.Intent;
import android.hardware.Sensor;
import android.hardware.SensorEvent;
import android.hardware.SensorEventListener;
import android.hardware.SensorManager;
import android.support.annotation.Nullable;
import android.support.v4.app.JobIntentService;
import android.util.Log;

import com.auth0.android.Auth0;
import com.auth0.android.authentication.AuthenticationAPIClient;
import com.auth0.android.authentication.AuthenticationException;
import com.auth0.android.callback.BaseCallback;
import com.auth0.android.management.ManagementException;
import com.auth0.android.management.UsersAPIClient;
import com.auth0.android.result.UserProfile;

import cloud.headsup.model.JSONRequestHandler;

import java.util.Date;
import java.util.Random;


/**
 * Created by Federica on 23/02/2019.
 */

public class BackgroundIntent extends JobIntentService {

    private SensorManager sensorManager;
    private Sensor proximitySensor;
    private SensorEventListener proximitySensorListener;



    @Override
    public void onCreate() {
        super.onCreate();
        sensorManager = (SensorManager)getSystemService(SENSOR_SERVICE);
        proximitySensor = sensorManager.getDefaultSensor(Sensor.TYPE_PROXIMITY);
    }

    @Override
    protected void onHandleWork(@Nullable final Intent intent) {

        final Random rnd = new Random();
        if (proximitySensor != null) {
            proximitySensorListener = new SensorEventListener() {
                @Override
                public void onSensorChanged(SensorEvent sensorEvent) {

                    float randomReading = rnd.nextFloat() * 2;
                    float randomReadingBig = 7 + rnd.nextFloat() * 1.5f;
                    Log.d("Prox sensor", "Proximity distance: " + sensorEvent.values[0]);
                    JSONRequestHandler.sendJSONPostRequest(new Date(), sensorEvent.values[0] < 9 ? sensorEvent.values[0] + randomReading :
                    sensorEvent.values[0] - randomReadingBig, String.valueOf(getUserInfo(intent)));
                }

                @Override
                public void onAccuracyChanged(Sensor sensor, int i) {

                }
            };

            sensorManager.registerListener(proximitySensorListener, proximitySensor, SensorManager.SENSOR_DELAY_NORMAL);
        }

        getUserInfo(intent);
    }

    public String[] getUserInfo(@Nullable Intent intent) {
        final String[] username = new String[]{};

        //Obtain the token from the Intent's extras
        Auth0 auth0 = new Auth0(this);
        auth0.setOIDCConformant(true);
        String accessToken = intent.getStringExtra(LogInActivity.EXTRA_ACCESS_TOKEN);
        final UsersAPIClient usersClient = new UsersAPIClient(auth0, accessToken);
        AuthenticationAPIClient authenticationAPIClient = new AuthenticationAPIClient(auth0);
        authenticationAPIClient.userInfo(accessToken).start(new BaseCallback<UserProfile, AuthenticationException>() {
            @Override
            public void onSuccess(UserProfile payload) {
                usersClient.getProfile(payload.getId()).start(new BaseCallback<UserProfile, ManagementException>() {
                    @Override
                    public void onSuccess(UserProfile payload) {
                        payload.getEmail();
                        payload.getId();
                       username[0] = payload.getName();
                    }

                    @Override
                    public void onFailure(ManagementException error) {

                    }
                });
            }

            @Override
            public void onFailure(AuthenticationException error) {

            }
        });
        return username;
    }
}
