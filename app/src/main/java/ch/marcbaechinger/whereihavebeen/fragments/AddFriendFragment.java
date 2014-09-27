package ch.marcbaechinger.whereihavebeen.fragments;

import android.app.Fragment;
import android.content.Intent;
import android.content.IntentSender;
import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ArrayAdapter;
import android.widget.ImageView;
import android.widget.ListView;
import android.widget.TextView;

import com.google.android.gms.common.ConnectionResult;
import com.google.android.gms.common.SignInButton;
import com.google.android.gms.common.api.CommonStatusCodes;
import com.google.android.gms.common.api.GoogleApiClient;
import com.google.android.gms.common.api.ResultCallback;
import com.google.android.gms.plus.People;
import com.google.android.gms.plus.Plus;
import com.google.android.gms.plus.model.people.Person;
import com.google.android.gms.plus.model.people.PersonBuffer;

import app.ch.marcbaechinger.whereihavebeen.R;
import ch.marcbaechinger.whereihavebeen.actions.LoadBitmapTask;
import ch.marcbaechinger.whereihavebeen.adapter.PersonAdapter;
import ch.marcbaechinger.whereihavebeen.adapter.PersonBufferMapper;
import ch.marcbaechinger.whereihavebeen.model.PersonData;

public class AddFriendFragment extends Fragment implements GoogleApiClient.ConnectionCallbacks,
        GoogleApiClient.OnConnectionFailedListener, View.OnClickListener, ResultCallback<People.LoadPeopleResult> {


    private static final int RC_SIGN_IN = 0;
    private GoogleApiClient apiClient;
    private boolean intentInProgress;
    private boolean signInClicked;
    private ConnectionResult connectionResult;
    private ArrayAdapter<PersonData> friendsAdapter;
    private SignInButton signInButton;
    private ImageView profilePicture;
    private View profilePanel;
    PersonBufferMapper mapper = new PersonBufferMapper();

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
            Bundle savedInstanceState) {

        View rootView = inflater.inflate(R.layout.fragment_add_friend, container, false);

        signInButton = (SignInButton) rootView.findViewById(R.id.sign_in_button);
        signInButton.setOnClickListener(this);

        profilePicture = (ImageView) rootView.findViewById(R.id.profilePicture);
        profilePicture.setOnClickListener(this);

        profilePanel = rootView.findViewById(R.id.signed_in_panel);

        apiClient = new GoogleApiClient.Builder(getActivity())
                .addConnectionCallbacks(this)
                .addOnConnectionFailedListener(this)
                .addApi(Plus.API)
                .addScope(Plus.SCOPE_PLUS_LOGIN)
                .build();



        ListView friendList = (ListView) rootView.findViewById(R.id.add_friend_friend_list);
        friendsAdapter = new PersonAdapter(getActivity(), R.layout.friend_suggestion_list_item, new PersonData[0]);
        friendList.setAdapter(friendsAdapter);

        return rootView;
    }

    @Override
    public void onStart() {
        super.onStart();
        apiClient.connect();
    }

    @Override
    public void onActivityResult(int requestCode, int resultCode, Intent data) {
        if (requestCode == RC_SIGN_IN) {
            intentInProgress = false;
            if (!apiClient.isConnecting()) {
                apiClient.connect();
            }
        }
    }

    @Override
    public void onStop() {
        super.onStop();
        if (apiClient.isConnected()) {
            apiClient.disconnect();
        }
    }

    @Override
    public void onConnected(Bundle bundle) {
        signInClicked = false;
        signInButton.setVisibility(View.INVISIBLE);
        setupProfileUi();
        Plus.PeopleApi.loadVisible(apiClient, null).setResultCallback(this);
    }

    private void setupProfileUi() {
        Person currentPerson = Plus.PeopleApi.getCurrentPerson(apiClient);
        String personName = currentPerson.getDisplayName();
        ((TextView)getView().findViewById(R.id.profileDisplayName)).setText(personName);

        Person.Image personPhoto = currentPerson.getImage();
        new LoadBitmapTask(((ImageView)getView().findViewById(R.id.profilePicture)))
                .execute(personPhoto.getUrl() + "0");
    }

    @Override
    public void onConnectionSuspended(int i) {
        apiClient.connect();
    }

    @Override
    public void onConnectionFailed(ConnectionResult result) {
        if (!intentInProgress) {
            // Store the ConnectionResult so that we can use it later when the user clicks
            // 'sign-in'.
            connectionResult = result;
            if (signInClicked) {
                resolveSignInError();
            }
        }
    }
    private void resolveSignInError() {
        if (connectionResult != null && connectionResult.hasResolution()) {
            try {
                intentInProgress = true;
                getActivity().startIntentSenderForResult(
                        connectionResult.getResolution().getIntentSender(),
                        RC_SIGN_IN, null, 0, 0, 0);
            } catch (IntentSender.SendIntentException e) {
                intentInProgress = false;
                apiClient.connect();
            }
        }
    }

    @Override
    public void onResult(People.LoadPeopleResult peopleData) {
        if (peopleData.getStatus().getStatusCode() == CommonStatusCodes.SUCCESS) {
            PersonBuffer personBuffer = peopleData.getPersonBuffer();
            try {
                for (int i = 0; i < personBuffer.getCount(); i++) {
                    friendsAdapter.add(mapper.map(personBuffer.get(i)));

                }
                friendsAdapter.notifyDataSetChanged();

                String nextPageToken = peopleData.getNextPageToken();
                if (nextPageToken != null) {
                    Plus.PeopleApi.loadVisible(apiClient, nextPageToken).setResultCallback(this);
                }
            } finally {
                personBuffer.close();
            }
        }
    }

    private void signOut() {
        if (apiClient.isConnected()) {
            Plus.AccountApi.clearDefaultAccount(apiClient);
            apiClient.disconnect();
        }
    }

    @Override
    public void onClick(View view) {
        if (view.getId() == R.id.sign_in_button && !apiClient.isConnecting()) {
            signInClicked = true;
            resolveSignInError();
        } else if (view.getId() == R.id.profilePicture) {
            signOut();
            signInButton.setVisibility(View.VISIBLE);
            profilePanel.setVisibility(View.INVISIBLE);
        }
    }
}
