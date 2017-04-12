package com.appfission.wordzza;

import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.view.LayoutInflater;
import android.view.View;
import android.view.View.OnClickListener;
import android.view.ViewGroup;
import android.widget.TextView;

/**
 * Created by srikanthmannepalle on 3/21/17.
 */

public class MainMenuFragment extends Fragment implements OnClickListener {

    private String mGreeting = "Hello user (not signed in)";

    public interface Listener {
        public void onStartGameRequested();
        public void onShowAchievementsRequested();
        public void onShowLeaderboardsRequested();
        public void onShowRatingsRequested();
        public void onSignInButtonClicked();
        public void onSignOutButtonClicked();
    }

    Listener mListener = null;
    boolean mShowSignIn = true;

    @Nullable
    @Override
    public View onCreateView(LayoutInflater inflater, @Nullable ViewGroup container, Bundle savedInstanceState) {
        View view = inflater.inflate(R.layout.fragment_mainmenu, container, false);
        final int[] itemsClickable = new int[] {
                R.id.playButton, R.id.show_achievements_button,
                R.id.show_leaderboards_button, R.id.show_rating_button,
                R.id.sign_in_button, R.id.sign_out_button
        };
        for (int i : itemsClickable) {
            view.findViewById(i).setOnClickListener(this);
        }
        return view;
    }

    @Override
    public void onStart() {
        super.onStart();
        updateUi();
    }

    public void setListener(Listener l) {
        mListener = l;
    }

    public void setGreeting(String greeting) {
        mGreeting = greeting;
        updateUi();
    }

    void updateUi() {
        if (getActivity() == null) return;
        TextView tv = (TextView) getActivity().findViewById(R.id.hello);
        if (tv != null) tv.setText(mGreeting);

        getActivity().findViewById(R.id.sign_in_bar).setVisibility(mShowSignIn ?
                View.VISIBLE : View.GONE);
        getActivity().findViewById(R.id.sign_out_bar).setVisibility(mShowSignIn ?
                View.GONE : View.VISIBLE);
    }

    @Override
    public void onClick(View view) {
        switch (view.getId()) {
            case R.id.playButton:
                mListener.onStartGameRequested();
                break;
            case R.id.show_achievements_button:
                mListener.onShowAchievementsRequested();
                break;
            case R.id.show_leaderboards_button:
                mListener.onShowLeaderboardsRequested();
                break;
            case R.id.show_rating_button:
                mListener.onShowRatingsRequested();
                break;
            case R.id.sign_in_button:
                mListener.onSignInButtonClicked();
                break;
            case R.id.sign_out_button:
                mListener.onSignOutButtonClicked();
                break;
        }
    }

    public void setShowSignInButton(boolean showSignIn) {
        mShowSignIn = showSignIn;
        updateUi();
    }
}
