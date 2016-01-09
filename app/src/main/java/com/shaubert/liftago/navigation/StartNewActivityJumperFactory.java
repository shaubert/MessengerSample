package com.shaubert.liftago.navigation;

import android.app.Fragment;
import android.content.Context;
import com.shaubert.ui.jumper.JumperFactory;

public class StartNewActivityJumperFactory implements JumperFactory<Jumper> {

    @Override
    public Jumper createFor(Context context) {
        return new StartNewActivityJumper(context);
    }

    @Override
    public Jumper createFor(Fragment fragment) {
        return new StartNewActivityJumper(fragment);
    }

    @Override
    public Jumper createFor(android.support.v4.app.Fragment fragment) {
        return new StartNewActivityJumper(fragment);
    }

}
