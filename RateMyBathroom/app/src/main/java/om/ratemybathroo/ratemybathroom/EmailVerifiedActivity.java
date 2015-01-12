package om.ratemybathroo.ratemybathroom;

import android.app.Activity;
import android.content.Intent;
import android.os.Handler;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.Toast;

import com.parse.ParseUser;


public class EmailVerifiedActivity extends Activity {

    ParseUser parseUser;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_email_verified);
        parseUser = ParseUser.getCurrentUser();


    }

    public void verified(View v){
        //check if verified
        parseUser = ParseUser.getCurrentUser();
        boolean verify = parseUser.getBoolean("emailVerified");
        if(verify){
            Intent i = new Intent(this,ViewBathrooms.class);
            startActivity(i);
            finish();
        }else{
            Toast.makeText(this,"Are you sure you're verified? Check your email!", Toast.LENGTH_LONG).show();
        }
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_email_verified, menu);
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
