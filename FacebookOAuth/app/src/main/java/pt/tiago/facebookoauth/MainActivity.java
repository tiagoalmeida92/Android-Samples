package pt.tiago.facebookoauth;

import android.content.Intent;
import android.graphics.drawable.Drawable;
import android.os.AsyncTask;
import android.support.v7.app.ActionBarActivity;
import android.os.Bundle;
import android.view.View;
import android.widget.ImageView;
import android.widget.TextView;

import org.apache.http.HttpResponse;
import org.apache.http.client.HttpClient;
import org.apache.http.client.methods.HttpGet;
import org.apache.http.impl.client.DefaultHttpClient;
import org.apache.http.util.EntityUtils;
import org.json.JSONException;
import org.json.JSONObject;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;


public class MainActivity extends ActionBarActivity {

    private static final String FACEBOOK_PICTURE_URL = "https://graph.facebook.com/v2.2/me?fields=picture&access_token=";

    private String _token;
    private TextView _accessTokenTextView;
    private ImageView _photo;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        View loginFacebook = findViewById(R.id.login_facebook);
        View logoutFacebook = findViewById(R.id.logout_facebook);
        View getPhoto = findViewById(R.id.get_photo);
        _accessTokenTextView = (TextView) findViewById(R.id.access_Token);
        _photo = (ImageView) findViewById(R.id.photo);
        loginFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                Intent intent = new Intent(MainActivity.this, LogInFacebookActivity.class);
                startActivityForResult(intent, 0);
            }
        });
        logoutFacebook.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                _accessTokenTextView.setText("not logged in");
            }
        });
        getPhoto.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                new PhotoAsyncTask().execute(_token);
            }
        });
    }

    @Override
    protected void onActivityResult(int requestCode, int resultCode, Intent data) {
        if(resultCode == RESULT_OK){
            _token = data.getStringExtra("token");
            _accessTokenTextView.setText(_token);
        }
    }

    public static Drawable loadImageFromWebOperations(String url) {
        try {
            InputStream is = (InputStream) new URL(url).getContent();
            Drawable d = Drawable.createFromStream(is, "src name");
            return d;
        } catch (Exception e) {
            return null;
        }
    }

    class PhotoAsyncTask extends AsyncTask<String, Void, Drawable> {

        @Override
        protected Drawable doInBackground(String... params) {
            HttpClient client = new DefaultHttpClient();
            HttpGet get = new HttpGet(FACEBOOK_PICTURE_URL + params[0]);
            try {
                HttpResponse response = client.execute(get);
                String result = EntityUtils.toString(response.getEntity());
                JSONObject object = new JSONObject(result);
                String imageUrl = object.getJSONObject("picture").getJSONObject("data").getString("url");
                Drawable drawable = loadImageFromWebOperations(imageUrl);
                return drawable;
            } catch (IOException | JSONException e) {
                e.printStackTrace();
            }
            return null;
        }

        @Override
        protected void onPostExecute(Drawable drawable) {
            _photo.setImageDrawable(drawable);
        }
    }
}
