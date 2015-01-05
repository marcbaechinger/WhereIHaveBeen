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

public class ImageSearchTask extends AsyncTask<String, Void, ImageSearchResult> {

    private static final String TAG = ImageSearchTask.class.getSimpleName();
    private ImageSearchResultListener listener;

    public ImageSearchTask(ImageSearchResultListener listener) {
        this.listener = listener;
    }

    @Override
    protected ImageSearchResult doInBackground(String... params) {
        URL url = null;
        try {
            String query = URLEncoder.encode(params[0], "UTF-8");
            if (query == null || query.trim().length() == 0) {
                query = URLEncoder.encode("beautiful pictures", "UTF-8");
            }
            StringBuilder buf = new StringBuilder("https://ajax.googleapis.com/ajax/services/search/images?");
            buf.append("v=1.0&rsz=8&imgtype=photo")
               .append("&q=").append(query)
               .append("&userip=").append(InetAddress.getLocalHost());

            if (params.length > 1) {
                buf.append("&start=").append(params[1]);
            }
            Log.d(TAG, "using google image search with" + buf.toString());
            url = new URL(buf.toString());

            URLConnection connection = url.openConnection();
            connection.addRequestProperty("Referer", "localhost:8080");

            String line;
            StringBuilder builder = new StringBuilder();
            BufferedReader reader = new BufferedReader(new InputStreamReader(connection.getInputStream()));
            while((line = reader.readLine()) != null) {
                builder.append(line);
            }
            JSONObject root = new JSONObject(builder.toString());
            Log.d(TAG, root.toString(4));
            return new ImageSearchResult(
                root.getJSONObject("responseData").getJSONArray("results"),
                root.getJSONObject("responseData").getJSONObject("cursor").getJSONArray("pages"),
                root.getJSONObject("responseData").getJSONObject("cursor").getInt("currentPageIndex")
            );

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
    protected void onPostExecute(ImageSearchResult res) {
        try {
            listener.retrieveResult(res);
        } catch (JSONException e) {
            Log.e(TAG, e.getMessage(), e);
        }
    }

    public interface ImageSearchResultListener {
        void retrieveResult(ImageSearchResult result) throws JSONException;
    }
}
