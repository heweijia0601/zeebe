package com.zeebe.utils;


import com.zeebe.dto.ResponseCode;
import com.zeebe.dto.ResponseJson;

public class ResponseUtil {
    public static ResponseJson ok(){
        return new ResponseJson(ResponseCode.OK.getCode(),ResponseCode.OK.toString(),null);
    }

    public static ResponseJson ok(Object data){
        return new ResponseJson(ResponseCode.OK.getCode(),ResponseCode.OK.toString(),data);
    }

    public static ResponseJson error(){
        return new ResponseJson(ResponseCode.ERROR.getCode(),ResponseCode.ERROR.toString(),null);
    }

    public static ResponseJson error(String msg){
        return new ResponseJson(ResponseCode.ERROR.getCode(),msg,null);
    }

    public static ResponseJson innerError(String msg) { return new ResponseJson(ResponseCode.INNER_ERROR.getCode(),msg,null); }

    public static ResponseJson invaildToken(){
        return new ResponseJson(ResponseCode.INVALID_TOKEN.getCode(),ResponseCode.INVALID_TOKEN.toString(),null);
    }
}
