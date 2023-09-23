package com.sky.controller.user;


import com.sky.entity.Category;
import com.sky.mapper.CategoryMapper;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.Api;
import io.swagger.annotations.ApiOperation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController("userCategoryController")
@RequestMapping("/user/category")
@Api(tags = "C端分类相关接口")
public class CategoryController {


    @Autowired
    private CategoryService categoryService;

    @GetMapping("/list")
    @ApiOperation("查询所有分类")
    public Result getCategoryList() {
        List<Category> list = categoryService.getListByType(null);
        return Result.success(list);
    }

}
