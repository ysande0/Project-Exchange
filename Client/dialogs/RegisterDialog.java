package com.syncadapters.czar.exchange.dialogs;

import android.app.DatePickerDialog;
import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.CheckBox;
import android.widget.DatePicker;
import android.widget.EditText;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.ProgressBar;
import android.widget.TextView;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;

import com.google.firebase.auth.FirebaseAuth;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.viewmodels.RegisterActivityViewModel;

import org.apache.commons.validator.routines.EmailValidator;

import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.Locale;
import java.util.Objects;

public class RegisterDialog extends DialogFragment implements DatePickerDialog.OnDateSetListener {

    private static final String TAG = "MSG";
    private Context context;
    private FragmentManager fragment_manager;
    private EditText FirstName_editText;
    private EditText LastName_editText;
    private EditText DateOfBirth_editText;
    private EditText Email_editText;
    private EditText Password_editText;
    private EditText Retype_Password_editText;
    private CheckBox tos_and_pp_checkBox;
    private TextView terms_of_service_textView;
    private TextView privacy_policy_textView;
    private ProgressBar register_progress_bar;
    private Button register_button;

    private String first_name;
    private String last_name;
    private String date_of_birth;
    private String date_of_birth_ms;
    private String email_address;
    private String password;
    private String retype_password;
    private FirebaseAuth firebase_auth;
    private RegisterActivityViewModel register_activity_view_model;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);
        
        this.context = context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "RegisterDialog onCreate");

        firebase_auth = FirebaseAuth.getInstance();

    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "RegisterDialog onCreateView");
        View view = inflater.inflate(R.layout.dialog_register, container, false);

        // noinspection ConstantConditions
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));

        register_activity_view_model = new ViewModelProvider(this).get(RegisterActivityViewModel.class);

        firebase_auth = FirebaseAuth.getInstance();

        register_activity_view_model.set_context(this.context);
        register_activity_view_model.set_firebase_auth(firebase_auth);

        FirstName_editText = view.findViewById(R.id.first_name_editText_id);
        LastName_editText = view.findViewById(R.id.last_name_editText_id);
        DateOfBirth_editText = view.findViewById(R.id.date_of_birth_editText_id);
        DateOfBirth_editText.setEnabled(false);
        Email_editText = view.findViewById(R.id.email_address_editText_id);
        Password_editText = view.findViewById(R.id.password_editText_id);
        Retype_Password_editText = view.findViewById(R.id.re_type_password_editText_id);

        tos_and_pp_checkBox = view.findViewById(R.id.tos_and_pp_check_box_id);
        terms_of_service_textView = view.findViewById(R.id.tos_and_pp_two_text_view_id);
        privacy_policy_textView = view.findViewById(R.id.tos_and_pp_four_text_view_id);

        ImageView registeration_clear_dialog_button = view.findViewById(R.id.registeration_dialog_clear_image_imageView_id);
        registeration_clear_dialog_button.setOnClickListener(v -> dismiss());

        ImageButton date_of_birth_button = view.findViewById(R.id.date_of_birth_button_id);
        date_of_birth_button.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View v) {
                display_date_picker();
            }
        });


        terms_of_service_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment_manager = getActivity().getSupportFragmentManager();
                TosDialog tos_dialog = new TosDialog();
                 tos_dialog.show(fragment_manager, "TOS_Dialog");

            }
        });

        privacy_policy_textView.setOnClickListener(new View.OnClickListener() {
            @Override
            public void onClick(View view) {

                fragment_manager = getActivity().getSupportFragmentManager();
                PPDialog pp_dialog = new PPDialog();
                pp_dialog.show(fragment_manager, "pp_Dialog");

            }
        });

        register_progress_bar = view.findViewById(R.id.register_progress_bar_circular_id);
        register_progress_bar.setVisibility(View.GONE);
        register_activity_view_model.set_register_progress_bar(register_progress_bar);

        register_button = view.findViewById(R.id.register_user_button_id);
        register_button.setOnClickListener(v -> {


            if(!tos_and_pp_checkBox.isChecked()){
                Toast.makeText(context, "TOS and PP was not read and agreed to", Toast.LENGTH_LONG).show();
                return;
            }
            //noinspection UnusedAssignment
            boolean flag_error = false;
            first_name = FirstName_editText.getText().toString().trim();
            last_name = LastName_editText.getText().toString().trim();
            date_of_birth = DateOfBirth_editText.getText().toString().trim();
            DateOfBirth_editText.setError(null);
            email_address = Email_editText.getText().toString().trim();
            password = Password_editText.getText().toString().trim();
            retype_password = Retype_Password_editText.getText().toString().trim();



            flag_error = validate_email(email_address);
            flag_error = validate_birthday(date_of_birth);

     
            if(first_name.isEmpty()){

                flag_error = true;
                FirstName_editText.setError(getResources().getString(R.string.register_first_name_edittext_error));

            }

            if(last_name.isEmpty()){

                flag_error = true;
                LastName_editText.setError(getResources().getString(R.string.register_last_name_edittext_error));

            }

            if(email_address.isEmpty()){

                flag_error = true;
                Email_editText.setError(getResources().getString(R.string.register_email_address_edittext_error));

            }

            if(date_of_birth.isEmpty()){

                flag_error = true;
                DateOfBirth_editText.setError(getResources().getString(R.string.register_date_of_birthday_edittext_error));
                Toast.makeText(this.context, getResources().getString(R.string.register_date_of_birthday_edittext_error), Toast.LENGTH_LONG).show();
            
            }

            if(password.isEmpty()){

                Password_editText.setError(getResources().getString(R.string.register_password_edittext_error));
                flag_error = true;
            }

            if(password.length() < 6){

                Password_editText.setError(getResources().getString(R.string.register_password_length_min_error));
                flag_error = true;
            }

            if(password.length() > 299){

                Password_editText.setError(getResources().getString(R.string.register_password_length_max_error));
                flag_error = true;
            }

            if(!(password.equals(retype_password))){

                Password_editText.setError(getResources().getString(R.string.register_password_matching_error));
                Retype_Password_editText.setError(getResources().getString(R.string.register_password_matching_error));
                flag_error = true;
            }


             Toast.makeText(context, "Processing...", Toast.LENGTH_LONG).show();

             register_progress_bar.setVisibility(View.VISIBLE);
             register_activity_view_model.account_creation(getActivity(), RegisterDialog.this, email_address, date_of_birth_ms , password, first_name, last_name);

        });

        return view;
    }

    private boolean validate_email(String email){

        email = email.trim();
        EmailValidator email_validator = EmailValidator.getInstance();
        if(email_validator.isValid(email))
            return false;
        else {
            Email_editText.setError(getResources().getString(R.string.register_email_address_edittext_error));
            return true;
        }
    }

    private void display_date_picker(){


        DatePickerDialog date_picker_dialog = new DatePickerDialog(
                this.context,
                this,
                Calendar.getInstance().get(Calendar.YEAR),
                Calendar.getInstance().get(Calendar.MONTH),
                Calendar.getInstance().get(Calendar.DAY_OF_MONTH)
        );

        date_picker_dialog.show();
    }

    private boolean validate_birthday(String date_of_birth){

        Calendar calendar = Calendar.getInstance();
        SimpleDateFormat parser = new SimpleDateFormat("MM/dd/yyyy", Locale.getDefault());

        Date birth_date = null;
        try {
            birth_date = parser.parse(date_of_birth);
            calendar.setTime(birth_date);

        }catch (ParseException parse_error){

            parse_error.printStackTrace();
            DateOfBirth_editText.setError(getResources().getString(R.string.register_date_of_birthday_edittext_error));
            return true;
        }
        
        date_of_birth_ms = String.valueOf(calendar.getTimeInMillis());
        int birth_year = calendar.get(Calendar.YEAR);
        int birth_month = calendar.get(Calendar.MONTH) + 1;
        int birth_day = calendar.get(Calendar.DAY_OF_MONTH);

       // String current_time = Calendar.getInstance().getTime().toString();
        Date current_date = Calendar.getInstance().getTime();
        calendar.setTime(current_date);

        int current_year = calendar.get(Calendar.YEAR);
        int current_month = calendar.get(Calendar.MONTH) + 1;
        int current_day = calendar.get(Calendar.DAY_OF_MONTH);

        int age = (current_year - birth_year);
        
        if(age < 18){
            DateOfBirth_editText.setError(getResources().getString(R.string.register_age_requirement_edittext_error));
            Toast.makeText(this.context, getResources().getString(R.string.register_age_requirement_edittext_error), Toast.LENGTH_LONG).show();
            return true;
        }

        if(age == 18){

            if(current_month < birth_month){
                
                DateOfBirth_editText.setError(getResources().getString(R.string.register_age_requirement_edittext_error));
                Toast.makeText(this.context, getResources().getString(R.string.register_age_requirement_edittext_error), Toast.LENGTH_LONG).show();
                return true;

            }

            if(current_month == birth_month){

                if(current_day < birth_day){
                    
                    DateOfBirth_editText.setError(getResources().getString(R.string.register_age_requirement_edittext_error));
                    Toast.makeText(this.context, getResources().getString(R.string.register_age_requirement_edittext_error), Toast.LENGTH_LONG).show();
                    return true;

                }

            }

        }


        return false;
    }

    @Override
    public void onDateSet(DatePicker view, int year, int month, int day_of_month) {

        String birth_day = (month + 1) + "/" + day_of_month + "/" + year;
        DateOfBirth_editText.setText(birth_day);
    }

    @Override
    public void onStart() {
        super.onStart();


    }

    @Override
    public void onResume() {
        super.onResume();


        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onPause() {
        super.onPause();

       // Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onSaveInstanceState(@NonNull Bundle out_state) {
        super.onSaveInstanceState(out_state);

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
