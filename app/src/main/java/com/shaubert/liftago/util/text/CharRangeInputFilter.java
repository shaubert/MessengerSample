package com.shaubert.liftago.util.text;

import android.text.*;

public class CharRangeInputFilter implements InputFilter {

    public static final char[] US_ALPHABET = { 'a', 'b', 'c', 'd', 'e', 'f', 'g', 'h', 'i', 'j', 'k', 'l', 'm', 'n', 'o', 'p', 'q', 'r', 's', 't', 'u', 'v', 'w', 'x', 'y', 'z' };

    private boolean digits;
    private boolean decimals;
    private char[] allowedChars;
    private CharFilter charFilter;

    //Make sure your input without suggestions
    public CharRangeInputFilter(CharFilter charFilter)  {
        this.charFilter = charFilter;
    }

    public CharRangeInputFilter(char ... allowedChars)  {
        this.allowedChars = allowedChars;
    }

    public static CharRangeInputFilter newNumericFilter() {
        CharRangeInputFilter filter = new CharRangeInputFilter();
        filter.digits = true;
        filter.allowedChars = null;
        return filter;
    }

    public static CharRangeInputFilter newDecimalFilter() {
        CharRangeInputFilter filter = new CharRangeInputFilter();
        filter.decimals = true;
        filter.allowedChars = null;
        return filter;
    }

    @Override
    public CharSequence filter(CharSequence source, int start, int end, Spanned dest, int dstart, int dend) {
        int i;
        for (i = start; i < end; i++) {
            if (!isCharAllowed(source.charAt(i))) {
                break;
            }
        }

        if (i == end) {
            // It was all OK.
            return null;
        }

        if (end - start == 1) {
            // It was not OK, and there is only one char, so nothing remains.
            return "";
        }

        SpannableStringBuilder filtered =
                new SpannableStringBuilder(source, start, end);
        i -= start;
        end -= start;

        // Only count down to i because the chars before that were all OK.
        for (int j = end - 1; j >= i; j--) {
            if (!isCharAllowed(source.charAt(j))) {
                filtered.delete(j, j + 1);
            }
        }

        return filtered;
    }

    private boolean isCharAllowed(char currentChar) {
        if (allowedChars != null) {
            for (char allowedChar : allowedChars) {
                if (allowedChar == currentChar) {
                    return true;
                }
            }
        } else if (digits) {
            return Character.isDigit(currentChar);
        } else if (decimals) {
            return Character.isDigit(currentChar) || currentChar == '.' || currentChar == ',';
        } else if (charFilter != null) {
            return charFilter.isCharAllowed(currentChar);
        }
        return false;
    }

    public interface CharFilter {
        boolean isCharAllowed(char ch);
    }

    public static class SingleWordFilter implements CharFilter {
        @Override
        public boolean isCharAllowed(char ch) {
            return !Character.isWhitespace(ch);
        }
    }

}