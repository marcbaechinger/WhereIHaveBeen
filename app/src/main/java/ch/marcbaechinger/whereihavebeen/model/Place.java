package ch.marcbaechinger.whereihavebeen.model;

public class Place {
    private String title;
    private String description;
    private int id;
    private Double lat;
    private Double lng;
    private Category category;
    private String pictureUri;

    public void setLatLng(Double latitude, Double longitude) {
        this.lat = latitude;
        this.lng = longitude;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getTitle() {
        return title;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getDescription() {
        return description;
    }

    public void setId(int id) {
        this.id = id;
    }

    public int getId() {
        return id;
    }

    public void setLat(double lat) {
        this.lat = lat;
    }

    public Double getLat() {
        return lat;
    }

    public void setLng(double lng) {
        this.lng = lng;
    }

    public Double getLng() {
        return lng;
    }

    public void setCategory(Category category) {
        this.category = category;
    }

    public Category getCategory() {
        return category;
    }

    public String getPictureUri() {
        return pictureUri;
    }

    public void setPictureUri(String pictureUri) {
        this.pictureUri = pictureUri;
    }
}
