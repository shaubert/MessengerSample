package com.shaubert.liftago.util;

import android.support.v4.app.Fragment;
import android.support.v4.app.FragmentManager;
import android.support.v4.app.FragmentTransaction;
import com.shaubert.liftago.App;
import com.shaubert.liftago.R;
import com.shaubert.ui.jumper.Args;

public class Fragments {

    @SuppressWarnings("unchecked")
    public static <T> T newInstance(Class<T> fragmentClazz, Args args) {
        return (T) Fragment.instantiate(App.get(), fragmentClazz.getName(), args != null ? args.toBundle() : null);
    }

    public static void moveToNext(FragmentManager fragmentManager, Fragment newFragment, boolean backStack, int resId) {
        moveTo(fragmentManager, newFragment, backStack, resId, true);
    }

    public static void moveToPrev(FragmentManager fragmentManager, Fragment newFragment, boolean backStack, int resId) {
        moveTo(fragmentManager, newFragment, backStack, resId, false);
    }

    private static void moveTo(FragmentManager fragmentManager, Fragment newFragment, boolean backStack, int resId, boolean forward) {
        FragmentTransaction fragmentTransaction = fragmentManager.beginTransaction();
        if (forward) {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_right, R.anim.slide_out_left,
                    R.anim.slide_in_left, R.anim.slide_out_right);
        } else {
            fragmentTransaction.setCustomAnimations(R.anim.slide_in_left, R.anim.slide_out_right,
                    R.anim.slide_in_right, R.anim.slide_out_left);
        }
        fragmentTransaction.replace(resId, newFragment);
        if (backStack) {
            fragmentTransaction.addToBackStack(null);
        }
        fragmentTransaction.commit();
    }

    public static <T> T getParent(Fragment fragment, Class<T> parentClass) {
        Fragment parentFragment = fragment.getParentFragment();
        if (parentClass.isInstance(parentFragment)) {
            //noinspection unchecked
            return (T) parentFragment;
        } else if (parentClass.isInstance(fragment.getActivity())) {
            //noinspection unchecked
            return (T) fragment.getActivity();
        }
        return null;
    }

}
