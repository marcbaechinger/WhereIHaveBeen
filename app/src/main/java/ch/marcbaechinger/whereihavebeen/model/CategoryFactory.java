package ch.marcbaechinger.whereihavebeen.model;

import android.database.Cursor;

import ch.marcbaechinger.whereihavebeen.app.data.DataContract;

public class CategoryFactory {
    public Category createCategory(Cursor cursor) {

        Category category = new Category();

        int titleIdx = cursor.getColumnIndex(DataContract.CATEGORY.FIELD_TITLE);
        int colorIdx = cursor.getColumnIndex(DataContract.CATEGORY.FIELD_COLOR);
        int idIdx = cursor.getColumnIndex(DataContract.CATEGORY.FIELD_ID);

        String title = cursor.getString(titleIdx);
        category.setTitle(title);

        int id = cursor.getInt(idIdx);
        category.setId(id);

        String color = cursor.getString(colorIdx);
        category.setColor(color);

        return category;
    }
}
