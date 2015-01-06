package om.ratemybathroo.ratemybathroom;

import com.parse.Parse;
import com.parse.ParseACL;
import com.parse.ParseCrashReporting;
import com.parse.ParseUser;

import android.app.Application;

/**
 * Created by Deepak on 12/30/2014.
 */
public class RateMyBathroomApplication extends Application {
    @Override
    public void onCreate() {
        super.onCreate();

        // Initialize crash reporting.
        ParseCrashReporting.enable(this);

        Parse.initialize(this, "jyU75G63GYA6GZZXfvKqZHMZYjNiFpR6zAOwpswp", "NSjSKUuiDwDE7EKxZm2eNLvVp3BiW0CUIWJythsy");


        ParseUser.enableAutomaticUser();
        /*ParseACL defaultACL = new ParseACL();
        // Optionally enable public read access.
        defaultACL.setPublicReadAccess(true);
        ParseACL.setDefaultACL(defaultACL, true);*/
    }


}
