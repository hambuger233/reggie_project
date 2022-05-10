package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import com.itheima.reggie.entity.dto.OrderDto;
import com.itheima.reggie.entity.dto.OrdersDetailDto;


/**
 * 订单表(Orders)表服务接口
 *
 * @author makejava
 * @since 2022-05-10 13:05:27
 */
public interface OrdersService extends IService<Orders> {

    R<String> submit(Orders orders);

    R<Page<OrderDto>> pageOrderDetail(Integer page, Integer pageSize);

    R<Page<Orders>> pageOrderList(OrdersDetailDto ordersDetailDto);

    R<String> changeStatus(Orders orders);

}
