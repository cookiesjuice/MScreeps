package com.cookiesjuice.mscreeps.ui;

import android.os.Bundle;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.AdapterView;
import android.widget.ArrayAdapter;
import android.widget.Button;
import android.widget.EditText;
import android.widget.Spinner;
import android.widget.Toast;

import androidx.annotation.NonNull;
import androidx.fragment.app.Fragment;

import com.cookiesjuice.mscreeps.HttpClient;
import com.cookiesjuice.mscreeps.MainActivity;
import com.cookiesjuice.mscreeps.Manager;
import com.cookiesjuice.mscreeps.R;
import com.cookiesjuice.mscreeps.SocketClient;

import java.util.Objects;

public class SetTokenFragment extends Fragment {
    private MainActivity activity;
    public SetTokenFragment(@NonNull MainActivity activity){
        this.activity = activity;
    }
    private final boolean testing = true;

    @Override
    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle){
        View view = inflater.inflate(R.layout.fragment_set_token, container, false);
        EditText editToken = view.findViewById(R.id.tokenEdit);
        Manager manager = activity.getManager();
        Button save = view.findViewById(R.id.saveToken);
        Button done = view.findViewById(R.id.doneToken);

        editToken.setText(manager.getToken());

        final String EMPTY_TOKEN_ALERT = "Token cannot be empty";

        save.setOnClickListener( v -> {
            String token = editToken.getText().toString();
            if(token.length() == 0){
                Toast.makeText(getContext(),EMPTY_TOKEN_ALERT, Toast.LENGTH_SHORT).show();
            }else{
                manager.setToken(token);
            }

            // test
            Thread t = new Thread( () -> {
                try{
                    manager.setId(new HttpClient(activity).getUserId());
                }catch (Exception e){
                    e.printStackTrace();
                }

            });
            t.start();
        });

        done.setOnClickListener( v -> {
            String token = editToken.getText().toString();
            save.performClick();
            if(token.length() > 0){
                activity.deleteFragment(this);
            }
        });

        Spinner shard = view.findViewById(R.id.shardSelectSpinner);
        String selection = manager.getShard();
        ArrayAdapter<CharSequence> adapter =
                ArrayAdapter.createFromResource(getContext(), R.array.shards,
                        R.layout.support_simple_spinner_dropdown_item);
        adapter.setDropDownViewResource(R.layout.support_simple_spinner_dropdown_item);
        shard.setAdapter(adapter);
        shard.setSelection(adapter.getPosition(selection));

        shard.setOnItemSelectedListener(new AdapterView.OnItemSelectedListener() {
            @Override
            public void onItemSelected(AdapterView<?> parent, View view, int position, long id) {
                manager.setShard(parent.getItemAtPosition(position).toString());
            }

            @Override
            public void onNothingSelected(AdapterView<?> parent) {

            }
        });

        Button test = view.findViewById(R.id.test);
        if(!testing){
            test.setVisibility(View.INVISIBLE);
        }else{
            test.setVisibility(View.VISIBLE);
        }
        test.setOnClickListener( v -> {
            testFunction();
        });


        return view;
    }

    private void testFunction(){
        Thread t = new Thread(() -> {
            try{
                HttpClient client = new HttpClient(activity);
                System.out.println(client.readMemorySegment());
            }catch (Exception e){
                e.printStackTrace();
            }
        });
        t.start();

    }

    public void done(){
        try{
            Objects.requireNonNull(this.getView()).findViewById(R.id.doneToken).performClick();
        }catch (NullPointerException e){
            e.printStackTrace();
        }

    }

}
