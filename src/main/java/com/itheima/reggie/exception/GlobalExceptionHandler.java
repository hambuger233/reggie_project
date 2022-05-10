package com.itheima.reggie.exception;

import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import java.sql.SQLIntegrityConstraintViolationException;

@RestControllerAdvice
@Slf4j
public class GlobalExceptionHandler {

        @ExceptionHandler(SQLIntegrityConstraintViolationException.class)
        public R<String> exception(SQLIntegrityConstraintViolationException exception){
            log.error(exception.getMessage());

            if (exception.getMessage().contains("Duplicate entry")){
                String[] s = exception.getMessage().split(" ");
                String msg = s[2].replace("\'","") + "已存在";
                return R.error(msg);
            }
            return R.error("未知错误");
        }


    @ExceptionHandler(CustomException.class)
    public R<String> exception(CustomException exception){
        log.error(exception.getMessage());
        return R.error(exception.getMessage());
    }
}
