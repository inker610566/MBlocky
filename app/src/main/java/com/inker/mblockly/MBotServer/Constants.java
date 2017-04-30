package com.inker.mblockly.MBotServer;

/**
 * Created by kuoin on 2017/4/27.
 */

public class Constants {
    // ACTION to MBotService
    public static final String MBOTSERVICE_CONNECT_ACTION = "CONNECT";
    public static final String MBOTSERVICE_DISCONNECT_ACTION = "DISCONNECT";
    public static final String MBOTSERVICE_QUERY_CONNECT_STATE_ACTION = "QCSTATE";
    public static final String MBOTSERVICE_SEND_PACKAGE_ACTION = "SEND";
    // ACTION Field
    public static final String BLUETOOTH_DEVICE = "BLUETOOTH_DEVICE";

    // ACTION from MBotService
    public static final String MBOTSERVICE_CONNECT_RESULT_ACTION = "R_CONNECT";
    public static final String MBOTSERVICE_DISCONNECT_RESULT_ACTION = "R_DISCONNECT";
    public static final String MBOTSERVICE_QUERY_CONNECT_RESULT_ACTION = "R_QCSTATE";
    public static final String MBOTSERVICE_RXPACKAGE_RESULT_ACTION = "R_PACKAGE";

    // ACTION Field
    public static final String MBOTSERVICE_ERROR_MESSAGE = "ERROR_MSG";
    public static final String MBOTSERVICE_PACKGE = "PACKAGE";

    // ACTION Field ErrorType
    public static final String MBOTSERVICE_ERROR_NO_DEVICE_CONNECT = "NO_DEVICE";
}
