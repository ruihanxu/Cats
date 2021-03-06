package com.example.xuruihan.cats;

import android.animation.Animator;
import android.animation.AnimatorListenerAdapter;
import android.annotation.TargetApi;
import android.content.Context;
import android.os.Build;
import android.os.Bundle;
import android.support.annotation.Nullable;
import android.support.v4.app.Fragment;
import android.text.TextUtils;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.widget.AutoCompleteTextView;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Toast;

import com.example.xuruihan.cats.model.LoginManager;
import com.example.xuruihan.cats.model.User;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;

import static android.content.ContentValues.TAG;
import static com.example.xuruihan.cats.R.id.login;

/**
 * A simple {@link Fragment} subclass.
 * Activities that contain this fragment must implement the
 * {@link LoginFragment.OnLoginFragmentInteractionListener} interface
 * to handle interaction events.
 */
public class LoginFragment extends Fragment implements LoginManager.LoginCallBack{


    // --Commented out by Inspection (11/18/17, 15:56):private static final int REQUEST_READ_CONTACTS = 0;
    private AutoCompleteTextView muserView;
    private EditText mPasswordView;
    private View mProgressView;
    private View mLoginFormView;
    private FirebaseAuth mAuth;
    private FirebaseAuth.AuthStateListener mAuthListener;
    private String userUID;

    private OnLoginFragmentInteractionListener loginListener;

    /**
     * The empty public constructor
     */
    public LoginFragment() {
        // Required empty public constructor
    }


    @Override
    public void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        mAuth = FirebaseAuth.getInstance();
        mAuthListener = firebaseAuth -> {
            FirebaseUser user = firebaseAuth.getCurrentUser();
            if (user != null) {
                // User is signed in
                Log.d(TAG, "onAuthStateChanged:signed_in:" + user.getUid());
            } else {
                // User is signed out
                Log.d(TAG, "onAuthStateChanged:signed_out");
            }
            // ...
        };
    }

    @Override
    public void onStart() {
        super.onStart();
        mAuth.addAuthStateListener(mAuthListener);
    }

    @Override
    public void onStop() {
        super.onStop();
        if (mAuthListener != null) {
            mAuth.removeAuthStateListener(mAuthListener);
        }
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container,
                             Bundle savedInstanceState) {
        return inflater.inflate(R.layout.fragment_signin, container, false);
    }

    @Override
    public void onActivityCreated(@Nullable Bundle savedInstanceState) {
        super.onActivityCreated(savedInstanceState);

        muserView = (AutoCompleteTextView) getActivity().findViewById(R.id.user);
        Button cancelButton = (Button) getActivity().findViewById(R.id.cancel_login_button);
        cancelButton.setOnClickListener((View v) -> loginListener.cancelLogin());

        mPasswordView = (EditText) getActivity().findViewById(R.id.password);
        mPasswordView.setOnEditorActionListener((textView, id, keyEvent) -> {
            if (id == login || id == EditorInfo.IME_NULL) {
                attemptLogin();
                return true;
            }
            return false;
        });

        Button muserSignInButton = (Button) getActivity().findViewById(R.id.user_sign_in_button);
        muserSignInButton.setOnClickListener(view -> attemptLogin());

        mLoginFormView = getActivity().findViewById(R.id.login_form);
        mProgressView = getActivity().findViewById(R.id.login_progress);


    }

    /**
     * Attempts to sign in or register the account specified by the login form.
     * If there are form errors (invalid user, missing fields, etc.), the
     * errors are presented and no actual login attempt is made.
     */
    private void attemptLogin() {


        // Reset errors.
        muserView.setError(null);
        mPasswordView.setError(null);

        // Store values at the time of the login attempt.
        String user = muserView.getText().toString();
        String password = mPasswordView.getText().toString();

        boolean cancel = false;
        View focusView = null;

        // Check for a valid password, if the user entered one.
        if (!TextUtils.isEmpty(password) && !isPasswordValid()) {
            mPasswordView.setError(getString(R.string.error_invalid_password));
        }

        // Check for a valid user address.
        if (TextUtils.isEmpty(user)) {
            muserView.setError(getString(R.string.error_field_required));
        } else if (!isuserValid()) {
            muserView.setError(getString(R.string.error_invalid_user));
        }

        LoginManager.LoginCallBack callback = this;

        mAuth.signInWithEmailAndPassword(user, password)
                .addOnCompleteListener(getActivity(), task -> {
                    Log.d(TAG, "signInWithEmail:onComplete:" + task.isSuccessful());

                    // If sign in fails, display a message to the user. If sign in succeeds
                    // the auth state listener will be notified and logic to handle the
                    // signed in user can be handled in the listener.
                    if (!task.isSuccessful()) {
                        Log.w(TAG, "signInWithEmail:failed", task.getException());
                        Toast.makeText(getActivity(), "Log in failed.",
                                Toast.LENGTH_SHORT).show();
                    } else {
                        FirebaseUser currentUser = mAuth.getCurrentUser();
                        assert currentUser != null;
                        userUID = currentUser.getUid();
                        LoginManager.getInstance().doLogin(userUID, password, callback);
                        Toast.makeText(getActivity(), "Log in successful.",
                                Toast.LENGTH_SHORT).show();

                    }
                });
    }

    /**
     * Check to see if the username is valid
     * @return the boolean if the user is valid
     */
    @SuppressWarnings("SameReturnValue")
    private boolean isuserValid() {
        //TODO: Replace this with your own logic
        return true;
    }

    /**
     * Check to see if the password is valid
     * @return the boolean if the password is valid
     */
    @SuppressWarnings("SameReturnValue")
    private boolean isPasswordValid() {
        //TODO: Replace this with your own logic
        return true;
    }

    /**
     * Shows the progress UI and hides the login form.
     */
    @TargetApi(Build.VERSION_CODES.HONEYCOMB_MR2)
    private void showProgress() {
        // On Honeycomb MR2 we have the ViewPropertyAnimator APIs, which allow
        // for very easy animations. If available, use these APIs to fade-in
        // the progress spinner.
        if (Build.VERSION.SDK_INT >= Build.VERSION_CODES.HONEYCOMB_MR2) {
            int shortAnimTime = getResources().getInteger(android.R.integer.config_shortAnimTime);

            mLoginFormView.setVisibility(View.VISIBLE);
            mLoginFormView.animate().setDuration(shortAnimTime).alpha(
                    1).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mLoginFormView.setVisibility(View.VISIBLE);
                }
            });

            mProgressView.setVisibility(View.GONE);
            mProgressView.animate().setDuration(shortAnimTime).alpha(
                    0).setListener(new AnimatorListenerAdapter() {
                @Override
                public void onAnimationEnd(Animator animation) {
                    mProgressView.setVisibility(View.GONE);
                }
            });
        } else {
            // The ViewPropertyAnimator APIs are not available, so simply show
            // and hide the relevant UI components.
            mProgressView.setVisibility(View.GONE);
            mLoginFormView.setVisibility(View.VISIBLE);
        }
    }

    @Override
    public void onLoginSuccess(User user) {
        showProgress();
        loginListener.goToMainPage();
    }

    @Override
    public void onLoginFail(String msg) {
        muserView.setError("Invalid username or password:(");
        showProgress();
    }

    @Override
    public void onAttach(Context context) {
        super.onAttach(context);
        if (context instanceof OnLoginFragmentInteractionListener) {
            loginListener = (OnLoginFragmentInteractionListener) context;
        } else {
            throw new RuntimeException(context.toString()
                    + " must implement OnFragmentInteractionListener");
        }
    }

    @Override
    public void onDetach() {
        super.onDetach();
        loginListener = null;
    }

    /**
     * This interface must be implemented by activities that contain this
     * fragment to allow an interaction in this fragment to be communicated
     * to the activity and potentially other fragments contained in that
     * activity.
     * <p>
     * See the Android Training lesson <a href=
     * "http://developer.android.com/training/basics/fragments/communicating.html"
     * >Communicating with Other Fragments</a> for more information.
     */
    public interface OnLoginFragmentInteractionListener {
        // TODO: Update argument type and name
        void goToMainPage();
        void cancelLogin();

    }
}
