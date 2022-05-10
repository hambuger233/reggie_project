package com.itheima.reggie.entity.dto;

import com.itheima.reggie.entity.OrderDetail;
import com.itheima.reggie.entity.Orders;
import lombok.Data;

import java.math.BigDecimal;
import java.util.List;

@Data
public class OrderDto extends Orders {

    private BigDecimal sumNum;

    private List<OrderDetail> orderDetails;

}
