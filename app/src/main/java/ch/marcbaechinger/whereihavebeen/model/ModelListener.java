package ch.marcbaechinger.whereihavebeen.model;

public interface ModelListener {
    void onPlaceDeletion(int id);
    void onCategorySelection(Category category);
}
