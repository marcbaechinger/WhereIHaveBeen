package ch.marcbaechinger.whereihavebeen.imagesearch;

import android.app.Activity;
import android.net.Uri;
import android.view.View;
import android.view.ViewGroup;
import android.widget.BaseAdapter;
import android.widget.ImageView;
import android.widget.LinearLayout;

import com.squareup.picasso.Picasso;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import ch.marcbaechinger.whereihavebeen.fragments.UIUtils;

/**
 * Created by bachinger on 1/2/15.
 */
public class ImageListAdapter extends BaseAdapter {

    private static final String TAG = ImageListAdapter.class.getSimpleName();

    private JSONArray imageSearchResult;
    private Activity activity;

    public ImageListAdapter(JSONArray imageSearchResult, Activity activity) {
        this.imageSearchResult = imageSearchResult;
        this.activity = activity;
    }

    @Override
    public int getCount() {
        return imageSearchResult.length();
    }

    @Override
    public Object getItem(int position) {
        try {
            return imageSearchResult.getJSONObject(position);
        } catch (JSONException e) {
            throw new IllegalStateException(e);
        }
    }

    @Override
    public long getItemId(int position) {
        return 0;
    }

    @Override
    public View getView(int position, View convertView, ViewGroup parent) {
        ImageView imageView = null;
        int width = UIUtils.getScreenWidth(activity) / 2;
        if (convertView == null) {
            imageView = new ImageView(activity);
            imageView.setLayoutParams(new LinearLayout.LayoutParams(
                    LinearLayout.LayoutParams.MATCH_PARENT,
                    LinearLayout.LayoutParams.WRAP_CONTENT));
        } else {
            imageView = (ImageView) convertView;
        }

        try {
            JSONObject image = imageSearchResult.getJSONObject(position);
            Picasso.with(activity)
                    .load(Uri.parse(image.getString("url")))
                    .resize(width, width)
                    .centerCrop()
                    .into(imageView);

        } catch (JSONException e) {
            e.printStackTrace();
        }

        return imageView;
    }
}
