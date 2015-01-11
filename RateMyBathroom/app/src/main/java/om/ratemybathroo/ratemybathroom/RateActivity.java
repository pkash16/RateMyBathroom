package om.ratemybathroo.ratemybathroom;

import android.app.Activity;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.RatingBar;
import android.widget.Toast;

import com.parse.FindCallback;
import com.parse.GetCallback;
import com.parse.ParseException;
import com.parse.ParseObject;
import com.parse.ParseQuery;
import com.parse.ParseRelation;
import com.parse.ParseUser;
import com.parse.SaveCallback;

import java.util.List;


public class RateActivity extends Activity {

    String parseId;
    RatingBar rating;
    EditText comment;
    ParseUser user;
    ParseObject bathroom;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_rate);
        rating= (RatingBar) findViewById(R.id.rb_rating_rate);
        comment = (EditText) findViewById(R.id.et_comment_rate);
        parseId = getIntent().getCharSequenceExtra("id").toString();
        user = ParseUser.getCurrentUser();
        ParseQuery<ParseObject> query = ParseQuery.getQuery("bathrooms");
        query.getInBackground(parseId, new GetCallback<ParseObject>() {
            @Override
            public void done(ParseObject parseObject, ParseException e) {
                if(e == null) {
                    bathroom = parseObject;
                }else{
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_LONG).show();
                }
            }
        });




    }


    public void rate(View v){

        //refresh avgRating
        double total = Double.parseDouble(bathroom.get("avgRating").toString()) * Double.parseDouble(bathroom.get("numRatings").toString());
        total += rating.getRating();
        total = total /( Double.parseDouble(bathroom.get("numRatings").toString()) + 1);
        bathroom.put("avgRating",total);
        bathroom.increment("numRatings",1);
        bathroom.saveInBackground();


        ParseQuery<ParseObject> query = ParseQuery.getQuery("ratings");
        query.whereEqualTo("username",user.getUsername()).whereEqualTo("parent",bathroom).findInBackground(new FindCallback<ParseObject>() {
            @Override
            public void done(List<ParseObject> parseObjects, ParseException e) {
                if(e == null){
                    if(parseObjects.size() == 0){
                        //good, add rating




                        ParseObject ratings = new ParseObject("ratings");
                        ratings.put("rating", rating.getRating());
                        ratings.put("comment", comment.getText().toString());
                        ratings.put("username",user.getUsername().toString());
                        ratings.put("parent",bathroom);
                        try{
                            ratings.save();
                        }catch(ParseException ex){
                            Toast.makeText(getApplicationContext(),ex.toString(),Toast.LENGTH_LONG).show();
                        }finally {
                            ParseRelation<ParseObject> relation = user.getRelation("rates");
                            relation.add(ratings);
                            user.saveInBackground(new SaveCallback() {
                                @Override
                                public void done(ParseException exc) {
                                    if (exc == null) {
                                        finish();
                                    } else {
                                        Toast.makeText(getApplicationContext(), exc.toString(), Toast.LENGTH_LONG).show();
                                    }
                                }
                            });
                        }



                    }else{
                        Toast.makeText(getApplicationContext(),"You have already rated this!", Toast.LENGTH_SHORT).show();
                        finish();
                    }
                }else{
                    Toast.makeText(getApplicationContext(),e.getMessage(),Toast.LENGTH_SHORT).show();
                }
            }
        });

    }

    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_rate, menu);
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
