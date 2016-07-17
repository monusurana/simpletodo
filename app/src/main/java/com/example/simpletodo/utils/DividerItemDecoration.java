package com.example.simpletodo.utils;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Canvas;
import android.graphics.drawable.Drawable;
import android.support.v4.content.ContextCompat;
import android.support.v7.widget.RecyclerView;
import android.util.TypedValue;
import android.view.View;

/**
 * Created by monusurana on 7/15/16.
 */
public class DividerItemDecoration extends RecyclerView.ItemDecoration {

    private static final int[] ATTRS = new int[]{android.R.attr.listDivider};

    private Drawable mDivider;
    private int mPaddingDp;

    /**
     * Default divider will be used
     */
    public DividerItemDecoration(Context context) {
        final TypedArray styledAttributes = context.obtainStyledAttributes(ATTRS);
        mDivider = styledAttributes.getDrawable(0);
        styledAttributes.recycle();
    }

    /**
     * Custom divider will be used
     */
    public DividerItemDecoration(Context context, int resId, int paddingDp) {
        mDivider = ContextCompat.getDrawable(context, resId);
        mPaddingDp = paddingDp;
    }

    @Override
    public void onDraw(Canvas c, RecyclerView parent, RecyclerView.State state) {
        int left = (int) TypedValue.applyDimension(TypedValue.COMPLEX_UNIT_DIP, mPaddingDp, parent.getResources().getDisplayMetrics());
        int right = parent.getWidth() - left;

        int childCount = parent.getChildCount();
        for (int i = 1; i < childCount ; i++) {
            View child = parent.getChildAt(i);

            RecyclerView.LayoutParams params = (RecyclerView.LayoutParams) child.getLayoutParams();

            int top = child.getBottom() + params.bottomMargin;
            int bottom = top + mDivider.getIntrinsicHeight();

            mDivider.setBounds(left, top, right, bottom);
            mDivider.draw(c);
        }
    }
}
