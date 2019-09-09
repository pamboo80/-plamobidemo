package in.bangbit.plamobidemo;

import android.Manifest;
import android.app.Application;
import android.content.Context;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.location.Location;
import android.os.Bundle;
import android.os.Handler;
import android.support.annotation.NonNull;
import android.support.v4.app.ActivityCompat;
import android.widget.Toast;

import com.android.volley.RequestQueue;
import com.android.volley.Response;
import com.android.volley.VolleyError;
import com.android.volley.toolbox.Volley;
import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.location.LocationListener;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.model.LatLng;

import java.util.ArrayList;

public class MainSingleton extends Application {

    private static Context context;
    private static GoogleApiClient apiClient;
    public static RequestQueue volleyQueue;
    private static DatabaseHelper databaseHelper;

    public static ArrayList<Message> messages;
    private static Location location;

    @Override
    public void onCreate() {
        super.onCreate();
        context = this;
        volleyQueue = Volley.newRequestQueue(context);
        databaseHelper = new DatabaseHelper(this);
        apiClient = new GoogleApiClient.Builder(context)
                .addApi(LocationServices.API)
                .addConnectionCallbacks(new GoogleApiClient.ConnectionCallbacks() {
                    @Override
                    public void onConnected(Bundle bundle) {
                        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                            // TODO: Consider calling
                            //    ActivityCompat#requestPermissions
                            // here to request the missing permissions, and then overriding
                            //   public void onRequestPermissionsResult(int requestCode, String[] permissions,
                            //                                          int[] grantResults)
                            // to handle the case where the user grants the permission. See the documentation
                            // for ActivityCompat#requestPermissions for more details.
                            Toast.makeText(context, R.string.you_must_give_geo_permission, Toast.LENGTH_LONG).show();
                            MainSingleton.getDatabaseHelper().getMessages(new Response.Listener<ArrayList<Message>>() {
                                @Override
                                public void onResponse(ArrayList<Message> response) {
                                    messages = response;
                                    startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                }
                            });                            return;
                        }
                        LocationServices.FusedLocationApi.requestLocationUpdates(apiClient,
                                LocationRequest.create()
                                        .setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY)
                                        //.setInterval(1)
                                        .setNumUpdates(1)
                                        .setMaxWaitTime(10000),
                                new LocationListener() {
                                    @Override
                                    public void onLocationChanged(Location location) {
                                        MainSingleton.location = location;
                                        volleyQueue.add(new MessagesRequest(location.getLatitude(), location.getLongitude(),
                                                new Response.Listener<ArrayList<Message>>() {
                                                    @Override
                                                    public void onResponse(ArrayList<Message> response) {
                                                        messages = response;
                                                        startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                    }
                                                },
                                                new Response.ErrorListener() {
                                                    @Override
                                                    public void onErrorResponse(VolleyError error) {
                                                        Toast.makeText(context, error.getMessage(), Toast.LENGTH_LONG).show();
                                                        MainSingleton.getDatabaseHelper().getMessages(new Response.Listener<ArrayList<Message>>() {
                                                            @Override
                                                            public void onResponse(ArrayList<Message> response) {
                                                                messages = response;
                                                                startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                                            }
                                                        });
                                                    }
                                                }
                                        ));
                                    }
                                });
                        new Handler().postDelayed(new Runnable() {
                            @Override
                            public void run() {
                                if(MainSingleton.location==null) {
                                    MainSingleton.getDatabaseHelper().getMessages(new Response.Listener<ArrayList<Message>>() {
                                        @Override
                                        public void onResponse(ArrayList<Message> response) {
                                            messages = response;
                                            startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                                        }
                                    });
                                }
                            }
                        },10000);
                    }

                    @Override
                    public void onConnectionSuspended(int i) {

                    }
                })
                .addOnConnectionFailedListener(new GoogleApiClient.OnConnectionFailedListener() {
                    @Override
                    public void onConnectionFailed(@NonNull ConnectionResult connectionResult) {
                        MainSingleton.getDatabaseHelper().getMessages(new Response.Listener<ArrayList<Message>>() {
                            @Override
                            public void onResponse(ArrayList<Message> response) {
                                messages = response;
                                startActivity(new Intent(context, LoginActivity.class).addFlags(Intent.FLAG_ACTIVITY_NEW_TASK));
                            }
                        });
                    }
                })
                .build();
        apiClient.connect();
    }

    public static GoogleApiClient getApiClient() {
        return apiClient;
    }

    public static float convertDpToPixel(float dp) {
        return dp * context.getResources().getDisplayMetrics().density;
    }

    public static Float calculateDistanceFromMeInMiles(LatLng toPoint) throws NullPointerException { //if lastLocation == null
        if (ActivityCompat.checkSelfPermission(context, Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return null;
        }
        Location lastLocation = LocationServices.FusedLocationApi.getLastLocation(apiClient);
        float[] distance = new float[1];
        Location.distanceBetween(lastLocation.getLatitude(),lastLocation.getLongitude(),
                toPoint.latitude,toPoint.longitude, distance);
        return (float)(distance[0]/1609.34);
    }

    public static int getDaysAgoFromNow(long millisTo) {
        return (int)((float)(System.currentTimeMillis() - millisTo) / (float)(3600*1000*24));
    }

    public static DatabaseHelper getDatabaseHelper() {
        return databaseHelper;
    }
}
