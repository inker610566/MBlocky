package com.inker.mblockly.MBotServer.SerialTransmission;

import android.bluetooth.BluetoothSocket;

import java.io.IOException;
import java.io.InputStream;
import java.io.OutputStream;

/**
 * Created by kuoin on 2017/4/27.
 */

public class BTSerialPortAdapter {
    BluetoothSocket socket;
    public BTSerialPortAdapter(BluetoothSocket socket) {
        this.socket = socket;
    }

    public void RequestSendPackage() {

    }

    private class RxThread extends Thread {
        private BluetoothSocket sock;
        public RxThread(BluetoothSocket sock) {
            this.sock = sock;
        }

        @Override
        public void run() {
            try {
                InputStream is = this.sock.getInputStream();
                byte[] rearBuf = new byte[Constants.ISTREAM_BUFFER_SIZE],
                       backBuf = new byte[Constants.ISTREAM_BUFFER_SIZE];
                int stOffset = 0, edOffset = 0, nstOffset;
                while(true) {
                    while((nstOffset = ProcessPackage(rearBuf, backBuf, stOffset, edOffset)) != stOffset) {
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
            }
            // TODO: enter shutdown process
        }

        /**
         * @return new offset, if return > rearBuf size then should swap 2 buf
         */
        private int ProcessPackage(byte[] rearBuf, byte[] backBuf, int stOffset, int edOffset) {
            // TODO: implement a single byte package stub
        }
    }

    private class TxThread extends Thread {
        private BluetoothSocket sock;
        public TxThread(BluetoothSocket sock) {
            this.sock = sock;
        }

        @Override
        public void run() {
            try {
                OutputStream os = sock.getOutputStream();

            } catch (IOException e) {
            }
            // TODO: enter shutdown process
        }
    }

    private void WritePackage(byte[] pkg) {
        try {
            ostream.write(pkg);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }
}
