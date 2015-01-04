package ch.marcbaechinger.whereihavebeen.bitmap;

import android.content.Context;
import android.graphics.Bitmap;
import android.net.Uri;
import android.util.Log;

import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileOutputStream;
import java.io.IOException;

public class BitmapStorageHelper {

    private static final String TAG = BitmapStorageHelper.class.getSimpleName();
    private Context context;
    private OutputMediaFileFactory outputMediaFileFactory;

    public BitmapStorageHelper(Context context, OutputMediaFileFactory outputMediaFileFactory) {
        this.context = context;
        this.outputMediaFileFactory = outputMediaFileFactory;
    }

    public Uri store(Bitmap bitmap, FilenameGenerator filenameGenerator) {
        File pictureFile = outputMediaFileFactory.create(filenameGenerator);
        if (pictureFile == null) {
            Log.d(TAG, "Error creating media file, check storage permissions: ");
            throw new IllegalArgumentException("Error creating media file, check storage permissions for file " + pictureFile.getAbsolutePath());
        }

        try {
            FileOutputStream fos = new FileOutputStream(pictureFile);
            bitmap.compress(Bitmap.CompressFormat.JPEG, 100, fos);
            fos.close();
            return Uri.fromFile(pictureFile);

        } catch (FileNotFoundException e) {
            Log.e(TAG, "File not found: " + e.getMessage(), e);
        } catch (IOException e) {
            Log.e(TAG, "Error accessing file: " + e.getMessage(), e);
        }
        return null;
    }
}
