package ch.marcbaechinger.whereihavebeen.adapter;

import android.graphics.Bitmap;
import android.widget.ImageView;
import android.widget.TextView;

import ch.marcbaechinger.whereihavebeen.actions.LoadBitmapTask;

public class PersonHolder {
    private ImageView image;
    private TextView displayName;
    private String imageUrl;
    private Bitmap bitmap;

    public PersonHolder(ImageView image, TextView displayName) {
        this.image = image;
        this.displayName = displayName;
    }

    public void setImage(Bitmap bitmap) {
        this.bitmap = bitmap;
        this.image.setImageBitmap(bitmap);
    }

    public void setDisplayName(String displayName) {
        this.displayName.setText(displayName);
    }

    public void setImageUrl(String imageUrl) {
        if (this.imageUrl == null || !this.imageUrl.equals(imageUrl)) {
            this.imageUrl = imageUrl;
            this.bitmap = null;
            new LoadBitmapTask(this).execute(this.imageUrl);
        } else if (this.bitmap != null) {
            this.image.setImageBitmap(bitmap);
        }
    }
}
