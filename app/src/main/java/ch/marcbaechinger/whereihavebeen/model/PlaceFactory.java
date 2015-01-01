package ch.marcbaechinger.whereihavebeen.model;

import android.content.Context;
import android.database.Cursor;

import ch.marcbaechinger.whereihavebeen.app.data.DataContract;

public class PlaceFactory {
    public Place createPlace(Cursor cursor, Context context) {
        Place place = new Place();

        int titleIdx = cursor.getColumnIndex(DataContract.PLACE.FIELD_TITLE);
        place.setTitle(cursor.getString(titleIdx));

        int descIdx = cursor.getColumnIndex(DataContract.PLACE.FIELD_DESCRIPTION);
        place.setDescription(cursor.getString(descIdx));

        int idIdx = cursor.getColumnIndex(DataContract.PLACE.FIELD_ID);
        place.setId(cursor.getInt(idIdx));

        int latIdx = cursor.getColumnIndex(DataContract.PLACE.FIELD_LAT);
        if (!cursor.isNull(latIdx)) {
            place.setLat(cursor.getDouble(latIdx));
        }

        int lngIdx = cursor.getColumnIndex(DataContract.PLACE.FIELD_LNG);
        if (!cursor.isNull(lngIdx)) {
            place.setLng(cursor.getDouble(lngIdx));
        }

        int pictureUriIndex = cursor.getColumnIndex(DataContract.PLACE.FIELD_PICTURE);
        place.setPictureUri(cursor.getString(pictureUriIndex));

        Category category = fetchCategory(cursor, context);
        if (category != null) {
            place.setCategory(category);
        }

        return place;
    }


    private Category fetchCategory(Cursor cursor, Context context) {
        int categoryFieldIdx = cursor.getColumnIndex(DataContract.CATEGORY.FIELD_TITLE);
        String title = cursor.getString(categoryFieldIdx);
        return UIModel.instance(context).getCategoryByTitle(title);
    }
}
