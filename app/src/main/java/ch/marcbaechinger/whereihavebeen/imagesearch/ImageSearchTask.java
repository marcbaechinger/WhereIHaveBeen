package ch.marcbaechinger.whereihavebeen.imagesearch;

import android.os.AsyncTask;
import android.util.Log;

import org.json.JSONException;
import org.json.JSONObject;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStreamReader;
import java.net.InetAddress;
import java.net.MalformedURLException;
import java.net.URL;
import java.net.URLConnection;
import java.net.URLEncoder;

public class ImageSearchTask extends AsyncTask<String, Void, JSONObject> {

    private static final String TAG = ImageSearchTask.class.getSimpleName();
    private ImageSearchResultListener listener;

    public ImageSearchTask(ImageSearchResultListener listener) {
        this.listener = listener;
    }

    @Override
    protected JSONObject doInBackground(String... params) {
        URL url = null;
        try {
            String query = URLEncoder.encode(params[0], "UTF-8");

            url = new URL("https://ajax.googleapis.com/ajax/services/search/images?" +
                    "v=1.0&rsz=8&q=" + query + "&userip=" + InetAddress.getLocalHost());

            URLConnection connection = url.openConnection();
            connection.addRequestProperty("Referer", "localhost:8080");

            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }

            return new JSONObject(builder.toString());

        } catch (MalformedURLException e) {
            e.printStackTrace();
        } catch (JSONException e) {
            e.printStackTrace();
        } catch (IOException e) {
            e.printStackTrace();
        }

        return null;
    }

    @Override
    protected void onPostExecute(JSONObject res) {
        try {
            listener.retrieveResult(res);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public interface ImageSearchResultListener {
        void retrieveResult(JSONObject result) throws JSONException;
    }
}
