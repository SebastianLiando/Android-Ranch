package com.zetzaus.dragandraw;

import android.graphics.PointF;

/**
 * This class holds information of a rectangle object drawn on the screen.
 */
public class Box {
    private PointF mOrigin;
    private PointF mCurrent;
    private PointF mStartRotationPoint;
    private PointF mEndRotationPoint;

    /**
     * Creates a box.
     *
     * @param origin the starting point.
     */
    public Box(PointF origin) {
        mOrigin = origin;
        mCurrent = origin;
    }

    /**
     * Returns the box's starting point.
     *
     * @return the box's starting point.
     */
    public PointF getOrigin() {
        return mOrigin;
    }

    /**
     * Returns the box's current point.
     *
     * @return the box's current point.
     */
    public PointF getCurrent() {
        return mCurrent;
    }

    /**
     * Returns the x-coordinate of the box's center point.
     *
     * @return the x-coordinate of the box's center point.
     */
    public float getCenterX() {
        return getCenter(mOrigin.x, mCurrent.x);
    }

    /**
     * Returns the y-coordinate of the box's center point.
     *
     * @return the y-coordinate of the box's center point.
     */
    public float getCenterY() {
        return getCenter(mOrigin.y, mCurrent.y);
    }

    /**
     * Returns the center of 2 values.
     *
     * @param start the starting values.
     * @param end   the ending values.
     * @return the center of 2 values.
     */
    private float getCenter(float start, float end) {
        return 0.5f * (start + end);
    }

    /**
     * Sets the current point.
     *
     * @param current the current point.
     */
    public void setCurrent(PointF current) {
        mCurrent = current;
    }

    /**
     * Returns the rotation angle for the box.
     *
     * @return the rotation angle for the box.
     */
    public float getRotation() {
        if (mStartRotationPoint == null || mEndRotationPoint == null) return 0;

        PointF startPointRelative = getPointFCenterRelative(mStartRotationPoint);
        PointF endPointRelative = getPointFCenterRelative(mEndRotationPoint);

        double startDegree = (Math.atan2(startPointRelative.y, startPointRelative.x)) * 180 / Math.PI;
        double endDegree = (Math.atan2(endPointRelative.y, endPointRelative.x)) * 180 / Math.PI;

        return (float) (endDegree - startDegree);
    }

    /**
     * Returns the point relative to the center.
     *
     * @param pointF the point to be calculated.
     * @return the point relative to the center.
     */
    private PointF getPointFCenterRelative(PointF pointF) {
        PointF center = new PointF(getCenterX(), getCenterY());
        return new PointF(pointF.x - center.x, pointF.y - center.y);
    }

    /**
     * Sets the starting rotation point.
     *
     * @param startRotationPoint the starting rotation point.
     */
    public void setStartRotationPoint(PointF startRotationPoint) {
        mStartRotationPoint = startRotationPoint;
    }

    /**
     * Sets the ending rotation point.
     *
     * @param endRotationPoint the ending rotation point.
     */
    public void setEndRotationPoint(PointF endRotationPoint) {
        mEndRotationPoint = endRotationPoint;
    }
}
