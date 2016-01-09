package com.shaubert.liftago.navigation;

import android.app.Fragment;
import android.content.Context;
import com.shaubert.liftago.state.SimulatorStateActivity;
import com.shaubert.liftago.state.SimulatorStateArgs;
import com.shaubert.ui.jumper.Args;
import com.shaubert.ui.jumper.Jump;
import com.shaubert.ui.jumper.StartNewActivityJump;

public class StartNewActivityJumper extends BaseJumper {

    public StartNewActivityJumper(Context context) {
        super(context);
    }

    public StartNewActivityJumper(Fragment fragment) {
        super(fragment);
    }

    public StartNewActivityJumper(android.support.v4.app.Fragment fragment) {
        super(fragment);
    }

    @Override
    public Jump<SimulatorStateArgs> toSimulatorState() {
        return to(SimulatorStateActivity.class);
    }

    @Override
    public <T extends Args> Jump<T> to(Class<?> aClass) {
        return new StartNewActivityJump<>(getStarter(), aClass);
    }

}
