package om.ratemybathroo.ratemybathroom;

import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.Menu;
import android.view.MenuItem;
import android.view.View;
import android.widget.EditText;
import android.widget.Toast;

import com.parse.ParseException;
import com.parse.ParseUser;
import com.parse.SignUpCallback;


public class Register extends ActionBarActivity {

    private EditText username, password, passwordconfirm, email;


    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        username = (EditText) findViewById(R.id.et_reg_username);
        password = (EditText) findViewById(R.id.et_reg_password);
        passwordconfirm= (EditText) findViewById(R.id.et_reg_password_confirm);
        email = (EditText) findViewById(R.id.et_reg_email);

    }


    @Override
    public boolean onCreateOptionsMenu(Menu menu) {
        // Inflate the menu; this adds items to the action bar if it is present.
        getMenuInflater().inflate(R.menu.menu_register, menu);
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

    public void register(View v){
        ParseUser user = new ParseUser();
        if(!username.getText().toString().equals("") && !password.getText().toString().equals("") && !email.getText().toString().equals("") && !passwordconfirm.getText().toString().equals("")){
            if(password.getText().toString().equals(passwordconfirm.getText().toString())){
                user.setUsername(username.getText().toString());
                user.setPassword(password.getText().toString());
                user.setEmail(email.getText().toString());

                user.signUpInBackground(new SignUpCallback() {
                    @Override
                    public void done(ParseException e) {
                        if(e == null){
                            finish();
                        }else{
                            Toast.makeText(getApplicationContext(),e.getMessage(), Toast.LENGTH_LONG).show();

                        }
                    }
                });
            }else{
                Toast.makeText(getApplicationContext(),"Passwords not equal",Toast.LENGTH_SHORT).show();
            }

        }else{
            Toast.makeText(getApplicationContext(),"enter in all fields!",Toast.LENGTH_SHORT).show();
        }


    }

    public void cancel(View v){

        finish();
    }

}
