package tetro.puzzle;

import java.util.List;

public interface HttpResponseListener {
    void getCodeResponse(PhpResponse response);
    void getTopScoresResponse(List<ScoreItem> scores);
}
