package com.zetzaus.dragandraw;

import android.content.Context;
import android.util.AttributeSet;
import android.view.View;

import androidx.annotation.Nullable;

public class BoxDrawingView extends View {

    /**
     * Creates a <code>BoxDrawingView</code>
     *
     * @param context the context.
     */
    public BoxDrawingView(Context context) {
        this(context, null);
    }

    /**
     * Creates a <code>BoxDrawingView</code> with attributes.
     *
     * @param context the context.
     * @param attrs   the attributes.
     */
    public BoxDrawingView(Context context, @Nullable AttributeSet attrs) {
        super(context, attrs);
    }
}
