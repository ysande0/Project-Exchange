package com.syncadapters.czar.exchange.fragments;

import android.content.Context;
import android.content.pm.ActivityInfo;
import android.os.Bundle;
import android.os.Handler;
import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.FrameLayout;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.fragment.app.Fragment;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.RecyclerView;

import com.bumptech.glide.Glide;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.adapters.ConversationsEntryAdapter;
import com.syncadapters.czar.exchange.datamodels.ConversationEntry;
import com.syncadapters.czar.exchange.datamodels.Message;
import com.syncadapters.czar.exchange.datamodels.MessageRead;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.enums.UserInterface;
import com.syncadapters.czar.exchange.viewmodels.ConversationsFragmentViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.greenrobot.eventbus.ThreadMode;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class ConversationsFragment extends Fragment {

    private static final String TAG = "OUT";

    private Context context;
    private  RecyclerView conversations_entry_recycle_view;
    @SuppressWarnings("unused")
    private ConversationsEntryAdapter conversations_entry_adapter;
    private ConversationsFragmentViewModel conversations_fragment_view_model;
    private ConversationEntry conversation_entry;
    private static boolean should_reload_messages = true;
    @SuppressWarnings("CanBeFinal")
    private Handler main_handler = new Handler();

    @Override
    public void onAttach(@NotNull Context context){
        super.onAttach(context);

        Log.d(TAG, "ConversationsFragment: onAttach");
        this.context = context;

    }

    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "ConversationsFragment: onCreate");

        App app = ((App) this.context.getApplicationContext());
        app.set_conversation_fragment_foreground(true);
        app.set_home_fragment_foreground(false);
        app.set_inventory_fragment_foreground(false);
        app.set_software_profile_dialog_foreground(false);

        should_reload_messages = false;
        //conversations_fragment_view_model = ViewModelProviders.of(getActivity()).get(ConversationsFragmentViewModel.class);
        conversations_fragment_view_model = new ViewModelProvider(this).get(ConversationsFragmentViewModel.class);

        conversation_entry = new ConversationEntry();
        conversation_entry.current_user.id = UserSettings.get_user_id(this.context.getApplicationContext());
        conversation_entry.current_user.first_name = UserSettings.get_user_first_name(this.context.getApplicationContext());
        conversation_entry.current_user.user_image_thumbnail_url = UserSettings.get_user_profile_image_thumbnail_url(this.context.getApplicationContext());
        conversation_entry.current_user.user_image_full_url = UserSettings.get_user_profile_image_full_url(this.context.getApplicationContext());

     //  cancel_notifications();
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle onSaveInstanceState){

        View view = inflater.inflate(R.layout.fragment_conversations, container, false);

        Log.d(TAG, "ConversationsFragment: onCreateView");
        Log.d(TAG, "ConversationsFragment pid: " + android.os.Process.myPid());
        conversations_entry_recycle_view = view.findViewById(R.id.conversations_recycleView_id);
        TextView no_messages_available_textView = view.findViewById(R.id.no_messages_available_textView_id);
        FrameLayout conversations_fragment_frame_layout = view.findViewById(R.id.conversations_fragment_framelayout_id);
       // ConversationsFragment conversations_fragment = new ConversationsFragment();
        conversations_fragment_view_model.load_recent_messages(this.context, Glide.with(this), conversations_fragment_frame_layout, conversations_entry_recycle_view, no_messages_available_textView, conversations_entry_adapter,
                UserInterface.CONVERSATIONS_FRAGMENT, conversation_entry);

        EventBus.getDefault().register(this);

        return view;
    }

    @Override
    public void onStart() {
        super.onStart();

        Log.d(TAG, "ConversationsFragment: onStart");


    }


    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "ConversationsFragment: onResume");

        if(should_reload_messages)
            conversations_fragment_view_model.update_conversation_adapter(main_handler);




        requireActivity().setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(Message message){

        conversations_entry_recycle_view.smoothScrollToPosition(0);
        conversations_fragment_view_model.update_conversation_entry(message);

    }

    @SuppressWarnings("unused")
    @Subscribe(threadMode = ThreadMode.MAIN)
    public void onEvent(MessageRead message_read){

        conversations_fragment_view_model.update_message_received(message_read);

    }




    public void onSaveInstanceState(@NotNull Bundle out_state){
        super.onSaveInstanceState(out_state);

        Log.d(TAG, "ConversationsFragment: onSaveInstanceState");


    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "ConversationsFragment: onPause");
       // Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);

    }

    @Override
    public void onStop() {
        super.onStop();

        Log.d(TAG, "ConversationsFragment: onStop");

        should_reload_messages = true;


    }

    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "ConversationsFragment: onDestroyView");

        EventBus.getDefault().unregister(this);
        conversations_fragment_view_model.cancel_load_recent_message();
        conversations_entry_adapter = null;
        conversations_entry_recycle_view.setAdapter(null);
    }

    @Override
    public void onDestroy() {
        super.onDestroy();

       // app.set_conversation_fragment_foreground(false);
        Log.d(TAG, "ConversationsFragment: onDestroy");

    }



    @Override
    public void onDetach() {
        super.onDetach();

        Log.d(TAG, "ConversationsFragment: onDetach");
    }
}
