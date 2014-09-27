package ch.marcbaechinger.whereihavebeen.fragments;

import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.app.MainActivity;
import ch.marcbaechinger.whereihavebeen.app.data.DataContract;

public class EditPlaceFragment extends Fragment {

    public static final int PICTURE_REQUEST = 1001;
    private static final String TAG = EditPlaceFragment.class.getName();

    private Uri imageUri;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_place, container, false);

        setHasOptionsMenu(true);

        Intent intent = getActivity().getIntent();
        final EditText description = (EditText) rootView.findViewById(R.id.createEditDescription);
        final EditText title = (EditText) rootView.findViewById(R.id.createEditTitle);
        if (intent.getExtras() != null) {
            String subject = intent.getExtras().getString(Intent.EXTRA_SUBJECT);
            title.setText(subject);
            String text = intent.getExtras().getString(Intent.EXTRA_TEXT);
            description.setText(text);
        }
        Button saveButton = (Button) rootView.findViewById(R.id.createButtonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                ContentValues contentValues = new ContentValues();
                contentValues.put(DataContract.PLACE.FIELD_TITLE, title.getText().toString());
                contentValues.put(DataContract.PLACE.FIELD_DESCRIPTION, description.getText().toString());
                if (imageUri != null) {
                    contentValues.put(DataContract.PLACE.FIELD_PICTURE, imageUri.toString());
                }
                getActivity().getContentResolver().insert(DataContract.PLACE.CONTENT_URI, contentValues);

                Intent backIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(backIntent);
            }
        });

        return rootView;
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_addPicture) {
            // Create the File where the photo should go
            try {
                openImageIntent();
            } catch (IOException ex) {
                Log.e(TAG, ex.getMessage());
                ex.printStackTrace();
            }
            return true;
        }
        return super.onOptionsItemSelected(item);
    }


    private void openImageIntent() throws IOException {
        final List<Intent> cameraIntents = new ArrayList<Intent>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();
        for (ResolveInfo res : packageManager.queryIntentActivities(captureIntent, 0)) {
            final String packageName = res.activityInfo.packageName;
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(packageName);

            File photoFile = ImageUtility.createImageFile();
            imageUri = Uri.fromFile(photoFile);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);
            cameraIntents.add(intent);
        }

        final Intent chooserIntent = Intent.createChooser(ImageUtility.getGalleryIntent(), "Select Source");

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[]{}));
        startActivityForResult(chooserIntent, PICTURE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PICTURE_REQUEST) {
            if(resultCode == getActivity().RESULT_OK) {
                if(!ImageUtility.isCameraIntent(intent)) {
                    imageUri = intent.getData();
                }

                try {
                    ImageView imageView = (ImageView)getActivity().findViewById(R.id.createImage);
                    ImageUtility.setBitmapFromUri(imageUri, imageView, getActivity());
                } catch (IOException e) {
                    e.printStackTrace();
                }
            }
        } else {
            super.onActivityResult(requestCode, resultCode, intent);
        }
    }
}
