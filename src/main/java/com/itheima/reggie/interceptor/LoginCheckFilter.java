package com.itheima.reggie.interceptor;

import com.alibaba.fastjson.JSON;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import lombok.extern.slf4j.Slf4j;
import org.springframework.util.AntPathMatcher;

import javax.servlet.*;
import javax.servlet.annotation.WebFilter;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import java.io.IOException;
import java.util.Objects;

@Slf4j
@WebFilter(filterName = "loginCheckFilter",urlPatterns = "/*")
public class LoginCheckFilter implements Filter {

    //路径匹配器
    public static final AntPathMatcher PATH_MATCHER = new AntPathMatcher();

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {
        HttpServletRequest request = (HttpServletRequest) servletRequest;
        HttpServletResponse response = (HttpServletResponse) servletResponse;

        //获取本次请求的url
        String requestURI = request.getRequestURI();
        log.info("拦截到请求，{}",requestURI);

        //定义不需要处理的请求
        String[] urlPaths = new String[]{
                "/employee/login",
                "/employee/logout",
                "/backend/**",
                "/front/**",
                "/user/sendMsg",
                "/user/login"
        };

        //判断哪些请求需要处理
        boolean check = check(urlPaths, requestURI);
        //如果不需要处理
        if (check){
            log.info("拦截到请求：{},不需要处理",requestURI);
            filterChain.doFilter(request, response);
            return;
        }
        //判断登录状态，如果已经登录，则放行
        if (Objects.nonNull(request.getSession().getAttribute("employee"))){
            log.info("浏览器用户已经登录，用户id为：{}",request.getSession().getAttribute("employee"));
            Long empId = (Long) request.getSession().getAttribute("employee");
            BaseContext.setCurrentId(empId);
            filterChain.doFilter(request, response);
            return;
        }
        //判断登录状态，如果已经登录，则放行
        if (Objects.nonNull(request.getSession().getAttribute("user"))){
            log.info("移动端用户已经登录，用户id为：{}",request.getSession().getAttribute("user"));
            Long userId = (Long) request.getSession().getAttribute("user");
            BaseContext.setCurrentId(userId);
            filterChain.doFilter(request, response);
            return;
        }
        //未登录状态，则返回登录结果
        response.getWriter().write(JSON.toJSONString(R.error("NOTLOGIN")));
        return;
    }


    /**
     * 检查路径匹配，是否需要放行
     * @param requestURI
     * @return
     */
    private boolean check(String[] urlPaths,String requestURI){
        for (String urlPath : urlPaths) {
            boolean match = PATH_MATCHER.match(urlPath, requestURI);
            if (match){
                return true;
            }
        }
        return false;
    }
}
