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
import ch.marcbaechinger.whereihavebeen.app.ImageSearchActivity;
import ch.marcbaechinger.whereihavebeen.app.MapActivity;
import ch.marcbaechinger.whereihavebeen.app.data.DataContract;
import ch.marcbaechinger.whereihavebeen.model.Category;
import ch.marcbaechinger.whereihavebeen.model.Place;
import ch.marcbaechinger.whereihavebeen.model.UIModel;

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
    private EditText title;
    private ImageView imageView;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {
        View rootView = inflater.inflate(R.layout.fragment_edit_place, container, false);

        model = UIModel.instance(getActivity());


        Intent intent = getActivity().getIntent();

        final Place place = model.getEditPlace();

        setHasOptionsMenu(true);

        title = (EditText) rootView.findViewById(R.id.createEditTitle);

        setupCategorySelectionUi(rootView);

        imageView = (ImageView)rootView.findViewById(R.id.createImage);
        imageView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                try {
                    startImageInputIntent();
                } catch (IOException e) {
                    Log.e(TAG, e.getMessage(), e);
                }
            }
        });
        ImageButton saveButton = (ImageButton) rootView.findViewById(R.id.createButtonSave);
        saveButton.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {
                save(title, model);
                getActivity().finish();
            }
        });

        locationRow = rootView.findViewById(R.id.location_row);
        locationRow.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                startMapActivity();
            }
        });
        locationRow.setOnLongClickListener(new View.OnLongClickListener() {
            @Override
            public boolean onLongClick(View v) {
                place.setLat(null);
                place.setLng(null);
                syncLocationUI(place);
                return true;
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

        if (intent.getAction() != null && intent.getAction().equals(Intent.ACTION_SEND)) {
            handleSendAction(intent);
        }
        return rootView;
    }

    @Override
    public void onResume() {
        super.onResume();
        if (model.getEditPlace() != null) {
            syncUI(model.getEditPlace());
        }
    }

    @Override
    public void onPause() {
        model.getEditPlace().setTitle(title.getText().toString());
        super.onPause();
    }

    private void handleSendAction(Intent intent) {
        Bundle extras = intent.getExtras();
        if (extras.containsKey(Intent.EXTRA_SUBJECT)) {
            title.setText(extras.get(Intent.EXTRA_SUBJECT).toString());
        }
        if (extras.containsKey(Intent.EXTRA_TEXT)) {
            String text = extras.get(Intent.EXTRA_TEXT).toString().trim();
            if (text.startsWith("http://") || text.startsWith("https://")) {

            }
        }

        for (String key : extras.keySet()) {
            Log.d(TAG, "key: " + key + " - " + extras.get(key));
        }
    }

    private void syncUI(Place place) {
        title.setText(place.getTitle());
        syncLocationUI(place);
        if (place.getCategory() != null) {
            selectCategory(place.getCategory());
        }
        if (place.getPictureUri() != null) {
            try {
                imageUri = Uri.parse(place.getPictureUri());
                ImageUtility.setBitmapFromUri(imageUri, imageView, getActivity());
            } catch (IOException e) {
                Log.e(TAG, e.getMessage(), e);
            }
        }
    }

    private void syncLocationUI(Place place) {
        if (place.getLat() == null) {
            locationRow.setVisibility(View.GONE);
        } else {
            latView.setText(place.getLat() + "");
            lngView.setText(place.getLng() + "");
            locationRow.setVisibility(View.VISIBLE);
        }
    }

    private void setupCategorySelectionUi(View rootView) {
        categoryLabel = (TextView) rootView.findViewById(R.id.categoryLabel);
        categoryLabel.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                UIUtils.setListViewHeight(categoryList, UIUtils.calculateTotalListHeight(categoryList));
                categoryLabel.setVisibility(View.GONE);
                categoryList.setVisibility(View.VISIBLE);
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
                selectCategory((Category) adapter.getItem(position));
                categoryList.setSelection(position);
            }
        });
        categoryList.setAdapter(adapter);
    }

    private boolean save(EditText title, UIModel uiModel) {
        ContentValues contentValues = new ContentValues();
        Place editPlace = uiModel.getEditPlace();

        contentValues.put(DataContract.PLACE.FIELD_TITLE, title.getText().toString());
        editPlace.setTitle(title.getText().toString());

        contentValues.put(DataContract.PLACE.FIELD_PICTURE, editPlace.getPictureUri());

        contentValues.put(DataContract.PLACE.FIELD_CATEGORY, editPlace.getCategory().getId());

        if (editPlace.getLat() != null) {
            contentValues.put(DataContract.PLACE.FIELD_LAT, editPlace.getLat());
        }
        if (editPlace.getLng() != null) {
            contentValues.put(DataContract.PLACE.FIELD_LNG, editPlace.getLng());
        }

        if (model.getSelectedPlace() == null) {
            getActivity().getContentResolver().insert(DataContract.PLACE.CONTENT_URI, contentValues);
            return true;
        } else {
            String[] selectionArgs = new String[] {String.valueOf(model.getSelectedPlace().getId())};
            contentValues.put(DataContract.PLACE.FIELD_ID, model.getSelectedPlace().getId());
            getActivity().getContentResolver().update(
                    DataContract.PLACE.CONTENT_URI,
                    contentValues,
                    DataContract.PLACE.FIELD_ID,
                    selectionArgs
            );
            editPlace.setId(model.getSelectedPlace().getId());
            model.commitPlaceEdit();
            return false;
        }
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
        categoryLabel.setVisibility(View.VISIBLE);
        categoryList.setVisibility(View.GONE);
    }

    @Override
    public boolean onOptionsItemSelected(MenuItem item) {
        int id = item.getItemId();
        if (id == R.id.action_addPicture) {
            // Create the File where the photo should go
            try {
                startImageInputIntent();
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


    private void startImageInputIntent() throws IOException {
        final List<Intent> cameraIntents = new ArrayList<>();
        final Intent captureIntent = new Intent(android.provider.MediaStore.ACTION_IMAGE_CAPTURE);
        final PackageManager packageManager = getActivity().getPackageManager();

        File photoFile = ImageUtility.createImageFile();
        imageUri = Uri.fromFile(photoFile);

        for (ResolveInfo res : packageManager.queryIntentActivities(captureIntent, 0)) {
            final Intent intent = new Intent(captureIntent);
            intent.setComponent(new ComponentName(res.activityInfo.packageName, res.activityInfo.name));
            intent.setPackage(res.activityInfo.packageName);
            intent.putExtra(MediaStore.EXTRA_OUTPUT, imageUri);

            cameraIntents.add(intent);
        }

        cameraIntents.add(getImageSearchIntent());

        final Intent chooserIntent = Intent.createChooser(ImageUtility.getGalleryIntent(), "Select Source");

        chooserIntent.putExtra(Intent.EXTRA_INITIAL_INTENTS, cameraIntents.toArray(new Parcelable[0]));
        startActivityForResult(chooserIntent, PICTURE_REQUEST);

    }

    private Intent getImageSearchIntent() {
        Intent imageSearchIntent = new Intent(getActivity(), ImageSearchActivity.class);
        imageSearchIntent.putExtra(ImageSearchActivity.IMAGE_QUERY, title.getText().toString());
        return imageSearchIntent;
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent intent) {
        if (requestCode == PICTURE_REQUEST) {
            if(resultCode == Activity.RESULT_OK) {
                if(!ImageUtility.isCameraIntent(intent)) {
                    imageUri = intent.getData();
                }

                model.getEditPlace().setPictureUri(imageUri.toString());

                try {
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
