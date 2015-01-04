package ch.marcbaechinger.whereihavebeen.adapter;

import android.content.Context;
import android.database.Cursor;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.actions.BitmapCropAction;
import ch.marcbaechinger.whereihavebeen.app.data.DataContract;
import ch.marcbaechinger.whereihavebeen.model.Place;
import ch.marcbaechinger.whereihavebeen.model.PlaceFactory;

public class PlaceAdapter extends SimpleCursorAdapter {

    private Context context;
    private PlaceFactory placeFactory;

    public PlaceAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
        this.context = context;
        this.placeFactory = new PlaceFactory();
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.place_list_item, null, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Place place = (Place) view.getTag(R.id.placeTag);
        int idIdx = cursor.getColumnIndex(DataContract.PLACE.FIELD_ID);

        if (place == null || cursor.getInt(idIdx) != place.getId()) {
            place = placeFactory.createPlace(cursor, context);
            view.setTag(R.id.placeTag, place);
        }

        if (place.getLat() == null) {
            view.findViewById(R.id.placeIcon).setVisibility(View.GONE);
        } else {
            view.findViewById(R.id.placeIcon).setVisibility(View.VISIBLE);
        }
        TextView title = (TextView) view.findViewById(R.id.placeListViewItemTitle);
        title.setText(place.getTitle());

        TextView categoryView = (TextView) view.findViewById(R.id.placeListViewItemCategory);

        GradientDrawable background = (GradientDrawable) categoryView.getBackground();
        background.setColor(Color.parseColor(place.getCategory().getColor()));
        categoryView.setText(place.getCategory().getTitle());

        if (place.getPictureUri() != null) {
            ImageView image = (ImageView) view.findViewById(R.id.placeListViewItemImage);
            new BitmapCropAction(image, context).execute(place.getPictureUri());
        }
    }
}
