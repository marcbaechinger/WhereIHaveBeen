package ch.marcbaechinger.whereihavebeen.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.adapter.PlaceAdapter;
import ch.marcbaechinger.whereihavebeen.app.data.DataContract;
import ch.marcbaechinger.whereihavebeen.model.Category;

public class PlaceDetailFragment extends Fragment {

    private ImageView pictureView;

    public PlaceDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_place, container, false);

        long id = getActivity().getIntent().getLongExtra(Intent.EXTRA_UID, -1);
        String categoryTitle = getActivity().getIntent().getStringExtra("category");
        Category category = PlaceAdapter.CATEGORY_MAP.get(categoryTitle);

        Uri uri = DataContract.PLACE.buildUriWithId(id);
        Cursor cursor = getActivity().getContentResolver().query(uri, new String[]{
                DataContract.PLACE.FIELD_TITLE,
                DataContract.PLACE.FIELD_DESCRIPTION,
                DataContract.PLACE.FIELD_LAT,
                DataContract.PLACE.FIELD_LNG,
                DataContract.PLACE.FIELD_PICTURE

        }, null, null, null);

        if (cursor.moveToFirst()) {
            getActivity().setTitle(cursor.getString(cursor.getColumnIndex(DataContract.PLACE.FIELD_TITLE)));
            TextView categoryView = (TextView) rootView.findViewById(R.id.placeDetailCategory);
            categoryView.setText(category.getTitle());
            GradientDrawable categoryBg = (GradientDrawable) categoryView.getBackground();
            categoryBg.setColor(Color.parseColor(category.getColor()));

            TextView lat = (TextView) rootView.findViewById(R.id.placeDetailLat);
            lat.setText(cursor.getString(cursor.getColumnIndex(DataContract.PLACE.FIELD_LAT)));

            TextView lng = (TextView) rootView.findViewById(R.id.placeDetailLng);
            lng.setText(cursor.getString(cursor.getColumnIndex(DataContract.PLACE.FIELD_LNG)));

            pictureView = (ImageView) rootView.findViewById(R.id.placeDetailImageView);
            String imageUriString = cursor.getString(cursor.getColumnIndex(DataContract.PLACE.FIELD_PICTURE));
            if (imageUriString != null) {
                Picasso.with(getActivity())
                    .load(imageUriString)
                    .resize(Utils.getDisplaySize(getActivity()).x, Utils.dpToPx(320, getActivity()))
                    .centerCrop()
                    .into(pictureView);
//                pictureView.setImageURI(Uri.parse(imageUriString));
            }
        }
        return rootView;
    }

    @Override
    public void onStop() {
        Picasso.with(getActivity()).cancelRequest(pictureView);
        super.onStop();
    }
}
