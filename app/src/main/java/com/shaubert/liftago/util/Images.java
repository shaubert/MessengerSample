package com.shaubert.liftago.util;

import android.annotation.SuppressLint;
import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.*;
import android.graphics.drawable.BitmapDrawable;
import android.graphics.drawable.Drawable;
import android.graphics.drawable.GradientDrawable;
import android.support.annotation.NonNull;
import android.support.v4.graphics.drawable.DrawableCompat;
import android.util.StateSet;
import android.view.View;
import com.shaubert.liftago.App;
import com.shaubert.ui.dialogs.commons.Sizes;

public class Images {

    private static Paint badgeTextPaint;
    private static Rect tempBounds = new Rect();

    public static Drawable recolor(Drawable in, int colorFrom, int colorTo) {
        if (in instanceof BitmapDrawable) {
            Bitmap result = recolor(((BitmapDrawable) in).getBitmap(), colorFrom, colorTo);
            return new BitmapDrawable(App.get().getResources(), result);
        }
        return in;
    }

    public static Bitmap recolor(Bitmap in, int colorFrom, int colorTo) {
        if (in == null) {
            return null;
        }

        colorFrom &= 0x00FFFFFF;
        colorTo &= 0x00FFFFFF;

        int[] pixels = new int[in.getWidth() * in.getHeight()];
        in.getPixels(pixels, 0, in.getWidth(), 0, 0, in.getWidth(), in.getHeight());
        for (int i = 0; i < pixels.length; i++) {
            int color = pixels[i];
            if ((color & 0x00FFFFFF) == colorFrom) {
                pixels[i] = (color & 0xFF000000) | colorTo;
            }
        }
        return Bitmap.createBitmap(pixels, in.getWidth(), in.getHeight(), in.getConfig());
    }

    public static int setAlpha(int color, int alpha) {
        return (alpha << 24) | (color & 0x00FFFFFF);
    }

    @SuppressWarnings("deprecation")
    @SuppressLint("NewApi")
    public static void setBackground(View view, Drawable background) {
        if (Versions.hasJellyBeanApi()) {
            view.setBackground(background);
        } else {
            view.setBackgroundDrawable(background);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static Drawable getDrawable(Context context, int resId) {
        if (Versions.hasLollipopApi()) {
            return context.getDrawable(resId);
        } else {
            return context.getResources().getDrawable(resId);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static int getColor(Context context, int resId) {
        if (Versions.hasM()) {
            return context.getColor(resId);
        } else {
            return context.getResources().getColor(resId);
        }
    }

    @SuppressLint("NewApi")
    @SuppressWarnings("deprecation")
    public static ColorStateList getColorStateList(Context context, int resId) {
        if (Versions.hasM()) {
            return context.getColorStateList(resId);
        } else {
            return context.getResources().getColorStateList(resId);
        }
    }

    public static Drawable getWrappedDrawable(Context context, int resId) {
        return getWrappedDrawable(getDrawable(context, resId));
    }

    public static Drawable getWrappedDrawable(Drawable drawable) {
        return DrawableCompat.wrap(drawable.mutate());
    }

    public static Drawable getTintDrawable(Context context, int resId) {
        return getTintDrawable(context, getDrawable(context, resId));
    }

    public static Drawable getTintDrawable(Context context, Drawable drawable) {
        return getTintDrawable(drawable, ThemeUtils.getCheckableTintList(context));
    }

    public static Drawable getTintDrawable(Context context, int resId, int color) {
        return getTintDrawable(getDrawable(context, resId), color);
    }

    public static Drawable getTintDrawable(Drawable drawable, int color) {
        Drawable wrappedDrawable = getWrappedDrawable(drawable);
        DrawableCompat.setTint(wrappedDrawable, color);
        return wrappedDrawable;
    }

    public static Drawable getTintDrawable(Context context, int resId, ColorStateList colorStateList) {
        return getTintDrawable(getDrawable(context, resId), colorStateList);
    }

    public static Drawable getTintDrawable(Drawable drawable, ColorStateList colorStateList) {
        Drawable wrappedDrawable = getWrappedDrawable(drawable);
        DrawableCompat.setTintList(wrappedDrawable, colorStateList);
        return wrappedDrawable;
    }

    public static Drawable getDrawableWithPressedState(Context context, int resId, int colorPressed) {
        return getDrawableWithPressedState(getDrawable(context, resId), colorPressed);
    }

    public static Drawable getDrawableWithPressedState(Drawable drawable, int colorPressed) {
        return getTintDrawable(drawable, getPressedColorStateList(colorPressed, Color.TRANSPARENT));
    }

    @NonNull
    public static ColorStateList getPressedColorStateList(int colorPressed, int colorNormal) {
        int[][] states = new int[2][];
        states[0] = new int[] { android.R.attr.state_pressed };
        states[1] = StateSet.WILD_CARD;
        int[] colors = { colorPressed, colorNormal };
        return new ColorStateList(states, colors);
    }

    public static void setBackgroundColor(Drawable drawable, Integer color) {
        if (color == null) return;
        try {
            ((GradientDrawable) drawable).setColor(color);
        } catch (ClassCastException ignored) {
        }
    }

    public static Drawable getIconWithBadgeAbove(Context context, int iconResId, int badgeDrawableResId, int badgeCount) {
        Drawable drawable = getDrawable(context, iconResId);
        if (badgeCount > 0 && drawable.getIntrinsicWidth() > 0 && drawable.getIntrinsicHeight() > 0) {
            Bitmap resultBitmap = Bitmap.createBitmap(
                    drawable.getIntrinsicWidth(), drawable.getIntrinsicHeight(), Bitmap.Config.ARGB_8888);
            drawable.setBounds(0, 0, resultBitmap.getWidth(), resultBitmap.getHeight());
            String badgeStr = badgeCount > 99 ? "∞" : String.valueOf(badgeCount);

            if (badgeTextPaint == null) {
                badgeTextPaint = new Paint(Paint.ANTI_ALIAS_FLAG);
                badgeTextPaint.setColor(Color.WHITE);
                badgeTextPaint.setTextSize(Sizes.dpToPx(9, context));
                badgeTextPaint.setTypeface(Typeface.DEFAULT_BOLD);
            }

            badgeTextPaint.getTextBounds(badgeStr, 0, badgeStr.length(), tempBounds);
            int width = (int) badgeTextPaint.measureText(badgeStr);
            int height = tempBounds.height();
            int padding = Sizes.dpToPx(4, context);
            int size = Math.max(width, height) + padding * 2;

            Canvas canvas = new Canvas(resultBitmap);
            drawable.draw(canvas);
            Drawable badgeDrawable = getDrawable(context, badgeDrawableResId);
            badgeDrawable.setBounds(0, 0, size, size);
            badgeDrawable.draw(canvas);
            canvas.drawText(badgeStr, (size - width) / 2, (size + height) / 2, badgeTextPaint);

            return new BitmapDrawable(context.getResources(), resultBitmap);
        } else {
            return drawable;
        }
    }

    public static ColorFilter createGrayscaleColorFilter() {
        ColorMatrix matrix = new ColorMatrix();
        matrix.setSaturation(0);  //0 means grayscale
        return new ColorMatrixColorFilter(matrix);
    }

    public static ColorFilter createColorFilter(int color) {
        return new PorterDuffColorFilter(color, PorterDuff.Mode.SRC_ATOP);
    }

    public static Bitmap getCircleBitmap(Bitmap bitmap) {
        final Bitmap output = Bitmap.createBitmap(bitmap.getWidth(),
                bitmap.getHeight(), Bitmap.Config.ARGB_8888);
        final Canvas canvas = new Canvas(output);

        final int color = Color.RED;
        final Paint paint = new Paint();
        final Rect rect = new Rect(0, 0, bitmap.getWidth(), bitmap.getHeight());
        final RectF rectF = new RectF(rect);

        paint.setAntiAlias(true);
        canvas.drawARGB(0, 0, 0, 0);
        paint.setColor(color);
        canvas.drawOval(rectF, paint);

        paint.setXfermode(new PorterDuffXfermode(PorterDuff.Mode.SRC_IN));
        canvas.drawBitmap(bitmap, rect, rect, paint);

        bitmap.recycle();

        return output;
    }

}
