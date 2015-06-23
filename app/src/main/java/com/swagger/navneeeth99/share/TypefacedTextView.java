package com.swagger.navneeeth99.share;

import android.content.Context;
import android.graphics.Typeface;
import android.util.AttributeSet;
import android.widget.TextView;

/**
 * Created by yanch on 23/6/2015.
 */
public class TypefacedTextView extends TextView {

    public TypefacedTextView(Context context, AttributeSet attrs, int defStyle) {
        super(context, attrs, defStyle);
        init();
    }

    public TypefacedTextView(Context context, AttributeSet attrs) {
        super(context, attrs);
        init();
    }

    public TypefacedTextView(Context context) {
        super(context);
        init();
    }

    private void init() {
        if (!isInEditMode()) {
            Typeface tf = Typeface.createFromAsset(getContext().getAssets(), "heado.otf");
            setTypeface(tf);
        }
    }
}

