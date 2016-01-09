package com.shaubert.liftago.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.graphics.drawable.Drawable;
import com.shaubert.liftago.R;

public class ThemeUtils {

    public static ColorStateList getPrimaryTextSelector(Context context) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(R.styleable.MainTheme);
        ColorStateList colorStateList = typedArray.getColorStateList(R.styleable.MainTheme_primaryTextSelector);
        typedArray.recycle();
        return colorStateList;
    }

    public static ColorStateList getSecondaryTextSelector(Context context) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(R.styleable.MainTheme);
        ColorStateList colorStateList = typedArray.getColorStateList(R.styleable.MainTheme_secondaryTextSelector);
        typedArray.recycle();
        return colorStateList;
    }

    public static int makeDisabledColor(int color) {
        return Color.argb(66, Color.red(color), Color.green(color), Color.blue(color));
    }

    public static int getDefaultSecondaryTextColor(Context context) {
        return getSecondaryTextSelector(context).getDefaultColor();
    }

    public static int getDefaultPrimaryTextColor(Context context) {
        return getPrimaryTextSelector(context).getDefaultColor();
    }

    public static int getDividerColor(Context context) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(R.styleable.MainTheme);
        int dividerColor = typedArray.getColor(R.styleable.MainTheme_dividerColor,
                Images.getColor(context, R.color.divider_light));
        typedArray.recycle();
        return dividerColor;
    }

    public static Drawable getSelectableItemBg(Context context) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[] { R.attr.selectableItemBackground });
        Drawable result = typedArray.getDrawable(0);
        typedArray.recycle();
        return result;
    }

    public static int getPrimaryColor(Context context) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[] { R.attr.colorPrimary});
        int color = typedArray.getColor(0, Images.getColor(context, R.color.primary));
        typedArray.recycle();
        return color;
    }

    public static int getPrimaryColorDark(Context context) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[] { R.attr.colorPrimaryDark});
        int color = typedArray.getColor(0, Images.getColor(context, R.color.primary_dark));
        typedArray.recycle();
        return color;
    }

    public static int getAccentColor(Context context) {
        TypedArray typedArray = context.getTheme().obtainStyledAttributes(new int[] { R.attr.colorAccent});
        int color = typedArray.getColor(0, Images.getColor(context, R.color.accent_active));
        typedArray.recycle();
        return color;
    }

    public static ColorStateList getControlColorSelector(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(null,
                new int[]{R.attr.colorControlNormal});
        ColorStateList colorStateList = typedArray.getColorStateList(0);
        typedArray.recycle();
        return colorStateList;
    }

    public static ColorStateList getCheckableTintList(Context context) {
        TypedArray res = context.getTheme().obtainStyledAttributes(R.styleable.MainTheme);
        int resId = res.getResourceId(R.styleable.MainTheme_checkableTintList, R.color.checkable_icon_tint_list_light);
        res.recycle();

        return Images.getColorStateList(context, resId);
    }

    public static ColorStateList getClickableTintList(Context context) {
        TypedArray res = context.getTheme().obtainStyledAttributes(R.styleable.MainTheme);
        int resId = res.getResourceId(R.styleable.MainTheme_clickableTintList, R.color.clickable_icon_tint_list_light);
        res.recycle();

        return Images.getColorStateList(context, resId);
    }

    public static int getActionBarTheme(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(null,
                new int[]{R.attr.actionBarTheme});
        int resourceId = typedArray.getResourceId(0, R.style.ActionBarTheme_Dark);
        typedArray.recycle();
        return resourceId;
    }

    public static int getActionBarPopupTheme(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(null,
                new int[]{R.attr.actionBarPopupTheme});
        int resourceId = typedArray.getResourceId(0, R.style.ActionBarPopupTheme_Light);
        typedArray.recycle();
        return resourceId;
    }

    public static int getActionBarSize(Context context) {
        TypedArray typedArray = context.obtainStyledAttributes(null,
                new int[]{R.attr.actionBarSize});
        int size = typedArray.getDimensionPixelSize(0, 0);
        typedArray.recycle();
        return size;
    }

    public static int getStatusBarHeight(Context context) {
        int resourceId = context.getResources().getIdentifier("status_bar_height", "dimen", "android");
        if (resourceId > 0) {
            return context.getResources().getDimensionPixelSize(resourceId);
        } else {
            return Sizes.dpToPx(context, 24);
        }
    }

}
