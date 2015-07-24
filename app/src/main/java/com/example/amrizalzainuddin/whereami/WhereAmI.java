package com.example.amrizalzainuddin.whereami;

import android.content.Context;
import android.location.Address;
import android.location.Criteria;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.model.Circle;
import com.google.android.gms.maps.model.CircleOptions;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import java.io.IOException;
import java.util.List;
import java.util.Locale;


public class WhereAmI extends ActionBarActivity {

    GoogleMap googleMap;
    Circle circle;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_where_am_i);

        googleMap = ((MapFragment)getFragmentManager().findFragmentById(R.id.myMapView)).getMap();
        googleMap.setMapType(GoogleMap.MAP_TYPE_SATELLITE);
        googleMap.setOnMapLoadedCallback(new GoogleMap.OnMapLoadedCallback() {
            @Override
            public void onMapLoaded() {
                if (circle == null) {
                    CircleOptions circleOptions = new CircleOptions();

                    circleOptions.center(new LatLng(0,0));
                    circleOptions.radius(100);
                    circleOptions.fillColor(0x110000FF);
                    circleOptions.strokeColor(0xFF0000FF);
                    circleOptions.strokeWidth(2.0f);

                    circle = googleMap.addCircle(circleOptions);
                }
            }
        });

        LocationManager locationManager;
        String svcName = Context.LOCATION_SERVICE;
        locationManager = (LocationManager)getSystemService(svcName);

        Criteria criteria = new Criteria();
        criteria.setAccuracy(Criteria.ACCURACY_FINE);
        criteria.setPowerRequirement(Criteria.POWER_LOW);
        criteria.setAltitudeRequired(false);
        criteria.setBearingRequired(false);
        criteria.setSpeedRequired(false);
        criteria.setCostAllowed(true);
        String provider = locationManager.getBestProvider(criteria, true);

        Location l = locationManager.getLastKnownLocation(provider);
        
        updateWithNewLocation(l);

        locationManager.requestLocationUpdates(provider, 2000, 10, locationListener);
    }

    private final LocationListener locationListener = new LocationListener() {
        @Override
        public void onLocationChanged(Location location) {
            updateWithNewLocation(location);
        }

        @Override
        public void onStatusChanged(String provider, int status, Bundle extras) {

        }

        @Override
        public void onProviderEnabled(String provider) {

        }

        @Override
        public void onProviderDisabled(String provider) {

        }
    };

    private void updateWithNewLocation(Location location) {
        TextView myLocationText;
        myLocationText = (TextView)findViewById(R.id.myLocationText);

        String latLongString = "No location found";
        String addressString = "No address found";

        if(location != null){
            LatLng currentPosition = new LatLng(location.getLatitude(), location.getLongitude());

            googleMap.animateCamera(CameraUpdateFactory.newLatLngZoom(currentPosition, 17));
            circle.setCenter(currentPosition);

            double lat = location.getLatitude();
            double lng = location.getLongitude();
            latLongString = "Lat:" + lat + "\nLong:" + lng;

            double latitude = location.getLatitude();
            double longitude = location.getLongitude();

            Geocoder gc = new Geocoder(this, Locale.getDefault());

            try{
                List<Address> addresses = gc.getFromLocation(latitude, longitude, 1);
                StringBuilder sb = new StringBuilder();
                if(addresses.size() > 0){
                    Address address = addresses.get(0);

                    for(int i=0; i<address.getMaxAddressLineIndex(); i++)
                        sb.append(address.getAddressLine(i)).append("\n");

                    sb.append(address.getLocality()).append("\n");
                    sb.append(address.getPostalCode()).append("\n");
                    sb.append(address.getCountryName());

                    addressString = sb.toString();
                }
            }catch (IOException e){}
        }

        myLocationText.setText("Your Current Position is:\n" + latLongString
                                + "\n\n" + addressString);
    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_where_am_i, menu);
        return true;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        // Handle action bar item clicks here. The action bar will
        // automatically handle clicks on the Home/Up button, so long
        // as you specify a parent activity in AndroidManifest.xml.
        int id = item.getItemId();

        //noinspection SimplifiableIfStatement
        if (id == R.id.action_settings) {
            return true;
        }

        return super.onOptionsItemSelected(item);
    }
}
