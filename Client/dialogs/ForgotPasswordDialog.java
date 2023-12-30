package com.syncadapters.czar.exchange.dialogs;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ImageView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.viewmodels.ForgotPasswordDialogViewModel;

import org.apache.commons.validator.routines.EmailValidator;

import java.util.Objects;

public class ForgotPasswordDialog extends DialogFragment {

    private static final String TAG = "MSG";
    private EditText email_editText;

    private FirebaseAuth firebase_auth;
    private ForgotPasswordDialogViewModel forgot_password_dialog_view_model;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        firebase_auth = FirebaseAuth.getInstance();
        
        forgot_password_dialog_view_model = new ViewModelProvider(this).get(ForgotPasswordDialogViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "ForgotPasswordDialog onCreateView");
        View view = inflater.inflate(R.layout.dialog_forgot_password, container, false);

        ImageView forgot_password_clear_dialog_button = view.findViewById(R.id.forgot_password_dialog_clear_image_imageView_id);
        email_editText = view.findViewById(R.id.forgot_password_email_address_editText_id);
        EditText password_editText = view.findViewById(R.id.forgot_password_new_password_editText_id);
        EditText retype_password_editText = view.findViewById(R.id.forgot_password_confirm_password_editText_id);
        Button submit_button = view.findViewById(R.id.forgot_password_submit_button_id);

        if(savedInstanceState != null){

            String email = savedInstanceState.getString("email");
            email_editText.setText(email);

        }

        forgot_password_clear_dialog_button.setOnClickListener(v -> dismiss());

        password_editText.setVisibility(View.GONE);
        retype_password_editText.setVisibility(View.GONE);

        submit_button.setOnClickListener(v -> {

            @SuppressWarnings("UnusedAssignment") boolean flag_error = false;
            String email = email_editText.getText().toString();

            if(email.isEmpty()){
                email_editText.setError(getResources().getString(R.string.register_email_address_edittext_error));
                return;
            }

            flag_error = validate_email(email);

            if(flag_error){

                email_editText.setError(getResources().getString(R.string.register_email_address_edittext_error));
                return;

            }

            forgot_password_dialog_view_model.send_reset_password_email(firebase_auth, email);
            dismiss();

        });



        return view;
    }

    private boolean validate_email(String email){

        email = email.trim();
        EmailValidator email_validator = EmailValidator.getInstance();
        if(email_validator.isValid(email))
            return false;
        else {
            email_editText.setError(getResources().getString(R.string.register_email_address_edittext_error));
            return true;
        }
    }

    @Override
    public void onStart() {
        super.onStart();
        
    }

    @Override
    public void onResume() {
        super.onResume();
        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle out_state) {
        super.onSaveInstanceState(out_state);

        Log.d(TAG, "ForgotPasswordDialog onSaveInstanceState");

        out_state.putString("email", email_editText.getText().toString());

    }

    @Override
    public void onPause() {
        super.onPause();
    }

    @Override
    public void onStop() {
        super.onStop();

        
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
    }

    @Override
    public void onDestroy() {
        super.onDestroy();
    }

    @Override
    public void onDetach() {
        super.onDetach();
    }
}
