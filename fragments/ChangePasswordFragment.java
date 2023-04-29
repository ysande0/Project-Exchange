package com.syncadapters.czar.exchange.fragments;

import android.content.Context;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.google.firebase.auth.FirebaseUser;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.viewmodels.ChangePasswordFragmentViewModel;

import org.jetbrains.annotations.NotNull;

import java.util.Objects;

@SuppressWarnings("unused")
class ChangePasswordFragment extends Fragment {

    private static final String TAG = "MSG";

    private App app;
    private Context context;
    private EditText password_editText;
    private EditText retype_password_editText;
    private ProgressBar progress_bar;

    private String password;
    private String retype_password;
    private ChangePasswordFragmentViewModel change_password_fragment_view_model;


    public void onAttach(@NotNull Context context){
        super.onAttach(context);

        this.context = context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        setHasOptionsMenu(true);
        app = ((App) context.getApplicationContext());

        //change_password_fragment_view_model = ViewModelProviders.of(this).get(ChangePasswordFragmentViewModel.class);
        change_password_fragment_view_model = new ViewModelProvider(this).get(ChangePasswordFragmentViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.fragment_change_password, container, false);

        password_editText =  view.findViewById(R.id.setting_password_editText_id);
        retype_password_editText =  view.findViewById(R.id.setting_retype_password_editText_id);
        progress_bar = view.findViewById(R.id.settings_progress_bar_circular_id);
        progress_bar.setVisibility(View.GONE);
        Button save_password_button = view.findViewById(R.id.setting_save_password_button_id);

        FirebaseAuth firebase_auth = FirebaseAuth.getInstance();
        FirebaseUser firebase_user = firebase_auth.getCurrentUser();

        if (savedInstanceState != null) {

            password = savedInstanceState.getString(context.getResources().getString(R.string.password_key));
            retype_password = savedInstanceState.getString(context.getResources().getString(R.string.retype_password_key));

            password_editText.setText(password);
            retype_password_editText.setText(retype_password);
        }

        save_password_button.setOnClickListener(v -> {

            password = password_editText.getText().toString();
            retype_password = retype_password_editText.getText().toString();
            progress_bar.setVisibility(View.VISIBLE);

            if(!password.equals(retype_password)) {
                password_editText.setBackgroundColor(Color.RED);
                retype_password_editText.setBackgroundColor(Color.RED);
                progress_bar.setVisibility(View.GONE);
            }
            else {

                Objects.requireNonNull(firebase_user).updatePassword(password).addOnCompleteListener(task -> {

                    if(task.isSuccessful()){
                        Log.d(TAG, "[ChangePasswordFragment] Firebase Password Changed");
                        //change_password_fragment_view_model.remote_server(context, progress_bar, password);
                    }

                });


            }

        });

        return view;
    }


    @Override
    public void onCreateOptionsMenu(@NonNull Menu menu, @NonNull MenuInflater inflater) {

        inflater.inflate(R.menu.home_activity_menu_items, menu);
        super.onCreateOptionsMenu(menu, inflater);

    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {


        if (item.getItemId() == R.id.profile_logout_item_id) {
            launch_profile_logout();
            return true;
        }
        return super.onOptionsItemSelected(item);


    }

    private void launch_profile_logout(){

        change_password_fragment_view_model.logout(app, context);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle outState) {
        super.onSaveInstanceState(outState);

        password = password_editText.getText().toString();
        retype_password = retype_password_editText.getText().toString();

        outState.putString(context.getResources().getString(R.string.password_key), password);
        outState.putString(context.getResources().getString(R.string.retype_password_key), retype_password);

    }

}
