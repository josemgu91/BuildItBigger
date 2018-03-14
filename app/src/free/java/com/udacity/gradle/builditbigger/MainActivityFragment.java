package com.udacity.gradle.builditbigger;

import android.os.Bundle;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import com.google.android.gms.ads.AdListener;
import com.google.android.gms.ads.AdRequest;
import com.google.android.gms.ads.AdView;
import com.google.android.gms.ads.InterstitialAd;


/**
 * A placeholder fragment containing a simple view.
 */
public class MainActivityFragment extends Fragment {

    private InterstitialAd interstitialAd;

    private MainActivityFragmentInterface mainActivityFragmentInterface;

    public void setMainActivityFragmentInterface(MainActivityFragmentInterface mainActivityFragmentInterface) {
        this.mainActivityFragmentInterface = mainActivityFragmentInterface;
    }

    public MainActivityFragment() {
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        View root = inflater.inflate(R.layout.fragment_main, container, false);

        AdView mAdView = (AdView) root.findViewById(R.id.adView);
        // Create an ad request. Check logcat output for the hashed device ID to
        // get test ads on a physical device. e.g.
        // "Use AdRequest.Builder.addTestDevice("ABCDEF012345") to get test ads on this device."
        AdRequest adRequest = new AdRequest.Builder()
                .addTestDevice(AdRequest.DEVICE_ID_EMULATOR)
                .build();
        mAdView.loadAd(adRequest);
        interstitialAd = new InterstitialAd(getActivity());
        interstitialAd.setAdUnitId("ca-app-pub-3940256099942544/1033173712");
        root.findViewById(R.id.buttonTellJoke).setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                interstitialAd.loadAd(new AdRequest.Builder().build());
                interstitialAd.setAdListener(new AdListener() {
                    @Override
                    public void onAdLoaded() {
                        interstitialAd.show();
                    }

                    @Override
                    public void onAdClosed() {
                        super.onAdClosed();
                        if (mainActivityFragmentInterface != null) {
                            mainActivityFragmentInterface.onShowJokeButtonClick();
                        }
                    }

                    @Override
                    public void onAdFailedToLoad(int i) {
                        super.onAdFailedToLoad(i);
                        if (mainActivityFragmentInterface != null) {
                            mainActivityFragmentInterface.onShowJokeButtonClick();
                        }
                    }
                });
            }
        });
        return root;
    }

    public interface MainActivityFragmentInterface {

        void onShowJokeButtonClick();

    }
}
