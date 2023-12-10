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
import android.widget.ProgressBar;

import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;

import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.viewmodels.ChangePasswordFragmentViewModel;

import java.util.Objects;

public class ChangePasswordDialog extends DialogFragment {

    private static final String TAG = "MSG";

    private Context context;
    private EditText new_password_editText;
    private EditText retype_password_editText;
    private ProgressBar progress_bar;

    private String password;
    private String retype_password;
    private ChangePasswordFragmentViewModel change_password_fragment_view_model;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

       // app = ((App) context.getApplicationContext());
        //change_password_fragment_view_model = ViewModelProviders.of(this).get(ChangePasswordFragmentViewModel.class);

        change_password_fragment_view_model = new ViewModelProvider(this).get(ChangePasswordFragmentViewModel.class);

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_change_password, container, false);

        ImageView change_password_clear_dialog_button = view.findViewById(R.id.change_password_dialog_clear_image_imageView_id);
        new_password_editText = view.findViewById(R.id.setting_new_password_editText_id);
        retype_password_editText = view.findViewById(R.id.setting_retype_password_editText_id);
        progress_bar = view.findViewById(R.id.settings_progress_bar_circular_id);
        progress_bar.setVisibility(View.GONE);
        Button save_password_button = view.findViewById(R.id.setting_save_password_button_id);
    
        if (savedInstanceState != null) {

            password = savedInstanceState.getString(context.getResources().getString(R.string.password_key));
            retype_password = savedInstanceState.getString(context.getResources().getString(R.string.retype_password_key));

            new_password_editText.setText(password);
            retype_password_editText.setText(retype_password);
        }

        change_password_clear_dialog_button.setOnClickListener(v -> dismiss());

        save_password_button.setOnClickListener(v -> {

            boolean flag_error = false;
            password = new_password_editText.getText().toString();
            retype_password = retype_password_editText.getText().toString();
            progress_bar.setVisibility(View.VISIBLE);

            if(password.isEmpty()){

                new_password_editText.setError(getResources().getString(R.string.change_password_edittext_error));
                flag_error = true;
            }

            if(password.length() < 6){

                new_password_editText.setError(getResources().getString(R.string.change_password_length_min_error));
                flag_error = true;
            }

            if(password.length() > 299){

                retype_password_editText.setError(getResources().getString(R.string.change_password_length_max_error));
                flag_error = true;
            }


            if(!password.equals(retype_password)) {

                new_password_editText.setError(getResources().getString(R.string.change_password_matching_error));
                retype_password_editText.setError(getResources().getString(R.string.change_password_matching_error));
                flag_error = true;

            }

            if(flag_error) {
                progress_bar.setVisibility(View.GONE);
                return;
            }

            update_password(password);

        });


        return view;
    }

    private void update_password(String password){

        change_password_fragment_view_model.remote_server(context, this , progress_bar, password);

    }


    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        password = new_password_editText.getText().toString();
        retype_password = retype_password_editText.getText().toString();

        outState.putString(context.getResources().getString(R.string.password_key), password);
        outState.putString(context.getResources().getString(R.string.retype_password_key), retype_password);

    }


    @Override
    public void onResume() {
        super.onResume();

        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @SuppressWarnings("EmptyMethod")
    @Override
    public void onPause() {
        super.onPause();

    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "ChangePasswordDialog onDestroyView");


    }
}
