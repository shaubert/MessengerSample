package com.shaubert.liftago.util.text;

import android.text.Layout;
import android.text.StaticLayout;
import android.text.TextPaint;
import android.text.TextUtils;
import android.util.TypedValue;
import android.view.*;
import android.widget.TextView;
import android.widget.Toast;
import com.shaubert.liftago.App;
import com.shaubert.ui.adapters.common.Strings;
import com.shaubert.ui.dialogs.commons.Sizes;

public class TextViews {

    public static void setText(TextView view, CharSequence value) {
        setText(view, value, false);
    }

    public static void setText(TextView view, CharSequence value, boolean goneWhenEmpty) {
        view.setText(value);
        if (TextUtils.isEmpty(value)) {
            view.setVisibility(goneWhenEmpty ? View.GONE : View.INVISIBLE);
        } else {
            view.setVisibility(View.VISIBLE);
        }
    }

    public static void changeTextIfNotEqual(TextView textView, CharSequence text) {
        if (!TextUtils.equals(textView.getText(), text == null ? "" : text)) {
            textView.setText(text);
        }
    }

    public static void setTextSizeToFit(TextView textView, CharSequence text, int availableSizePx, int minTextSizePx) {
        setTextSizeToFit(textView, text, availableSizePx, textView.getTextSize(), minTextSizePx);
    }

    public static void setTextSizeToFit(TextView textView, CharSequence text, int availableSizePx, float fromSize, int minTextSizePx) {
        if (TextUtils.isEmpty(text)) {
            return;
        }

        float textSizeChangeDelta = Sizes.dpToPx(2, App.get());


        TextPaint textPaint = textView.getPaint();
        float originalSize = textPaint.getTextSize();
        textPaint.setTextSize(fromSize);
        float curSize = measureText(text, textPaint, false);
        float textSize = fromSize;
        for (; textSize > minTextSizePx && curSize > availableSizePx;) {
            textSize -= textSizeChangeDelta;
            textPaint.setTextSize(textSize);
            curSize = measureText(text, textPaint, false);
        }

        if (textSize != originalSize) {
            textPaint.setTextSize(originalSize);
            textView.setTextSize(TypedValue.COMPLEX_UNIT_PX, textSize);
        }
    }

    public static int measureText(CharSequence text, TextPaint textPaint, boolean forceStaticLayout) {
        if (TextUtils.isEmpty(text)) {
            return 0;
        }

        if (forceStaticLayout
                || !(text instanceof String)) {
            StaticLayout staticLayout = new StaticLayout(text,
                    textPaint,
                    Integer.MAX_VALUE,
                    Layout.Alignment.ALIGN_NORMAL,
                    0,
                    0,
                    false);
            return (int) (0.5f + staticLayout.getLineWidth(0));
        } else {
            return (int) (0.5f + textPaint.measureText((String) text));
        }
    }

    public static String getFieldValuerOrShowErrorIfBlank(TextView widget, int errorMes) {
        String text = widget.getText().toString();

        if (Strings.isBlank(text)) {
            Toast.makeText(widget.getContext(), errorMes, Toast.LENGTH_SHORT).show();
            widget.requestFocus();
            return null;
        }
        return text.trim();
    }

    public static String getTrimmedText(TextView widget) {
        return widget.getText().toString().trim();
    }

    public static void disableCopyAndPasteFor(TextView view) {
        if (android.os.Build.VERSION.SDK_INT < 11) {
            view.setOnCreateContextMenuListener(new View.OnCreateContextMenuListener() {
                public void onCreateContextMenu(ContextMenu menu, View v, ContextMenu.ContextMenuInfo menuInfo) {
                    menu.clear();
                }
            });
        } else {
            view.setTextIsSelectable(false);

            view.setCustomSelectionActionModeCallback(new ActionMode.Callback() {

                public boolean onPrepareActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public void onDestroyActionMode(ActionMode mode) {
                }

                public boolean onCreateActionMode(ActionMode mode, Menu menu) {
                    return false;
                }

                public boolean onActionItemClicked(ActionMode mode, MenuItem item) {
                    return false;
                }
            });
        }
    }
}
