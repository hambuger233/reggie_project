package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.entity.SetmealDish;
import com.itheima.reggie.entity.dto.SetmealDto;
import com.itheima.reggie.exception.CustomException;
import com.itheima.reggie.mapper.SetmealMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.SetmealDishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.BeanUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.RequestParam;

import java.util.List;
import java.util.Objects;
import java.util.stream.Collectors;

/**
 * 套餐(Setmeal)表服务实现类
 *
 * @author makejava
 * @since 2022-05-09 14:43:01
 */
@Service("setmealService")
@Transactional
@Slf4j
public class SetmealServiceImpl extends ServiceImpl<SetmealMapper, Setmeal> implements SetmealService {

    @Autowired
    private SetmealDishService setmealDishService;

    @Autowired
    private CategoryService categoryService;

    /**
     * 新增套餐，同时需要保存套餐和菜品关联关系
     * @param setmealDto
     * @return
     */
    @Override
    public R<String> saveSetMeal(SetmealDto setmealDto) {
        //保存套餐的基本信息 insert操作
        save(setmealDto);
        List<SetmealDish> setmealDishes = setmealDto.getSetmealDishes();
        setmealDishes = setmealDishes.stream()
                .map(item -> {
                    item.setSetmealId(String.valueOf(setmealDto.getId()));
                    return item;
                })
                .collect(Collectors.toList());
        //保存套餐和菜品的关联信息 insert
        setmealDishService.saveBatch(setmealDishes);

        return R.success("新增套餐成功");
    }

    @Override
    public R<Page> setMealPageList(Integer page, Integer pageSize, String name) {
        //分页构造器
        Page<Setmeal> pageInfo = new Page<>(page,pageSize);
        Page<SetmealDto> dtoPage = new Page<>();
        //条件查询
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        //添加查询条件
        queryWrapper.like(StringUtils.hasText(name),Setmeal::getName,name);
        //排序条件
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);

        page(pageInfo,queryWrapper);

        //对象的拷贝
        BeanUtils.copyProperties(pageInfo,dtoPage,"records");
        List<Setmeal> records = pageInfo.getRecords();

        List<SetmealDto> setmealDtoList = records.stream()
                .map(item -> {
                    SetmealDto setmealDto = new SetmealDto();
                    BeanUtils.copyProperties(item, setmealDto);
                    Long categoryId = item.getCategoryId();
                    Category category = categoryService.getById(categoryId);
                    if (Objects.nonNull(category)) {
                        //分类名称
                        setmealDto.setCategoryName(category.getName());
                    }
                    return setmealDto;
                })
                .collect(Collectors.toList());

        dtoPage.setRecords(setmealDtoList);

        return R.success(dtoPage);
    }

    /**
     * 删除套餐
     * @param ids
     * @return
     */
    @Override
    public R<String> deleteSetMeal(List<Long> ids) {
        //只有停售的套餐才需要删除
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        queryWrapper.eq(Setmeal::getStatus,1);
        //如果不能删除，抛出异常
        int count = count(queryWrapper);
        if (count > 0){
            //说明套餐不能删除
            throw new CustomException("有套餐正在售卖中，不能删除");
        }
        //如果可以删除，先删除套餐表中的数据 --setmeal
        removeByIds(ids);
        // 删除关系表中的数据 --setmeal_dish
        LambdaQueryWrapper<SetmealDish> lambdaQueryWrapper = new LambdaQueryWrapper<>();
        lambdaQueryWrapper.in(SetmealDish::getSetmealId,ids);
        setmealDishService.remove(lambdaQueryWrapper);

        return R.success("套餐数据删除成功");
    }

    @Override
    public R<String> setStatus(Integer status, List<Long> ids) {
        //修改套餐状态
        //根据id查询对应的套餐
        LambdaQueryWrapper<Setmeal> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.in(Setmeal::getId,ids);
        List<Setmeal> setmealList = list(queryWrapper);
        setmealList = setmealList.stream()
                .map(item -> {
                    item.setStatus(status);
                    updateById(item);
                    return item;
                })
                .collect(Collectors.toList());

        //修改状态
        return R.success("状态修改成功");
    }

    @Override
    public R<List<Setmeal>> getSetMealList(Setmeal setmeal) {
        //查询条件
        LambdaQueryWrapper<Setmeal>  queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(setmeal.getCategoryId() != null,Setmeal::getCategoryId,setmeal.getCategoryId());
        queryWrapper.eq(setmeal.getStatus() != null,Setmeal::getStatus,setmeal.getStatus());
        queryWrapper.orderByDesc(Setmeal::getUpdateTime);
        List<Setmeal> setmealList = list(queryWrapper);
        return R.success(setmealList);
    }


}
