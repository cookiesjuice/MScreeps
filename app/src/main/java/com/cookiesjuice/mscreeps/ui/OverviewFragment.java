package com.cookiesjuice.mscreeps.ui;

import android.os.Bundle;
import android.os.Handler;
import android.os.Looper;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.ListView;
import android.widget.TableLayout;
import android.widget.TableRow;

import androidx.fragment.app.Fragment;

import com.cookiesjuice.mscreeps.DataLine;
import com.cookiesjuice.mscreeps.MainActivity;
import com.cookiesjuice.mscreeps.OverviewThread;
import com.cookiesjuice.mscreeps.R;

import org.json.JSONException;
import org.json.JSONObject;

import java.util.ArrayList;
import java.util.Iterator;
import java.util.List;
import java.util.Observable;
import java.util.Observer;

public class OverviewFragment extends Fragment implements Observer {

    private MainActivity activity;
    private OverviewThread thread;
    private TableLayout table;

    public OverviewFragment(MainActivity activity){
        this.activity = activity;
        this.thread = new OverviewThread(activity);
        Observable threadTarget = this.thread.getTarget();
        threadTarget.addObserver(this);
    }

    public View onCreateView(LayoutInflater inflater, ViewGroup container, Bundle savedInstanceBundle) {
        View view = inflater.inflate(R.layout.fragment_overview, container, false);
        table = view.findViewById(R.id.listTable);
        drawLoadingTable(table);
        try{
            thread.start();
        }catch (Exception e){
            e.printStackTrace();
        }

        return view;
    }



    @Override
    public void update(Observable o, Object arg) {
        String s = (String)arg;
        System.out.println("Received " + s);
        try{
            JSONObject object = new JSONObject(s);
            if(object.has("error")){
                return;
            }
            Object data = object.get("data");
            ArrayList<String[]> list = new ArrayList<>();
            if(data == JSONObject.NULL){
                activity.runOnUiThread(() -> drawEmptyTable(table));
                return;
            }
            data = new JSONObject((String) data);
            for (Iterator<String> it = ((JSONObject) data).keys(); it.hasNext(); ) {
                String key = it.next();
                String[] kv = new String[2];
                kv[0] = key;
                kv[1] = ((JSONObject) data).get(key).toString();
                list.add(kv);
            }
            activity.runOnUiThread(() -> drawTable(table, list));
        }catch (JSONException e){
            e.printStackTrace();;
        }
    }

    private void drawTable(TableLayout table, List<String[]> list){
        table.removeAllViews();
        for(String[] item : list){
            if(item.length != 2){
                continue;
            }
            table.addView(new DataLine(getContext(), item[0], item[1]));
        }
    }

    private void drawLoadingTable(TableLayout table){
        table.removeAllViews();
        table.addView(new DataLine(getContext(), getString(R.string.loading), ""));
    }

    private void drawEmptyTable(TableLayout table){
        table.removeAllViews();
        table.addView(new DataLine(getContext(), getString(R.string.no_data), ""));
    }

}
