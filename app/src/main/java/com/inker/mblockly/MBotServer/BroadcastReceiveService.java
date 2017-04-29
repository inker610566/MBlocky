package com.inker.mblockly.MBotServer;

import android.app.Service;
import android.content.BroadcastReceiver;
import android.content.Context;
import android.content.Intent;
import android.content.IntentFilter;
import android.os.Handler;
import android.os.HandlerThread;
import android.os.IBinder;
import android.os.Looper;
import android.os.Message;
import android.os.Process;
import android.support.annotation.IntDef;

/**
 * Created by kuoin on 2017/4/29.
 */

abstract public class BroadcastReceiveService extends Service{
    private Looper mServiceLooper;
    private MessageHandler mServiceHandler;
    private HandlerThread thread;
    private String name;

    // Handler that receives messages from the thread
    private final class MessageHandler extends Handler {
        public MessageHandler(Looper looper) {
            super(looper);
        }
        @Override
        public void handleMessage(Message msg) {
            onHandleIntent((Intent)msg.obj);
        }
    }

    public BroadcastReceiveService(String name) {
        this.name = name;
    }

    protected abstract String[] getIntentActions();

    private final BroadcastReceiver mReceiver = new BroadcastReceiver() {
        private Context context = null;

        @Override
        public void onReceive(Context context, Intent intent) {
            Message.obtain(mServiceHandler, 0, intent);
        }
    };

    private void RegisterReceiver(BroadcastReceiver receiver) {
        IntentFilter filter = new IntentFilter();
        for(String action: getIntentActions())
            filter.addAction(action);
        registerReceiver(receiver, filter);
    }

    private void UnRegister(BroadcastReceiver receiver) {
        unregisterReceiver(receiver);
    }

    @Override
    public void onCreate() {
        thread = new HandlerThread(name + "Thread",
                Process.THREAD_PRIORITY_BACKGROUND);
        thread.start();

        mServiceLooper = thread.getLooper();
        mServiceHandler = new MessageHandler(mServiceLooper);
        RegisterReceiver(mReceiver);
    }

    protected abstract void onHandleIntent(Intent intent);

    @Override
    public IBinder onBind(Intent intent) {
        // We don't provide binding, so return null
        return null;
    }

    @Override
    public void onDestroy() {
        UnRegister(mReceiver);
        thread.quit();
    }
}
