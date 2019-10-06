package com.zeebe.constant;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;

@Component
public class ConstantProperties {
    @Value("F:\\zeebe\\src\\main\\resources")
    private String uploadDir;

//    @Value("${redirect-url}")
//    private String redirectUrl;

    public String getUploadDir() {
        return uploadDir;
    }

    public void setUploadDir(String uploadDir) {
        this.uploadDir = uploadDir;
    }

//    public String getRedirectUrl() {
//        return redirectUrl;
//    }
//
//    public void setRedirectUrl(String redirectUrl) {
//        this.redirectUrl = redirectUrl;
//    }
}
