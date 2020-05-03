package com.zetzaus.dragandraw;

import android.content.Context;
import android.graphics.Canvas;
import android.graphics.Paint;
import android.graphics.PointF;
import android.os.Bundle;
import android.os.Parcelable;
import android.util.AttributeSet;
import android.util.Log;
import android.view.MotionEvent;
import android.view.View;

import java.util.ArrayList;
import java.util.List;

import androidx.annotation.Nullable;

/**
 * Custom view that allows drawing rectangles by touch and drag.
 */
public class BoxDrawingView extends View {

    private static final String TAG = BoxDrawingView.class.getSimpleName();
    private static final String STATE_PARENT = "parent parcelable";
    private static final String STATE_BOXES = "state boxes";

    private Box mCurrentBox;
    private List<Box> mBoxList = new ArrayList<>();
    private int mRotationPointerId = -1;

    private Paint mBackgroundPaint;
    private Paint mBoxPaint;

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

        mBoxPaint = new Paint();
        mBoxPaint.setColor(0x22ff0000);

        mBackgroundPaint = new Paint();
        mBackgroundPaint.setColor(0xfff8efe0);
    }

    /**
     * Draw boxes to the screen.
     *
     * @param canvas the canvas to draw.
     */
    @Override
    protected void onDraw(Canvas canvas) {
        super.onDraw(canvas);
        // Draw the background
        canvas.drawPaint(mBackgroundPaint);

        //Draw the boxes
        for (Box box : mBoxList) {
            float left = Math.min(box.getCurrent().x, box.getOrigin().x);
            float right = Math.max(box.getCurrent().x, box.getOrigin().x);
            float top = Math.min(box.getCurrent().y, box.getOrigin().y);
            float bottom = Math.max(box.getCurrent().y, box.getOrigin().y);
            canvas.save();
            canvas.rotate(box.getRotation(), box.getCenterX(), box.getCenterY());
            canvas.drawRect(left, top, right, bottom, mBoxPaint);
            canvas.restore();
        }
    }

    /**
     * Returns the View's state.
     *
     * @return the View's state.
     */
    @Nullable
    @Override
    protected Parcelable onSaveInstanceState() {
        Bundle bundle = new Bundle();
        // Save the superclass' state
        bundle.putParcelable(STATE_PARENT, super.onSaveInstanceState());
        // Save the boxes
        bundle.putSerializable(STATE_BOXES, (ArrayList) mBoxList);
        return bundle;
    }

    /**
     * Restores the View's state.
     *
     * @param state the saved state.
     */
    @Override
    protected void onRestoreInstanceState(Parcelable state) {
        Bundle bundle = (Bundle) state;
        // Restore superclass' state
        super.onRestoreInstanceState(bundle.getParcelable(STATE_PARENT));
        // Restore the boxes
        mBoxList = (ArrayList) bundle.getSerializable(STATE_BOXES);
    }

    /**
     * Turns touch events into rectangles.
     *
     * @param event the touch event.
     * @return true (handled).
     */
    @Override
    public boolean onTouchEvent(MotionEvent event) {
        PointF touchCoordinate = new PointF(event.getX(), event.getY());
        String action = "";
        switch (event.getActionMasked()) {
            case MotionEvent.ACTION_DOWN:
                action = "Action Down";
                // Start drawing a new box
                mCurrentBox = new Box(touchCoordinate);
                mBoxList.add(mCurrentBox);
                break;
            case MotionEvent.ACTION_MOVE:
                action = "Action Move";
                // Change current coordinate
                if (mCurrentBox != null) {
                    if (event.getPointerCount() == 1) {
                        // Set the rectangle size
                        mCurrentBox.setCurrent(touchCoordinate);
                    } else {
                        // Get the indexes
                        int secondIndex = (event.getPointerId(0) == mRotationPointerId) ? 0 : 1;
                        touchCoordinate.set(new PointF(event.getX(secondIndex), event.getY(secondIndex)));
                        mCurrentBox.setEndRotationPoint(touchCoordinate);
                    }
                    invalidate();
                }
                break;
            case MotionEvent.ACTION_UP:
                action = "Action Up";
                // Stop drawing
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_CANCEL:
                action = "Action Cancel";
                mCurrentBox = null;
                break;
            case MotionEvent.ACTION_POINTER_DOWN:
                action = "Action pointer down";
                mRotationPointerId = event.getPointerId(event.getActionIndex());
                touchCoordinate.set(new PointF(event.getX(event.getActionIndex()), event.getY(event.getActionIndex())));
                if (mCurrentBox != null) {
                    mCurrentBox.setStartRotationPoint(touchCoordinate);
                }
                break;
            case MotionEvent.ACTION_POINTER_UP:
                action = "Action pointer up";
                touchCoordinate.set(new PointF(event.getX(event.getActionIndex()), event.getY(event.getActionIndex())));
                if (mCurrentBox != null) {
                    mCurrentBox.setEndRotationPoint(touchCoordinate);
                }
                mRotationPointerId = -1;
                break;
        }

        Log.i(TAG, action + " at (" + touchCoordinate.x + "," + touchCoordinate.y +
                ") by pointer " + event.getPointerId(0));

        return true;
    }
}
