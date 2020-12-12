package com.imou.live.servcie;

import com.alibaba.fastjson.JSONArray;
import com.alibaba.fastjson.JSONObject;
import com.imou.live.common.CONST;
import com.imou.live.common.ResultMap;
import com.imou.live.manager.OpenManage;
import com.imou.live.pojo.BO.DeviceChannelInfoBO;
import com.imou.live.pojo.BO.VideoBO;
import org.apache.commons.lang.StringUtils;
import org.apache.logging.log4j.util.Strings;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URLEncoder;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.List;

@Component
public class OpenService {
    private Logger logger = LoggerFactory.getLogger(OpenService.class);


    @Autowired
    private OpenManage openManage;



    /**
     * 调用乐橙开放平台接口获取accessToken
     *
     * @param appId
     * @param appSecret
     * @return
     */
    public String getAccessToken(String appId, String appSecret) {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        JSONObject jsonObject = (JSONObject) openManage.HttpSendMethod(paramsMap, "accessToken", appId, appSecret);

        JSONObject jsonResult = jsonObject.getJSONObject("result");
        JSONObject jsonData = jsonResult.getJSONObject("data");
        if (jsonData != null) {
            return jsonData.getString("accessToken");
        } else {
            return Strings.EMPTY;
        }
    }


    /**
     * 获取设备列表
     *
     * @param appId
     * @param appSecret
     * @param queryRange
     * @return
     */
    public ResultMap videoList(String appId, String appSecret, String queryRange, String accessToken,ResultMap resultMap) {

        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", accessToken);
        paramsMap.put("queryRange", queryRange);


        //乐橙设备列表协议:https://open.imoulife.com/book/zh/http/old/deviceList.html
        JSONObject jsonObject = (JSONObject) openManage.HttpSendMethod(paramsMap, "deviceList", appId, appSecret);
        JSONObject jsonresult = jsonObject.getJSONObject("result");
        String code = jsonresult.getString("code");
        List<VideoBO> videoList = new ArrayList<VideoBO>();


        if ("0".equals(code)) {
            JSONObject dataObject = jsonresult.getJSONObject("data");
            JSONArray devices = JSONObject.parseArray(dataObject.get("devices").toString());
            for (int i = 0; i < devices.size(); i++) {
                JSONObject device = JSONObject.parseObject(devices.getString(i).toString());
                String deviceId = device.getString("deviceId");
                JSONArray channles = JSONObject.parseArray(device.get("channels").toString());
                for (int j = 0; j < channles.size(); j++) {
                    JSONObject channle = JSONObject.parseObject(channles.getString(j).toString());
                    VideoBO bo = new VideoBO();
                    bo.setChannelName(channle.getString("channelName"));
                    bo.setChannelOnline(channle.getBoolean("channelOnline"));
                    bo.setChannelId(channle.getString("channelId"));
                    bo.setDeviceId(deviceId);
                    bo.setPicUrl(channle.getString("channelPicUrl"));//.replace("?", "\\?")
                    bo.setEncodePicUrl(URLEncoder.encode(channle.getString("channelPicUrl")));//.replace("?", "\\?")
                    videoList.add(bo);
                }
            }
            resultMap.getData().put("videolist", videoList);
        } else if ("TK1002".equals(code)) {
            logger.info("token过期提示前端重新登录");
            resultMap.setCode(CONST.RESULT_CG.TOKEN_NOTEXIST_CODE);
            resultMap.setMsg(CONST.RESULT_CG.TOKEN_NOTEXISTL_MSG);
        } else {
            logger.info("获取设备列表失败!" + jsonObject);
            resultMap.setCode(CONST.RESULT_CG.PARAM_NULL_CODE);
            resultMap.setMsg(CONST.RESULT_CG.PARAM_NULL_MSG);
        }
        return resultMap;
    }


    /**
     * 根据设备序列号获取设备信息
     * @param accessToken
     * @param appId
     * @param appSecret
     * @param deviceId
     * @return
     */
    public List<VideoBO> deviceInfo(String accessToken, String appId, String appSecret, String deviceId) {
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", accessToken);
        paramsMap.put("deviceId", deviceId);

        JSONObject jsonObject = (JSONObject) openManage.HttpSendMethod(paramsMap, "bindDeviceInfo", appId, appSecret);
        JSONObject jsonresult = jsonObject.getJSONObject("result");
        String code = jsonresult.getString("code");
        List<VideoBO> videoList = new ArrayList<VideoBO>();


        if ("0".equals(code)) {
            JSONObject device = jsonresult.getJSONObject("data");
            String did = device.getString("deviceId");
            JSONArray channles = JSONObject.parseArray(device.get("channels").toString());
            for (int j = 0; j < channles.size(); j++) {
                JSONObject channle = JSONObject.parseObject(channles.getString(j).toString());
                VideoBO bo = new VideoBO();
                bo.setChannelName(channle.getString("channelName"));
                bo.setChannelOnline(channle.getBoolean("channelOnline"));
                bo.setChannelId(channle.getString("channelId"));
                bo.setDeviceId(did);
                bo.setPicUrl(channle.getString("channelPicUrl"));
                videoList.add(bo);
            }
        } else {
            logger.info("获取设备信息失败!" + jsonObject);
            return null;
        }
        return videoList;
    }


    /**
     * 获取直播的一次性kitToken
     *
     * @param appId
     * @param appSecret
     * @param deviceId
     * @param channelId
     * @return
     */
    public ResultMap getKitToken(String accessToken, String appId, String appSecret, String deviceId, String channelId) {
        ResultMap resultMap = new ResultMap(CONST.RESULT_CG.OK_CODE, CONST.RESULT_CG.OK_MSG);
        HashMap<String, Object> paramsMap = new HashMap<String, Object>();
        paramsMap.put("token", accessToken);
        paramsMap.put("deviceId", deviceId);
        paramsMap.put("channelId", channelId);
        paramsMap.put("type", "1");


        JSONObject jsonObject = (JSONObject) openManage.HttpSendMethod(paramsMap, "getKitToken", appId, appSecret);
        JSONObject jsonResult = jsonObject.getJSONObject("result");
        String code = jsonResult.getString("code");

        if ("0".equals(code)) {
            JSONObject jsonData = jsonResult.getJSONObject("data");
            resultMap.getData().put("kitToken", jsonData.getString("kitToken"));
        } else if ("DV1007".equals(code)) {
            resultMap.setCode(CONST.RESULT_CG.BS_DEVICE_OFFLINE_CODE);
            resultMap.setMsg(CONST.RESULT_CG.BS_DEVICE_OFFLINE_MSG);
            logger.info("获取kitToken失败!" + jsonObject);
        } else {
            resultMap.setCode(CONST.RESULT_CG.BS_ERROR_CODE);
            resultMap.setMsg(CONST.RESULT_CG.BS_ERROR_MSG);
            logger.info("获取kitToken失败!" + jsonObject);
        }
        return resultMap;
    }


}
