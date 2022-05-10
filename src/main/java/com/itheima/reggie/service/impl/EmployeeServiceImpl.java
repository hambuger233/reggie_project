package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.mapper.EmployeeMapper;
import com.itheima.reggie.service.EmployeeService;
import org.springframework.stereotype.Service;
import org.springframework.util.DigestUtils;
import org.springframework.util.StringUtils;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;
import java.util.Objects;


/**
 * 员工信息(Employee)表服务实现类
 *
 * @author makejava
 * @since 2022-05-08 21:17:17
 */
@Service("employeeService")
public class EmployeeServiceImpl extends ServiceImpl<EmployeeMapper, Employee> implements EmployeeService {

    @Override
    public R<Employee> login(HttpServletRequest request, Employee employee) {
        /*
         * 1、将页面提交的密码password进行MD5加密处理
         * 2、根据页面提交的用户名username查询数据库
         * 3、如果没有查询到则返回登录失败结果
         * 4、密码比对，如果不一致则返回登录失败结果
         * 5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
         * 6、登录成功，则将员工id存入session并且返回登录成功
         * */

        //1、将页面提交的密码password进行MD5加密处理
        String password = employee.getPassword();
        password = DigestUtils.md5DigestAsHex(password.getBytes());
        //2、根据页面提交的用户名username查询数据库
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Employee::getUsername,employee.getUsername());
        Employee emp = getOne(queryWrapper);
        //3、如果没有查询到则返回登录失败结果
        if (Objects.isNull(emp)){
            return R.error("用户不存在");
        }
        //4、密码比对，如果不一致则返回登录失败结果
        if (!password.equals(emp.getPassword())){
            return R.error("密码错误");
        }
        //5、查看员工状态，如果为已禁用状态，则返回员工已禁用结果
        if (emp.getStatus() == 0){
            return R.error("账号已经禁用");
        }
        //6、登录成功，则将员工id存入session并且返回登录成功
        request.getSession().setAttribute("employee",emp.getId());
        return R.success(emp);
    }

    @Override
    public R<String> logout(HttpServletRequest request) {
        //清空session中的员工信息
        request.getSession().removeAttribute("employee");
        return R.success("退出成功");
    }

    @Override
    public R<Page> pageEmployeeList(Integer page, Integer pageSize, String name) {
        //分页构造器
        Page pageInfo = new Page(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Employee> queryWrapper = new LambdaQueryWrapper<>();
        //添加过滤条件
        queryWrapper.like(StringUtils.hasText(name),Employee::getName,name);
        queryWrapper.orderByDesc(Employee::getUpdateTime);
        page(pageInfo, queryWrapper);
        return R.success(pageInfo);
    }

    @Override
    public R<String> updateEmployee(HttpServletRequest request, Employee employee) {
        Long empId = (Long) request.getSession().getAttribute("employee");
        employee.setUpdateTime(LocalDateTime.now());
        employee.setUpdateUser(empId);
        updateById(employee);
        return R.success("员工信息修改成功");
    }

    @Override
    public R<Employee> getEmployeeById(Long id) {
        Employee employee = getById(id);
        if (Objects.nonNull(employee)){
            return R.success(employee);
        }
        return R.error("没有查询到对应的员工信息");
    }
}
