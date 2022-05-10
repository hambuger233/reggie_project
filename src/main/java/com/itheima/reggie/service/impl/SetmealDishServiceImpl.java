package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.mapper.SetmealDishMapper;
import com.itheima.reggie.service.SetmealDishService;
import org.springframework.stereotype.Service;

/**
 * 套餐菜品关系(SetmealDish)表服务实现类
 *
 * @author makejava
 * @since 2022-05-09 18:20:07
 */
@Service("setmealDishService")
public class SetmealDishServiceImpl extends ServiceImpl<SetmealDishMapper, SetmealDish> implements SetmealDishService {

}
