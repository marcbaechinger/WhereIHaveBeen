package ch.marcbaechinger.whereihavebeen.fragments;

import android.view.View;
import android.widget.ImageButton;

import app.ch.marcbaechinger.whereihavebeen.R;

public class FabManager {

    private FabListener fabListener;

    public void captureButtons(View rootView) {
        ImageButton fab = (ImageButton) rootView.findViewById(R.id.fab);
        if (fab != null) {
            fab.setOnClickListener(new FabClickHandler());
        }
        ImageButton secondaryFab = (ImageButton) rootView.findViewById(R.id.fabSecondary);
        if (secondaryFab!= null) {
            secondaryFab.setOnClickListener(new SecondaryFabClickHandler());
        }
    }

    public class FabClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (fabListener != null) {
                fabListener.onClick(v, true);
            }
        }
    }

    public class SecondaryFabClickHandler implements View.OnClickListener {
        @Override
        public void onClick(View v) {
            if (fabListener != null) {
                fabListener.onClick(v, false);
            }
        }
    }

    public void setFabListener(FabListener fabListener) {
        this.fabListener = fabListener;
    }

    interface FabListener {
        void onClick(View v, boolean isPrimary);
    }
}

