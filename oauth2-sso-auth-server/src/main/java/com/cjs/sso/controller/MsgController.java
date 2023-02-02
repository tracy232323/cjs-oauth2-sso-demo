package com.cjs.sso.controller;

import com.cjs.sso.aes.SHA1;
import com.cjs.sso.aes.WXBizMsgCrypt;
import com.cjs.sso.aes.XMLParse;
import com.cjs.sso.qw.QWService;
import org.dom4j.Document;
import org.dom4j.DocumentHelper;
import org.dom4j.Element;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.client.RestTemplate;

import javax.annotation.Resource;
import java.net.URLEncoder;
import java.text.SimpleDateFormat;
import java.util.Date;

@RestController
public class MsgController {
    private static final String TOKEN = "Q7Ih1LG9JWtATzfBIGl3IIWr2B95rd";
    private static final String AES_KEY = "3eROs9P7GJNdfkbkS4UhFdLVezIDVqULhx0SWLULjQv";
    private static final String CORPID = "wl49366340eb";

    @Resource
    private RestTemplate restTemplate;

    @Resource
    private QWService qwService;

    @GetMapping("/message/send")
    public Object send(String msg_signature, String timestamp, String nonce, String echostr) {
        try {
            String msg = new WXBizMsgCrypt(TOKEN, AES_KEY, CORPID).VerifyURL(msg_signature, timestamp, nonce, echostr);
            return msg;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }
    }

    @PostMapping("/message/send")
    public String messageP(@RequestBody String message, String msg_signature, String timestamp, String nonce) {
        try {
            WXBizMsgCrypt msgCrypt = new WXBizMsgCrypt(TOKEN, AES_KEY, CORPID);
            String sign1 = SHA1.getSHA1(TOKEN, timestamp, nonce, (String) XMLParse.extract(message)[1]);
            //签名验证
            if (msg_signature.equals(sign1)) {
                String msg = msgCrypt.DecryptMsg(msg_signature, timestamp, nonce, message);
                Document document = DocumentHelper.parseText(msg);
                //获取根节点,在例子中就是responsedata节点
                Element rootElement = document.getRootElement();
                String content = rootElement.element("Content").getStringValue();
                if ("值班".equals(content)) {
                    SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd");
                    String current = sdf.format(new Date());
                    String rep = "当天的值班人员是：" + qwService.getCurrentUser(current) + "；值班时间为：" + current;
                    rootElement.element("Content").setText(rep);
                    //document转成xml格式才能成功回复
                    String value = document.asXML();
                    return msgCrypt.EncryptMsg(value, timestamp, nonce);
                }
            }
            return null;
        } catch (Exception e) {
            throw new RuntimeException(e);
        }

    }

}