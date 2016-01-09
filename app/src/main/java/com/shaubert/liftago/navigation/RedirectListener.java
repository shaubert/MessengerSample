package com.shaubert.liftago.navigation;

public interface RedirectListener {

    boolean onRedirect(Jumper jumper, ActivityStack.Record record, ActivityStack stack);

}