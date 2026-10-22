package com.rookie.rookiemeeting.common.execption;

import com.rookie.rookiemeeting.common.lang.Constants;
import com.rookie.rookiemeeting.common.lang.Result;
import lombok.extern.slf4j.Slf4j;
import org.springframework.dao.DuplicateKeyException;
import org.springframework.http.converter.HttpMessageNotReadableException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.ResponseBody;
import org.springframework.web.bind.annotation.RestControllerAdvice;

/**
 * 全局异常捕获
 */
@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {


    @ExceptionHandler(ServiceException.class)
    @ResponseBody
    public Result handle(ServiceException serviceException) {
        return Result.fail(serviceException.getCode(), serviceException.getMessage());
    }

    //违反唯一约束，比如账号重名
    @ExceptionHandler(DuplicateKeyException.class)
    @ResponseBody
    public Result handle(DuplicateKeyException e) {
        log.warn("数据重复", e);
        return Result.fail("数据重复，比如账号或角色标识已经存在");
    }

    //请求体格式不对，比如时间格式不是 yyyy-MM-dd HH:mm:ss
    @ExceptionHandler(HttpMessageNotReadableException.class)
    @ResponseBody
    public Result handle(HttpMessageNotReadableException e) {
        log.warn("请求格式错误", e);
        return Result.fail("请求数据格式不正确");
    }

    //其他没处理的异常统一返回 500，前端能显示出错信息，而不是只看到网络错误
    @ExceptionHandler(Exception.class)
    @ResponseBody
    public Result handle(Exception e) {
        log.error("服务器内部错误", e);
        return Result.fail(Constants.CODE_500, "服务器内部错误：" + e.getMessage());
    }

}
