package com.inker.mblockly.MBotServer.SerialTransmission;

/**
 * Created by kuoin on 2017/4/27.
 */

public interface ReceivePackageCallback {
    void call(RxPackage pkg);
}
