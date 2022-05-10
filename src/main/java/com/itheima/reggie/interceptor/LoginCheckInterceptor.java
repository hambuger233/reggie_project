//package com.itheima.reggie.interceptor;
//
//import com.alibaba.fastjson.JSON;
//import com.itheima.reggie.common.R;
//import lombok.extern.slf4j.Slf4j;
//import org.springframework.stereotype.Component;
//import org.springframework.web.servlet.HandlerInterceptor;
//
//import javax.servlet.http.HttpServletRequest;
//import javax.servlet.http.HttpServletResponse;
//import java.util.Objects;
//
//
///**
// * 登录验证拦截器
// */
//@Component
//@Slf4j
//public class LoginCheckInterceptor implements HandlerInterceptor {
//
//    @Override
//    public boolean preHandle(HttpServletRequest request, HttpServletResponse response, Object handler) throws Exception {
//        log.info("拦截到请求：{}",request.getRequestURI());
//        //获取请求，判断登录状态，如果是已经登录，则放行
//        if (request.getSession().getAttribute("employee") != null){
//            return true;
//        }
//        //未登录则返回登录页面,通过输出流响应给前端
//        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
//        return false;
//    }
//}
