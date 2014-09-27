package ch.marcbaechinger.whereihavebeen.adapter;

import android.content.Context;
import android.database.Cursor;
import android.net.Uri;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageView;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.app.data.DataContract;

public class PlaceAdapter extends SimpleCursorAdapter {

    public PlaceAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public View newView(Context context, Cursor cursor, ViewGroup viewGroup) {
        LayoutInflater inflater = LayoutInflater.from(viewGroup.getContext());
        View view = inflater.inflate(R.layout.place_list_item, viewGroup, false);
        return view;
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        TextView title = (TextView) view.findViewById(R.id.placeListViewItemTitle);
        int titleFieldIdx = cursor.getColumnIndex(DataContract.PLACE.FIELD_TITLE);
        title.setText(cursor.getString(titleFieldIdx));

        int pictureUriIndex = cursor.getColumnIndex(DataContract.PLACE.FIELD_PICTURE);
        String pictureUri = cursor.getString(pictureUriIndex);
        if (pictureUri != null) {
            ImageView image = (ImageView) view.findViewById(R.id.placeListViewItemImage);
            image.setImageURI(Uri.parse(pictureUri));
        }
    }
}
