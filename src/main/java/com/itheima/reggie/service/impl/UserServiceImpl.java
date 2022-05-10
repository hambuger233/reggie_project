package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.User;
import com.itheima.reggie.mapper.UserMapper;
import com.itheima.reggie.service.UserService;
import com.itheima.reggie.utils.SMSUtils;
import com.itheima.reggie.utils.ValidateCodeUtils;
import lombok.extern.slf4j.Slf4j;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpSession;
import java.util.Map;
import java.util.Objects;

/**
 * 用户信息(User)表服务实现类
 *
 * @author makejava
 * @since 2022-05-09 20:20:58
 */
@Service("userService")
@Slf4j
public class UserServiceImpl extends ServiceImpl<UserMapper, User> implements UserService {

    @Override
    public R<String> sendMsg(User user, HttpSession session) {
        //获取手机号
        String realPhone = user.getPhone();
        String phone = "+86" + realPhone;
        String[] phoneList = new String[]{
            phone
        };
        //生成随机的思维验证码
        if (StringUtils.hasText(phone)){
            String[] code = new String[]{
                    ValidateCodeUtils.generateValidateCode(4).toString()
            };
            log.info("code={}",code);
            //调用api，短信服务完成发送短信
            //SMSUtils.sendMessage(phoneList,code);
            session.setAttribute(realPhone,code);
            return R.success("手机验证码短信发送成功");
        }
        return R.error("短信发送失败");
    }

    @Override
    public R<User> login(Map map, HttpSession session) {
        //获得手机号
        String phone = map.get("phone").toString();
        //获取验证码
        String code = map.get("code").toString();
        //从session中获得验证码
        String[] codeInSession = (String[]) session.getAttribute(phone);
        //进行验证码比对
        if (Objects.nonNull(codeInSession) && codeInSession[0].equals(code)){
            //如果比对通过，则登录成功
            //判断当前手机号是否为新用户，如果是新用户则自动完成注册
            LambdaQueryWrapper<User> queryWrapper = new LambdaQueryWrapper<>();
            queryWrapper.eq(User::getPhone,phone);
            User user = getOne(queryWrapper);
            if (Objects.isNull(user)){
                //用户是新用户
                user = new User();
                user.setPhone(phone);
                user.setStatus(1);
                save(user);
            }
            session.setAttribute("user",user.getId());
            return R.success(user);
        }

        return R.error("验证码错误");
    }
}
