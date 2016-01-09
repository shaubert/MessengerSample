package com.shaubert.liftago.util;

import android.content.Context;
import android.content.res.Resources;
import android.util.TypedValue;

public class Sizes {

    public static int dpToPx(Context context, float dp) {
        Resources r = context.getResources();
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_DIP, dp, r.getDisplayMetrics()) + 0.5f);
    }

    public static int spToPx(Context context, float sp) {
        Resources r = context.getResources();
        return (int) (TypedValue.applyDimension(
                TypedValue.COMPLEX_UNIT_SP, sp, r.getDisplayMetrics()) + 0.5f);
    }

}
