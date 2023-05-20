package com.syncadapters.czar.exchange.dialogs;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.graphics.Color;
import android.graphics.drawable.ColorDrawable;
import android.os.Bundle;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ImageButton;
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.Spinner;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.viewmodels.HardwareLibraryDialogViewModel;

import java.util.Objects;

public class HardwareLibraryDialog extends DialogFragment {

    private static final String TAG = "MSG";
    private Context context;
    private RecyclerView hardware_platform_recycle_view;
    private HardwareLibraryDialogViewModel hardware_library_dialog_view_model;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        Log.d(TAG, "HardwareLibraryDialog onAttach");
        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "HardwareLibraryDialog onCreate");

       // hardware_library_dialog_view_model = ViewModelProviders.of(this).get(HardwareLibraryDialogViewModel.class);
        hardware_library_dialog_view_model = new ViewModelProvider(this).get(HardwareLibraryDialogViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        Log.d(TAG, "HardwareLibraryDialog onCreateView");

        App app = ((App) context.getApplicationContext());
        View view = inflater.inflate(R.layout.dialog_hardware_library, container, false);


        // noinspection ConstantConditions
        getDialog().getWindow().setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout hardware_display_section_linear_layout = view.findViewById(R.id.hardware_display_section_linear_layout_id);
        ImageView hardware_dialog_clear_imageView = view.findViewById(R.id.hardware_library_dialog_clear_image_imageView_id);
        hardware_dialog_clear_imageView.setOnClickListener(v -> dismiss());

        hardware_platform_recycle_view =  view.findViewById(R.id.hardware_platform_recycleView_id);
        Spinner hardware_platform_spinner =  view.findViewById(R.id.hardware_platform_spinner_id);
        ProgressBar progress_bar =  view.findViewById(R.id.hardware_progress_bar_circular_id);
        progress_bar.setVisibility(View.GONE);
        ImageButton add_hardware_platform_button =  view.findViewById(R.id.add_hardware_platform_button_id);
        TextView no_hardware_available_textView =  view.findViewById(R.id.no_hardware_available_textView_id);

        hardware_library_dialog_view_model.query_hardware(app, this.context, this, hardware_platform_recycle_view, hardware_platform_spinner,
                add_hardware_platform_button, progress_bar, hardware_display_section_linear_layout, no_hardware_available_textView);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "HardwareLibraryDialog onStart");
    }

    @Override
    public void onResume() {
        super.onResume();
        Log.d(TAG, "HardwareLibraryDialog onResume");

        /*
        Window window = getDialog().getWindow();
        if(window == null)
            return;

        WindowManager.LayoutParams window_params = window.getAttributes();
        window_params.width = 800;
        window_params.height = 800;

        window.setAttributes(window_params);

         */

        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);

    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "HardwareLibraryDialog onPause");

       // Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "HardwareLibraryDialog onStop");
    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();
        Log.d(TAG, "HardwareLibraryDialog onDestroy");

        hardware_platform_recycle_view.setAdapter(null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

        Log.d(TAG, "HardwareLibraryDialog onDestroyView");
    }

    @Override
    public void onDetach() {
        super.onDetach();

        Log.d(TAG, "HardwareLibraryDialog onDetach");
    }

}
