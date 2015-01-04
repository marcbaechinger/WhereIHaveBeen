package ch.marcbaechinger.whereihavebeen.bitmap;

public class PlaceTileFilenameGenerator implements FilenameGenerator {

    public PlaceTileFilenameGenerator() {
    }

    @Override
    public String getFilename() {
        return "place_tile_" + System.currentTimeMillis() + ".jpg";
    }
}
