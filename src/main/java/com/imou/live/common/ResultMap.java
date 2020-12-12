package com.imou.live.common;

import com.alibaba.fastjson.JSONObject;
import lombok.Getter;
import lombok.Setter;

import java.io.Serializable;

/**
 * Created by max on 2020/11/22.
 */

@Getter
@Setter
public class ResultMap implements Serializable {
    private String code;

    private String msg;

    private JSONObject data;

    public ResultMap(String code, String msg) {
        this.code = code;
        this.msg = msg;
        this.data = new JSONObject();
    }

    @Override
    public String toString() {
        return "ResultMap{" +
                "code='" + code + '\'' +
                ", msg='" + msg + '\'' +
                ", data=" + data +
                '}';
    }
}
