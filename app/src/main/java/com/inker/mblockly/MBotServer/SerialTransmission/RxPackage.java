package com.inker.mblockly.MBotServer.SerialTransmission;

/**
 * Created by kuoin on 2017/4/27.
 */

public class RxPackage {
    private byte[] bytes;

    /**
     * @return return null if incomplete package format
     */
    public static RxPackage ParsePackage(byte[] rearBuf, byte[] backBuf, int stOffset, int edOffset) {
        // stub for consume 1 byte package
        assert stOffset < rearBuf.length;
        if(stOffset < edOffset)
            return new RxPackage(new byte[]{rearBuf[stOffset]});
        else
            return null;
    }

    /**
     * @param bytes take ownership of input byte array
     */
    private RxPackage(byte[] bytes) {
        this.bytes = bytes;
    }

    public boolean isSync() {
        return false;
    }

    public byte[] getBytes() { return bytes; }

    public int getByteCount() {return bytes.length; }
}
