package tetromino.puzzle;

import android.app.AlertDialog;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.os.Bundle;
import android.os.Process;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.facebook.AccessToken;
import com.facebook.CallbackManager;
import com.facebook.FacebookCallback;
import com.facebook.FacebookException;
import com.facebook.GraphRequest;
import com.facebook.GraphResponse;
import com.facebook.login.LoginManager;
import com.facebook.login.LoginResult;
import com.facebook.login.widget.LoginButton;
import com.google.firebase.analytics.FirebaseAnalytics;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.List;
import java.util.Locale;

import cz.msebera.android.httpclient.Header;

public class InitialScreenActivity extends AppCompatActivity {
    private LoginButton loginFacebook;
    private Button fbExit;
    private CallbackManager callbackManager;
    private LinearLayout content;
    private ImageView logo;
    private Button newGame, main, classic, challenge, easy, hard, bestPlayers, noFacebookLogin, whyBackButton;
    private TextView whyFacebookLogin;
    private TextView llTVWhyFacebookLogin1, llTVWhyFacebookLogin2, llTVWhyFacebookLogin3;
    private LinearLayout llWhyFacebookLogin;
    private FirebaseAnalytics firebaseAnalytics;
    private boolean isFacebookLoginDialogVisible;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_initial_screen);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (isFacebookLoginDialogVisible) {
                    isFacebookLoginDialogVisible = false;
                    llWhyFacebookLogin.setVisibility(View.GONE);
                    content.setAlpha(1);
                } else {
                    if(newGame.getVisibility()==View.VISIBLE || loginFacebook.getVisibility()==View.VISIBLE){
                        AlertDialog.Builder dialogBox = new AlertDialog.Builder(InitialScreenActivity.this);
                        dialogBox.setTitle("Tetromino puzzle");
                        dialogBox.setMessage(InitialScreenActivity.this.getResources().getString(R.string.wannaClose));
                        dialogBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {
                                finish();
                            }
                        });
                        dialogBox.setNegativeButton(InitialScreenActivity.this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                            @Override
                            public void onClick(DialogInterface dialog, int which) {

                            }
                        });
                        AlertDialog dialogOk = dialogBox.create();
                        dialogOk.show();
                    }else if(classic.getVisibility()==View.VISIBLE){
                        main.setVisibility(View.GONE);
                        classic.setVisibility(View.GONE);
                        challenge.setVisibility(View.GONE);
                        newGame.setVisibility(View.VISIBLE);
                        bestPlayers.setVisibility(View.VISIBLE);
                    }else if(easy.getVisibility()==View.VISIBLE){
                        easy.setVisibility(View.GONE);
                        hard.setVisibility(View.GONE);
                        main.setVisibility(View.VISIBLE);
                        classic.setVisibility(View.VISIBLE);
                        challenge.setVisibility(View.VISIBLE);
                    }
                }
            }
        };

        getOnBackPressedDispatcher().addCallback(this, callback);

        /*MobileAds.initialize(this, initializationStatus -> {});
        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList(
                        "7B7BBEAA33FED98C6CF09E90E6B28148", // tu celular
                        "6237E130C04BBE00039E8293458D1B7D", // emu sgs2
                        "A74DBE195129763EE0C647166F2EBDA5"  // custom phone
                ))
                .build();
        MobileAds.setRequestConfiguration(configuration);*/

        firebaseAnalytics = FirebaseAnalytics.getInstance(this);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        int displayWidth = size.x;
        int displayHeight = size.y;
        content = (LinearLayout)findViewById(R.id.LLInitialScreen);

        fbExit = (Button)findViewById(R.id.fbExit);
        if(Locale.getDefault().getLanguage().equals("es")){
            fbExit.setBackground(ContextCompat.getDrawable(getApplicationContext(),R.drawable.bt_fb_exit));
        }
        int fbExitWidth = displayWidth*58/100;
        RelativeLayout.LayoutParams fbExitParams = new RelativeLayout.LayoutParams(fbExitWidth,(int)(fbExitWidth/7.36));
        fbExitParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        fbExitParams.addRule(RelativeLayout.ALIGN_PARENT_BOTTOM);
        fbExitParams.setMargins(0,0,0,displayHeight*10/100);
        fbExit.setLayoutParams(fbExitParams);
        fbExit.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                LoginManager.getInstance().logOut();
                newGame.setVisibility(View.GONE);
                newGame.setAlpha(0.6f);
                newGame.setEnabled(false);
                bestPlayers.setVisibility(View.GONE);
                main.setVisibility(View.GONE);
                main.setEnabled(true);
                classic.setVisibility(View.GONE);
                challenge.setVisibility(View.GONE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                loginFacebook.setVisibility(View.VISIBLE);
                noFacebookLogin.setVisibility(View.VISIBLE);
                whyFacebookLogin.setVisibility(View.VISIBLE);
                fbExit.setVisibility(View.GONE);
            }
        });

        //logo = (ImageView)findViewById(R.id.IMLogo);
        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(displayWidth-displayWidth*10/100,
                ViewGroup.LayoutParams.WRAP_CONTENT);
        //params.gravity= Gravity.CENTER_HORIZONTAL;
        //logo.setLayoutParams(params);

        newGame = (Button)findViewById(R.id.BTNewGame);
        bestPlayers = (Button)findViewById(R.id.BTBestPlayers);
        main = (Button)findViewById(R.id.BTMain);
        classic = (Button)findViewById(R.id.BTClassic);
        challenge = (Button)findViewById(R.id.BTChallenge);
        easy = (Button)findViewById(R.id.BTEasyChallenge);
        hard = (Button)findViewById(R.id.BTHardChallenge);

        whyBackButton = (Button)findViewById(R.id.whyBackButton);
        whyBackButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                llWhyFacebookLogin.setVisibility(View.GONE);
                content.setAlpha(1);
                isFacebookLoginDialogVisible = false;
            }
        });

        llWhyFacebookLogin = (LinearLayout)findViewById(R.id.LLWhyFBLogin);
        llTVWhyFacebookLogin1 = (TextView)findViewById(R.id.LLTVWhyFacebookLogin1);
        llTVWhyFacebookLogin2 = (TextView)findViewById(R.id.LLTVWhyFacebookLogin2);
        llTVWhyFacebookLogin3 = (TextView)findViewById(R.id.LLTVWhyFacebookLogin3);

        loginFacebook = (LoginButton)findViewById(R.id.facebook_login);
        whyFacebookLogin = (TextView)findViewById(R.id.TVWhyFacebookLogin);
        whyFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                content.setAlpha(.2f);
                llTVWhyFacebookLogin1.setText(R.string.whyFacebookLogin);
                llTVWhyFacebookLogin2.setText(R.string.whyFBLoginLine1);
                llTVWhyFacebookLogin3.setText(R.string.whyFBLoginLine2);
                llWhyFacebookLogin.bringToFront();
                llWhyFacebookLogin.setVisibility(View.VISIBLE);
                isFacebookLoginDialogVisible = true;
            }
        });
        noFacebookLogin = (Button)findViewById(R.id.BTNoFacebookLogin);
        noFacebookLogin.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {

                Bundle bundle = new Bundle();
                bundle.putString("click","11click");
                firebaseAnalytics.logEvent(FirebaseAnalytics.Event.SHARE,bundle);

                AlertDialog.Builder dialogBox = new AlertDialog.Builder(InitialScreenActivity.this);
                dialogBox.setTitle("Tetromino puzzle");
                dialogBox.setMessage(InitialScreenActivity.this.getResources().getString(R.string.continueWithoutFBLogin));
                dialogBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        loginFacebook.setVisibility(View.GONE);
                        whyFacebookLogin.setVisibility(View.GONE);
                        noFacebookLogin.setVisibility(View.GONE);
                        newGame.setVisibility(View.VISIBLE);
                        bestPlayers.setVisibility(View.VISIBLE);
                        newGame.setAlpha(1);
                        newGame.setEnabled(true);
                    }
                });
                dialogBox.setNegativeButton(InitialScreenActivity.this.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialogOk = dialogBox.create();
                dialogOk.show();
            }
        });


        params = new LinearLayout.LayoutParams(displayWidth*50/100, ViewGroup.LayoutParams.WRAP_CONTENT);
        params.gravity = Gravity.CENTER_HORIZONTAL;
        params.setMargins(0,displayHeight*2/100,0,0);


        main.setLayoutParams(params);
        classic.setLayoutParams(params);
        challenge.setLayoutParams(params);
        easy.setLayoutParams(params);
        hard.setLayoutParams(params);
        newGame.setLayoutParams(params);
        bestPlayers.setLayoutParams(params);
        callbackManager = CallbackManager.Factory.create();
        loginFacebook.registerCallback(callbackManager, new FacebookCallback<LoginResult>() {
            @Override
            public void onSuccess(LoginResult loginResult) {
                loginFacebook.setVisibility(View.GONE);
                fbExit.setVisibility(View.VISIBLE);
                whyFacebookLogin.setVisibility(View.GONE);
                noFacebookLogin.setVisibility(View.GONE);
                newGame.setVisibility(View.VISIBLE);
                bestPlayers.setVisibility(View.VISIBLE);
                invalidateOptionsMenu();

                AccessToken accessToken = loginResult.getAccessToken();
                GraphRequest request = GraphRequest.newMeRequest(accessToken, new GraphRequest.GraphJSONObjectCallback() {
                    @Override
                    public void onCompleted(final JSONObject object, GraphResponse response) {
                        try {
                            final String facebookId = (String)object.get("id");
                            final String facebookName = (String)object.get("name");
                            Toast.makeText(getApplicationContext(), getResources().getString(R.string.hello) + " " + object.getString("first_name"),Toast.LENGTH_SHORT).show();

                            String url = "https://tetromino.page.gd/get_profile.php?name=" + URLEncoder.encode(facebookName, "UTF-8") +
                                    "&fbid=" + URLEncoder.encode(facebookId, "UTF-8");

                            HttpBrowser.callUrl(InitialScreenActivity.this, url, "GET_PROFILE", new HttpResponseListener() {
                                @Override
                                public void getCodeResponse(PhpResponse response) {
                                    SharedPreferences prefs = getSharedPreferences("con_login_user",MODE_PRIVATE);
                                    SharedPreferences.Editor editor = prefs.edit();
                                    editor.putString("fbid",facebookId);
                                    editor.putString("name",facebookName);
                                    if (response.getResponse().equals("NEW_USER")) {
                                        editor.putString("stage","1");
                                        editor.commit();
                                    } else if (response.getResponse().contains("ERROR")) {
                                        Toast.makeText(getApplicationContext(),R.string.serverError,Toast.LENGTH_LONG).show();
                                        main.setEnabled(false);
                                    } else {
                                        editor.putString("stage",response.getResponse());
                                        editor.commit();
                                    }
                                    newGame.setAlpha(1);
                                    newGame.setEnabled(true);
                                }

                                @Override
                                public void getTopScoresResponse(List<ScoreItem> scores) {

                                }
                            });
                        } catch (JSONException e) {
                            e.printStackTrace();
                        } catch (UnsupportedEncodingException e) {
                            e.printStackTrace();
                        }
                    }
                });
                Bundle parameters = new Bundle();
                parameters.putString("fields", "id,name,first_name");
                request.setParameters(parameters);
                request.executeAsync();
            }

            @Override
            public void onCancel() {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.facebookLoginCancel), Toast.LENGTH_LONG).show();
            }

            @Override
            public void onError(FacebookException error) {
                Toast.makeText(getApplicationContext(),getResources().getString(R.string.facebookLoginError), Toast.LENGTH_LONG).show();
            }
        });
        if(AccessToken.getCurrentAccessToken()==null){
            loginFacebook.setVisibility(View.VISIBLE);
            whyFacebookLogin.setVisibility(View.VISIBLE);
            noFacebookLogin.setVisibility(View.VISIBLE);
        }else{
            fbExit.setVisibility(View.VISIBLE);
            newGame.setVisibility(View.VISIBLE);
            newGame.setAlpha(1);
            newGame.setEnabled(true);
            bestPlayers.setVisibility(View.VISIBLE);
        }
        invalidateOptionsMenu();
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        super.onActivityResult(requestCode, resultCode, data);
        callbackManager.onActivityResult(requestCode,resultCode,data);
    }


    /*@Override
    public boolean onCreateOptionsMenu(Menu menu) {
        getMenuInflater().inflate(R.menu.menu_initial_screen,menu);
        return super.onCreateOptionsMenu(menu);
    }

    @Override
    public boolean onPrepareOptionsMenu(Menu menu) {
        if(AccessToken.getCurrentAccessToken()==null){
            menu.findItem(R.id.logOut).setVisible(false);
        }else{
            menu.findItem(R.id.logOut).setVisible(true);
        }

        return super.onPrepareOptionsMenu(menu);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        switch (item.getItemId()){
            case R.id.logOut:
                LoginManager.getInstance().logOut();
                newGame.setVisibility(View.GONE);
                bestPlayers.setVisibility(View.GONE);
                main.setVisibility(View.GONE);
                main.setEnabled(true);
                classic.setVisibility(View.GONE);
                challenge.setVisibility(View.GONE);
                easy.setVisibility(View.GONE);
                hard.setVisibility(View.GONE);
                loginFacebook.setVisibility(View.VISIBLE);
                noFacebookLogin.setVisibility(View.VISIBLE);
                whyFacebookLogin.setVisibility(View.VISIBLE);
                fbExit.setVisibility(View.GONE);
                return true;
        }
        return false;
    }*/

    public void onClick(View v){
        Intent i;
        i = new Intent(InitialScreenActivity.this,DisplayGameActivity.class);
        String tag = v.getTag().toString();
        switch (tag){
            case "NEW_GAME":
                newGame.setVisibility(View.GONE);
                bestPlayers.setVisibility(View.GONE);
                main.setVisibility(View.VISIBLE);
                classic.setVisibility(View.VISIBLE);
                challenge.setVisibility(View.VISIBLE);
                break;
            case "MAIN_GAME":
                Intent i2 = new Intent(InitialScreenActivity.this,StageSelectActivity.class);
                startActivity(i2);
                break;
            case "CLASSIC_GAME":
                i.putExtra("modo", Constants.CLASSIC_GAME);
                startActivity(i);
                break;
            case "CHALLENGE":
                main.setVisibility(View.GONE);
                classic.setVisibility(View.GONE);
                challenge.setVisibility(View.GONE);
                easy.setVisibility(View.VISIBLE);
                hard.setVisibility(View.VISIBLE);
                break;
            case "EASY_CHALLENGE":
                i.putExtra("modo",Constants.EASY_CHALLENGE);
                startActivity(i);
                break;
            case "HARD_CHALLENGE":
                i.putExtra("modo",Constants.HARD_CHALLENGE);
                startActivity(i);
                break;
            case "BEST_PLAYERS":
                Intent HS = new Intent(InitialScreenActivity.this,HighScoresActivity.class);
                startActivity(HS);
                break;
        }
    }

    @Override
    protected void onResume() {
        if(hard.getVisibility() == View.VISIBLE || easy.getVisibility() == View.VISIBLE || classic.getVisibility() == View.VISIBLE ||
                challenge.getVisibility() == View.VISIBLE || main.getVisibility() == View.VISIBLE){
            loginFacebook.setVisibility(View.GONE);
            whyFacebookLogin.setVisibility(View.GONE);
            noFacebookLogin.setVisibility(View.GONE);
            hard.setVisibility(View.GONE);
            easy.setVisibility(View.GONE);
            main.setVisibility(View.GONE);
            classic.setVisibility(View.GONE);
            challenge.setVisibility(View.GONE);
            newGame.setVisibility(View.VISIBLE);
            bestPlayers.setVisibility(View.VISIBLE);
        }
        super.onResume();
    }
}
