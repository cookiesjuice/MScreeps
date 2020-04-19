package com.cookiesjuice.mscreeps.ui;

import android.content.Context;
import android.os.Bundle;
import android.view.KeyEvent;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.view.inputmethod.EditorInfo;
import android.view.inputmethod.InputMethodManager;
import android.widget.EditText;
import android.widget.Toast;

import androidx.fragment.app.Fragment;

import com.cookiesjuice.mscreeps.HttpClient;
import com.cookiesjuice.mscreeps.MainActivity;
import com.cookiesjuice.mscreeps.R;

import java.io.IOException;

public class ConsoleFragment extends Fragment {
    private MainActivity activity;
    ConsoleFragment(MainActivity activity){
        this.activity = activity;
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle){
        View view = inflater.inflate(R.layout.fragment_console, container, false);
        EditText editText = view.findViewById(R.id.consoleEdit);
        editText.setOnEditorActionListener(((v, actionId, event) -> {
            if(actionId == EditorInfo.IME_ACTION_DONE){
                String text = editText.getText().toString();
                if(text.length() > 0){
                    send(text);
                }
                editText.setText("");
                return true;
            }
            return false;
        }));
        return view;
    }

    private String send(String message){
        System.out.println(message);
        try{
            HttpClient client = new HttpClient(activity);
            Runnable t = () -> {
                try {
                    System.out.println(client.console(message));
                } catch (IOException e) {
                    System.err.println(e.toString());
                }
            };
            new Thread(t).start();
            return message;
        }catch (Exception e){
            Toast.makeText(getContext(), e.toString(), Toast.LENGTH_LONG).show();
        }
        return "";
    }
}
