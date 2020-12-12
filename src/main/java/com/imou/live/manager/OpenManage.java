package com.imou.live.manager;

import com.alibaba.fastjson.JSON;
import com.alibaba.fastjson.JSONObject;
import com.imou.live.common.CONST;
import com.imou.live.utils.open.MySecureProtocolSocketFactory;
import lombok.Getter;
import lombok.Setter;
import org.apache.commons.codec.digest.DigestUtils;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.methods.PostMethod;
import org.apache.commons.httpclient.methods.RequestEntity;
import org.apache.commons.httpclient.methods.StringRequestEntity;
import org.apache.commons.httpclient.params.HttpMethodParams;
import org.apache.commons.httpclient.protocol.Protocol;
import org.apache.commons.httpclient.protocol.ProtocolSocketFactory;
import org.apache.commons.io.IOUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.stereotype.Component;

import java.io.InputStream;
import java.util.*;

@Component
@Getter
@Setter
public class OpenManage {
    private Logger logger = LoggerFactory.getLogger(OpenManage.class);


    private String host = CONST.OPEN_SYSTEM.HOST;

    private String ver = "1.0";

    public JSONObject HttpSendMethod(Map<String, Object> paramsMap, String method, String appId, String appSecret) {
        Map<String, Object> map = new HashMap<String, Object>();
        map = paramsInit(paramsMap, appId, appSecret);
        // 返回json
        JSONObject jsonObj = doPost(host + "/openapi/" + method, map);
        logger.info("=============================");
        logger.info("返回结果：" + jsonObj.toJSONString());

        return jsonObj;

    }

    public JSONObject doPost(String url, Map<String, Object> map) {
        String json = JSON.toJSONString(map);
        ProtocolSocketFactory fcty = new MySecureProtocolSocketFactory();
        Protocol.registerProtocol("https", new Protocol("https", fcty, 8080));
        HttpClient client = new HttpClient();
        client.getParams().setParameter(HttpMethodParams.HTTP_CONTENT_CHARSET, "UTF-8");
        PostMethod method = new PostMethod(url);
        String restult = "";
        JSONObject jsonObject = new JSONObject();
        try {
            RequestEntity entity = new StringRequestEntity(json, "application/json", "UTF-8");
            method.setRequestEntity(entity);
            client.executeMethod(method);

            InputStream inputStream = method.getResponseBodyAsStream();
            restult = IOUtils.toString(inputStream, "UTF-8");
            jsonObject = JSONObject.parseObject(restult);
        } catch (Exception e) {
            e.printStackTrace();
        } finally {
            method.releaseConnection();
        }
        return jsonObject;
    }

    protected Map<String, Object> paramsInit(Map<String, Object> paramsMap, String appId, String appSecret) {
        Map<String, Object> map = new HashMap<String, Object>();
        long time = System.currentTimeMillis() / 1000;
        String nonce = UUID.randomUUID().toString();
        String id = UUID.randomUUID().toString();
        String sign = StringUtils.EMPTY;
        //
        StringBuilder paramString = new StringBuilder();
        List<String> paramList = new ArrayList<String>();
        for (String key : paramsMap.keySet()) {
            String param = key + ":" + paramsMap.get(key);
            paramList.add(param);
        }
        /**
         * 为计算签名串，参数按照字母升序排列 第一步：计算“签名原始串 将params部分的deviceId、accessToken、userId、phone
         * 按字母升序排序，按逗号分割组成无空格字符串，并在字符串最后拼接time、nonce、appSecret， 第二步：计算MD5值
         */
        String[] params = paramList.toArray(new String[paramList.size()]);
        Arrays.sort(params);
        for (String param : params) {
            paramString.append(param).append(",");
        }
        paramString.append("time:").append(time).append(",");
        paramString.append("nonce:").append(nonce).append(",");
        paramString.append("appSecret:").append(appSecret);

        // 计算MD5得值
        try {
            System.out.println("传入参数：" + paramString.toString().trim());
            System.out.println("sign值[" + paramString.toString().trim() + "]");
            sign = DigestUtils.md5Hex(paramString.toString().trim().getBytes("UTF-8"));
        } catch (Exception e) {
            e.printStackTrace();
        }

        Map<String, Object> systemMap = new HashMap<String, Object>();
        systemMap.put("ver", ver);
        systemMap.put("sign", sign);
        systemMap.put("appId", appId);
        systemMap.put("nonce", nonce);
        systemMap.put("time", time);
        map.put("system", systemMap);
        map.put("params", paramsMap);
        map.put("id", id);
        return map;
    }

}
