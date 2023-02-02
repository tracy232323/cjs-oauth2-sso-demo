package com.cjs.sso.qw.impl;

import com.alibaba.fastjson.JSONObject;
import com.cjs.sso.qw.QWService;
import com.cjs.sso.qw.QWTokenHandler;
import org.apache.commons.lang3.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestTemplate;

@Service
public class QWServiceImpl implements QWService {
    @Autowired
    private QWTokenHandler qwTokenHandler;
    @Autowired
    private RestTemplate restTemplate;

    @Override
    public String getuserinfo(String code) {
        String token = QWTokenHandler.getToken(restTemplate);
        if(StringUtils.isNotBlank(token)) {
            String url = "http://10.11.134.71/cgi-bin/user/getuserinfo?access_token="+token+"&code=" + code;
            String result = restTemplate.getForObject(url, String.class);
            JSONObject obj = JSONObject.parseObject(result);
            Integer errcode = obj.getInteger("errcode");
            if(errcode == 0) {
                String userId = obj.getString("UserId");
                String uurl ="http://10.11.134.71/cgi-bin/user/get?access_token="+token+"&userid="+userId+"&avatar_addr=AVATAR_ADDR";
                String resutUser = restTemplate.getForObject(uurl, String.class);
                JSONObject object = JSONObject.parseObject(resutUser);
                System.out.println("----user detail----" + resutUser);
                return object.getString("mobile");
            }
        }
        return null;
    }

    @Override
    public String getCurrentUser(String current) {
        return "涂刚";
    }
}
