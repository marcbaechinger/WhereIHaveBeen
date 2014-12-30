package ch.marcbaechinger.whereihavebeen.fragments;

import android.app.Fragment;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.app.UIModel;
import ch.marcbaechinger.whereihavebeen.model.Place;

public class PlaceDetailFragment extends Fragment {

    private ImageView pictureView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail_place, container, false);

        Place place = UIModel.instance(getActivity()).getSelectedPlace();

        getActivity().setTitle(place.getTitle());
        TextView categoryView = (TextView) rootView.findViewById(R.id.placeDetailCategory);
        categoryView.setText(place.getCategory().getTitle());
        GradientDrawable categoryBg = (GradientDrawable) categoryView.getBackground();
        categoryBg.setColor(Color.parseColor(place.getCategory().getColor()));

        TextView lat = (TextView) rootView.findViewById(R.id.placeDetailLat);
        if (place.getLat() != null) {
            lat.setText(place.getLat().toString());
        }
        TextView lng = (TextView) rootView.findViewById(R.id.placeDetailLng);
        if (place.getLng() != null) {
            lng.setText(place.getLng().toString());
        }

        pictureView = (ImageView) rootView.findViewById(R.id.placeDetailImageView);
        if (place.getPictureUri() != null) {
            Picasso.with(getActivity())
                .load(place.getPictureUri())
                .resize(Utils.getDisplaySize(getActivity()).x, Utils.dpToPx(320, getActivity()))
                .centerCrop()
                .into(pictureView);
        }
        return rootView;
    }

    @Override
    public void onStop() {
        Picasso.with(getActivity()).cancelRequest(pictureView);
        super.onStop();
    }
}
