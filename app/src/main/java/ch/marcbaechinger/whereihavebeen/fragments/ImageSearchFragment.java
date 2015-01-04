package ch.marcbaechinger.whereihavebeen.fragments;

import android.app.Fragment;
import android.content.Context;
import android.graphics.drawable.BitmapDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.util.Log;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.InputMethodManager;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.GridView;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.TextView;

import org.json.JSONArray;
import org.json.JSONException;
import org.json.JSONObject;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.app.ImageSearchActivity;
import ch.marcbaechinger.whereihavebeen.bitmap.BitmapStorageHelper;
import ch.marcbaechinger.whereihavebeen.bitmap.OutputMediaFileFactory;
import ch.marcbaechinger.whereihavebeen.bitmap.PlaceTileFilenameGenerator;
import ch.marcbaechinger.whereihavebeen.imagesearch.ImageListAdapter;
import ch.marcbaechinger.whereihavebeen.imagesearch.ImageSearchResult;
import ch.marcbaechinger.whereihavebeen.imagesearch.ImageSearchTask;
import ch.marcbaechinger.whereihavebeen.model.UIModel;

public class ImageSearchFragment extends Fragment {

    private static final String TAG = ImageSearchFragment.class.getSimpleName();

    private String query;
    private int start = 0;
    private int pageSize = 8;
    private ImageButton backButton;
    private ImageButton nextButton;
    private ImageListAdapter imageListAdapter;
    private ImageSearchResult searchResult;
    private TextView pageCounter;
    private EditText queryEdit;
    private ImageButton searchButton;
    private boolean queryVisible = false;
    private GridView grid;
    private BitmapStorageHelper storageHelper;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {

        final View rootView = inflater.inflate(R.layout.fragment_image_search, container, false);
        final UIModel model = UIModel.instance(getActivity());

        grid = (GridView) rootView.findViewById(R.id.grid);
        storageHelper = new BitmapStorageHelper(getActivity(), new OutputMediaFileFactory(getActivity()));

        grid.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                JSONObject image = (JSONObject) parent.getAdapter().getItem(position);

                ImageView imageView = (ImageView) view;
                BitmapDrawable bitmapDrawable = (BitmapDrawable) imageView.getDrawable();

                Uri uri = storageHelper.store(bitmapDrawable.getBitmap(), new PlaceTileFilenameGenerator(model.getSelectedPlace()));
                model.getEditPlace().setPictureUri(uri.toString());

                Log.d(TAG, "stored to " + uri.toString());
                getActivity().finish();
            }
        });

        query = getActivity().getIntent().getStringExtra(ImageSearchActivity.IMAGE_QUERY);

        pageCounter = (TextView)rootView.findViewById(R.id.pageCounter);

        backButton = (ImageButton)rootView.findViewById(R.id.back);
        backButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (start - pageSize > -1) {
                    start -= pageSize;
                }
                search();
            }
        });
        nextButton = (ImageButton)rootView.findViewById(R.id.next);
        nextButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                if (hasNextPage()) {
                    start += pageSize;
                    search();
                }
            }
        });

        searchButton = (ImageButton)rootView.findViewById(R.id.changeQuery);
        searchButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                String query = queryEdit.getText().toString().trim();
                if (queryVisible) {
                    hideQueryEdit();
                    setQuery(query);
                } else {
                    showQueryEdit();
                }
            }
        });

        queryEdit = (EditText)rootView.findViewById(R.id.query);
        queryEdit.setText(query);
        queryEdit.setOnKeyListener(new View.OnKeyListener() {
            @Override
            public boolean onKey(View v, int keyCode, KeyEvent event) {
                if (keyCode == 66) {
                    setQuery(queryEdit.getText().toString().trim());
                }
                return false;
            }
        });

        imageListAdapter = new ImageListAdapter(new JSONArray(), getActivity());
        grid.setAdapter(imageListAdapter);

        search();

        return rootView;
    }

    private void showQueryEdit() {
        queryEdit.setVisibility(View.VISIBLE);
        queryEdit.requestFocus();
        queryEdit.setSelection(queryEdit.length());
        InputMethodManager imm = (InputMethodManager) getActivity().getSystemService(Context.INPUT_METHOD_SERVICE);
        imm.showSoftInput(queryEdit, InputMethodManager.SHOW_IMPLICIT);
        queryVisible = true;
    }

    private void hideQueryEdit() {
        queryEdit.setVisibility(View.GONE);
        queryVisible = false;
    }

    private void setQuery(String newQuery) {
        if (newQuery != null && !newQuery.equals(query)) {
            query = newQuery;
            start = 0;
            search();
        }
    }

    private void search() {
        new ImageSearchTask(new ImageSearchTask.ImageSearchResultListener() {
            @Override
            public void retrieveResult(ImageSearchResult result) throws JSONException {
                JSONArray results = result.getMatches();
                Log.d(TAG, "found: " + results.length());
                searchResult = result;
                imageListAdapter.setImageSearchResult(results);
                nextButton.setEnabled(result.getCurrentPage() < result.getPages().length() - 1);
                backButton.setEnabled(result.getCurrentPage() != 0);
                pageCounter.setText( (searchResult.getCurrentPage() + 1 ) + "/" + searchResult.getMatches().length());
            }
        }).execute(query, String.valueOf(start));
    }

    private boolean hasNextPage() {
        boolean hasNext = false;
        if (searchResult == null) {
            hasNext = true;
        } else if (searchResult.getCurrentPage() + 1 < searchResult.getPages().length()) {
            hasNext = true;
        }
        return hasNext;
    }


}
