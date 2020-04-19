package com.cookiesjuice.mscreeps.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.Menu;
import android.view.MenuInflater;
import android.view.MenuItem;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Button;
import android.widget.EditText;

import androidx.annotation.NonNull;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.viewpager.widget.ViewPager;

import com.cookiesjuice.mscreeps.MainActivity;
import com.cookiesjuice.mscreeps.R;
import com.google.android.material.tabs.TabLayout;

public class TabFragment extends Fragment {
    private MainActivity activity;
    public TabFragment(MainActivity activity){
        this.activity = activity;
    }

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle){
        View view = inflater.inflate(R.layout.fragment_tab, container, false);

        Toolbar toolbar = view.findViewById(R.id.toolbar);
        activity.setSupportActionBar(toolbar);
        setHasOptionsMenu(true);

        ViewPager viewPager = view.findViewById(R.id.viewPager);
        setupViewPager(viewPager);

        TabLayout tabs = view.findViewById(R.id.tabs);
        tabs.setupWithViewPager(viewPager);

        return view;
    }

    @Override
    public void onCreateOptionsMenu(Menu menu, MenuInflater inflater) {
        inflater.inflate(R.menu.menu_main, menu);
        super.onCreateOptionsMenu(menu,inflater);
    }

    @Override
    public boolean onOptionsItemSelected(@NonNull MenuItem item) {
        if(item.getItemId() == R.id.setting) {
            activity.launchFragment(new SetTokenFragment(activity));
        }else if(item.getItemId() == R.id.help){
            activity.launchFragment(new HelpFragment(activity));
        }
        return super.onOptionsItemSelected(item);
    }

    private void setupViewPager(ViewPager viewPager){
        SectionsPagerAdapter adapter = new SectionsPagerAdapter(activity.getSupportFragmentManager());

        adapter.addFragment(new OverviewFragment(activity), getString(R.string.tab0));
        adapter.addFragment(new ConsoleFragment(activity), getString(R.string.tab1));

        viewPager.setAdapter(adapter);
    }
}
