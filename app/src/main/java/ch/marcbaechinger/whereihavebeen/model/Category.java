package ch.marcbaechinger.whereihavebeen.model;

public class Category {
    private String title;
    private String color;

    public Category() {
    }

    public Category(String categoryTitle, String categoryColor) {
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
}
