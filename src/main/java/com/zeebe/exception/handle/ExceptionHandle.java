package com.zeebe.exception.handle;

import com.zeebe.dto.ResponseJson;
import com.zeebe.exception.ServiceException;
import com.zeebe.utils.ResponseUtil;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

@RestControllerAdvice
public class ExceptionHandle{

    @ExceptionHandler(ServiceException.class)
    public ResponseJson handle(Exception e){
        return ResponseUtil.innerError(e.getMessage());
    }

}
