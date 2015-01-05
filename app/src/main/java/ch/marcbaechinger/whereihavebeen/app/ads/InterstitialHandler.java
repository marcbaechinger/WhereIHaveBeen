package ch.marcbaechinger.whereihavebeen.app.ads;

import android.app.Activity;

import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.InterstitialAd;

import app.ch.marcbaechinger.whereihavebeen.R;

public class InterstitialHandler {
    private final InterstitialAd interstitial;
    private Activity activity;
    private boolean onceOnly;


    private boolean interstitialShown;

    public InterstitialHandler(Activity activity, boolean onceOnly) {
        this.activity = activity;
        this.onceOnly = onceOnly;
        interstitial = new InterstitialAd(activity);
        interstitial.setAdUnitId(activity.getString(R.string.interstitial_ad_unit_id));
    }

    public void loadAd(String... keyword) {
        AdRequest.Builder builder = new AdRequest.Builder();
        for (String kw : keyword) {
            builder.addKeyword(kw);
        }
        interstitial.loadAd(builder.build());
    }

    public boolean show() {
        if (onceOnly && !interstitialShown && interstitial.isLoaded()) {
            interstitial.show();
            interstitialShown = true;
            return true;
        }
        return false;
    }

    public boolean isInterstitialShown() {
        return interstitialShown;
    }

}
