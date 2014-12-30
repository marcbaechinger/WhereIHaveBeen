package ch.marcbaechinger.whereihavebeen.fragments;

import android.app.Fragment;
import android.location.Location;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.TextView;

import com.google.android.gms.maps.CameraUpdateFactory;
import com.google.android.gms.maps.GoogleMap;
import com.google.android.gms.maps.MapFragment;
import com.google.android.gms.maps.OnMapReadyCallback;
import com.google.android.gms.maps.model.LatLng;
import com.google.android.gms.maps.model.Marker;
import com.google.android.gms.maps.model.MarkerOptions;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.app.UIModel;
import ch.marcbaechinger.whereihavebeen.location.LocationRetriever;
import ch.marcbaechinger.whereihavebeen.location.SimpleLocationListener;
import ch.marcbaechinger.whereihavebeen.model.Place;

public class GMapFragment extends Fragment implements OnMapReadyCallback, SimpleLocationListener {

    private static final float DEFAULT_ZOOM_LEVEL = 15;
    private GoogleMap map;
    private Marker marker;
    private LatLng selectedPosition;
    private TextView latLabel;
    private TextView lngLabel;
    private UIModel model;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_map, container, false);

        model = UIModel.instance(getActivity());

        MapFragment mapFragment = (MapFragment)getChildFragmentManager().findFragmentById(R.id.map);
        mapFragment.getMapAsync(this);

        ImageButton ok = (ImageButton) rootView.findViewById(R.id.fab);
        ok.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                model.getEditPlace().setLatLng(selectedPosition.latitude, selectedPosition.longitude);
                getActivity().finish();
            }
        });
        ImageButton cancel = (ImageButton) rootView.findViewById(R.id.fabCancel);
        cancel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                getActivity().finish();
            }
        });

        latLabel = (TextView)rootView.findViewById(R.id.latitudeLabel);
        lngLabel = (TextView)rootView.findViewById(R.id.longitudeLabel);

        return rootView;
    }

    @Override
    public void onMapReady(GoogleMap map) {
        initMap(map);
    }

    private void initMap(GoogleMap map) {
        this.map = map;
        Place editPlace = model.getEditPlace();
        if (editPlace.getLat() == null) {
            LocationRetriever locationRetriever = new LocationRetriever(getActivity());
            locationRetriever.getCurrentLocation(this);
        } else {
            updateMapLocation(editPlace.getLat(), editPlace.getLng());
        }
    }


    @Override
    public void locationRetrieved(Location location) {
        updateMapLocation(location.getLatitude(), location.getLongitude());
    }

    private void updateMapLocation(double latitude, double longitude) {
        LatLng latLng = new LatLng(latitude, longitude);
        if (map != null) {
            map.moveCamera(CameraUpdateFactory.newLatLngZoom(latLng, DEFAULT_ZOOM_LEVEL));
            if (marker != null) {
                marker.setPosition(latLng);
            } else {
                initMarker(latLng);
            }
        }
        latLabel.setText("" + latLng.latitude);
        lngLabel.setText("" + latLng.longitude);
    }

    private void initMarker(LatLng latLng) {
        selectedPosition = latLng;
        this.marker = this.map.addMarker(new MarkerOptions()
                .position(latLng)
                .title(getActivity().getResources().getString(R.string.current_location))
                .draggable(true));

        this.map.setOnMarkerDragListener(new GoogleMap.OnMarkerDragListener() {
            @Override
            public void onMarkerDragStart(Marker marker) {}
            @Override
            public void onMarkerDrag(Marker marker) {
                latLabel.setText("" + marker.getPosition().latitude);
                lngLabel.setText("" + marker.getPosition().longitude);
            }
            @Override
            public void onMarkerDragEnd(Marker marker) {
                selectedPosition = marker.getPosition();
                latLabel.setText("" + selectedPosition.latitude);
                lngLabel.setText("" + selectedPosition.longitude);
            }
        });
    }
}
