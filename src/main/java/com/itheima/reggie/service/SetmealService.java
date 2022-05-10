package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.dto.SetmealDto;

import java.util.List;


/**
 * 套餐(Setmeal)表服务接口
 *
 * @author makejava
 * @since 2022-05-09 14:43:00
 */
public interface SetmealService extends IService<Setmeal> {

    R<String> saveSetMeal(SetmealDto setmealDto);

    R<Page> setMealPageList(Integer page, Integer pageSize, String name);

    R<String> deleteSetMeal(List<Long> ids);

    R<String> setStatus(Integer status, List<Long> ids);

    R<List<Setmeal>> getSetMealList(Setmeal setmeal);

}
