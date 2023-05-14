package com.syncadapters.czar.exchange.asynctasks;

import android.app.Activity;
import android.content.Context;
import android.graphics.Color;
import android.os.AsyncTask;
import android.util.Log;
import android.view.View;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.recyclerview.widget.DefaultItemAnimator;
import androidx.recyclerview.widget.RecyclerView;

import com.syncadapters.czar.exchange.R;
import com.syncadapters.czar.exchange.activities.HomeActivity;
import com.syncadapters.czar.exchange.adapters.SoftwareAdapter;
import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.datamodels.UserSettings;
import com.syncadapters.czar.exchange.datautility.URL;
import com.syncadapters.czar.exchange.decoration.SpacesItemDecoration;
import com.syncadapters.czar.exchange.fragments.InventoryFragment;
import com.syncadapters.czar.exchange.roomdatabase.MySoftware;
import com.syncadapters.czar.exchange.roomdatabase.MySoftwareDao;

import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;

public class LoadSoftwareTask extends AsyncTask<Void, Void, ArrayList<Software>> {

    private static final String TAG = "MSG";

// --Commented out by Inspection START (1/9/2021 11:51 PM):
//    @SuppressWarnings("FieldCanBeLocal")
//    private final WeakReference<Context> context_weak_reference;
// --Commented out by Inspection STOP (1/9/2021 11:51 PM)
    private final WeakReference<Context> context_weak_reference;
    private WeakReference<InventoryFragment> inventory_fragment_weak_reference;
    private WeakReference<CoordinatorLayout> inventory_fragment_coordinatorlayout_weak_reference;
    private final WeakReference<RecyclerView> inventory_recycler_view_weak_reference;
    private final SoftwareAdapter software_adapter;
    private final MySoftwareDao my_software_dao;
    //private InventoryFragmentViewModel inventory_fragment_view_model;
    private FragmentManager fragment_manager;
    private WeakReference<TextView> no_software_available_textView_weak_reference;

    public LoadSoftwareTask(@SuppressWarnings("unused") Context context, MySoftwareDao my_software_dao,
                            RecyclerView inventory_recycler_view, SoftwareAdapter software_adapter){

        context_weak_reference = new WeakReference<>(context);
        this.my_software_dao = my_software_dao;
        this.software_adapter = software_adapter;
        this.inventory_recycler_view_weak_reference = new WeakReference<>(inventory_recycler_view);
    }


    public void set_no_software_available_textView(TextView no_software_available_textView){

        this.no_software_available_textView_weak_reference = new WeakReference<>(no_software_available_textView);
    }

/*
    public void set_inventory_management_view_model(InventoryFragmentViewModel inventory_fragment_view_model){

       this.inventory_fragment_view_model = inventory_fragment_view_model;
    }
*/
   public void set_fragment(InventoryFragment inventory_fragment){

       inventory_fragment_weak_reference = new WeakReference<>(inventory_fragment);

   }

   public void set_fragment_manager(FragmentManager fragment_manager){
        this.fragment_manager = fragment_manager;
   }

   public void set_inventory_fragment_coordinator_layout(CoordinatorLayout inventory_fragment_coordinatorlayout){

       this.inventory_fragment_coordinatorlayout_weak_reference = new WeakReference<>(inventory_fragment_coordinatorlayout);
   }


    @Override
    protected ArrayList<Software> doInBackground(Void... voids) {

        ArrayList<Software> softwares = new ArrayList<>();

        List<MySoftware> my_software = this.my_software_dao.query_all_software();

        for(int i = 0; i < my_software.size(); i++){

            /*
            if(my_software.isEmpty())
                break;
*/
            Software software = new Software();
            software.id = my_software.get(i).get_id();
            software.title = my_software.get(i).get_title();
            software.platform = my_software.get(i).get_platform();
            software.game_publisher = my_software.get(i).get_publisher();
            software.game_developer = my_software.get(i).get_developer();
            software.upc = my_software.get(i).get_upc();
            software.user_description = my_software.get(i).get_user_description();
            software.bitmap_name_thumbnail = my_software.get(i).get_remote_software_image_thumbnail_url();
            software.bitmap_name_full = my_software.get(i).get_remote_software_image_full_url();
            software.uid = my_software.get(i).get_software_uid();

            if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.ldpi_label))){

                software.software_image_full_url = URL.LDPI + software.bitmap_name_full;
                software.software_image_thumbnail_url = URL.LDPI + software.bitmap_name_thumbnail;

            }
            else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.mdpi_label))){

                software.software_image_full_url = URL.MDPI + software.bitmap_name_full;
                software.software_image_thumbnail_url = URL.MDPI + software.bitmap_name_thumbnail;

            }
            else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.hdpi_label))){

                software.software_image_full_url = URL.HDPI + software.bitmap_name_full;
                software.software_image_thumbnail_url = URL.HDPI + software.bitmap_name_thumbnail;

            }
            else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xhdpi_label))){

                software.software_image_full_url = URL.XHDPI + software.bitmap_name_full;
                software.software_image_thumbnail_url = URL.XHDPI + software.bitmap_name_thumbnail;

            }
            else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xxhdpi_label))){

                software.software_image_full_url = URL.XXHDPI + software.bitmap_name_full;
                software.software_image_thumbnail_url = URL.XXHDPI + software.bitmap_name_thumbnail;

            }
            else if(UserSettings.get_user_dpi(context_weak_reference.get()).equals(context_weak_reference.get().getResources().getString(R.string.xxxhdpi_label))){

                software.software_image_full_url = URL.XXXHDPI + software.bitmap_name_full;
                software.software_image_thumbnail_url = URL.XXXHDPI + software.bitmap_name_thumbnail;

            }

            Log.d(TAG, (i + 1) + ") " + software.software_image_full_url + "     " +  software.software_image_thumbnail_url);

            softwares.add(software);


        }

        return softwares;
    }

    @Override
    protected void onPostExecute(ArrayList<Software> softwares) {
        super.onPostExecute(softwares);

        Log.d(TAG, "[LoadSoftwareTask] onPostExecute");

        if ( context_weak_reference.get() instanceof HomeActivity) {
            Log.d(TAG, "[LoadSoftwareTask] is HomeActivity instance");

            Activity activity = (Activity)context_weak_reference.get();
            if ( activity.isDestroyed() ) {
                Log.d(TAG, "[LoadSoftwareTask] is destroyed");
                return;
            }
            else
                Log.d(TAG, "[LoadSoftwareTask] is NOT destroyed");

        }
        else
            Log.d(TAG, "[LoadSoftwareTask] is NOT HomeActivity instance");

        if(softwares.isEmpty()) {

            Log.d(TAG, "[LoadSoftwareTask] Library is Empty ");
            inventory_recycler_view_weak_reference.get().setVisibility(View.GONE);
            no_software_available_textView_weak_reference.get().setVisibility(View.VISIBLE);
            inventory_fragment_coordinatorlayout_weak_reference.get().setBackgroundColor(Color.GRAY);
          //  Objects.requireNonNull(inventory_fragment_weak_reference.get().getView()).setBackgroundColor(Color.GRAY);
        }
        else
            Log.d(TAG, "[LoadSoftwareTask] Library is NOT Empty ");

        if(no_software_available_textView_weak_reference.get() == null || inventory_recycler_view_weak_reference.get() == null) {
            Log.d(TAG, "[LoadSoftwareTask] no_software_available_textView_weak_reference is null or inventory_recycler_view_weak_reference is null");
            return;
        }
        else{
            Log.d(TAG, "[LoadSoftwareTask] no_software_available_textView_weak_reference is NOT null or inventory_recycler_view_weak_reference is NOT null");
         //   no_software_available_textView_weak_reference.get().setVisibility(View.GONE);
         //   inventory_recycler_view_weak_reference.get().setVisibility(View.VISIBLE);

        }


        for(int i = 0; i < softwares.size(); i++){

            Log.d(TAG, "[LoadSoftwareTask] ID: " + softwares.get(i).id + " " + softwares.get(i).title + " by " + softwares.get(i).game_developer);
        }

       // software_adapter = new SoftwareAdapter(this.context_weak_reference.get(), softwares);
        software_adapter.set_softwares(softwares);
        software_adapter.set_is_recipient_inventory(false);
        software_adapter.set_fragment_manager(fragment_manager);
        //inventory_fragment_view_model.set_software_adapter(software_adapter);
        inventory_recycler_view_weak_reference.get().setAdapter(software_adapter);

        inventory_recycler_view_weak_reference.get().setItemAnimator(new DefaultItemAnimator());
        inventory_recycler_view_weak_reference.get().addItemDecoration(new SpacesItemDecoration(10));



        fragment_manager.setFragmentResultListener("update_software", inventory_fragment_weak_reference.get(), (requestKey, result) -> {

            Log.d(TAG, "[LoadSoftwareTask] Proceeding to update......");
            boolean update_item = result.getBoolean("update_item");
            int position = result.getInt("position");
            Software software_item = result.getParcelable("software");
            if (update_item) {

                software_adapter.update(software_item, position);
                software_adapter.notifyDataSetChanged();
                Log.d(TAG, "SoftwareAdapter UPDATED");
            }

        });

        fragment_manager.setFragmentResultListener("delete_software", inventory_fragment_weak_reference.get(), (requestKey, result) -> {

            Log.d(TAG, "[LoadSoftwareTask] Proceeding to delete......");
            boolean delete_item = result.getBoolean("delete_item");
            Software software_item = result.getParcelable("software");
            if (delete_item) {

                software_adapter.delete(software_item);
                software_adapter.notifyDataSetChanged();

                if (software_adapter.getItemCount() == 0 && inventory_fragment_weak_reference.get().getView() != null) {

                    no_software_available_textView_weak_reference.get().setVisibility(View.VISIBLE);
                    inventory_recycler_view_weak_reference.get().setVisibility(View.GONE);
                    Objects.requireNonNull(inventory_fragment_weak_reference.get().getView()).setBackgroundColor(Color.GRAY);

                }

                Log.d(TAG, "SoftwareAdapter DELETED");
            }
        });


    }
}
