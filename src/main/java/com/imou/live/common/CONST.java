package com.imou.live.common;


public class CONST {
    public static final class RESULT_CG {
        public static final String OK_CODE = "0";
        public static final String OK_MSG = "操作成功";

        public static final String TOKEN_NOTEXIST_CODE = "1001";
        public static final String TOKEN_NOTEXISTL_MSG = "token不存在或过期";

        public static final String PARAM_NULL_CODE = "1002";
        public static final String PARAM_NULL_MSG = "请求参数为空";

        public static final String WX_LCOPENACCOUNT_ERROR_CODE = "1003";
        public static final String WX_LCOPENACCOUNT_ERROR_MSG = "乐橙开发者账号不正确,请输入正确的appid和appsecret";

        public static final String BS_ERROR_CODE = "1004";
        public static final String BS_ERROR_MSG = "业务异常,请稍后重试!";

        public static final String BS_DEVICE_OFFLINE_CODE = "1005";
        public static final String BS_DEVICE_OFFLINE_MSG = "设备离线,无法播放,请检查设备和网络!";

    }


    public static final class OPEN_SYSTEM {

        public static final String HOST = "https://openapi.lechange.cn:443";

        public static final String APPID = "";

        public static final String SECRET = "";

    }

}
