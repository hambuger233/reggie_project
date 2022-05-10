package com.itheima.reggie.config;

import com.baomidou.mybatisplus.core.handlers.MetaObjectHandler;
import com.itheima.reggie.common.BaseContext;
import lombok.extern.slf4j.Slf4j;
import org.apache.ibatis.reflection.MetaObject;
import org.springframework.stereotype.Component;

import java.time.LocalDateTime;

@Component
@Slf4j
public class MyMetaObjectHandler implements MetaObjectHandler {

    @Override
    public void insertFill(MetaObject metaObject) {
        log.info("公共字段自动填充【insert】....");

        this.setFieldValByName("createTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("createUser", BaseContext.getCurrentId(), metaObject);
        this.setFieldValByName("updateUser", BaseContext.getCurrentId(), metaObject);

    }

    @Override
    public void updateFill(MetaObject metaObject) {
        log.info("公共字段自动填充【update】....");
        this.setFieldValByName("updateTime", LocalDateTime.now(), metaObject);
        this.setFieldValByName("updateUser", BaseContext.getCurrentId(), metaObject);

    }

}