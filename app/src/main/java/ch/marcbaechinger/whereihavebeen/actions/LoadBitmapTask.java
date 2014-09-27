package ch.marcbaechinger.whereihavebeen.actions;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.AsyncTask;
import android.widget.ImageView;

import java.io.IOException;
import java.io.InputStream;
import java.net.URL;

import ch.marcbaechinger.whereihavebeen.adapter.PersonHolder;

public class LoadBitmapTask extends AsyncTask<String, Void, Bitmap> {

    private PersonHolder personHolder;
    private ImageView view;

    public LoadBitmapTask(PersonHolder personHolder) {
        this.personHolder = personHolder;
    }

    public LoadBitmapTask(ImageView view) {
        this.view = view;
    }

    @Override
    protected Bitmap doInBackground(String... urls) {
        String url = urls[0];
        Bitmap bitmap = null;
        try {
            InputStream is = new URL(url).openStream();
            bitmap = BitmapFactory.decodeStream(is);
        } catch (IOException e) {
            e.printStackTrace();
        }
        return bitmap;
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        if (personHolder != null) {
            personHolder.setImage(bitmap);
        } else {
            view.setImageBitmap(bitmap);
        }
    }
}
