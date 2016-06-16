package app.com.example.android.twitteroauth;

import android.content.Intent;
import android.content.SharedPreferences;
import android.content.pm.ActivityInfo;
import android.net.Uri;
import android.os.AsyncTask;
import android.os.Bundle;
import android.support.v7.app.AppCompatActivity;
import android.text.Html;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.TextView;
import android.widget.Toast;

import java.util.ArrayList;

import butterknife.BindView;
import butterknife.ButterKnife;
import timber.log.Timber;
import twitter4j.ResponseList;
import twitter4j.Status;
import twitter4j.Twitter;
import twitter4j.TwitterException;
import twitter4j.TwitterFactory;
import twitter4j.User;
import twitter4j.auth.AccessToken;
import twitter4j.auth.RequestToken;
//import twitter4j.conf.Configuration;
import twitter4j.conf.Configuration;
import twitter4j.conf.ConfigurationBuilder;
//generate method on top
//final on top
//static method -- oncreate -- lifecycle --


public class MainActivity extends AppCompatActivity {

    private final String PREF_KEY_OAUTH_TOKEN = "oauth_token";
    private final String PREF_KEY_OAUTH_SECRET = "oauth_token_secret";
    private final String PREF_KEY_TWITTER_LOGIN = "isTwitterLogedIn";
    private final String TWITTER_CALLBACK_URL = "oauth://t4jsample";
    private String verifier;
    private Twitter twitter;
    private AccessToken accessToken = null;
    private RequestToken requestToken;
    private SharedPreferences mSharedPreferences; //preference manager

    private User user;
    //private ArrayList<Status> statuses = new ArrayList<>();

    @BindView(R.id.btnLoginTwitter)
    Button btnLoginTwitter;
    @BindView(R.id.btnLogoutTwitter)
    Button btnLogoutTwitter;
    @BindView(R.id.btnGetTimeline)
    Button btnGetTimeline;
    @BindView(R.id.lblUpdate)
    TextView lblUpdate;
    @BindView(R.id.lblUserName)
    TextView lblUserName;

    public ResponseList<Status> statuses; //length of 20
   // String[] texts = new String[20 * 2];

    @Override
    protected void onCreate(Bundle savedInstanceState) {

        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        ButterKnife.bind(this);

        setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
        //checkInternet();

        mSharedPreferences = getApplicationContext().getSharedPreferences("MyPref", 0);

        btnLoginTwitter.setOnClickListener(new View.OnClickListener() {

            @Override
            public void onClick(View arg0) {
                // Call login twitter function
                loginToTwitter();
                //here catch internet connect exception
            }
        });

        if (!isTwitterLoggedInAlready()) {
            getUri(getIntent());
        }

        btnLogoutTwitter.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View arg0) {
                // Call logout twitter function
                logoutFromTwitter();
            }
        });

        btnGetTimeline.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                loginToTwitter();
                Intent intent = new Intent(MainActivity.this, TimelineActivity.class);
                OAuthAccessTokenTask accessToken = new OAuthAccessTokenTask();
                accessToken.execute(verifier);
                startActivity(intent);
            }
        });

    }

    private void loginToTwitter() {
        Timber.d("start log in");
        if (!isTwitterLoggedInAlready()) {
            twitter = TwitterFactory.getSingleton();
            ConfigurationBuilder builder = new ConfigurationBuilder();
            String TWITTER_CONSUMER_KEY = "FZ5upj4lpNv2EbjkPMF5MirWx";
            builder.setOAuthConsumerKey(TWITTER_CONSUMER_KEY);
            String TWITTER_CONSUMER_SECRET = "aq8Q1B7u8iHGDxFCInnfkRpDr2Wg1XwGi7fiQtRoGdpdIfL5eX";
            builder.setOAuthConsumerSecret(TWITTER_CONSUMER_SECRET);
            Configuration configuration = builder.build();

            TwitterFactory factory = new TwitterFactory(configuration);
            twitter = factory.getInstance();

            Thread thread = new Thread(new Runnable() {
                @Override
                public void run() {
                    try {
                        requestToken = twitter.getOAuthRequestToken(TWITTER_CALLBACK_URL);
                        Timber.d("REQUEST TOKEN: " + requestToken.toString());
                        startActivity(new Intent(Intent.ACTION_VIEW, Uri
                                .parse(requestToken.getAuthenticationURL())));
                    } catch (Exception e) {
                        Timber.e(e, "request token error");
                    }
                }
            });
            thread.start();
        } else {
            // user already logged into twitter
            Toast.makeText(getApplicationContext(),
                   "Already Logged into twitter", Toast.LENGTH_LONG).show();
        }
    }


    private void getUri(Intent intent) {
        Uri uri = intent.getData();
        if (uri != null && uri.toString().startsWith(TWITTER_CALLBACK_URL)) {
            // oAuth verifier
            String URL_TWITTER_OAUTH_VERIFIER = "oauth_verifier";
            verifier = uri.getQueryParameter(URL_TWITTER_OAUTH_VERIFIER);
            Timber.v("VERIFIER: " + verifier);
            try {
                new OAuthAccessTokenTask().execute(verifier);

            } catch (Exception e) {
                Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
            }
        }
    }

    private class OAuthAccessTokenTask extends AsyncTask<String, Void, Exception> {

        @Override
        protected Exception doInBackground(String... params) {
            Exception toReturn = null;
            try {
                accessToken = twitter.getOAuthAccessToken(requestToken, params[0]);
                Timber.v("ACCESS TOKEN: " + accessToken.toString());
                user = twitter.showUser(accessToken.getUserId());

                //Get user timeline
                //User user = twitter.verifyCredentials();
                statuses = twitter.getHomeTimeline();
                int statusLength = statuses.size();
                Timber.v("STATUS LENGTH: " + statuses.size());
                Intent passArrIntent = new Intent(MainActivity.this, TimelineActivity.class);
                String[] texts = new String[statusLength * 2];

                for (int i = 0; i < statusLength; i++) {
                    texts[i] = statuses.get(i).getText();
                }
                for (int i = statusLength; i < statusLength * 2; i++) {
                    texts[i] = statuses.get(i - statusLength).getUser().getMiniProfileImageURL();
                }
                // Timber.v("IMG_URL: " + texts[22]);

                passArrIntent.putExtra(getString(R.string.passStatus), texts);
                startActivity(passArrIntent);

            } catch (TwitterException e) {
                Timber.e("Twitter Error: " + e.getErrorMessage());
                toReturn = e;
            } catch (Exception e) {
                Timber.e("Error: " + e.getMessage());
                toReturn = e;
            }
            return toReturn;
        }

        //@Override
        protected void onPostExecute(Exception exception) {

            onRequestTokenReceived(exception);

        }
    }

    private void onRequestTokenReceived(Exception result) {
        if (result != null) {
            Toast.makeText(this, result.getMessage(), Toast.LENGTH_SHORT).show();
        } else {
            try {
                SharedPreferences.Editor e = mSharedPreferences.edit();

                // After getting access token, access token secret
                // store them in application preferences
                e.putString(PREF_KEY_OAUTH_TOKEN, accessToken.getToken());
                e.putString(PREF_KEY_OAUTH_SECRET,
                        accessToken.getTokenSecret());
                // Store login status - true
                e.putBoolean(PREF_KEY_TWITTER_LOGIN, true);
                e.apply(); // save changes

                Timber.d("GOT TOKEN");
                btnLoginTwitter.setVisibility(View.GONE); // Hide login button

                String username = user.getName();
                lblUserName.setText(Html.fromHtml("<b>Welcome " + username + "</b>"));
                lblUpdate.setVisibility(View.VISIBLE);
                btnLogoutTwitter.setVisibility(View.VISIBLE);
                btnGetTimeline.setVisibility(View.VISIBLE);
            } catch (Exception e) {
                Log.e("Twitter OAuth Token", "> " + accessToken.getToken());
            }
        }
    }

    private void logoutFromTwitter() {
        // Clear the shared preferences
        SharedPreferences.Editor e = mSharedPreferences.edit();
        e.remove(PREF_KEY_OAUTH_TOKEN);
        e.remove(PREF_KEY_OAUTH_SECRET);
        e.remove(PREF_KEY_TWITTER_LOGIN);
        e.apply();

        // showing the hiding/showing buttons again
        btnLogoutTwitter.setVisibility(View.GONE);
        lblUpdate.setVisibility(View.GONE);
        lblUserName.setText("");
        lblUserName.setVisibility(View.GONE);
        btnGetTimeline.setVisibility(View.GONE);
        btnLoginTwitter.setVisibility(View.VISIBLE);
    }

    private boolean isTwitterLoggedInAlready() {
        // return twitter login status from Shared Preferences
        return mSharedPreferences.getBoolean(PREF_KEY_TWITTER_LOGIN, false);
    }

//    public void checkInternet() {
//        ConnectionDetector cd = new ConnectionDetector(getApplicationContext());
//        if (!cd.isConnectingToInternet()) {
//            Timber.e("InternetConnection Error");
//            //alert.showAlertDialog(MainActivity.this, "InternetConnection Error", "Please connect to working internet", false);
//        }
//        if (TWITTER_CONSUMER_KEY.trim().length() == 0
//                || TWITTER_CONSUMER_SECRET.trim().length() == 0) {
//            Timber.e("Please set your twitter oauth tokens first!");
//            //alert.showAlertDialog(MainActivity.this, "Twitter oAuth tokens", "Please set your twitter oauth tokens first!", false);
//        }
//    }

    @Override
    protected void onNewIntent(Intent intent) {
        super.onNewIntent(intent);
        getUri(intent);
    }

}
