package jp.co.smbc.gridbeacon;

import org.json.simple.parser.ParseException;

import java.io.UnsupportedEncodingException;
import java.net.URLDecoder;
import java.util.Map;

/**
 * Created by amrizal.zainuddin on 12/8/2016.
 */
public class QrResult {
    public String getGid() {
        return gid;
    }

    public String getS() {
        return s;
    }

    public String getUrl() {
        return url;
    }

    public String getVdid() {
        return vdid;
    }

    private String gid;
    private String vdid;
    private String url;
    private String s;

    public QrResult(String json) {
        parseJson(json);
    }

    private void parseJson(String json) {
        JsonResult jResult = null;
        Map<?, ?> jMap = null;
        if (json != null && !json.isEmpty()) {
            try {
                jMap = JsonHelper.parseToMap(json);
                gid = String.valueOf(jMap.get("gid"));
                vdid = String.valueOf(jMap.get("vdid"));
                url = URLDecoder.decode(String.valueOf(jMap.get("url")),"UTF-8");
                s = String.valueOf(jMap.get("s"));
            } catch (ParseException e) {
                e.printStackTrace();
            } catch (UnsupportedEncodingException e) {
                e.printStackTrace();
            }
        }
    }

    public boolean isValid(){
        return !gid.isEmpty()
                && !vdid.isEmpty()
                && !url.isEmpty()
                && !s.isEmpty();
    }
}
