package com.shaubert.liftago.simulator;

public enum SimulatorState {

    UNKNOWN(Integer.MIN_VALUE),

    WAITING(1),
    BROADCASTING(2),
    RIDE(3);

    public final int code;

    SimulatorState(int code) {
        this.code = code;
    }

    public static SimulatorState parse(int code) {
        for (SimulatorState state : values()) {
            if (state.code == code) {
                return state;
            }
        }

        return UNKNOWN;
    }

}
