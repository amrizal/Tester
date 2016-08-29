package jp.co.smbc.gridbeacon;

/**
 * <code>JsonResult</code> JSON object
 *
 * @author tauwei
 */
public class JsonResult {
    private String mts = "";
    private String ts = "";
    private String s = "";
    private String token = "";
    private String guid = "";
    private String encseed = "";
    private String hashseed = "";
    private String trxdata = "";
    private String url = "";
    private String sid = "";
    private String err = "";
    private String trxid = "";
    private String data = "";

    public void setVersion(String version) {
        this.version = version;
    }

    private String version = "";


    // on off result
    private String resultcode = "";
    private String msgid = "";
    private String enc = "";

    //turnoff timer
    private String cmd = "";

    JsonResult() {

    }

    public JsonResult(String inGuid, String inSid, String inUrl, String inToken, String inEncseed, String inHashseed, String inTrxdata, String inErr, String inTrxid, String inData, String inMts,
                      String inTs, String inSig, String inResultCode, String inMsgID, String inEnc, String inCmd) {

        mts = inMts != null ? inMts : "";
        ts = inTs != null ? inTs : "";
        s = inSig != null ? inSig : "";
        token = inToken != null ? inToken : "";
        guid = inGuid != null ? inGuid : "";
        encseed = inEncseed != null ? inEncseed : "";
        hashseed = inHashseed != null ? inHashseed : "";
        trxdata = inTrxdata != null ? inTrxdata : "";
        url = inUrl != null ? inUrl : "";
        sid = inSid != null ? inSid : "";
        err = inErr != null ? inErr : "";
        trxid = inTrxid != null ? inTrxid : "";
        data = inData != null ? inData : "";

        resultcode = inResultCode != null ? inResultCode : "";
        msgid = inMsgID != null ? inMsgID : "";
        enc = inEnc != null ? inEnc : "";
        cmd = inCmd != null ? inCmd : "";
    }

    public String getTrxList() {
        return data;
    }

    public String getTrxID() {
        return trxid;
    }

    public String getError() {
        return err;
    }

    public String getURL() {
        return url;
    }

    public String getSessionId() {
        return sid;
    }

    public String getMts() {
        return mts;
    }

    public String getTimestamp() {
        return ts;
    }

    public String getToken() {
        return token;
    }

    public String getGuID() {
        return guid;
    }

    public String getEncSeed() {
        return encseed;
    }

    public String getHashSeed() {
        return hashseed;
    }

    public String getTrxData() {
        return trxdata;
    }

    public String getSignature() {
        return s;
    }

    public String getResultcode() {
        return resultcode;
    }

    public String getMsgid() {
        return msgid;
    }

    public String getEnc() {
        return enc;
    }

    public String getCmd() {
        return cmd;
    }

    public String getVersion() {
        return version;
    }
}

