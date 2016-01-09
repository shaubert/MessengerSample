package com.shaubert.liftago.simulator;

abstract class Messages {

    public static final int MSG_OUT_REGISTER = 1;
    public static final int MSG_OUT_ON_BROADCAST = 2;
    public static final int MSG_OUT_GET_STATE = 3;

    public static final int MSG_IN_OK = 999;
    public static final int MSG_IN_ERROR = 1000;
    public static final int MSG_IN_ON_RIDE_START = 1001;
    public static final int MSG_IN_ON_RIDE_FINISH = 1002;
    public static final int MSG_IN_STATE = 1003;

    private Messages() {
    }
}