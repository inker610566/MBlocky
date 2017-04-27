package com.inker.mblockly.MBotServer.SerialTransmission;

import android.bluetooth.BluetoothSocket;
import android.os.Handler;
import android.os.Message;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;
import java.util.concurrent.BlockingQueue;
import java.util.concurrent.LinkedBlockingDeque;
import java.util.concurrent.Semaphore;

/**
 * Created by kuoin on 2017/4/27.
 */

public class BTSerialPortAdapter {
    private BluetoothSocket socket;
    private Semaphore TxWaitRx,
                      RxWaitTx,
                      WaitRxTxShutdown;
    private RxThread rxThread;
    private TxThread txThread;
    private ReceivePackageCallback rpcallback;
    private ShutdownEventCallback scallback;

    private class ToServiceEventHandler extends Handler
    {
        private static final int MSG_RX = 0, MSG_SHUTDOWN = 1;
        public ToServiceEventHandler() {
            super();
        }

        @Override
        public void handleMessage(Message msg) {
            // On Service Thread
            super.handleMessage(msg);
            if(msg.what == MSG_RX)
                rpcallback.call((RxPackage)msg.obj);
            else if(msg.what == MSG_SHUTDOWN) {
                Shutdown();
                scallback.call();
            }
            else assert false;
        }

        public void PostRxPackage(RxPackage pkg) {
            sendMessage(Message.obtain(this, MSG_RX, pkg));
        }

        public void PostShutdownEvent() {
            sendMessage(Message.obtain(this, MSG_SHUTDOWN));
        }

        public void ClearAllEvents() {
            removeMessages(MSG_RX);
            removeMessages(MSG_SHUTDOWN);
        }
    }
    ToServiceEventHandler toService;

    public BTSerialPortAdapter(
            ReceivePackageCallback rpcallback,
            ShutdownEventCallback scallback) {
        this.socket = socket;
        this.rpcallback = rpcallback;
        this.scallback = scallback;
        Init();
    }

    public void onCreate() {
        toService = new ToServiceEventHandler();
    }

    // Should be called on ServiceThread
    public void Connect(BluetoothSocket socket) {
        if(socket != null) {
            Shutdown();
        }
        this.socket = socket;
        rxThread = new RxThread(this.socket);
        txThread = new TxThread(this.socket);
        rxThread.start();
        txThread.start();
    }

    private void Init() {
        socket = null;
        toService.ClearAllEvents();
        WaitRxTxShutdown = new Semaphore(0);
        RxWaitTx = new Semaphore(0);
        TxWaitRx = new Semaphore(0);
    }

    // Should be called on ServiceThread
    public void Shutdown() {
        // close rx tx
        rxThread.TryClose();
        txThread.TryClose();

        // wait rx tx close
        try {
            WaitRxTxShutdown.acquire(2);
        } catch (InterruptedException e) {
            assert false;
        }

        // Cleanup
        Init();
    }

    // Should be called on ServiceThread
    public void RequestSendPackage(TxPackage pkg) {
        assert socket != null;
        txThread.Queue.add(pkg);
    }

    private class RxThread extends Thread {
        private BluetoothSocket sock;
        private InputStream is;
        private boolean isShutdown = false;

        public RxThread(BluetoothSocket sock) {
            this.sock = sock;
        }

        @Override
        public void run() {
            try {
                is = this.sock.getInputStream();
                byte[] rearBuf = new byte[Constants.ISTREAM_BUFFER_SIZE],
                       backBuf = new byte[Constants.ISTREAM_BUFFER_SIZE];
                int stOffset = 0, edOffset = 0, nstOffset;
                while(!isShutdown) {
                    while(!isShutdown && (nstOffset = ProcessPackage(rearBuf, backBuf, stOffset, edOffset)) != stOffset) {
                        if(nstOffset >= rearBuf.length) {
                            stOffset = nstOffset - rearBuf.length;
                            edOffset -= rearBuf.length;
                            byte[] tmp = rearBuf;
                            rearBuf = backBuf;
                            backBuf = tmp;
                        }
                        else
                            stOffset = nstOffset;
                    }
                    // read next bytes
                    int nbyte;
                    if(edOffset >= rearBuf.length) {
                        nbyte = is.read(backBuf, edOffset, edOffset-backBuf.length);
                    }
                    else {
                        nbyte = is.read(rearBuf, edOffset, edOffset-rearBuf.length);
                    }
                    if (nbyte == 0) break;
                    edOffset += nbyte;
                }
            } catch (IOException e) {
            } catch (InterruptedException e) {
            }
            WaitRxTxShutdown.release();
            toService.PostShutdownEvent();
        }

        /**
         * @return new offset, if return > rearBuf size then should swap 2 buf
         */
        private int ProcessPackage(byte[] rearBuf, byte[] backBuf, int stOffset, int edOffset)
            throws InterruptedException {
            // TODO: implement a single byte package stub
            RxPackage pkg = RxPackage.ParsePackage(rearBuf, backBuf, stOffset, edOffset);
            if(pkg == null)
                return stOffset;
            toService.PostRxPackage(pkg);
            if(pkg.isSync()) {
                TxWaitRx.release();
                RxWaitTx.acquire();
            }
            return stOffset+pkg.getByteCount();
        }

        public void TryClose() {
            isShutdown = true;
            try {
                is.close();
            } catch (IOException e) {
            }
            RxWaitTx.release();
        }
    }

    private class TxThread extends Thread {
        public BlockingQueue<TxPackage> Queue;
        private BluetoothSocket sock;
        private boolean isShutdown = false;
        private OutputStream os;
        public TxThread(BluetoothSocket sock) {
            this.sock = sock;
            Queue = new LinkedBlockingDeque<>();
        }

        @Override
        public void run() {
            try {
                os = sock.getOutputStream();
                while(!isShutdown) {
                    TxPackage pkg = Queue.take();
                    if(TxPackage.IS_DUMMY(pkg))
                    {
                        assert isShutdown;
                        break;
                    }
                    os.write(pkg.getBytes());
                    if(pkg.isSync()) {
                        os.flush();
                        TxWaitRx.acquire();
                        RxWaitTx.release();
                    }
                }
            } catch (IOException e) {
            } catch (InterruptedException e) {
            }

            WaitRxTxShutdown.release();
            toService.PostShutdownEvent();
        }

        public void TryClose() {
            isShutdown = true;
            try {
                os.close();
            } catch (IOException e) {
            }
            TxWaitRx.release();
            Queue.add(TxPackage.DUMMY);
        }
    }
}
