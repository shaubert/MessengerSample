package com.shaubert.liftago.util.text;

import android.text.InputFilter;
import android.text.SpannableStringBuilder;
import android.text.Spanned;

import java.text.DecimalFormatSymbols;

public class DecimalInputFilter implements InputFilter {

    private char[] mAccepted;
    private boolean mSign;
    private boolean mDecimal;

    private char localDecimalChar;

    private static final int SIGN = 1;
    private static final int DECIMAL = 2;

    private static final char[][] CHARACTERS = {
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9'},
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '+'},
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '.', ','},
            {'0', '1', '2', '3', '4', '5', '6', '7', '8', '9', '-', '+', '.', ','},
    };

    private static boolean isSignChar(final char c) {
        return c == '-' || c == '+';
    }

    private static boolean isDecimalPointChar(final char c) {
        return c == '.' || c == ',';
    }

    private boolean isLocalDecimalChar(final char c) {
        return c == localDecimalChar;
    }

    public DecimalInputFilter() {
        this(false, false);
    }

    public DecimalInputFilter(boolean sign, boolean decimal) {
        mSign = sign;
        mDecimal = decimal;

        int kind = (sign ? SIGN : 0) | (decimal ? DECIMAL : 0);
        mAccepted = CHARACTERS[kind];

        localDecimalChar = new DecimalFormatSymbols().getDecimalSeparator();
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end,
                               Spanned dest, int dstart, int dend) {
        CharSequence out = filterAllowedChars(source, start, end, dest, dstart, dend);

        if (mSign == false && mDecimal == false) {
            return out;
        }

        if (out != null) {
            source = out;
            start = 0;
            end = out.length();
        }

        int sign = -1;
        int decimal = -1;
        int dlen = dest.length();

        /*
         * Find out if the existing text has a sign or decimal point characters.
         */

        for (int i = 0; i < dstart; i++) {
            char c = dest.charAt(i);

            if (isSignChar(c)) {
                sign = i;
            } else if (isDecimalPointChar(c)) {
                decimal = i;
            }
        }
        for (int i = dend; i < dlen; i++) {
            char c = dest.charAt(i);

            if (isSignChar(c)) {
                return "";    // Nothing can be inserted in front of a sign character.
            } else if (isDecimalPointChar(c)) {
                decimal = i;
            }
        }

        /*
         * If it does, we must strip them out from the source.
         * In addition, a sign character must be the very first character,
         * and nothing can be inserted before an existing sign character.
         * Go in reverse order so the offsets are stable.
         */

        SpannableStringBuilder stripped = null;

        for (int i = end - 1; i >= start; i--) {
            char c = source.charAt(i);
            boolean strip = false;

            if (isSignChar(c)) {
                if (i != start || dstart != 0) {
                    strip = true;
                } else if (sign >= 0) {
                    strip = true;
                } else {
                    sign = i;
                }
            } else if (isDecimalPointChar(c)) {
                if (decimal >= 0) {
                    strip = true;
                } else {
                    decimal = i;
                }
            }

            if (strip) {
                if (end == start + 1) {
                    return "";  // Only one character, and it was stripped.
                }

                if (stripped == null) {
                    stripped = new SpannableStringBuilder(source, start, end);
                }

                stripped.delete(i - start, i + 1 - start);
            }
        }

        if (stripped != null) {
            return stripped;
        } else if (out != null) {
            return out;
        } else {
            return null;
        }
    }

    private CharSequence filterAllowedChars(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        boolean replaceDecimalChar = false;

        int i;
        for (i = start; i < end; i++) {
            char charAt = source.charAt(i);
            if (!ok(mAccepted, charAt)) {
                break;
            }
            if (isDecimalPointChar(charAt) && !isLocalDecimalChar(charAt)) {
                replaceDecimalChar = true;
            }
        }

        if (i == end) {
            if (replaceDecimalChar) {
                return replaceDecimalChar(source, start, end);
            } else {
                // It was all OK.
                return null;
            }
        }

        if (end - start == 1) {
            // It was not OK, and there is only one char, so nothing remains.
            return "";
        }

        SpannableStringBuilder filtered = replaceDecimalChar
                        ? replaceDecimalChar(source, start, end)
                        : new SpannableStringBuilder(source, start, end);
        i -= start;
        end -= start;

        // Only count down to i because the chars before that were all OK.
        for (int j = end - 1; j >= i; j--) {
            if (!ok(mAccepted, source.charAt(j))) {
                filtered.delete(j, j + 1);
            }
        }

        return filtered;
    }

    private SpannableStringBuilder replaceDecimalChar(CharSequence source, int start, int end) {
        SpannableStringBuilder filtered =
                new SpannableStringBuilder(source, start, end);
        int length = filtered.length();
        for (int i = 0; i < length; i++) {
            char ch = filtered.charAt(i);
            if (isDecimalPointChar(ch) && !isLocalDecimalChar(ch)) {
                filtered.replace(i, i + 1, String.valueOf(localDecimalChar));
            }
        }
        return filtered;
    }

    protected static boolean ok(char[] accept, char c) {
        for (int i = accept.length - 1; i >= 0; i--) {
            if (accept[i] == c) {
                return true;
            }
        }

        return false;
    }

}
