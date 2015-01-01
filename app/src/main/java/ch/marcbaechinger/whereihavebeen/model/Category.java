package ch.marcbaechinger.whereihavebeen.model;

public class Category {
    private String title;
    private String color;
    private int id;

    public Category() {
    }

    public Category(int id, String categoryTitle, String categoryColor) {
        title = categoryTitle;
        color = categoryColor;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getColor() {
        return color;
    }

    public void setColor(String color) {
        this.color = color;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    @Override
    public String toString() {
        return title;
    }
}
