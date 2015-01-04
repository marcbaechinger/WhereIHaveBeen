package ch.marcbaechinger.whereihavebeen.bitmap;

import ch.marcbaechinger.whereihavebeen.model.Place;

public class PlaceTileFilenameGenerator implements FilenameGenerator {

    private Place place;

    public PlaceTileFilenameGenerator(Place place) {
        this.place = place;
    }

    @Override
    public String getFilename() {
        return "place_tile_" + place.getId() + ".jpg";
    }
}
