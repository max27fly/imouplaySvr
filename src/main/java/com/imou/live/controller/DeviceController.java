package com.imou.live.controller;

import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import com.imou.live.common.CONST;
import com.imou.live.common.ResultMap;
import com.imou.live.pojo.BO.*;
import com.imou.live.servcie.OpenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;

/**
 * Created by max on 2020/11/22
 */
@RestController
public class DeviceController {

    private static Logger logger = LoggerFactory.getLogger(DeviceController.class);


    @Autowired
    public OpenService openService;

    /**
     * 获取设备视频通道列表(默认加载)
     *
     * @param jsonParam
     * @return 视频通道列表
     */
    @PostMapping("/videoList")
    public ResultMap videoList(@RequestBody JSONObject jsonParam) throws HttpProcessException {
        ResultMap resultMap = new ResultMap(CONST.RESULT_CG.OK_CODE, CONST.RESULT_CG.OK_MSG);

        if (jsonParam.get("accessToken") == null || jsonParam.get("queryRange") == null||jsonParam.get("appId") == null || jsonParam.get("appSecret") == null) {
            resultMap.setCode(CONST.RESULT_CG.PARAM_NULL_CODE);
            resultMap.setMsg(CONST.RESULT_CG.PARAM_NULL_MSG);
            logger.info("请求参数为空:" + jsonParam.toString());
            return resultMap;
        }

        String appId = jsonParam.get("appId").toString();
        String appSecret = jsonParam.get("appSecret").toString();
        String queryRange = jsonParam.get("queryRange").toString();
        String accessToken = jsonParam.get("accessToken").toString();
        //分页获取设备列表
        resultMap = openService.videoList(appId, appSecret, queryRange,accessToken,resultMap);


        return resultMap;


    }





    /**
     * 搜索单个设备通道列表信息
     *
     * @param jsonParam
     * @return
     * @throws HttpProcessException
     */
    @PostMapping("/searchVideoList")
    public ResultMap searchVideoList(@RequestBody JSONObject jsonParam) throws HttpProcessException {
        ResultMap resultMap = new ResultMap(CONST.RESULT_CG.OK_CODE, CONST.RESULT_CG.OK_MSG);

        if (jsonParam.get("accessToken") == null || jsonParam.get("deviceId") == null||jsonParam.get("appId") == null || jsonParam.get("appSecret") == null) {
            resultMap.setCode(CONST.RESULT_CG.PARAM_NULL_CODE);
            resultMap.setMsg(CONST.RESULT_CG.PARAM_NULL_MSG);
            logger.info("请求参数为空:" + jsonParam.toString());
            return resultMap;
        }

        String accessToken = jsonParam.get("accessToken").toString();
        String appId = jsonParam.get("appId").toString();
        String appSecret = jsonParam.get("appSecret").toString();
        String deviceId = jsonParam.get("deviceId").toString();
        //根据设备序列号获取设备信息
        List<VideoBO> videolist = openService.deviceInfo(accessToken,appId, appSecret, deviceId);

        //返回结果
        resultMap.getData().put("videolist", videolist);
        return resultMap;


    }




    /**
     * 获取轻应用kittoken
     *
     * @param jsonParam
     * @return
     * @throws HttpProcessException
     */
    @PostMapping("/getKitToken")
    public ResultMap getKitToken(@RequestBody JSONObject jsonParam) throws HttpProcessException {
        ResultMap resultMap = new ResultMap(CONST.RESULT_CG.OK_CODE, CONST.RESULT_CG.OK_MSG);

        if (jsonParam.get("accessToken") == null || jsonParam.get("deviceId") == null || jsonParam.get("channelId") == null ||jsonParam.get("appId") == null || jsonParam.get("appSecret") == null) {
            resultMap.setCode(CONST.RESULT_CG.PARAM_NULL_CODE);
            resultMap.setMsg(CONST.RESULT_CG.PARAM_NULL_MSG);
            logger.info("请求参数为空:" + jsonParam.toString());
            return resultMap;
        }

        String appId = jsonParam.get("appId").toString();
        String appSecret = jsonParam.get("appSecret").toString();
        String deviceId = jsonParam.get("deviceId").toString();
        String channelId = jsonParam.get("channelId").toString();
        String accessToken = jsonParam.get("accessToken").toString();
        //获取轻应用播放token
        ResultMap kitTokenResultMap = openService.getKitToken(accessToken,appId, appSecret, deviceId, channelId);
        if (!kitTokenResultMap.getCode().equals(CONST.RESULT_CG.OK_CODE)) {
            resultMap.setCode(kitTokenResultMap.getCode());
            resultMap.setMsg(kitTokenResultMap.getMsg());
            return resultMap;
        }
        resultMap.getData().put("kitToken", kitTokenResultMap.getData().get("kitToken"));

        logger.info("调用getKitAndShareToken接口返回:" + resultMap.toString());
        return resultMap;
    }


}
