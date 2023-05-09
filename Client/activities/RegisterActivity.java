package com.syncadapters.czar.exchange.activities;

import android.graphics.Color;
import android.os.Bundle;
import android.util.Log;
import android.view.View;
import android.widget.Button;
import android.widget.EditText;
import android.widget.ProgressBar;
import android.widget.Toast;
import androidx.appcompat.app.AppCompatActivity;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.viewmodels.RegisterActivityViewModel;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

public class RegisterActivity extends AppCompatActivity {

    private static final String TAG = "MSG";
    private EditText FirstName_editText;
    private EditText LastName_editText;
    private EditText Email_editText;
    private EditText Password_editText;
    private EditText Retype_Password_editText;
    private ProgressBar register_progress_bar;

    private String first_name;
    private String last_name;
    private String email_address;
    private String password;
    private String retype_password;
    private static final String PASSWORD_REGEX = "[^A-Za-z0-9]|\\d";

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_register);

        Log.d(TAG, "Register Activity onCreate");
      //  RegisterActivityViewModel register_activity_view_model = ViewModelProviders.of(this).get(RegisterActivityViewModel.class);
        RegisterActivityViewModel register_activity_view_model = new ViewModelProvider(this).get(RegisterActivityViewModel.class);
        FirebaseAuth firebase_auth = FirebaseAuth.getInstance();

        register_activity_view_model.set_context(RegisterActivity.this);
        register_activity_view_model.set_firebase_auth(firebase_auth);

        FirstName_editText = findViewById(R.id.first_name_editText_id);
        LastName_editText = findViewById(R.id.last_name_editText_id);
        Email_editText = findViewById(R.id.email_address_editText_id);
        Password_editText = findViewById(R.id.password_editText_id);
        Retype_Password_editText = findViewById(R.id.re_type_password_editText_id);

        register_progress_bar = findViewById(R.id.register_progress_bar_circular_id);
        register_progress_bar.setVisibility(View.GONE);
        register_activity_view_model.set_register_progress_bar(register_progress_bar);
        Button register_button = findViewById(R.id.register_user_button_id);
        register_button.setOnClickListener(v -> {

            first_name = FirstName_editText.getText().toString().trim();
            last_name = LastName_editText.getText().toString().trim();
            email_address = Email_editText.getText().toString().trim();
            password = Password_editText.getText().toString();
            retype_password = Retype_Password_editText.getText().toString();

            Pattern pattern = Pattern.compile(PASSWORD_REGEX);
            Matcher matcher = pattern.matcher(password);


            if(password.length() < 4){

                Toast.makeText(getApplicationContext(), "Password is less than 8 characters or does not contain non-alphabetic characters", Toast.LENGTH_LONG).show();
                Password_editText.setBackgroundColor(Color.RED);
                return;
            }

            if(!(password.equals(retype_password))){

                Toast.makeText(getApplicationContext(), "Passwords do not match", Toast.LENGTH_LONG).show();
                Password_editText.setBackgroundColor(Color.RED);
                Retype_Password_editText.setBackgroundColor(Color.RED);
                return;
            }

            if(first_name.isEmpty() || last_name.isEmpty() || email_address.isEmpty()) {

                if(first_name.isEmpty())
                    FirstName_editText.setBackgroundColor(Color.RED);

                if(last_name.isEmpty())
                    LastName_editText.setBackgroundColor(Color.RED);

                if(email_address.isEmpty())
                    Email_editText.setBackgroundColor(Color.RED);

                if(password.isEmpty())
                    Password_editText.setBackgroundColor(Color.RED);

                if(retype_password.isEmpty())
                    Retype_Password_editText.setBackgroundColor(Color.RED);

                Toast.makeText(getApplicationContext(), "Information is missing", Toast.LENGTH_LONG).show();
            }
            else {

                register_progress_bar.setVisibility(View.VISIBLE);
              //  register_activity_view_model.account_creation( email_address, password, first_name, last_name);
            }

        });


    }

}
