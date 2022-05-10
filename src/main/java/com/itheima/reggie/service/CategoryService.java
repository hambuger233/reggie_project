package com.itheima.reggie.service;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.baomidou.mybatisplus.extension.service.IService;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;

import java.util.List;


/**
 * 菜品及套餐分类(Category)表服务接口
 *
 * @author makejava
 * @since 2022-05-09 14:01:23
 */
public interface CategoryService extends IService<Category> {

    R<String> saveCategory(Category category);

    R<Page> getCategoryList(Integer page, Integer pageSize);

    R<String> deleteCategory(Long id);

    R<String> updateCategory(Category category);

    R<List<Category>> listCategory(Category category);
}
