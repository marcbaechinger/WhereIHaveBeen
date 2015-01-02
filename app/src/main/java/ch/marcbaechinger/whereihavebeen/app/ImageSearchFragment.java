package ch.marcbaechinger.whereihavebeen.app;

import android.app.Fragment;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.GridView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.imagesearch.ImageListAdapter;
import ch.marcbaechinger.whereihavebeen.imagesearch.ImageSearchTask;

public class ImageSearchFragment extends Fragment {

    private static final String TAG = ImageSearchFragment.class.getSimpleName();

    private String query;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_image_search, container, false);

        final GridView grid = (GridView) rootView.findViewById(R.id.grid);
        query = getActivity().getIntent().getStringExtra(ImageSearchActivity.IMAGE_QUERY);

        new ImageSearchTask(new ImageSearchTask.ImageSearchResultListener() {
            @Override
            public void retrieveResult(JSONObject result) throws JSONException {
                JSONArray results = result.getJSONObject("responseData").getJSONArray("results");
                Log.d(TAG, "found: " + result.length());
                grid.setAdapter(new ImageListAdapter(results, getActivity()));
            }
        }).execute(query);

        return rootView;
    }
}
