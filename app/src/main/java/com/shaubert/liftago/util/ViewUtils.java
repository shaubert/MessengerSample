package com.shaubert.liftago.util;

import android.view.View;
import android.view.ViewGroup;

public class ViewUtils {

    public static int getChildsSumHeight(View parent) {
        int height = 0;
        if (parent instanceof ViewGroup) {
            ViewGroup viewGroup = (ViewGroup) parent;
            for (int j = 0; j < viewGroup.getChildCount(); j++) {
                View view = viewGroup.getChildAt(j);
                height += view.getHeight();
                if (view.getLayoutParams() instanceof ViewGroup.MarginLayoutParams) {
                    ViewGroup.MarginLayoutParams params = (ViewGroup.MarginLayoutParams) view.getLayoutParams();
                    height += params.bottomMargin + params.topMargin;
                }
            }
        } else {
            height = parent.getHeight();
        }
        return height;
    }

}
