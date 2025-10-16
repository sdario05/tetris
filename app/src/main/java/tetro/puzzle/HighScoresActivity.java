package tetro.puzzle;

import android.graphics.Point;
import android.os.Bundle;
import android.view.Display;
import android.view.Gravity;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.RelativeLayout;
import android.widget.ScrollView;
import android.widget.TextView;
import android.widget.Toast;

import androidx.appcompat.app.AppCompatActivity;
import androidx.core.content.ContextCompat;

import java.util.List;

public class HighScoresActivity extends AppCompatActivity {

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_high_scores);

        LinearLayout header = (LinearLayout)findViewById(R.id.LLHeader);
        ImageView logo = (ImageView)findViewById(R.id.IMHighScoresLogo);
        TextView ranking = (TextView)findViewById(R.id.TVHighScoresTitle);

        Display display = getWindowManager().getDefaultDisplay();
        Point size = new Point();
        display.getSize(size);
        final int displayWidth = size.x;
        int displayHeight = size.y;

        LinearLayout.LayoutParams params = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                         ViewGroup.LayoutParams.WRAP_CONTENT);
        header.setLayoutParams(params);
        params = new LinearLayout.LayoutParams(displayWidth-displayWidth*25/100,
                                               displayHeight*10/100);
        params.setMargins(0, 1, 0, 0);
        ranking.setLayoutParams(params);
        ranking.setGravity(Gravity.CENTER_HORIZONTAL);

        TextView position = (TextView)findViewById(R.id.highScoresPosition);
        final RelativeLayout.LayoutParams paramsPosition = (RelativeLayout.LayoutParams)position.getLayoutParams();
        paramsPosition.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        paramsPosition.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        final int positionLeftMargin = displayWidth * 10 / 100;
        final int nameLeftMargin = displayWidth * 30 / 100;
        final int scoreLeftMargin = displayWidth*80/100;
        final int nameAndScoreTopMargin = displayWidth * 2 / 100;
        paramsPosition.setMargins(positionLeftMargin, nameAndScoreTopMargin, 0, 0);
        position.setLayoutParams(paramsPosition);

        TextView name = (TextView)findViewById(R.id.highScoresName);
        final RelativeLayout.LayoutParams paramsName = (RelativeLayout.LayoutParams)name.getLayoutParams();
        paramsName.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        paramsName.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        paramsName.setMargins(nameLeftMargin, nameAndScoreTopMargin, 0, 0);
        name.setLayoutParams(paramsName);

        final TextView score = (TextView)findViewById(R.id.highScoresScore);
        final RelativeLayout.LayoutParams paramsScore = (RelativeLayout.LayoutParams)score.getLayoutParams();
        paramsScore.addRule(RelativeLayout.ALIGN_PARENT_LEFT);
        paramsScore.addRule(RelativeLayout.ALIGN_PARENT_TOP);
        paramsScore.setMargins(scoreLeftMargin, nameAndScoreTopMargin, 0, 0);
        score.setLayoutParams(paramsScore);

        final RelativeLayout scores = (RelativeLayout)findViewById(R.id.RLScores);
        final ScrollView scroll = (ScrollView)findViewById(R.id.scrollView);

        LinearLayout.LayoutParams params2 = new LinearLayout.LayoutParams(ViewGroup.LayoutParams.MATCH_PARENT,
                                                                          ViewGroup.LayoutParams.MATCH_PARENT);
        params2.setMargins(0,nameAndScoreTopMargin,0,0);
        scroll.setLayoutParams(params2);


        String url = "https://tetromino.page.gd/get_top_scores.php";

        HttpBrowser.callUrl(this, url, "GET_TOP_SCORES", new HttpResponseListener() {
            @Override
            public void getCodeResponse(PhpResponse response) {

            }

            @Override
            public void getTopScoresResponse(List<ScoreItem> scoresResponse) {
                if (scoresResponse != null) {
                    int topMargin = 0;
                    for(int i=0; i<scoresResponse.size(); i++) {
                        RelativeLayout.LayoutParams rlpositionparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        RelativeLayout.LayoutParams rlnameparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        RelativeLayout.LayoutParams rlscoreparams = new RelativeLayout.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                                ViewGroup.LayoutParams.WRAP_CONTENT);
                        RelativeLayout.LayoutParams rllineparams = new RelativeLayout.LayoutParams(displayWidth - displayWidth * 10 / 100, 1);

                        rlpositionparams.setMargins(positionLeftMargin, topMargin * 4, 0, 0);
                        rlnameparams.setMargins(nameLeftMargin, topMargin * 4, 0, 0);
                        rlscoreparams.setMargins(scoreLeftMargin, topMargin * 4, 0, 0);

                        TextView tv = new TextView(HighScoresActivity.this);
                        tv.setText("" + (i + 1));
                        tv.setTextSize(12);
                        tv.setTextColor(ContextCompat.getColor(HighScoresActivity.this, R.color.playAreaColor));
                        tv.setLayoutParams(rlpositionparams);
                        scores.addView(tv);

                        tv = new TextView(HighScoresActivity.this);
                        tv.setText(scoresResponse.get(i).getName());
                        tv.setTextSize(12);
                        tv.setTextColor(ContextCompat.getColor(HighScoresActivity.this, R.color.playAreaColor));
                        tv.setLayoutParams(rlnameparams);
                        scores.addView(tv);


                        tv = new TextView(HighScoresActivity.this);
                        tv.setText(String.valueOf(scoresResponse.get(i).getScore()));
                        tv.setTextSize(12);
                        tv.setTextColor(ContextCompat.getColor(HighScoresActivity.this, R.color.playAreaColor));
                        tv.setLayoutParams(rlscoreparams);
                        scores.addView(tv);

                        TextView line = new TextView(HighScoresActivity.this);
                        rllineparams.setMargins(displayWidth * 5 / 100, topMargin * 4 + displayWidth * 7 / 100, 0, 0);
                        line.setLayoutParams(rllineparams);
                        line.setBackgroundColor(ContextCompat.getColor(HighScoresActivity.this, R.color.playAreaColor));
                        scores.addView(line);
                        topMargin += nameAndScoreTopMargin;
                    }
                } else {
                    Toast.makeText(HighScoresActivity.this,R.string.serverConnectionError,Toast.LENGTH_LONG).show();
                }
            }
        });
    }
}
