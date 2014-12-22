package ch.marcbaechinger.whereihavebeen.adapter;

import android.content.Context;
import android.database.Cursor;
import android.view.View;
import android.widget.SimpleCursorAdapter;
import android.widget.TextView;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.app.data.DataContract;

public class CategoryAdapter extends SimpleCursorAdapter {

    public CategoryAdapter(Context context, int layout, Cursor c, String[] from, int[] to, int flags) {
        super(context, layout, c, from, to, flags);
    }

    @Override
    public void bindView(View view, Context context, Cursor cursor) {
        int columnIndex = cursor.getColumnIndex(DataContract.CATEGORY.FIELD_TITLE);
        String title = cursor.getString(columnIndex);

        ((TextView)view.findViewById(R.id.textLabel)).setText(title);
    }
}
