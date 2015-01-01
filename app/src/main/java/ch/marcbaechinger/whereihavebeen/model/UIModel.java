package ch.marcbaechinger.whereihavebeen.model;

import android.content.Context;
import android.database.Cursor;

import java.util.ArrayList;
import java.util.Collection;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import ch.marcbaechinger.whereihavebeen.app.data.DataContract;
import ch.marcbaechinger.whereihavebeen.app.data.DatabaseHelper;

public class UIModel {

    private static UIModel INSTANCE;
    private List<ModelListener> modelListeners;

    public static UIModel instance(Context context) {
        if (INSTANCE == null) {
            INSTANCE = new UIModel(context);
        }
        return INSTANCE;
    }

    private Context context;
    private CategoryFactory categoryFactory;

    private Map<String, Category> categoryMap = new HashMap<>();
    Category selectedCategory;

    Place selectedPlace;
    Place editPlace;

    private UIModel(Context context) {
        this(context, new CategoryFactory());
    }

    protected UIModel(Context context, CategoryFactory categoryFactory) {
        this.context = context;
        this.categoryFactory = categoryFactory;

        this.modelListeners = new ArrayList<>();

        loadCategories();
    }

    public void addDeletionListener(ModelListener listener) {
        modelListeners.add(listener);
    }

    public void removeDeletionListener(ModelListener listener) {
        modelListeners.remove(listener);
    }

    public Category getSelectedCategory() {
        return selectedCategory;
    }

    public void setSelectedCategory(Category selectedCategory) {
        this.selectedCategory = selectedCategory;
        notifyCategorySelection(selectedCategory);
    }

    private void notifyCategorySelection(Category selectedCategory) {
        for (ModelListener listener: modelListeners) {
            listener.onCategorySelection(selectedCategory);
        }
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
        if (editPlace == null) {
            editPlace = new Place();
        }
        return editPlace;
    }

    public void setEditPlace(Place editPlace) {
        this.editPlace = editPlace;
    }

    public void commitPlaceEdit() {
        if (selectedPlace != null && editPlace != null) {
            selectedPlace.setTitle(editPlace.getTitle());
            selectedPlace.setCategory(editPlace.getCategory());
            selectedPlace.setPictureUri(editPlace.getPictureUri());
            selectedPlace.setDescription(editPlace.getDescription());
            selectedPlace.setLat(editPlace.getLat());
            selectedPlace.setLng(editPlace.getLng());
            editPlace = null;
        }
    }

    public void deletePlace(int id) {
        context.getContentResolver().delete(
            DataContract.PLACE.CONTENT_URI,
            DataContract.PLACE.FIELD_ID + " = ?",
            new String[] { String.valueOf(id) }
        );
        notifyPlaceDeletion(id);
    }

    public void queryPlaces(PlaceIterator qi, Category selectedCategory) {
        String[] selectionArgs = null;
        if (selectedCategory != null) {
            selectionArgs = new String[]{selectedCategory.getTitle()};
        }

        Cursor cursor = null;
        try {
            cursor = context.getContentResolver().query(DataContract.PLACE.CONTENT_URI, DataContract.PLACE.VIEW_PROJECTION, null, selectionArgs, null);
            PlaceFactory factory = new PlaceFactory();
            while (cursor.moveToNext()) {
                qi.next(factory.createPlace(cursor, context));
            }
        } finally {
            qi.ended();
            cursor.close();
        }
    }

    private void notifyPlaceDeletion(int id) {
        for (ModelListener listener : modelListeners) {
            listener.onPlaceDeletion(id);
        }
    }


    private void loadCategories() {
        DatabaseHelper dbHelper = new DatabaseHelper(context);
        try (Cursor cursor = dbHelper.getWritableDatabase().query(DataContract.CATEGORY.TABLE, null, null, null, null, null, null)) {
            if (cursor.moveToFirst()) {
                Category category = categoryFactory.createCategory(cursor);

                categoryMap.put(category.getTitle(), category);
                while (cursor.moveToNext()) {
                    category = categoryFactory.createCategory(cursor);
                    categoryMap.put(category.getTitle(), category);
                }
            }
        }
    }

    public static abstract class PlaceIterator {
        public abstract void next(Place place);
        public void ended() {};
    }
}
