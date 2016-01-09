package com.shaubert.liftago.util.text;

import android.content.Context;
import android.content.res.TypedArray;
import android.graphics.Color;
import android.text.Spannable;
import android.text.SpannableStringBuilder;
import android.text.Spanned;
import android.text.TextUtils;
import android.text.style.UnderlineSpan;
import android.view.View;
import android.widget.TextView;

public class Spannables {

    public static void makeClickable(TextView textView, final View.OnClickListener clickListener) {
        textView.setText(makeClickable(textView.getContext(), false, textView.getText(), clickListener));
        textView.setMovementMethod(LinkTouchMovementMethod.get());
    }

    public static SpannableStringBuilder makeClickable(Context context, CharSequence text, final View.OnClickListener clickListener) {
        return makeClickable(context, false, text, clickListener);
    }

    public static SpannableStringBuilder makeClickable(Context context, boolean inverse, CharSequence text, final View.OnClickListener clickListener) {
        return makeClickable(context, inverse, true, text, clickListener);
    }

    public static SpannableStringBuilder makeClickable(Context context, boolean inverse, boolean underline, CharSequence text, final View.OnClickListener clickListener) {
        final int bgColor = getLinkBgColor(context, inverse);
        final int linkColor = getLinkColor(context, inverse);
        final int pressedLinkColor = getPressedLinkColor(context, inverse);
        return makeClickable(bgColor, linkColor, pressedLinkColor, underline, text, clickListener);
    }

    public static SpannableStringBuilder makeClickable(int bgColor, int linkColor, int pressedLinkColor,
                                                       boolean underline, CharSequence text,
                                                       final View.OnClickListener clickListener) {
        return append(new SpannableStringBuilder(), text, new TouchableUrlSpan("", bgColor, linkColor, pressedLinkColor, underline) {
            @Override
            public void onClick(View widget) {
                if (clickListener != null) {
                    clickListener.onClick(widget);
                }
            }
        });
    }

    public static SpannableStringBuilder makeUnderlinesClickable(Context context, boolean inverse, CharSequence text, final View.OnClickListener ... clickListeners) {
        final int bgColor = getLinkBgColor(context, inverse);
        final int linkColor = getLinkColor(context, inverse);
        final int pressedLinkColor = getPressedLinkColor(context, inverse);
        SpannableStringBuilder builder;
        if (text instanceof SpannableStringBuilder) {
            builder = (SpannableStringBuilder) text;
        } else {
            builder = new SpannableStringBuilder(text);
        }
        UnderlineSpan[] spans = builder.getSpans(0, builder.length(), UnderlineSpan.class);
        if (spans != null && spans.length > 0) {
            int minSize = Math.min(spans.length, clickListeners.length);
            for (int i = 0; i < minSize; i++) {
                UnderlineSpan span = spans[i];
                int start = builder.getSpanStart(span);
                int end = builder.getSpanEnd(span);
                builder.removeSpan(span);
                final View.OnClickListener clickListener = clickListeners[i];
                builder.setSpan(new TouchableUrlSpan("", bgColor, linkColor, pressedLinkColor, true) {
                    @Override
                    public void onClick(View widget) {
                        clickListener.onClick(widget);
                    }
                }, start, end, Spanned.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
        }
        return builder;
    }

    public static int getPressedLinkColor(Context context, boolean inverse) {
        return getColor(context, inverse ? android.R.attr.textColorPrimary : android.R.attr.textColorPrimaryInverse);
    }

    public static int getLinkColor(Context context, boolean inverse) {
        return getColor(context, inverse ? android.R.attr.textColorLinkInverse : android.R.attr.textColorLink);
    }

    public static int getLinkBgColor(Context context, boolean inverse) {
        return getColor(context, android.support.v7.appcompat.R.attr.colorControlHighlight);
    }

    private static int getColor(Context context, int attr) {
        TypedArray res = context.getTheme().obtainStyledAttributes(new int[] { attr });
        int resColor = res.getColor(0, Color.BLACK);
        res.recycle();
        return resColor;
    }

    public static SpannableStringBuilder make(CharSequence text, Object... spans) {
        return append(new SpannableStringBuilder(), text, spans);
    }

    public static SpannableStringBuilder append(CharSequence target, CharSequence text, Object... spans) {
        return append(new SpannableStringBuilder(target), text, spans);
    }

    public static SpannableStringBuilder append(SpannableStringBuilder builder, CharSequence text, Object... spans) {
        if (text == null) {
            return builder;
        }

        builder.append(text);
        for (Object span : spans) {
            builder.setSpan(span, builder.length() - text.length(), builder.length(),
                    Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
        }
        return builder;
    }

    public static SpannableStringBuilder appendAndSetSpansBefore(SpannableStringBuilder builder, CharSequence text, Object... spans) {
        if (text == null) {
            return builder;
        }

        if (text instanceof Spannable) {
            final Spannable spannable = (Spannable) text;
            builder.append(text.toString());
            for (Object span : spans) {
                builder.setSpan(span, builder.length() - text.length(), builder.length(),
                        Spannable.SPAN_EXCLUSIVE_EXCLUSIVE);
            }
            TextUtils.copySpansFrom(spannable, 0, spannable.length(), null, builder, builder.length() - text.length());
            return builder;
        } else {
            return append(builder, text, spans);
        }
    }

    public static SpannableStringBuilder format(String format, String marker, CharSequence ... args) {
        if (format == null) {
            return null;
        }

        SpannableStringBuilder builder = new SpannableStringBuilder(format);
        int delta = 0;
        int lastPos = 0;
        int argsCounter = 0;
        while (true) {
            if (argsCounter >= args.length || lastPos >= format.length()) {
                break;
            }

            int index = format.indexOf(marker, lastPos);
            if (index > -1) {
                int posInResult = index + delta;
                CharSequence sequence = args[argsCounter];
                if (sequence == null) {
                    sequence = "";
                }
                builder.replace(posInResult, posInResult + marker.length(), sequence);
                delta += sequence.length() - marker.length();
                lastPos = index + marker.length();
                argsCounter++;
            } else {
                break;
            }
        }
        return builder;
    }

}
