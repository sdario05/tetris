package tetromino.puzzle;

import com.facebook.AccessToken;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import android.app.Activity;
import android.app.AlertDialog;
import android.content.Context;
import android.content.DialogInterface;
import android.content.Intent;
import android.content.SharedPreferences;
import android.content.res.Resources;
import android.graphics.Point;
import android.graphics.Typeface;
import android.graphics.drawable.AnimationDrawable;
import android.media.AudioManager;
import android.media.MediaPlayer;
import android.media.SoundPool;
import android.net.ConnectivityManager;
import android.os.Build;
import android.os.Handler;
import android.os.Bundle;
import android.text.Editable;
import android.text.TextWatcher;
import android.util.DisplayMetrics;
import android.view.Display;
import android.view.Gravity;
import android.view.MotionEvent;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.TextView;
import android.widget.Toast;

import androidx.activity.OnBackPressedCallback;
import androidx.annotation.NonNull;
import androidx.annotation.RequiresApi;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.google.android.gms.ads.FullScreenContentCallback;
import com.google.android.gms.ads.LoadAdError;
import com.google.android.gms.ads.MobileAds;
import com.google.android.gms.ads.RequestConfiguration;
import com.google.android.gms.ads.interstitial.InterstitialAd;
import com.google.android.gms.ads.interstitial.InterstitialAdLoadCallback;
import com.loopj.android.http.AsyncHttpClient;
import com.loopj.android.http.AsyncHttpResponseHandler;
import org.json.JSONArray;
import org.json.JSONException;
import java.io.UnsupportedEncodingException;
import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.Locale;
import java.util.Random;
import cz.msebera.android.httpclient.Header;

public class DisplayGameActivity extends AppCompatActivity {
    private int navBarHeight;
    private int combo;
    private int x,y;
    private TextView tvtObjective;
    private TextView tvObjective;
    private Button resultRestart, resultBack;
    private LinearLayout llButtons;
    private RelativeLayout rlContentPlayArea;
    private ImageView resultImage;
    private boolean objectiveCompleted;
    private RelativeLayout resultLayout;
    private String appVersion;
    private int pixelsToGoDown;
    private int actualCompleteRow;
    private int[] deletedRows;
    private List<View> affectedViews;
    private View[] views;
    private boolean animation = false;
    private TextView TVLinesScore;
    private final int[] soundIds = new int[4];
    private SoundPool sp;
    private boolean mute;
    private RelativeLayout rlnextchip;
    private int randomNextChip;
    private int playAreaBorder;
    private int stageSelected;
    private String objective;
    private int brickSize;
    private MediaPlayer player;
    private RelativeLayout RLPlayArea;
    private LinearLayout LLTexts, pauseLayout;
    private Button continueButton, restartButton, exitButton;
    private Button okButton;
    private TextView pauseTag;
    private EditText userNameBox;
    private TextView userNameTag;
    private ImageView leftButton, rightButton, downButton, rotateButton, pauseButton, muteButton;
    private Chip chip;
    private final Random random = new Random();
    private int gameState = Constants.RUNNING;
    private TextView highestScoreTag;
    private TextView highestScoreBox;
    private TextView scoreTag;
    private TextView linesTag;
    private TextView levelTag;
    private TextView scoreBox;
    private TextView linesBox;
    private TextView levelBox;
    private boolean highestScore = false;
    private int score;
    private int level;
    private int lines;
    private int nextSpeedLines = Constants.INITIAL_NEXT_SPEED_LINES;
    private int timerValue;
    private boolean activeChip;
    private boolean[][] board = new boolean[Constants.PLAY_AREA_WIDTH]
            [Constants.PLAY_AREA_HEIGHT];

    private Handler handlerClock = new Handler();
    private Runnable runnableClock = new Runnable() {
        @Override
        public void run() {
            if(!tvObjective.getText().toString().equals("0") && !objectiveCompleted){
                tvObjective.setText(String.valueOf((Integer.parseInt(tvObjective.getText().toString())-1)));
                handlerClock.postDelayed(runnableClock,1000);
            }
        }
    };
    private Handler TVScoreLinesHandler = new Handler();
    private Runnable TVScoreLinesRunnable = new Runnable() {
        @Override
        public void run() {
            TVLinesScore.setY(TVLinesScore.getY() - 5);
            TVLinesScore.setAlpha(TVLinesScore.getAlpha() - 0.025f);
            if(TVLinesScore.getAlpha()>0){
                TVScoreLinesHandler.postDelayed(this, 25);
            }else{
                TVLinesScore.setY(Constants.PLAY_AREA_HEIGHT*brickSize); //esto es para que salga de la pantalla
                TVLinesScore.setVisibility(View.GONE);
            }
        }
    };


    private Handler LineDeleteHandler = new Handler();
    private Runnable LineDeleteRunnable = new Runnable() {
        @Override
        public void run() {
            int i=0;
            for(View brick : affectedViews){
                if(brick.getLayoutParams().height - brickSize*10/100 >= 0){
                    brick.getLayoutParams().height = brick.getLayoutParams().height - brickSize*10/100;
                    brick.setY(brick.getY() + brickSize*5/100);
                    RLPlayArea.requestLayout();
                    if(i==affectedViews.size()-1){
                        LineDeleteHandler.postDelayed(LineDeleteRunnable,10);
                    }
                }else{
                    RLPlayArea.removeView(brick);
                    if(i==affectedViews.size()-1){
                        affectedViews.clear();
                        loadArrayViews();
                        actualCompleteRow=0;
                        pixelsToGoDown=0;
                        if(stageSelected==2 || stageSelected==4 || stageSelected==6 || stageSelected==11 || stageSelected==13 ||
                           stageSelected==16 || stageSelected==23 || stageSelected==24 || stageSelected==28 || stageSelected==30 ||
                           stageSelected==34 || stageSelected==35 || stageSelected==38 || stageSelected==41 || stageSelected==44 ||
                           stageSelected==47 || stageSelected==48 || stageSelected>=50){
                            tvObjective.setText(String.valueOf(countGrayBricks()));
                        }

                        goDownUpperRows();
                    }
                }
                i++;
            }
        }
    };
    private Handler goDownLinesHandler = new Handler();
    private Runnable goDownLinesRunnable = new Runnable() {
        @Override
        public void run() {
            if(!affectedViews.isEmpty()){
                int i=0;
                for (View view : affectedViews) {
                    if(pixelsToGoDown + brickSize*10/100 <= brickSize) {
                        view.setY(view.getY() + brickSize*10/100);
                        if (i==affectedViews.size()-1) {
                            pixelsToGoDown += brickSize*10/100;
                            goDownLinesHandler.postDelayed(this, 10);
                        }
                    }else{
                        view.setY(view.getY()+(brickSize-pixelsToGoDown));
                        if (i==affectedViews.size()-1) {
                            actualCompleteRow++;
                            if(actualCompleteRow<4){
                                affectedViews.clear();
                                pixelsToGoDown=0;
                                goDownUpperRows();
                            }else{
                                animation=false;
                                pauseButton.setEnabled(true);
                                muteButton.setEnabled(true);
                                timerHandler.postDelayed(timerRunnable, 500);
                                //printBoard();
                            }
                        }
                    }
                    i++;
                }
            }else{
                actualCompleteRow++;
                if(actualCompleteRow<4){
                    affectedViews.clear();
                    goDownUpperRows();
                }else{
                    pauseButton.setEnabled(true);
                    muteButton.setEnabled(true);
                    animation=false;
                    timerHandler.postDelayed(timerRunnable, 500);
                    //printBoard();
                }
            }
        }
    };
    private Handler timerHandler = new Handler();
    private Runnable timerRunnable = new Runnable() {
        @Override
        public void run() {
            if(!activeChip && !objectiveCompleted){

                activeChip=true;
                int randomChip = randomNextChip;
                randomNextChip=random.nextInt(7);
                rlnextchip.removeAllViews();
                if((float)x/y>0.625){
                    if(navBarHeight>0){
                        drawChip(randomNextChip, rlnextchip, brickSize / 2, 5, 2, true); //ficha next
                    }else{
                        drawChip(randomNextChip, rlnextchip, brickSize / 2, 7, 1, true); //ficha next
                    }

                }else{
                    drawChip(randomNextChip, rlnextchip, brickSize / 2, 4, 1, true); //ficha next
                }

                drawChip(randomChip, RLPlayArea, brickSize, Constants.CHIP_INITIAL_POSITION_X, Constants.CHIP_INITIAL_POSITION_Y, false); //ficha actual
            }else{
                automaticGoDownChip();
            }
            if(gameState!=Constants.GAME_OVER && !animation){
                timerHandler.postDelayed(this, timerValue);
            }
        }
    };

    private boolean displayingInterstitial;

    private int countGrayBricks(){
        int count=0;
        for(int i=0; i<RLPlayArea.getChildCount(); i++){
            if(RLPlayArea.getChildAt(i).getTag().toString().equals("gray")){
                count++;
            }
        }
        return count;
    }

    private void drawChip(int nextChip, RelativeLayout layout, int brickSize, int positionX, int positionY, boolean next){
        switch(nextChip){
            case 0: chip = new IChip(layout, board, positionX,
                    positionY, brickSize, context, next, new GameOverListener() {
                @Override
                public void callGameOver() {
                    gameOver();
                }
            });
                break;
            case 1: chip = new LChip(layout, board, positionX,
                    positionY, brickSize, context, next, new GameOverListener() {
                @Override
                public void callGameOver() {
                    gameOver();
                }
            });
                break;
            case 2: chip = new ILChip(layout, board, positionX,
                    positionY, brickSize, context, next, new GameOverListener() {
                @Override
                public void callGameOver() {
                    gameOver();
                }
            });
                break;
            case 3: chip = new SChip(layout, board, positionX,
                    positionY, brickSize, context, next, new GameOverListener() {
                @Override
                public void callGameOver() {
                    gameOver();
                }
            });
                break;
            case 4: chip = new ISChip(layout, board, positionX,
                    positionY, brickSize, context, next, new GameOverListener() {
                @Override
                public void callGameOver() {
                    gameOver();
                }
            });
                break;
            case 5: chip = new SquareChip(layout, board, positionX,
                    positionY, brickSize, context, next, new GameOverListener() {
                @Override
                public void callGameOver() {
                    gameOver();
                }
            });
                break;
            case 6: chip = new TChip(layout, board, positionX,
                    positionY, brickSize, context, next, new GameOverListener() {
                @Override
                public void callGameOver() {
                    gameOver();
                }
            });
                break;
        }
    }

    public float convertDpToPixel(float dp){
        Resources resources = getResources();
        DisplayMetrics metrics = resources.getDisplayMetrics();
        return dp * ((float)metrics.densityDpi / DisplayMetrics.DENSITY_DEFAULT);
    }

    @RequiresApi(api = Build.VERSION_CODES.JELLY_BEAN_MR1)
    public int navBarHeight(Display d){

        DisplayMetrics realDisplayMetrics = new DisplayMetrics();
        d.getRealMetrics(realDisplayMetrics);

        int realHeight = realDisplayMetrics.heightPixels;
        int realWidth = realDisplayMetrics.widthPixels;

        DisplayMetrics displayMetrics = new DisplayMetrics();
        d.getMetrics(displayMetrics);

        int displayHeight = displayMetrics.heightPixels;
        int displayWidth = displayMetrics.widthPixels;

        if((realWidth - displayWidth) > 0 || (realHeight - displayHeight) > 0){
            return realHeight - displayHeight;
        }
        return 0;
    }

    private AdView adView;
    private  InterstitialAd interstitial;
    private  boolean interstitialLoaded = false;
    private  Context context;
    private  int modo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_display_game);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if (!displayingInterstitial) {
                    if (gameState != Constants.GAME_OVER) {
                        pause();
                    } else {
                        View view = new View(context);
                        view.setTag("EXIT");
                        myOnClick(view);
                    }
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        navBarHeight=0;
        appVersion = BuildConfig.VERSION_NAME;

        context = DisplayGameActivity.this;


        // Inicializar MobileAds y configurar test devices
        MobileAds.initialize(this, initializationStatus -> {});

        RequestConfiguration configuration = new RequestConfiguration.Builder()
                .setTestDeviceIds(Arrays.asList(
                        "10FCF45AC499FED384D8CB2AC611A8B9"
                ))
                .build();
        MobileAds.setRequestConfiguration(configuration);

        // -------------------------
        // Banner Ad
        // -------------------------
        adView = findViewById(R.id.adView);
        AdRequest adRequest = new AdRequest.Builder().build();
        if (isOnline()) {
            adView.loadAd(adRequest);
        }

        // -------------------------
        // Interstitial Ad
        // -------------------------
        loadInterstitial(adRequest);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();


        if (Build.VERSION.SDK_INT >16){
            navBarHeight = navBarHeight(display);
            display.getRealSize(size);
            y=size.y-navBarHeight;
        }else{
            display.getSize(size);
            y=size.y;
        }
        x=size.x;

        int padding = (int) (x * 0.03);

        llButtons = (LinearLayout)findViewById(R.id.LLButtons);
        playAreaBorder = (int)convertDpToPixel(2);

        //layout todo
        RelativeLayout rltodo = (RelativeLayout)findViewById(R.id.RLtodo);
        rltodo.setPadding(padding - playAreaBorder, padding - playAreaBorder, padding - playAreaBorder, padding - playAreaBorder);

        resultLayout = new RelativeLayout(DisplayGameActivity.this);
        RelativeLayout.LayoutParams resultParams = new RelativeLayout.LayoutParams(x*80/100,x*80/100);
        resultParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        resultLayout.setLayoutParams(resultParams);
        resultLayout.setPadding(padding, padding, padding, padding);
        resultLayout.setBackground(getResources().getDrawable(R.drawable.borde_celeste));
        rltodo.addView(resultLayout);
        resultLayout.setVisibility(View.GONE);

        resultImage = new ImageView(DisplayGameActivity.this);
        resultImage.setId(R.id.id_image_result);
        resultParams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, (x*70/100)/5*3);
        resultParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        resultParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        resultImage.setLayoutParams(resultParams);
        resultLayout.addView(resultImage);

        resultRestart = new Button(DisplayGameActivity.this);
        resultRestart.setId(R.id.id_restart_result);
        resultRestart.setTag("RESTART");
        resultRestart.setAllCaps(false);
        resultRestart.setText(getResources().getString(R.string.repeat));
        resultRestart.setTextSize(12f);
        resultRestart.setBackground(getResources().getDrawable(R.drawable.button2));
        resultParams = new RelativeLayout.LayoutParams(x*55/100, x*10/100);
        resultParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        resultParams.addRule(RelativeLayout.BELOW,resultImage.getId());
        resultRestart.setLayoutParams(resultParams);
        resultRestart.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myOnClick(v);
            }
        });
        resultLayout.addView(resultRestart);

        resultBack = new Button(DisplayGameActivity.this);
        resultBack.setId(R.id.id_back_result);
        resultBack.setTag("EXIT");
        resultBack.setText(getResources().getString(R.string.continueGame));
        resultBack.setTextSize(12f);
        resultBack.setAllCaps(false);
        resultBack.setBackground(getResources().getDrawable(R.drawable.button2));
        resultParams = new RelativeLayout.LayoutParams(x*55/100, x*10/100);
        resultParams.addRule(RelativeLayout.CENTER_HORIZONTAL);
        resultParams.addRule(RelativeLayout.BELOW, resultRestart.getId());
        resultParams.setMargins(0,20,0,0);
        resultBack.setLayoutParams(resultParams);
        resultBack.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                myOnClick(v);
            }
        });
        resultLayout.addView(resultBack);


        //inicio Layout de las fichas
        RLPlayArea = new RelativeLayout(context);
        RLPlayArea.setTag("RLPlayArea");
        RLPlayArea.setId(R.id.RLPlayAreaId);
        int playAreaWidth, playAreaHeight;
        float auxFloat;
        int auxInt;



        if((float)x/y>0.625){
            if(navBarHeight>0){
                auxFloat = (float)x/24*10;
            }else{
                auxFloat = (float)x/20*10;
            }
        }else{
            auxFloat = (float)x/17*10;
        }

        auxInt= (int)auxFloat;
        auxFloat = (float)auxInt/10;

        playAreaWidth = Math.round(auxFloat) * 10;


        brickSize = playAreaWidth/10;

        playAreaHeight = playAreaWidth*2;
        rlContentPlayArea = (RelativeLayout)findViewById(R.id.RLContentPlayArea);
        rlContentPlayArea.setLayoutParams(new RelativeLayout.LayoutParams(playAreaWidth + playAreaBorder * 2, playAreaHeight + playAreaBorder * 2));
        TVLinesScore = new TextView(context);
        TVLinesScore.setVisibility(View.GONE);
        TVLinesScore.setLayoutParams(new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT, ViewGroup.LayoutParams.WRAP_CONTENT));
        TVLinesScore.setTextSize(22);
        TVLinesScore.setTypeface(TVLinesScore.getTypeface(), Typeface.BOLD);
        TVLinesScore.setTextColor(context.getResources().getColor(R.color.playAreaColor));
        TVLinesScore.setBackgroundColor(context.getResources().getColor(R.color.transparent));
        rlContentPlayArea.addView(TVLinesScore);

        RelativeLayout RLlines = new RelativeLayout(DisplayGameActivity.this);
        RLlines.setLayoutParams(new RelativeLayout.LayoutParams(playAreaWidth, playAreaHeight));

        for(int i=1; i<10; i++){
            View line = new View(DisplayGameActivity.this);
            line.setBackgroundColor(ContextCompat.getColor(context,R.color.gray));
            RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams(1,playAreaHeight);
            lineParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lineParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lineParams.setMargins(i*brickSize,0,0,0);
            line.setLayoutParams(lineParams);
            RLlines.addView(line);
        }

        for(int i=1; i<20; i++){
            View line = new View(DisplayGameActivity.this);
            line.setBackgroundColor(ContextCompat.getColor(context,R.color.gray));
            RelativeLayout.LayoutParams lineParams = new RelativeLayout.LayoutParams(playAreaWidth,1);
            lineParams.addRule(RelativeLayout.ALIGN_PARENT_TOP);
            lineParams.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
            lineParams.setMargins(0,i*brickSize,0,0);
            line.setLayoutParams(lineParams);
            RLlines.addView(line);
        }

        RLPlayArea.setLayoutParams(new RelativeLayout.LayoutParams(playAreaWidth, playAreaHeight));
        rlContentPlayArea.addView(RLlines);
        rlContentPlayArea.addView(RLPlayArea);

        //inicio Layout botones
        leftButton = (ImageView)findViewById(R.id.leftButton);
        rightButton = (ImageView)findViewById(R.id.rightButton);
        downButton = (ImageView)findViewById(R.id.downButton);
        leftButton.setOnTouchListener(touchListener);
        rightButton.setOnTouchListener(touchListener);
        downButton.setOnTouchListener(touchListener);
        rotateButton = (ImageView)findViewById(R.id.rotateButton);
        int buttonsMargin = (int)((x - 2*padding)*0.02);
        int buttonWidth = (int)((x - 8*padding)/4);
        LinearLayout.LayoutParams buttonsParams = new LinearLayout.LayoutParams((int)(buttonWidth - buttonsMargin*7/9),
                (int)(buttonWidth - buttonsMargin*7/9));
        buttonsParams.setMargins(buttonsMargin + padding, 0, 0, 0);
        rightButton.setLayoutParams(buttonsParams);
        downButton.setLayoutParams(buttonsParams);
        rotateButton.setLayoutParams(buttonsParams);
        LinearLayout.LayoutParams button2Params = new LinearLayout.LayoutParams((int)(buttonWidth - buttonsMargin*7/9),
                (int)(buttonWidth - buttonsMargin*7/9));
        leftButton.setLayoutParams(button2Params);

        //inicio Layout etiquetas de texto
        LLTexts = (LinearLayout)findViewById(R.id.LLTexts);
        LLTexts.setPadding(padding, 0, padding, padding/2);
        int LLTextsWidth = x - 4*padding - playAreaWidth;
        LinearLayout.LayoutParams textViewParams = new LinearLayout.LayoutParams((int)(LLTextsWidth - LLTextsWidth * 0.2),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams.setMargins(0,buttonsMargin*2,0,0);

        LinearLayout.LayoutParams textViewHighetsScoreParams = new LinearLayout.LayoutParams((int)(LLTextsWidth - LLTextsWidth * 0.2),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewHighetsScoreParams.setMargins(0,buttonsMargin,0,0);

        LinearLayout.LayoutParams textViewParams2 = new LinearLayout.LayoutParams((int)(LLTextsWidth - LLTextsWidth * 0.2),
                ViewGroup.LayoutParams.WRAP_CONTENT);
        textViewParams2.setMargins(0, buttonsMargin / 2, 0, 0);

        highestScoreTag = (TextView)findViewById(R.id.TVThighestScore);
        highestScoreTag.setLayoutParams(textViewHighetsScoreParams);
        highestScoreTag.setGravity(Gravity.CENTER);
        highestScoreBox = (TextView)findViewById(R.id.TVhighestScore);
        highestScoreBox.setLayoutParams(textViewParams2);
        highestScoreBox.setGravity(Gravity.CENTER);

            AsyncHttpClient client = new AsyncHttpClient();
            client.setTimeout(30000);
            client.post("http://bricks.000webhostapp.com/phps/get_highest_score.php", new AsyncHttpResponseHandler() {
                @Override
                public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                    String resp = new String(responseBody);
                    try {
                        JSONArray json = new JSONArray(resp);
                        highestScoreBox.setText(String.valueOf(json.getJSONObject(0).getInt("MAX(score)")));
                    } catch (JSONException e) {
                        e.printStackTrace();
                    }
                }

                @Override
                public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                    highestScoreBox.setText(context.getResources().getString(R.string.couldntget));
                }
            });


        scoreTag = (TextView)findViewById(R.id.TVTscore);
        scoreTag.setLayoutParams(textViewParams);
        scoreTag.setGravity(Gravity.CENTER);
        scoreBox = (TextView)findViewById(R.id.TVscore);
        scoreBox.setLayoutParams(textViewParams2);
        scoreBox.setGravity(Gravity.CENTER);
        linesTag = (TextView)findViewById(R.id.TVTlines);
        linesTag.setLayoutParams(textViewParams);
        linesTag.setLayoutParams(textViewParams);
        linesTag.setGravity(Gravity.CENTER);
        linesBox = (TextView)findViewById(R.id.TVlines);
        linesBox.setLayoutParams(textViewParams2);
        linesBox.setGravity(Gravity.CENTER);
        levelTag = (TextView)findViewById(R.id.TVTlevel);
        levelTag.setLayoutParams(textViewParams);
        levelTag.setGravity(Gravity.CENTER);
        levelBox = (TextView)findViewById(R.id.TVlevel);
        levelBox.setLayoutParams(textViewParams2);
        levelBox.setGravity(Gravity.CENTER);
        tvtObjective = (TextView)findViewById(R.id.TVTObjective);
        tvtObjective.setLayoutParams(textViewParams);
        tvtObjective.setGravity(Gravity.CENTER);
        tvObjective = (TextView)findViewById(R.id.TVObjective);
        tvObjective.setLayoutParams(textViewParams2);
        tvObjective.setGravity(Gravity.CENTER);


        LinearLayout.LayoutParams pauseButtonParams = new LinearLayout.LayoutParams((int)(buttonWidth - buttonsMargin*7/9),
                (int)(buttonWidth - buttonsMargin*7/9));
        pauseButtonParams.setMargins(0, buttonsMargin, 0, 0);
        pauseButton = (ImageView)findViewById(R.id.pauseButton);
        pauseButton.setLayoutParams(pauseButtonParams);

        LinearLayout.LayoutParams muteButtonParams = new LinearLayout.LayoutParams((int)((buttonWidth - buttonsMargin*7/9)/2),
                (int)((buttonWidth - buttonsMargin*7/9)/2));
        muteButtonParams.setMargins(0, buttonsMargin, 0, buttonsMargin);

        muteButton = (ImageView)findViewById(R.id.muteButton);
        muteButton.setLayoutParams(muteButtonParams);



        if((float)x/y>0.625){
            LinearLayout llButtonsTextLayout = new LinearLayout(this);
            llButtonsTextLayout.setOrientation(LinearLayout.HORIZONTAL);
            LinearLayout.LayoutParams llButtonsTextLayoutParams = new LinearLayout.LayoutParams(
                    ViewGroup.LayoutParams.MATCH_PARENT,
                    ViewGroup.LayoutParams.WRAP_CONTENT);
            llButtonsTextLayoutParams.setMargins(0,buttonsMargin,0,0);
            llButtonsTextLayout.setLayoutParams(llButtonsTextLayoutParams);
            llButtonsTextLayout.setGravity(Gravity.CENTER_VERTICAL|Gravity.CENTER_HORIZONTAL);

            LLTexts.removeView(pauseButton);
            LLTexts.removeView(muteButton);
            if (navBarHeight>0){

                LinearLayout llcontentnext = (LinearLayout)findViewById(R.id.llcontentnext);
                LLTexts.removeView(llcontentnext);

                LinearLayout.LayoutParams llcontentnextParams  = new LinearLayout.LayoutParams((int)((LLTextsWidth - LLTextsWidth * 0.2)/2),
                        (int)(buttonWidth + buttonWidth/100));
                llcontentnextParams.setMargins(buttonsMargin*3,buttonsMargin,0,0);
                llcontentnextParams.gravity=Gravity.LEFT;
                llcontentnext.setLayoutParams(llcontentnextParams);
                llButtonsTextLayout.addView(llcontentnext);

                LinearLayout llPauseAndMute = new LinearLayout(this);
                LinearLayout.LayoutParams llPauseAndMuteParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        ViewGroup.LayoutParams.MATCH_PARENT);
                llPauseAndMute.setOrientation(LinearLayout.VERTICAL);
                llPauseAndMute.setLayoutParams(llPauseAndMuteParams);
                pauseButton.setLayoutParams(muteButtonParams);
                muteButton.setLayoutParams(muteButtonParams);
                LinearLayout.LayoutParams newMuteButtonParams = new LinearLayout.LayoutParams((int)((buttonWidth - buttonsMargin*7/9)/2),
                        (int)((buttonWidth - buttonsMargin*7/9)/2));
                newMuteButtonParams.setMargins(buttonsMargin*3, buttonsMargin/3, 0, 0);
                pauseButton.setLayoutParams(newMuteButtonParams);
                llPauseAndMute.addView(pauseButton);
                muteButton.setLayoutParams(newMuteButtonParams);
                llPauseAndMute.addView(muteButton);
                llButtonsTextLayout.addView(llPauseAndMute);

                LLTexts.addView(llButtonsTextLayout);
            }else{
                LinearLayout.LayoutParams pauseButtonParams2 = new LinearLayout.LayoutParams((int)(buttonWidth - buttonsMargin*7/9),
                        (int)(buttonWidth*7/9));
                pauseButton.setLayoutParams(pauseButtonParams2);
                llButtonsTextLayout.addView(pauseButton);
                muteButtonParams.setMargins(buttonsMargin, buttonsMargin, 0, buttonsMargin);
                muteButton.setLayoutParams(muteButtonParams);
                llButtonsTextLayout.addView(muteButton);
                LLTexts.addView(llButtonsTextLayout);
                LinearLayout llcontentnext = (LinearLayout)findViewById(R.id.llcontentnext);
                LinearLayout.LayoutParams llcontentnextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                        (int)(buttonWidth + buttonWidth*10/100));
                llcontentnextParams.setMargins(0,buttonsMargin,0,0);
                llcontentnext.setLayoutParams(llcontentnextParams);
            }

        }else{
            LinearLayout llcontentnext = (LinearLayout)findViewById(R.id.llcontentnext);
            LinearLayout.LayoutParams llcontentnextParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                    (int)(buttonWidth + buttonWidth*10/100));
            llcontentnextParams.setMargins(0,buttonsMargin,0,0);
            llcontentnext.setLayoutParams(llcontentnextParams);
        }


        //inicio Layout de pausa
        pauseLayout = (LinearLayout)findViewById(R.id.pauseLayout);

        TextView inGameObjectives = (TextView) findViewById(R.id.inGameTVObjectives);
        pauseTag = (TextView)findViewById(R.id.pauseTag);
        userNameTag = (TextView)findViewById(R.id.TVTName);
        userNameBox = (EditText)findViewById(R.id.ETName);
        okButton = (Button)findViewById(R.id.BTOk);
        continueButton = (Button)findViewById(R.id.continueButton);
        restartButton = (Button)findViewById(R.id.restartButton);
        exitButton = (Button)findViewById(R.id.exitButton);

        int pauseLayoutWidth = (int)((playAreaWidth-2*padding));
        int pauseLayoutHeight = (int)((playAreaHeight)*3/4);
        RelativeLayout.LayoutParams pauseLayoutParams = new RelativeLayout.LayoutParams(pauseLayoutWidth, ViewGroup.LayoutParams.WRAP_CONTENT);

        pauseLayoutParams.addRule(RelativeLayout.CENTER_IN_PARENT);
        pauseLayout.setLayoutParams(pauseLayoutParams);
        pauseLayout.setGravity(Gravity.CENTER);

        LinearLayout.LayoutParams viewsBottomMargin = new LinearLayout.LayoutParams(pauseLayoutWidth - pauseLayoutWidth * 10 / 100,
                pauseLayoutHeight/10);
        viewsBottomMargin.setMargins(0, 0, 0, pauseLayoutHeight / 20);

        LinearLayout.LayoutParams viewsWithoutBottomMargin = new LinearLayout.LayoutParams(pauseLayoutWidth - pauseLayoutWidth*10/100,
                pauseLayoutHeight/10);

        LinearLayout.LayoutParams editTextParams = new LinearLayout.LayoutParams(pauseLayoutWidth - pauseLayoutWidth*10/100,
                pauseLayoutHeight/8);


        pauseTag.setLayoutParams(viewsBottomMargin);
        userNameTag.setLayoutParams(viewsWithoutBottomMargin);
        userNameBox.setLayoutParams(editTextParams);
        okButton.setLayoutParams(viewsBottomMargin);
        continueButton.setLayoutParams(viewsBottomMargin);
        restartButton.setLayoutParams(viewsBottomMargin);
        exitButton.setLayoutParams(viewsBottomMargin);

        modo = getIntent().getExtras().getInt("modo");
        stageSelected = getIntent().getExtras().getInt("stageSelected");
        objective = getIntent().getExtras().getString("objective");
        inGameObjectives.setText(objective);

        randomNextChip=random.nextInt(7);
        rlnextchip = (RelativeLayout)findViewById(R.id.RLNextChip);
        mute=false;

        sp = new SoundPool(10,AudioManager.STREAM_MUSIC,0);
        setVolumeControlStream(AudioManager.STREAM_MUSIC);

        soundIds[0] = sp.load(context, R.raw.game_over_1,1);
        soundIds[1] = sp.load(context, R.raw.high_score_1,1);
        soundIds[2] = sp.load(context, R.raw.line_1,1);
        soundIds[3] = sp.load(context, R.raw.cheer,1);

        affectedViews = new ArrayList<View>();
        deletedRows = new int[]{-1,-1,-1,-1};
        initializeVars();
    }

    private void loadInterstitial(AdRequest adRequest) {

        InterstitialAd.load(this, "ca-app-pub-5386313981498626/8709112801", adRequest,
                new InterstitialAdLoadCallback() {
                    @Override
                    public void onAdLoaded(@NonNull InterstitialAd interstitialAd) {
                        interstitial = interstitialAd;
                        interstitialLoaded = true;

                        interstitial.setFullScreenContentCallback(new FullScreenContentCallback() {
                            @Override
                            public void onAdDismissedFullScreenContent() {
                                // Se cerró el anuncio
                                if (modo == Constants.MAIN_GAME) {
                                    startActivity(new Intent(context, StageSelectActivity.class));
                                    finish();
                                } else {
                                    finish();
                                }
                            }

                            @Override
                            public void onAdFailedToShowFullScreenContent(@NonNull com.google.android.gms.ads.AdError adError) {
                                interstitial = null;
                            }

                            @Override
                            public void onAdShowedFullScreenContent() {
                                interstitial = null;
                            }
                        });
                    }

                    @Override
                    public void onAdFailedToLoad(@NonNull LoadAdError loadAdError) {
                        interstitial = null;
                        interstitialLoaded = false;
                    }
                });
    }

    private  void initializeVars(){

        activeChip=false;
        combo=0;

        player = MediaPlayer.create(context, R.raw.harold);
        player.setLooping(true);
        player.setVolume(100, 100);
        player.start();

        score = 0;
        level =1;
        lines= 0;
        scoreBox.setText(String.valueOf(score));
        levelBox.setText(String.valueOf(level));
        linesBox.setText(String.valueOf(lines));
        timerValue=Constants.INITIAL_TIMER_VALUE;

        initializeBoard(modo);

        nextSpeedLines = Constants.INITIAL_NEXT_SPEED_LINES;
        gameState=Constants.RUNNING;
        timerHandler.postDelayed(timerRunnable, timerValue);

        pauseButton.setEnabled(true);
        muteButton.setEnabled(true);
        leftButton.setEnabled(true);
        rightButton.setEnabled(true);
        downButton.setEnabled(true);
        rotateButton.setEnabled(true);

        animation = false;

        rlContentPlayArea.setAlpha(1);
        LLTexts.setAlpha(1);
        llButtons.setAlpha(1);
    }

    private  void restartGame() {

        rlContentPlayArea.setAlpha(1f);
        LLTexts.setAlpha(1f);
        llButtons.setAlpha(1f);
        player.reset();
        muteButton.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.mute_off_button));
        RLPlayArea.removeAllViews();

        if(highestScore){
            scoreBox.setTextColor(ContextCompat.getColor(context,R.color.playAreaColor));
            scoreTag.setTextColor(ContextCompat.getColor(context,R.color.playAreaColor));
            highestScoreBox.setTextColor(ContextCompat.getColor(context,R.color.yellow));
            highestScoreTag.setTextColor(ContextCompat.getColor(context,R.color.yellow));
            highestScoreBox.setText(scoreBox.getText());
            highestScore = false;
        }
        pauseLayout.setVisibility(View.INVISIBLE);
        initializeVars();
    }

    private  void automaticGoDownChip() {
        if (chip != null) {
            int collided = chip.isCollidedFrom(board, RLPlayArea, brickSize);
            if(collided==Constants.BOTTOM_BORDER || collided==Constants.BOTTOM_LEFT_BORDER ||
                    collided == Constants.BOTTOM_RIGHT_BORDER || collided==Constants.BOTTOM_LEFT_RIGHT_BORDER){
                fillBoardBoxes();
                checkCompleteRows();
                activeChip=false;
            }else{
                chip.move("DOWN", RLPlayArea, brickSize,x,y,appVersion);
            }
        }
    }

    private  void loadArrayViews(){
        views = new  View[RLPlayArea.getChildCount()];
        for(int j=0; j<RLPlayArea.getChildCount(); j++){
            views[j] = RLPlayArea.getChildAt(j);
        }
    }

    private  void checkCompleteRows() {
        for(int i=0; i<4;i++){
            deletedRows[i]=-1;
        }
        int completeRows = 0;
        int lowerRowDeleted =0;
        for(int i=0; i<Constants.PLAY_AREA_HEIGHT; i++){
            boolean allRowTrue = true;
            for(int j=0; j<Constants.PLAY_AREA_WIDTH; j++){
                if(!board[j][i]){
                    allRowTrue = false;
                    break;
                }
            }
            if(allRowTrue){
                deletedRows[completeRows]=i;
                completeRows++;
                lowerRowDeleted = i;
                if(completeRows==4){
                    break;
                }
            }
        }
        if(completeRows>0){
            int partialScore=0;
            combo++;
            timerHandler.removeCallbacks(timerRunnable);
            animation = true;
            loadArrayViews();
            pauseButton.setEnabled(false);
            muteButton.setEnabled(false);
            affectedViews.clear();
            deleteCompleteRow();
            switch(completeRows){
                case 1: partialScore=100;
                    TVLinesScore.setText(String.valueOf(100));
                    break;
                case 2: partialScore=400;
                    TVLinesScore.setText(String.valueOf(400));
                    break;
                case 3: partialScore=900;
                    TVLinesScore.setText(String.valueOf(900));
                    break;
                case 4: partialScore=1600;
                    TVLinesScore.setText(String.valueOf(1600));
                    if(stageSelected == 20 || stageSelected == 32){
                        tvObjective.setText(String.valueOf((Integer.parseInt(tvObjective.getText().toString())+1) ));
                    }
                    break;
            }
            if(modo != Constants.MAIN_GAME || stageSelected==5 || stageSelected==8 || stageSelected==22 || stageSelected==33  || stageSelected==39  || stageSelected==43){
                if(modo!=Constants.MAIN_GAME){ //reto o clasico
                    score+=partialScore*combo;
                    if(combo>1){
                        TVLinesScore.setText(TVLinesScore.getText() + " X " + combo);
                    }
                }else{ //campaña
                    score+=partialScore;
                }
                TVLinesScore.setX(brickSize * 4);
                TVLinesScore.setY(lowerRowDeleted*brickSize - brickSize);
                TVLinesScore.setAlpha(1f);
                TVLinesScore.bringToFront();
                TVLinesScore.setVisibility(View.VISIBLE);
                TVScoreLinesHandler.postDelayed(TVScoreLinesRunnable,10);
            }
            lines+=completeRows;
            if(stageSelected !=19 && stageSelected !=27 && stageSelected !=40){
                accelerateSpeed();
            }
            linesBox.setText(String.valueOf(lines));
            scoreBox.setText(String.valueOf(score));
            if(!highestScore){ //si todavia no se supero el puntaje mas alto entra y pregunta si se supero
                if(!highestScoreBox.getText().toString().equals(context.getResources().getString(R.string.couldntget)) && !highestScoreBox.getText().toString().equals("...")){
                    if(Integer.valueOf(scoreBox.getText().toString()) > Integer.parseInt(highestScoreBox.getText().toString())){

                        scoreBox.setTextColor(ContextCompat.getColor(context,R.color.yellow));
                        scoreTag.setTextColor(ContextCompat.getColor(context,R.color.yellow));
                        highestScoreBox.setTextColor(ContextCompat.getColor(context,R.color.playAreaColor));
                        highestScoreTag.setTextColor(ContextCompat.getColor(context,R.color.playAreaColor));
                        highestScore = true;
                        sp.play(soundIds[1],1,1,1,0,1);
                    }else{
                        sp.play(soundIds[2],1,1,1,0,1);
                    }
                }else{
                    sp.play(soundIds[2],1,1,1,0,1);
                }
            }else{ //sino ya se supero el puntaje mas alto
                sp.play(soundIds[2],1,1,1,0,1);
            }
        }else{
            combo = 0;
        }
    }

    private  void goDownUpperRows() {
        int row = deletedRows[actualCompleteRow];
        if(row>0) {
            row--;
            while (row >= 0) {
                for (View view : views) {
                    if ((view.getY() / brickSize) == row) {
                        board[(int) (view.getX() / brickSize)][(int) (view.getY() / brickSize)] = false;        //pq casting a int
                        board[(int) (view.getX() / brickSize)][(int) (( view.getY() / brickSize) + 1)] = true;
                        affectedViews.add(view);
                    }
                }
                row--;
            }
        }
        goDownLinesHandler.postDelayed(goDownLinesRunnable, 1);
    }

    private  void deleteCompleteRow() {
        for(int row : deletedRows){
            if(row>=0){
                for(View view : views){
                    if(view.getY()/brickSize == row){
                        affectedViews.add(view);
                        board[(int) (view.getX()/brickSize)][row]=false;
                    }
                }
            }
        }
        LineDeleteHandler.postDelayed(LineDeleteRunnable, 1);
    }

    private  void accelerateSpeed() {
        if(lines>=nextSpeedLines){
            if(timerValue>=200){
                timerValue-=100;
            }else{
                timerValue-=10;
            }
            nextSpeedLines+=Constants.INITIAL_NEXT_SPEED_LINES;
            level++;
            levelBox.setText(String.valueOf(level));
        }
    }

    @Override
    protected void onStop() {
        if(gameState==Constants.RUNNING){
            pause();
        }
        super.onStop();
    }

    private View.OnTouchListener touchListener = new View.OnTouchListener() {
        private Handler mHandler;
        private View view;

        @Override
        public boolean onTouch(View v, MotionEvent event) {
            view = v;
            switch(event.getAction()) {
                case MotionEvent.ACTION_DOWN:
                    if (mHandler != null) return true;
                    mHandler = new Handler();
                    mHandler.postDelayed(mAction, 200);
                    break;
                case MotionEvent.ACTION_UP:
                    if (mHandler == null) return true;
                    mHandler.removeCallbacks(mAction);
                    mHandler = null;
                    break;
            }
            return false;
        }

        Runnable mAction = new Runnable() {
            @Override public void run() {
                myOnClick(view);
                if(gameState == Constants.RUNNING){
                    mHandler.postDelayed(this, 50);
                }
            }
        };
    };

    public void myOnClick(View v){
        String key = v.getTag().toString();
        if(key.equals("PAUSE") && gameState != Constants.GAME_OVER){
            pause();
        }
        if(key.equals("EXIT")){
            if(modo == Constants.MAIN_GAME){
                AlertDialog.Builder dialogBox = new AlertDialog.Builder(context);
                dialogBox.setTitle("Tetromino puzzle");
                dialogBox.setMessage(context.getResources().getString(R.string.wantToFinishStage));
                dialogBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {
                        if(interstitialLoaded){
                            displayInterstitial();
                            pauseLayout.setVisibility(View.INVISIBLE);
                            resultLayout.setVisibility(View.INVISIBLE);
                        }else{
                            Intent i;
                            i = new Intent(context,StageSelectActivity.class);
                            context.startActivity(i);
                            ((Activity)context).finish();
                        }
                    }
                });
                dialogBox.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialogOk = dialogBox.create();
                dialogOk.show();
            }else{
                AlertDialog.Builder dialogBox = new AlertDialog.Builder(context);
                dialogBox.setTitle("Tetromino puzzle");
                dialogBox.setMessage(context.getResources().getString(R.string.wantToFinishGame));
                dialogBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        sp.release();
                        if(interstitialLoaded){
                            displayInterstitial();
                            pauseLayout.setVisibility(View.INVISIBLE);
                            resultLayout.setVisibility(View.INVISIBLE);
                        }else{
                            ((Activity)context).finish();
                        }
                    }
                });
                dialogBox.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialogOk = dialogBox.create();
                dialogOk.show();
            }
        }
        if(key.equals("CONTINUE")){
            pause();
        }
        if(key.equals("RESTART")){
            if(gameState == Constants.GAME_OVER){
                if(modo!=Constants.MAIN_GAME) {
                    if(AccessToken.getCurrentAccessToken()==null){
                        InputMethodManager imm = (InputMethodManager) (context).getSystemService(Activity.INPUT_METHOD_SERVICE);
                        View view = ((Activity)context).getCurrentFocus();
                        imm.hideSoftInputFromWindow(view.getWindowToken(), 0);
                    }
                }
                resultLayout.setVisibility(View.GONE);
                restartGame();
            }else{
                AlertDialog.Builder dialogBox = new AlertDialog.Builder(context);
                dialogBox.setTitle("Tetromino puzzle");
                if(modo!=Constants.MAIN_GAME) {
                    dialogBox.setMessage(context.getResources().getString(R.string.endGameAndRestart));
                }else{
                    dialogBox.setMessage(context.getResources().getString(R.string.restartStage));
                }
                dialogBox.setPositiveButton("OK", new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                        restartGame();
                    }
                });
                dialogBox.setNegativeButton(context.getResources().getString(R.string.cancel), new DialogInterface.OnClickListener() {
                    @Override
                    public void onClick(DialogInterface dialog, int which) {

                    }
                });
                AlertDialog dialogOk = dialogBox.create();
                dialogOk.show();
            }
        }
        if(key.equals("OK")){
            InputMethodManager imm = (InputMethodManager) (context).getSystemService(Activity.INPUT_METHOD_SERVICE);
            imm.hideSoftInputFromWindow(userNameBox.getWindowToken(), 0);
            if(userNameBox.length()>0) {
                okButton.setEnabled(false);
                try {
                    saveScore("noName");
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }else{
                Toast.makeText(context,context.getResources().getString(R.string.noName),Toast.LENGTH_SHORT).show();
            }
        }
        if(activeChip){
            if (key.equals("ROTATE")) {
                chip.rotate(board, RLPlayArea, brickSize,context);
            }else{
                arrowPressed(key);
            }
        }
        if(key.equals("MUTE")){
            if(player.isPlaying()){
                muteButton.setImageDrawable(context.getResources().getDrawable(R.drawable.mute_on_button));
                player.pause();
                mute=true;
            }else{
                muteButton.setImageDrawable(context.getResources().getDrawable(R.drawable.mute_off_button));
                player.reset();
                player = MediaPlayer.create(context,R.raw.harold);
                player.setLooping(true);
                player.setVolume(100, 100);
                player.start();
                mute=false;
            }
        }
    }

    private  void saveScore(String name) throws UnsupportedEncodingException {

        String nameToSave = name;
        if(nameToSave.equals("noName")){
            nameToSave=userNameBox.getText().toString();
        }
        AsyncHttpClient client = new AsyncHttpClient();
        client.setTimeout(30000);
        client.post("https://tetromino.page.gd/save_score.php?score=" + URLEncoder.encode(scoreBox.getText().toString(),"UTF-8") +
                        "&name=" + URLEncoder.encode(nameToSave,"UTF-8"),
                new AsyncHttpResponseHandler() {
                    @Override
                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {
                        String respuesta = new String(responseBody);
                        if (respuesta.equals("CORRECTO")) {
                            okButton.setVisibility(View.GONE);
                            userNameTag.setVisibility(View.GONE);
                            userNameBox.setVisibility(View.GONE);
                            Toast.makeText(context, context.getResources().getString(R.string.resultSaved), Toast.LENGTH_LONG).show();
                        } else {
                            Toast.makeText(context, context.getResources().getString(R.string.resultNotSaved), Toast.LENGTH_LONG).show();
                        }
                        okButton.setEnabled(true);
                    }

                    @Override
                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {
                        Toast.makeText(context, context.getResources().getString(R.string.serverConnectionError), Toast.LENGTH_LONG).show();
                        okButton.setEnabled(true);
                    }
                });
    }

    private  void arrowPressed(String key){

        switch(chip.isCollidedFrom(board, RLPlayArea, brickSize)){
            case Constants.NO_COLLIDED: chip.move(key, RLPlayArea, brickSize,x,y,appVersion);
                break;
            case Constants.BOTTOM_BORDER: switch(key){
                case "DOWN": fillBoardBoxes();
                    checkCompleteRows();
                    activeChip=false;
                    break;
                case "LEFT": chip.move(key, RLPlayArea, brickSize,x,y,appVersion);
                    break;
                case "RIGHT": chip.move(key, RLPlayArea, brickSize,x,y,appVersion);
                    break;
            }
                break;
            case Constants.LEFT_BORDER: switch(key){
                case "DOWN": chip.move(key, RLPlayArea, brickSize,x,y,appVersion);
                    break;
                case "LEFT": //nada, no se mueve
                    break;
                case "RIGHT": chip.move(key, RLPlayArea, brickSize,x,y,appVersion);
                    break;
            }
                break;
            case Constants.RIGHT_BORDER: switch(key){
                case "DOWN": chip.move(key, RLPlayArea, brickSize,x,y,appVersion);
                    break;
                case "LEFT": chip.move(key, RLPlayArea, brickSize,x,y,appVersion);
                    break;
                case "RIGHT": //nada, no se mueve
                    break;
            }
                break;
            case Constants.BOTTOM_LEFT_RIGHT_BORDER: switch(key){
                case "DOWN": fillBoardBoxes();
                    checkCompleteRows();
                    activeChip=false;
                    break;
                case "LEFT": //nada
                    break;
                case "RIGHT": //nada
                    break;
            }
                break;
            case Constants.BOTTOM_LEFT_BORDER: switch(key){
                case "DOWN": fillBoardBoxes();
                    checkCompleteRows();
                    activeChip=false;
                    break;
                case "LEFT": //nada
                    break;
                case "RIGHT": chip.move(key, RLPlayArea, brickSize,x,y,appVersion);
                    break;
            }
                break;
            case Constants.BOTTOM_RIGHT_BORDER: switch(key){
                case "DOWN": fillBoardBoxes();
                    checkCompleteRows();
                    activeChip=false;
                    break;
                case "LEFT": chip.move(key, RLPlayArea, brickSize,x,y,appVersion);
                    break;
                case "RIGHT": //nada
                    break;
            }
                break;
            case Constants.LEFT_RIGHT_BORDER: switch(key){
                case "DOWN": chip.move(key, RLPlayArea, brickSize,x,y,appVersion);
                    break;
                case "LEFT": //nada
                    break;
                case "RIGHT": //nada
                    break;
            }
                break;
        }
    }



    private  void pause() {
        if(gameState == Constants.RUNNING){
            rlContentPlayArea.setAlpha(.2f);
            LLTexts.setAlpha(.2f);
            llButtons.setAlpha(.2f);
            pauseButton.setEnabled(false);
            muteButton.setEnabled(false);
            leftButton.setEnabled(false);
            rightButton.setEnabled(false);
            downButton.setEnabled(false);
            rotateButton.setEnabled(false);
            if(stageSelected==3 || stageSelected==7 || stageSelected==10 || stageSelected==12 || stageSelected==14 || stageSelected==15 ||
               stageSelected==18 || stageSelected==21 || stageSelected==29 || stageSelected==31 || stageSelected==36 || stageSelected==42 ||
               stageSelected==46 || stageSelected==49){
                handlerClock.removeCallbacks(runnableClock);
            }
            timerHandler.removeCallbacks(timerRunnable);
            activeChip=false;
            gameState=Constants.PAUSED;
            pauseTag.setText(context.getResources().getString(R.string.gamePaused));
            userNameTag.setVisibility(View.GONE);
            userNameBox.setVisibility(View.GONE);
            okButton.setVisibility(View.GONE);
            continueButton.setVisibility(View.VISIBLE);
            pauseLayout.setVisibility(View.VISIBLE);
            player.pause();
        }else{
            rlContentPlayArea.setAlpha(1);
            LLTexts.setAlpha(1);
            llButtons.setAlpha(1);
            pauseButton.setEnabled(true);
            muteButton.setEnabled(true);
            leftButton.setEnabled(true);
            rightButton.setEnabled(true);
            downButton.setEnabled(true);
            rotateButton.setEnabled(true);
            timerHandler.postDelayed(timerRunnable, 100);
            if(stageSelected==3 || stageSelected==7 || stageSelected==10 || stageSelected==12 || stageSelected==14 || stageSelected==15 ||
                    stageSelected==18 || stageSelected==21 || stageSelected==29 || stageSelected==31 || stageSelected==36 || stageSelected==42 ||
                    stageSelected==46 || stageSelected==49){
                handlerClock.postDelayed(runnableClock, 1000);
            }
            if (chip != null) {
                activeChip=true;
            }
            gameState=Constants.RUNNING;
            pauseLayout.setVisibility(View.INVISIBLE);
            if(!mute){
                player.start();
            }
        }
    }

    private  boolean isOnline(){
        ConnectivityManager cm = (ConnectivityManager)context.getSystemService(Context.CONNECTIVITY_SERVICE);
        return cm.getActiveNetworkInfo() != null && cm.getActiveNetworkInfo().isConnectedOrConnecting();
    }

    public  void gameOver() {
        rlContentPlayArea.setAlpha(.2f);
        LLTexts.setAlpha(.2f);
        llButtons.setAlpha(.2f);
        pauseButton.setEnabled(false);
        muteButton.setEnabled(false);
        leftButton.setEnabled(false);
        rightButton.setEnabled(false);
        downButton.setEnabled(false);
        rotateButton.setEnabled(false);
        player.pause();
        timerHandler.removeCallbacks(timerRunnable);
        gameState = Constants.GAME_OVER;
        activeChip=false;

        if(modo != Constants.MAIN_GAME){
            sp.play(soundIds[0],1,1,1,0,1);
            pauseTag.setText(context.getResources().getString(R.string.endGame));
            userNameTag.setVisibility(View.GONE);
            userNameBox.setVisibility(View.GONE);
            okButton.setVisibility(View.GONE);
            continueButton.setVisibility(View.GONE);
            if(AccessToken.getCurrentAccessToken()==null){
                userNameTag.setVisibility(View.VISIBLE);
                userNameBox.setVisibility(View.VISIBLE);
                okButton.setVisibility(View.VISIBLE);
                pauseLayout.setVisibility(View.VISIBLE);
                userNameBox.requestFocus();
                InputMethodManager imm = (InputMethodManager) (context).getSystemService(Activity.INPUT_METHOD_SERVICE);
                imm.showSoftInput(userNameBox, InputMethodManager.SHOW_IMPLICIT);
            }else{
                SharedPreferences prefs = context.getSharedPreferences("con_login_user",MODE_PRIVATE);
                try {
                    saveScore(prefs.getString("name","x"));
                } catch (UnsupportedEncodingException e) {
                    e.printStackTrace();
                }
            }
            pauseLayout.setVisibility(View.VISIBLE);
        }else{
            rlContentPlayArea.setAlpha(.2f);
            LLTexts.setAlpha(.2f);
            llButtons.setAlpha(.2f);
            if(objectiveCompleted){

                SharedPreferences prefs;
                SharedPreferences.Editor editor;
                if(AccessToken.getCurrentAccessToken()==null){
                    prefs = context.getSharedPreferences("sin_login_user", MODE_PRIVATE);
                    int lastUnlockedStage = Integer.parseInt(prefs.getString("stage","1"));
                    if(objectiveCompleted && lastUnlockedStage == stageSelected){
                        editor = prefs.edit();
                        int stage = Integer.parseInt(prefs.getString("stage","1"));
                        stage++;
                        editor.putString("stage",String.valueOf(stage));
                        editor.commit();
                    }
                }else{
                    prefs = context.getSharedPreferences("con_login_user", MODE_PRIVATE);
                    int lastUnlockedStage = Integer.parseInt(prefs.getString("stage","1"));
                    if(objectiveCompleted && lastUnlockedStage == stageSelected){
                        AsyncHttpClient client = new AsyncHttpClient();
                        client.setMaxRetriesAndTimeout(5,30000);
                        String fbidToSave = prefs.getString("fbid","0");
                        client.post("http://bricks.000webhostapp.com/phps/save_level.php?fbid=" + URLEncoder.encode(fbidToSave)
                                , new AsyncHttpResponseHandler() {
                                    @Override
                                    public void onSuccess(int statusCode, Header[] headers, byte[] responseBody) {}

                                    @Override
                                    public void onFailure(int statusCode, Header[] headers, byte[] responseBody, Throwable error) {}
                                });
                        editor = prefs.edit();
                        int stage = Integer.parseInt(prefs.getString("stage","1"));
                        stage++;
                        editor.putString("stage",String.valueOf(stage));
                        editor.commit();
                    }
                }

                resultRestart.setText(context.getResources().getString(R.string.repeat));
                resultBack.setText(context.getResources().getString(R.string.continueGame));
                sp.play(soundIds[3], 1, 1, 1, 0, 1);
                if(Locale.getDefault().getLanguage().equals("es")){
                    resultImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.animation_ganaste));
                    AnimationDrawable anim = (AnimationDrawable)resultImage.getDrawable();
                    anim.start();
                }else{
                    resultImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.animation_win));
                    AnimationDrawable anim = (AnimationDrawable)resultImage.getDrawable();
                    anim.start();
                }
            }else{
                if(stageSelected==10 || stageSelected==14 || stageSelected==18 || stageSelected==21){
                    handlerClock.removeCallbacks(runnableClock);
                }
                resultRestart.setText(context.getResources().getString(R.string.retry));
                resultBack.setText(context.getResources().getString(R.string.backToSelectStage));
                sp.play(soundIds[0], 1, 1, 1, 0, 1);
                if(Locale.getDefault().getLanguage().equals("es")){
                    resultImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.animation_perdiste));
                    AnimationDrawable anim = (AnimationDrawable)resultImage.getDrawable();
                    anim.start();
                }else {
                    resultImage.setImageDrawable(ContextCompat.getDrawable(context, R.drawable.animation_lost));
                    AnimationDrawable anim = (AnimationDrawable) resultImage.getDrawable();
                    anim.start();
                }
            }
            resultLayout.setVisibility(View.VISIBLE);
        }
    }

    private  void fillBoardBoxes() {
        int children = RLPlayArea.getChildCount();
        int brick = brickSize;
        for(int i=4; i>0; i--){
            board[(int) (RLPlayArea.getChildAt(children-i).getX()/brick)][(int) (RLPlayArea.getChildAt(children-i).getY()/brick)]=true;
        }
    }

    private  void generateFalseBoard(){
        for(int i=0; i<board.length; i++){
            for(int j=0; j<board[i].length; j++ ){
                board[i][j]=false;
            }
        }
    }

    private  void generateRandomBoard(int emptyRows, int bricks){
        Random random = new Random();
        int color=0;
        for(int i=emptyRows; i<Constants.PLAY_AREA_HEIGHT; i++){
            for(int j=0;j<bricks; j++){
                int randomNumber;
                int randomNumber2;
                do {
                    randomNumber = random.nextInt(10);
                }while(board[randomNumber][i]);
                board[randomNumber][i]=true;
                if(stageSelected==2 || stageSelected==4 || stageSelected==6 || stageSelected==11 || stageSelected==13 ||
                   stageSelected==16 || stageSelected==23 || stageSelected==24 || stageSelected==28 || stageSelected==30 ||
                   stageSelected==34 || stageSelected==35 || stageSelected==38 || stageSelected==41 || stageSelected==44 ||
                   stageSelected==47 || stageSelected==48 || stageSelected>=50){
                    randomNumber2 = 7;
                }else{
                    randomNumber2 = random.nextInt(7);
                }
                switch (randomNumber2){
                    case 0:
                        color=R.drawable.orange;
                        break;
                    case 1:
                        color=R.drawable.blue;
                        break;
                    case 2:
                        color=R.drawable.cyan;
                        break;
                    case 3:
                        color=R.drawable.green;
                        break;
                    case 4:
                        color=R.drawable.pink;
                        break;
                    case 5:
                        color=R.drawable.red;
                        break;
                    case 6:
                        color=R.drawable.yellow;
                        break;
                    case 7:
                        color=R.drawable.gray;
                        break;
                }
                new Brick(RLPlayArea,color,randomNumber,i,brickSize,context);
            }
        }
    }

    private  void initializeBoard(int modo){
        objectiveCompleted = false;
        generateFalseBoard();
        switch (modo){
            case Constants.CLASSIC_GAME:
                break;
            case Constants.EASY_CHALLENGE:
                generateRandomBoard(15,9); //se pasa las filas libres
                break;
            case Constants.HARD_CHALLENGE:
                generateRandomBoard(10,5);
                break;
            case Constants.MAIN_GAME:
                switch (stageSelected){
                    case 1:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=25){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 2:
                        generateRandomBoard(15,9);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(5*9));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 3:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(2*60));
                        handlerClock.postDelayed(runnableClock,1000);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=10){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 4:
                        generateRandomBoard(10,9);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(10*9));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }
                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;


                    case 5:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=10000){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;

                    case 6:
                        generateRandomBoard(15,8);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(5*8));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;

                    case 7:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(4*60));
                        handlerClock.postDelayed(runnableClock,1000);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=20){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 8:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=15000){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 9:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=50){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 10:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(3*60));
                        level=9;
                        levelBox.setText(""+level);
                        timerValue = timerValue - ((level-1)*100);
                        handlerClock.postDelayed(runnableClock,1000);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 11:
                        generateRandomBoard(10,8);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(10*8));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 12:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(6*60));
                        handlerClock.postDelayed(runnableClock,1000);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=30){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 13:
                        generateRandomBoard(15,7);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(5*7));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 14:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(5*60));
                        level=8;
                        levelBox.setText(""+level);
                        timerValue = timerValue - ((level-1)*100);

                        handlerClock.postDelayed(runnableClock,1000);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 15:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(8*60));
                        handlerClock.postDelayed(runnableClock,1000);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=40){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 16:
                        generateRandomBoard(10,7);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(10*7));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }
                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 17:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=75){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 18:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(String.valueOf(10*60));
                        level=5;
                        levelBox.setText(""+level);
                        timerValue = timerValue - ((level-1)*100);

                        handlerClock.postDelayed(runnableClock,1000);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 19:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                level = (Integer.parseInt(s.toString())) + 1;
                                if(!levelBox.getText().toString().equals(String.valueOf(level))){
                                    levelBox.setText(""+level);
                                    if(Integer.parseInt(levelBox.getText().toString())>=11){
                                        timerValue = 100 - (level-10)*10;
                                    }else{
                                        timerValue = Constants.INITIAL_TIMER_VALUE - ((level-1)*100);
                                    }

                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        levelBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString())>=13){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 20:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvtObjective.setText(R.string.fourLines);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText("0");
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("4")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 21:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(5*60));
                        level=6;
                        levelBox.setText(""+level);
                        timerValue = timerValue - ((level-1)*100);

                        handlerClock.postDelayed(runnableClock,1000);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 22:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=20000){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 23:
                        generateRandomBoard(15,6);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(5*6));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 24:
                        generateRandomBoard(10,6);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(10*6));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }
                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 25:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=100){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 26:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        leftButton.setTag("RIGHT");
                        rightButton.setTag("LEFT");
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=50){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }
                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 27:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                level = (Integer.parseInt(s.toString())/3) + 1;
                                if(!levelBox.getText().toString().equals(String.valueOf(level))){
                                    levelBox.setText(""+level);
                                    if(Integer.parseInt(levelBox.getText().toString())>=11){
                                        timerValue = 100 - (level-10)*10;
                                    }else{
                                        timerValue = Constants.INITIAL_TIMER_VALUE - ((level-1)*100);
                                    }
                                }

                                /*
                                level = (Integer.parseInt(s.toString())) + 1;
                                if(!levelBox.getText().toString().equals(String.valueOf(level))){
                                    levelBox.setText(""+level);
                                    if(Integer.parseInt(levelBox.getText().toString())>=11){
                                        timerValue = 100 - (level-10)*10;
                                    }else{
                                        timerValue = Constants.INITIAL_TIMER_VALUE - ((level-1)*100);
                                    }
                                }
                                 */
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        levelBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString())>=11){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 28:
                        generateRandomBoard(15,5);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(5*5));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 29:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(10*60));
                        handlerClock.postDelayed(runnableClock,1000);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=50){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 30:
                        generateRandomBoard(15,4);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(5*4));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 31:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(12*60));
                        handlerClock.postDelayed(runnableClock,1000);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=60){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 32:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvtObjective.setText(R.string.fourLines);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText("0");
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("10")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 33:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=25000){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 34:
                        generateRandomBoard(10,5);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(10*5));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 35:
                        generateRandomBoard(15,3);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(5*3));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 36:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(14*60));
                        handlerClock.postDelayed(runnableClock,1000);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=70){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 37:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=125){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 38:
                        generateRandomBoard(10,4);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(10*4));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 39:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=30000){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 40:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                level = (Integer.parseInt(s.toString())/5) + 1;
                                if(!levelBox.getText().toString().equals(String.valueOf(level))){
                                    levelBox.setText(""+level);
                                    if(Integer.parseInt(levelBox.getText().toString())>=11){
                                        timerValue = 100 - (level-10)*10;
                                    }else{
                                        timerValue = Constants.INITIAL_TIMER_VALUE - ((level-1)*100);
                                    }
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        levelBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString())>=10){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 41:
                        generateRandomBoard(10,3);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(10*3));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 42:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(16*60));
                        handlerClock.postDelayed(runnableClock,1000);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=80){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 43:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=35000){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 44:
                        generateRandomBoard(15,2);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(5*2));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 45:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=150){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 46:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(18*60));
                        handlerClock.postDelayed(runnableClock,1000);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=90){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 47:
                        generateRandomBoard(10,2);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(10*2));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 48:
                        generateRandomBoard(15,1);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(5*1));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 49:
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(20*60));
                        handlerClock.postDelayed(runnableClock,1000);
                        linesBox.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(Integer.parseInt(s.toString()) >=100){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {

                            }

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {

                            }
                        });
                        break;
                    case 50:
                        generateRandomBoard(10,1);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(10*1));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;

                    case 51:
                        for(int i=Constants.PLAY_AREA_HEIGHT-1; i>=10; i--) {
                            for (int j = 0; j < Constants.PLAY_AREA_WIDTH; j++) {
                                if((j%2==0 && i%2==0) || (j%2!=0 && i%2!=0)){
                                    board[j][i]=true;
                                    new Brick(RLPlayArea,R.drawable.gray,j,i,brickSize,context);
                                }
                            }
                        }
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(10*5));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;

                    case 52:
                        generateRandomBoard(5,9);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(15*9));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 53:
                        generateRandomBoard(5,8);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(15*8));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 54:
                        generateRandomBoard(5,7);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(15*7));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;

                    case 55:
                        for(int i=Constants.PLAY_AREA_HEIGHT-1; i>=10; i--) {
                            for (int j = 0; j < Constants.PLAY_AREA_WIDTH; j+=2) {
                                if(j%2==0){
                                    board[j][i]=true;
                                    new Brick(RLPlayArea,R.drawable.gray,j,i,brickSize,context);
                                }
                            }
                        }
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(50));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;

                    case 56:
                        generateRandomBoard(5,6);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(15*6));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 57:
                        generateRandomBoard(5,5);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(15*5));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 58:
                        generateRandomBoard(5,4);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(15*4));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;

                    case 59:
                        int j=0;
                        for(int i=Constants.PLAY_AREA_HEIGHT-1; i>=10; i--) {
                            board[j][i]=true;
                            new Brick(RLPlayArea,R.drawable.gray,j,i,brickSize,context);
                            j++;
                            board[10-j][i]=true;
                            new Brick(RLPlayArea,R.drawable.gray,10-j,i,brickSize,context);
                        }
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(20));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;

                    case 60:
                        generateRandomBoard(5,3);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(15*3));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;



                    case 61:
                        generateRandomBoard(5,2);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(15*2));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 62:
                        generateRandomBoard(5,1);
                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+(15*1));
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                    case 63:

                        for(int jj=0; jj<=9; jj+=9){
                            for(int i=0; i<4; i++){
                                board[i+3][jj+10]=true;
                                new Brick(RLPlayArea,R.drawable.gray,i+3,jj+10,brickSize,context);
                            }
                        }
                        for(int jj=0; jj<=9; jj+=9){
                            for(int i=13; i<17; i++){
                                board[jj][i]=true;
                                new Brick(RLPlayArea,R.drawable.gray,jj,i,brickSize,context);
                            }
                        }
                        board[2][11]=true;
                        new Brick(RLPlayArea,R.drawable.gray,2,11,brickSize,context);
                        board[7][11]=true;
                        new Brick(RLPlayArea,R.drawable.gray,7,11,brickSize,context);
                        board[1][12]=true;
                        new Brick(RLPlayArea,R.drawable.gray,1,12,brickSize,context);
                        board[8][12]=true;
                        new Brick(RLPlayArea,R.drawable.gray,8,12,brickSize,context);
                        board[1][17]=true;
                        new Brick(RLPlayArea,R.drawable.gray,1,17,brickSize,context);
                        board[8][17]=true;
                        new Brick(RLPlayArea,R.drawable.gray,8,17,brickSize,context);
                        board[2][18]=true;
                        new Brick(RLPlayArea,R.drawable.gray,2,18,brickSize,context);
                        board[7][18]=true;
                        new Brick(RLPlayArea,R.drawable.gray,7,18,brickSize,context);



                        highestScoreBox.setVisibility(View.GONE);
                        highestScoreTag.setVisibility(View.GONE);
                        scoreBox.setVisibility(View.GONE);
                        scoreTag.setVisibility(View.GONE);
                        tvtObjective.setText(R.string.remainingBricks);
                        tvtObjective.setVisibility(View.VISIBLE);
                        tvObjective.setText(""+24);
                        tvObjective.setVisibility(View.VISIBLE);
                        tvObjective.addTextChangedListener(new TextWatcher() {
                            @Override
                            public void beforeTextChanged(CharSequence s, int start, int count, int after) {}

                            @Override
                            public void onTextChanged(CharSequence s, int start, int before, int count) {
                                if(s.toString().equals("0")){
                                    objectiveCompleted = true;
                                    gameOver();
                                }
                            }

                            @Override
                            public void afterTextChanged(Editable s) {}
                        });
                        break;
                }
                break;
        }
    }

    public void displayInterstitial() {
        displayingInterstitial = true;
        if (interstitial != null) {
            interstitial.show(this); // 'this' debe ser tu Activity
        } else {
            // Si no está cargado, hacer algo alternativo
            if (modo == Constants.MAIN_GAME) {
                startActivity(new Intent(context, StageSelectActivity.class));
                finish();
            } else {
                finish();
            }
        }
    }
}

