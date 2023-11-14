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
import android.widget.ImageView;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.annotation.NonNull;
import androidx.annotation.Nullable;
import androidx.fragment.app.DialogFragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.LinearLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.viewmodels.BlockedUsersDialogViewModel;

import java.util.Objects;

public class BlockedUsersDialog extends DialogFragment {

    private static final String TAG = "MSG";
    private Context context;
    private BlockedUsersDialogViewModel blocked_users_dialog_view_model;
    private  RecyclerView blocked_users_recycler_view;

    @Override
    public void onAttach(@NonNull Context context) {
        super.onAttach(context);

        this.context = context;
    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        blocked_users_dialog_view_model = new ViewModelProvider(this).get(BlockedUsersDialogViewModel.class);
    }

    @Nullable
    @Override
    public View onCreateView(@NonNull LayoutInflater inflater, @Nullable ViewGroup container, @Nullable Bundle savedInstanceState) {

        View view = inflater.inflate(R.layout.dialog_blocked_users, container, false);

        Objects.requireNonNull(Objects.requireNonNull(getDialog()).getWindow()).setBackgroundDrawable(new ColorDrawable(Color.TRANSPARENT));
        LinearLayout blocked_users_section_linear_layout = view.findViewById(R.id.blocked_users_section_linear_layout_id);

        ImageView blocked_users_dialog_clear_image_view = view.findViewById(R.id.blocked_users_dialog_clear_image_imageView_id);
        blocked_users_dialog_clear_image_view.setOnClickListener(v -> dismiss());

        blocked_users_recycler_view = view.findViewById(R.id.blocked_users_recycleView_id);
        LinearLayoutManager linear_layout_manager = new LinearLayoutManager(this.context);
        linear_layout_manager.setStackFromEnd(true);
        blocked_users_recycler_view.setLayoutManager(linear_layout_manager);
        blocked_users_recycler_view.setHasFixedSize(true);

        TextView no_blocked_users_available_text_view = view.findViewById(R.id.no_blocked_users_available_textView_id);
        ProgressBar blocked_users_progress_bar = view.findViewById(R.id.blocked_users_progress_bar_circular_id);
        blocked_users_progress_bar.setVisibility(View.VISIBLE);
        blocked_users_dialog_view_model.query_blocked_users(this.context, blocked_users_section_linear_layout, blocked_users_recycler_view, no_blocked_users_available_text_view, blocked_users_progress_bar);

        return view;
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
        Log.d(TAG, "onDestroyView");
        blocked_users_recycler_view.setAdapter(null);
    }
}

