package om.ratemybathroo.ratemybathroom;

import android.app.Activity;
import android.content.Intent;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.LogInCallback;
import com.parse.ParseException;
import com.parse.ParseUser;


public class LoginActivity extends Activity {

    private EditText username,password;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_login);
        username = (EditText) findViewById(R.id.et_username);
        password = (EditText) findViewById(R.id.et_password);
    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_login, menu);
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

    public void login(View v){
        //TODO: check if ets are empty
        ParseUser user = new ParseUser();
        user.setUsername(username.getText().toString());
        user.setPassword(username.getText().toString());
        ParseUser.logInInBackground(username.getText().toString(),password.getText().toString(),new LogInCallback() {
            @Override
            public void done(ParseUser parseUser, ParseException e) {
                if(e == null) {
                    if(parseUser.getBoolean("emailVerified")){
                        Intent i = new Intent(getApplicationContext(), ViewBathrooms.class);
                        startActivity(i);
                        finish();
                    }else{
                        //do something else
                        Intent i = new Intent(getApplicationContext(),EmailVerifiedActivity.class);
                        startActivity(i);
                    }

                }else{
                    Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();
                }
            }
        });
    }

    public void register(View v ){
        //TODO: make a new activity and then prompt user to register there.
        Intent i = new Intent(getApplicationContext(),Register.class);
        startActivity(i);
    }
}
