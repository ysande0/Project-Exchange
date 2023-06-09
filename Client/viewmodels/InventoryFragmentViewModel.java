package com.syncadapters.czar.exchange.viewmodels;

import android.app.Application;
import android.content.Context;
import android.widget.TextView;

import androidx.coordinatorlayout.widget.CoordinatorLayout;
import androidx.fragment.app.FragmentManager;
import androidx.lifecycle.AndroidViewModel;
import androidx.recyclerview.widget.RecyclerView;

import com.syncadapters.czar.exchange.adapters.SoftwareAdapter;
import com.syncadapters.czar.exchange.datamodels.Software;
import com.syncadapters.czar.exchange.fragments.InventoryFragment;
import com.syncadapters.czar.exchange.repositories.InventoryFragmentRepository;

public class InventoryFragmentViewModel extends AndroidViewModel {


    private final InventoryFragmentRepository inventory_fragment_repository;


    public InventoryFragmentViewModel(Application application){
        super(application);
      //  this.is_transaction_request = isRequestTransaction;

        inventory_fragment_repository = InventoryFragmentRepository.getInstance();

        inventory_fragment_repository.initialize_database(application);
        inventory_fragment_repository.set_context(application);

    }

    public void set_inventory_fragment(InventoryFragment inventory_fragment){
        inventory_fragment_repository.set_inventory_fragment(inventory_fragment);
    }

    public void load_software(Context context, FragmentManager fragment_manager, CoordinatorLayout inventory_fragment_coordinatorlayout,
                              RecyclerView inventory_recycler_view, TextView no_software_available_textView, SoftwareAdapter software_adapter){


        inventory_fragment_repository.load_software(context, fragment_manager, inventory_fragment_coordinatorlayout, inventory_recycler_view,  no_software_available_textView,
                software_adapter);
    }


    public void update_grid(RecyclerView inventory_recycler_view, TextView no_software_available_textView,
                            SoftwareAdapter software_adapter, Software software_item){

       inventory_fragment_repository.updated_grid(inventory_recycler_view, no_software_available_textView, software_adapter, software_item);
    }


}
