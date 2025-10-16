package tetro.puzzle;

import android.content.Context;
import android.os.Handler;
import android.os.Looper;
import android.util.Log;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.json.JSONArray;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.List;

/*
Uso esta clase porque el servidor gratuito que tengo necesita javascript, y si lo llamo a las urls directamente con con las librerias de android
no me responde lo que quiero
 */

public class HttpBrowser {

    public static void callUrl(final Context ctx, final String url, final String feature, final HttpResponseListener callback) {
        new Handler(Looper.getMainLooper()).post(() -> {
            WebView webView = new WebView(ctx);
            webView.getSettings().setJavaScriptEnabled(true);
            webView.getSettings().setDomStorageEnabled(true);

            webView.setWebViewClient(new WebViewClient() {
                @Override
                public void onPageFinished(WebView view, String url) {
                    Log.d("BrowserSimulator", "Página cargada: " + url);
                    webView.evaluateJavascript(
                            "(function() { return document.body.innerText; })();",
                            value -> {

                                String clean = value;
                                if (clean != null && clean.length() > 1 && clean.startsWith("\"") && clean.endsWith("\"")) {
                                    clean = clean.substring(1, clean.length() - 1); // quita comillas inicial y final
                                    clean = clean.replace("\\\"", "\""); // desescapa comillas internas
                                }

                                try {

                                    switch (feature) {
                                        case "SAVE_SCORE", "GET_PROFILE": {
                                            JSONObject object = new JSONObject(clean);
                                            PhpResponse response = new PhpResponse();
                                            response.setResponse(object.getString("response"));
                                            Log.d("HttpBrowserResponse", "response: " + response.getResponse());
                                            callback.getCodeResponse(response);
                                            break;
                                        }
                                        case "GET_TOP_SCORES": {
                                            JSONArray array = new JSONArray(clean);
                                            List<ScoreItem> scores = new ArrayList<>();
                                            for (int i = 0; i < array.length(); i++) {
                                                JSONObject obj = array.getJSONObject(i);
                                                ScoreItem item = new ScoreItem();
                                                item.setName(obj.getString("name"));
                                                item.setScore(Integer.parseInt(obj.getString("score"))); // convertir a int
                                                scores.add(item);
                                            }
                                            callback.getTopScoresResponse(scores);
                                            break;
                                        }
                                    }

                                } catch (Exception e) {
                                    switch (feature) {
                                        case "SAVE_SCORE", "GET_PROFILE": {
                                            PhpResponse errorResponse = new PhpResponse();
                                            errorResponse.setResponse("ERROR");
                                            callback.getCodeResponse(errorResponse);
                                        }
                                        case "GET_TOP_SCORES": {
                                            callback.getTopScoresResponse(null);
                                        }
                                    }
                                    e.printStackTrace();
                                }

                            }
                    );
                }
            });

            webView.loadUrl(url);
        });
    }
}

