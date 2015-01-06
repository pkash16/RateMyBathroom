package om.ratemybathroo.ratemybathroom;

import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.widget.ListView;
import android.widget.RatingBar;
import android.widget.TextView;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;

import java.util.List;


public class BathroomDetails extends ActionBarActivity {

    private String parseId;
    public TextView title,address;
    public RatingBar rb_rating;
    private ListView list;
    private RatingListAdapter adapter;
    public ParseQuery<ParseObject> query;
    public ParseObject bathroom;
    boolean firstTime = true;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_bathroom_details);

        title = (TextView) findViewById(R.id.et_title_details);
        address = (TextView) findViewById(R.id.et_address_details);
        rb_rating = (RatingBar) findViewById(R.id.rb_rating_details);
        list = (ListView) findViewById(R.id.lv_ratings_details);


        parseId = getIntent().getCharSequenceExtra("id").toString();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("bathrooms");
        query.getInBackground(parseId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e == null){
                    title.setText(parseObject.get("title").toString());
                    address.setText(parseObject.get("address").toString());
                    rb_rating.setRating(Float.parseFloat(parseObject.get("avgRating").toString()));
                    bathroom = parseObject;
                    refresh();
                    firstTime=false;
                }else{
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });



    }

    @Override
    protected void onResume() {
        super.onResume();
        if(!firstTime){
            refresh();
        }

    }

    private void refresh(){
        query = ParseQuery.getQuery("ratings").whereEqualTo("parent",bathroom);
        query.findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null){
                    adapter = new RatingListAdapter(getApplicationContext(),parseObjects);
                    list.setAdapter(adapter);

                }else{
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });
            //refresh avgRating sign

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_bathroom_details, menu);
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
        if(id == R.id.rateme){
            Intent i = new Intent(getApplicationContext(),RateActivity.class);
            i.putExtra("id", parseId);
            startActivity(i);
        }

        return super.onOptionsItemSelected(item);
    }
}
