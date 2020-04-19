package com.cookiesjuice.mscreeps;

import android.content.Context;
import android.graphics.Typeface;
import android.widget.TableRow;
import android.widget.TextView;

public class DataLine extends TableRow {
    public DataLine(Context context, String key, String value) {
        super(context);
        TextView keyView = new TextView(context);
        keyView.setText(key);
        keyView.setPadding(10, 2, 10, 2);
        keyView.setTypeface(null, Typeface.BOLD);
        TextView valueView = new TextView(context);
        valueView.setText(value);
        valueView.setPadding(10, 2, 10, 2);
        //keyView.setWidth(this.getWidth() / 2);
        //valueView.setWidth(this.getWidth() / 2);
        this.addView(keyView);
        this.addView(valueView);
        this.setPadding(5, 5, 5, 5);
    }

}
