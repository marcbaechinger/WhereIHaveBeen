package ch.marcbaechinger.whereihavebeen.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.TextView;

import com.squareup.picasso.Picasso;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.app.EditPlaceActivity;
import ch.marcbaechinger.whereihavebeen.model.Place;
import ch.marcbaechinger.whereihavebeen.model.UIModel;

public class PlaceDetailFragment extends Fragment implements FabClickHandler {

    private static final String TAG = PlaceDetailFragment.class.getSimpleName();
    private ImageView pictureView;
    private FabManager fabManager;
    private TextView location;
    private TextView categoryView;
    private UIModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_detail_place, container, false);

        model = UIModel.instance(getActivity());

        fabManager = new FabManager();
        fabManager.captureButtons(rootView);
        fabManager.setFabListener(new FabManager.FabListener() {
            @Override
            public void onClick(View v, boolean isPrimary) {
                if (isPrimary) {
                    Intent detailIntent = new Intent(getActivity(), EditPlaceActivity.class);
                    model.setEditPlace((Place) model.getSelectedPlace().clone());
                    startActivity(detailIntent);
                    getActivity().overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
                } else {
                    Place placeToDelete = model.getSelectedPlace();
                    model.setSelectedPlace(null);
                    model.deletePlace(placeToDelete.getId());
                    getActivity().finish();
                }
            }
        });
        categoryView = (TextView) rootView.findViewById(R.id.placeDetailCategory);
        location = (TextView) rootView.findViewById(R.id.placeDetailLat);
        pictureView = (ImageView) rootView.findViewById(R.id.placeDetailImageView);
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        syncUI(model.getSelectedPlace());
    }

    private void syncUI(Place place) {
        if (place.getPictureUri() != null) {
            Log.d(TAG, "set image: " + place.getPictureUri());
            Picasso.with(getActivity())
                    .load(place.getPictureUri())
                    .placeholder(R.drawable.image_placeholder)
                    .resize(Utils.getDisplaySize(getActivity()).x, Utils.dpToPx(320, getActivity()))
                    .centerCrop()
                    .into(pictureView);
        }
        if (place.getLat() != null) {
            location.setText(place.getLat().toString() + " / " + place.getLng().toString());
        }

        categoryView.setText(place.getCategory().getTitle());
        GradientDrawable categoryBg = (GradientDrawable) categoryView.getBackground();
        categoryBg.setColor(Color.parseColor(place.getCategory().getColor()));

        getActivity().setTitle(place.getTitle());
    }

    @Override
    public void onStop() {
        Picasso.with(getActivity()).cancelRequest(pictureView);
        super.onStop();
    }
}
