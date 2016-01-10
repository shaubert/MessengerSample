package com.shaubert.liftago.simulator;

import android.app.Service;
import android.content.ComponentName;
import android.content.Context;
import android.content.Intent;
import android.content.ServiceConnection;
import android.content.pm.ResolveInfo;
import android.os.IBinder;
import android.support.annotation.Nullable;
import com.shaubert.liftago.App;
import com.shaubert.liftago.util.Logger;
import de.greenrobot.event.EventBus;

import java.util.List;

public class SimulatorConnectionService extends Service {

    public static final String TAG = SimulatorConnectionService.class.getSimpleName();

    private static final String REMOTE_SERVICE_CONNECT_ACTION = "com.adleritech.android.developertest.SimulatorService";

    public static final String ACTION_EXTRA_NAME = "__action";
    public static final String ACTION_CONNECT = "connect";
    public static final String ACTION_START_BROADCAST = "start_broadcast";
    public static final String ACTION_REFRESH_STATE = "refresh_state";
    public static final String ACTION_DISCONNECT = "disconnect";

    private EventBus bus;

    private ConnectorState state = ConnectorState.IDLE;

    private SimulatorConnection simulatorConnection;
    private SimulatorConnection.Callback simulatorConnectionCallback = new SimulatorConnection.Callback() {
        @Override
        public void onConnectionError() {
            disconnect();
        }

        @Override
        public void onStateChanged(SimulatorState newState) {
            onSimulatorStateChanged(newState);
        }
    };

    private ServiceConnection remoteConnectionListener = new ServiceConnection() {
        public void onServiceConnected(ComponentName className, IBinder service) {
            onConnected(className, service);
        }

        public void onServiceDisconnected(ComponentName className) {
            disconnect();
        }
    };

    public static void connect(Context context) {
        startWithAction(context, ACTION_CONNECT);
    }

    public static void startBroadcast(Context context) {
        startWithAction(context, ACTION_START_BROADCAST);
    }

    public static void refreshState(Context context) {
        startWithAction(context, ACTION_REFRESH_STATE);
    }

    public static void disconnect(Context context) {
        startWithAction(context, ACTION_DISCONNECT);
    }

    private static void startWithAction(Context context, String action) {
        Intent intent = new Intent(context, SimulatorConnectionService.class);
        intent.putExtra(ACTION_EXTRA_NAME, action);
        context.startService(intent);
    }

    public static boolean singleRemoteServiceExists(Context context) {
        Intent intent = new Intent(REMOTE_SERVICE_CONNECT_ACTION);
        List<ResolveInfo> infos = context.getPackageManager().queryIntentServices(intent, 0);
        return infos != null && infos.size() == 1;
    }

    @Override
    public void onCreate() {
        super.onCreate();
        bus = App.get().getBus();
    }

    @Override
    public int onStartCommand(Intent intent, int flags, int startId) {
        if (intent != null) {
            processAction(intent.getStringExtra(ACTION_EXTRA_NAME));
        }

        stopSelfIfNeeded();
        return START_NOT_STICKY;
    }

    private void processAction(String action) {
        if (action == null) {
            return;
        }

        switch (action) {
            case ACTION_CONNECT:
                connect();
                break;

            case ACTION_START_BROADCAST:
                startBroadcast();
                break;

            case ACTION_REFRESH_STATE:
                refreshState();
                break;

            case ACTION_DISCONNECT:
                disconnect();
                break;
        }
    }

    private void connect() {
        if (state != ConnectorState.IDLE) {
            return;
        }

        Intent intent = createRemoteServiceIntent(this);
        if (intent == null) {
            return;
        }

        setState(ConnectorState.CONNECTING);

        try {
            Logger.debug(TAG, "binding to remote service");
            bindService(intent, remoteConnectionListener, BIND_AUTO_CREATE);
        } catch (Exception ex) {
            Logger.error(TAG, "failed to bind remote service", ex);
            disconnect();
        }
    }

    public Intent createRemoteServiceIntent(Context context) {
        Intent intent = new Intent(REMOTE_SERVICE_CONNECT_ACTION);
        List<ResolveInfo> infos = context.getPackageManager().queryIntentServices(intent, 0);

        if (infos == null || infos.isEmpty()) {
            return null;
        }

        if (infos.size() > 1) {
            Logger.warn(TAG, "multiple services found for intent: " + intent);
            return null;
        }

        ResolveInfo serviceInfo = infos.get(0);
        String packageName = serviceInfo.serviceInfo.packageName;
        String className = serviceInfo.serviceInfo.name;
        ComponentName component = new ComponentName(packageName, className);

        Intent explicitIntent = new Intent(intent);
        explicitIntent.setComponent(component);
        return explicitIntent;
    }

    private void startBroadcast() {
        if (state != ConnectorState.CONNECTED
                || simulatorConnection == null
                || simulatorConnection.getState() != SimulatorState.WAITING) {
            return;
        }

        simulatorConnection.sendOnBroadcastMessage();
    }

    private void refreshState() {
        if (state != ConnectorState.CONNECTED
                || simulatorConnection == null) {
            return;
        }

        simulatorConnection.refreshState();
    }

    private void onConnected(ComponentName className, IBinder service) {
        setState(ConnectorState.CONNECTED);
        simulatorConnection = new SimulatorConnection(service);
        simulatorConnection.start(simulatorConnectionCallback);
    }

    private void onSimulatorStateChanged(@Nullable SimulatorState newState) {
        bus.postSticky(new StateEvent(newState));
    }

    private void disconnect() {
        if (state == ConnectorState.IDLE || state == ConnectorState.DISCONNECTING) {
            return;
        }

        setState(ConnectorState.DISCONNECTING);

        try {
            Logger.debug(TAG, "unbinding from remote service");
            unbindService(remoteConnectionListener);
        } catch (Exception ex) {
            Logger.warn(TAG, "failed to unbind service", ex);
        }
        onDisconnected();
    }

    public void setState(ConnectorState state) {
        this.state = state;
        Logger.debug(TAG, "service state changed: " + state);
    }

    private void onDisconnected() {
        reset();
        stopSelfIfNeeded();
    }

    private void reset() {
        setState(ConnectorState.IDLE);
        if (simulatorConnection != null) {
            simulatorConnection.close();
            simulatorConnection = null;
        }

        onSimulatorStateChanged(null);
    }

    private void stopSelfIfNeeded() {
        if (state == ConnectorState.IDLE) {
            Logger.debug(TAG, "stopping service");
            stopSelf();
        }
    }

    @Nullable
    @Override
    public IBinder onBind(Intent intent) {
        return null;
    }

}
