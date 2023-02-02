package com.cjs.sso.qw;

import com.alibaba.fastjson.JSONObject;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestTemplate;

@Component
public class QWTokenHandler {
    private static final String KEY = "";
    private static final String SECRET = "ikXc_sOI5fJ6CzJ_Hg-kdMh1fXpZdmWQlq2R-Top8NA";
    private static final String CORPID = "wl49366340eb";

    public static String getToken(RestTemplate template) {
        String url = "http://10.11.134.71/cgi-bin/gettoken?corpid="+CORPID+"&corpsecret=" + SECRET;
        String result = template.getForObject(url, String.class);
        JSONObject obj = JSONObject.parseObject(result);
        Integer errcode = obj.getInteger("errcode");
        if(errcode == 0) {
            return obj.getString("access_token");
        } else {
            return null;
        }
    }
}
