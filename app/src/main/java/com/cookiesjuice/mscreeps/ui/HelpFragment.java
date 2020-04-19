package com.cookiesjuice.mscreeps.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;

import androidx.fragment.app.Fragment;

import com.cookiesjuice.mscreeps.MainActivity;
import com.cookiesjuice.mscreeps.R;

public class HelpFragment extends Fragment {
    public HelpFragment(MainActivity activity){

    }


    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        View view = inflater.inflate(R.layout.fragment_help, container, false);
        return view;
    }

}
