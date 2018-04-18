package com.example.fareed.lazeezoshipper;

import android.app.ProgressDialog;
import android.content.pm.PackageManager;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import android.location.Location;
import android.os.AsyncTask;
import android.os.Looper;
import android.support.v4.app.ActivityCompat;
import android.support.v4.app.FragmentActivity;
import android.os.Bundle;
import android.util.Log;
import android.widget.Toast;

import com.example.fareed.lazeezoshipper.Common.Common;
import com.example.fareed.lazeezoshipper.Helper.DirectionJSONParser;
import com.example.fareed.lazeezoshipper.Model.Request;
import com.example.fareed.lazeezoshipper.Remote.IGeoCoordinates;
import com.google.android.gms.location.FusedLocationProviderClient;
import com.google.android.gms.location.LocationCallback;
import com.google.android.gms.location.LocationRequest;
import com.google.android.gms.location.LocationResult;
import com.google.android.gms.location.LocationServices;
import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.SupportMapFragment;
import com.google.android.gms.maps.model.BitmapDescriptorFactory;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;
import com.google.android.gms.maps.model.Polyline;
import com.google.android.gms.maps.model.PolylineOptions;
import com.google.android.gms.tasks.OnSuccessListener;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

import retrofit2.Call;
import retrofit2.Callback;
import retrofit2.Response;

public class TrackingOrder extends FragmentActivity implements OnMapReadyCallback {

    FusedLocationProviderClient fusedLocationProviderClient;
    LocationCallback locationCallback;
    LocationRequest locationRequest;

    Location mLastLocation;

    Polyline polyline;

    IGeoCoordinates mService;
    Marker mCurrentMarker;
    private GoogleMap mMap;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_tracking_order);
        // Obtain the SupportMapFragment and get notified when the map is ready to be used.
        SupportMapFragment mapFragment = (SupportMapFragment) getSupportFragmentManager()
                .findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);
        mService=Common.getGeoCodeService();

        buildLocationRequest();
        buildLocationCallback();

        fusedLocationProviderClient = LocationServices.getFusedLocationProviderClient(this);
        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
            return;
        }
        fusedLocationProviderClient.requestLocationUpdates(locationRequest, locationCallback, Looper.myLooper());

    }


    private void buildLocationRequest() {
        locationRequest = new LocationRequest();
        locationRequest.setPriority(LocationRequest.PRIORITY_HIGH_ACCURACY);
        locationRequest.setSmallestDisplacement(10f);
        locationRequest.setInterval(5000);
        locationRequest.setFastestInterval(3000);
    }

    private void buildLocationCallback() {
        locationCallback = new LocationCallback() {
            @Override
            public void onLocationResult(LocationResult locationResult) {
                mLastLocation = locationResult.getLastLocation();
                if(mCurrentMarker!=null)
                    mCurrentMarker.setPosition(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()));

                Common.updateShippingInformation(Common.currentKey,mLastLocation);

                mMap.moveCamera(CameraUpdateFactory.newLatLng(new LatLng(mLastLocation.getLatitude(),
                        mLastLocation.getLongitude())));

                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));


                drawRoute(new LatLng(mLastLocation.getLatitude(),mLastLocation.getLongitude()),Common.currentRequest);

//                Toast.makeText(TrackingOrder.this, new StringBuilder("")
//                                .append(mLastLocation.getLatitude())
//                                .append("/")
//                                .append(mLastLocation.getLongitude()).toString()
//                        , Toast.LENGTH_SHORT).show();
            }
        };
    }

    private void drawRoute(final LatLng yourLocation, Request request) {
        if(polyline!=null)
            polyline.remove();
        if (request.getAddress()!=null && !request.getAddress().isEmpty()){
            mService.getGeoCode(request.getAddress()).enqueue(new Callback<String>(){
                @Override
                public void onResponse(Call<String> call, Response<String> response){
                    try{
                        JSONObject jsonObject =new JSONObject(response.body().toString());

                        String lat=((JSONArray)jsonObject.get("results"))
                                .getJSONObject(0)
                                .getJSONObject("geometry")
                                .getJSONObject("location")
                                .get("lat").toString();
                        String lng=((JSONArray)jsonObject.get("results"))
                                .getJSONObject(0)
                                .getJSONObject("geometry")
                                .getJSONObject("location")
                                .get("lng").toString();
                        LatLng orderLocation =new LatLng(Double.parseDouble(lat),Double.parseDouble(lng));
                        Drawable drawable=getResources().getDrawable(R.drawable.ic_shopping_cart_black_24dp);
                        //Bitmap bitmap=drawableToBitmap(drawable);
                        Bitmap bitmap= BitmapFactory.decodeResource(getResources(),R.drawable.ship);
                        bitmap=Common.scaleBitmap(bitmap,70,70);

                        MarkerOptions marker=new MarkerOptions().icon(BitmapDescriptorFactory.fromBitmap(bitmap))
                                .title("Order of "+Common.currentRequest.getPhone())
                                .position(orderLocation);
                        mMap.addMarker(marker);

                        mService.getDirections(yourLocation.latitude+","+yourLocation.longitude,
                                orderLocation.latitude+","+orderLocation.longitude)
                                .enqueue(new Callback<String>() {
                                    @Override
                                    public void onResponse(Call<String> call, Response<String> response) {
                                        Log.i("onRespone", response.body().toString());
                                        new ParserTask().execute(response.body().toString());
                                    }

                                    @Override
                                    public void onFailure(Call<String> call, Throwable t) {

                                    }
                                });

                    }catch (JSONException e){
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(Call<String> call,Throwable t){

                }
            });

        }
    }


    @Override
    protected void onStop() {
        super.onStop();
        if (fusedLocationProviderClient != null)
            fusedLocationProviderClient.removeLocationUpdates(locationCallback);

    }

    @Override
    public void onMapReady(GoogleMap googleMap) {
        mMap = googleMap;

        if (ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_FINE_LOCATION) != PackageManager.PERMISSION_GRANTED && ActivityCompat.checkSelfPermission(this, android.Manifest.permission.ACCESS_COARSE_LOCATION) != PackageManager.PERMISSION_GRANTED) {
                 return;
        }
        fusedLocationProviderClient.getLastLocation().addOnSuccessListener(new OnSuccessListener<Location>() {
            @Override
            public void onSuccess(Location location) {
                // Add a marker in Sydney and move the camera
                mLastLocation=location;
                LatLng yourLocation = new LatLng(location.getLatitude(), location.getLongitude());
                mCurrentMarker=mMap.addMarker(new MarkerOptions().position(yourLocation).title("Your location"));
                mMap.moveCamera(CameraUpdateFactory.newLatLng(yourLocation));
                mMap.animateCamera(CameraUpdateFactory.zoomTo(16.0f));
            }
        });
    }

    private class ParserTask extends AsyncTask<String,Integer,List<List<HashMap<String,String>>>> {
        ProgressDialog pd=new ProgressDialog(TrackingOrder.this);
        @Override
        protected void onPreExecute() {
            super.onPreExecute();
            pd.setMessage("Please wait");
            pd.show();
        }

        @Override
        protected List<List<HashMap<String, String>>> doInBackground(String... strings) {
            JSONObject jObject;
            List<List<HashMap<String, String>>> routes=null;
            try {
                jObject=new JSONObject(strings[0]);
                DirectionJSONParser parser=new DirectionJSONParser();

                routes=parser.parse(jObject);

            } catch (JSONException e) {
                e.printStackTrace();
            }

            return routes;
        }

        @Override
        protected void onPostExecute(List<List<HashMap<String, String>>> lists) {
            super.onPostExecute(lists);
            pd.dismiss();

//            ArrayList points = new ArrayList();;
//            PolylineOptions lineOptions = new PolylineOptions();;
//            lineOptions.width(2);
//            lineOptions.color(Color.RED);
//            MarkerOptions markerOptions = new MarkerOptions();
//            // Traversing through all the routes
//            for(int i=0;i<lists.size();i++){
//                // Fetching i-th route
//                List<HashMap<String, String>> path = lists.get(i);
//                // Fetching all the points in i-th route
//                for(int j=0;j<path.size();j++){
//                    HashMap<String,String> point = path.get(j);
//                    double lat = Double.parseDouble(point.get("lat"));
//                    double lng = Double.parseDouble(point.get("lng"));
//                    LatLng position = new LatLng(lat, lng);
//                    points.add(position);
//                }
//                // Adding all the points in the route to LineOptions
//                lineOptions.addAll(points);
//                lineOptions.width(12);
//                lineOptions.color(Color.BLUE);
//                lineOptions.geodesic(true);
//            }


            ArrayList points=null;
            PolylineOptions lineOptions=null;
            Log.i("listSize=",String.valueOf(lists.size()));
            for(int i=0; i<lists.size(); i++){
                points=new ArrayList();
                lineOptions=new PolylineOptions();
                List<HashMap<String, String>> path=lists.get(i);

                for (int j=0; j<path.size(); j++){
                    HashMap<String, String> point=path.get(j);

                    double lat=Double.parseDouble(point.get("lat"));
                    double lng=Double.parseDouble(point.get("lng"));

                    LatLng position=new LatLng(lat,lng);

                    points.add(position);
                }
                lineOptions.addAll(points);
                lineOptions.width(12);
                lineOptions.color(Color.BLUE);
                lineOptions.geodesic(true);
            }

            // Drawing polyline in the Google Map for the i-th route
            if(points.size()!=0){
                Toast.makeText(TrackingOrder.this, "In if", Toast.LENGTH_SHORT).show();
                mMap.addPolyline(lineOptions);//to avoid crash
            }
            // mMap.addPolyline(lineOptions);
        }
    }

}
