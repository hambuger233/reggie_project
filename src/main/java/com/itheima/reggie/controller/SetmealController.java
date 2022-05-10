package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.dto.SetmealDto;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/setmeal")
@Slf4j
public class SetmealController {

    @Autowired
    private SetmealService setmealService;


    /**
     * 新增套餐
     * @param setmealDto
     * @return
     */
    @PostMapping
    public R<String>  saveSetMeal(@RequestBody SetmealDto setmealDto){
        log.info("套餐信息：{}",setmealDto);
        return setmealService.saveSetMeal(setmealDto);
    }


    /**
     * 套餐分页查询
     */
    @GetMapping("/page")
    public R<Page> setMealPageList(Integer page,Integer pageSize,String name){
        return setmealService.setMealPageList(page,pageSize,name);

    }

    /**
     * 删除套餐代码
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteSetMeal(@RequestParam List<Long> ids){
        log.info("参数: {}",ids);
        return setmealService.deleteSetMeal(ids);
    }


    @PostMapping("/status/{status}")
    public R<String> setStatus(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        log.info("status=={}",status);
        log.info("ids=={}",ids);
        return setmealService.setStatus(status,ids);
    }


    /**
     * 根据条件查询套餐数据
     * @param setmeal
     * @return
     */
    @GetMapping("/list")
    public R<List<Setmeal>> getSetMealList(Setmeal setmeal){
        return setmealService.getSetMealList(setmeal);
    }
}
