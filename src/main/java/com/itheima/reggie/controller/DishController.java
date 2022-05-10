package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/dish")
@Slf4j
public class DishController {

    @Autowired
    private DishService dishService;

    @Autowired
    private DishFlavorService dishFlavorService;


    @PostMapping
    public R<String> saveWithFlavor(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        //添加菜品，同时插入口味数据
        dishService.saveWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }


    /**
     * 菜品信息分页
     * @param page
     * @param pageSize
     * @param name
     * @return
     */
    @GetMapping("/page")
    public R<Page> getDishList(Integer page, Integer pageSize, String name){
        return dishService.getDishList(page,pageSize,name);
    }


    /**
     * 修改菜品中数据回显
     * @param id
     * @return
     */
    @GetMapping("/{id}")
    public R<DishDto> getDishDetail(@PathVariable("id") Long id){
        return dishService.getDishDetail(id);
    }

    @PutMapping
    public R<String> updateWithFlavor(@RequestBody DishDto dishDto){
        log.info(dishDto.toString());
        //添加菜品，同时插入口味数据
        dishService.updateWithFlavor(dishDto);

        return R.success("新增菜品成功");
    }

    @DeleteMapping("/{ids}")
    public R<String> deleteDishList(@PathVariable("ids") List<Long> ids) {
        //根据选中id删除数据
        return dishService.deleteDishList(ids);

    }


    /**
     * 根据条件查询对应的菜品数据
     * @param dish
     * @return
     */
//    @GetMapping("/list")
//    public R<List<Dish>> queryDishList(Dish dish){
//        return dishService.queryDishList(dish);
//    }
    //修改后
    @GetMapping("/list")
    public R<List<DishDto>> queryDishList(Dish dish){
        return dishService.queryDishList(dish);
    }

    /**
     * 批量修改菜品的出售状态
     * @param status
     * @param ids
     * @return
     */
    @PostMapping("/status/{status}")
    public R<String> setStatus(@PathVariable("status") Integer status,@RequestParam List<Long> ids){
        log.info("状态：{}",status);
        log.info("ids是：{}",ids);
        return dishService.setStatus(status,ids);
    }


    @DeleteMapping
    public R<String> deleteDish(@RequestParam List<Long> ids){
        log.info("删除的ids:{}",ids);
        return dishService.deleteDish(ids);
    }

}
