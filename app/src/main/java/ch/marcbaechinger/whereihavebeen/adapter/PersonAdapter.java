package ch.marcbaechinger.whereihavebeen.adapter;

import android.app.Activity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.TextView;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.model.PersonData;

public class PersonAdapter extends ArrayAdapter<PersonData> {
    private final Activity activity;
    private final int listItemId;
    private PersonData[] data;

    public PersonAdapter(Activity activity, int listItemId, PersonData[] data) {
        super(activity, listItemId);
        this.activity = activity;
        this.listItemId = listItemId;
        this.data = data;
    }

    @Override
    public View getView(int position, View row, ViewGroup parent) {
        PersonData person = getItem(position);
        PersonHolder personHolder = null;
        if (row == null) {
            LayoutInflater inflater = activity.getLayoutInflater();
            row = inflater.inflate(listItemId, parent, false);

            personHolder = new PersonHolder(
                (ImageView) row.findViewById(R.id.friend_list_view_picture),
                (TextView) row.findViewById(R.id.friend_list_view_display_name)
            );
            row.setTag(personHolder);
        } else {
            personHolder = (PersonHolder) row.getTag();
        }
        personHolder.setDisplayName(person.getDisplayName());
        personHolder.setImageUrl(person.getImageUrl());
        return row;
    }

}
