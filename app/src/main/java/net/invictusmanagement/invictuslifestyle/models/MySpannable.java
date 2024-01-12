package net.invictusmanagement.invictuslifestyle.models;

import android.graphics.Color;
import android.graphics.Typeface;
import android.text.TextPaint;
import android.text.style.ClickableSpan;
import android.view.View;

public class MySpannable extends ClickableSpan {

    private boolean isUnderline = true;

    /**
     * Constructor
     */
    public MySpannable(boolean isUnderline) {
        this.isUnderline = isUnderline;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        ds.setUnderlineText(true);
        ds.setTypeface(Typeface.create(Typeface.DEFAULT, Typeface.BOLD));
        ds.setColor(Color.parseColor("#022e6d"));
    }

    @Override
    public void onClick(View widget) {


    }
}
