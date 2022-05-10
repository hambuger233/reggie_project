package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.DishFlavor;
import com.itheima.reggie.entity.dto.DishDto;
import com.itheima.reggie.exception.CustomException;
import com.itheima.reggie.mapper.DishMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishFlavorService;
import com.itheima.reggie.service.DishService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 菜品管理(Dish)表服务实现类
 *
 * @author makejava
 * @since 2022-05-09 14:43:00
 */
@Service("dishService")
@Transactional
@Slf4j
public class DishServiceImpl extends ServiceImpl<DishMapper, Dish> implements DishService {

    @Autowired
    private DishFlavorService dishFlavorService;

    @Autowired
    private CategoryService categoryService;
    /**
     * 新增菜品，同时保存对应的口味
     * @param dishDto
     */
    @Override
    public void saveWithFlavor(DishDto dishDto) {
        //保存菜品的基本信息到菜品表
        save(dishDto);
        Long dishId = dishDto.getId();
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream()
                .map(item -> {
                    item.setDishId(dishId);
                    return item;
                })
                .collect(Collectors.toList());
        //保存菜品口味数据到菜品口味表dish_flavor
        dishFlavorService.saveBatch(flavors);
    }

    @Override
    public R<Page> getDishList(Integer page, Integer pageSize, String name) {
        //分页构造器对象
        Page<Dish> pageInfo = new Page<>(page,pageSize);
        Page<DishDto> dishDtoPage = new Page<>();
        //条件构造器
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.like(name != null,Dish::getName,name);
        queryWrapper.orderByDesc(Dish::getUpdateTime);
        //执行分页查询
        pageInfo = page(pageInfo,queryWrapper);

        //对象copy
        BeanUtils.copyProperties(pageInfo,dishDtoPage,"records");
        List<Dish> records = pageInfo.getRecords();
        List<DishDto> collect = records.stream()
                .map(item -> {
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(item, dishDto);
                    Long id = item.getCategoryId();//分类id
                    //根据分类id查询分类
                    Category category = categoryService.getById(id);
                    if (Objects.nonNull(category)){
                        dishDto.setCategoryName(category.getName());
                    }
                    return dishDto;
                })
                .collect(Collectors.toList());
        dishDtoPage.setRecords(collect);


        return R.success(dishDtoPage);
    }

    @Override
    public R<DishDto> getDishDetail(Long id) {
        //通过菜品id查询菜品信息
        Dish dish = getById(id);

        DishDto dishDto = new DishDto();
        BeanUtils.copyProperties(dish,dishDto);

        //通过菜品查询口味信息
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,id);
        List<DishFlavor> list = dishFlavorService.list(queryWrapper);
        dishDto.setFlavors(list);

        return R.success(dishDto);
    }

    @Override
    public void updateWithFlavor(DishDto dishDto) {
        //修改菜品表内的数据
        updateById(dishDto);
        //更新口味表中的数据,先清理口味数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(DishFlavor::getDishId,dishDto.getId());
        dishFlavorService.remove(queryWrapper);
        //添加口味数据
        List<DishFlavor> flavors = dishDto.getFlavors();
        flavors = flavors.stream()
                .map(item -> {
                    item.setDishId(dishDto.getId());
                    return item;
                })
                .collect(Collectors.toList());
        dishFlavorService.saveBatch(flavors);

    }

    @Override
    public R<String> deleteDishList(List<Long> ids) {
        //根据表中的数据进行修改
        //根据id,修改菜品表中isDeleted字段


        //删除口味表中的数据
        LambdaQueryWrapper<DishFlavor> queryWrapper = new LambdaQueryWrapper<>();



        return null;
    }

    @Override
    public R<List<DishDto>> queryDishList(Dish dish) {

        //构造查询条件
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(dish.getCategoryId() != null,Dish::getCategoryId,dish.getCategoryId());
        //查询状态为1(起售状态)
        queryWrapper.eq(Dish::getStatus,1);
        //添加排序条件
        queryWrapper.orderByAsc(Dish::getSort);
        queryWrapper.orderByAsc(Dish::getUpdateTime);

        List<Dish> list = list(queryWrapper);

        List<DishDto> dishDtos = list.stream()
                .map(item -> {
                    DishDto dishDto = new DishDto();
                    BeanUtils.copyProperties(item, dishDto);
                    Long id = item.getCategoryId();//分类id
                    //根据分类id查询分类
                    Category category = categoryService.getById(id);
                    if (Objects.nonNull(category)) {
                        dishDto.setCategoryName(category.getName());
                    }
                    //查询口味信息
                    Long dishId = item.getId();
                    LambdaQueryWrapper<DishFlavor> queryWrapper1 = new LambdaQueryWrapper<>();
                    queryWrapper1.eq(DishFlavor::getDishId,dishId);
                    List<DishFlavor> dishFlavorList = dishFlavorService.list(queryWrapper1);
                    dishDto.setFlavors(dishFlavorList);
                    return dishDto;
                })
                .collect(Collectors.toList());

        return R.success(dishDtos);
    }

    @Override
    public R<String> setStatus(Integer status, List<Long> ids) {
        //根据ids查询菜品 select * from dish where id in (1,2,3)  update dish set xxxx status = ? where id in (1,2,3)
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Dish::getId,ids);
        //将查询出来的菜品状态设置为对应的状态
        List<Dish> dishList = list(queryWrapper);
        dishList = dishList.stream()
                .map(item -> {
                    item.setStatus(status);
                    //进行保存操作，将菜品保存
                    updateById(item);
                    return item;
                })
                .collect(Collectors.toList());

        return R.success("状态修改成功");
    }

    @Override
    public R<String> deleteDish(List<Long> ids) {
        //判断是否是停售的菜品，停售的菜品才需要删除
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getStatus,1);
        queryWrapper.in(Dish::getId,ids);
        int count = count(queryWrapper);
        if (count > 0){
            //说明能够根据id查询到有在售菜品,抛出异常
            throw  new CustomException("所选菜品中有正在起售的，无法删除");
        }
        //如果可用删除，则删除菜品表中的数据 --Dish
        removeByIds(ids);
        //如果可以删除，删除口味表中的数据 --Dish_flavor delete from dish_flavor where dish_id in (?,?,?)
        LambdaQueryWrapper<DishFlavor> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(DishFlavor::getDishId,ids);
        dishFlavorService.remove(lambdaQueryWrapper);
        return R.success("菜品数据删除成功");
    }
}
