package com.shaubert.liftago.util;

import android.app.Activity;
import android.content.Context;
import android.content.res.Resources;
import android.support.annotation.NonNull;
import android.support.v7.app.ActionBar;
import android.support.v7.app.AppCompatActivity;
import android.support.v7.widget.AppCompatSpinner;
import android.util.DisplayMetrics;
import android.util.TypedValue;
import android.view.Gravity;
import android.view.LayoutInflater;
import android.view.View;
import android.view.ViewGroup;
import android.widget.Spinner;
import android.widget.SpinnerAdapter;
import android.widget.TextView;
import com.shaubert.liftago.R;
import com.shaubert.ui.adapters.ListAdapter;
import com.shaubert.ui.adapters.common.AdapterItemIds;

public class ActionBars {

    public static int getActionBarHeight(Activity activity) {
        TypedValue value = new TypedValue();
        activity.getTheme().resolveAttribute(R.attr.actionBarSize, value, true);
        DisplayMetrics metrics = new DisplayMetrics();
        activity.getWindowManager().getDefaultDisplay().getMetrics(metrics);
        return (int) value.getDimension(metrics);
    }

    public static void removeSpinner(AppCompatActivity activity) {
        ActionBar bar = activity.getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowCustomEnabled(false);
            bar.setCustomView(null);
            bar.setDisplayShowTitleEnabled(true);
        }
    }

    public static Spinner addSpinner(AppCompatActivity activity, int ... itemsTextResId) {
        ActionBar bar = activity.getSupportActionBar();
        if (bar != null) {
            bar.setDisplayShowTitleEnabled(false);
            bar.setDisplayShowCustomEnabled(true);

            Spinner spinner = createSpinner(activity, itemsTextResId);
            bar.setCustomView(spinner,
                    new ActionBar.LayoutParams(ViewGroup.LayoutParams.WRAP_CONTENT,
                            ViewGroup.LayoutParams.MATCH_PARENT, Gravity.CENTER_VERTICAL));
            return spinner;
        }

        return null;
    }

    @NonNull
    public static Spinner createSpinner(AppCompatActivity activity, int ... itemsTextResId) {
        Context context = activity;
        ActionBar actionBar = activity.getSupportActionBar();
        if (actionBar != null) {
            context = actionBar.getThemedContext();
        }

        int popupThemeRes = ThemeUtils.getActionBarPopupTheme(activity);
        Resources.Theme popupTheme = activity.getResources().newTheme();
        Resources.Theme theme = context.getTheme();
        if (theme != null) {
            popupTheme.setTo(theme);
        }
        popupTheme.applyStyle(popupThemeRes, true);

        SpinnerAdapter adapter = createSpinnerAdapter(context, itemsTextResId);
        Spinner spinner = new AppCompatSpinner(context, null,
                android.support.v7.appcompat.R.attr.spinnerStyle, Spinner.MODE_DROPDOWN, popupTheme);
        spinner.setAdapter(adapter);
        return spinner;
    }

    public static SpinnerAdapter createSpinnerAdapter(Context context, int ... itemsTextResId) {
        AbSpinnerAdapter<String> adapter = new AbSpinnerAdapter<>(R.layout.ab_spinner_selected_item,
                R.layout.ab_spinner_dropdown_item);
        for (int itemTextResId : itemsTextResId) {
            adapter.addItem(context.getString(itemTextResId));
        }
        return adapter;
    }

    public static class AbSpinnerAdapter<T> extends ListAdapter<T> {
        private int layoutSelectedItem;
        private int layoutDropDownItem;

        public AbSpinnerAdapter(int layoutSelectedItem, int layoutDropDownItem) {
            this.layoutSelectedItem = layoutSelectedItem;
            this.layoutDropDownItem = layoutDropDownItem;
        }

        @Override
        public long getItemId(int position) {
            return AdapterItemIds.getIdFrom(getItem(position));
        }

        @Override
        protected View createNormalView(T item, int pos, ViewGroup parent, LayoutInflater inflater) {
            return inflater.inflate(layoutSelectedItem, parent, false);
        }

        @Override
        protected View createDropDownView(T item, int pos, ViewGroup parent, LayoutInflater inflater) {
            return inflater.inflate(layoutDropDownItem, parent, false);
        }

        @Override
        protected void bindNormalView(View view, T item, int pos) {
            TextView textView = (TextView) view;
            if (item instanceof CharSequence) {
                textView.setText((CharSequence)item);
            } else {
                textView.setText(item.toString());
            }
        }
    }
}