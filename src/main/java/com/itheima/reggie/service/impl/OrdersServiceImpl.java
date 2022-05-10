package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.core.toolkit.IdWorker;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.*;
import com.itheima.reggie.entity.dto.OrderDto;
import com.itheima.reggie.entity.dto.OrdersDetailDto;
import com.itheima.reggie.exception.CustomException;
import com.itheima.reggie.mapper.OrdersMapper;
import com.itheima.reggie.service.*;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.List;
import java.util.Objects;
import java.util.concurrent.atomic.AtomicInteger;
import java.util.stream.Collectors;

/**
 * 订单表(Orders)表服务实现类
 *
 * @author makejava
 * @since 2022-05-10 13:05:27
 */
@Service("ordersService")
@Transactional
public class OrdersServiceImpl extends ServiceImpl<OrdersMapper, Orders> implements OrdersService {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @Autowired
    private UserService userService;

    @Autowired
    private AddressBookService addressBookService;

    @Autowired
    private OrderDetailService orderDetailService;

    /**
     * 用户下单
     * @param orders
     * @return
     */
    @Override
    public R<String> submit(Orders orders) {
        //获得是哪个用户进行下单
        Long userId = BaseContext.getCurrentId();
        //查询当前用户的购物车数据 通过userId
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        List<ShoppingCart> shoppingCartList = shoppingCartService.list(queryWrapper);
        if (Objects.isNull(shoppingCartList) || shoppingCartList.size() == 0){
            throw  new CustomException("购物车为空，不能下单");
        }
        //根据用户id查询用户信息
        User user = userService.getById(userId);
        //查询地址表
        Long addressBookId = orders.getAddressBookId();
        AddressBook addressBook = addressBookService.getById(addressBookId);
        if (Objects.isNull(addressBook)){
            throw  new CustomException("用户地址信息错误");
        }
        //完成下单操作，insert
        long orderId = IdWorker.getId();
        AtomicInteger amount = new AtomicInteger(0);
        //计算总金额
        List<OrderDetail> collect = shoppingCartList.stream()
                .map(item -> {
                    OrderDetail orderDetail = new OrderDetail();
                    orderDetail.setOrderId(orderId);
                    orderDetail.setNumber(item.getNumber());
                    orderDetail.setDishFlavor(item.getDishFlavor());
                    orderDetail.setDishId(item.getDishId());
                    orderDetail.setSetmealId(item.getSetmealId());
                    orderDetail.setName(item.getName());
                    orderDetail.setImage(item.getImage());
                    orderDetail.setAmount(item.getAmount());
                    //item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue()
                    amount.addAndGet(item.getAmount().multiply(new BigDecimal(item.getNumber())).intValue());
                    return orderDetail;
                })
                .collect(Collectors.toList());

        orders.setOrderTime(LocalDateTime.now());
        orders.setCheckoutTime(LocalDateTime.now());
        orders.setStatus(2);
        orders.setAmount(new BigDecimal(amount.get()));//总金额
        orders.setUserId(userId);
        orders.setNumber(String.valueOf(orderId));
        orders.setUserName(user.getName());
        orders.setConsignee(addressBook.getConsignee());
        orders.setPhone(addressBook.getPhone());
        orders.setAddress((addressBook.getProvinceName() == null ? "" : addressBook.getProvinceName())
                + (addressBook.getCityName() == null ? "" : addressBook.getCityName())
                + (addressBook.getDistrictName() == null ? "" : addressBook.getDistrictName())
                + (addressBook.getDetail() == null ? "" : addressBook.getDetail()));
        save(orders);

        //向明细表插入数据
        orderDetailService.saveBatch(collect);

        //清空购物车数据
        shoppingCartService.remove(queryWrapper);
        return R.success("支付成功");
    }

    /**
     * 用户订单信息
     * @param page
     * @param pageSize
     * @return
     */
    @Override
    public R<Page<OrderDto>> pageOrderDetail(Integer page, Integer pageSize) {
        //分页查询器
        Page<Orders> ordersPage = new Page<>(page,pageSize);
        Page<OrderDto> orderDtoPage = new Page<>();
        //条件查询器
        Long userId = BaseContext.getCurrentId();
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        //根据用户id查询所有订单
        queryWrapper.eq(Orders::getUserId,userId);
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        page(ordersPage, queryWrapper);
        BeanUtils.copyProperties(ordersPage,orderDtoPage,"records");
        List<Orders> records = ordersPage.getRecords();
        List<OrderDto> collect = records.stream()
                .map(item -> {
                    OrderDto orderDto = new OrderDto();
                    BeanUtils.copyProperties(item, orderDto);
                    //根据订单号查询订单信息
                    String orderMsg = item.getNumber();
                    //去订单明细表查询对应的订单信息
                    LambdaQueryWrapper<OrderDetail> lambdaQueryWrapper = new LambdaQueryWrapper<>();
                    lambdaQueryWrapper.eq(OrderDetail::getOrderId, orderMsg);
                    List<OrderDetail> orderDetailList = orderDetailService.list(lambdaQueryWrapper);
                    if (Objects.nonNull(orderDetailList)) {
                        orderDto.setOrderDetails(orderDetailList);
                    }
                    return orderDto;
                })
                .collect(Collectors.toList());
        orderDtoPage.setRecords(collect);


        return R.success(orderDtoPage);
    }

    @Override
    public R<Page<Orders>> pageOrderList(OrdersDetailDto ordersDetailDto) {
        Integer page = ordersDetailDto.getPage();
        Integer pageSize = ordersDetailDto.getPageSize();
        LocalDateTime beginTime = ordersDetailDto.getBeginTime();
        LocalDateTime endTime = ordersDetailDto.getEndTime();
        //分页构造器
        Page<Orders> pageInfo = new Page<>(page,pageSize);
        //条件构造器
        LambdaQueryWrapper<Orders> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ordersDetailDto.getNumber() != null,Orders::getNumber,ordersDetailDto.getNumber());
        //订单开始时间和结束时间都不为空
        queryWrapper.between(Objects.nonNull(ordersDetailDto.getBeginTime()) && Objects.nonNull(ordersDetailDto.getEndTime())
                ,Orders::getCheckoutTime,beginTime,endTime);
        queryWrapper.orderByDesc(Orders::getCheckoutTime);
        page(pageInfo,queryWrapper);
        List<Orders> records = pageInfo.getRecords();
        records.stream()
                .map(item -> {
                    //根据订单中的用户id查询用户信息
                    User user = userService.getById(item.getUserId());
                    item.setUserName(user.getName());
                    return item;
                })
                .collect(Collectors.toList());

        pageInfo.setRecords(records);
        return R.success(pageInfo);
    }

    /**
     * 修改订单状态
     * @param orders
     * @return
     */
    @Override
    public R<String> changeStatus(Orders orders) {
        //判断订单修改状态是什么
        //修改订单状态 ---3 update from orders  set status = ? where id = ?
        if (orders.getStatus() == 3){
            //修改状态为已派送
            boolean b = updateById(orders);
            if (!b){
                throw new CustomException("修改状态失败");
            }
        }else if (orders.getStatus() == 4){
            //修改状态为已完成
            updateById(orders);
        }


        return R.success("修改状态成功");
    }
}
