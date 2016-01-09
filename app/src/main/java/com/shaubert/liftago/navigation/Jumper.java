package com.shaubert.liftago.navigation;


import com.shaubert.liftago.state.SimulatorStateArgs;
import com.shaubert.ui.jumper.Jump;

public interface Jumper extends com.shaubert.ui.jumper.Jumper {

    void setRedirectListener(RedirectListener redirectListener);

    void processRedirectStack(ActivityStack stack);


    Jump<SimulatorStateArgs> toSimulatorState();

}
