package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;

import javax.servlet.http.HttpSession;
import java.util.Map;


/**
 * 用户信息(User)表服务接口
 *
 * @author makejava
 * @since 2022-05-09 20:20:58
 */
public interface UserService extends IService<User> {

    R<String> sendMsg(User user, HttpSession session);

    R<User> login(Map map, HttpSession session);

}
