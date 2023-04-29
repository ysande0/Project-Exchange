package com.syncadapters.czar.exchange.fragments;

import android.content.Context;
import android.content.Intent;
import android.content.pm.ActivityInfo;
import android.os.Bundle;

import android.util.Log;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.TextView;

import androidx.annotation.Nullable;
import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.ViewModelProvider;
import androidx.recyclerview.widget.GridLayoutManager;
import androidx.recyclerview.widget.RecyclerView;

import com.google.android.material.floatingactionbutton.FloatingActionButton;
import com.syncadapters.czar.exchange.App;
import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.SoftwareInventoryActivity;
import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.adapters.SoftwareAdapter;
import com.syncadapters.czar.exchange.viewmodels.InventoryFragmentViewModel;

import org.greenrobot.eventbus.EventBus;
import org.greenrobot.eventbus.Subscribe;
import org.jetbrains.annotations.NotNull;

import java.util.Objects;

public class InventoryFragment extends Fragment {


    /*
    *  NOTE: 8/31/2019 Current fragment loads titles.
    *
    * */

    private static final String TAG = "MSG";
    @SuppressWarnings("FieldCanBeLocal")
    private CoordinatorLayout inventory_fragment_coordinatorlayout;
    private RecyclerView inventory_recycler_view;
    private TextView no_software_available_textView;
    private Context context;
    private SoftwareAdapter software_adapter = null;
    private InventoryFragmentViewModel inventory_fragment_view_model;

    public void onAttach(@NotNull Context context){
        super.onAttach(context);

        this.context = context;
        Log.d(TAG, "InventoryFragment onAttach");

        EventBus.getDefault().register(this);

    }


    @Override
    public void onCreate(@Nullable Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);

        Log.d(TAG, "InventoryFragment onCreate");

        App app = ((App) this.context.getApplicationContext());
        app.set_conversation_fragment_foreground(false);
        app.set_home_fragment_foreground(false);
        app.set_inventory_fragment_foreground(true);
        app.set_software_profile_dialog_foreground(false);

        if(getArguments() != null){

            @SuppressWarnings("unused") boolean is_beginner = false;
            if(getArguments().containsKey("Beginner"))
                //noinspection UnusedAssignment
                is_beginner = getArguments().getBoolean("Beginner");

/*
            if(getArguments().containsKey("is_transaction_request")) {
                Log.d(TAG, "EXECUTED: is_transaction_request");
                is_request_transaction = getArguments().getBoolean("is_transaction_request");
            }

            if(getArguments().containsKey("transaction_request")) {

                Log.d(TAG, "EXECUTED: transaction_request");
                //transaction_request = getArguments().getParcelable("transaction_request");
                //EventBus.getDefault().register(this);
                //EventBus.getDefault().post(transaction_request);

               // getParentFragmentManager().setFragmentResultListener("", );


            }
*/
        }


    }


    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceState){

         View view = inflater.inflate(R.layout.fragment_inventory, container, false);

        Log.d(TAG, "InventoryFragment onCreateView");

        software_adapter = new SoftwareAdapter(this.context);
        FragmentManager fragment_manager = Objects.requireNonNull(getActivity()).getSupportFragmentManager();

        inventory_fragment_view_model = new ViewModelProvider(this).get(InventoryFragmentViewModel.class);
        inventory_fragment_view_model.set_inventory_fragment(this);

        no_software_available_textView = view.findViewById(R.id.no_software_available_textView_id);

        inventory_fragment_coordinatorlayout = view.findViewById(R.id.inventory_fragment_coordinatorlayout_id);

        FloatingActionButton floating_action_bar = view.findViewById(R.id.fab_id);
        floating_action_bar.setOnClickListener(v -> {

            Intent intent = new Intent(context, SoftwareInventoryActivity.class);
            startActivity(intent);

        });

      //  Log.d(TAG, "Number of Software entries " + software.size());
        inventory_recycler_view = view.findViewById(R.id.inventory_recycler_view_id);
        int NUM_COLUMNS = 3;
        inventory_recycler_view.setLayoutManager(new GridLayoutManager(this.context, NUM_COLUMNS));

        inventory_fragment_view_model.load_software(this.context, fragment_manager, inventory_fragment_coordinatorlayout, inventory_recycler_view,
                 no_software_available_textView, software_adapter);

         return view;


    }

    public void onSaveInstanceState(@NotNull Bundle out_state){
        super.onSaveInstanceState(out_state);

        /*
        out_state.putParcelableArrayList("software", this.software);
        out_state.putBoolean("is_transaction_request", is_request_transaction);
         */
    }

    @Override
    public void onStart() {
        super.onStart();
        Log.d(TAG, "InventoryFragment onStart");

    }


    @SuppressWarnings("unused")
    @Subscribe
    public void onEvent(Software software){

        Log.d(TAG, "[InventoryFragment] Event Title: " + software.title + " by " + software.game_developer);
        inventory_fragment_view_model.update_grid(inventory_recycler_view, no_software_available_textView, software_adapter, software);
    }



    @Override
    public void onResume() {
        super.onResume();

        Log.d(TAG, "InventoryFragment onResume");

        Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_PORTRAIT);
    }

    @Override
    public void onPause() {
        super.onPause();

        Log.d(TAG, "InventoryFragment onPause");
        //Objects.requireNonNull(getActivity()).setRequestedOrientation(ActivityInfo.SCREEN_ORIENTATION_FULL_SENSOR);
    }

    @Override
    public void onStop() {
        super.onStop();
        Log.d(TAG, "InventoryFragment onStop");


    }

    @SuppressWarnings("ConstantConditions")
    @Override
    public void onDestroyView() {
        super.onDestroyView();

        Log.d(TAG, "InventoryFragment onDestroyView");
        software_adapter = null;
        software_adapter = null;
        inventory_recycler_view.setAdapter(null);

    }

    @Override
    public void onDestroy() {
        super.onDestroy();

       // app.set_inventory_fragment_foreground(false);
        Log.d(TAG, "InventoryFragment onDestroy");
    }

    @Override
    public void onDetach() {
        super.onDetach();
        Log.d(TAG, "InventoryFragment onDetach");
        EventBus.getDefault().unregister(this);
    }

}
