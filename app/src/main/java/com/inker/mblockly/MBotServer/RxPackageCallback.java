package com.inker.mblockly.MBotServer;

import com.inker.mblockly.MBotServer.SerialTransmission.RxPackage;

/**
 * Created by kuoin on 2017/4/28.
 */

public interface RxPackageCallback {
    void call(RxPackage pkg);
}
