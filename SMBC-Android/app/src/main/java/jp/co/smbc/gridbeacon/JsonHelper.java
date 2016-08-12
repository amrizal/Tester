package jp.co.smbc.gridbeacon;

import android.util.Log;
import org.json.simple.JSONValue;
import org.json.simple.parser.ContainerFactory;
import org.json.simple.parser.JSONParser;
import org.json.simple.parser.ParseException;

import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

/**
 * Created by amrizal.zainuddin on 12/8/2016.
 */
public class JsonHelper {
    private static final String TAG = JsonHelper.class.getSimpleName();

    /**
     * Parse json string to JsonResult object
     *
     * @param sJson Jason String
     * @return Return JsonResult object
     * @throws ParseException
     */
    public static JsonResult parseJSON(String sJson) throws ParseException {
        JsonResult jResult = null;
        Map<?, ?> jMap = null;
        try {

            if (sJson != null && !sJson.isEmpty()) {

                jMap = parseToMap(sJson);

                if (jMap != null) {
                    jResult = new JsonResult((String) jMap.get("guid"), (String) jMap.get("sid"), (String) jMap.get("url"), (String) jMap.get("cloudid"), (String) jMap.get("encseed"),
                            (String) jMap.get("hashseed"), (String) jMap.get("trxdata"), (String) jMap.get("err"), (String) jMap.get("trxid"), JSONValue.toJSONString(jMap.get("data")),
                            (String) jMap.get("mts"), (String) jMap.get("ts"), (String) jMap.get("s"), (String) jMap.get("resultcode"), (String) jMap.get("msgid"), (String) jMap.get("enc"), (String) jMap.get("cmd"));

                    jResult.setVersion(String.valueOf(jMap.get("ver")));

                } else {
                    Log.e(TAG, "parseJSON(), jMap is null");
                }
            } else {
                Log.e(TAG, "parseJSON(), jResult is null");
                jResult = null;
            }
        } catch (ParseException e) {
            Log.e(TAG, "parseJSON(), Exception thrown. ", e);
            throw e;
        }

        return jResult;
    }

    /**
     * Parse json string to mapping object
     *
     * @param sJson Jason String
     * @return Return Map object
     * @throws ParseException
     */
    public static Map<?, ?> parseToMap(String sJson) throws ParseException {
        Map<?, ?> jMap = null;
        try {

            if (sJson != null && !sJson.isEmpty()) {

                JSONParser parser = new JSONParser();

                ContainerFactory containerFactory = new ContainerFactory() {
                    public List<String> creatArrayContainer() {
                        return new LinkedList<String>();
                    }

                    public Map<String, String> createObjectContainer() {
                        return new LinkedHashMap<String, String>();
                    }
                };

                jMap = (Map<?, ?>) parser.parse(sJson, containerFactory);
            } else {
                Log.d(TAG, "parseToMap(), sJson is empty");
            }

        } catch (ParseException e) {
            Log.e(TAG, "parseToMap(), JsonParseException = ", e);
            throw e;
        }

        return jMap;
    }
}
