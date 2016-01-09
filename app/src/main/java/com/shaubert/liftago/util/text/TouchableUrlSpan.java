package com.shaubert.liftago.util.text;

import android.graphics.Color;
import android.os.Parcel;
import android.text.TextPaint;
import android.text.style.URLSpan;

public class TouchableUrlSpan extends URLSpan implements TouchableSpan {
    private boolean mIsPressed;
    private int mPressedBackgroundColor;
    private int mPressedLinkColor;
    private int mLinkColor;
    private boolean underline = true;

    public TouchableUrlSpan(String url) {
        this(url, Color.LTGRAY);
    }

    public TouchableUrlSpan(String url, int pressedBackgroundColor) {
        this(url, pressedBackgroundColor, 0);
    }

    public TouchableUrlSpan(String url, int pressedBackgroundColor, int pressedLinkColor) {
        this(url, pressedBackgroundColor, 0, pressedLinkColor, true);
    }

    public TouchableUrlSpan(String url, int pressedBackgroundColor, int linkColor, int pressedLinkColor, boolean withUnderline) {
        super(url);
        mPressedBackgroundColor = pressedBackgroundColor;
        mPressedLinkColor = pressedLinkColor;
        mLinkColor = linkColor;
        underline = withUnderline;
    }

    public TouchableUrlSpan(Parcel parcel) {
        super(parcel);
        mPressedBackgroundColor = parcel.readInt();
        mPressedLinkColor = parcel.readInt();
        mLinkColor = parcel.readInt();
        underline = parcel.readInt() != 0;
    }

    @Override
    public void writeToParcel(Parcel dest, int flags) {
        super.writeToParcel(dest, flags);
        dest.writeInt(mPressedBackgroundColor);
        dest.writeInt(mPressedLinkColor);
        dest.writeInt(mLinkColor);
        dest.writeInt(underline ? 1 : 0);
    }

    @Override
    public void setPressed(boolean isSelected) {
        mIsPressed = isSelected;
    }

    @Override
    public boolean isPressed() {
        return mIsPressed;
    }

    @Override
    public void updateDrawState(TextPaint ds) {
        super.updateDrawState(ds);
        ds.setUnderlineText(underline);
        if (mLinkColor != 0) {
            ds.linkColor = mLinkColor;
        }
        if (mPressedLinkColor != 0) {
            ds.setColor(mIsPressed ? mPressedLinkColor : ds.linkColor);
        }
        if (mPressedBackgroundColor != 0) {
            ds.bgColor = mIsPressed ? mPressedBackgroundColor : Color.TRANSPARENT;
        }
    }
}
