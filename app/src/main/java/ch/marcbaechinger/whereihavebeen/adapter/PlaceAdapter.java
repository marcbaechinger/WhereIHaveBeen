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

import java.util.HashMap;
import java.util.Map;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.actions.BitmapCropAction;
import ch.marcbaechinger.whereihavebeen.app.data.DataContract;
import ch.marcbaechinger.whereihavebeen.model.Category;
import ch.marcbaechinger.whereihavebeen.model.Place;

public class PlaceAdapter extends SimpleCursorAdapter {

    public static final Map<String, Category> CATEGORY_MAP = new HashMap<>();

    public PlaceAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.place_list_item, null, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        Place place = createPlace(cursor);
        view.setTag(R.id.placeTag, place);

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

    private Place createPlace(Cursor cursor) {
        Place place = new Place();

        int titleIdx = cursor.getColumnIndex(DataContract.PLACE.FIELD_TITLE);
        place.setTitle(cursor.getString(titleIdx));

        int descIdx = cursor.getColumnIndex(DataContract.PLACE.FIELD_DESCRIPTION);
        place.setDescription(cursor.getString(descIdx));

        int idIdx = cursor.getColumnIndex(DataContract.PLACE.FIELD_ID);
        place.setId(cursor.getInt(idIdx));

        int latIdx = cursor.getColumnIndex(DataContract.PLACE.FIELD_LAT);
        place.setLat(cursor.getDouble(latIdx));

        int lngIdx = cursor.getColumnIndex(DataContract.PLACE.FIELD_LNG);
        place.setLng(cursor.getDouble(lngIdx));

        int pictureUriIndex = cursor.getColumnIndex(DataContract.PLACE.FIELD_PICTURE);
        place.setPictureUri(cursor.getString(pictureUriIndex));

        Category category = fetchCategory(cursor);
        if (category != null) {
            place.setCategory(category);
        }

        return place;
    }


    private Category fetchCategory(Cursor cursor) {
        Category category = null;
        int categoryFieldIdx = cursor.getColumnIndex(DataContract.CATEGORY.FIELD_TITLE);
        String title = cursor.getString(categoryFieldIdx);
        if (!CATEGORY_MAP.containsKey(title)) {
            int categoryColorFieldIdx = cursor.getColumnIndex(DataContract.CATEGORY.FIELD_COLOR);
            CATEGORY_MAP.put(title, new Category(title, cursor.getString(categoryColorFieldIdx)));
        }
        return CATEGORY_MAP.get(title);
    }
}
