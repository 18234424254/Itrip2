package cn.itrip.auth.controller;

import cn.itrip.auth.AuthExcepiton;
import cn.itrip.beans.dto.Dto;
import cn.itrip.common.DtoUtil;
import cn.itrip.common.ErrorCode;
import org.springframework.web.bind.annotation.ControllerAdvice;
import org.springframework.web.bind.annotation.ExceptionHandler;

@ControllerAdvice
public class ExcpetionController {

    @ExceptionHandler(AuthExcepiton.class)
    public Dto handleAuthExcpetion(Exception e){
        e.printStackTrace();
        return DtoUtil.returnFail(e.getMessage(), ErrorCode.AUTH_UNKNOWN);
    }

    @ExceptionHandler(Exception.class)
    public Dto handleExcpetion(Exception e){
        e.printStackTrace();
        return DtoUtil.returnFail("系统未知异常", ErrorCode.AUTH_UNKNOWN);
    }
}
