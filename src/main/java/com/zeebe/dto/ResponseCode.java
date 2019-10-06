package com.zeebe.dto;

public enum ResponseCode {
    OK(2000), ERROR(5000),INVALID_AUTH_HEADER(5001),INVALID_TOKEN(5002),INNER_ERROR(5003);

    private int code;

    ResponseCode(int code) {
        this.code = code;
    }

    public int getCode() {
        return code;
    }
}
