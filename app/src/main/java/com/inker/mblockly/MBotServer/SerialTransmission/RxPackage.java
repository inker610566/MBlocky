package com.inker.mblockly.MBotServer.SerialTransmission;

import android.os.Parcel;
import android.os.Parcelable;

/**
 * Created by kuoin on 2017/4/27.
 */

public class RxPackage implements Parcelable{
    private byte[] bytes;

    public static final Creator<RxPackage> CREATOR = new Creator<RxPackage>() {
        @Override
        public RxPackage createFromParcel(Parcel in) {
            return new RxPackage(in.createByteArray());
        }

        @Override
        public RxPackage[] newArray(int size) {
            return new RxPackage[size];
        }
    };

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

    @Override
    public int describeContents() {
        return 0;
    }

    @Override
    public void writeToParcel(Parcel parcel, int flags) {
        parcel.writeByteArray(bytes);
    }
}
