package com.shaubert.liftago.util;

public interface Func<PARAM, RESULT> {
    RESULT perform(PARAM param);
}
