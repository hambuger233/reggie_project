package com.itheima.reggie.controller;

import com.baomidou.mybatisplus.extension.plugins.pagination.Page;
import com.itheima.reggie.common.R;
import com.itheima.reggie.entity.Category;
import com.itheima.reggie.service.CategoryService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;


    /**
     * 新增分类
     * @param category
     * @return
     */
    @PostMapping
    public R<String> saveCategory(@RequestBody Category category){
       return categoryService.saveCategory(category);
    }


    @GetMapping("/page")
    public R<Page> getCategoryList(Integer page,Integer pageSize){
        return categoryService.getCategoryList(page,pageSize);

    }

    /**
     * 根据id删除分类，要先进行判断是否关联菜品
     * @param ids
     * @return
     */
    @DeleteMapping
    public R<String> deleteCategory(Long ids){
        return categoryService.deleteCategory(ids);
    }


    /**
     * 修改分类信息
     * @param category
     * @return
     */
    @PutMapping
    public R<String> updateCategory(@RequestBody Category category){

        return categoryService.updateCategory(category);
    }


    /**
     * 根据条件查询分类数据
     * @param category
     * @return
     */
    @GetMapping("/list")
    public R<List<Category>> listCategory(Category category){
        return categoryService.listCategory(category);
    }
}
