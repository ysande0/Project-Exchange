package com.syncadapters.czar.exchange.adapters;


import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentActivity;
import androidx.viewpager2.adapter.FragmentStateAdapter;

import java.util.ArrayList;
import java.util.List;

public class ViewPagerAdapter extends FragmentStateAdapter {

    private final List<Fragment> fragment_list = new ArrayList<>();
    @SuppressWarnings("MismatchedQueryAndUpdateOfCollection")
    private final List<String> fragment_tab_title_list = new ArrayList<>();

    public ViewPagerAdapter(@NonNull  FragmentActivity fragment_activity){
        super(fragment_activity);

    }

    public void addFragment(Fragment fragment, String title){
        fragment_list.add(fragment);
        fragment_tab_title_list.add(title);

    }


    @NonNull
    @Override
    public Fragment createFragment(int position) {
        return fragment_list.get(position);
    }

    @Override
    public int getItemCount() {
        return fragment_list.size();
    }
}
