package com.inker.mblockly.MBotServer.SerialTransmission;

/**
 * Created by kuoin on 2017/4/27.
 */

public class TxPackage {
    private byte[] bytes;

    /**
     * @param bytes take ownership of input byte array
     */
    public TxPackage(byte[] bytes) {
        this.bytes = bytes;
    }

    public boolean isSync() {
        return false;
    }

    public byte[] getBytes() { return bytes; }
}
