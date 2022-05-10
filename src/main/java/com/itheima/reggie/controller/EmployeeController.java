package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Employee;
import com.itheima.reggie.service.EmployeeService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.util.DigestUtils;
import org.springframework.web.bind.annotation.*;

import javax.servlet.http.HttpServletRequest;
import java.time.LocalDateTime;

@Slf4j
@RestController
@RequestMapping("/employee")
public class EmployeeController {


    @Autowired
    private EmployeeService employeeService;


    /**
     * 员工登录
     * @param request
     * @param employee
     * @return
     */
    @PostMapping("/login")
    public R<Employee> login(HttpServletRequest request, @RequestBody Employee employee){
        return employeeService.login(request,employee);
    }


    /**
     * 员工退出功能
     * @param request
     * @return
     */
    @PostMapping("/logout")
    public R<String> logout(HttpServletRequest request){
        return employeeService.logout(request);
    }


    @PostMapping
    public R<String> save(HttpServletRequest request,@RequestBody Employee employee){
        log.info("新增员工，员工信息:{}",employee.toString());
        String password = DigestUtils.md5DigestAsHex("123456".getBytes());
        employee.setPassword(password);
//        employee.setCreateTime(LocalDateTime.now());
//        employee.setUpdateTime(LocalDateTime.now());
//        Long empId = (Long) request.getSession().getAttribute("employee");
//        employee.setCreateUser(empId);
//        employee.setUpdateUser(empId);
        employeeService.save(employee);
        return R.success("新增员工成功");
    }

    @GetMapping("/page")
    public R<Page>  pageEmployeeList(Integer page,Integer pageSize,String name){
        log.info("page = {},pageSize = {},name = {}",page,pageSize,name);
        return employeeService.pageEmployeeList(page,pageSize,name);
    }


    /**
     * 根据id修改员工信息
     * @param employee
     * @return
     */
    @PutMapping
    public R<String> updateEmployee(HttpServletRequest request,@RequestBody Employee employee){
        log.info(employee.toString());
        return employeeService.updateEmployee(request,employee);
    }


    /**
     * 编辑员工信息，数据回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<Employee> getEmployeeById(@PathVariable("id") Long id){
        log.info("根据id查询员工信息");
        return  employeeService.getEmployeeById(id);
    }

}
