package com.itheima.reggie.service.impl;

import com.baomidou.mybatisplus.core.conditions.query.LambdaQueryWrapper;
import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.impl.ServiceImpl;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.entity.Dish;
import com.itheima.reggie.entity.Setmeal;
import com.itheima.reggie.exception.CustomException;
import com.itheima.reggie.mapper.CategoryMapper;
import com.itheima.reggie.service.CategoryService;
import com.itheima.reggie.service.DishService;
import com.itheima.reggie.service.SetmealService;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

/**
 * 菜品及套餐分类(Category)表服务实现类
 *
 * @author makejava
 * @since 2022-05-09 14:01:24
 */
@Service("categoryService")
@Slf4j
public class CategoryServiceImpl extends ServiceImpl<CategoryMapper, Category> implements CategoryService {

    @Autowired
    private DishService dishService;

    @Autowired
    private SetmealService setmealService;

    @Override
    public R<String> saveCategory(Category category) {
        log.info("category：{}",category);
        save(category);

        return R.success("新增分类成功");
    }

    @Override
    public R<Page> getCategoryList(Integer page, Integer pageSize) {
        //创建Page对象
        Page<Category> categoryPage = new Page<>(page,pageSize);
        //创建查询条件
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.orderByAsc(Category::getSort);
        //调用page方法
        page(categoryPage,queryWrapper);
        return R.success(categoryPage);
    }

    @Override
    public R<String> deleteCategory(Long id) {
        //需要先进行判断，该id下的分类是否关联了菜品或者菜单
        //查询当前分类是否关联了菜品，如果已经关联，则抛出业务异常
        LambdaQueryWrapper<Dish> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(Dish::getCategoryId,id);
        int count = dishService.count(queryWrapper);
        if (count > 0){
            //说明已经关联了菜品，抛出异常
            throw new CustomException("当前分类下关联了菜品，不能删除");
        }
        //查询当前分类是否关联了套餐，如果已经关联，则抛出业务异常
        LambdaQueryWrapper<Setmeal> queryWrapper1 = new LambdaQueryWrapper<>();
        queryWrapper1.eq(Setmeal::getCategoryId,id);
        int count1 = setmealService.count(queryWrapper1);
        if (count1 > 0){
            //说明已经关联了套餐，抛出异常
            throw new CustomException("当前分类下关联了套餐，不能删除");
        }
        //正常删除
        removeById(id);

        return R.success("分类信息删除成功");
    }

    @Override
    public R<String> updateCategory(Category category) {
        log.info("修改分类信息，{}",category);
        updateById(category);
        return R.success("修改分类信息成功");
    }

    @Override
    public R<List<Category>> listCategory(Category category) {
        //条件构造器
        LambdaQueryWrapper<Category> queryWrapper = new LambdaQueryWrapper<>();
        queryWrapper.eq(category.getType() != null,Category::getType,category.getType());
        //排序条件
        queryWrapper.orderByAsc(Category::getSort).orderByDesc(Category::getUpdateTime);
        List<Category> categoryList = list(queryWrapper);

        return R.success(categoryList);
    }
}
