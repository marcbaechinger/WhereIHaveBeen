package ch.marcbaechinger.whereihavebeen.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.database.Cursor;
import android.net.Uri;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.app.data.DataContract;

public class PlaceDetailFragment extends Fragment {

    public PlaceDetailFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_detail_place, container, false);

        long id = getActivity().getIntent().getLongExtra(Intent.EXTRA_UID, -1);

        rootView.findViewById(R.id.placeDetailDescription).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                Utils.showToast(getActivity(), "sadasdsa");
            }
        });

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
            TextView description = (TextView) rootView.findViewById(R.id.placeDetailDescription);
            description.setText(cursor.getString(cursor.getColumnIndex(DataContract.PLACE.FIELD_DESCRIPTION)));

            TextView lat = (TextView) rootView.findViewById(R.id.placeDetailLat);
            lat.setText(cursor.getString(cursor.getColumnIndex(DataContract.PLACE.FIELD_LAT)));

            TextView lng = (TextView) rootView.findViewById(R.id.placeDetailLng);
            lng.setText(cursor.getString(cursor.getColumnIndex(DataContract.PLACE.FIELD_LNG)));

            ImageView pictureView = (ImageView) rootView.findViewById(R.id.placeDetailImageView);
            String imageUriString = cursor.getString(cursor.getColumnIndex(DataContract.PLACE.FIELD_PICTURE));
            if (imageUriString != null) {
                pictureView.setImageURI(Uri.parse(imageUriString));
            }
        }
        return rootView;
    }
}
