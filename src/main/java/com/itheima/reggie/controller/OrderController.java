package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.dto.OrderDto;
import com.itheima.reggie.entity.dto.OrdersDetailDto;
import com.itheima.reggie.service.OrdersService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.web.bind.annotation.*;

import java.time.LocalDateTime;

@RestController
@Slf4j
@RequestMapping("/order")
public class OrderController {

    @Autowired
    private OrdersService ordersService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @PostMapping("/submit")
    public R<String> submit(@RequestBody Orders orders){
        return ordersService.submit(orders);
    }


    @GetMapping("/userPage")
    public R<Page<OrderDto>> pageOrderDetail(Integer page, Integer pageSize){
            return ordersService.pageOrderDetail(page,pageSize);
    }

    /**
     * 订单明细分页
     *
     * @return
     */
    @GetMapping("/page")
    public R<Page<Orders>> pageOrderList(OrdersDetailDto ordersDetailDto){
//        log.info("page: {},page: {},page: {},page: {},page: {}",page,pageSize,number,beginTime,endTime);
        log.info("ordersDetailDto:{}",ordersDetailDto);
        return ordersService.pageOrderList(ordersDetailDto);
        //Integer page, Integer pageSize, Integer number, LocalDateTime beginTime,LocalDateTime endTime
    }

    @PutMapping
    public R<String> changeStatus(@RequestBody Orders orders){
        log.info("orders:{}",orders);
        return ordersService.changeStatus(orders);
    }
}
