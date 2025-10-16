package tetro.puzzle;

import android.content.Context;
import android.content.Intent;
import android.content.SharedPreferences;
import android.graphics.Point;
import android.graphics.drawable.Drawable;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.LinearLayout;
import android.widget.TextView;

import androidx.activity.OnBackPressedCallback;
import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import com.facebook.AccessToken;
import java.util.ArrayList;
import java.util.List;


public class StageSelectActivity extends AppCompatActivity implements View.OnClickListener {

    private LinearLayout llObjectives;
    private TextView llTitleTV;
    private TextView tvObjectives;
    private int stageSelected;
    private String objective;
    private LinearLayout rowsLayout;
    private List<Button> affectedButtons;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_stage_select);

        OnBackPressedCallback callback = new OnBackPressedCallback(true) {
            @Override
            public void handleOnBackPressed() {
                if(llObjectives.getVisibility()==View.VISIBLE){
                    llObjectives.setVisibility(View.GONE);
                    for(int i=0; i<rowsLayout.getChildCount(); i++){
                        rowsLayout.getChildAt(i).setAlpha(1);
                        llTitleTV.setAlpha(1);
                    }
                    for(int i=0; i<affectedButtons.size(); i++){
                        affectedButtons.get(i).setEnabled(true);
                    }
                }else{
                    finish();
                }
            }
        };
        getOnBackPressedDispatcher().addCallback(this, callback);

        affectedButtons = new ArrayList<>();
        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int displayWidth = size.x;

        Context context = StageSelectActivity.this;

        llTitleTV = (TextView)findViewById(R.id.stageSelectTitleTV);

        llObjectives = (LinearLayout)findViewById(R.id.LLLevelObjectives);
        tvObjectives = (TextView) findViewById(R.id.TVLevelObjectives);

        Button start = (Button)findViewById(R.id.BTStart);
        start.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent i = new Intent(StageSelectActivity.this, DisplayGameActivity.class);
                i.putExtra("modo", Constants.MAIN_GAME);
                i.putExtra("stageSelected", stageSelected);
                i.putExtra("objective", objective);
                startActivity(i);
                finish();
            }
        });

        rowsLayout = (LinearLayout)findViewById(R.id.levelsRow);
        LinearLayout.LayoutParams rowParams = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT, ViewGroup.LayoutParams.WRAP_CONTENT);
        rowParams.setMargins(0, displayWidth*(25/4)/100,0,0);
        LinearLayout.LayoutParams buttonsParams = new LinearLayout.LayoutParams(displayWidth*25/100,displayWidth*25/100);
        buttonsParams.setMargins(displayWidth*(25/4)/100,0,0,0);
        LinearLayout rowLayout = null;
        Button stageButton;
        Drawable padlock = ContextCompat.getDrawable(context,R.drawable.candado_t);
        padlock.setBounds(0, -(int)(padlock.getIntrinsicHeight()*0.02), (int)(padlock.getIntrinsicWidth()*0.07), (int)(padlock.getIntrinsicHeight()*0.07)-(int)(padlock.getIntrinsicHeight()*0.03));
        Drawable thick = ContextCompat.getDrawable(context,R.drawable.tilde);
        thick.setBounds(0, -(int)(thick.getIntrinsicHeight()*0.02), (int)(thick.getIntrinsicWidth()*0.11), (int)(thick.getIntrinsicHeight()*0.07)-(int)(thick.getIntrinsicHeight()*0.03));

        SharedPreferences prefs;
        if(AccessToken.getCurrentAccessToken()==null){
            prefs = getSharedPreferences("sin_login_user",MODE_PRIVATE);
        }else{
            prefs = getSharedPreferences("con_login_user",MODE_PRIVATE);
        }
        int stage = Integer.parseInt(prefs.getString("stage","1"));
        for(int i=0; i<63; i++){
            if(i%3==0){
                rowLayout = new LinearLayout(context);
                rowLayout.setLayoutParams(rowParams);
                rowsLayout.addView(rowLayout);
            }
            stageButton = new Button(context);
            stageButton.setText("" + (i + 1));
            stageButton.setTextSize(26);
            stageButton.setTextColor(ContextCompat.getColor(StageSelectActivity.this,R.color.playAreaColor));
            stageButton.setBackground(getResources().getDrawable(R.drawable.button));
            if((i+1) > stage){
                stageButton.setCompoundDrawables(null, null, null, padlock);
                stageButton.setEnabled(false);
            }else{
                if((i+1) < stage){
                    stageButton.setCompoundDrawables(null, null, null, thick);
                }
                stageButton.setEnabled(true);
                affectedButtons.add(stageButton);

            }
            stageButton.setGravity(Gravity.CENTER_HORIZONTAL);
            stageButton.setLayoutParams(buttonsParams);
            stageButton.setId(i+1);
            stageButton.setOnClickListener(this);
            rowLayout.addView(stageButton);
        }
    }

    @Override
    public void onClick(View v) {

        for(int i=0; i<affectedButtons.size(); i++){
            affectedButtons.get(i).setEnabled(false);
        }
        llObjectives.setVisibility(View.VISIBLE);
        switch (v.getId()){
            case 1:
                objective = getResources().getString(R.string.level1);
                break;
            case 2:
                objective = getResources().getString(R.string.level2);
                break;
            case 3:
                objective = getResources().getString(R.string.level3);
                break;
            case 4:
                objective = getResources().getString(R.string.level4);
                break;
            case 5:
                objective = getResources().getString(R.string.level5);
                break;
            case 6:
                objective = getResources().getString(R.string.level6);
                break;
            case 7:
                objective = getResources().getString(R.string.level7);
                break;
            case 8:
                objective = getResources().getString(R.string.level8);
                break;
            case 9:
                objective = getResources().getString(R.string.level9);
                break;
            case 10:
                objective = getResources().getString(R.string.level10);
                break;
            case 11:
                objective = getResources().getString(R.string.level11);
                break;
            case 12:
                objective = getResources().getString(R.string.level12);
                break;
            case 13:
                objective = getResources().getString(R.string.level13);
                break;
            case 14:
                objective = getResources().getString(R.string.level14);
                break;
            case 15:
                objective = getResources().getString(R.string.level15);
                break;
            case 16:
                objective = getResources().getString(R.string.level16);
                break;
            case 17:
                objective = getResources().getString(R.string.level17);
                break;
            case 18:
                objective = getResources().getString(R.string.level18);
                break;
            case 19:
                objective = getResources().getString(R.string.level19);
                break;
            case 20:
                objective = getResources().getString(R.string.level20);
                break;
            case 21:
                objective = getResources().getString(R.string.level21);
                break;
            case 22:
                objective = getResources().getString(R.string.level22);
                break;
            case 23:
                objective = getResources().getString(R.string.level23);
                break;
            case 24:
                objective = getResources().getString(R.string.level24);
                break;
            case 25:
                objective = getResources().getString(R.string.level25);
                break;
            case 26:
                objective = getResources().getString(R.string.level26);
                break;
            case 27:
                objective = getResources().getString(R.string.level27);
                break;
            case 28:
                objective = getResources().getString(R.string.level28);
                break;
            case 29:
                objective = getResources().getString(R.string.level29);
                break;
            case 30:
                objective = getResources().getString(R.string.level30);
                break;
            case 31:
                objective = getResources().getString(R.string.level31);
                break;
            case 32:
                objective = getResources().getString(R.string.level32);
                break;
            case 33:
                objective = getResources().getString(R.string.level33);
                break;
            case 34:
                objective = getResources().getString(R.string.level34);
                break;
            case 35:
                objective = getResources().getString(R.string.level35);
                break;
            case 36:
                objective = getResources().getString(R.string.level36);
                break;
            case 37:
                objective = getResources().getString(R.string.level37);
                break;
            case 38:
                objective = getResources().getString(R.string.level38);
                break;
            case 39:
                objective = getResources().getString(R.string.level39);
                break;
            case 40:
                objective = getResources().getString(R.string.level40);
                break;
            case 41:
                objective = getResources().getString(R.string.level41);
                break;
            case 42:
                objective = getResources().getString(R.string.level42);
                break;
            case 43:
                objective = getResources().getString(R.string.level43);
                break;
            case 44:
                objective = getResources().getString(R.string.level44);
                break;
            case 45:
                objective = getResources().getString(R.string.level45);
                break;
            case 46:
                objective = getResources().getString(R.string.level46);
                break;
            case 47:
                objective = getResources().getString(R.string.level47);
                break;
            case 48:
                objective = getResources().getString(R.string.level48);
                break;
            case 49:
                objective = getResources().getString(R.string.level49);
                break;
            case 50:
                objective = getResources().getString(R.string.level50);
                break;
            case 51:
                objective = getResources().getString(R.string.level51);
                break;
            case 52:
                objective = getResources().getString(R.string.level52);
                break;
            case 53:
                objective = getResources().getString(R.string.level53);
                break;
            case 54:
                objective = getResources().getString(R.string.level54);
                break;
            case 55:
                objective = getResources().getString(R.string.level55);
                break;
            case 56:
                objective = getResources().getString(R.string.level56);
                break;
            case 57:
                objective = getResources().getString(R.string.level57);
                break;
            case 58:
                objective = getResources().getString(R.string.level58);
                break;
            case 59:
                objective = getResources().getString(R.string.level59);
                break;
            case 60:
                objective = getResources().getString(R.string.level60);
                break;
            case 61:
                objective = getResources().getString(R.string.level61);
                break;
            case 62:
                objective = getResources().getString(R.string.level62);
                break;
            case 63:
                objective = getResources().getString(R.string.level63);
                break;
        }

        for(int i=0; i<rowsLayout.getChildCount(); i++){
            rowsLayout.getChildAt(i).setAlpha(.2f);
        }
        llTitleTV.setAlpha(.2f);
        stageSelected = v.getId();
        tvObjectives.setText(objective);
        llObjectives.setVisibility(View.VISIBLE);
        llObjectives.bringToFront();
    }
}
