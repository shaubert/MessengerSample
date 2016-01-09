package com.shaubert.liftago.util;

import android.content.Context;
import android.content.res.ColorStateList;
import android.graphics.drawable.Drawable;
import android.view.Menu;
import android.view.MenuItem;
import android.view.SubMenu;

public class MenuIconsColorizer {

    public static void colorize(Menu menu, Context context) {
        ColorStateList selector = ThemeUtils.getControlColorSelector(context);
        int color = selector != null
                ? selector.getDefaultColor()
                : ThemeUtils.getCheckableTintList(context).getDefaultColor();
        colorize(menu, color);
    }

    public static void colorize(Menu menu, int color) {
        for (int i = 0, size = menu.size(); i < size; i++) {
            final MenuItem menuItem = menu.getItem(i);
            colorMenuItem(menuItem, color);
            if (menuItem.hasSubMenu()) {
                final SubMenu subMenu = menuItem.getSubMenu();
                for (int j = 0; j < subMenu.size(); j++) {
                    colorMenuItem(subMenu.getItem(j), color);
                }
            }
        }
    }

    private static void colorMenuItem(MenuItem menuItem, int color) {
        Drawable icon = menuItem.getIcon();
        if (icon != null) {
            menuItem.setIcon(Images.getTintDrawable(icon, color));
        }
    }

}
