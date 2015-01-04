package ch.marcbaechinger.whereihavebeen.bitmap;

import android.content.Context;
import android.os.Environment;

import java.io.File;

public class OutputMediaFileFactory {

    private Context context;

    public OutputMediaFileFactory(Context context) {
        this.context = context;
    }

    public File create(FilenameGenerator filenameGenerator){
        // To be safe, you should check that the SDCard is mounted
        // using Environment.getExternalStorageState() before doing this.
        File mediaStorageDir = new File(Environment.getExternalStorageDirectory()
                + "/Android/data/"
                + context.getPackageName()
                + "/Files");

        // This location works best if you want the created images to be shared
        // between applications and persist after your app has been uninstalled.

        // Create the storage directory if it does not exist
        if (! mediaStorageDir.exists()){
            if (! mediaStorageDir.mkdirs()){
                return null;
            }
        }
        // Create a media file name
        return new File(mediaStorageDir.getPath() + File.separator + filenameGenerator.getFilename());
    }
}
