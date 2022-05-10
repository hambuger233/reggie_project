package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.dto.DishDto;

import java.util.List;


/**
 * 菜品管理(Dish)表服务接口
 *
 * @author makejava
 * @since 2022-05-09 14:43:00
 */
public interface DishService extends IService<Dish> {

    void saveWithFlavor(DishDto dishDto);

    R<Page> getDishList(Integer page, Integer pageSize, String name);

    R<DishDto> getDishDetail(Long id);

    void updateWithFlavor(DishDto dishDto);

    R<String> deleteDishList(List<Long> ids);

    R<List<DishDto>> queryDishList(Dish dish);

    R<String> setStatus(Integer status, List<Long> ids);

    R<String> deleteDish(List<Long> ids);

}
