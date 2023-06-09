package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;
import android.content.Context;
import android.widget.LinearLayout;
import android.widget.ProgressBar;
import android.widget.TextView;

import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.syncadapters.czar.exchange.repositories.BlockedUsersRepository;

public class BlockedUsersDialogViewModel extends AndroidViewModel {

    private final BlockedUsersRepository blocked_users_repository;

    public BlockedUsersDialogViewModel(Application application){
        super(application);

        blocked_users_repository = BlockedUsersRepository.getInstance();


    }

    public void query_blocked_users(Context context, LinearLayout blocked_users_section_linear_layout, RecyclerView blocked_users_recycler_view,
                                    TextView no_blocked_users_available_text_view, ProgressBar blocked_users_progress_bar){

        blocked_users_repository.query_blocked_users(context, blocked_users_section_linear_layout, blocked_users_recycler_view,
                no_blocked_users_available_text_view, blocked_users_progress_bar);
    }

}
