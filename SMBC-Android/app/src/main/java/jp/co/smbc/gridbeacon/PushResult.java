package jp.co.smbc.gridbeacon;

import android.util.Log;

import org.json.simple.parser.ParseException;

/**
 * <code>PushResult</code> store result from GRID server
 */
public class PushResult {
    private static final String TAG = PushResult.class.getSimpleName();
    private String sJsonData;
    private int iCode;

    public PushResult(int iResponseCode, String inJson) {
        iCode = iResponseCode;

        sJsonData = inJson;
    }

    public int getCode() {
        return iCode;
    }

    public JsonResult getJResult() {
        //tw
        int iLocalCode = iCode;

        JsonResult jResult = null;
        try {
            if (sJsonData != null && !sJsonData.isEmpty()) {
                jResult = JsonHelper.parseJSON(sJsonData);
            }

        } catch (ParseException e) {
            Log.e(TAG, "getJResult(), Exception thrown. ", e);
            if (iLocalCode == 501 || iLocalCode == 403) { // tw: no json for 501 if server fails to generate json response
                iCode = iLocalCode;
            } else {
                iCode = StatusCode.ERR_DATA_CORRUPTED;
            }
        }
        return jResult;
    }

    public String getJson() {
        return sJsonData;
    }
}

