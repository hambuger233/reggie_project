package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;

import java.util.List;


/**
 * 购物车(ShoppingCart)表服务接口
 *
 * @author makejava
 * @since 2022-05-10 10:51:38
 */
public interface ShoppingCartService extends IService<ShoppingCart> {

    R<ShoppingCart> addShoppingCart(ShoppingCart shoppingCart);

    R<List<ShoppingCart>> getShoppingCartList();

    R<String> cleanShoppingCartList();

}
