package ch.marcbaechinger.whereihavebeen.fragments;

import android.app.Activity;
import android.app.Fragment;
import android.content.ComponentName;
import android.content.ContentValues;
import android.content.Intent;
import android.content.pm.PackageManager;
import android.content.pm.ResolveInfo;
import android.graphics.Color;
import android.graphics.drawable.GradientDrawable;
import android.net.Uri;
import android.os.Bundle;
import android.os.Parcelable;
import android.provider.MediaStore;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ListAdapter;
import android.widget.ListView;
import android.widget.TextView;

import java.io.File;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.adapter.CategorySelectionAdapter;
import ch.marcbaechinger.whereihavebeen.app.MainActivity;
import ch.marcbaechinger.whereihavebeen.app.MapActivity;
import ch.marcbaechinger.whereihavebeen.app.UIModel;
import ch.marcbaechinger.whereihavebeen.app.data.DataContract;
import ch.marcbaechinger.whereihavebeen.model.Category;
import ch.marcbaechinger.whereihavebeen.model.Place;

public class EditPlaceFragment extends Fragment {

    public static final int PICTURE_REQUEST = 1001;
    private static final String TAG = EditPlaceFragment.class.getName();

    private Uri imageUri;
    private TextView categoryLabel;
    private ListView categoryList;

    private TextView latView;
    private TextView lngView;
    private UIModel model;
    private View locationRow;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_place, container, false);
        model = UIModel.instance(getActivity());

        setHasOptionsMenu(true);

        Intent intent = getActivity().getIntent();
        final EditText title = (EditText) rootView.findViewById(R.id.createEditTitle);
        if (intent.getExtras() != null) {
            String subject = intent.getExtras().getString(Intent.EXTRA_SUBJECT);
            title.setText(subject);
        }

        setupCategorySelectionUi(rootView);

        ImageButton saveButton = (ImageButton) rootView.findViewById(R.id.createButtonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(title, model);

                Intent backIntent = new Intent(getActivity(), MainActivity.class);
                startActivity(backIntent);
            }
        });

        locationRow = rootView.findViewById(R.id.location_row);
        locationRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMapActivity();
            }
        });
        latView = (TextView)rootView.findViewById(R.id.latitude);
        lngView = (TextView)rootView.findViewById(R.id.longitude);

        if (model.getSelectedCategory()!= null) {
            selectCategory(model.getSelectedCategory());
            categoryLabel.setVisibility(View.VISIBLE);
            UIUtils.setListViewHeight(categoryList, 0);
        } else {
            UIUtils.setListViewHeight(categoryList, UIUtils.calculateTotalListHeight(categoryList));
        }

        return rootView;
    }

    private void setupCategorySelectionUi(View rootView) {
        categoryLabel = (TextView) rootView.findViewById(R.id.categoryLabel);
        categoryLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.setListViewHeight(categoryList, UIUtils.calculateTotalListHeight(categoryList));
                categoryLabel.setVisibility(View.GONE);
            }
        });


        categoryList = (ListView) rootView.findViewById(R.id.categoryList);

        final ListAdapter adapter = new CategorySelectionAdapter(getActivity(),
                R.layout.category_selection_list_item,
                new ArrayList<>(model.getCategories()));
        categoryList.setOnItemClickListener(new AdapterView.OnItemClickListener() {
            @Override
            public void onItemClick(AdapterView<?> parent, View view, int position, long id) {
                UIUtils.setListViewHeight(categoryList, 0);
                categoryLabel.setVisibility(View.VISIBLE);
                selectCategory((Category) adapter.getItem(position));
                categoryList.setSelection(position);
            }
        });
        categoryList.setAdapter(adapter);
    }

    private void save(EditText title, UIModel uiModel) {
        ContentValues contentValues = new ContentValues();
        Place editPlace = uiModel.getEditPlace();

        contentValues.put(DataContract.PLACE.FIELD_TITLE, title.getText().toString());
        if (imageUri != null) {
            contentValues.put(DataContract.PLACE.FIELD_PICTURE, imageUri.toString());
        }
        contentValues.put(DataContract.PLACE.FIELD_CATEGORY, editPlace.getCategory().getId());

        if (editPlace.getLat() != null) {
            contentValues.put(DataContract.PLACE.FIELD_LAT, editPlace.getLat());
        }
        if (editPlace.getLng() != null) {
            contentValues.put(DataContract.PLACE.FIELD_LNG, editPlace.getLng());
        }

        getActivity().getContentResolver().insert(DataContract.PLACE.CONTENT_URI, contentValues);
    }

    @Override
    public void onStart() {
        super.onStart();
        updateLatLng();
    }

    private void updateLatLng() {
        if (model.getEditPlace().getLat() != null) {
            latView.setText(model.getEditPlace().getLat().toString());
            lngView.setText(model.getEditPlace().getLng().toString());
            locationRow.setVisibility(View.VISIBLE);
        } else {
            locationRow.setVisibility(View.GONE);
        }
    }



    private void selectCategory(Category category) {
        model.getEditPlace().setCategory(category);
        categoryLabel.setText(category.getTitle());
        ((GradientDrawable)categoryLabel.getBackground()).setColor(Color.parseColor(category.getColor()));
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
        } else if (id == R.id.action_edit_location) {
            startMapActivity();
            return true;
        }
        return super.onOptionsItemSelected(item);
    }

    private void startMapActivity() {
        Intent mapIntent = new Intent(getActivity(), MapActivity.class);
        startActivity(mapIntent);
        getActivity().overridePendingTransition(R.anim.right_slide_in, R.anim.right_slide_out);
    }


    private void openImageIntent() throws IOException {
        final List<Intent> cameraIntents = new ArrayList<>();
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

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[0]));
        startActivityForResult(chooserIntent, PICTURE_REQUEST);

    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PICTURE_REQUEST) {
            if(resultCode == Activity.RESULT_OK) {
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
