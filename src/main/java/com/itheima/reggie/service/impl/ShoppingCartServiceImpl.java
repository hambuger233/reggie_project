package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.BaseContext;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.exception.CustomException;
import com.itheima.reggie.mapper.ShoppingCartMapper;
import com.itheima.reggie.service.ShoppingCartService;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;

/**
 * 购物车(ShoppingCart)表服务实现类
 *
 * @author makejava
 * @since 2022-05-10 10:51:38
 */
@Service("shoppingCartService")
@Transactional
public class ShoppingCartServiceImpl extends ServiceImpl<ShoppingCartMapper, ShoppingCart> implements ShoppingCartService {

    /**
     * 添加购物车
     * @param shoppingCart
     * @return
     */
    @Override
    public R<ShoppingCart> addShoppingCart(ShoppingCart shoppingCart) {
        //设置用户id，告诉是哪个用户的购物车
        Long userId = BaseContext.getCurrentId();
        shoppingCart.setUserId(userId);
        //查询添加的菜品或者套餐在不在购物车内，如果在购物车内
        Long dishId = shoppingCart.getDishId();
        Long setmealId = shoppingCart.getSetmealId();
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        if (dishId != null){
            //则添加到购物车的是菜品   select * from shopping_cart where userId = ? and dishId = ?
            queryWrapper.eq(ShoppingCart::getDishId,dishId);
        }else {
            //否则添加到购物车的是套餐  select * from shopping_cart where userId = ? and setmealId = ?
            queryWrapper.eq(ShoppingCart::getSetmealId,setmealId);
        }
        ShoppingCart shoppingCartOne = getOne(queryWrapper);
        //判断是否存在

        if (Objects.nonNull(shoppingCartOne)){
            //如果在，则只增加数量 insert into from shopping_cart values(?,?,?,?,?,?,?) where userId = ? and dishId
            Integer beforeNumber = shoppingCartOne.getNumber();
            shoppingCartOne.setNumber(beforeNumber+1);
            updateById(shoppingCartOne);
        }else {
            //如果不存在，则添加到购物车,数量默认是1
            shoppingCart.setNumber(1);
            save(shoppingCart);
            shoppingCartOne = shoppingCart;

        }
        return R.success(shoppingCartOne);
    }

    @Override
    public R<List<ShoppingCart>> getShoppingCartList() {
        //根据用户id，查询对应的购物车
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        Long userId = BaseContext.getCurrentId();
        queryWrapper.eq(ShoppingCart::getUserId,userId);
        queryWrapper.orderByAsc(ShoppingCart::getCreateTime);
        List<ShoppingCart> list = list(queryWrapper);
        return R.success(list);
    }

    @Override
    public R<String> cleanShoppingCartList() {
        Long uerId = BaseContext.getCurrentId();
        //根据用户id，删除对应的购物车信息
        LambdaQueryWrapper<ShoppingCart> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(ShoppingCart::getUserId,uerId);
        boolean remove = remove(queryWrapper);
        if (!remove){
            throw new CustomException("删除失败");
        }
        return R.success("清空购物车成功");
    }
}
