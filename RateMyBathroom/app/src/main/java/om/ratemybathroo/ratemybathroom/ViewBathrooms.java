package om.ratemybathroo.ratemybathroom;

import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.location.Location;
import android.location.LocationListener;
import android.location.LocationManager;
import android.os.Bundle;
import android.support.v7.app.ActionBarActivity;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.ParseException;
import com.parse.ParseGeoPoint;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseUser;

import java.text.DecimalFormat;
import java.util.List;


public class ViewBathrooms extends Activity {

    private ParseUser currentUser;
    private ListView listview;
    private BathroomListAdapter adapter;
    public double lat, lng;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_view_bathrooms);
        currentUser = ParseUser.getCurrentUser();
        lat = 0;
        lng = 0;
        listview = (ListView) findViewById(R.id.lv_bathrooms);
        refresh();
        listview.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                TextView tv_id= (TextView) (view.findViewById(R.id.tv_id_row));
                Intent i = new Intent(getApplicationContext(),BathroomDetails.class);
                i.putExtra("id",tv_id.getText().toString());
                startActivity(i);

            }
        });
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


    private void refresh(){
        geolocate();
        ParseGeoPoint currentLocation = new ParseGeoPoint(lat,lng);

        ParseQuery<ParseObject> query = ParseQuery.getQuery("buildings");
        query.whereNear("location",currentLocation);
        query.setLimit(50);

        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null){
                    adapter = new BathroomListAdapter(getApplicationContext(),parseObjects);
                    listview.setAdapter(adapter);

                }else{
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });


    }

    @Override
    protected void onResume() {
        super.onResume();

        refresh();

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_view_bathrooms, menu);
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
        if(id == R.id.addroom){
            //switch to add bathroom activity
            Intent i = new Intent(this, AddBathroom.class);
            startActivity(i);
        }
        if(id== R.id.refresh){
            refresh();
        }
        if(id == R.id.viewMap){
            Intent i = new Intent(this,Main.class);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }

    public class BathroomListAdapter extends ArrayAdapter<ParseObject> {
        public BathroomListAdapter(Context context, List<ParseObject> bathrooms){
            super(context,R.layout.bathroom_element, bathrooms);
        }

        @Override
        public View getView(int position, View convertView, ViewGroup parent) {
            LayoutInflater inflater = LayoutInflater.from(getContext());
            View customView = inflater.inflate(R.layout.bathroom_element,parent,false);
            ParseObject individualItem = getItem(position);
            TextView title = (TextView) customView.findViewById(R.id.tv_title_row);
            RatingBar rating = (RatingBar) customView.findViewById(R.id.rb_rating_row);
            TextView tv_id = (TextView) customView.findViewById(R.id.tv_id_row);
            TextView distance = (TextView) customView.findViewById(R.id.distance);

            ParseGeoPoint point = new ParseGeoPoint(lat,lng);
            DecimalFormat df = new DecimalFormat("#.##");


            distance.setText(String.valueOf(df.format(point.distanceInMilesTo(individualItem.getParseGeoPoint("location")))) + " miles");

            tv_id.setText(individualItem.getObjectId());
            title.setText(individualItem.get("title").toString());
            rating.setRating(Float.parseFloat(individualItem.get("avgRating").toString()));
            return customView;

        }


    }



}
