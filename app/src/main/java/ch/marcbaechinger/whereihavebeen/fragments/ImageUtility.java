package ch.marcbaechinger.whereihavebeen.fragments;

import android.content.Context;
import android.content.Intent;
import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.net.Uri;
import android.os.Environment;
import android.os.ParcelFileDescriptor;
import android.provider.MediaStore;
import android.widget.ImageView;

import java.io.File;
import java.io.FileDescriptor;
import java.io.FileOutputStream;
import java.io.IOException;
import java.text.SimpleDateFormat;
import java.util.Date;

public class ImageUtility {
    private static final String TAG = ImageUtility.class.getName();

    public static File createImageFile() throws IOException {
        String timeStamp = new SimpleDateFormat("yyyyMMdd_HHmmss").format(new Date());
        String imageFileName = "picture_" + timeStamp + "_";
        File storageDir = Environment.getExternalStoragePublicDirectory(Environment.DIRECTORY_PICTURES);
        File image = File.createTempFile(imageFileName, ".jpg", storageDir);
        return image;
    }
    
    public static void setBitmapFromUri(Uri uri, ImageView imageView, Context context) throws IOException {
        ParcelFileDescriptor parcelFileDescriptor =
                context.getContentResolver().openFileDescriptor(uri, "r");
        FileDescriptor fileDescriptor = parcelFileDescriptor.getFileDescriptor();
        Bitmap image = BitmapFactory.decodeFileDescriptor(fileDescriptor);
        parcelFileDescriptor.close();
        imageView.setImageBitmap(image);
    }

    public static final void writeToFile(Bitmap bitmap, String filename) {
        FileOutputStream out = null;
        try {
            out = new FileOutputStream(filename);
            bitmap.compress(Bitmap.CompressFormat.PNG, 90, out);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            try {
                if (out != null) {
                    out.close();
                }
            } catch (IOException e) { e.printStackTrace(); }
        }
    }

    public static boolean isCameraIntent(Intent intent) {
        boolean isCamera = true;
        if(intent != null) {
            final String action = intent.getAction();
            if(action == null) {
                isCamera = false;
            } else {
                isCamera = action.equals(MediaStore.ACTION_IMAGE_CAPTURE);
            }
        }
        return isCamera;
    }

    public static Intent getGalleryIntent() {
        final Intent galleryIntent = new Intent(Intent.ACTION_OPEN_DOCUMENT);
        galleryIntent.setType("image/*");
        galleryIntent.addCategory(Intent.CATEGORY_OPENABLE);
        return  galleryIntent;
    }

}
