package com.syncadapters.czar.exchange.repositories;

import android.app.Application;
import android.content.Context;
import android.graphics.Color;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.RecyclerView;

import com.syncadapters.czar.exchange.adapters.SoftwareAdapter;
import com.syncadapters.czar.exchange.asynctasks.LoadSoftwareTask;
import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.fragments.InventoryFragment;
import com.syncadapters.czar.exchange.roomdatabase.ExchangeDatabase;
import com.syncadapters.czar.exchange.roomdatabase.MySoftwareDao;

import java.lang.ref.WeakReference;

public class InventoryFragmentRepository {

    private static final String TAG = "MSG";
    private static InventoryFragmentRepository inventory_fragment_repository;
    private WeakReference<InventoryFragment> inventory_fragment_weak_reference;
    private MySoftwareDao my_software_dao;
    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private WeakReference<Context> context_weak_reference;

    public static InventoryFragmentRepository getInstance(){

        if(inventory_fragment_repository == null)
            inventory_fragment_repository = new InventoryFragmentRepository();

        return inventory_fragment_repository;
    }

    public void initialize_database(Application application){

        ExchangeDatabase exchange_database = ExchangeDatabase.get_database(application);
        my_software_dao = exchange_database.my_software_dao();

    }

    public void set_context(Context context){

       this.context_weak_reference = new WeakReference<>(context);
    }


    public void set_inventory_fragment(InventoryFragment inventory_fragment){
        //this.inventory_fragment = inventory_fragment;
        this.inventory_fragment_weak_reference = new WeakReference<>(inventory_fragment);
    }

    public void load_software(Context context, FragmentManager fragment_manager, CoordinatorLayout inventory_fragment_coordinatorlayout, RecyclerView inventory_recycler_view,
                              TextView no_software_available_textView, SoftwareAdapter software_adapter){



        LoadSoftwareTask load_software_task = new LoadSoftwareTask(context, this.my_software_dao, inventory_recycler_view, software_adapter);
        load_software_task.set_no_software_available_textView(no_software_available_textView);
        load_software_task.set_fragment_manager(fragment_manager);
        load_software_task.set_inventory_fragment_coordinator_layout(inventory_fragment_coordinatorlayout);
       // load_software_task.set_inventory_management_view_model(inventory_fragment_view_model);
        load_software_task.set_fragment(inventory_fragment_weak_reference.get());
        load_software_task.execute();

    }

    public void updated_grid(RecyclerView inventory_recycler_view, TextView no_software_available_textView,
            SoftwareAdapter software_adapter,Software software_item){

        WeakReference<RecyclerView> inventory_recycler_view_weak_reference = new WeakReference<>(inventory_recycler_view);
        WeakReference<TextView> no_software_available_textView_weak_reference = new WeakReference<>(no_software_available_textView);

        Log.d(TAG, "INSERTED!");
        Log.d(TAG, "[LoadSoftwareTask] Proceeding to Insert......");
        Log.d(TAG, "Before Number of Items: " + software_adapter.getItemCount());
        Log.d(TAG, "--> update grid [URL]: " + software_item.software_image_thumbnail_url);
        software_adapter.insert(software_item);
        software_adapter.notifyDataSetChanged();

        if(software_adapter.getItemCount() > 0 && inventory_fragment_weak_reference.get() != null){

                no_software_available_textView_weak_reference.get().setVisibility(View.GONE);
                inventory_recycler_view_weak_reference.get().setVisibility(View.VISIBLE);
                //noinspection ConstantConditions
                inventory_fragment_weak_reference.get().getView().setBackgroundColor(Color.WHITE);
                Log.d(TAG, "[InventoryFragment] UI updated [Inserted]");
            }

        Log.d(TAG, "After Number of Items: " + software_adapter.getItemCount());
        Log.d(TAG, "SoftwareAdapter INSERTED");

    }


}
