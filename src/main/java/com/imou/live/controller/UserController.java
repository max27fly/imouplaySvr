package com.imou.live.controller;

import com.alibaba.fastjson.JSONObject;
import com.arronlong.httpclientutil.exception.HttpProcessException;
import com.imou.live.common.CONST;
import com.imou.live.common.ResultMap;
import com.imou.live.servcie.OpenService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RestController;


@RestController
public class UserController {

    private static Logger logger = LoggerFactory.getLogger(UserController.class);

    @Autowired
    public OpenService openService;


    /**
     * 开发者账号登录
     *
     * @return
     */
    @PostMapping("/openLogin")
    public ResultMap openLogin(@RequestBody JSONObject jsonParam) throws HttpProcessException {
        ResultMap resultMap = new ResultMap(CONST.RESULT_CG.OK_CODE, CONST.RESULT_CG.OK_MSG);

        //校验appid和appsecret是否为空
        if (jsonParam.get("appid") == null || jsonParam.get("appsecret") == null) {
            resultMap.setCode(CONST.RESULT_CG.PARAM_NULL_CODE);
            resultMap.setMsg(CONST.RESULT_CG.PARAM_NULL_MSG);
            logger.info("请求参数为空:" + jsonParam.toString());
            return resultMap;
        }

        //获取乐橙开放平台accessToken
        String appId = jsonParam.get("appid").toString();
        String appSecret = jsonParam.get("appsecret").toString();
        String accessToken = openService.getAccessToken(appId, appSecret);

        //返回结果给小程序
        if (accessToken.isEmpty()) {
            resultMap.setCode(CONST.RESULT_CG.WX_LCOPENACCOUNT_ERROR_CODE);
            resultMap.setMsg(CONST.RESULT_CG.WX_LCOPENACCOUNT_ERROR_MSG);
        } else {
            resultMap.getData().put("accessToken", accessToken);

        }
        return resultMap;
    }

}
