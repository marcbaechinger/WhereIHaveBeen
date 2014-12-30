package ch.marcbaechinger.whereihavebeen.app;

import android.content.Context;
import android.database.Cursor;

import java.util.Collection;
import java.util.HashMap;
import java.util.Map;

import ch.marcbaechinger.whereihavebeen.app.data.DataContract;
import ch.marcbaechinger.whereihavebeen.app.data.DatabaseHelper;
import ch.marcbaechinger.whereihavebeen.model.Category;
import ch.marcbaechinger.whereihavebeen.model.Place;

/**
 * Created by bachinger on 12/27/14.
 */
public class UIModel {

    private static UIModel INSTANCE;
    private Context context;
    private Map<String, Category> categoryMap = new HashMap<>();

    public static final UIModel instance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new UIModel(context);
        }
        return INSTANCE;
    }

    private UIModel(Context context) {
        this.context = context;
        loadCategories();
    }

    Category selectedCategory;
    Place selectedPlace;
    Place editPlace;

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
    }
    public void setSelectedCategoryByTitle(String title) {
        Category category = categoryMap.get(title);
        if (category != null) {
            setSelectedCategory(category);
        }
    }

    public Collection<Category> getCategories() {
        return categoryMap.values();
    }


    private void loadCategories() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        Cursor cursor = dbHelper.getWritableDatabase().query(DataContract.CATEGORY.TABLE, null, null, null, null, null, null);

        try {
            if (cursor.moveToFirst()) {
                Category category = createCategory(cursor);

                categoryMap.put(category.getTitle(), category);
                while (cursor.moveToNext()) {
                    category = createCategory(cursor);
                    categoryMap.put(category.getTitle(), category);
                }
            }
        } finally {
            cursor.close();
        }
    }


    private Category createCategory(Cursor cursor) {

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

    public Category getCategoryByTitle(CharSequence text) {
        return categoryMap.get(text.toString());
    }

    public Place getSelectedPlace() {
        return selectedPlace;
    }

    public void setSelectedPlace(Place selectedPlace) {
        this.selectedPlace = selectedPlace;
    }

    public Place getEditPlace() {
        return editPlace;
    }

    public void setEditPlace(Place editPlace) {
        this.editPlace = editPlace;
    }
}
