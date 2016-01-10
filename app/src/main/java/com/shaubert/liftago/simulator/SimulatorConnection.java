package com.shaubert.liftago.simulator;

import android.os.Handler;
import android.os.IBinder;
import android.os.Message;
import android.os.Messenger;
import android.support.annotation.Nullable;
import com.shaubert.liftago.util.Logger;

public class SimulatorConnection implements Handler.Callback {

    public static final String TAG = SimulatorConnection.class.getSimpleName();

    private SimulatorState state;

    private Messenger commandSender;
    private Messenger commandReceiver = new Messenger(new Handler(this));
    private @Nullable Callback callback;

    public SimulatorConnection(IBinder service) {
        commandSender = new Messenger(service);
    }

    public @Nullable SimulatorState getState() {
        return state;
    }

    public void start(Callback callback) {
        if (state != null) {
            throw new IllegalStateException("already has started or closed");
        }
        this.callback = callback;

        sendRegisterMessage();
    }

    public void close() {
        callback = null;
    }

    private void sendRegisterMessage() {
        sendMessage(Messages.MSG_OUT_REGISTER);
    }

    public void sendOnBroadcastMessage() {
        if (state != SimulatorState.WAITING) {
            throw new IllegalStateException("services is not in WAITING state");
        }

        sendMessage(Messages.MSG_OUT_ON_BROADCAST);
    }

    public void refreshState() {
        sendMessage(Messages.MSG_OUT_GET_STATE);
    }

    private void sendMessage(int what) {
        Logger.debug(TAG, "sending message: " + what);

        try {
            Message message = Message.obtain(null, what);
            message.replyTo = commandReceiver;
            commandSender.send(message);
        } catch (Exception ex) {
            Logger.error(TAG, "sending message error", ex);
            if (callback != null) callback.onConnectionError();
        }
    }

    @Override
    public boolean handleMessage(Message msg) {
        Logger.debug(TAG, "new message received: " + msg.what);

        switch (msg.what) {
            case Messages.MSG_IN_STATE:
            case Messages.MSG_IN_ERROR:
                handleNewSimulatorState(msg.arg1);
                break;

            case Messages.MSG_IN_OK:
                handleOkMessage();
                break;

            case Messages.MSG_IN_ON_RIDE_START:
                handleRideStart();
                break;

            case Messages.MSG_IN_ON_RIDE_FINISH:
                handleRideFinish();
                break;

            default:
                refreshState();
                break;
        }
        return false;
    }

    private void handleOkMessage() {
        stateTransition(SimulatorState.WAITING, SimulatorState.BROADCASTING);
    }

    private void handleRideStart() {
        stateTransition(SimulatorState.BROADCASTING, SimulatorState.RIDE);
    }

    private void handleRideFinish() {
        stateTransition(SimulatorState.RIDE, SimulatorState.WAITING);
    }

    private void stateTransition(SimulatorState requiredInitialState, SimulatorState newState) {
        if (state == requiredInitialState) {
            handleNewSimulatorState(newState);
        } else {
            refreshState();
        }
    }

    private void handleNewSimulatorState(int code) {
        handleNewSimulatorState(SimulatorState.parse(code));
    }

    private void handleNewSimulatorState(SimulatorState newState) {
        if (newState != state) {
            Logger.debug(TAG, "simulator state changed: " + newState);
            state = newState;
            if (callback != null) callback.onStateChanged(newState);
        }
    }

    public interface Callback {
        void onConnectionError();
        void onStateChanged(SimulatorState newState);
    }

}