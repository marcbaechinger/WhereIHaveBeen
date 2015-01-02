package ch.marcbaechinger.whereihavebeen.actions;

import android.content.Context;
import android.graphics.Bitmap;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PorterDuff;
import android.graphics.PorterDuffXfermode;
import android.graphics.Rect;
import android.os.AsyncTask;
import android.widget.ImageView;

import com.squareup.picasso.Picasso;

import java.io.IOException;
import java.util.HashMap;
import java.util.Map;

public class BitmapCropAction extends AsyncTask<String, Void, Bitmap> {


    private static final Map<String, Bitmap> CACHE = new HashMap<>();

    private ImageView imageView;
    private Context context;

    public BitmapCropAction(ImageView imageView, Context context) {
        this.imageView = imageView;
        this.context = context;
    }

    @Override
    protected Bitmap doInBackground(String... uris) {
        if (!CACHE.containsKey(uris[0])) {
            try {
                Bitmap bitmap = Picasso.with(context)
                        .load(uris[0])
                        .resize(120, 120)
                        .centerCrop()
                        .get();
                CACHE.put(uris[0], getCroppedBitmap(bitmap));

            } catch (IOException e) {
                e.printStackTrace();
                return null;
            }
        }
        return CACHE.get(uris[0]);
    }

    @Override
    protected void onPostExecute(Bitmap bitmap) {
        imageView.setImageBitmap(bitmap);
    }


    public Bitmap getCroppedBitmap(Bitmap bitmap) {
        if (bitmap == null) {
            return null;
        }
        Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        Canvas canvas = new Canvas(output);

        final int color = 0xff424242;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);

        canvas.drawCircle(
            bitmap.getWidth() / 2,
            bitmap.getHeight() / 2,
            bitmap.getWidth() / 2,
            paint
        );
        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        return output;
    }
}
