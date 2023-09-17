package com.sky.controller.admin;


import com.github.pagehelper.Page;
import com.github.pagehelper.PageHelper;
import com.sky.dto.CategoryDTO;
import com.sky.dto.CategoryPageQueryDTO;
import com.sky.entity.Category;
import com.sky.result.PageResult;
import com.sky.result.Result;
import com.sky.service.CategoryService;
import io.swagger.annotations.ApiOperation;
import org.apache.ibatis.annotations.Delete;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import static com.sky.result.Result.success;

@RestController
@RequestMapping("admin/category")
public class CategoryController {

    @Autowired
    private CategoryService categoryService;

    @GetMapping("/page")
    @ApiOperation("分页查询分类")
    public Result pageQuery(CategoryPageQueryDTO categoryPageQueryDTO) {
        System.out.println("categoryPageQueryDTO " + categoryPageQueryDTO);
        PageResult pageResult = categoryService.pageQuery(categoryPageQueryDTO);
        return success(pageResult);
    }

    @PostMapping()
    @ApiOperation("新增分类")
    public Result save(@RequestBody CategoryDTO categoryDTO) {
        categoryService.save(categoryDTO);
        return success();
    }

    @DeleteMapping()
    @ApiOperation("根据id删除分类")
    public Result removeCategory(@RequestParam("id") Long id) {
        System.out.println("删除的分类id：" + id);
        categoryService.remove(id);
        return success();
    }

    @PutMapping()
    @ApiOperation("修改分类")
    public Result update(@RequestBody CategoryDTO categoryDTO) {
        categoryService.update(categoryDTO);
        return success();
    }

    @PostMapping("/status/{status}")
    @ApiOperation("启动、禁用状态")
    public Result changeStatus(@PathVariable("status") Integer stats,@RequestParam("id") Long id) {

        categoryService.changeStatus(stats,id);
        return success();
    }



}
