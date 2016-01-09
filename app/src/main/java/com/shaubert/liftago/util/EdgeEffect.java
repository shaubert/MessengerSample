package com.shaubert.liftago.util;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.PorterDuff;
import android.graphics.drawable.Drawable;
import com.shaubert.liftago.R;

public class EdgeEffect {

    private static boolean FAIL = false;

    public static void brandGlowEffectFromThemeIfNeeded(Context context) {
        if (Versions.hasLollipopApi() || FAIL) return;

        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[] { R.attr.colorPrimary });
        int color = typedArray.getColor(0, Color.TRANSPARENT);
        typedArray.recycle();

        if (color != Color.TRANSPARENT) {
            brandGlowEffect(context, color);
        } else {
            FAIL = true;
        }
    }

    public static void brandGlowEffect(Context context, int brandColor) {
        if (Versions.hasLollipopApi() || FAIL) return;

        boolean failed = false;
        //glow
        try {
            int glowDrawableId = context.getResources().getIdentifier("overscroll_glow", "drawable", "android");
            Drawable androidGlow = context.getResources().getDrawable(glowDrawableId);
            if (androidGlow != null) androidGlow.setColorFilter(brandColor, PorterDuff.Mode.SRC_IN);
            failed = false;
        } catch (Exception ignored) {
            failed = true;
        }

        //edge
        try {
            int edgeDrawableId = context.getResources().getIdentifier("overscroll_edge", "drawable", "android");
            Drawable androidEdge = Images.getDrawable(context, edgeDrawableId);
            androidEdge.setColorFilter(brandColor, PorterDuff.Mode.SRC_IN);
            failed = false;
        } catch (Exception ignored) {
        }

        FAIL = failed;
    }

}
