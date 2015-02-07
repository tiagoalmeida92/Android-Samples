package pt.tiago.facebookoauth;

import android.app.Activity;
import android.content.Intent;
import android.content.SharedPreferences;
import android.net.Uri;
import android.net.http.AndroidHttpClient;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.webkit.WebSettings;
import android.webkit.WebView;
import android.webkit.WebViewClient;

import org.apache.http.HttpResponse;
import org.apache.http.NameValuePair;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpPost;
import org.apache.http.client.utils.URLEncodedUtils;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.net.URI;
import java.net.URISyntaxException;
import java.nio.charset.Charset;
import java.util.Calendar;
import java.util.List;


public class LogInFacebookActivity extends Activity {

    private static final String REDIRECT_URI = "https://localhost/";
    private static final String CLIENTID = "";
    private static final String CLIENTSECRET = "";
    private static final String AUTHORIZATION_ENDPOINT = "https://www.facebook.com/dialog/oauth?" +
            "client_id=" + CLIENTID +
            "&client_secret=" + CLIENTSECRET +
            "&response_type=code" +
            "&redirect_uri=" + REDIRECT_URI;
    private static final String TOKEN_ENDPOINT = "https://graph.facebook.com/oauth/access_token?" +
            "client_id=" + CLIENTID +
            "&redirect_uri=" + REDIRECT_URI +
            "&client_secret=" + CLIENTSECRET +
            "&code=";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_log_in_facebook);
        WebView webView = (WebView) findViewById(R.id.webview);
        webView.getSettings().setJavaScriptEnabled(true);
        webView.getSettings().setCacheMode(WebSettings.LOAD_CACHE_ELSE_NETWORK);

        webView.setWebViewClient(new WebViewClient() {
            @Override
            public boolean shouldOverrideUrlLoading(WebView view, String url) {
                if (url.startsWith(REDIRECT_URI)) {
                    Uri uri = Uri.parse(url);
                    String authCode = uri.getQueryParameter("code");
                    new TokenTask().execute(authCode);
                    return true;
                }
                return false;
            }

        });
        webView.loadUrl(AUTHORIZATION_ENDPOINT);

    }


    class TokenTask extends AsyncTask<String, Void, String> {

        @Override
        protected String doInBackground(String... params) {
            if (params.length > 0) {
                String authCode = params[0];
                String endpoint = TOKEN_ENDPOINT + authCode;

                HttpClient client = new DefaultHttpClient();
                HttpPost post = new HttpPost(endpoint);
                try {
                    HttpResponse response = client.execute(post);
                    if (response != null) {
                        //If status is OK 200
                        if (response.getStatusLine().getStatusCode() == 200) {
                            String result = EntityUtils.toString(response.getEntity());
                            //Convert the string result to a JSON Object

                            //Extract data from JSON Response

                            String accessToken = extractToken(result);
                            Intent data = new Intent();
                            data.putExtra("token", accessToken);
                            setResult(RESULT_OK, data);
                            finish();
                        }
                    }
                } catch (IOException e) {
                    e.printStackTrace();
                }

            }
            return null;
        }

        private String extractToken(String result) {
            String[] tokens = result.split("&");
            String token = tokens[0];
            return token.substring(token.indexOf('=') + 1);
        }
    }

}
