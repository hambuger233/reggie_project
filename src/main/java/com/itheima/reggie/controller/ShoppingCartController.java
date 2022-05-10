package com.itheima.reggie.controller;

import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.ShoppingCart;
import com.itheima.reggie.service.ShoppingCartService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/shoppingCart")
@Slf4j
public class ShoppingCartController {

    @Autowired
    private ShoppingCartService shoppingCartService;

    @PostMapping("/add")
    public R<ShoppingCart> addShoppingCart(@RequestBody ShoppingCart shoppingCart){
        log.info("shoppingcart = {}",shoppingCart);
        return shoppingCartService.addShoppingCart(shoppingCart);
    }


    @GetMapping("/list")
    public R<List<ShoppingCart>> getShoppingCartList(){
        log.info("进入购物车列表");
        return shoppingCartService.getShoppingCartList();
    }

    @DeleteMapping("/clean")
    public R<String> cleanShoppingCartList(){
        return shoppingCartService.cleanShoppingCartList();
    }
}
