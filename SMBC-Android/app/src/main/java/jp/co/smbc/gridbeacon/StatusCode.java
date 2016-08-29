package jp.co.smbc.gridbeacon;

/**
 * <code>Statuscode</code> This class stores local status codes
 */
public class StatusCode {

    // QR
    public static final int QR_VALID_SIG = 200;

    // OTP
    public static final int OTP_REG_OK = 200;
    public static final int OTP_WEBPAGE_REG_OK = 200;

    public static final int SIG_INVALID = 900;
    public static final int TS_EXPIRED = 901;
    public static final int INVALID_SETTING = 902;
    public static final int QR_NOT_FOUND = 903;
    // Local error
    /**
     * Failed to connect to GRIDGOO server
     */
    public static final int FAILED_CONNECT_GRID = 910;
    public static final int FAILED_GET_DEVICEID = 911;
    public static final int CONNECTION_TIMEOUT = 912;
    public static final int ERR_HTTP_SSL = 913;
    public static final int ERR_HTTP_PROTOCOL = 914;
    public static final int ERR_HTTP_IO = 915;
    public static final int ERR_HTTP_HOST = 916;
    public static final int ERR_HTTP_RUNTIME = 917;
    public static final int ERR_HTTP_CONREFUSED = 918;
    public static final int ERR_DATA_CORRUPTED = 919;

}
