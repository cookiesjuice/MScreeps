package com.cookiesjuice.mscreeps;

import androidx.appcompat.app.AppCompatActivity;
import androidx.appcompat.widget.Toolbar;
import androidx.fragment.app.Fragment;
import androidx.fragment.app.FragmentManager;

import android.os.Bundle;

import com.cookiesjuice.mscreeps.ui.SetTokenFragment;
import com.cookiesjuice.mscreeps.ui.TabFragment;

public class MainActivity extends AppCompatActivity {

    private Manager manager;
    private Fragment currentFragment;
    private FragmentManager fragmentManager;
    private Fragment tabFragment;

    private final int TAB_OVERVIEW = 0;
    private final int TAB_CONSOLE = 1;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);

        fragmentManager = getSupportFragmentManager();
        manager = new Manager(this);

        tabFragment = new TabFragment(this);
        fragmentManager.beginTransaction().add(R.id.fragmentContiner, tabFragment).commit();

        if(manager.getToken().length() == 0){
            SetTokenFragment setTokenFragment = new SetTokenFragment(this);
            launchFragment(setTokenFragment);
        }
    }

    public Manager getManager(){
        return manager;
    }

    public void launchFragment(Fragment fragment){
        fragmentManager.beginTransaction().hide(tabFragment).commit();
        currentFragment = fragment;
        fragmentManager.beginTransaction().
                setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).
                add(R.id.fragmentContiner, fragment).commit();
    }

    public void deleteFragment(Fragment fragment){
        fragmentManager.beginTransaction().show(tabFragment).commit();
        fragmentManager.beginTransaction().
                setCustomAnimations(android.R.animator.fade_in, android.R.animator.fade_out).
                remove(fragment).commit();
        currentFragment = null;
    }

    @Override
    public void onBackPressed() {
        if(currentFragment == null){
            super.onBackPressed();
        }else if(currentFragment instanceof SetTokenFragment){
            ((SetTokenFragment) currentFragment).done();
        }else{
            deleteFragment(currentFragment);
        }
    }
}
