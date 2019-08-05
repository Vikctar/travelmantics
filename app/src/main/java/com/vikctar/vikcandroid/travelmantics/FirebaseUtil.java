package com.vikctar.vikcandroid.travelmantics;

import android.app.Activity;
import android.widget.Toast;

import androidx.annotation.NonNull;

import com.firebase.ui.auth.AuthUI;
import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.database.DatabaseReference;
import com.google.firebase.database.FirebaseDatabase;

import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;

public class FirebaseUtil {
    public static FirebaseDatabase firebaseDatabase;
    public static DatabaseReference databaseReference;
    private static FirebaseUtil firebaseUtil;
    public static FirebaseAuth firebaseAuth;
    public static FirebaseAuth.AuthStateListener authStateListener;
    public static ArrayList<TravelDeal> deals;
    private static Activity caller;

    private static final int RC_SIGN_IN = 123;

    private FirebaseUtil() {}

    public static void openFirebaseReference(String reference, final Activity callerActivity) {
        if (firebaseUtil == null) {
            firebaseUtil = new FirebaseUtil();
            firebaseDatabase = FirebaseDatabase.getInstance();
            firebaseAuth = FirebaseAuth.getInstance();

            caller = callerActivity;
            authStateListener = new FirebaseAuth.AuthStateListener() {
                @Override
                public void onAuthStateChanged(@NonNull FirebaseAuth firebaseAuth) {
                    if (firebaseAuth.getCurrentUser() == null) {
                        FirebaseUtil.signIn();
                    }
                    Toast.makeText(callerActivity.getBaseContext(), "Welcome back!",
                            Toast.LENGTH_LONG).show();

                }
            };

        }
        deals = new ArrayList<>();
        databaseReference = firebaseDatabase.getReference().child(reference);
    }

    private static void signIn() {
        // Choose authentication providers
        List<AuthUI.IdpConfig> providers = Arrays.asList(
                new AuthUI.IdpConfig.EmailBuilder().build(),
                new AuthUI.IdpConfig.GoogleBuilder().build());


        // Create and launch sign-in intent
        caller.startActivityForResult(
                AuthUI.getInstance()
                        .createSignInIntentBuilder()
                        .setAvailableProviders(providers)
                        .build(),
                RC_SIGN_IN);
    }

    public static void attachAuthListener() {
        firebaseAuth.addAuthStateListener(authStateListener);
    }

    public static void detachAuthListener() {
        firebaseAuth.removeAuthStateListener(authStateListener);
    }
}
