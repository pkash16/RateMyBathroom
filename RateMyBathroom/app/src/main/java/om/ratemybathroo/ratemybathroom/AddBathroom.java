package om.ratemybathroo.ratemybathroom;

import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.graphics.Color;
import android.location.Address;
import android.location.Geocoder;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.io.IOException;
import java.util.ArrayList;
import java.util.List;


public class AddBathroom extends ActionBarActivity {

    private EditText title, address, comment;
    private RatingBar rating;
    private ParseUser user;
    public double lat, lng;
    boolean manualAddress;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_add_bathroom);
        lat = 0;
        lng = 0;
        title = (EditText) findViewById(R.id.et_title);
        address = (EditText) findViewById(R.id.et_address);
        rating = (RatingBar) findViewById(R.id.rb_rating);
        comment = (EditText) findViewById(R.id.et_comment);
        user = ParseUser.getCurrentUser();
        manualAddress = true;
        geolocate();
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_add_bathroom, menu);
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

    public void cancel(View v){
        finish();
    }

    private void reverseGeolocate(String address){
        if(manualAddress){
            Geocoder coder = new Geocoder(getApplicationContext());
            List<Address> addresses = new ArrayList<Address>();
            try{
                addresses = coder.getFromLocationName(address,5);
                if(addresses == null){
                    lat = 0;
                    lng = 0;

                    return;
                }
            }catch(Exception e){
                Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
            }
            finally{
                lng = addresses.get(0).getLongitude();
                lat =  addresses.get(0).getLatitude();
                Toast.makeText(this,lat+ " " + lng,Toast.LENGTH_LONG).show();
            }
        }
    }


    public void save(View v){

        //reverse geolocate from address
        reverseGeolocate(address.getText().toString());
        if(lat == 0  & lng == 0){
            Toast.makeText(getApplicationContext(),"Is your address accurate?", Toast.LENGTH_LONG).show();
            return;
        }

        ParseObject bathroom = new ParseObject("bathrooms");
        bathroom.put("title", title.getText().toString());
        bathroom.put("address", address.getText().toString());
        bathroom.put("numRatings", 1);
        bathroom.put("avgRating", rating.getRating());
        ParseGeoPoint point = new ParseGeoPoint(lat,lng);
        bathroom.put("location",point);

        ParseObject ratings = new ParseObject("ratings");
        ratings.put("rating", rating.getRating());
        ratings.put("comment", comment.getText().toString());
        ratings.put("username",user.getUsername().toString());
        ratings.put("parent",bathroom);
        try{
            ratings.save();
        }catch(ParseException e){
            Toast.makeText(this,e.toString(),Toast.LENGTH_LONG).show();
        }finally {
            ParseRelation<ParseObject> relation = user.getRelation("rates");
            relation.add(ratings);
            user.saveInBackground(new SaveCallback() {
                @Override
                public void done(ParseException e) {
                    if (e == null) {
                        finish();
                    } else {
                        Toast.makeText(getApplicationContext(), e.toString(), Toast.LENGTH_LONG).show();
                    }
                }
            });
        }


    }

    private void geolocate(){
        LocationManager lm = (LocationManager) getSystemService(Context.LOCATION_SERVICE);
        if(!lm.isProviderEnabled(LocationManager.GPS_PROVIDER)){
            alertnogps();
        }

        Location location = lm.getLastKnownLocation(LocationManager.GPS_PROVIDER);

        if(location!=null){
           lat = location.getLatitude();
           lng = location.getLongitude();
        }

        lm.requestLocationUpdates(LocationManager.GPS_PROVIDER,100,1,new LocationListener() {
            @Override
            public void onLocationChanged(Location location) {
                if(location!=null){
                    lat = location.getLatitude();
                    lng = location.getLongitude();
                }
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
        });




    }

    public void setAddress(View v){
        manualAddress = false;
        address.setKeyListener(null);
        address.setCursorVisible(false);
        address.setPressed(false);
        address.setFocusable(false);
        address.setTextColor(Color.GRAY);
        geolocate();
        Geocoder geocoder = new Geocoder(this);
        try {
            List<Address> addressList = geocoder.getFromLocation(lat, lng, 1);
            int maxIndex = addressList.get(0).getMaxAddressLineIndex();
            String addressText = addressList.get(0).getAddressLine(0);
            for(int i = 1; i < maxIndex; i ++){
                addressText = addressText +  " " + addressList.get(0).getAddressLine(i);
            }
            address.setText(addressText);
        }catch(IOException e){
            address.setText(e.getMessage());
        }finally{

        }

    }

    private void alertnogps(){
        final AlertDialog.Builder builder = new AlertDialog.Builder(this);
        builder.setMessage("Your GPS seems to be disabled, do you want to enable it?")
                .setCancelable(false)
                .setPositiveButton("Yes", new DialogInterface.OnClickListener() {
                    public void onClick(@SuppressWarnings("unused") final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        startActivity(new Intent(android.provider.Settings.ACTION_LOCATION_SOURCE_SETTINGS));
                    }
                })
                .setNegativeButton("No", new DialogInterface.OnClickListener() {
                    public void onClick(final DialogInterface dialog, @SuppressWarnings("unused") final int id) {
                        dialog.cancel();
                    }
                });
        final AlertDialog alert = builder.create();
        alert.show();
    }

    @Override
    protected void onDestroy() {
        super.onDestroy();
    }
}
